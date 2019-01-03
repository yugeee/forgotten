(ns forgotten.handler.fare
  (:require [ring.util.response :as res]
            [slingshot.slingshot :refer [try+]]
            [forgotten.config.environment :refer [ROOTDIR]]
            [forgotten.handler.login :refer [login-check]]
            [forgotten.service.fare :as fare-service]
            [forgotten.service.fare_master :as fare-master-service]
            [forgotten.validation.fare :refer [validate]]
            [forgotten.util.validate :refer [violate-format]]
            [forgotten.views.fare :as views]
            [forgotten.views.fare_list :refer [fare-select-page]]
            [forgotten.util.common-message :refer [FATAL-ERROR SAVE-SUCCESS DELETE-SUCCESS]]
            [forgotten.util.time :refer [current-period weekday-reversal-kv]]
            [forgotten.util.log :refer [write-log]]
            [forgotten.util.response :refer [html]]))


;;;; const

;; mapを作るキーになるもの
(def ^:const fare-keys [:fare_id :user_id :date :purpose :transportation :departure :arrival :round_trip :fare])


;;;; suport

(defn- ->map
  "リクエストボディからmapを作る"
  [user_id params]

  (let [fare_ids        (params "fare[][fare_id]")
        dates           (params "fare[][date]")
        purposes        (params "fare[][purpose]")
        transportations (params "fare[][transportation]")
        departures      (params "fare[][departure]")
        arrivals        (params "fare[][arrival]")
        round_trip_nums (params "fare[][round_trip]")
        fares           (params "fare[][fare]")]
    
    (if (vector? fare_ids)
      
      (do
        ;; 複数行ある場合
        (map (fn [fare_id date purpose transportation departure arrival round_trip_num fare_str]
               (let [round_trip (condp = round_trip_num
                                  "1" true
                                  "0" false)
                      fare       fare_str
                     values [fare_id user_id date purpose transportation departure arrival round_trip fare]]
                 ;; mapへ変換
                 (zipmap fare-keys values)))
             
             fare_ids dates purposes transportations departures arrivals round_trip_nums fares))
      
      (do
         ;; 1行しかない場合
        (let [fare_id        fare_ids
              date           dates
              purpose        purposes
              transportation transportations
              departure      departures
              arrival        arrivals
              round_trip     (condp = round_trip_nums
                                "1" true
                                "0" false)
              fare           fares
              values [fare_id user_id date purpose transportation departure arrival round_trip fare]]
          ;; mapへ変換
          (list (zipmap fare-keys values)))))))
  


;;;; handler
(defn get-fare-list
  "勤怠選択画面表示"
  [{:as req :keys [session]}]

  (try+

   ;; ログインチェック
   (login-check req)
   
   (let [user_id (:id (:user session))
         periods (fare-service/get-fare-list user_id)]
     
     ;; 画面表示
     (-> (fare-select-page req periods)
         res/response
         (html)))
   
   ;; exception
   (catch [:type :forgotten.handler.login/authentication-error] {:keys [error]}
     ;; ログインエラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error error})))
   
   (catch [:type :forgotten.service.fare/fatal-sql-error] {:keys [error]}
     ;; SQL実行エラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))

   (catch Exception error
     ;; その他の予期せぬエラー
     (write-log error)
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))))


(defn get-fare
  "交通費情報取得"
  [{:as req :keys [session params]}]
  
  (try+

   ;; ログインチェック
   (login-check req)

   (let [user_id (:id (:user session))
         period   (if (empty? (:period params)) (current-period) (:period params))
         fares (fare-service/get-fare user_id period)
         fare-masters (fare-master-service/get-fare-master user_id)]

     ;; 画面表示
     (-> (views/fare-page req fares fare-masters period)
         res/response
         (html)))
   
   ;; exception
   (catch [:type :forgotten.handler.login/authentication-error] {:keys [error]}
     ;; ログインエラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error error})))
   
   (catch [:type :forgotten.service.fare/fatal-sql-error] {:keys [error]}
     ;; SQL実行エラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))
   
   (catch Exception error
     ;; その他の予期せぬエラー
     (write-log error)
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))))



(defn post-fare
  "交通費保存・保存後は入力画面へ"
  [{:as req :keys [session params]}]
  
  (let [user_id (:id (:user session))]
    
    (try+

     ;; ログインチェック
     (login-check req)

     ;; パラメータをmapに変換
     (let [fare-maps (->map user_id params)
           period    (:period  params)]

       ;; バリデーション
       (validate fare-maps)
       
       ;; DB保存。成功なら入力画面へ
       (when (count (fare-service/save-fare fare-maps))
         (-> (res/redirect (str ROOTDIR "/fare/period/" period))
             (assoc :flash {:success SAVE-SUCCESS}))))
       
     ;; exception
     (catch [:type :forgotten.handler.login/authentication-error] {:keys [error]}
       ;; ログインエラー
       (-> (res/redirect (str ROOTDIR "/login"))
           (assoc :flash {:error error})))
     
     (catch [:type :forgotten.validation.fare/validation-error] {:keys [error]}
       ;; バリデーションエラー
       (let [fare-maps (->map user_id params)
             fare-masters (fare-master-service/get-fare-master user_id)
             period   (-> (first fare-maps)
                        (:date)
                        (subs 0 7))]
         (-> (views/fare-page (assoc req :flash {:error error}) fare-maps fare-masters period)
             res/response
             (html))))
     
     (catch [:type :forgotten.service.fare/fatal-sql-error] {:keys [error]}
       ;; SQL実行エラー
       (-> (res/redirect (str ROOTDIR "/login"))
             (assoc :flash {:error {:fatal FATAL-ERROR}})))
     
     (catch Exception error
       ;; その他の予期せぬエラー
       (write-log error)
       (-> (res/redirect (str ROOTDIR "/login"))
           (assoc :flash {:error {:fatal FATAL-ERROR}}))))))


(defn post-delete-fare
  "交通費情報削除"
  [{:as req :keys [session params]}]
  
  (try+

   ;; ログインチェック
   (login-check req)

   (let [user_id (:id (:user session))
         fare_id (:fare_id params)
         period  (:period  params)]

     ;; 交通費削除
     (fare-service/delete-fare fare_id)

     (-> (res/redirect (str ROOTDIR "/fare/period/" period))
         (assoc :flash {:success DELETE-SUCCESS})))

   (catch [:type :forgotten.handler.login/authentication-error] {:keys [error]}
     ;; ログインエラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error error})))

   (catch [:type :forgotten.service.fare/fatal-sql-error] {:keys [error]}
     ;; SQL実行エラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))

   (catch Exception error
     ;; その他の予期せぬエラー
     (write-log error)
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))))