(ns utils.bar
	(:require [clojure.string :as str] 
		  [clojure.java.io :as io]
		  [clojure.test :as test])	
	(:import  (java.io File FileOutputStream)))

(defn bar []
	(println "I am BAR!!!"))
