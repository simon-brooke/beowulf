;; Bottom of page 66

(PUT 'SELECT 'FEXPR
    '(LABEL FORM
        (PROG (Q BODY)
            (SETQ Q (EVAL (CAR FORM))) ;; not sure that Q should be evaled.
            (SETQ BODY (CDR FORM))
            LOOP
            (COND 
                ((EQ NIL (CDR BODY)) (RETURN (CAR BODY)))
                ((EQ Q (EVAL (CAAR BODY))) (RETURN (CDAR BODY))))
            (SETQ BODY (CDR BODY))
            (GO LOOP))))