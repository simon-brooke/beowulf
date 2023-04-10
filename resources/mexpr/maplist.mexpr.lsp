;; page 63

maplist[l; f] = [null[l] -> nil;
                T -> cons[f[car[l]]; maplist[cdr[l]; f]]]