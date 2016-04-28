(ns kibit.rules.util
  (:require [cljs.core.logic :as logic])
  (:require-macros [cljs.core.logic.macros :as logic-m]))

(defn raw-rule? [rule]
  (println rule (meta rule))
  (:raw? (meta rule)))

(defn compile-rule [rule]
  (let [[pat alt] (logic/prep rule)]
    [(fn [expr] (logic-m/== expr pat))
     (fn [sbst] (logic-m/== sbst alt))]))
