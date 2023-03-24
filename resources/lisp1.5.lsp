;; Test comment
(DEFINE
 (APPEND
  (LAMBDA
   (X Y)
   (COND ((NULL X) Y) (T (CONS (CAR X) (APPEND (CDR X Y)))))))
 (CONC
  (LAMBDA
   (X Y)
   (COND ((NULL (CDR X)) (RPLACD X Y)) (T (CONC (CDR X) Y)))
   X)))