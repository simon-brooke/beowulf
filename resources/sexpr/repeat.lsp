;; REPEAT is not present in the Lisp 1.5 manual, but it's so simple and so
;; useful that it seems a legitimate extension.

(DEFUN REPEAT (N X)
    (COND ((EQ N 0) NIL)
        (T (CONS X (REPEAT (SUB1 N) X)))))