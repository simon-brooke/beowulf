;; page 59; slightly modified because I don't at this stage want to
;; assume the existence of CADR

get[x; y] = [null[x] -> NIL;
    eq[car[x]; y] -> car[cdr[x]];
    T -> get[cdr[x]; y]]