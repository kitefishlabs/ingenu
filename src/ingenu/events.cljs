(ns ingenu.events
  (:require
   [re-frame.core :refer [reg-event-fx reg-event-db after]]
   [cljs.spec.alpha :as s]
   [ajax.core :refer [json-request-format json-response-format]]
   [day8.re-frame.http-fx]
   [ingenu.db :refer [default-db]]))

;; -- Interceptors ------------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/blob/master/docs/Interceptors.md
;;
(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db] ; [event]]
  (when-not (s/valid? spec db)
    (throw (ex-info (str "Spec check failed: " (s/explain-str spec db)) {}))))


(def validate-spec
  (if goog.DEBUG
    (after (partial check-and-throw :ingenu.db/db))
    []))



;; -- Handlers --------------------------------------------------------------

(def base-url "http://localhost:3000/api/")

(reg-event-db
 :error-handler
 (fn [db [_ resp]]
   (print "error: " resp)
   db))

(reg-event-db
 :load-emails
;  [validate-spec]
 (fn [db [_ response]]
   (-> db
       (assoc :loading? false)
       (assoc :emails (->
                       (js->clj response)
                       :all-emails
                       (clojure.string/replace #" " "\n"))))))

(reg-event-fx
 :initialize-db
 [validate-spec]
 (fn [{:keys [db]} _]
   {:db default-db}))

(reg-event-fx
 :request-emails
 (fn
   [{db :db} _]
   {:http-xhrio {:method :get
                 :uri (str base-url "collect/email")
                 :timeout 10000
                 :response-format (json-response-format {:keywords? true})
                 :on-success [:load-emails]
                 :on-failure [:error-handler]}
    :db (assoc db :loading? true)}))


(reg-event-fx
 :update-email-list
;  [validate-spec]
 (fn [{db :db} [_ value]]
   {:http-xhrio {:method :post
                 :uri (str base-url "collect/email")
                 :params {:email (str value)}
                 :timeout 10000
                 :format (json-request-format)
                 :response-format (json-response-format {:keywords? true})
                 :on-success [:request-emails]
                 :on-failure [:error-handler]}
    :db (assoc db :emails (str (:emails db) "\n" value))}))
