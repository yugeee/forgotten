# これなに

Clojure で作った勤怠表・交通費精算のWebアプリ

## 機能

- ログイン

ユーザ登録で登録が住んでるユーザが使えます。  

- 勤怠管理

その月の勤怠を保存できます。  
保存されたいないものは自動でカレンダー作って表示するので、入力して保存できます。

- 勤怠マスタ

勤怠保存で使う値を設定できます。  
設定した値は勤怠を新規登録する際に平日に反映されます。

- 交通費精算

交通費精算の入力が行えます。

- 交通費マスタ

よく使うパターン（帰社とか）を設定できます。  
交通費精算の時にプルダウンから選択すれば勝手に反映されます。  
日付は自動で入らないので手動で入力してください。

- 提出

勤怠・交通費のPDFを作成して提出します。ゆげのgmailにきます。  
今は両方揃ってないと送れない設定になっています。

## 自分とこでうごかす

適当なので動くか不明

1. [leiningen](https://leiningen.org/) をインストール
2. git@github.com:yugeee/forgotten.git
3. cd 開発ディレクトリ
4. lein deps
5. DBとかメールの設定をいじる
    - src/forgotten/config
6. lein ring server

[emacsのプラグインにすごいのある](https://qiita.com/ayato_p/items/10f61995cdc21c2d1927)

## 所感

Clojure

シンプル。  
(-> の関数適用とか新しいのもJava使って古いやり方もできてうまい。  
マクロ・関数合成・transducerとかやってないけど便利な機能を使ってないのが、ちょっと心残り。  
うまい人はもっとうまく書ける感じがする。うまく使うには修行が入りそうだった。  
また帰ってこようと思う。  
やっててよかったClojure
