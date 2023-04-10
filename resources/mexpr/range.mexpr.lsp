;; this isn't a standard Lisp 1.5 function

range[n; m] = [lessp[m; n] -> NIL; T -> cons[n; range[add1[n]; m]]]