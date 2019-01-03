(ns forgotten.handler.fare_master
  (:require [ring.util.response :as res]
            [ring.middleware.session :as session]
            [slingshot.slingshot :refer [try+]]
            [forgotten.config.environment :refer [ROOTDIR]]
            [forgotten.handler.login :refer [login-check]]
            [forgotten.validation.fare_master :refer [validate]]
            [forgotten.service.fare_master :as service]
            [forgotten.views.fare_master :as view]
            [forgotten.util.common-message :refer [FATAL-ERROR SAVE-SUCCESS DELETE-SUCCESS]]
            [forgotten.util.log :refer [write-log]]
            [forgotten.util.response :refer [html]]))


;;;; const

;; mapを作るキーになるもの
(def ^:const fare-master-keys [:fare_master_id :user_id :purpose :transportation :departure :arrival :round_trip :fare])


;;;; suport

(defn- ->map
  "リクエストボディからmapを作る"
  [user_id params]
  (let [fare_master_ids  (params "fare-master[][fare_master_id]")
        purposes         (params "fare-master[][purpose]")
        transportations  (params "fare-master[][transportation]")
        departures       (params "fare-master[][departure]")
        arrivals         (params "fare-master[][arrival]")
        round_trip_nums  (params "fare-master[][round_trip]")
        fares            (params "fare-master[][fare]")]
    
    (if (vector? purposes)
      
      (do
        ;; 複数行ある場合
        (map (fn [fare_master_id purpose transportation departure arrival round_trip_num fare_str]
               (let [round_trip (condp = round_trip_num
                                  "1" true
                                  "0" false)
                     fare       fare_str
                     values [fare_master_id user_id purpose transportation departure arrival round_trip fare]]
                 ;; mapへ変換
                 (zipmap fare-master-keys values)))
             
             fare_master_ids purposes transportations departures arrivals round_trip_nums fares))

      (do
        ;; 1行しかない場合
        (let [fare_master_id fare_master_ids
              purpose        purposes
              transportation transportations
              departure      departures
              arrival        arrivals
              round_trip     (condp = round_trip_nums
                               "1" true
                               "0" false)
              fare           fares
              values [fare_master_id user_id purpose transportation departure arrival round_trip fare]]
          ;; mapへ変換
          (list (zipmap fare-master-keys values)))))))


;;;; handler


(defn get-fare-master
  "交通費マスタ表示"
  [{:as req :keys [session]}]
  
  (try+
   
   ;; ログインチェック
   (login-check req)
   
   (let [user_id (:id (:user session))
         fare-masters (service/get-fare-master user_id)]
     
     ;; 画面表示
     (-> (view/fare-master-page req fare-masters)
         res/response
         (html)))

   ;; exception
   (catch [:type :forgotten.handler.login/authentication-error] {:keys [error]}
     ;; ログインエラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error error})))
   
   (catch Exception error
     ;; その他の予期せぬエラー
     (write-log error)
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error FATAL-ERROR})))))


(defn post-fare-master
  "交通費マスタ保存"
  [{:as req :keys [session params]}]
  
  (let [user_id (:id (:user session))
        fare-master-maps (->map user_id params)]

    (try+
     
     ;; ログインチェック
     (login-check req)
     
     (validate fare-master-maps)
     
     ;; DB保存。成功なら入力画面へ
     (when (count (service/save-fare-master fare-master-maps))
       (-> (res/redirect (str ROOTDIR "/fare/master"))
           (assoc :flash {:success SAVE-SUCCESS})))
     
     ;; exception
     (catch [:type :forgotten.handler.login/authentication-error] {:keys [error]}
       ;; ログインエラー
       (-> (res/redirect (str ROOTDIR "/login"))
          (assoc :flash {:error error})))
     
     (catch [:type :forgotten.validation.fare_master/validation-error] {:keys [error]}
       ;; バリデーションエラー
       (-> (view/fare-master-page (assoc req :flash {:error error}) (->map user_id params))
           res/response
           (html)))

     (catch [:type :forgotten.service.fare_master/fatal-sql-error] {:keys [error]}
     ;; SQL実行エラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))
     
     (catch Exception error
       ;; その他の予期せぬエラー
      (write-log error)
      (-> (res/redirect (str ROOTDIR "/login"))
          (assoc :flash {:error FATAL-ERROR}))))))




(defn delete-fare-master
  "交通費マスタ削除"
  [{:as req :keys [session params]}]
  
  (try+
   
   ;; ログインチェック
   (login-check req)

   (let [user_id (:id (:user session))
         fare_master_id (:fare_master_id params)]
     
     ;; 交通費マスタ削除
     (service/delete-fare-master fare_master_id)

     (-> (res/redirect (str ROOTDIR "/fare/master"))
         (assoc :flash {:success DELETE-SUCCESS})))
   
   ;; exception
   (catch [:type :forgotten.handler.login/authentication-error] {:keys [error]}
     ;; ログインエラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error error})))
   
   (catch [:type :forgotten.service.fare_master/fatal-sql-error] {:keys [error]}
     ;; SQL実行エラー
     (-> (res/redirect (str ROOTDIR "/login"))
         (assoc :flash {:error {:fatal FATAL-ERROR}})))
   
   (catch Exception error
     ;; その他の予期せぬエラー
      (write-log error)
      (-> (res/redirect (str ROOTDIR "/login"))
          (assoc :flash {:error FATAL-ERROR})))))