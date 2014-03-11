(ns app.main
  (:use compojure.core
        korma.db
        korma.core
        korma.mysql)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clojure.data.json :as json]
            [clojure-csv.core :as csv]))

(defdb db (mysql {:db "lp_webapp" :user "root" :password "password"}))

(defentity form_submissions)

(defn- leads-from-page-id [page-id]
  "Load all leads belonging to the page identified by page-id.
   Result: vector of hash-maps."
  (select form_submissions
          (where {:page_uuid page-id})))

(defn- lead-to-hash [lead]
  "Convert the given lead into our canonical hash form."
  (let [form-data-hash (json/read-str (:form_data lead))
        form-data-hash-keywords (into {} (for [[k v] form-data-hash]
                                           [(keyword k) v]))
        partial-hash (apply hash-map [:variant (:variant_id lead)
                                      :ip_address (:submitter_ip lead)
                                      :page_uuid (:page_uuid lead)
                                      :date_submitted (:date_submitted lead)
                                      :time_submitted (:time_submitted lead)])]
    (conj partial-hash form-data-hash-keywords)))

(defn- leads-as-hashes [leads]
  "Convert all leads in the list to our canonical hash-map form."
  (map lead-to-hash leads))

(defn- leads-headers [hash-leads]
  "Get a canonicalized list of all headers for all given leads.
   hash-leads is the leads in our canonical hash-map form."
  (let [header-mess (distinct (flatten (map keys hash-leads)))]
    (-> [:page_uuid :date_submitted :time_submitted :variant :ip_address]
      (concat header-mess)
      (distinct))))

(defn- lead-data-in-order [headers hash-lead]
  "Given the canonicalized list of lead headings, get all values
   from the canonicalized hash-map lead in the same order."
  (map str (map #(hash-lead %) headers)))

(defn- leads-as-csv-arrays [hash-leads]
  "Generate a CSV that represents all leads.
   All leads must be in our canonicalized hash-map form."
  (let [headers (leads-headers hash-leads)]
    (concat [(map name headers)]
            (map #(lead-data-in-order headers %) hash-leads))))

(defn page-leads-as-csv [page-id]
  "Given a page ID, load all leads that belong to that page
   and then render them as an HTML CSV response."
  (let [leads (leads-from-page-id page-id)
        hash-leads (leads-as-hashes leads)
        csv-leads (leads-as-csv-arrays hash-leads)]
    {:headers {"Content-Type" "text/csv"}
     :body (csv/write-csv csv-leads)}))

;--------------------------- Compojure Routes ----------------------------------

(defroutes app-routes
  (GET "/page/:page_id" [page_id]
       (page-leads-as-csv page_id))
  (route/resources "/")
  (route/not-found "Not Found"))

;---------------------------- Ring Entrypoint ----------------------------------

(def app
  (handler/site app-routes))

