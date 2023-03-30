copy[x] = [null[x] -> NIL; 
    atom[x] -> x; 
    T -> cons[ copy[ car[x]]; copy[ cdr[x]]]]