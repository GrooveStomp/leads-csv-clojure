(ns app.main
  (:use compojure.core
        korma.db
        korma.core
        korma.mysql)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.data.json :as json]))

(defdb db (mysql {:db "lp_webapp"
                  :user "root"
                  :password "password"}))

(defentity form_submissions)

(defn leads-from-page-id [page-id]
  (select form_submissions
          (where {:page_uuid page-id})))

(defn leads-as-hashes [leads]
  (map lead-to-hash leads))

(defn lead-to-hash [lead]
  (apply hash-map (conj [:variant (:variant_id lead)
                         :ip_address (:submitter_ip lead)
                         :page_uuid (:page_uuid lead)
                         :date_submitted (:date_submitted lead)
                         :time_submitted (:time_submitted lead)]
                        (keys (json/read-str (:form_data lead))))))

(defn leads-headers [hash-leads]
  (let [header-mess (distinct (flatten (map keys hash-leads)))]
    (-> [:page_uuid :date_submitted :time_submitted :variant :ip_address]
      (concat header-mess)
      (distinct))))

(defn lead-data-in-order [headers hash-lead]
  (map #(hash-lead %) headers))

(defn leads-as-csv-arrays [headers hash-leads]
  (conj [(leads-headers hash-leads)]
        (map leads-data-in-order hash-leads)))

(defroutes app-routes
  (GET "/pages/:page_id" [page_id]
       (let [leads (leads-from-page-id page_id)]
         (str "Leads: " leads)))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
