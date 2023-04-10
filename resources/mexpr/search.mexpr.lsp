# page 63

search[x; p; f; u] = [null[x] -> u[x];
                        p[x] -> f[x];
                        T -> search[cdr[x]; p; f; u]]