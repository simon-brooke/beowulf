;; Common Lisp

(defun range (max &key (min 0) (step 1))
   (loop for n from min below max by step
      collect n))
      
(mapcar #'(lambda (x) (+ x 1)) (range 10))

(defun factoriali (n)
    (reduce #'* (range (+ n 1) :min 1 :step 1)))

(defun factorialr (n)
    (cond ((= n 1) 1)
        (t (* n (factorialr (- n 1))))))


;; Clojure
(defn factorial [n]
    (reduce *' (range 1 (+ n 1))))

(defn expt [n x]
  (reduce *' (repeat x n)))
