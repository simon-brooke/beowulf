;; page 15
union[x; y] = [null[x] -> y;
    member[car[x]; y] -> union[cdr[x]; y];
    T -> cons[car[x]; union[cdr[x]; y]]]