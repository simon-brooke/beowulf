;; Page 12 of the manual; this does NOT do what I expect a modern
;; ASSOC to do!

;; Modern ASSOC would be:
;; assoc[x; l] = [null[l] -> NIL;
;;            and[consp[car[l]]; eq[caar[l]; x]] -> cdar[l];
;;            T -> assoc[x; cdr[l]]]

;; In the Lisp 1.5 statement of ASSOC, there's no account of what should happen
;; if the key (here `x`) is not present on the association list `a`. It seems 
;; inevitable that this causes an infinite run up the stack until it fails with
;; stack exhaustion. Consequently this may be right but I'm not implementing it!
;; assoc[x; a] = [equal[caar[a]; x] -> car[a];
;;            T -> assoc[x; cdr[a]]]

;; Consequently, my solution is a hybrid. It returns the pair from the 
;; association list, as the original does, but it traps the end of list
;; condition, as a modern solution would.

assoc[x; l] = [null[l] -> NIL;
            and[consp[car[l]]; eq[caar[l]; x]] -> car[l];
            T -> assoc[x; cdr[l]]]
