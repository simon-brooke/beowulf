;; Not present in Lisp 1.5(!)

assoc[x; l] = [null[l] -> NIL;
            and[consp[car[l]]; eq[caar[l]; x]] -> cdar[l];
            T -> assoc[x; cdr[l]]]

;; (ASSOC 'C (PAIR '(A B C D E F) (RANGE 1 6)))