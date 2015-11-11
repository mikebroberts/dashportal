(defproject dashportal "0.1.0-SNAPSHOT"
  ; USERS - Change this section for your given setup.
  ; For people that are savvy in Clojure, use any location that's valid with
  ; the 'environ' library
  :env {
        ; Each site will be available at '/[site]' . Visiting a site
        ; will refresh every n seconds, rotating around the specified URLs
        :sites {
                :news   {:refresh-seconds 10 :urls [
                                                    "http://nytimes.com"
                                                    "http://gu.com"
                                                    ]}
                ; Specifying a single url isn't pointless! This is useful
                ; if you want the dashboard to auto refresh,
                ; e.g. to refresh a re-deployed application
                :comics {:refresh-seconds 300 :urls ["http://xkcd.com/"]}
                }
        ; Which dashboard site to redirect to if we visit '/'. Use one of the
        ; keys you defined above
        :default-site :comics
        }

  ; *** USERS - NOTHING BELOW HERE OF INTEREST! ***

  :description "A portal for displaying rotating dashboard screens"
  :url "https://github.com/intentmedia/dashportal"
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
