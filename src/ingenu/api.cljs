(ns ingenu.api)
;   ; (:require 

; (defn api-get
;   [url success-handler error-handler] ;token
;   (-> (js/fetch url (clj->js {:method "GET"
;                               :headers {"Content-Type" "application/json"}}))
;                                         ; "Authorization" (str "Bearer " token)}}))
;       (.then (fn [resp]
;                (if (not (nil? resp))
;                  (let [ok (.-ok resp)
;                        handle-fn (fn [data]
;                                    (if ok
;                                      (success-handler data)
;                                      (error-handler data)))]
;                    (-> (.json resp)
;                        (.then handle-fn))))))
;       (.catch (fn [error]
;                 (error-handler error)))))

; (def base-url "localhost:3000/api/")


; (defn refresh-emails
;   []
;   (let [uri (str base-url "collect/email")]
;     (api-get uri
;              (fn [res]
;                (let [res (js->clj res :keywordize-keys true)]
;                  (print "RES:")
;                  (print res)
;                  (dispatch [:update-email-list (:all-emails res)])))
;              (fn [error] (print error)))))