;; page 15

intersection[x;y] = [null[x] -> NIL;
    member[car[x]; y] -> cons[car[x]; intersection[cdr[x]; y]];
    T -> intersection[cdr[x]; y]]