;; page 12

pairlis[x;y;a] = [null[x] -> a;
                T -> cons[cons[car[x]; car[y]];
                        pairlis[cdr[x]; cdr[y]; a]]]