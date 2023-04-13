;; This isn't working but it's really not far off.

(PUT 'CONC 'FEXPR
    ;; possibly ARGS should be (ARGS)...
    '(LABEL ARGS
        (COND ((COND ((ONEP (LENGTH ARGS)) ARGS)
            (T (ATTRIB (CAR ARGS) (APPLY CONC (CDR ARGS) NIL)))) ARGS))))