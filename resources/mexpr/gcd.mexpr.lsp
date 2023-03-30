gcd[x;y] = [x>y -> gcd[y;x];
            rem[y;x] = 0 -> x;
            T -> gcd[rem[y;x];x]]

;; gcd[x;y] = [x>y -> gcd[y;x]; remainder[y;x] = 0 -> x; T -> gcd[remainder[y;x];x]]