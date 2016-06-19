(ns my-line-bot-app-clojure.message
  (:require [clojure.string :as string]))

(def config (load-file "config.clj"))
(def content-type {:text 1
                   :image 2
                   :video 3
                   :audio 4
                   :location 7
                   :sticker 8
                   :contact 10
                   :rich 12})
(def image-link {:h4 "https://s3-ap-northeast-1.amazonaws.com/walter-s3/line-bot/image/h4-logo"
                 :emacs "https://s3-ap-northeast-1.amazonaws.com/walter-s3/line-bot/image/emacs-logo"})
(def send-text {:default "Welcome to h4!"
                :h4 "Welcome to h4!"
                :emacs "Welcome to Emacs Taiwan!"
                :how-are-you-today "How are you today?"
                :i-am-fine "Fine, how do you do."
                :h4-place '["餐廳：田中園光華店"
                            "地址：台北市中正區臨沂街 1 號"
                            "(捷運忠孝新生站一號出口直走第一個路口右轉)"
                            "時間：7:30pm ~ 10:00pm"
                            "Restaurant : 田中園 (Tian Jung Yuan)"
                            ""
                            "Venue : No. 1, Linyi St, Zhongzheng District, Taipei City"
                            "(MRT JungXiao Xingshen Station Exit 1)"
                            "Time : 7:30pm ~ 10:00pm"]
                :h4-people-do '["1. 討論 web, network, programming, system, blablah…."
                                "2. 交流系統工具 & 使用技巧"
                                "3. 八卦"]
                :h4-beginning '["Hacking Thursday 是由幾位居住於台北地區的自由軟體/開放原碼開發者所發起，"
                                "每週四晚上會於特定咖啡店聚會。以非會議形式、交換並實做各自提出的想法，"
                                "輕鬆的會議過程以禮貌、謙遜與尊重的互信態度接納並鼓勵概念發想、發起新計畫、"
                                "並從開發者的協同開發與經驗分享中互相學習。"]
                :contact-us '["除了實體聚會外，我們使用 Google group / Facebook group 做為大家的溝通聯絡管道。"
                              "聊天，討論，及聚會通告都會在這裡發佈。如果您對我們的聚會有興趣，隨時都歡迎您加入/訂閱我們的討論區，和我們交流！！"
                              ""
                              "http://groups.google.com/group/hackingthursday ( Google group )"
                              "http://www.facebook.com/groups/hackingday/ ( Facebook group )"
                              "https://www.meetup.com/hackingthursday/ ( Meetup )"]})

(defn get-suggest-message [text]
  (case text
    "how to get to h4" (string/join "\n" (send-text :h4-place))
    "what h4 people do" (string/join "\n" (send-text :h4-people-do))
    "h4 beginning" (string/join "\n" (send-text :h4-beginning))
    "contact us" (string/join "\n" (send-text :contact-us))
    "emacs" (send-text :emacs)
    "how are you" (send-text :i-am-fine)
    (send-text :default)))

(defn create-text-message [from text]
  (let [send-text (get-suggest-message (string/lower-case text))]
    {:to (list from)
     :toChannel (config :event-to-channel-id)
     :eventType (config :event-type)
     :content {:toType 1
               :contentType (content-type :text)
               :text send-text}}))
