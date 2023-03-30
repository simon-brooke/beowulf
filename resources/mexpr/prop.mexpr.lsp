;; page 59
prop[x;y;u] = [null[x] -> u[];
    eq[car[x]; y] -> cdr[x];
    T -> prop[cdr[x]; y; u]]