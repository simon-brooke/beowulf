;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Beowulf 0.3.0-SNAPSHOT Sysout file generated at 2023-04-05T23:30:32.954
;; generated by simon
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

((NIL 32767 APVAL NIL)
  (T 32767 APVAL T)
  (F 32767 APVAL NIL)
  (ADD1 32767 SUBR (BEOWULF HOST ADD1))
  (AND 32767 SUBR (BEOWULF HOST AND))
  (APPEND
    32767
    EXPR
    (LAMBDA
      (X Y) (COND ((NULL X) Y) (T (CONS (CAR X) (APPEND (CDR X) Y))))))
  (APPLY 32767 SUBR (BEOWULF BOOTSTRAP APPLY))
  (ASSOC
    32767
    EXPR
    (LAMBDA
      (X L)
      (COND
        ((NULL L) NIL)
        ((AND (CONSP (CAR L)) (EQ (CAAR L) X)) (CAR L))
        (T (ASSOC X (CDR L)))))
    SUBR (BEOWULF HOST ASSOC))
  (ATOM 32767 SUBR (BEOWULF HOST ATOM))
  (CAR 32767 SUBR (BEOWULF HOST CAR))
  (CAAAAR 32767 EXPR (LAMBDA (X) (CAR (CAR (CAR (CAR X))))))
  (CAAADR 32767 EXPR (LAMBDA (X) (CAR (CAR (CAR (CDR X))))))
  (CAAAR 32767 EXPR (LAMBDA (X) (CAR (CAR (CAR X)))))
  (CAADAR 32767 EXPR (LAMBDA (X) (CAR (CAR (CDR (CAR X))))))
  (CAADDR 32767 EXPR (LAMBDA (X) (CAR (CAR (CDR (CDR X))))))
  (CAADR 32767 EXPR (LAMBDA (X) (CAR (CAR (CDR X)))))
  (CAAR 32767 EXPR (LAMBDA (X) (CAR (CAR X))))
  (CADAAR 32767 EXPR (LAMBDA (X) (CAR (CDR (CAR (CAR X))))))
  (CADADR 32767 EXPR (LAMBDA (X) (CAR (CDR (CAR (CDR X))))))
  (CADAR 32767 EXPR (LAMBDA (X) (CAR (CDR (CAR X)))))
  (CADDAR 32767 EXPR (LAMBDA (X) (CAR (CDR (CDR (CAR X))))))
  (CADDDR 32767 EXPR (LAMBDA (X) (CAR (CDR (CDR (CDR X))))))
  (CADDR 32767 EXPR (LAMBDA (X) (CAR (CDR (CDR X)))))
  (CADR 32767 EXPR (LAMBDA (X) (CAR (CDR X))))
  (CDAAAR 32767 EXPR (LAMBDA (X) (CDR (CAR (CAR (CAR X))))))
  (CDAADR 32767 EXPR (LAMBDA (X) (CDR (CAR (CAR (CDR X))))))
  (CDAAR 32767 EXPR (LAMBDA (X) (CDR (CAR (CAR X)))))
  (CDADAR 32767 EXPR (LAMBDA (X) (CDR (CAR (CDR (CAR X))))))
  (CDADDR 32767 EXPR (LAMBDA (X) (CDR (CAR (CDR (CDR X))))))
  (CDADR 32767 EXPR (LAMBDA (X) (CDR (CAR (CDR X)))))
  (CDAR 32767 EXPR (LAMBDA (X) (CDR (CAR X))))
  (CDDAAR 32767 EXPR (LAMBDA (X) (CDR (CDR (CAR (CAR X))))))
  (CDDADR 32767 EXPR (LAMBDA (X) (CDR (CDR (CAR (CDR X))))))
  (CDDAR 32767 EXPR (LAMBDA (X) (CDR (CDR (CAR X)))))
  (CDDDAR 32767 EXPR (LAMBDA (X) (CDR (CDR (CDR (CAR X))))))
  (CDDDDR 32767 EXPR (LAMBDA (X) (CDR (CDR (CDR (CDR X))))))
  (CDDDR 32767 EXPR (LAMBDA (X) (CDR (CDR (CDR X)))))
  (CDDR 32767 EXPR (LAMBDA (X) (CDR (CDR X))))
  (CDR 32767 SUBR (BEOWULF HOST CDR))
  (CONS 32767 SUBR (BEOWULF HOST CONS))
  (CONSP 32767 SUBR (BEOWULF HOST CONSP))
  (COPY
    32767
    EXPR
    (LAMBDA
      (X)
      (COND
        ((NULL X) NIL)
        ((ATOM X) X) (T (CONS (COPY (CAR X)) (COPY (CDR X)))))))
  (DEFINE 32767 SUBR (BEOWULF HOST DEFINE))
  (DIFFERENCE 32767 SUBR (BEOWULF HOST DIFFERENCE))
  (DIVIDE
    32767
    EXPR
    (LAMBDA (X Y) (CONS (QUOTIENT X Y) (CONS (REMAINDER X Y) NIL))))
  (DOC 32767 SUBR (BEOWULF HOST DOC))
  (EFFACE
    32767
    EXPR
    (LAMBDA
      (X L)
      (COND
        ((NULL L) NIL)
        ((EQUAL X (CAR L)) (CDR L)) (T (RPLACD L (EFFACE X (CDR L)))))))
  (ERROR 32767 SUBR (BEOWULF HOST ERROR))
  (EQ 32767 SUBR (BEOWULF HOST EQ))
  (EQUAL 32767 SUBR (BEOWULF HOST EQUAL))
  (EVAL 32767 SUBR (BEOWULF BOOTSTRAP EVAL))
  (FACTORIAL
    32767
    EXPR (LAMBDA (N) (COND ((EQ N 1) 1) (T (TIMES N (FACTORIAL (SUB1 N)))))))
  (FIXP 32767 SUBR (BEOWULF HOST FIXP))
  (GENSYM 32767 SUBR (BEOWULF HOST GENSYM))
  (GET
    32767
    EXPR
    (LAMBDA
      (X Y)
      (COND
        ((NULL X) NIL)
        ((EQ (CAR X) Y) (CAR (CDR X))) (T (GET (CDR X) Y))))
    SUBR (BEOWULF HOST GET))
  (GREATERP 32767 SUBR (BEOWULF HOST GREATERP))
  (INTEROP 32767 SUBR (BEOWULF INTEROP INTEROP))
  (INTERSECTION
    32767
    EXPR
    (LAMBDA
      (X Y)
      (COND
        ((NULL X) NIL)
        ((MEMBER (CAR X) Y) (CONS (CAR X) (INTERSECTION (CDR X) Y)))
        (T (INTERSECTION (CDR X) Y)))))
  (LENGTH
    32767
    EXPR
    (LAMBDA
      (L)
      (COND ((EQ NIL L) 0) ((CONSP (CDR L)) (ADD1 (LENGTH (CDR L)))) (T 0))))
  (LESSP 32767 SUBR (BEOWULF HOST LESSP))
  (MAPLIST
    32767
    EXPR
    (LAMBDA
      (L F)
      (COND
        ((NULL L) NIL) (T (CONS (F (CAR L)) (MAPLIST (CDR L) F))))))
  (MEMBER
    32767
    EXPR
    (LAMBDA
      (A X)
      (COND
        ((NULL X) (QUOTE F))
        ((EQ A (CAR X)) T) (T (MEMBER A (CDR X))))))
  (MINUSP 32767 EXPR (LAMBDA (X) (LESSP X 0)))
  (NOT 32767 EXPR (LAMBDA (X) (COND (X NIL) (T T))))
  (NULL
    32767 EXPR (LAMBDA (X) (COND ((EQUAL X NIL) T) (T (QUOTE F)))))
  (NUMBERP 32767 SUBR (BEOWULF HOST NUMBERP))
  (OBLIST 32767 SUBR (BEOWULF HOST OBLIST))
  (ONEP 32767 EXPR (LAMBDA (X) (EQ X 1)))
  (PAIR
    32767
    EXPR
    (LAMBDA
      (X Y)
      (COND
        ((AND (NULL X) (NULL Y)) NIL)
        ((NULL X) (ERROR (QUOTE F2)))
        ((NULL Y) (ERROR (QUOTE F3)))
        (T (CONS (CONS (CAR X) (CAR Y)) (PAIR (CDR X) (CDR Y)))))))
  (PAIRLIS
    32767
    EXPR
    (LAMBDA
      (X Y A)
      (COND
        ((NULL X) A)
        (T (CONS (CONS (CAR X) (CAR Y)) (PAIRLIS (CDR X) (CDR Y) A)))))
    SUBR (BEOWULF HOST PAIRLIS))
  (PLUS 32767 SUBR (BEOWULF HOST PLUS))
  (PRETTY 32767)
  (PRINT 32767)
  (PROP
    32767
    EXPR
    (LAMBDA
      (X Y U)
      (COND
        ((NULL X) (U))
        ((EQ (CAR X) Y) (CDR X)) (T (PROP (CDR X) Y U)))))
  (QUOTE 32767 EXPR (LAMBDA (X) X))
  (QUOTIENT 32767 SUBR (BEOWULF HOST QUOTIENT))
  (RANGE
    32767
    EXPR
    (LAMBDA
      (N M)
      (COND
        ((LESSP M N) NIL) (T (CONS N (RANGE (ADD1 N) M))))))
  (READ 32767 SUBR (BEOWULF READ READ))
  (REMAINDER 32767 SUBR (BEOWULF HOST REMAINDER))
  (REPEAT
    32767
    EXPR
    (LAMBDA (N X) (COND ((EQ N 0) NIL) (T (CONS X (REPEAT (SUB1 N) X))))))
  (RPLACA 32767 SUBR (BEOWULF HOST RPLACA))
  (RPLACD 32767 SUBR (BEOWULF HOST RPLACD))
  (SET 32767 SUBR (BEOWULF HOST SET))
  (SUB1 32767 EXPR (LAMBDA (N) (DIFFERENCE N 1)) SUBR (BEOWULF HOST SUB1))
  (SUB2
    32767
    EXPR
    (LAMBDA
      (A Z)
      (COND
        ((NULL A) Z) ((EQ (CAAR A) Z) (CDAR A)) (T (SUB2 (CDAR A) Z)))))
  (SUBLIS
    32767 EXPR (LAMBDA (A Y) (COND ((ATOM Y) (SUB2 A Y)) (T (CONS)))))
  (SUBST
    32767
    EXPR
    (LAMBDA
      (X Y Z)
      (COND
        ((EQUAL Y Z) X)
        ((ATOM Z) Z)
        (T (CONS (SUBST X Y (CAR Z)) (SUBST X Y (CDR Z)))))))
  (SYSIN 32767 SUBR (BEOWULF IO SYSIN))
  (SYSOUT 32767 SUBR (BEOWULF IO SYSOUT))
  (TERPRI 32767)
  (TIMES 32767 SUBR (BEOWULF HOST TIMES))
  (TRACE 32767 SUBR (BEOWULF HOST TRACE))
  (UNION
    32767
    EXPR
    (LAMBDA
      (X Y)
      (COND
        ((NULL X) Y)
        ((MEMBER (CAR X) Y) (UNION (CDR X) Y))
        (T (CONS (CAR X) (UNION (CDR X) Y))))))
  (UNTRACE 32767 SUBR (BEOWULF HOST UNTRACE))
  (ZEROP 32767 EXPR (LAMBDA (N) (EQ N 0))))
