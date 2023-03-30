null[x] = [x = NIL -> T; T -> F]

(SETQ NULL 
    '(LAMBDA (X)
        (COND 
            ((EQUAL X NIL) 'T) 
            (T (QUOTE F)))))