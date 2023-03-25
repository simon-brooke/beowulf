gcd[x;y] = [x>y -> gcd[y;x];
            rem[y;x] = 0 -> x;
            T -> gcd[rem[y;x];x]]