(ns ingenu.db
  (:require [cljs.reader]
            [cljs.spec.alpha :as s]))

;; spec of app-db
(s/def ::drawer (s/nilable string?))
(s/def ::android (s/keys :req-un [::drawer]))

(s/def ::tab string?)
(s/def ::shared (s/keys :req-un [::tab]))

(s/def ::emails string?)
(s/def ::error (s/nilable string?))
(s/def ::loading? boolean?)

(s/def ::db (s/keys :req-un [::android
                             ::shared
                             ::emails
                             ::error
                             ::loading?]))

;; initial state of app-db
(def default-db {:android {:drawer nil} ;; not used (yet?)
                 :shared {:tab "Home"} ;; ignored
                 :emails ""
                 :error nil
                 :loading? false})
