(defproject dashportal "0.1.0-SNAPSHOT"
  ; USERS - Change this section for your given setup.
  ; For people that are savvy in Clojure, use any location that's valid with
  ; the 'environ' library
  :env {
        ; Each dashboard will be available at '/dashboards/[dashboard]' . Visiting a dashboard
        ; will refresh every n seconds, rotating around the specified URLs
        ; Included in the rotation list will also be any image saved in
        ; /public/full-screen-image-files/[dashboard]
        :dashboards        {
                       :news    {:rotate-seconds 10
                                 :urls            [
                                                   "http://nytimes.com"
                                                   "http://gu.com"
                                                   ]}

                       ; Specifying a single url isn't pointless! This is useful
                       ; if you want the dashboard to auto refresh,
                       ; e.g. to refresh a re-deployed application
                       :comics  {:rotate-seconds 300
                                 :urls            ["http://xkcd.com/"]}

                       ; We have 2 pictures in /public/full-screen-image-files/kittens
                       ; so those will be the second and third page in the rotation
                       :kittens {:rotate-seconds 20
                                 :urls            ["https://en.wikipedia.org/wiki/Kitten"]
                                 }
                       }
        ; If you want to set a default dashboard (i.e. where to go if we visit '/')
        ; set it below, specifiying one of the keys above.
        ; Otherwise don't set to use dashboard list (equivalent to visiting "/list-dashboards")
        ;:default-dashboard :kittens
        }

  ; *** USERS - NOTHING BELOW HERE OF INTEREST! ***

  :description "A portal for displaying rotating dashboard screens"
  :url "https://github.com/mikebroberts/dashportal"
  :min-lein-version "2.0.0"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [hiccup "1.0.5"]
                 [environ "1.0.1"]]

  :plugins [[lein-ring "0.9.6"]
            [lein-environ "1.0.1"]]

  :main dashportal.handler
  :ring {:handler dashportal.handler/app}
  :uberjar-name "dashportal-standalone.jar"
  )
