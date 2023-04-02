;; page 11

subst[x; y; z] = [equal[y; z] -> x;
                atom[z] -> z;
                T -> cons[subst[x; y; car[z]]; subst[x; y; cdr[z]]]]