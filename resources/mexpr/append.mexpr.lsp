;; page 61

append[x; y] = [null[x] -> y; T -> cons[car[x]; append[cdr[x]; y]]]
