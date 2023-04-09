;; There are two different statements of SUBLIS and SUB2 in the manual, on 
;; pages 12 and 61 respectively, although they are said to be semantically
;; equivalent; this is the version from page 12. 

sub2[a; z] = [null[a] -> z;
            eq[caar[a]; z] -> cdar[a];
            T -> sub2[cdar[a]; z]]

sublis[a; y] = [atom[y] -> sub2[a; y];
                T -> cons[sublis[a; car[y]];
                        sublis[a; cdr[y]]]]

;; this is the version from page 61               

sublis[x;y] = [null[x] -> y;
            null[y] -> y;
            T -> search[x;
                    λ[[j]; equal[y; caar[j]]];
                    λ[[j]; cdar[j]];
                    λ[[j]; [atom[y] -> y;
                        T -> cons[sublis[x; car[y]];
                                sublis[x; cdr[y]]]]]]]

;; the test for this is:
;; (SUBLIS '((X . SHAKESPEARE) (Y . (THE TEMPEST))) '(X WROTE Y))