(ns ingenu.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
 :get-greeting
 (fn [db _]
   (:greeting db)))

(reg-sub
 :all-emails-submitted
 (fn [db _]
   (print db)
   (:emails db)))

(reg-sub
 :dump-db
 (fn [db _]
   db))