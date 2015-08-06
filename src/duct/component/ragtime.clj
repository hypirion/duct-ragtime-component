(ns duct.component.ragtime
  (:require [com.stuartsierra.component :as component]
            [ragtime.core :as core]
            [ragtime.jdbc :as jdbc]
            [ragtime.strategy :as strategy]))

(defrecord Ragtime [resource-path]
  component/Lifecycle
  (start [component]
    (assoc component
           :datastore  (-> component :db :spec jdbc/sql-database)
           :migrations (jdbc/load-resources resource-path)))
  (stop [component]
    (dissoc component :datastore :migrations)))

(defn ragtime [options]
  (map->Ragtime options))

(defn migrate
  [{:keys [datastore migrations strategy]
    :or {strategy strategy/raise-error}}]
  (core/migrate-all datastore {} migrations strategy))

(defn rollback
  [{:keys [datastore migrations]}]
  (core/rollback-last datastore (core/into-index migrations) 1))
