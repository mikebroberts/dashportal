(ns dashportal.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.util.response :as response]
            [environ.core :as env]
            [hiccup.core :refer :all]
            [ring.adapter.jetty :refer :all]
            [clojure.java.io :as io]
            [clojure.string :as cstr]
            ))

(defn load-template
  ([template-name replace-marker replace-content]
   (cstr/replace
     ; Consider optimizing resource load - cache ttl, or just def
     (slurp (io/resource (str "templates/" template-name)))
     replace-marker replace-content))
  ([template-name replace-marker-1 replace-content-1 replace-marker-2 replace-content-2]
   (cstr/replace (load-template template-name replace-marker-1 replace-content-1)
                 replace-marker-2 replace-content-2)))

(defn dashboards []
  (env/env :dashboards))

(defn full-screen-image-urls [board-name]
  (->> (file-seq (clojure.java.io/file (str "public/full-screen-image-files/" board-name)))
       (filter #(.isFile %))
       (map (comp (partial str "/full-screen-images/" board-name "/") #(.getName %)))))

(defn add-nav [& content]
  [[:nav.navbar.navbar-inverse.navbar-fixed-top
    [:div.container
     [:div.navbar-header
      [:a.navbar-brand {:href "/"} "Dashportal"]
      ]]]
   [:div.container
    content
    ]])

(defn list-dashboards []
  (load-template
    "dashboard-list.html" "**CONTENT**"
    (html (apply list
                 (add-nav
                   [:h2 "What's on the Dashboards?"]
                   (apply concat (map (fn [[board-name {:keys [rotate-seconds urls]}]]
                                        [[:h3 [:a {:href (str "/dashboards/" (name board-name))} (name board-name)]
                                          (str " - changes every " rotate-seconds " seconds")] ; Convert to anchor
                                         [:ul
                                          (map (fn [url] [:li [:a {:href url} url]]) (concat urls (full-screen-image-urls (name board-name))))
                                          ]]
                                        )
                                      (dashboards)))
                   )))))

(defn default-page []
  (if-let [default-dashboard (env/env :default-dashboard)]
    (response/redirect (str "///dashboards/" (name default-dashboard)))
    (list-dashboards)))

(defn load-dashboard [dashboard]
  (load-template "iframe-page.html" "**TITLE**" dashboard))

(defn full-screen-image [board-name image-file-name]
  (load-template "full-screen-image.html" "**IMAGE**" (str board-name "/" image-file-name)))

(defn now-in-seconds []
  (int (/ (.getTime (java.util.Date.)) 1000)))

(def flashes (atom {}))

(defn non-flash-url-for [dashboard]
  (let [{:keys [rotate-seconds urls]} (get (dashboards) (keyword dashboard))
        all-urls (concat urls (full-screen-image-urls dashboard))]
    (nth all-urls (mod (int (/ (now-in-seconds) rotate-seconds)) (count all-urls)))))

(defn flash-url-for [dashboard]
  (when-let [{:keys [url seconds flash-set]} (get @flashes dashboard)]
    (when (< (- (now-in-seconds) flash-set) seconds)
      (or url (str "/flash-message?dashboard=" dashboard))
      )))

(defn url-for [dashboard]
  (or (flash-url-for dashboard) (flash-url-for "ALL") (non-flash-url-for dashboard)))

(defn flash-background-colors [] {
                                  :regular "cornflowerblue"
                                  :warning "tomato"
                                  })

(defn flash-background-color [alert-level]
  (or
    (get (flash-background-colors) alert-level)
    (:regular (flash-background-colors))))

(defn flash-page-for-flash [{:keys [message alert-level]}]
  (println message alert-level)
  (load-template "flash-page.html"
                 "**MESSAGE**" message
                 "**BACKGROUND-COLOR**" (flash-background-color alert-level)))

(defn flash-page [dashboard]
  (let [flash (get @flashes dashboard)]
    (if (:message flash)
      (flash-page-for-flash flash)
      (do
        (println "No flash message for" dashboard " - redirecting to default page")
        (response/redirect (non-flash-url-for dashboard)))
      )))

(defn process-flash-update [{:keys [dashboards message url seconds alert-level]}]
  (swap! flashes assoc dashboards {
                                   :message     message
                                   :url         url
                                   ; ToDo Make default flash time configurable (might want to make javascript refresh config too)
                                   ; refresh time configurable too
                                   :seconds     (min (Integer. (or seconds "15")) 60)
                                   :alert-level (or (keyword alert-level) :regular)
                                   :flash-set   (now-in-seconds)
                                   })
  (println "Flash update!" dashboards "now set to" (get @flashes dashboards))
  "OK")

(defn app-routes []
  (apply routes (concat
                  ; To Do - consider how to entirely reload client. Special return? Separate API returning build number?
                  [
                   (GET "/" [] (default-page))
                   (GET "/list-dashboards" [] (list-dashboards))
                   (GET "/dashboards/:dashboard" [dashboard] (load-dashboard dashboard))
                   (GET "/full-screen-images/:board-name/:image-file-name" [board-name image-file-name]
                     (full-screen-image board-name image-file-name))
                   (GET "/flash-message" {params :params}
                     (flash-page (get params :dashboard)))

                   (GET "/api/urls/:dashboard" [dashboard] (url-for dashboard))
                   (POST "/api/flashes" {params :params} (process-flash-update params))

                   (GET "/test/flash-message" {{:keys [alert-level] :as all-ps} :params}
                     (flash-page-for-flash (if alert-level (assoc all-ps :alert-level (keyword alert-level)) all-ps)))

                   (route/files "/")
                   (route/not-found "Not Found")
                   ])))

(defn create-app []
  (wrap-defaults
    (app-routes)
    (update-in site-defaults [:security] dissoc :anti-forgery)))

(defn -main []
  (let [port (if-let [port (env/env :port)] port 3001)]
    (println "Starting web server on port" port)
    (run-jetty (create-app) {:port (Integer. port)})))

; This is provided for the lein ring plugin
(def app
  (create-app))
