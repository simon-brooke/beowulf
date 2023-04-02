;; There are two different statements of SUBLIS and SUB2 in the manual, on 
;; pages 12 and 61 respectively, although they are said to be semantically
;; equivalent; this is the version from page 12. 

sub2[a; z] = [null[a] -> z;
            eq[caar[a]; z] -> cdar[a];
            T -> sub2[cdar[a]; z]]

sublis[a; y] = [atom[y] -> sub2[a; y];
                T -> cons[]]