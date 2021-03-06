(ns kibit.rules.macros)

(defn- raw?
  [rule]
  (not (vector? rule)))

(defmacro defrules [name & rules]
  (let [raws (filter raw? rules)
        to-be-compiled (remove raw? rules)]
    `(let [rules# (for [rule# '~to-be-compiled]
                    (kibit.rules.util/compile-rule rule#))]
       (def ~name (vec (concat rules# [~@raws]))))))
