(ns kibit.rules.misc
  (:require [cljs.core.logic :as logic]
            [kibit.rules.util])
  (:require-macros [kibit.rules.macros :refer [defrules]]
                   [cljs.core.logic.macros :as logic-m]))

;; Returns true if symbol is of
;; form Foo or foo.bar.Baz
(defn class-symbol? [sym]
  (let [sym (pr-str sym)
        idx (.lastIndexOf sym ".")]
    (if (neg? idx)
      (Character/isUpperCase (first sym))
      (Character/isUpperCase (nth sym (inc idx))))))

(defrules rules
  ;; clojure.string
  [(apply str (interpose ?x ?y)) (clojure.string/join ?x ?y)]
  [(apply str (reverse ?x)) (clojure.string/reverse ?x)]
  [(apply str ?x) (clojure.string/join ?x)]

  ;; mapcat
  [(apply concat (apply map ?x ?y)) (mapcat ?x ?y)]
  [(apply concat (map ?x . ?y)) (mapcat ?x . ?y)]

  ;; filter
  [(filter (complement ?pred) ?coll) (remove ?pred ?coll)]
  [(filter seq ?coll) (remove empty? ?coll)]
  [(filter (fn* [?x] (not (?pred ?x))) ?coll) (remove ?pred ?coll)]
  [(filter (fn [?x] (not (?pred ?x))) ?coll) (remove ?pred ?coll)]

  ;; first/next shorthands
  [(first (first ?coll)) (ffirst ?coll)]
  [(first (next ?coll))  (fnext ?coll)]
  [(next (next ?coll))   (nnext ?coll)]
  [(next (first ?coll))  (nfirst ?coll)]

  ;; Unneeded anonymous functions
  (let [fun (logic/lvar)
        args (logic/lvar)]
    [(fn [expr]
       (logic-m/all
         (logic-m/conde
           [(logic-m/== expr (list 'fn args (logic-m/llist fun args)))]
           [(logic-m/== expr (list 'fn* args (logic-m/llist fun args)))])
         (logic-m/pred fun #(or (keyword? %)
                                (and (symbol? %)
                                     (not-any? #{\/ \.} (str %)))))))
     #(logic-m/== % fun)])

  ;; Javascript stuff
  [(.toString ?x) (str ?x)]

  (let [obj (logic/lvar)
        method (logic/lvar)
        args (logic/lvar)]
    [#(logic-m/all
        (logic-m/== % (logic-m/llist '. obj method args))
        (logic-m/pred obj (complement class-symbol?)))
     #(logic-m/project [method args]
                       (let [s? (seq? method)
                             args (if s? (rest method) args)
                             method (if s? (first method) method)]
                         (logic-m/== % `(~(symbol (str "." method)) ~obj ~@args))))])

  (let [args (logic/lvar)
        klass (logic/lvar)
        static-method (logic/lvar)]
    [#(logic-m/all
        (logic-m/== % (logic-m/llist '. klass static-method args))
        (logic-m/pred klass class-symbol?))
     #(logic-m/project [klass static-method args]
                       (let [s? (seq? static-method)
                             args (if s? (rest static-method) args)
                             static-method (if s? (first static-method) static-method)]
                         (logic-m/== % `(~(symbol (str klass "/" static-method)) ~@args))))])

  ;; Threading
  (let [arg (logic/lvar)
        form (logic/lvar)]
    [#(logic-m/all (logic-m/== % (list '-> arg form)))
     (fn [sbst]
       (logic-m/conde
         [(logic-m/all
            (logic-m/pred form #(or (symbol? %) (keyword? %)))
            (logic-m/== sbst (list form arg)))]
         [(logic-m/all
            (logic-m/pred form seq?)
            (logic-m/project [form]
                             (logic-m/== sbst (list* (first form) arg (rest form)))))]))])

  (let [arg (logic/lvar)
        form (logic/lvar)]
    [#(logic-m/all (logic-m/== % (list '->> arg form)))
     (fn [sbst]
       (logic-m/conde
         [(logic-m/all
            (logic-m/pred form #(or (symbol? %) (keyword? %)))
            (logic-m/== sbst (list form arg)))]
         [(logic-m/all
            (logic-m/pred form seq?)
            (logic-m/project [form]
                             (logic-m/== sbst (concat form (list arg)))))]))])

  ;; Other
  [(not (some ?pred ?coll)) (not-any? ?pred ?coll)]
  [(with-meta ?x (?f (meta ?x) . ?arg)) (vary-meta ?x ?f . ?arg)])


(comment
  (apply concat (apply map f (apply str (interpose \, "Hello"))))
  (filter (complement nil?) [1 2 3])

  (.toString (apply str (reverse "Hello")))

  (map (fn [x] (inc x)) [1 2 3])
  (map (fn [x] (.method x)) [1 2 3])
  (map #(dec %) [1 2 3])
  (map #(.method %) [1 2 3])
  (map #(Double/parseDouble %) [1 2 3])
  (map (fn [x] (Integer/parseInteger x))
       [1 2 3])


  (map (fn [m] (:key m)) [some maps])
  (map (fn [m] (:key m alt)) [a b c])

  (. obj toString)
  (. obj toString a b c)

  (. Thread (sleep (read-string "2000")))
  (. Thread sleep (read-string "2000"))

  (-> x f) ;; (f x)
  (-> x (f a b)) ;; (f x a b)
  (-> x (f)) ;; (f x)

  (->> x f) ;; (f x)
  (->> x (f a b)) ;; (f a b x)
  (->> x (f)) ;; (f x)

  )
