(ns my-line-bot-app-clojure.core
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clojure.string :as string]
            [my-line-bot-app-clojure.message :as message]))                                        

(def config (load-file "config.clj"))

(def headers {"Content-Type" "application/json",
              "X-Line-ChannelID" (config :channel-id),
              "X-Line-ChannelSecret" (config :channel-secret),
              "X-Line-Trusted-User-With-ACL" (config :channel-mid)})

(def options {:timeout 200             ; ms
              :user-agent "User-Agent-string"
              :headers headers})
  
(defn callback-handler [req]
  (let [res (json/read-str (slurp(req :body)) :key-fn keyword)] 
    (let [content (-> res
                     :result
                     first
                     :content)
          from (content :from)
          send-text (message/send-text :default)          
          to-text (content :text);(string/join "\n" send-text)
          send-body {:to (list from)
                     :toChannel (config :event-to-channel-id)
                     :eventType (config :event-type)
                     :content {:toType 1
                               :contentType (message/content-type :text)
                               :text to-text}}]

      (let [resp1 (http/post (str (config :channel-url) (config :event-path)) (assoc options :body (json/write-str send-body)))]        
        (println "Response 1's status: " (:status @resp1))))))

(defroutes main-routes
  (POST "/callback" req (callback-handler req)))

(defn -main []
  (jetty/run-jetty main-routes {:port 3000}))
