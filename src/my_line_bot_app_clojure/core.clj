(ns my-line-bot-app-clojure.core
  (:require [clojure.data.json :as json]
            [clojure.string :as string]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [org.httpkit.client :as http]
            [my-line-bot-app-clojure.message :as message]))                                        

(def config (load-file "config.clj"))
(def headers {"Content-Type" "application/json",
              "X-Line-ChannelID" (config :channel-id),
              "X-Line-ChannelSecret" (config :channel-secret),
              "X-Line-Trusted-User-With-ACL" (config :channel-mid)})
(def options {:timeout 200 :headers headers})

(defn callback-handler [req]
  (let [res (json/read-str (slurp(req :body)) :key-fn keyword)] 
    (let [content (-> res
                     :result
                     first
                     :content)
          send-body (message/create-text-message (content :from) (content :text))]
      (let [resp (http/post (str (config :channel-url) (config :event-path))
                            (assoc options :body (json/write-str send-body)))]
        (println "Response 's status: " (:status @resp))))))

(defroutes main-routes
  (POST "/callback" req (callback-handler req)))

(defn -main []
  (jetty/run-jetty main-routes {:port 3000}))
