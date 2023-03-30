;; page 15
member[a; x] = [null[x] -> F;
    eq[a; car[x]] -> T;
    T-> member[a; cdr[x]]]