;; page 63. I'm not at all sure why an implementation using RPLACD is preferred
;; over a pure functional implementation here.

efface[x; l] = [null[l] -> NIL; 
            equal[x; car[l]] -> cdr[l];
            T -> rplacd[l; efface[x; cdr[l]]]]