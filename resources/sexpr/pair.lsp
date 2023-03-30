;; PAIR is defined on page 60 of the manual, but the definition depends on both
;; PROG and GO, and I haven't got those working yet; so this is a pure 
;; functional implementation.
;; Return a list of pairs from lists `x` and `y`, required both to have the same
;; length.

(DEFUN PAIR (X Y) 
    (COND ((AND (NULL X) (NULL Y)) NIL)
        ((NULL X) (ERROR 'F2))
        ((NULL Y) (ERROR 'F3))
        (T (CONS (CONS (CAR X) (CAR Y)) (PAIR (CDR X) (CDR Y))))))