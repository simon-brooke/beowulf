(SETQ LENGTH 
    '(LAMBDA (L) 
        (COND 
            ((EQ NIL L) 0)
             ((CONSP (CDR L)) (ADD1 (LENGTH (CDR L))))
             (T 0))))