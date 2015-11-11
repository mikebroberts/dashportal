(ns dashportal.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as response]
            [environ.core :as env]
            [hiccup.core :refer :all]
            [ring.adapter.jetty :refer :all]))

(defn generate-page [url refresh-seconds]
  (str "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
       (html
         [:html
          [:head
           [:meta {:charset "UTF-8"}]
           [:meta {:http-equiv "refresh" :content refresh-seconds}]
           [:style {:type "text/css"}
            "
            body, html
            {
              margin: 0; padding: 0; height: 100%; overflow: hidden;
            }
            #content
            {
              position:absolute; left: 0; right: 0; bottom: 0; top: 0px;
            }"
            ]
           ]
          [:body
           [:iframe {:src url :width "100%" :height "100%" :frameborder 0}
            "Your browser does not support iframes."
            ]]
          ])))

(defn now-in-seconds []
  (int (/ (.getTime (java.util.Date.)) 1000)))

(defn page-for [{:keys [refresh-seconds urls]}]
  (generate-page
    ; The idea here is we pick the next URL every time a refresh happens
    (nth urls (mod (int (/ (now-in-seconds) refresh-seconds)) (count urls)))
    refresh-seconds))

(defn sites []
  (env/env :sites))

(defn generate-route [key]
  (GET (str "/" (name key)) [] (page-for (key (sites)))))

(defn default-route [default-site]
  (GET "/" [] (response/redirect (str "///" (name default-site)))))

(defn app-routes []
  (apply routes (concat
                  (map (comp generate-route first) (sites))
                  (when-let [default-site (env/env :default-site)] [(default-route default-site)])
                  [(route/not-found "Not Found")])
         ))

(defn create-app []
  (wrap-defaults (app-routes) site-defaults))

(defn -main []
  (let [port (if-let [port (env/env :port)] port 3001)]
    (println "Starting web server on port" port)
    (run-jetty (create-app) {:port (Integer. port)})))

; This is provided for the lein ring plugin
(def app
  (create-app))
