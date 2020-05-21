;----------------------------------------------------------
; Problem Set #4
; Date: May 18, 2020.
; Authors:
;          A01378562 Karla Daniela López Vega
;          A01371925 Luis Eduardo Díaz Niño de Rivera
;----------------------------------------------------------

(defmacro my-or
  "Make sure that any expression gets evaluated at most once.
  You may not use Clojure's or macro."
  ([] nil)
  ([x] x)
  ([x & args]
   `(let [t# ~x]
      (if t#
        ~x
        (my-or ~@args))))
)

(defmacro do-loop
  [& args]
  (let [type (first (last args))
        expression (second (last args))
        body (butlast args)]
    (if (= type :while)
      `(loop []
         (do ~@body)
         (if (not ~expression)
           nil
           (recur)))
      `(loop []
         (do ~@body)
         (if ~expression
           nil
           (recur))))))

(defmacro def-pred
  "Write a macro called def-pred,the macro should define two predicate functions: a regular one and its
  negated version"
  [& exprs]
  `(do (defn ~(first exprs) ~(second exprs) ~@(rest (rest exprs)))
       (defn ~(symbol (str 'not- (first exprs))) ~(second exprs) (not (do ~@(rest (rest exprs))))))
  )

(defmacro defn-curry
  " It takes as parameters a name, an args vector, and a body of one or more expressions.
  The macro should define a function called name that takes only the first argument from
  args and returns a function that takes the second argument from args and returns a function
  that takes the third argument from args, and so on."
  [name args & body]
  (let [d `(defn ~name ~(if (empty? args) [] [(first args)]))]
    (reverse (conj (reverse d)
                   (loop
                     [body `(do ~@body)
                      args (reverse (rest args))]
                     (if (empty? args)
                       body
                       (recur
                         (reverse (conj (reverse `(fn ~[(first args)])) body))
                         (rest args))))))))

(defn aux-if
  [expressions condition value]
  (let [then (rest (take-while #(not= value %) expressions))
        else (rest (drop-while #(not= value %) expressions))]
    `(if ~condition
       ~(cons `do then)
       ~(cons `do else)))
  )

(defmacro IF
  ([condition & args]
   (let [expressions (drop-while #(and (not= :ELSE %) (not= :THEN %)) args)]
     (let [count_else (count (take-while #(not= :ELSE %) expressions))
           count_then (count (take-while #(not= :THEN %) expressions))]
       (if (> count_else count_then)
         (aux-if expressions condition :ELSE)
         (aux-if expressions condition :THEN)
         )))))


