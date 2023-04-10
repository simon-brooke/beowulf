;; see page 70 of Lisp 1.5 Programmers Manual; this expands somewhat 
;; on the accounts of eval and apply given on page 13. This is M-expr
;; syntax, obviously. 

;; ## APPLY

;; NOTE THAT I suspect there is a typo in the printed manual in line
;; 7 of this definition, namely a missing closing square bracket before
;; the final semi-colon; that has been corrected here. 

;; RIGHT! So the 'EXPR' representation of a function is expected to be
;; on the `EXPR` property on the property list of the symbol which is 
;; its name; an expression is simply a Lisp S-Expression as a structure
;; of cons cells and atoms in memory. The 'SUBR' representation, expected
;; to be on the `SUBR` property, is literally a subroutine written in 
;; assembly code, so what is happening in the curly braces is putting the
;; arguments into processor registers prior to a jump to subroutine - TSX
;; being presumably equivalent to a 6502's JSR call.

;; This accounts for the difference between this statement and the version
;; on page 12: that is a pure interpreter, which can only call those host 
;; functions that are explicitly hard coded in.

;; This version knows how to recognise subroutines and jump to them, but I 
;; think that by implication at least this version can only work if it is
;; itself compiled with the Lisp compiler, since the section in curly braces
;; appears to be intended to be passed to the Lisp assembler.

;; apply[fn;args;a] = [
;;     null[fn] -> NIL;
;;     atom[fn] -> [get[fn;EXPR] -> apply[expr; args; a];
;;                  get[fn;SUBR] -> {spread[args];
;;                                   $ALIST := a;
;;                                   TSX subr4, 4};
;;                  T -> apply[cdr[sassoc[fn; a; λ[[]; error[A2]]]]; args a]];
;;     eq[car[fn]; LABEL] -> apply[caddr[fn]; args; 
;;                                 cons[cons[cadr[fn];caddr[fn]]; a]];
;;     eq[car[fn]; FUNARG] -> apply[cadr[fn]; args; caddr[fn]];
;;     eq[car[fn]; LAMBDA] -> eval[caddr[fn]; nconc[pair[cadr[fn]; args]; a]];
;;     T -> apply[eval[fn;a]; args; a]]