;; see page 70 of Lisp 1.5 Programmers Manual; this expands somewhat 
;; on the accounts of eval and apply given on page 13. This is M-expr
;; syntax, obviously. 

;; apply
;; NOTE THAT I suspect there is a typo in the printed manual in line
;; 7 of this definition, namely a missing closing square bracket before
;; the final semi-colon; that has been corrected here.

apply[fn;args;a] = [
    null[fn] -> NIL;
    atom[fn] -> [get[fn;EXPR] -> apply[expr; args; a];
                 get[fn;SUBR] -> {spread[args];
                                  $ALIST := a;
                                  TSX subr4, 4};
                 T -> apply[cdr[sassoc[fn; a; λ[[]; error[A2]]]]; args a]];
    eq[car[fn]; LABEL] -> apply[caddr[fn]; args; 
                                cons[cons[cadr[fn];caddr[fn]]; a]];
    eq[car[fn]; FUNARG] -> apply[cadr[fn]; args; caddr[fn]];
    eq[car[fn]; LAMBDA] -> eval[caddr[fn]; nconc[pair[cadr[fn]; args]; a]];
    T -> apply[eval[fn;a]; args; a]]

