(ns leiningen.utils
  (:require [selmer.parser :as parser]
            [endophile.core :as endophile]
            [clojure.string :as str]
            [clj-http.client :as client])
  (:use clojure.data))

(defn- get-content [item]
  (first (:content item)))

(defn- transform [item]
  (let [titem (get-content item)]
    (if (= :code (:tag titem))
        (get-content (get-content item))
        titem)))

(defn convert-description-file [description]
  (apply merge
         (for [[header content] (partition 2 (endophile/to-clj (endophile/mp description)))]
           {(keyword (.toLowerCase (first (:content header)))) (transform content)})))

(defn combine [kword values]
  (distinct (apply concat (map #(str/split (kword %) #"\n") values))))


(defn agreegate [functions]
  {:requires (combine :requires functions)
   :imports  (combine :imports functions)
   :fns (map #(:function %) functions)
   :project-name "test"})

(defn conn-problem-exit []
  (leiningen.core.main/warn "Cannot connect to the repository, please check network connectivity to GitHub")
  (leiningen.core.main/exit))

(defn get-list-of-functions []
  (let [response (client/get "https://api.github.com/repos/chort409/lein-utils/contents/sources/"  {:as :json})]
    (if (= 200 (:status response))
      (map (fn [{:keys [name]}] (subs name 0 (- (count name) 3))) (:body response))
      (conn-problem-exit))))


(defn download-function [fn-name]
  (let [response (client/get (str "https://raw.githubusercontent.com/Chort409/lein-utils/master/sources/" fn-name ".md"))]
    (if (= 200 (:status response))
      (convert-description-file (:body response))
      (conn-problem-exit))))

(defn is-all-valid-functions [function-names]
  (let [[things-only-in-a ] (diff (set function-names) (set (get-list-of-functions)))]
    (if (not-empty things-only-in-a)
      (do
        (println (str "Unknowen function names: " things-only-in-a))
        things-only-in-a))))

(defn project-name-to-folder [project]
  (str "src/" (str/replace (:name project) #"-" "_") "/utils.clj") )

(defn utils
  "I don't do a lot."
  [project & [ & commands ]]
  (cond
    (empty? commands) (leiningen.core.main/warn "No arguments provided")
    (not-empty (is-all-valid-functions commands) ) (leiningen.core.main/exit)
    :else
      (spit (project-name-to-folder project) (parser/render-file "templates/template.clj" (agreegate (pmap #(download-function %) commands))))))