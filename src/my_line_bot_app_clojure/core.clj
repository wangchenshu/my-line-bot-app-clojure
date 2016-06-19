(ns my-line-bot-app-clojure.core
  (:require [clojure.data.json :as json]
            [clojure.string :as string]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [org.httpkit.client :as http]
            [my-line-bot-app-clojure.message :as message]))                                        

(def config (load-file "config.clj"))
(def port (config :port))
(def channel-id (config :channel-id))
(def channel-secret (config :channel-secret))
(def channel-mid (config :channel-mid))
(def channel-url (config :channel-url))
(def event-path (config :event-path))
(def secret-key (config :secret-key))
(def username (config :username))
(def team-id (config :team-id))
(def db-name (config :db-name))
(def headers {"Content-Type" "application/json",
              "X-Line-ChannelID" channel-id,
              "X-Line-ChannelSecret" channel-secret,
              "X-Line-Trusted-User-With-ACL" channel-mid})
(def options {:timeout 500 :headers headers})

(defn handle-resp [resp]
  (-> @resp :status println))

(defn callback-handler [req]
  (let [res (json/read-str (slurp(req :body)) :key-fn keyword)] 
    (let [content (-> res :result first :content)
          send-body (message/create-text-message (content :from) (content :text))]
      (let [resp (http/post (str channel-url event-path)
                            (assoc options :body (json/write-str send-body)))]
        (handle-resp resp)))))

(defroutes main-routes
  (POST "/callback" req (callback-handler req)))

(defn -main [& args]
  (jetty/run-jetty main-routes {:port port}))
  ;(run-server main-routes {:port listening-port}))
