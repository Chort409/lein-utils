(ns leiningen.utils
  (:require [selmer.parser :as parser]
            [clojure.string :as str]
            [clj-http.client :as client])
  (:use clojure.data))

(defn conn-problem-exit []
  (leiningen.core.main/warn "Cannot connect to the repository, please check network connectivity to GitHub")
  (leiningen.core.main/exit))

(defn get-list-of-functions []
  (let [response (client/get "https://api.github.com/repos/chort409/lein-utils/contents/sources/"  {:as :json})]
    (if (= 200 (:status response))
      (map (fn [{:keys [name]}] (subs name 0 (- (count name) 4))) (:body response))
      (conn-problem-exit))))


(defn download-function [fn-name]
  (let [response (client/get (str "https://raw.githubusercontent.com/Chort409/lein-utils/master/sources/" fn-name ".clj"))]
    (if (= 200 (:status response))
      (:body response)
      (conn-problem-exit))))

(defn is-all-valid-functions [function-names]
  (let [[things-only-in-a _ _] (diff (set function-names) (set (get-list-of-functions)))]
    (if (not-empty things-only-in-a)
      (do
        (println (str "Unknowen function names: " things-only-in-a))
        things-only-in-a))))

(defn project-name-to-folder [project]
  (str "src/" (str/replace (:name project) #"-" "_") "/utils.clj") )

(defn read-two-objects-from-trusted-string [^String string]
  (with-open [r (java.io.PushbackReader. (java.io.StringReader. string))]
    (binding [*read-eval* false]
      [(read r)
       (read r)] )))

(defn convert-clj [result item]
  (let [kw (first item)]
    (if (kw result)
      (assoc result kw (concat (kw result) (rest item)))
      (assoc result kw (rest item)))))

(defn clj-to-map [^String clj-str]
  (let [objects (read-two-objects-from-trusted-string clj-str)]
    (as-> (rest (rest (first objects))) data
        (reduce convert-clj {:import [] :require []} data)
        (assoc data :function [(second objects)]))))

(defn agreegate [strings]
  (apply (partial merge-with #(distinct (concat %1 %2))) strings))

(defn utils
  "I don't do a lot."
  [project & [ & commands ]]
  (cond
    (empty? commands) (leiningen.core.main/warn "No arguments provided")
    (not-empty (is-all-valid-functions commands) ) (leiningen.core.main/exit)
    :else
    (as-> (pmap #(clj-to-map (download-function %)) commands) data
          (agreegate data)
          (assoc data :project-name (:name project))
          (parser/render-file "templates/template.clj" data)
          (spit (project-name-to-folder project) data))))