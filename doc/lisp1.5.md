
# LISP 1.5 Programmer's Manual

**The Computation Center and Research Laboratory of Electronics**

**Massachusetts Institute of Technology**

> [John McCarthy](https://en.wikipedia.org/wiki/John_McCarthy_(computer_scientist))
> [Paul W. Abrahams](https://mitpress.mit.edu/author/paul-w-abrahams-31449/)
> [Daniel J. Edwards](https://www.chessprogramming.org/Daniel_Edwards)
> [Timothy P. Hart](https://www.chessprogramming.org/Timothy_Hart)

> The M. I.T. Press
> Massachusetts Institute of Technology
> Cambridge, Massachusetts

The Research Laboratory af Electronics is an interdepartmental laboratory in which faculty members and graduate students from numerous academic departments conduct research.

The research reported in this document was made possible in part by support extended the Massachusetts Institute of Technology, Research Laboratory of Electronics, jointly by the U.S. Army, the U.S. Navy (Office of Naval Research), and the U.S. Air Force (Office of Scientific Research) under Contract DA36-039-sc-78108, Department of the Army Task 3-99-25-001-08; and in part by Contract DA-SIG-36-039-61-G14; additional support was received from the National Science Foundation (Grant G-16526) and the National Institutes of Health (Grant MH-04737-02).

Reproduction in whole or in part is permitted for any purpose of the United States Government.

SECOND EDITION Fifteenth printing, 1985

ISBN 0 262 130 1 1 4 (paperback)

-----

#### Note regarding this Markdown document

This Markdown version of the manual was created by me, [Simon Brooke](mailto:simon@journeyman.cc), by passing the PDF version found at [Software Preservation](https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf) through a [PDF to 
Markdown processor](https://pdf2md.morethan.io/), and hand-editing the resulting document.

**This document is not authorised by the copyright holders.** It was made for the purposes of study, only.

Generally I have tried to keep the text unaltered. Some minor headings, especially of examples, have been deliberately changed in order to aid navigation, and some apparent typographic errors have been corrected. *I have also added spaces between syntactic elements in M-expression examples to aid legibility.* Page numbers are taken from the original. Notes which I have added during editing are *NOTE: given in italics, like this*.

-----

## PREFACE

The over-all design of the LISP Programming System is the work of John McCarthy and is based on his paper "[Recursive Functions of Symbolic Expressions and Their Computation by Machine](http://www-formal.stanford.edu/jmc/recursive/recursive.html)" which was published in Communications of the ACM, April 1960.

This manual was written by Michael I. Levin.

The interpreter was programmed by [Stephen B. Russell](https://en.wikipedia.org/wiki/Steve_Russell_(computer_scientist)) and Daniel J. Edwards. The print and read programs were written by John McCarthy, Klim Maling, Daniel J. Edwards, and Paul W. Abrahams.

The garbage collector and arithmetic features Were written by Daniel J. Edwards. The compiler and assembler were written by Timothy P. Hart and Michael I. Levin. An earlier compiler was written by Robert Brayton.

The "LISP 1 Programmer's Manual" March 1, 1960, was written by [Phyllis A. Fox](https://en.wikipedia.org/wiki/Phyllis_Fox). Additional programs and suggestions were contributed by the following members of the Artificial Intelligence Group of the Research Laboratory of Electronics: Marvin L. Minsky, Bertram Raphael, Louis Hodes, David M. R. Park, David C. Luckham, Daniel G. Bobrow, James R. Slagle, and Nathaniel Rochester.

August 17, 1962

## TABLE OF CONTENTS

1. THE LISP LANGUAGE
    1. Symbolic Expressions
    2. Elementary Functions
    3. List Notation
    4. The LISP Meta-language
    5. Syntactic Summary
    6. A Universal LISP Function
2. THE LISP INTERPRETER SYSTEM
    1. Variables
    2. Constants
    3. Functions
    4. Machine Language Functions
    5. Special Forms
    6. Programming for the Interpreter
3. EXTENSION OF THE LISP LANGUAGE
    1. Functional Arguments
    2. Logical Connectives
    3. Predicates and Truth in LISP
4. ARITHMETIC IN LISP
    1. Reading and Printing Numbers
    2. Arithmetic Functions and Predicates
    3. Programming with Arithmetic
    4. The Array Feature
5. THE PROGRAM FEATURE
6. RUNNING THE LISP SYSTEM
    1. Preparing a Card Deck
    2. Tracing
    3. Error Diagnostics
    4. The cons Counter and errorset
7. LIST STRUCTURES
    1. Representation of List Structure
    2. Construction of List Structure
    3. Property Lists
    4. List Structure Operators
    5. The Free-Storage List and the Garbage Collector
8. A COMPLETE LISP PROGRAM - THE WANG ALGORITHM FOR THE PROPOSITIONAL CALCULUS

## APPENDICES

A. Functions and Constants in the LISP System
B. The LISP Interpreter
C. The LISP Assembly Program (LAP)
D. The LISP Compiler
E. OVERLORD - The Monitor
F. LISP Input and Output
G. Memory Allocation and the Garbage Collector
H. Recursion and the Push-Down List
I. LISP for SHARE Distribution

* INDEX TO FUNCTION DESCRIPTIONS
* GLOSSARY

<a name="page1">page 1</a>

## I. THE LISP LANGUAGE

The LISP language is designed primarily for symbolic data processing. It has been used for symbolic calculations in differential and integral calculus, electrical circuit theory, mathematical logic, game playing, and other fields of artificial intelligence.

LISP is a formal mathematical language. It is therefore possible to give a concise yet complete description of it. Such is the purpose of this first section of the manual. Other sections will describe ways of using LISP to advantage and will explain extensions of the language which make it a convenient programming system.

LISP differs from most programming languages in three important ways. The first way is in the nature of the data. In the LISP language, all data are in the form of symbolic expressions usually referred to as S-expressions. S-expressions are of indefinite length and have a branching tree type of structure, so that significant sub-expressions can be readily isolated. In the LISP programming system, the bulk of available memory is used for storing S-expressions in the form of list structures. This type of memory organization frees the programmer from the necessity of allocating storage for the different sections of his program.

The second important part of the LISP language is the source language itself which specifies in what way the S-expressions are to be processed. This consists of recursive functions of S-expressions. Since the notation for the writing of recursive functions of S-expressions is itself outside the S-expression notation, it will be called the meta language. These expressions will therefore be called M-expressions.

Third, LISP can interpret and execute programs written in the form of S-expressions. Thus, like machine language, and unlike most other higher level languages, it can be used to generate programs for further execution.

### 1.1 Symbolic Expressions

The most elementary type of S-expression is the atomic symbol.

**Definition**: An atomic symbol is a string of no more than thirty numerals and capital letters; the first character must be a letter.

#### Examples - atomic symbols

* A
* APPLE
* PART
* EXTRALONGSTRINGOFLETTERS
* A4B66XYZ

These symbols are called atomic because they are taken as a whole and are not capable of being split within LISP into individual characters, Thus A, B, and AB have no relation to each other except in so far as they are three distinct atomic symbols.

All S-expressions are built out of atomic symbols and the punctuation marks 

<a name="page2">page 2</a>

`(` `)` and `.`. The basic operation for forming S-expressions is to combine two of them to produce a larger one. From the two atomic symbols A1 and A2, one can form the S-expression `(A1 . A2)`.

**Definition**: An S-expression is either an atomic symbol or it is composed of these elements in the following order: a left parenthesis, an S-expression, a dot, an S-expression, and a right parenthesis.

Notice that this definition is recursive.

#### Examples - S-expressions

* ATOM
* (A B)
* (A . (B C))
* ((A1 . A2) . B)
* ((U V) . (X . Y))
* ((U VI . (X (Y Z)))

### 1.2 Elementary Functions

We shall introduce some elementary functions of S-expressions. To distinguish the functions from the S-expressions themselves, we shall write function names in lower case letters, since atomic symbols consist of only upper case letters. Furthermore, the arguments of functions will be grouped in square brackets rather than parentheses. As a separator or punctuation mark we shall use the semicolon.

The first function that we shall introduce is the function `cons`. It has two arguments and is in fact the function that is used to build S-expressions from smaller S-expressions.

#### Examples - the cons function

```
cons[A; B]=(A . B)
cons[(A . B); C] = ((A . B) . C)
cons[cons[A; B]; C] = ((A . B) . C)
```

The last example is an instance of composition of functions. It is possible to build any S-expression from its atomic components by compositions of the function cons. The next pair of functions do just the opposite of cons. They produce the subexpressions of a given expression.

The function `car` has one argument. Its value is the first part of its composite argument. `car` of an atomic symbol is undefined.

*Note that where this says 'car of an atomic symbol is undefined', it seems to mean it literally. There seems to have been no mechanism for distinguishing cons cells from other items in memory, so that the car of, for example, a decimal number could be taken, although the result 

#### Examples - the car function

```
car[(A . B)] = A
car[(A . (B1 . B2))] = A
car[((A1 . A2) . B)] = (A1 . A2)
car[A] is undefined
```

<a name="page3">page 3</a>

The function `cdr` has one argument. Its value is the second part of its composite
argument. `cdr` is also undefined if its argument is atomic.

#### Examples - the cdr function

```
cdr[(A . B)] = B
cdr[(A . (B1 . B2))] = (B1 . B2)
cdr[((A1 . A2) . B)] = B
cdr[A] is undefined
car[cdr[(A . (B1 . B2))]] = B1
car[cdr[(A . B)]] is undefined
car[cons[A; B]] = A
```

Given any S-expression, it is possible to produce any subexpression of it by a
suitable composition of `car`s and `cdr`s. If `x` and `y` represent any two S-expressions,
the following identities are true:

```
car[ cons[x; y]] = x
cdr[ cons[x; y]] = y
```

The following identity is also true for any S-expression x such that x is composite
(non-atomic):

```
cons[car[x]; cdr[x]] = x
```

The symbols `x` and `y` used in these identities are called variables. In LISP, variables are used to represent S-expressions. In choosing names for variables and functions, we shall use the same type of character strings that are used in forming atomic
symbols, except that we shall use lower case letters.

A function whose value is either `true` or `false` is called a predicate. In LISP, the
values `true` and `false` are represented by the atomic symbols `T` and `F`, respectively.
A LISP predicate is therefore a function whose value is either `T` or `F`.

The predicate `eq` is a test for equality on atomic symbols. It is undefined for
non-atomic arguments. *NOTE: this differs from the statement given on [page 57](#page57).*

#### Examples - eq

```
eq[A; A] = T
eq[A; B] = F
eq[A; (A . B)] is undefined
eq[(A . B);(A . B)] is undefined
```

The predicate `atom` is true if its argument is an atomic symbol, and false if its
argument is composite.

#### Examples - atom

```
atom[EXTRALONGSTRINGOFLETTERS] = T
atom[(u . v)] = F
atom[car[(u . v)]] = T
```

<a name="page4">page 4</a>

### 1.3 List Notation

The S-expressions that have been used heretofore have been written in dot notation. It is usually more convenient to be able to write lists of expressions of indefinite length, such as `(A B C D E)`.

Any S-expression can be expressed in terms of the dot notation. However, LISP has an alternative form of S-expression called the list notation. The list `(m1 m2... mn)` can be defined in terms of dot notation. It is identical to `(m1 . (m2 . (... . (mn . NIL)... )))`.

The atomic symbol NIL serves as a terminator for lists. The null list `()` is identical to `NIL`. Lists may have sublists. The dot notation and the list notation may be used in the same S-expression,

Historically, the separator for elements of lists was the comma `(,)`; however, the blank is now generally used. The two are entirely equivalent in LISP. `(A, B, C)` is identical to `(A B C)`.

#### Examples - list notation

```lisp
(A B C) = (A . (B . (C . NIL)))
((A B) C) = ((A . (B . NIL)) . (C . NIL))
(A B (C D)) = (A . (B . ((C . (D . NIL)). NIL)))
(A) = (A . NIL)
((A))=((A . NIL) . NIL)
(A (B . C)) = (A . ((B . C) . NIL))
```

It Is important to become familiar with the results of elementary functions on S-expressions written in list notation. These can always be determined by translating into dot notation.

#### Examples - list notation 2

```
car[(A B C)] = A
cdr[(A B C)] = (B C)
cons[A; (B C)] = (A B C)
car[((A B) C)] = (A B)
cdr[(A)] = NIL
car[cdr[(A B C)]] = B
```

It is convenient to abbreviate multiple `car`s and `cdr`s. This is done by forming function names that begin with `c`, end with `r`, and have several `a`s and `d`s between them.

#### Examples - composed accessor functions

```
cadr[(A B C)] = car[cdr[(A B C)]] = B
caddr[(A B C)] = C
cadadr[(A (B C) D)] = C
```

<a name="page5">page 5</a>

The last `a` or `d` in the name actually signifies the first operation in order to be
performed, since it is nearest to the argument.

### 1.4 The LISP Meta-language

We have introduced a type of data called S-expressions, and five elementary functions of S-expressions. We have also discussed the following features of the meta-language.

1. Function names and variable names are like atomic symbols except that they use lower case letters.
2. The arguments of a function are bound by square brackets and separated from each other by semicolons.
3. Compositions of functions may be written by using nested sets of brackets. These rules allow one to write function definitions such as `third[x]=car[cdr[cdr[x]]]`.

This function selects the third item on a list. For example, `third` is actually the same function as `caddr`.

The class of functions that can be formed in this way is quite limited and not very interesting. A much larger class of functions can be defined by means of the conditional expression, a device for providing branches in function definitions. A conditional expression has the following form:

> where each p<sub>i</sub> is an expression whose value may be truth or falsity, and each e<sub>i</sub> is
> any expression. The meaning of a conditional expression is: if p<sub>1</sub> is true. then the
> value of e<sub>1</sub> is the value of the entire expression. If p<sub>1</sub> is false, then if p<sub>2</sub> is true
> the value of e<sub>2</sub> is the value of the entire expression. The p<sub>i</sub> are searched from left
> to right until the first true one is found. Then the corresponding e<sub>i</sub> is selected. If
> none of the p<sub>i</sub> are true, then the value of the entire expression is undefined.
>
> Each p<sub>i</sub> or e<sub>i</sub> can itself be either an S-expression, a function, a composition of
> functions or may itself be another conditional expression.

#### Example - conditional expression

`[eq[car[x]; A] -> cons[B; cdr[x]]; T -> x]`

The atomic symbol `T` represents truth. The value of this expression is obtained
if one replaces `car` of `x` by B if it happens to be A, but leaving `x` unchanged if `car` of
it is not A.

<a name="page6">page 6</a>

The main application of conditional expressions is in defining functions recursively.

#### Example - recursive function

`ff[x] = [atom[x] -> x; T -> ff[car[x]]]`

This example defines the function `ff` which selects the first atomic symbol of any
given expression. This expression can be read: If `x` is an atomic symbol, then `x`
itself is the answer. Otherwise the function `ff` is to be applied to car of `x`.

If `x` is atomic, then the first branch which is `x` will be selected. Otherwise, the
second branch `ff[car[x]]` will be selected, since `T` is always true.

The definition of `ff` is recursive in that `ff` is actually defined in terms of itself. If
one keeps taking `car` of any S-expression, one will eventually produce an atomic symbol; therefore the process is always well defined.

Some recursive functions may be well defined for certain arguments only, but infinitely recursive for certain other arguments. When such a function is interpreted in the LISP programming system, it will either use up all of the available memory, or loop until the program is halted artificially.

We shall now work out the evaluation of `ff[((A. B). C)]`. First, we substitute the
arguments in place of the variable `x` in the definition and obtain
```
ff[((A . B) . C)]=[atom[((A . B) . C)]->((A . B) . C); T->ff[car[((A . B) . C)]]]
```
but `((A. B). C)` is not atomic, and so we have
```
= [T->ff [car[((A . B) . C)]]
= ff[car[((A . B) . C)]]
= ff[(A . B)]
```
At this point, the definition of ff must be used recursively. Substituting `(A . B)`
for `x` gives
```
= [atom[(A . B)] -> (A . B); T -> ff[car[(A . B)]]]
= [T -> ff[car[(A . B)]]]
= ff[car[(A . B)]]
= ff[A]
= [atom[A] -> A; T -> ff[car[A]]]
= A
```

The conditional expression is useful for defining numerical computations, as well as computations with S-expressions. The absolute value of a number can be defined by
```
|x| = [x<0 -> -x; T -> x]
```
The factorial of a non-negative integer can be defined by
```
n! = [n=0 -> 1; T -> n.[n-l]!]
```
This recursive definition does not terminate for negative arguments. A function that

<a name="page7">page 7</a>

is defined only for certain arguments is called a partial function.

The Euclidean algorithm for finding the greatest common divisor of two positive integers can be defined by using conditional expressions as follows:

```
gcd[x; y]=[x>y -> gcd[y; x];
        rem[y;x]=0 -> x]
```

`rem[u; v]` is the remainder when `u` is divided by `v`.

A detailed discussion of the theory of functions defined recursively by conditional expressions is found in [A Basis for a Mathematical Theory of Computation](http://jmc.stanford.edu/articles/basis/basis.pdf) by J. McCarthy, Proceedings of the Western Joint Computer Conference, May 1961 (published by the Institute of Radio Engineers).

It is usual for most mathematicians -- exclusive of those devoted to logic -- to use the word 'function' imprecisely, and to apply it to forms such as y<sup>2</sup>+x. Because we shall later compute with expressions that stand for functions, we need a notation that expresses the distinction between functions and forms. The notation that we shall use is the [lambda notation of Alonzo Church](https://compcalc.github.io/public/church/church_calculi_1941.pdf).

Let  `f`be an expression that stands for a function of two integer variables. It
should make sense to write `f[3; 4]` and to be able to determine the value of this expres-
sion. For example, `sum[3; 4] = 7`. The expression y<sup>2</sup> + x does not meet this requirement.
It is not at all clear whether the value of y<sup>2</sup> + x[3; 4]  is 13 or 19. An expression such as
y<sup>2</sup> + x will be called a form rather than a function. A form can be converted to a function by specifying the correspondence between the variables in the form and the arguments of the desired function.

If &epsilon; is a form in the variables x<sub>1</sub>;... ;x<sub>n</sub>, then the expression &lambda;[[x<sub>1</sub>;... ;x<sub>n</sub>]&epsilon;] represents the function of n variables obtained by substituting the n arguments in order for the variables x<sub>1</sub>;... ;x<sub>n</sub>, respectively. For example, the function &lambda;[[x; y]; y<sup>2</sup> + x] is a function of two variables, and &lambda;[[x; y]; y<sup>2</sup> + x][3; 4] =4<sup>2</sup> + 3 = 19. &lambda;[[x; y]; y<sup>2</sup> + x][4; 3] = 3<sup>2</sup> + 4 = 13.

*TODO: detail formatting in the above paragraph is still slightly wrong.*

The variables in a lambda expression are dummy or bound variables because systematically changing them does not alter the meaning of the expression. Thus  &lambda;[[u; v]; u<sup>2</sup> + v] means the same thing as  &lambda;[[x; y]; y<sup>2</sup> + x].

We shall sometimes use expressions in which a variable is not bound by a lambda. For example, in the function of two variables   &lambda;[[x; y]; x<sup>n</sup> +  y<sup>n</sup>] the variable `n` is not bound. This is called a free variable. It may be regarded as a parameter. Unless `n` has been given a value before trying to compute with this function, the value of the function must be undefined.

<a name="page8">page 8</a>

The lambda notation alone is inadequate for naming recursive functions. Not only must the variables be bound, but the name of the function must be bound, since it is used inside an expression to stand for the entire expression. The function `ff` was previously defined by the identity

`ff[x] = [atom[x] -> x; T -> ff[car[x]]]`

Using the lambda notation, we can write

`ff =` &lambda;`[x] = [atom[x] -> x; T -> ff[car[x]]]`

The equality sign in these identities is actually not part of the LISP meta-language and is only a crutch until we develop the correct notation. The right side of the last equation cannot serve as an expression for the function `ff` because there is nothing to indicate that the occurrence of `ff` inside it stands for the function that is being defined.

In order to be able to write expressions that bear their own name, we introduce
the label notation. If &epsilon; is an expression, and &alpha; is its name, we write label[&alpha;; &epsilon;].

The function `ff` can now be written without an equal sign:

`label[ff =` &lambda;`[[x]; [atom[x] -> x; T -> ff[car[x]]]]`

In this expression, `x` is a bound variable, and `ff` is a bound function name.

### 1.5 Syntactic Summary

[This section is for completeness and may be skipped upon first reading.]

All parts of the LISP language have now been explained. That which follows is a complete syntactic definition of the LISP language, together with semantic comments. The definition is given in [Backus notation](https://www.softwarepreservation.org/projects/ALGOL/paper/Backus-ICIP-1959.pdf) with the addition of three dots(...) to avoid naming unnecessary syntactic types.

In Backus notation the symbols `::=`, `<`, `>`, and `|` are used. The rule

```BNF
<S-expression> ::= <atomic symbol> | (<S-expression> . <S-expression>) 
```

means that an S-expression is either an atomic symbol, or it is a left parenthesis followed by an S-expression followed by a dot followed by an S-expression followed by a right parenthesis. The vertical bar means "or" , and the angular brackets always enclose elements of the syntax that is being defined.

#### The Data Language

```BNF
<LETTER> ::= A|B|C| ... |Z
<number> ::= 0|1|2| ... |9
<atomic-symbol> ::= <LETTER><atom part>
<atom part> ::= <empty> | <LETTER><atom part> | <number><atom part>
```
Atomic symbols are the smallest entities in LISP. Their decomposition into characters has no significance.

<a name="page9">page 9</a>

```BNF
<S-expression> ::= <atomic symbol> |
                    (<S-expression> . <S-expression>) |
                    (<S-expression> ... <S-expression>)
```
When three dots are used in this manner, they mean that any number of the given type of symbol may occur, including none at all. According to this rule, `( )` is a valid S-expression. (It is equivalent to `NIL`. )

The dot notation is the fundamental notation of S-expressions, although the list notation is often more convenient. Any S-expression can be written in dot notation.

#### The Meta-Language

```BNF
<letter> ::= a|b|c| ... |z
<identifier> ::= <letter><id part>
<id part> ::= <empty> | <letter><id part> | <number><id part>
```
The names of functions and variables are fornied in the same manner as atomic symbols but with lower-case letters.

```BNF
<form> ::= <constant> |
           <variable> |
           <function>[<argument> ... <argument>] |
           [<form> -> <form>; ... ; <form> -> <form>]
<constant> ::= <S-expression>
<variable> ::= <identifier>
<argument> ::= <form>
           
```

A form is an expression that can be evaluated. A form that is merely a constant has that constant *[sic]* as its value. If a form is a variable, then the value of the form is the S-expression that is bound to that variable at the time when we evaluate the form,

The third part of this rule states that we may write a function followed by a list of arguments separated by semicolons and enclosed in square brackets. The expressions for the arguments are themselves forms; this indicates that compositions of functions are permitted.

The last part of this rule gives the format of the conditional expression. This is evaluated by evaluating the forms in the propositional position in order until one is found whose value is `T`. Then the form after the arrow is evaluated and gives the value of the entire expression.

`<function>::=<identifier> |` &lambda;`[<var list >;<form >] | label[<identifier>; <function>]`

`<var list> ::= [<variable >; ... ; <variable>]`

A function can be simply a name. In this case its meaning must be previously understood. A function may be defined by using the lambda notation and establishing a correspondence between the arguments and the variables used in a form. If the function is recursive, it must be given a name by using a label.

<a name="page10">page 10</a>

### 1.6 A Universal LISP Function
An interpreter or universal function is one that can compute the value of any given function applied to its arguments when given a description of that function. (Of course, if the function that is being interpreted has infinite recursion, the interpreter will recur infinitely also. )

We are now in a position to define the universal LISP function `evalquote[fn;args]`. When `evalquote` is given a function and a list of arguments for that function, it computes the value of the function applied to the arguments.

LISP functions have S-expressions as arguments. In particular, the argument `fn` of the function `evalquote` must be an S-expression. Since we have been writing functions as M-expressions, it is necessary to translate them into S-expressions.

The following rules define a method of translating functions written in the meta-language into S-expressions.

1. If the function is represented by its name, it is translated by changing all of the letters to upper case, making it an atomic symbol. Thus `car` is translated to `CAR`.

2. If the function uses the lambda notation, then the expression &lambda;`[x1; ...; xn]`&epsilon;`]` is translated into (LAMBDA (X1... XN) &epsilon;&ast;), where  &epsilon;&ast; is the translation of  &epsilon;.
3. If the function begins with label, then the translation of label[&alpha;; &epsilon;] is (LABEL
    &alpha;&ast; &epsilon;&ast;).

Forms are translated as follows:

1. A variable, like a function name, is translated by using uppercase letters. Thus the translation of `var1` is `VAR1`.

2. The obvious translation of letting a constant translate into itself will not work. Since the translation `x` of is `X`, the translation of `X` must be something else to avoid ambiguity. The solution is to quote it. Thus `X` is translated into `(QUOTE X)`.

3. The form `fn[arg`<sub>1</sub>`; ... ;arg`<sub>n</sub>`]` is translated into `(fn* arg`<sub>1</sub>&ast; `... arg`<sub>n</sub>&ast;`)`.

4. The conditional expression [p<sub>1</sub> -> e<sub>1</sub>; ... ; p<sub>n</sub> -> e<sub>n</sub>] is translated into 

   (COND (p<sub>1</sub>&ast; e<sub>1</sub>&ast;) ... (p<sub>n</sub>&ast; e<sub>n</sub>&ast;))

#### Examples - translation, M-expressions to S-expressions

| M-expressions | S -expressions |
| ---- | ---- |
| X    | X    |
| car     | CAR     |
| car[x]     | (CAR X)     |
| T | (QUOTE T) |
| ff[car[X]] | (FF (CAR X)) |
| [atom[x]-x; T-ff [car [x]]] | (COND ((ATOM X) X) ((QUOTE T) (FF (CAR X)))) |
| label[ff ;h[[x];[atom[x]-x; T-ff[car [X]]]] | (LABEL FF (LAMBDA (X) (COND ((ATOM X) X) ((QUOTE T) (FF (CAR X)))))) |

Some useful functions for handling S-expressions are given below. Some of them

<a name="page11">page 11</a>

are needed as auxiliary functions for `evalquote`.

`equal[x;y]`

This is a predicate that is true if its two arguments are identical S-expressions,
and is false if they are different. (The elementary predicate - eq is defined only for
atomic arguments. ) The definition of equal is an example of a conditional expression
inside a conditional expression.

```
equal[x; y]=[atom[x] -> [atom[y] -> eq[x; y]; T -> F];
             equal[car[x]; car[y]] -> equal[cdr[x]; cdr[y]];
             T -> F]
```

This can be translated into the following S-expression: ,

```lisp
(LABEL EQUAL 
    (LAMBDA (X Y) 
        (COND ((ATOM X) (COND ((ATOM Y) (EQ X Y)) 
                            ((QUOTE T) (QUOTE F))))
            ((EQUAL (CAR X) (CAR Y)) (EQUAL (CDR X) (CDR Y)))
            ((QUOTE T)(QUOTE F)))))
```

#### subst[x; y; z]

This function gives the result of substituting the S-expression x for all occurrences of the atomic symbol y in the S-expression z. It is defined by

```
subst[x; y; z] = [equal[y; z] -> x; 
        atom[z] - z;
        T - cons[subst
            [x; y; car[z]]; subst[x; y; cdr[z]]]]
```

As an example, we have

```lisp
SUBST[(X . A); B; ((A . B) . c)] = ((A . (X . A)) . C)
```

#### null[x]
This predicate is useful for deciding when a list is exhausted. It is true if and only if its argument is NIL.

The following functions are useful when S-expressions are regarded as lists.

#### 1. append[x; y]
``` 
append[x; y] = [null[x] -> y; T -> cons[car [x]; append[cdr [x]; y]]]
```

An example is

```
append[(A B);(C D E)] = (A B C D E)
```

#### 2. member[x; y]

This predicate is true if the S-expression `x` occurs among the elements of the list `y`. We have
```
member[x; y] = [null[y] -> F;
        equal[x; car [y ]] ->T;
        T -> member[x; cdr [y ]]]
```

#### 3. pairlis[x; y; a]

<a name="page12">page 12</a>

This function gives the list of pairs of corresponding elements of the lists `x` and
`y`, and appends this to the list `a`. The resultant list of pairs, which is like a table with
two columns, is called an association list. We have

```
pairlis [x; y; a] = [null[x] -> a; 
        T -> cons[cons[car[x]; car[y]];
            pairlis[cdr[x]; cdr [y]; a]]]
```

An example is

```
pairlis[(A B C);(U V W);((D . X) (E . Y))] =
((A . U) (B . V) (C . W)(D . X) (E . Y))
```

#### 4. assoc[x; a]

If `a` is an association list such as the one formed by `pairlis` in the above example,
then `assoc` will produce the first pair whose first term is `x`. Thus it is a table searching
function. We have

```
assoc[x; a] = [equal[caar[a]; x] -> car[a]; T -> assoc[x; cdr[a]]]
```

An example is

```
assoc[B; ((A . (M N)), (B . (CAR X)), (C . (QUOTE M)), (C . (CDR x)))]
= (B . (CAR X))
```

##### 5. sublis[a; y]

Here `a` is assumed to be an association list of the form ((u<sub>1</sub>. v<sub>1</sub>)... (u<sub>n</sub> . v<sub>n</sub>)),
where the `u`s are atomic, and `y` is any S-expression. What `sublis` does, is to treat
the `u`s as variables when they occur in `y`, and to substitute the corresponding `v`s
from the pair list. In order to define `sublis`, we first define an auxiliary function. We have

```
sub2[a; z] = [null[a] -> z; eq[caar[a]; z] -> cdar[a];
        T -> sub2[cdr[a]; z]]
```
and

```
sublis[a; y] = [atom[y] -> sub2[a; y]; 
        T -> cons[sublis[a; car[y]]; sublis[a; cdr[y]]]]
```

An example is

```
sublis[((X. SHAKESPEARE) (Y. (THE TEMPEST)));(X WROTE Y)] =
(SHAKESPEARE WROTE (THE TEMPEST))
```

The universal function `evalquote` that is about to be defined obeys the following identity. Let `f` be a function written as an M-expression, and let `fn` be its translation. (`fn` is an S-expression. ) Let `f` be a function of n arguments and let args=(arg<sub>1</sub>... arg<sub>n</sub>), a list of the `n` S-expressions being used as arguments. Then

`evalquote[fn; args] = f[arg`<sub>1</sub>`... arg`<sub>n</sub>`]`

<a name="page13">page 13</a>

if either side of the equation is defined at all.

Example

|                 |                                  |
| --------------- | -------------------------------- |
| f               | &lambda;[[x; y];cons[car[x]; y]] |
| fn              | (LAMBDA (X Y) (CONS (CAR X) Y))  |
| arg<sub>1</sub> | (A B)                            |
| arg<sub>2</sub> | (C D)                            |
| args            | ((A B) (C D))                    |

`evalquote[(LAMBDA (X Y) (CONS (CAR X) Y)); ((A B) (C D))] =`
&lambda;`[[x;y];cons[car[x];y]][(A B);(C D)] =`
`(A C D)`

`evalquote` is defined by using two main functions, called `eval` and `apply`. `apply` handles a function and its arguments, while `eval` handles forms. Each of these functions also has another argument that is used as an association list for storing the values of bound variables and function names.

*note here that the environment -- the combination of the object list and the pushdown list -- is said to be an assoc list, where, importantly, it isn't. Of course, for the simplest possible Lisp, it would be -- But (to my surprise) Lisp 1.5 is nothing like the simplest possible Lisp.*

```mexpr
evalquote[fn; x] = apply[fn; x; NIL]
```

where
```mexpr
apply[fn; x; a] =
        [atom[fn] -> [eq[fn; CAR] -> caar[x]
                eq[fn; CDR] -> cdar[x];
                eq[fn; CONS] -> cons[car[x]; cadr[x]];
                eq[fn; ATOM] -> atom[car[x]];
                eq[fn; EQ] -> eq[car[x]; cadr[x]];
                T -> apply[eval[fn; a]; x; a]]
        eq[car[fn]; LAMBDA] -> eval[caddr[fn]; pairlis[cadr[fn]; x; a]];
        eq[car[fn]; LABEL] -> apply[caddr [fn]; x; cons[cons[cadr [fn];
                                                      caddr[fn]]; a]]]

eval[e;a] = [atom[e] -> cdr[assoc[e;a]];
        atom[car[e]] -> [eq[car[e]; QUOTE] -> cadr[e];
                eq[car[e]; COND] -> evcon[cdr[e]; a];
                T -> apply[car[e]; evlis[cdr[el; a]; a]];
        T -> apply[car[e]; evlis [cdr[e]; a]; a]]
```

`pairlis` and `assoc` have been previously defined.

```mexpr
evcon[c; a] = [eval[caar[c]; a] -> eval[cadar[c]; a];
        T -> evcon[cdr [c];a]]
```

and

```mexpr
evlis[m; a] = [null[m] -> NIL;
        T -> cons [eval[car [m];a];evlis[cdr [m];a]]]
```

<a name="page14">page 14</a>

We shall explain a number of points about these definitions.

The first argument for `apply` is a function. If it is an atomic symbol, then there are two possibilities. One is that it is an elementary function: `car`, `cdr`, `cons`, `eq`, or `atom`. In each case, the appropriate function is applied to the argument(s). If it is not one of these, then its meaning has to be looked up in the association list.

If it begins with `LAMBDA`, then the arguments are paired with the bound variables, and the form is given to `eval` to evaluate.

If it begins with `LABEL`, then the function name and definition are added to the as-
sociation list, and the inside function is evaluated by apply. 

The first argument of `eval` is a form. If it is atomic, then it must be a variable, and its value is looked up on the association list.

If `car` of the form is `QUOTE`, then it is a constant, and the value is `cadr` of the form
itself.

If `car` of the form is `COND`, then it is a conditional expression, and `evcon` evaluates
the propositional terms in order, and choses the form following the first true predicate.

In all other cases, the form must be a function followed by its arguments. The arguments are then evaluated, and the function is given to apply.

The LISP Programming System has many added features that have not been described thus far. These will be treated hereafter. At this point, it is worth noting the following points.

1. In the pure theory of LISP, all functions other than the five basic ones need to be defined each time they are to be used. This is unworkable in a practical sense. The LISP programming system has a larger stock of built-in functions known to the interpreter, and provision for adding as many more as the programmer cares to define.
2. The basic functions `car` and `cdr` were said to be undefined for atomic arguments. In the system, they always have a value, although it may not always be meaningful.
Similarly, the basic predicate `eq` always has a value. The effects of these functions
in unusual cases will be understood after reading the chapter on list structures in the
computer.
3. Except for very unusual cases, one never writes `(QUOTE T)` or `(QUOTE F)`,
but T, and F respectively.
4. There is provision in LISP for computing with fixed and floating point numbers. These are introduced as psuedo-atomic symbols.

The reader is warned that the definitions of `apply` and `eval` given above are pedagogical devices and are not the same functions as those built into the LISP programming system. Appendix B contains the computer implemented version of these functions and should be used to decide questions about how things really work.

<a name="page15">page 15</a>

## II. THE LISP INTERPRETER SYSTEM

The following example is a LISP program that defines three functions `union`, `intersection`, and `member`, and then applies these functions to some test cases. The functions `union` and `intersection` are to be applied to "sets," each set being represented by a list of atomic symbols. The functions are defined as follows. Note that they are all recursive, and both union and intersection make use of member.

```
member[a; x] = [null[x] -> F; eq[a; car[x]] -> T;
        T -> member[a; cdr[x]]]

union[x; y] = [null[x] -> y;
        member[car[x];y] -> union[cdr[x]; y]; 
        T -> cons[car[x]; union[cdr[x]; y]]]

intersection[x;y] = [null[x] -> NIL;
    member[car[x]; y] -> cons[car[x]; intersection[cdr[x]; y]];
    T -> intersection[cdr[x]; y]]
```

To define these functions, we use the pseudo-function define. The program looks like
this :

DEFINE ((
(MEMBER (LAMBDA (A X) (COND ((NULL X) F)
( (EQ A (CAR X) ) T) (T (MEMBER A (CDR X))) )))
(UNION (LAMBDA (X Y) (COND ((NULL X) Y) ((MEMBER
(CAR X) Y) (UNION (CDR X) Y)) (T (CONS (CAR X)
(UNION (CDR X) Y))) 1))
(INTERSECTION (LAMBDA (X Y) (COND ((NULL X) NIL)
( (MEMBER (CAR X) Y) (CONS (CAR X) (INTERSECTION
(CDR X) Y))) (T (INTERSECTION (CDR X) Y)) )))
1)
INTERSECTION ((A1 A2 A3) (A1 A3 A5))
UNION ((X Y Z) (U V W X))
This program contains three distinct functions for the LISP interpreter. The first
function is the pseudo-function define. A pseudo-function is a function that is executed
for its effect on the system in core memory, as well as for its value. define causes
these functions to be defined and available within the system. Its value is a list of the
functions defined, in this case (MEMBER UNION INTERSECTION). ,
The value of the second function is (A1 A3). The value of the third function is
(Y Z U V W X). An inspection of the way in which the recursion is carried out will show
why the I1elementsN of the Itset" appear in just this order.
Following are some elementary rules for writing LISP 1.5 programs.

1. A program for execution in LISP consists of a sequence of doublets. The first
list or atomic symbol of each doublet is interpreted as a function. The second is a list

```
of arguments for the function. They are evaluated by evalquote, and the value isprinted.
```

2. There is no particular card format for writing LISP. Columns 1-72 of anynumber
of cards may be used. Card boundaries are ignored. The format of this program, in-
cluding indentation, was chosen merely for ease of reading.
3. A comma is the equivalent of a blank. Any number of blanks and/or commas can
occur at any point in a program except in the middle of an atomic symbol.
4. Do not use the forms (QUOTE T), (QUOTE F), and (QUOTE NIL). Use T, F, and
NIL instead.
5. Atomic symbols should begin with alphabetical characters to distinguish them
from numbers.
6. Dot notation may be used in LISP 1.5. Any number of blanks before or after the
dot will be ignored.
7. Dotted pairs may occur as elements of a list, and lists may occur as elements
of dotted pairs. For example,

```
is a valid S-expression. It could also be written
((A. B). (X. ((C. (E. (F. (G. NIL)))). NIL))) or
((A. B) X (C E F G))
```

8. A form of the type (A B C. D) is an abbreviation for (A. (B. (C. D))). Any
other mixing of commas (spaces) and dots on the same level is an error, e. g. (A. B C).
9. A selection of basic functions is provided with the LISP system. Other functions
may be iytroduced by the programmer. The order in which functions are introduced
is not significant. Any function may make use of any other function.

```
2.1 Variables
A variable is a symbol that is used to represent an argument of a function. Thus one
might write "a + b, where a = 341 and b = 216.11 In this situation no confusion can result
and all will agree that the answer is 557. In order to arrive at this result, it is neces-
sary to substitute the actual numbers for the variables, and then add the two number (on
an adding machine for instance).
One reason why there is no ambiguity in this case is that llall and "bl1 are not accept-
able inputs for an adding machine, and it is therefore obvious that they merely represent
the actual arguments. In LISP, the situation can be much more complicated. An atomic
symbol may be either a variable or an actual argument. To further complicate the sit-
uation, a part of an argument may be a variable when a function inside another function
is evaluated. The intuitive approach is no longer adequate. An understanding of the
formalism in use is necessary to do any effective LISP programming.
Lest the prospective LISP user be discouraged at this point, it should be pointed out
that nothing new is going to be introduced here. This section is intended to reinforce
the discussion of Section I. Everything in this section can be derived from the rule for
```

translating M-expressions into S-expressions, or alternatively everything in this section
can be inferred from the universal function evalquote of Section I.
The formalism for variables in LISP is the Church lambda notation. The part of the
interpreter that binds variables is called apply. When apply encounters a function be-
ginning with LAMBDA, the list of variables is paired with the list of arguments and added
to the front of the a-list. During the evaluation of the function, variables may be encountered.
They are evaluated by looking them up on the a-list. If a variable has been bound several
times, the last or most recent value is used. The part of the interpreter that does this
is called eval. The following example will illustrate this discussion. Suppose the inter-
preter is given the following doublet:

fn: (LAMBDA (X Y) (CONS X Y))
args: (A B)
evalquote will give these arguments to apply. (Look at the universal function of
Section I. )

```
~P~~Y[(LAMBDA (X Y) (CONS X Y)); (A B);NIL]
```

- apply will bind the variables and give the function and a-list to eval.
    eval[(~~N~ X Y); ((X. A) (Y. B))]
    eval will evaluate the variables and give it to cons.
    cons[^;^] = (A. B)
       The actual interpreter skips one step required by the universal function, namely,
    apply[~O~~;(A B);((X. A) (Y. B))].

2.2 Constants
It is sometimes assumed that a constant stands for itself as opposed to a variable
which stands for something else. This is not a very workable concept, since the student
who is learning calculus is taught to represent constants by a, b, c... and variables by
x, y, z.... It seems more reasonable to say that one variable is more nearly constant
than another if it is bound at a higher level and changes value less frequently.
In LISP, a variable remains bound within the scope of the LAMBDA that binds it.
When a variable always has a certain value regardless of the current a-list, it will be
called a constant. This is accomplished by means of the property list^1 (p-list) of the
variable symbol. Every atomic symbol has a p-list. When the p-list contains the in-
dicator APVAL, then the symbol is a constant and the next item on the list is the value.

* eval searches p -lists before a-lists when evaluating variables, thus making it possible
    to set constants.
       Constants can be made by the programmer. To make the variable X always stand
    for (A B C D), use the pseudo-function ~t.
       1. Property lists are discussed in Section VII.

```
An interesting type of constant is one that stands for itself. NIL is an example of
this. It can be evaluated repeatedly and will still be NIL. T, F, NIL, and other constants
cannot be used as variables.
```

```
2.3 Functions
```

When a symbol stands for a function, the situation is similar to that in which a symbol
stands for an argument. When a function is recursive, it must be given a name. This
is done by means of the form LABEL, which pairs the n&me with the function definition
on the a-list. The name is then bound to the function definition, just as a variable is
bound to its value.
In actual practice, LABEL is seldom used. It is usually more convenient to attach
the name to the definition in a uniform manner. This is done by putting on the property
list of the name,the symbolEXPR followed by the function definition. The pseudo-function
define used at the beginning of this section accomplishes this. When apply interprets
a function represented by an atomic symbol, it searches the p-list of the atomic symbol
before searching the current a-list. Thus a define will override a LABEL.
The fact that most functions are constants defined by the programmer, and not vari-
ables that are modified by the program, is not due to any weakness of the system. On the
contrary, it indicates a richness of the system which we do not know how to exploit very
well.

```
2.4 Machine Language Functions
Some functions instead of being defined by S-expressions are coded as closed machine
language subroutines. Such a function will have the indicator SUBR on its property list
followed by a pointer that allows the interpreter to link with the subroutine. There are
three ways in which a subroutine can be present in the system.
1. The subroutine is coded into the LISP system.
```

2. The function is hand-coded by the user in the assembly type language, LAP.
    3. The function is first defined by an S-expression, and then compiled by the LISP
    compiler. Compiled functions run from 10 to 100 times as fast as they do when they
    are interpreted.

```
2.5 Special Forms
Normally, eval evaluates the arguments of a function before applying the function
itself. Thus if =l is given (CONS X Y), it will evaluate X and Y, and then cons them.
But if eval is given (QUOTE X), X should not be evaluated. QUOTE is a special form
that prevents its argument from being evaluated.
A special form differs from a function in two ways. Its arguments are not evaluated
before the special form sees them. COND, for example, has a very special way of
```

evaluating its arguments by using evcon. The second way which special forms differ
from functions is that they may have an indefinite number of arguments. Special forrrls
have indicators on their property lists called FEXPR and FSUBR for LISP -defined forms
and machine language coded forms, respectively.

```
2.6 Programming for the Interpreter
```

The purpose of this section is to help the programmer avoid certain common errors.
Example 1
fn: CAR
args: ((A B))
The value is A. Note that the interpreter expects a list of arguments. The one argu-
ment for car is (A B). The extra pair of parentheses is necessary.
One could write (LAMBDA (X) (CAR X)) instead of just CAR. This is correct but
unnecessary.

```
Example 2
fn: CONS
args: (A (B. C))
The value is cons[^;(^. c)] = (A. (B. C)).
The print program will write this as (A B. C).
```

Example (^3) -
fn: CONS
args: ((CAR (QUOTE (A. B))) (CDR (QUOTE (C. D))))
The value of this computation will be ((CAR (QUOTE (A. B))). (CDR (QUOTE (C. D)))).
This is not what the programmer expected. He expected (CAR (QUOTE (A. B))) to
evaluate to A, and expected (A. D) as the value of cons.

* The interpreter expects a ---- list of arguments. ------- It does not expect a list of expressions
-- that will evaluate to the arguments. Tworcorrect ways of writing this function are listed
below. The first one makes the car and cdr part of a function specified by a LAMBDA.
The second one uses quoted arguments and gets them evaluated by eval with a null a-list.
fn: (LAMBDA (X Y) (CONS (CAR X) (CDR Y)))
args: ((A. B) (C. D))
fn: EVAL
args: ((CONS (CAR (QUOTE (A. B))) (CDR (QUOTE (C. D)))) NIL)
The value of both of these is (A. D).

111. ## EXTENSION OF THE LISP LANGUAGE

```
Section I of this manual presented a purely formal mathematical system that we
shall call pure LISP. The elements of this formal system are the following.
```

1. A set of symbols called S-expressions.
2. A functional notation called M-expressions.
3. A formal mapping of M-expressions into S-expressions.
4. A universal function (written ,IS an M-expression) for interpreting the application
of any function written as an S-expression to its arguments.
Section II introduced the LISP Programming System. The basis of the LISP Pro-
gramming System is the interpreter, or evalquote and its components.. A LISP program
in fact consists of pairs of arguments for evalquote which are interpreted in sequence.
In this section we shall introduce a number of extensions of elementary LISP. These
extensions of elementary LISP are of two sorts. The first includes propositional con-
nectives and functions with functions as arguments, and they are also of a mathematical
nature; the second is peculiar to the LISP Programming System on the IBM 7090 computer.
In all cases, additions to the LISP Programming System are made to conform to the
functional syntax of LISP even though they are not functions. For example, the command
to print an S-expression on the output tape is called print. Syntactically, print is a
function of one argument. It may be used in composition with other functions, and will

be evaluated in the usual manner, with the inside of the composition being evaluated first.
Its effect is to print its argument on the output tape (or on-line). It is a function only in
the trivial sense that its value happens to be its argument, thus making it an identity
function.
Commands to effect an action such as the operation of input-output, or the defining
functions define and cset discussed in Chapter 11, will be called pseudo-functions. It
is characteristic of the LISP system that all functions including psuedo-functions must
have values. In some cases the value is trivial and may be ignored.
This Chapter is concerned with several extensions of the LISP language that are in
the system.

```
3.1 Functional Arguments
Mathematically, it is possible to have functions as arguments of other functions.
For example, in arithmetic one could define a function operate [op;a;b], where op is a
functional argument that specifies which arithmetic operation is to be performed on a
and b. Thus
operate[+;3;4]=7 and
operate[x;3;4]= 12
```

In LISP, functional arguments are extremely useful. A very important function with
a functional argument is maplist. Its M-expression definition is

maplist[x;fn]=[null[x]-NIL;
T-cons [fn[x];maplis t [cdr [x];fn]]]
An examination of the universal function evalquote will show that the interpreter can
handle maplist and other functions written in this manner without any further addition.
The functional argument is, of course, a function translated into an S-expression. It is
bound to the variable fn and is then used whenever fn is mentioned as a function. The
S-expression for maplist itself is as follows:
(MAPLIST (LAMBDA (X FN) (COND ((NULL X) NIL)
(T (CONS (FN X) (MAPLIST (CDR X) FN))) )))

```
Now suppose we wish to define a function that takes a list and changes it by cons-ing
an X onto every item of the list so that, for example,
change[(^ B (C D))]=((A. X) (B. X) ((C. D). X))
```

```
Using maplist, we define change by
change[a]=maplist[a;~[[j];cons[car [j];~]]]
```

```
This is not a valid M-expression as defined syntactically in section 1.5 because a
function appears where a form is expected, This can be corrected by modifying the rule
defining an argument so as to include functional arguments:
< argument > :: = <form > 1 c function >
```

```
We also need a special rule to translate functional arguments into S-expression. If
```

- fn is a function used as an argument, then it is translated into (FUNCTION fn*).

```
Example
(CHANGE (LAMBDA (A) (MAPLIST A (FUNCTION
(LAMBDA (J) (CONS (CAR J) (QUOTE X))) )))
```

```
An examination of evalquote shows that QUOTE will work instead of FUNCTION,
provided that there are no free variables present. An explanation of how the interpreter
processes the atomic symbol FUNCTION is given in the Appendix B.
3.2 Logical Connectives
```

```
The logical or Boolian connectives are usually considered as primitive operators.
However, in LISP, they can be defined by using conditional expressions:
```

```
In the System, not is a predicate of one argument. However, g& and or are pred-
icates of an indefinite number of arguments, and therefore are special forms. In
```

```
writing M-expressions it is often convenient to use infix notation and write expressions
such as aV bVc for or[a;b;c]. In S-expressions, one must, of course, use prefix no-
tation and write (OR A B C).
The order in which the arguments of and and or are given may be of some significance
in the case in which some of the arguments may not be well defined. The definitions of
these predicated given above show that the value may be defined even if all of the argu-
ments are not.
@ evaluates its arguments from left to right. If one of them is found that is false,
then the value of the is false and no further arguments are evaluated. If the argu-
ments are all evaluated and found to be true, then the value is true.
```

- or evaluates its arguments from left to right. If one of them is true, then the value
of the or is true and no further arguments are evaluated. If the arguments are all eval-
uated and found to be false, then the value is false.
3.3 Predicates and Truth in LISP

Although the rule for translating M-expressions into S-expressions states that T is
(QUOTE T), it was stated that in the system one must always write T instead. Similarly,
one must write F rather than (QUOTE F). The programmer may either accept this
rule blindly or understand the following Humpty-Dumpty semantics.
In the LISP programming system there are two atomic symbols that represent truth
and falsity respectively. These two atomic symbols are *T* and NIL. It is these sym-
bols rather than T and F that are the actual value of all predicates in the system. This
is mainly a coding convenience.
The atomic symbols T and F have APVAL1s whose values are *T* and NIL, re-
spectively. The symbols T and F for constant predicates will work because:

```
The forms (QUOTE *T*) and (QUOTE NIL) will also work because
```

```
*T* and NIL both have APVAL.'s that point to themselves. Thus *T* and NIL are
also acceptable because
```

```
But
QUOTE QUOTE F) ;NIL]= F
which is wrong and this is why (QUOTE F) will not work. Note that
```

which is wrong but will work for a different reason that will be explained in the
paragraph after next.
There is no formal distinction between a function and a predicate in LISP. A pred-
icate can be defined as a function whose value is either *T* or NIL. This is true of all
predicates in the System.
One may use a form that is not a predicate in a location in which a predicate is called
for, such as in the p position of a conditional expression, or as an argument of a logical
predicate. Semantically, any S-expression that is not NIL will be regarded as truth in
such a case. One consequence of this is that the predicates null and not are identical.
Another consequence is that (QUOTE T) or (QUOTE X) is equivalent to T as a constant
predicate.
The predicate eq - has the following behavior.

1. If its arguments are different, the value of 3 is NIL.
2. If its arguments are both the same atomic symbol, its value is *T*.
3. If its arguments are both the same, but are not atomic, then the value is *T* or
NIL depending upon whether the arguments are identical in their representation in core
memory.
4. The value of - eq is always *T* or NIL. It is never undefined even if its arguments
are bad.

```
ARITHMETIC LISP
```

```
Lisp 1.5 has provision far handling fixed-point and floating-point numbers and log-
ical words. There are functions and predicates in the system for performing arithmetic
and logical operations and making basic tests.
4.1 Reading and Printing Numbers
```

```
Numbers are stored in the computer as though they were a special type of atomic
symbol. This is discussed more thoroughly in section 7.3. The following points should
be noted :
```

1. Numbers may occur in S-expressions as though they were atomic symbols.
2. Numbers are constants that evaluate to themselves. They do not need to be quoted.
3. Numbers should not be used as variables or function names.
a. Floating-Point Numbers

```
The rules for punching these for the read program are:
```

1. A decimal point must be included but not as the first or last character.
2. A plus sign or minus sign may precede the number. The plus sign is not required.
3. Exponent indication is optional. The letter E followed by the exponent to the
base 10 is written directly after the number. The exponent consists of one or two digits
that may be preceded by a plus or minus sign.
4. Absolute values must lie between 2' 28 and 2-I 28 and
5. Significance is limited to 8 decimal digits.
6. Any possible ambiguity between the decimal point and the point used in dot no-
tation may be eliminated by putting spaces before and after the LISP dot. This is not
required when there is no ambiguity.
Following are examples of correct floating-point numbers. These are all different
forms for the same number, and will have the same effect when read in.

```
The forms .6E+2 and 60. are incorrect because the decimal point is the first or last
character respectively.
b. Fixed-Point Numbers
These are written as integers with an optional sign.
Examples
-1 7
327 19
```

```
c. Octal Numbers or Logical Words
The correct form consists of
1. A sign (optional).
```

2. Up to 12 digits (0 through 7).
    3. The letter Q.
4. An optional scale factor. The scale factor is a decimal integer, no sign allowed.

```
Example
```

```
The effect of the read program on octal numbers is as follows.
```

1. The number is placed in the accumulator three bits per octal digit with zeros
added to the left-hand side to make twelve digits. The rightmost digit is placed in bits
33-35; the twelfth digit is placed in bits P, 1, and 2.
2. The accumulator is shifted left three bits (one octal digit) times the scale factor.
Thus the scale factor is an exponent to the base 8.
3. If there is a negative sign, it is OR-ed into the P bit. The number is then stored
as a logical word.
The examples a through e above will be converted to the following octal words.
Note that because the sign is OR-ed with the 36th numerical bit c, d, and e are equiv-
alent.

4.2 Arithmetic Functions and Predicates
We shall now list all of the arithmetic functions in the System. They must be given
numbers as arguments; otherwise an error condition will result. The arguments may
be any type of number. A function may be given some fixed-point arguments and some
floating-point arguments at the same time.
If all of the arguments for a function are fixed-point numbers, then the value will
be a fixed-point number. If at least one argument is a floating-point number, then the
value of the function will be a floating-point number.
plus[xl;. -.. ;xn] is a function of any number of arguments whose value is the alge-
braic sum of the arguments.

difference[^;^] has for its value the algebraic difference of its arguments.

* minus[x] has for its value -x.
    times[xl;.. .;xn] is a function of any number of arguments, whose value is the product
    (with correct sign) of its arguments.
    addl[x] has xtl for its value. The value is fixed-point or floating-point, depending
    on the argument.
* subl[x] has x-1 for its value. The value is fixed-point or floating-point, depending
on the argument.
* max[xl;... ;xn] chooses the largest of its arguments for its value. Note that
max[3;2.0] = 3.0.
* min[xl ;... ;xn] chooses the smallest of its arguments for its value.
* recip[x] computes l/x. The reciprocal of any fixed point number is defined as zero.
quo ti en![^;^] computes the quotient of its arguments. For fixed-point arguments,
the value is the number theoretic quotient. A divide check or floating-point trap will
result in a LISP error.
remainder[^;^] computes the number theoretic remainder for fixed-point numbers,
and the floating-point residue for floating-point arguments.
divide[x;y] = cons[qu~tient[x;~]; con~[remainder[x;~];~~~]]
* e~pt[x;~] = xY. If both x and y are fixed-point numbers, this is computed by iter-
ative multiplication. Otherwise the power is computed by using logarithms. The first
argument cannot be negative.
We shall now list all of the arithmetic predicates in the System. They may have
fixed-point and floating-point arguments mixed freely. The value of a predicate is *T*
or NIL.
les~~[x;~] - is true if x c y, and false otherwise.
greaterp[x;y] is true if x > y.
zerop[x] is true if x=O, or if 1 x IC 3 X
* onep[x] is true if^1 x-^1 ( <^3 X lo-'.
minusp[x] is true if x is / negative.
"-0 is negative.
numberp[x] is true if x is a number (fixed-point or floating-point).
* fixp[x] is true only if x is a fixed-point number. If x is not a number at all, an
error will result.
floatp[x] - is similar to fixp[x] but for floating-point numbers.
equal[x;y] works on any arguments including S-expressions incorporating numbers
inside them. Its value is true if the arguments are identical. Floating-point numbers
must satisfy I x-~ 1 < 3 X 10 -6.
The logical functions operate on 36-bit words. The only acceptable arguments are
fixed-point numbers. These may be read in as octal or decimal integers, or they may
be the result of a previous computation.
logor[xl ;... ;x n ] performs a logical OR on its arguments.

logand[xl ;... ;xn] performs a logical AND on its arguments.
logxor[xl ;... ;xn] performs an exclusive OR
(OxO=O, 1~0=0~1=1,1~1=0).
leftshift[x;n] = x x 2". The first argument is shifted left by the number of bits spec-
ified by the second argument. If the second argument is negative, the first argument
will be shifted right.

4.3 Programming with Arithmetic

The arithmetic functions may be used recursively, just as other functions available
to the interpreter. As an example, we define factorial as it was given in Section I.

```
n! = [n = 0 -1; T-n.(n-l)! ]
DEFINE ((
(FACTORIAL (LAMBDA (N) (COND
((ZEROP N) 1)
(T (TIMES N (FACTORIAL (SUB1 N)))) )))
```

### 4.4 The Array Feature

Provision is made in LISP 1.5 for allocating blocks of storage for data. The data
may consist of numbers, atomic symbols or other S-expressions.
The pseudo-function array reserves space for arrays, and turns the name of an
array into a function that can be used to fill the array or locate any element of it.
Arrays may have up to three indices. Each element (uniquely specified by its co-
ordinates) contains a pointer to an S-expression (see Section VII).
array is a function of one argument which is a list of arrays to be declared. Each
item is a list containing the name of an array, its dimensions, and the word LIST. (Non-
list arrays are reserved for future development~ of the LISP system.)
For example, to make an array called alpha of size 7 X 10, and one called beta - of
size 3 X 4 X 5 one should execute:
array[((A~p~A (7 10) LIST) (BETA (3 4 5) LIST))]
After this has been executed, both arrays exist and their elements are all set to
NIL. Indices range from 0 to n-I.
alpha and - beta are now functions that can be used to set or locate elements of these
respective arrays.
TO set alphai to x, execute -
s j

```
To set alpha3, to (A B C) execute -
alpha[s~~; (A B c); 3;4]
```

Inside a function or program X might be bound to (A B C), I bound to 3, and J bound
to 4, in which case the setting can be done by evaluating -

```
(ALPHA (QUOTE SET) X I J)
```

To locate an element of an array, use the array name as a function with the coordi-
nates as axes. Thus any time after executing the previous example -

```
alpha[3;4] = (A B C)
```

Arrays use marginal indexing for maximum speed. For most efficient results,
specify dimensions in increasing order. ~eta[3;4;5] is better than beta[5;3;4].
Storage for arrays is located in an area of memory called binary program space.

## V. THE PROGRAM FEATURE

The LISP 1 .5 program feature allows the user to write an Algol-like program con-
taining LISP statements to be executed.
An example of the program feature is the function length, which examines a list and
decides how many elements there are in the top level of the list. The value of length is
an integer.
Length is a function of one argurnentL. The program uses two program variables

- u and y, which can be regarded as storage locations whose contents are to be changed
    by the program. In English the program is written:
       This is a function of one argument 1.
          It is a program with two program variables 2 and 1.
    Store 0 in
    Store the argument 1 in 2.
    A If g contains NIL, then the program is finished,
    and the value is whatever is now in 2.
    Store in u, cdr of what is now in g.
    Store in 1, one more than what is now in
    Go to A.

```
We now write this program as an M-expression, with a few new notations. This
corresponds line for line with the program written above.
```

```
Rewriting this as an S-expression, we get the following program.
DEFINE ((
(LENGTH (LAMBDA (L)
(PROG (U V)
(SETQ V 0)
(SETQ U L)
(COND ((NULL U) (RETURN V)))
(SETQ U (CDR U))
(SETQ V (ADD1 V))
(GO A) 1)) 1)
LENGTH ((A B C D))
```

```
LENGTH (((X Y) A CAR (N B) (X Y 2)))
```

The last two lines are test cases. Their values are four and five, respectively.
The program form has the structure -
(PROG, list of program variables, sequence of statements and atomic' symbols.. .)
An atomic symbol in the list is the location marker for the statement that follows. In
the above example, A is a location marker for the statement beginning with COND.
The first list after the symbol PROG is a list of program variables. If there are
none, then this should be written NIL or (). Program variables are treated much like
bound variables, but they are not bound by LAMBDA. The value of each program vari-
able is NIL until it has been set to something else.
To set a program variable, use the form SET. To set variable PI to 3.14 write
(SET (QUOTE PI) 3.14). SETQ is like SET except that it quotes its first argument. Thus
(SETQ PI 3.14). SETQ is usually more convenient. SET and SETQ can change variables
that are on the a-list from higher level functions. The value of SET or SETQ is the value
of its second argument.
Statements are normally executed in sequence. Executing a statement means eval-
uating it with the current a-list and ignoring its value. Program statements are often
executed for their effect rather than their value.
GO is a form used to cause a transfer. (GO A) will cause the program to continue
at statement A. The form GO can be used only as a statement on the top level of a
PROG or immediately inside a COND which is on the top level of a PROG.
Conditional expressions as program statements have a useful peculiarity. If none
of the propositions are true, instead of an error indication which would otherwise occur,
the program continues with the next statement. This is true only for conditional expres-
sions that are on the top level of a PROG.
RETURN is the normal end of a program. The argument of RETURN is evaluated,
and this is the value of the program. No further statements are executed.
If a program runs out of statements, it returns with the value NIL.
The program feature, like other LISP functions, can be used recursively. The
function rev, which reverses a list and all its sublists is an example of this.
rev[x] = ~rog[[~;z];
A [null[x]-return[y];
z:= car[x];
[atom[z]- go[^]];
z:= rev[z];
B y: = cons[^;^];
x:= cdr[x];
goiA11
The function rev will reverse a list on all levels so that
rev[(A ((B C) D))] = ((D (C B)) A)

```
VI. RUNNING THE LISP SYSTEM
```

```
6.1 Preparing a Card Deck
```

A LISP program consists of several sections called packets. Each packet starts
with an Overlord direction card, followed by a set of doublets for evalquote, and ending
with the word STOP.
Overlord direction cards control tape movement, restoration of the system memory
between packets, and core dumps. A complete listing of Overlord directions is given
in Appendix E.
Overlord direction cards are punched in Share symbolic format; the direction starts
in column 8, and the comments field starts in column 16. Some Overlord cards will
now be described.
TEST: Subsequent doublets are read in until the word STOP is encountered, or until
a read error occurs. The doublets are then evaluated and each doublet with its value
is written on the output tape. If an error occurs, a diagnostic will be written and the
program will continue with the next doublet. When all doublets have been evaluated,
control is returned to Overlord which restores the core memory to what it was before
the TEST by reading in a core memory image from the temporary tape.

* SET: The doublets are read and interpreted in the same manner as a TEST. However,
    when all doublets have been evaluated, the core memory is not restored. Instead, the
    core memory is written out onto the temporary tape (overwriting the previous core
    image), and becomes the base memory for all remaining packets. Definitions and
    other memory changes made during a SET will affect all remaining packets.
       Several SET'S during a LISP run will set on top of each other.
       A SET will not set if it contains an error. The memory will be restored from the
    temporary tape instead.
    SETSET: This direction is like SET, except that it will set even if there is an error.
* FIN: End of LISP run.
    The reading of doublets is normally terminated by the word STOP. If parentheses
    do not count out, STOP will appear to be inside an S-expression and will not be recog-
    nized as such. To prevent reading from continuing indefinitely, each packet should end
    with STOP followed by a large number of right parentheses. An unpaired right paren-
    thesis will cause a read error and terminate reading.
       A complete card deck for a LISP run might consist of:
          a: LISP loader
    b: ID card (Optional)
    c: Several Packets
    .d: FIN card
    e: Two blank cards to prevent card reader from hanging up
    The ID card may have any information desired by the computation center. It will be

printed at the head of the output.

6.2 Tracing
Tracing is a technique used to debug recursive functions. The tracer prints the
name of a function and its arguments when it is entered, and its value when it is finished.
By tracing certain critical subfunctions, the user can often locate a fault in a large pro-
gram.
Tracing is controlled by the pseudo-function trace, whose argument is a list of func-
tions to be traced. After trace has been executed, tracing will occur whenever these
functions are entered.
When tracing of certain functions is no longer desrred, it can be terminated by the
pseudo-function untrace whose argument is a list of functions that are no longer to be
traced.

6.3 Error Diagnostics
When an error occurs in a LISP 1 .5 program, 'a diagnostic giving the nature of the
error is printed out. The diagnostic gives the type of error, and the contents of certain
registers at that time. In some cases a back-trace is also printed. This is a list of
functions that were entered recursively but not completed at the time of the error.
In most casee, the program continues with the next doublet. However, certain er-
rors are fatal; in this case control is given to the monitor Overlord. Errors during
Overlord also continue with Overlord.
A complete list of error diagnostics is given below, with comments.

Interpreter Errors:
A 1 APPLIED FUNCTION CALLED ERROR
The function error will cause an error diagnostic to occur. The argument
(if any) of error will be printed. Error is of some use as a debugging aid.
A 2 FUNCTION OBJECT HAS NO DEFINITION- APPLY
This occurs when an atomic symbol, given as the first argument of apply,
does not have a definition either on its property list or on the a-list of apply.
A 3 CONDITIONAL UNSATISFIED - EVCON
None of the propostiions following COND are true.
A 4 SETQ GIVEN ON NONEXISTEYT PROGRAM VARIABLE - APPLY
A 5 SET GIVEN ON NONEXISTENT PROGRAM VARIABLE - APPLY
A 6 GO REFERS TO A POINT NOT LABELLED - INTER
A 7 TOO MANY ARGUMENTS - SPREAD
The interpreter can handle only 20 arguments for a function.
A 8 UNBOUND VARIABLE - EVAL
The atomic symbol in question is not bound on the a-list for eval nor does it
have an APVAL.

A 9 FUNCTION OBJECT HAS NO DEFINITION - EVAL
Eva1 expects the first object on a list to be evaluated to be an atomic symbol.
A 8 and A 9 frequently occur when a parenthesis miscount causes the wrong
phrase to be evaluated.

Compiler Errors :
C 1 CONDITION NOT SATISFIED IN COMPILED FUNCTION

Character -Handling Functions :
CH 1 TOO MANY CHARACTERS IN PRINT NAME - PACK
CH 2 FLOATING POINT NUMBER OUT OF RANGE - NUMOB
CH 3 TAPE READING ERROR - ADVANCE
The character-handling functions are described in Appendix F.

Miscellaneous Errors :
F 1 CONS COUNTER TRAP
The cons counter is described in section 6.4.
F 2 FIRST ARGUMENT LIST TOO SHORT - PAIR
F 3 SECOND ARGUMENT LIST TOO SHORT - PAIR
Pair is used by the interpreter to bind variables to arguments. If a function
is given the wrong number of arguments, these errors may occur.

F 5 STR TRAP - CONTINUING WITH NEXT EVALQUOTE
When the instruction STR is executed, this error occurs.
If sense switch 6 is down when an STR is executed,
control goes to Overlord instead.
G 1 FLOATING POINT TRAP OR DIVIDE CHECK
G 2 OUT OF PUSH - DOWN LIST
The push-down list is the memory device that keeps track of the level of re-
cursion. When recursion becomes very deep, this error will occur. Non-
terminating recursion will cause this error.

Garbage Collector Errors:
GC 1 FATAL ERROR - RECLAIMER
This error only occurs when the system is so choked that it cannot be restored.
Control goes to Overlord.
GC 2 NOT ENOUGH WORDS COLLECTED - RECLAIMER
This error restores free storage as best it can and continues with the next
doublet.

Arithmetic Errors:
I1 NOT ENOUGH ROOM FOR ARRAY
Arrays are stored in binary program space.

```
I2 FIRST ARGUMENT NEGATIVE - EXPT
I3 BAD ARGUMENT - NUMVAL
I4 BAD ARGUMENT - FIXVAL
Errors I 3 and I 4 will occur when numerical functions are given wrong argu-
ments.
```

Lap Errors:
L 1 UNABLE TO DETERMINE ORIGIN
L 2 OUT OF BINARY PROGRAM SPACE
L 3 UNDEFINED SYMBOL
L 4 FIELD CONTAINED SUB - SUBFIELDS
Overlord Errors:
0 1 ERROR IN SIZE CARD - OVERLORD
0 2 INVALID TAPE DESIGNATION - OVERLORD
0 3 NO SIZE CARD - OVERLORD
0 4 BAD DUMP ARGUMENTS - OVERLORD
0 5 BAD INPUT BUT GOING ON ANYHOW - OVERLORD
0 7 OVERLAPPING PARAMETERS - SETUP

Input -Output Errors:
P 1 PRINl ASKED TO PRINT NON-OBJECT
R 1 FIRST OBJECT ON INPUT LIST IS ILLEGAL - RDA
This error occurs when the read program encounters a character such'as
I1)l1 or ." out of context. This occurs frequently when there is a parenthesis
miscount.
R 2 CONTEXT ERROR WITH DOT NOTATION - RDA
R 3 ILLEGAL CHARACTER - RDA
R 4 END OF FILE ON READ-IN - RDA
R 5 PRINT NAME TOO LONG - RDA
Print names may contain up to 30 BCD characters.
R 6 NUMBER TOO LARGE IN CONVERSION - RDA
6.4 The Cons Counter and Errorset
The cons counter is a useful device for breaking out of program loops. It automat-
ically causes a trap when a certain number of conses have been performed.
The counter is turned on by executing count [n], where n is an integer. If n conses
are performed before the counter is turned off, a trap will occur and an error diagnos-
tic will be given. The counter is turned off by uncount [NIL]. The counter is turned
on and reset each time count [n] is executed. The counter can be turned on so as to
continue counting from the state it was in when last turned off by executing count [NIL].
The function speak [NIL] gives the number of conses counted since the counter was
last reset.

errorset is a function available to the interpreter and compiler for making a graceful
retreat from an error condition encountered during a subroutine.
errorset[e;n;m;a] is a pseudo-function with four arguments. If no error occurs, then
errorset can be defined by
errorset[e;n;m;a] = list[eval[e;a]]

* n is the number of conses permitted before a cons trap will occur. The cons counter
is always on during an errorset; however, when leaving the errorset the counter is al-
ways restored to the value it had before entering the errorset. The on-off status of the
counter will also be restored.
When an error occurs inside an errorset, the error diagnostic will occur if m is
set true, but will not be printed if m is NIL.
If an error occurs inside of an errorset, then the value of errorset is NIL. If vari-
ables bound outside of the errorset have not been altered by using cset or set, and if no
damage has been done by pseudo-functions, it may be possible to continue computation
in a different direction when one path results in an error.

## VII. LIST STRUCTURES

In other sections of this manual, lists have been discussed by using the LISP input-output language. In this section, we discuss the representation of lists inside the computer, the nature of property lists of atomic symbols, representation of numbers, and the garbage collector.

### 7.1 Representation of List Structure

Lists are not stored in the computer as sequences of BCD characters, but as structural forms built out of computer words as parts of trees. In representing list structure, a computer word will be depicted as a rectangle divided into two sections, the address and decrement.

add. I dec.

Each of these is a 15-bit field of the word.
We define a pointer to a computer word as the 15-bit quantity that is the complement
of the address of the word. Thus a pointer to location 77777 would be 00001.
Suppose the decrement of word x contains a pointer to word y. We diagram this as

We can now give a rule for representing S-expressions in the computer. The repre-
sentation of atomic symbols will be explained in section 7.3. When a computer word
contains a pointer to an atomic symbol in the address or decrement, the atomic symbol
will be written there as

1:

The rule for representing non-atomic S-expressions is to start with a word containing
a pointer to car of the expression in the address, and a pointer to c&r of the expression
in the decrement.
Following are some diagrammed S-expressions, shown as they would appear in the
computer. It is convenient to indicate NIL by -- - -- - instead of -- -- -F].

It is possible for lists to make use of common subexpressions. ((M. N) X (M. N))
could also be represented as


Circular lists are ordinarily not permitted. They may not be read in; however, they
can occur inside the computer as the result of computations involving certain functions.
Their printed representation is infinite in length. For example, the structure


```
will print as (A B C A B C A. ..
That which follows is an actual assembly listing of the S-expression (A (B (C. A))
(C. A)) which is diagrammed:
```

```
The atoms Aj B, and C are represented by pointers to locations 12327, 12330, and
12331, respectively. NIL is represented by a pointer to location 00000.
```

```
The advantages of list structures for the storage of symbolic expressions are:
```

1. The size and even the number of expressions with which the program will have
to deal cannot be predicted in advance. Therefore, it is difficult to arrange blocks of

<a name=="page 37">page 37</a>

storage of fixed length to contain them.

2. Registers can be put back on the free.-storage list when they are no longer
needed. Even one register returned to the list is of value, but if expressions are stored
linearly, it is difficult to make use of blocks of registers of odd sizes that may become
available.
3. An expression that occurs as a subexpression of several expressions need be
represented in storage only once,

### 7.2 Construction of List Structure

The following simple example has been included to illustrate the exact construction
of list structures. Two types of list structures are shown, and a function for deriving
one from the other is given in LISP.

We assume that we have a list of the form
n, = ((A B C) (D E F),... , (X Y z)),

which is represented as

and that we wish to construct a list of the form

1, = ((A (B c)) (D (E F)),... , (x (Y z)))

which is represented as

We consider the typical substructure, (A (B C)) of the second list Q2. This may be
constructed from A, B, and C by the operation
cons [~;cons[cons [B; CO~S[C;NIL]]; NIL]]
or, using the l& function, we can write the same thing as

In any case, given a list, x, of three atomic symbols,
x = (A B C),
the arguments A, B, and C to be used in the previous construction are found from
A = car[x]
B = cadr[x]
C = caddr[x]
The first step in obtaining P2 from P1 is to define a function, m, of three arguments
which creates (X (Y Z)) from a list of the form (X Y Z).
grp[x] = list[car[x];list[cadr[x];caddr[x]]]
Then - grp is used on the list P1, under the assumption that P1 is of the form given.
For this purpose, a new function, mltgrp, is defined as

##### mltgrp[P] = [null[P] - NIL;T - cons[grp[car[P]];mltgrp[cdr[~]]]]

So rnltgrp applied to the list P1 takes each threesome, (X Y Z), in turn and applies - grp
to it to put it in the new form, (X (Y Z)) until the list P1 has been exhausted and the new
list P2 achieved.

### 7.3 Property Lists

In other sections, atomic symbols have been considered only as pointers. In this
section the property lists of atomic symbols that begin at the appointed locations are
described.

Every atomic symbol has a property list. When an atomic symbol is read in for
the first time, a property list is created for it.

A property list is characterized by having the special constant 777778 (i. e., minus 1)
as the first element of the list. The rest of the list contains various properties of the
atomic symbol. Each property is preceded by an atomic symbol which is called its
indicator. Some of the indicators are:

| Indicator | Description                                                  |
| --------- | ------------------------------------------------------------ |
| PNAME     | the BCD print name of the atomic symbol for input-output use. |
| EXPR      | S-expression defining a function whose name is the atomic symbol on whose property list the EXPR appears. |
| SUBR      | Function defined by a machine language subroutine.           |
| APVAL     | Permanent value for the atomic symbol considered as a variable. |

The atomic symbol NIL has two things on its property list - its PNAME, and an
APVAL that gives it a value of NIL. Its property list looks like this:
```

```
-1 I APVAL I PNAME~
A
1
I I
```

```
NIL???
```

-I,, -NIL
-APVAL, , -*-I
-*- 1, , *-2
0
,
-PNAME , , -*- 1
-*- 1
-*- 1
BCD NIL???
The print name (PNAME) is depressed two levels to allow for names of more than
six BCD characters. The last word of the print name is filled out with the illegal BCD
character 778 (7). The print name of EXAMPLE would look like this:

    - - - PNAME I I
    I

```
+
EXAMPL
```

The property list of a machine-language function contains the indicator SUBR
followed by a TXL instruction giving the location of the subroutine and the number of
arguments. For example

TXL 37721,, 2 1

```
The indicator EXPR points to an S-expression defining a function. The function define
puts EXPR1s on property lists. After defining ff, its property list would look like this

-1 I

LAMBDA I

The function get[x;i] can be used to find a property of x whose indicator is i. The
value of get[X; G] would be (LAMBDA (X) (COW...
A property with its indicator can be removed by remprop[x;i].
The function deflist[x;i] can be used to put any indicator on a property list. The
first argument is a list of pairs as for define, the second argument is the indicator to
be used. define[x] = deflist[x;Ex~R].
An indicator on a property list that does not have a property following it is called
a flag. For example, the flag TRACE is a signal that a function is to be traced. Flags
can be put on property lists and removed by using the pseudo-functions - flag and rernflag.
Numbers are represented by a type of atomic symbol in LISP. This word consists
of a word with -1 in the address, certain bits in the tag which specify that it is a number
and what type it is, and a pointer to the number itself in the decrement of this word.
Unlike atomic symbols, numbers are not stored uniquely.
For example, the decimal number 15 is represented as follows:

### 7.4 List Structure Operators

The theory of recursive functions developed in Section I will be referred to as elementary LISP. Although this language is universal in terms of computable functions of symbolic expressions, it is not convenient as a programming system without additional tools to increase its power.

In particular, elementary LISP has no ability to modify list structure. The only basic function that affects list structure is `cons`, and this does not change existing lists, but creates new lists. Functions written in pure LISP such as `subst` do not actually modify their arguments, but make the modifications while copying the original.

LISP is made general in terms of list structure by means of the basic list operators
`rplaca` and `rplacd`. These operators can be used to replace the address or decrement
or any word in a list. They are used for their effect, as well as for their value, and
are called pseudo-functions.

`rplaca[x;y]` replaces the address of `x` with `y`. Its value is `x`, but `x` is something different from what it was before. In terms of value, rplaca can be described by the equation
rpla~a[x;~] = c~ns[~;cdr[x]]
But the effect is quite different: there is no cons involved and a new word is not created.

`rplacd[x;y]` replaces the decrement of `x` with `y`.

These operators must be used with caution. They can permanently alter existing
definitions and other basic memory. They can be used to create circular lists, which
can cause infinite printing, and look infinite to functions that search, such as `equal` and
`subst`.

As an example, consider the function mltgrp of section 7.2. This is a list-altering function that alters a copy of its argument. The subfunction - grp rearranges a subgroup

The original function does this by creating new list structures, and uses four cons's. Because there are only three words in the original, at least one cons is necessary, but


- grp can be rewritten by using rplaca and rplacd.
    The modification is

The new word is created by cons[cadr[x];cddr[x]]. A pointer to it is provided by rplaca[cdr[x];cons[cadr[x];cddr[x]]].

The other modification is to break the pointer from the second to the third word.
This is done by rplacd[cdr[x];~l~].
pgrp - is now defined as
pgrp[x] = rplacd[rplaca[cdr[x];cons[cadr[x];cddr[x]]];~l~]
The function - pgrp is used entirely for its effect. Its value is not useful, being the
substructure ((B C)). Therefore a new mltgrp is needed that executes pgrp - and ignores
its value. Since the top level is not to be copied, mltprp should do no consing.
pmltgrp[l] = [null[l] -. NIL;
T -- ~rog2[~g~~[car[~Il~~~~tgr~[cdr[~1111
prog2 is a function that evaluates its two arguments. Its value is the second argument.
The value of pmltgrp is NIL. pgrp - and - pmltgrp are pseudo-functions.


### 7.5 The Free-Storage List and the Garbage Collector

At any given time only a part of the memory reserved for list structures will actually be in use for storing S-expressions. The remaining registers are arranged in a single list called the free-storage list. A certain register, FREE, in the program contains the location of the first register in this list. When a word is required to form some additional list structure, the first word on the free-storage list is taken and the number in register FREE is changed to become the location of the second word on the free-storage list. No provision need be made for the user to program the return of registers to the free-storage list.

This return takes place automatically whenever the free -storage list has been exhausted during the running of a LISP program. The program that retrieves the storage is called the garbage collector.

Any piece of list structure that is accessible to programs in the machine is considered an active list and is not touched by the garbage collector. The active lists are accessible to the program through certain fixed sets of base registers, such as the registers in the list of atomic symbols, the registers that contain partial results of the LISP computation in progress, etc. The list structures involved may be arbitrarily long but each register that is active must be connected to a base register through a car-cdr - chain of registers. Any register that cannot be so reached is not accessible to any program and is nonactive; therefore its contents are no longer of interest.

The nonactive, i. e. , inaccessible, registers are reclaimed for the free-storage list by the garbage collector as follows. First, every active register that can be reached through a car-cdr chain is marked by setting its sign negative. Whenever a negative register is reached in a chain during this process, the garbage collector knows that therest of the list involving that register has already been marked. Then the garbage collector does a linear sweep of the free-storage area, collecting all registers with a positive sign into a new free-storage list, and restoring the original signs of the active registers.

Sometimes list structure points to full words such as BCD print names and numbers. The garbage collector cannot mark these words because the sign bit may be in use. The garbage collector must also stop tracing because the pointers in the address and decrement of a full word are not meaningful.

These problems are solved by putting full words in a reserved section of memory called full-word space. The garbage collector stops tracing as soon as it leaves the free-storage space. Marking in full-word space is accomplished by a bit table.

### VIII. A COMPLETE LISP PROGRAM - THE WANG ALGORITHM FOR THE PROPOSITIONAL CALCULUS

This section gives an example of a complete collection of LISP function definitions which were written to define an algorithm. The program was then run on several test cases. The algorithm itself is explained, and is then written in M-expressions. The complete input card deck and the printed output of the run are reprinted here.

The [Wang Algorithm](https://dl.acm.org/doi/abs/10.1147/rd.41.0002) is a method of deciding whether or not a formula in the propositional calculus is a theorem. The reader will need to know something about the propositional calculus in order to understand this discussion.

We quote from pages 5 and 6 of Wang's paper:
"The propositional calculus (System P)
Since we are concerned with practical feasibility, it is preferable to use more logical
connectives to begin with when we wish actually to apply the procedure to concrete cases.
For this purpose we use the five usual logical constants .V (not), & (conjunction), V (dis-
junction), 3(implication), E; (biconditional), with their usual interpretations.
"A propositional letter P, Q, R, M or N, et cetera, is a formula (and an "atomic
formulat1). If 4, + are formulae, then N +, + & +, + V +, 4 3 +, + f + are formulae.
If n, p are strings of formulae (each, in particular, might be an empty string or a

###### single formula) and + is a formula, then IT, +, p is a string and a - p is a sequent

which, intuitively speaking, is true if and only if either some formula in the string n
(the I1antecedentI1) is false or some formula in the string p (the llconsequent") is true,
i. e., the conjunction of all formulae in the antecedent implies the disjunction of all for-
mulae in the consequent.
"There are eleven rules of derivation. An initial rule states that a sequent with only
atomic formulae (proposition letters) is a theorem if and only if a same formula occurs
on both sides of the arrow. There are two rules for each of the five truth functions -
one introducing it into the antecedent, one introducing it into the consequent. One need
only reflect on the intuitive meaning of the truth functions and the arrow sign to be con-
vinced that these rules are indeed correct. Later on, a proof will be given of their com-
pleteness, i. e., all intuitively valid sequents are provable, and of their consistency,
i. e. , all provable sequents are intuitively valid.

"PI. Initial rule: if h, 5 are strings of atomic formulae, then h -. 5 is a theorem if
some atomic formula occurs on both sides of the arrow.
"In the ten rules listed below, h and 5 are always strings (possibly empty) of atomic
formulae. As a proof procedure in the usual sense, each proof begins with a finite set
of cases of P1 and continues with successive consequences obtained by the other rules .I1

1. Wang, Hao. "Toward Mechanical Mathematics," IBM J. Res. Develop., Vo1.4,
    No. 1. January 1960.

"As will be explained below, a proof looks like a tree structure growing in the wrong
direction. We shall, however, be chiefly interested in doing the step backwards, thereby
incorporating the process of searching for a proof.
"The rules are so designed that given any sequent, we can find the first logical con-
nective, and apply the appropriate rule to eliminate it, thereby resulting in one or two
premises which, taken together, are equivalent to the conclusion. This process can be
repeated until we reach a finite set of sequents with atomic formulae only. Each
connective-free sequent can then be tested for being a theorem or not, by the initial rule.
If all of them are theorems, then the original sequent is a theorem and we obtain a proof;
otherwise we get a counterexample and a disproof. Some simple samples will make this
clear.
"For example, given any theorem of nPrincipia, we can automatically prefix an
arrow to it and apply the rules to look for a proof. When the main connective is 3, it is
simpler, though not necessary, to replace the main connective by an arrow and pro-
ceed. For example:
*2.45. +: (PVQ). 3 .I- P,
*5.21. +:N P &N Q .2. PS Q

```
can be rewritten and proved as follows:
*2.45. /v (P).) -wP
(1) -& Pa PVQ
(2) P -Pi"Q
(3) P -P,Q
VALID
*5.21. ---P& &Q .3. P, Q
(l)-P&w Q-PE Q
(2)"P, NQ -P = Q
(3)- Q -P Q,P
(4) -P 5 Q, P,Q
(5) P -Q, P, Q
VALID
(5) Q -Pa P, Q
VALID
```

```
P2a. Rule -0.y: If +, 5 -hap, thenS-h,~+,pd
P2b. Rule&--: If hap -a,+, thenh,~+, p-a.
P3a. Rule -&: If 5 -.A,+, pand5 -h,+,p, thenS*h,(~&~~l,~-
P3b. Rule &-: If h,+,+,p -a, then ha+&+, P-a.
P4a. Rule -- V : If 5 -A,+,+, p, then 5 -h,(SVICl1p
P4b. RuleV -. : If ha+, p--rand h,+, p-a, then X,+V+, p-a.
P5a. Rule-3 : If 5,+ -h,+,p, then 5 -. A, +3+, p.
```

```
P5b. Rule 3- : If h,+, pus and A, p -a,+, then h,+3JC,p-a.
```

###### P6a. Rule - : If 4, b - h, +, p and +,^5 - A, +,P , then^5 - +a+, P

```
P6b. Rule= -: If +,#,h, p-aand h, p-a,+,#, then h,+ 4, p-a."
```

(2) The LISP Program. We define a function theorem[s] whose value is truth or
falsity according to whether the sequent s is theorem.
The sequent

is represented by the S-expression

where in each case the ellipsis... denotes missing terms, and where +* denotes the
S-expression for +.
Propositional formulae are represented as follows:

1. For "atomic formulaeu (Wang's terminology) we use "atomic symbols1' (LISP
terminology).
2. The following table gives our "Cambridge Polish" way of representing proposi-
tional formulae with given main connectives.

### 1. - + becomes (NOT +*)

```
2- +&51 becomes (AND +* +*)
3- +v$ becomes (OR +* S*)
4- +3# becomes (IMPLIES +* #*)
5.+=+ becomes (EQUIV @* +*)
Thus the sequent
```

- P &NQ -P =, Q,RVS
is represented by
(ARROW ((AND (NOT P) (NOT Q))) ((EQUIV P Q) (OR R S)))

```
The S-function theorem[s] is given in terms of auxiliary functions as follows:
```

```
theorem[s] = thl [NIL;NIL;C~~~ [s] ;caddr[s]]
```

###### member[car[a];c] V [atom[car[ a]] -

```
thl [[member[car[a];al ] - a1 ;T -c cons[car[
a];al]];a2;cdr[a];c];~ -. thl [al;[
```

###### member[car[a];a2] - a2;T - cons[

```
car [a];a2]];cdr [a];~]]]
```

###### c2;cdr[c]];~ - th2[al ;a2;c 1 ;[

#### member[car[c];c2] -, c2;T - cons[

```
car [c];c 2]];cdr [c]]]
th[al ;a2;c 1 ;c2] = [null[a2] - n, null[c2]~thr[car[c2];
a1 ;a2;c 1 ;cdr[c2]];~ -. tke[car[a2];
a1 ;cdr[a2];cl ;c2]]
```

this the main predicate through which all the recursions take place. theorem, tu
anda2 break up and sort the information in the sequent for the benefit of &. The four
arguments oft& are:
al: atomic formulae on left side of arrow
a2: other formulae on left side of arrow
cl: atomic formulae on right side of arrow
c2: other formulae on right side of arrow
The atomic formulae are kept separate from the others in order to make faster the
detection of the occurrence of formula on both sides of the arrow and the finding of the
next formula to reduce. Each use of& represents one reduction according to one of the
10 rules. The forumla to be reduced is chosen from the left side of the arrow if possible.
According to whether the formula to be reduced is on the left or right we use or g.
We have

tke[u;al;a2;cl;c2] = [

###### car[u] = NOT - th1 r[cadr[u];al;a2;c 1 ;c2]

```
car[u] = AND -, th2l[cdr[u];al ;a2;c 1 ;c2];
```

##### car[u] = OR - thlQ[cadr[u];al ;a2;cl ;c2] A thlL

```
caddr [u];al ;a2;c 1 ;c2];
```

###### car[u] = IMPLIES - thll[caddr[u];al ;a2;cl ;c2] fi thlr

```
cadr[u];al ;a2;c 1 ;c2];
```

###### car[u] = EQUIV - th2P [cdr[u];al ;a2;c 1 ;c2] A th2r

```
cdr[u];al ;a2;c 1 ;c2];
```

###### T - error[list[~~~;u;al ;a2;cl;c2]]]

```
thr[u;al;a2;cl;c2] = [
car[u] = NOT -, thl4[cadr[u];al ;a2;cl ;c2];
```

##### car[u] = AND - thlr[cadr[u];al ;a2;cl ;c2] A thlr

```
caddr[u];al ;a2;c 1 ;c2];
```

##### car[u] = OR - th2r[cdr[u];al ;a2;c l;c2]

##### car[u] = IMPLIES - thl 1 [cadr[u];caddr[u];al;a2;c 1 ;c2]

```
car[u] = EQUIV -, thl 1 [cadr[u];caddr[u];al ;a2;c 1 ;c2]
thl 1 [caddr[u];cadr[u];al ;a2;cl;c2];
T -- error[~~~;u;al ;a2;c 1 ;c2]]]
```

The functions thld, thlr, th28, th2r, thll distribute the parts of the reduced for-
mula to the appropriate places in the reduced sequent.
These functions are

##### th2l[v;al ;a2;c 1 ;c2] = [atom[car[v]] - member[car[v];c 11 v

```
thlP[cadr[v];cons[car[v];al];a2;c 1 ;c2];~ -- member[
car [v];c2] v
thld[cadr[v];al ;cons[car[v];a2];cl ;c2]]
```

###### th2r [v;al ;a2;c 1 ;c2] = [atom[car[v]] - member[car[v];al] v

###### thl r[cadr[v];al ;a2;cons[car[v];cl];c2];~ - member[V

```
car [v];a2] V
thl r [cadr [v];a 1 ;a2;c 1 ;cons [car [v];c 2111
```

```
thl~[~l;v2;al;a2;cl;c2] = [atom[vl] -member[vl;cl]~
thlr[v2;cons[vl;al];a2;cl;c2];~ -member[vl;c2]~
thl r[v2;al ;cons[vl ;a2];c 1 ;c2]]
Finally the function member ,is defined by
```

member [ x;u] = ~lull[u]~[e~ual[x; car[u]]\lmember [x;cdr [u]]]

The entire card deck is reprinted below, with only the two loader cards, which are
binary, omitted. The function member is not defined because it is already in the sys-
tem.

* M948-1207 LEVIN, LISP, TEST, 2,3,250,0
TEST WANG ALGORITHM FOR THE PROPOSITIONAL CALCULUS

DEFINE((
(THEOREM (LAMBDA (S) (TH1 NIL NIL (CADR S) (CADDR S))))

```
(THl (LAMBDA (A1 .A2 A C) (COND ((NULL A)
(TH2 A1 A2 NIL NIL C)) (T
(OR (MEMBER (CAR A) C) (COND ((ATOM (CAR A))
(TH1 (COND ((MEMBER (CAR A) Al) Al)
(T (CONS (CAR A) Al))) A2 (CDR A) C))
(T (TH1 A1 (COND ((MEMBER (CAR A) A2) A2)
(T (CONS (CAR A) A2))) (CDR A) C))))))))
```

```
(TH2 (LAMBDA (A1 A2 C1 C2 C) (COND
((NULL C) (TH A1 A2 Cl C2))
((ATOM (CAR C)) (TH2 A1 A2 (COND
((MEMBER (CAR C) C1) C1) (T
(CONS (CAR C) Cl))) C2 (CDR C)))
(T (TH2 A1 A2 C1 (COND ((MEMBER
(CAR C) C2) C2) (T (CONS (CAR C) C2)))
(CDR C))))))
```

(TH (LAMBDA (A1 A2 C1 C2) (COND ((NULL A2) (AND (NOT (NULL C2))
(THR (CAR C2) A1 A2 C 1 (CDR C2)))) (T (THL (CAR A2) A1 (CDR A2)
Cl C2)))))

```
(THL (LAMBDA (U A1 A2 C1 C2) (COND
((EQ (CAR U) (QUOTE NOT)) (THlR (CADR U) A1 A2 C1 C2))
((EQ (CAR U) (QUOTE AND)) (TH2L (CDR U) A1 A2 C1 C2))
((EQ (CAR U) (QUOTE OR)) (AND (THlL (CADR U) A1 A2 C1 C2)
(THlL (CADDR U) A1 A2 Cl C2) ))
((EQ (CAR U) (QUOTE IMPLIES)) (AND (THlL (CADDR U) A1 A2 C1
C2) (THlR (CADR U) A1 A2 C1 C2) ))
((EQ (CAR U) (QUOTE EQUIV)) (AND (THZL (CDR U) A1 A2 C1 C2)
(THZR (CDR U) A1 A2 C1 C2) ))
(T (ERROR (LIST (QUOTE THL) U A1 A2 C1 C2)))
)I)
```

(THR (LAMBDA (U A1 A2 C1 C2) (COND
((EQ (CAR U) (QUOTE NOT)) (TH1 L (CADR U) A1 A2 C 1 C2))
((EQ (CAR U) (QUOTE AND)) (AND (THlR (CADR U) A1 A2 C1 C2)
(THlR (CADDR U) A1 A2 Cl C2) ))
((EQ (CAR U) (QUOTE OR)) (THZR (CDR U) A1 A2 C1 C2))
((EQ (CAR U) (QUOTE IMPLIES)) (THl1 (CADR U) (CADDR U)

```
A1 A2 C1 C2))
((EQ (CAR U) (QUOTE EQUIV)) (AND (TH11 (CADR U) (CADDR U)
A1 A2 C1 C2) (THl1 (CADDR U) (CADR U) A1 A2 C1 C2) ))
(T (ERROR (LIST (QUOTE THR) U A1 A2 C1 C2)))
1))
```

(THlL (LAMBDA (V A1 A2 C1 C2) (COND
((ATOM V) (OR (MEMBER V C1)
(TH (CONS V Al) A2 C1 C2) ))
(T (OR (MEMBER V C2) (TH A1 (CONS V A2) C1 C2) ))
))I

(THlR (LAMBDA (V A1 A2 C1 C2) (COND
((ATOM V) (OR (MEMBER V Al)
(TH A1 A2 (CONS V C1) C2) ))
(T (OR (MEMBER V A2) (TH A1 A2 C1 (CONS V C2))))
1)

(TH2L (LAMBDA (V A1 A2 C1 C2) (COND
((ATOM (CAR V)) (OR (MEMBER (CAR V) C1)
(THlL (CADR V) (CONS (CAR V) Al) A2 C1 C2)))
(T (OR (MEMBER (CAR V) C2) (THlL (CADR V) A1 (CONS (CAR V)
A2) C1 C2)))
1))

```
(TH2R (LAMBDA (V A1 A2 Cl C2) (COND
((ATOM (CAR V)) (OR (MEMBER (CAR V) Al)
(THlR (CADR V) A1 A2 (CONS (CAR V) C 1) C2)))
(T (OR (MEMBER (CAR V) A2) (THlR (CADR V) A1 A2 C1
(CONS (CAR V) C2))))
1))
```

(TH11 (LAMBDA (V1 V2 A1 A2 C1 C2) (COND
((ATOM V1) (OR (MEMBER V1 C1) (THlR V2 (CONS V1 Al) A2 C1
C2)))
(T (OR (MEMBER V1 C2) (THlR V2 A1 (CONS V1 A2) C1 C2)))
1))

TRACE ((THEOREM TH1 TH2 TH THL THR THlL THlR THEL TH2R TH11))

```
THEOREM
((ARROW (P) ((OR P Q))))
```

```
UNTRACE ((THEOREM TH1 TH2 THR THL THl L THlR THZL THZR TH11))
```

```
THEOREM
((ARROW ((OR A (NOT B))) ((IMPLIES (AND P Q) (EQUIV P Q))) ))
```

```
STOP))) 1)) 1)) 1))
FIN END OF LISP RUN M948- 1207 LEVIN
```

```
This run produced the following output:
```

```
* M948-1207 LEVIN, LISP, TEST, 2,3,250,0
TEST WANG ALGORITHM FOR THE PROPOSITIONAL CALCULUS
THE TIME (8/ 8 1506.1) HAS COME, THE WALRUS SAID, TO TALK OF MANY THINGS
```

... - LEWIS CARROLL -

```
FUNCTION EVAUUOTE HAS BEEN ENTERED, ARGUMENTS..
DEFINE
[ The complete list of definitions read in is omitted to save space.]
```

```
END OF EVALQUOTE, VALUE IS..
(THEOREM THl TH2 TH THL THR THlL THlR TH2L TH2R TH11)
```

```
FUNCTION EVALQUOTE HAS BEEN ENTERED, ARGUMENTS..
TRACE
((THEOREM TH1 TH2 TH THL THR THlL THlR TH2L THZR TH11))
```

```
END OF EVALQUOTE, VALUE IS..
NIL
```

```
FUNCTION EVALQUOTE HAS BEEN ENTERED, ARGUMENTS..
THEOREM
```

ARGUMENTS OF TH1
NIL
NIL

```
(P)
((OR P Q))
```

```
ARGUMENTS OF TH1
(PI
NIL
NIL
((OR P Q))
```

```
ARGUMENTS OF THZ
(PI
NIL
NIL
NIL
((OR P Q))
```

```
ARGUMENTS OF TH2
(PI
NIL
NIL
((OR P Q))
NIL
```

```
ARGUMENTS OF TH
(PI
NIL
NIL
((OR P Q))
```

```
ARGUMENTS OF THR
(OR P Q)
(PI
NIL
NIL
NIL
```

```
ARGUMENTS OF THZR
(P Q)
```

(PI
NIL
NIL
NIL

VALUE OF TH2R
*T*

VALUE OF THR
*T*

VALUE OF TH
*T*

VALUE OF TH2
*T*

VALUE OF TH2
ST*

VALUE OF TH1
*T*

VALUE OF TH1
*T*

END OF EVALQUOTE, VALUE IS..
*T*

FUNCTION EVALQUOTE HAS BEEN ENTERED, ARGUMENTS..
UNTRACE
((THEOREM TH1 TH2 THR THL THlL THlR THZL TH2R THl1))

END OF EVALQUOTE, VALUE IS..
NIL

```
FUNCTION EVALQUOTE HAS BEEN ENTERED, ARGUMENTS..
THEOREM
((ARROW ((OR A (NOT B))) ((IMPLIES (AND P Q) (EQUIV P Q )))))
```

```
ARGUMENTS OF TH
NIL
((OR A (NOT B)))
NI L
((IMPLIES (AND P Q) (EQUIV P Q)))
```

```
ARGUMENTS OF TH
(A)
NIL
NIL
((IMPLIES (AND P Q) (EQUIV P Q)))
```

```
ARGUMENTS OF TH
(A)
((AND P Q))
NIL
((EQUIV P Q))
```

ARGUMENTS OF TH
(Q P A)
NIL
NIL
((EQUIV P Q))

```
VALUE OF TH
*T*
```

```
VALUE OF TH
*T*
```

```
VALUE OF TH
*T*
```

```
ARGUMENTS OF TH
NIL
((NOT B))
NIL
((IMPLIES (AND P Q) (EQUIV P Q)))
```

```
ARGUMENTS OF TH
NIL
```

NIL

```
(B)
((IMPLIES (AND P Q) (EQUIV P Q))
```

ARGUMENTS OF TH
NIL

((AND P Q))
(B)
((EQUIV P Q))

ARGUMENTS OF TH
(Q P)
NIL
(B)
((EQUIV P Q))

VALUE OF TH
*T*

```
VALUE OF TH
*T*
```

VALUE OF TH
*T*

VALUE OF TH
*T*

VALUE OF TH
*T*

```
END OF EVALQUOTE, VALUE IS..
*T*
```

```
THE TIME (8/ 8 1506.3) HAS COME, THE WALRUS SAID, TO TALK OF MANY THINGS
```

... - LEWIS CARROLL -

```
END OF EVALQUOTE OPERATOR
```

```
FIN END OF LISP RUN M948-1207 LEVIN
```

```
END OF LISP JOB
```


## APPENDIX A : FUNCTIONS AND CONSTANTS IN THE LISP SYSTEM

This appendix contains all functions available in the LISP System as of August 1962. Each entry contains the name of the object, the property under which it is available (e. g., EXPR, FEXPR, SUBR, FSUBR, or APVAL), whether it is a pseudo-function, functional (function having functions as arguments), or predicate, and in some cases a definition of the function as an M-expression. In the case of APVALts, the value is given.

The LISP Library is a file of BCD cards distributed with the LISP System. It is not intended to be used as input to the computer without being edited first. Have the Library file punched out, and then list the cards. Each Library function is preceded by a title card that must be removed. Some Library entries are in the form of a DEFINE, while some are in the form of an assembly in LAP. Note that some of them have auxiliary functions that must be included.


Elementary Functions
car - [x] SUBR
cdr - [x] SUBR
The elementary functions car and &r always have some sort of value rather than
giving an error diagnostic. A chain of &Is applied to an atomic symbol will allow one
to search its property list. Indiscriminate use of these functions past the atomic level
will result in non-list structure and may appear as lengthy or even infinite garbage
expressions if printed.
CAR SXA CARX, 4
PDX 094
CLA 0,4
PAX 084
PXD 0,4
CARX AXT **, 4
TRA 1,4
CDR SXA CDRX, 4
PDX O,4
C LA 0,4
PDX 0,4
PXD 0,4
CDRX AXT **, 4
TRA 1,4
```

CO~S[X;~] - SUBR

cons obtains a new word from the free storage list and places its two arguments
in the address and decrement of this word, respectively. It does not check to see if
the arguments are valid list structure. The value of cons is a pointer to the word that

```
was just created. If the free storage list has been exhausted, cons calls the garbage
collector to make a new free storage list and then performs the cons operation.
SUBR predicate
The first word on the property list of an atomic symbol contains -1 or 777778 in
the address. The following subroutine depends upon this, and on the fact that NIL is
located at 0 and *T* is located at -1 and has 1 as its complement pointer.
ATOM
```

```
ATOMX
TRUE
```

```
SXA
PDX
CLA
PAX
TXL
CLA
TRA
PXA
AXT
TRA
OCT
```

```
ATOMX, 4
0,4
0~4 GET CAR OF ARGUMENT
084
*t3,4, -2 TRANSFER IF NOT ATOMIC
TRUE IF IT IS ATOMIC
*t2
O,O NIL IF NOT ATOMIC
**, 4
1,4
1000000
es[x;y - I SUBR predicate
```

- eq is true if its two arguments are identical list structure.
    EQ STQ
       SUB
          TZE
          PXA
          TRA
          CLA
          TRA
    TRUE OCT
    X PZE

```
X
X
*t3
0,o
184
TRUE
1s 4
1000000
```

```
TRANSFER IF EQUAL
OTHERWISE VALUE IS NIL
VALUE IS *T*

equal[x; y] - SUBR predicate

* equal is true if its arguments are the same S-expression, although they do not have
to be identical list structure in the computer. It uses 7 eq on the atomic level and is
recursive. Floating point numbers in S-expressions are compared for numerical equal-
ity with a floating point tolerance of 3 X loW6. Fixed point numbers are compared for
numerical equality.
FSUBR

The value of list is a list of its arguments.

* null[x] SUBR predicate

```
The value of & is true if its argument is NIL which is located at 0 in the computer.
NULL TZE *+3
PXA 080
TRA 1,4
CLA TRUE
TRA 1,4
TRUE OCT 1000000
```
<a name="page58">page 58</a>

#### rplaca[x; y] : SUBR pseudo-function

#### rplacd[x; y] : SUBR pseudo-function

These list operators change list structure and can damage the system memory if not
used properly. See [page 41](#page41) for a description of usage.

### Logical Connectives

#### and[x<sub>1</sub>; x<sub>2</sub>; ... ; x<sub>n</sub>] : FSUBR predicate

The arguments of are evaluated in sequence, from left to right, until one is found
that is false, or until the end of the list is reached. The value of and is false or true
respectively.

#### or[x<sub>1</sub>; x<sub>2</sub>; ... ; x<sub>n</sub>] : FSUBR predicate

The arguments of or are evaluated in sequence from left to right, until one is found that is true, or until the end of the list is reached. The value of or is true or false respectively.

#### not [x]: SUBR predicate

The value of not is true if its argument is false, and false otherwise.

### Interpreter and Prog Feature

These are described elsewhere in the manual:
`APPLY, EVAL, EVLIS, QUOTE, LABEL, FUNCTION, PROG, GO, RETURN, SET, SETQ.`

### Defining Functions and Functions Useful for Property Lists

#### define [x] : EXPR pseudo-function

The argument of `define`, `x`, is a list of pairs

> ((u<sub>l</sub> v<sub>l</sub>) (u<sub>2</sub> v<sub>2</sub>) ... (u<sub>n</sub> v<sub>n</sub>))

where each `u` is a name and each `v` is a &lambda;-expression for a function . For each `pair`, define puts an `EXPR` on the property list for `u` pointing to `v`. The function of `define` puts things on at the front of the property list. The value of `define` is the list of `u`s.

> define[x] = deflist[x; EXPR]

#### deflist [x; ind] : EXPR pseudo-function

The function `deflist` is a more general defining function. Its first argument is a list of pairs as for define. Its second argument is the indicator that is to be used. After `deflist` has been executed with (u<sub>i</sub> v<sub>i</sub>) among its first argument, the property list of u<sub>i</sub> will begin:

If `deflist` or `define` is used twice on the same object with the same indicator, the old value will be replaced by the new one.

#### attrib[x; e] : SUBR pseudo-function

The function attrib concatenates its two arguments by changing the last element of its first argument to point to the second argument. Thus it is commonly used to tack something onto the end of a property list. The value of attrib is the second argument.

For example
```
attrib[FF; (EXPR (LAMBDA (X) (COND ((ATOM X) X) (T (FF (CAR x))))))]
```
would put EXPR followed by the LAMBDA expression for FF onto the end of the prop-
erty list for FF.


#### prop[x; y; u] : SUBR functional

The function `prop` searches the list `x` for an item that is `eq` to `y`. If such an element is found, the value of `prop` is the rest of the list beginning immediately after the element. Otherwise the value is `u[]`, where u is a function of no arguments.
```
prop[x; y; u] = [null[x] -> u[ ];
                   eq[car[x];y] -> cdr[x]
                  T -> prop[cdr[x]; y; u]]
```
SUBR
```

- get is somewhat like prop; however its value is car of the rest of the list if the indi-
cator is found, and NIL otherwise.

##### get [x;~] = [null[x] - NIL; eq[car [x];~] - cadr [x]

* cset[ob;val] EXPR pseudo-function

```
This pseudo-function is used to create a constant by putting the indicator APVAL
and a value on the property list of an atomic symbol. The first argument should be an
atomic symbol; the second argument is the value is cons[val;N1~].

#### csetq[ob; val] : FEXPR pseudo-function

csetq is like cset - except that it quotes its first argument instead of evaluating it.

#### remprop[x; ind] : SUBR pseudo-function 

The pseudo-function remprop searches the list, x, looking for all occurrences of the
indicator ind. When such an indicator is found, its name and the succeeding property
are removed from the list. The two "endsn of the list are tied together as indicated by
the dashed line below.

The value of remprop is NIL.

When an indicator appears on a property list without a property following it, then
it is called a flag. An example of a flag is the indicator TRACE which informs the inter-
preter that the function on whose property list it appears is to be traced. There are two
pseudo-functions for creating and removing flags respectively.

#### flag [I; ind] : EXPR pseudo-function

The pseudo-function flag puts the flag ind on the property list of every atomic symbol
in the list 1. Note that d cannot be an atomic symbol, and must be a list of atomic sym-
bols. The flag is always placed immediately following the first word of the property
list, and the rest of the property list then follows. The value of flag is NIL. No property
list ever receives a duplicated flag.

#### remflag[l; ind] : EXPR pseudo-function

remflag removes all occurrences of the indicator ind from the property list of each
atomic symbol in the list 8. It does this by patching around the indicator with a rplacd
in a manner similar to the way remprop works.

### Table Building and Table Reference Functions

#### pair [x; y] : SUBR

The function pair has as value the list of pairs of corresponding elements of the lists x and y. The arguments x and y must be lists of the same number of elements. They should & be atomic symbols. The value is a dotted pair list, i. e. ((a (a p2)...

    pair[x;y] = [prog[u;v; m]
    u:= x;
    v:= y;

#### A [null[u] - [null[v] - return[m];~ - error[$2]]]

```
[null[v] -, error[~3]];
m:= cons[cons[car[u];car[v]];m];
u:= cdr[u];
v:= cdr[v];
go[~Il
```

#### sassoc[x; y; u] : SUBR functional

The function sassoc searches y, which is a list of dotted pairs, for a pair whose first
element that is x. If such a pair is found, the value of sassoc is this pair. Otherwise
the function u of no arguments is taken as the value of sassoc.

#### subst[x; y; z] : SUBR

The function subst has as value the result of substituting x for all occurrences of
the S-expression y in the S-expression z.

###### subst[x;y;z] = [equal[y;z] - x

##### atom[z] - z

T .- cons[subst[x;y;car [z]];subst [x;y;cdr[e]]]]

#### sublis [x ; y] : SUBR

Here x is a list of pairs,
((u<sub>1</sub> . v<sub>1</sub>) (u<sub>2</sub> .  v<sub>2</sub>) (u<sub>n</sub> . v<sub>n</sub>))
The value of `sublis[x; y]` is the result of substituting each `v` for the corresponding
`u` in `y`.
Note that the following M-expression is different from that given in Section I, though
the result is the same.

```
sublis[x;y] = [null[x] -> y;
            null[y] -> y;
            T -> search[x;
                    lambda[[j]; equal[y;caar[j]]];
                    lambda[[j]; cdar[j]];
                    lambda[[j]; [atom[y] -> y;
                                T -> cons[sublis[x; car[y]];
                                        sublis[x; cdr[y]]]]]]]
```

### List Handling Functions

#### append [x;y] : SUBR
The function append combines its two arguments into one new list. The value of
append is the resultant list. For example,
append[(^ B) (a1 = (A B C)
```
append [x;y] = [null[x] -. y ; T - cons [car [x]; append[cdr [x]; y I]]
```
Note that append copies the top level of the first list; append is like - nconc except that
nconc does not copy its first argument.

* conc[xl;x2;... ;x n ] : FEXPR pseudo-function
  * conc concatenates its arguments by stringing them all together on the top level. For
  example,
  conc[(~ (B C) D); (F); (G H)] = (A (B C) D F G H).
* conc concatenates its arguments without copying them. Thus it changes existing list
structure and is a pseudo-function. The value of conc is the resulting concatenated list.

#### nconc [x; y] : SUBR pseudo-function

The function `nconc` concatenates its arguments without copying the first one. The
operation is identical to that of attrib except that the value is the entire result, (i. e. the
modified first argument, x).

The program for nconc[x;y] has the program variable m and is as follows:
```
nconc [x; y ] = prog [[m];
[null[x] - return[~]]
```

#### COPY [X] : SUBR

This function makes a copy of the list x. The value of copy is the location of the
copied list.

copy[x] = [null[x] - ~~~;atom[x] - x;T -- cons[copy[car[x]]

```
co~[cdr[xllIl
```

#### reverse[t] : SUBR

This is a function to reverse the top level of a list. Thus
reverse[(A B (C. D))] = ((C D) B A))
reverse[t] = prog[[v];
u: =t;
```
A [null[u] - return[v]]

```
v:=cons[car[u];v];
u:=cdr[u];
so[AlI

#### member[x; l ] : SUBR predicate

If the S-expression x is a member of the list l, then the value of member is T.
Otherwise, the value is NIL.

##### member[x;l] = [null[l] - ~;equal[x;car[L]] -- T

##### T - member[x;cdr[l]]]

#### length[x] : SUBR

The value of length is the number of items in the list x. The list ( ) or NIL has
length 0.

#### efface[x; Q] : SUBR pseudo-function

The function efface deletes the first appearance of the item x from the list 8.
```
efface[x;l ] = [null[l] -. NIL; /'
equal[x;car[8]] .-. cdr[8];
T -- rplacd[k ;effac e[x;cdr [8]]]]
```

These four functionals apply a function, f, to x, then to cdr[x], then to cddr[x], etc.

Functionals or Functions with Functions as Arguments

#### maplist [x; f ] : SUBR functional

The function maplist is a mapping of the list x onto a new list f[x].

##### maplist [x; f] = [null[x] - NIL; T - cons[f [x]; maplist [cdr [x]; f]]]

#### mapcon [x; f ] : SUBR pseudo-functional

The function mapcon is like the function maplist except that the resultant list is a
concatenated one instead of having been created by cons-ing.
```
mapcon[x; f ] = [null[x] -> NIL; T - nconc[f [x]; mapcon[cdr [x]; f ]]]
```

#### map[x; f ] : SUBR functional
The function map - is like the function maplist except that the value of map is NIL,
and map does not do a cons of the evaluated functions. map is used only when the action
of doing f[x] is important.
The program for map[x;f] has the program variable m and is the following:
map[x;f] = prog[[m];
m:= x;
LOOP [null[m] -. returnl~~~]];
f [m];
m:= cdr[m];
go[~oopl1

#### search[x; p; f; u] : SUBR functional

The function search looks through a list x for an element that has the property p, and if such an element is found the function f of that element is the value of search. If there is no such element, the function u of one argument x is taken as the value of search (in this case x is, of course, NIL).

Arithmetic Functions
These are discussed at length in Section IV.
function type number of args
plus FSUBR indef.
minus SUBR 1

- value

difference
times
divide
quotient
remainder
add 1
sub 1
max
min
recip
expt
lessp
greaterp
zerop
onep
minusp
numberp
f ixp
float p
log or
logand
logxor
leftshift

SUBR
FSUBR
SUBR
SUBR
SUBR
SUBR
SUBR
FSUBR
FSUBR
SUBR
SUBR
SUBR
SUBR
SUBR
SUBR
SUBR
SUBR
SUBR
SUBR
FSUBR
FSUBR
FSUBR
SUBR

predicate
predicate
predicate
predicate
predicate
predicate
predicate
predicate
```

```
2
indef.
2
2
2
1
1
indef.
indef.
1 2 2 2 1 1 1 1 1 1

indef.
indef.
indef.
2

Xl'X2'... *X n
list [x/~; remainder]

remainder of x/~

largest of xi
smallest of xi
[fixp[x]-0; ~-.quotient[l ;XI]
XY

x is negative
x is a number
x is a fixed point number
x is a floating point no.
xlVx2V.. .Vx n ORA
x1Ax2A... A xn ANA
x ,*x24... Vxn ERA
x 2Y
array SUBR 1 declares arrays

### The Compiler and Assembler

#### compile[x] : SUBR pseudo-function

The list x contains the names of previously defined functions. They are compiled.

#### special[x] : SUBR pseudo-function

The list x contains the names of variables that are to be declared SPECIAL.

#### unspecial[x] : SUBR pseudo-function

The list x contains the names of variables that are no longer to be considered
SPECIAL by the compiler.

#### common[x] : SUBR pseudo-function

The list x contains the names of variables that are to be declared COMMON.

#### uncommon[x] : SUBR pseudo-function

The list x contains the names of variables that are no longer to be considered
COMMON by the compiler.

#### lap[list; - table] : SUBR pseudo-function

The assembler LAP is discussed in appendix C.
opd ef ine [x] EXPR pseudo-function
opdefine defines new symbols for the assembler LAP. The argument is a list of
dotted pairs, each pair consisting of symbol and value.

#### readlap[ ] : EXPR pseudo-function

readlap reads assembly language input and causes it to be assembled using LAP.
The input follows the STOP card of the packet containing the readlap. Each function to
be read in consists of a list of the two arguments of 2. These are read in successively
until a card containing NIL is encountered. readlap uses remob to remove unwanted
atomic symbols occurring in the listing. For this reason, it should only be used to read
cards that have been produced by punchlap.

### Input and Output

#### read[] : SUBR pseudo-function

The execution of read causes one list to be read from SYSPIT, or from the card
reader. The list that is read is the value of read.

#### print[x] : SUBR pseudo-function
    The execution of - print causes the S-expression x to be printed on SYSPOT and/or
    the on-line printer. The value of print is its argument.
    punchrx] - SUBR pseudo.-function
    The execution of punch causes S-expression x to be punched in BCD card images
    on SYSPPT. The value of punch - is its argument.
    prin l [x] SUBR pseudo-function
* prinl prints an atomic symbol without terminating the print line. The argument
of - prini must be an atomic symbol.

#### terpri[] : SUBR pseudo-function

terpri terminates the print line.

The character reading, sorting and printing functions are discussed in appendix F.

startread pack opc har error1 numob
advance unpack dash mknam
endread digit
clearbuff liter

#### Functions for System Control, Debugging, and Error Processing

#### trace[x] : EXPR pseudo-function

The argument of trace is a list of functions. After trace has been executed, the
arguments and values of these functions are printed each time the function is entered
recursively. This is illustrated in the printed output of the Wang Algorithm example.
The value of trace is NIL. Special forms cannot be traced.

#### untrace [x] : EXPR pseudo-function

This removes the tracing from all functions in the list x. The value of untrace is NIL.
The following pseudo-functions are described in the section on running the LISP
system:

--- count, uncount, speak, error, errorset.

### Miscellaneous Functions '
prog2 [x;~] SUBR
The value of prog2 is its second argument. It is used mainly to perform two pseudo-
functions.

- CPl [XI SUBR
  * cpA copies its argument which must be a list of a very special type.

-1 I ILL WORD I I-T&~]

The copied list is the value of cpi.

#### gensym[ ] : SUBR

The function gensym has no arguments. Its value is a new, distinct, and freshly-
created atomic symbol with a print name of the form G00001, G00002,... , G99999.
This function is useful for creating atomic symbols when one is needed; each one
is guaranteed unique. gensym names are not permanent and will not be recognized if
read back in.
FEXPR
The qits in select are evaluated in sequence from left to right until one is found that
qi =
and the value of select is the value of the corresponding ei. If no such qi is found the
value of select is that of e.

#### tempus -fugit [ ] : SUBR pseudo-function

Executing this will cause a time statement to appear in the output. The value is
NIL. (tempus-fugit is for MIT users only.)

#### load[] : SUBR pseudo-function

Program control is given to the LISP loader which expects octal correction cards,
704 row binary cards, and a transfer card.

#### plb[] : SUBR pseudo-function

This is equivalent to pushing "LOAD CARDS " on the console in the middle of a LISP
program.

#### reclaim[] : SUBR pseudo-function

Executing this will cause a garbage collection to occur. The value is NIL.

#### pause[] : SUBR pseudo-function

Executing this will cause a program halt. Pushing START will cause the program
to continue, returning the value NIL.

#### excise[x] : SUBR pseudo-function

If x is NIL, then the compiler will be overwritten with free storage, If x is *T*,
then both the compiler and LAP will be overwritten by free storage. excise may be
executed more than once. The effect of excise[W'*] is somewhat unreliable. It is
recommended that before executing this pair, remprop [*;sYM] be executed.
dump [low;high; mode; title] : SUBR pseudo-function
dump causes memory to be dumped in octal. The dump is from location 1s to
location - high. If the mode is 0, then the dump is straight. If the mode is 1, the words
containing zero in the prefix and tag will be dumped as complement decrements and
addresses. This is convenient for examining list structure.
intern[x] : SUBR pseudo-function
The argument of intern must be a PNAME type of structure, that is, a list of full
words forming a print name. If this print name belongs to an already existing atomic
symbol, this is found, otherwise a new one is created. The value of intern in either
case is an atomic symbol having the specified print name.
r emob[x] : SUBR
This removes the atom x from the object list. It causes the symbol and all its
properties to be lost unless the symbol is referred to by active list structure. When
an atomic symbol has been removed, subsequent reading of its name from input will
create a different atomic symbol.

### The LISP Library

The LISP Library is distributed as the second file on the LISP setup tape. To use
any part of it, punch out the entire library and remove the part you wish to use. Be

sure to strip off comment cards, unnecessary DEFINE cards, and unnecessary cards
that close a define with )).
Some entries in the library have several parts or define more than one function.
EXPR pseudo-function
traceset is a debugging aid. The argument x should be a list of functions names.
Each of these functions must be an EXPR which has a PROG on the top level. traceset
modifies the definition of the function so that every SETQ on the first level inside the
PROG is traced.
For example, suppose a PROG has the statement (SETQ A X). At run time, if this
statment is executed while x has the value (U V), then in addition to setting the variable
a, the function will print out:

(A =)
(U V)

untraceset[x] is part of the traceset package. Its argument is a list of functions whose
definitions are to be restored to their original condition.

punchlap[ 1 EXPR pseudo-function

punchlap allows one to compile functions and have the results punched out in assem-
bly language LAP. The punched output is in a format suitable for readlap. The func -
tions to be compiled into LAP are placed at the end of the packet following the STOP
card. Each one is read individually and the result is punched out. No assembling into
memory takes place. The process stops when a card containing the word NIL is encoun-
tered after the last function.
Each function must consist of a list of the form (name exp) which is the exact form
for insertion into a define.
Part of punchlap is a dummy definition of - lap. This prevents - lap from being used
within the memory of this packet. The printout from punchlap is not a copy of the

cards produced; only the internal functions have their LAP printed. The PNAMEs of
atoms in the EXPRs and FEXPRs of punchlapped functions must not contain class C
characters.
pr intpr op[x] EXPR pseudo-function

If x is an atomic symbol, all of its properties will be printed in the output. Nothing
is changed by printprop.

punchdef [x] EXPR pseudo-function

If x is a list of atomic symbols, each one having an EXPR or FEXPR will have its
definition punched out. Nothing is changed.

APVAL1s
The following is a list of all atoms with APVALts on their property lists in the basic
system and their values.

APVAL

BLANK
CHARCOUNT
COMMA
CURCHAR
DOLLAR
EOF
EOR
EQSIGN
F
LPAR
NIL
OBLIST
PERIOD
PLUSS
RPAR
SLASH
STAR
T
*T*

value

(BCD blank)
(character count during reading of characters)
J
(current character during reading of characters)
$
$EOF $
$EOR$

NIL
(
NIL
(bucket sorted object list)

1. The entire set of objects (atomic symbols) existing in the system can be printed out
    by performing
       EVAL (OBLIST NIL).

<a name="page70">page 70</a>

## APPENDIX B : THE LISP INTERPRETER

This appendix is written in mixed M-expressions and English. Its purpose is to describe as closely as possible the actual working of the interpreter and PROG feature. The functions `evalquote`, `apply`, `eval`, `evlis`, `evcon`, and the `PROG` feature are defined by using a language that follows the M-expression notation as closely as possible and contains some insertions in English.

```mexpr
evalquote[fn; args]=[get[fn; FEXPR] v get[fn; FSUBR] -> eval[cons [ fn; args]; NIL];
        T -> apply[fn; args; NIL]
```

This definition shows that `evalquote` is capable of handling special forms as a sort of exception. Apply cannot handle special forms and will give error `A2` if given one as its first argument.

The following definition of `apply` is an enlargement of the one given in Section I. It shows how functional arguments bound by FUNARG are processed, and describes the way in which machine language subroutines are called.

In this description, `spread` can be regarded as a pseudo-function of one argument. This argument is a list. `spread` puts the individual items of this list into the AC, MQ,
$ARG3,... the standard cells *[general purpose registers]* for transmitting arguments to functions.

These M-expressions should not be taken too literally. In many cases, the actual
program is a store and transfer where a recursion is suggested by these definitions.

```mexpr
apply[fn; args; a]=[
    null[fn] -> NIL;
    atom[fn] -> [get[fn; EXPR] -> apply[expr ; args ; a];
        get[fn; subr] -> {spread[args];
                          $ALIST := a;
                          TSX subr, 4};
        T -> apply[cdr[sassoc[fn; a; lambda[[];error [A2]]]]; args ;a];
    eq[car[fn]; LABEL] -> apply[caddr[fn];args;
                                cons[cons[cadr[fn];
                                    caddr[fn]];a]];
    eq[car[fn]; FUNARG] -> apply[cadr[fn]; args; caddr[fn]];
    eq[car [fn]; LAMBDA] -> eval[caddr[fn]; 
                                nconc [pair[cadr[fn]; args]; a]];
    T -> apply[eval[fn; a]; args; a]]
```
*NOTE THAT the formatting of this MEXPR is beyond the capabilities of Markdown to reproduce; this is a rational reconstruction, but to be perfectly certain of the interpretation consult the PDF*



-----

1. The value of get is set aside. This is the meaning of the apparent free or undefined variable.

<a name="page71">page 71</a>

```mexpr
eval[form; a]= [
    null[form] -> NIL;
    numberp[form] -> form;
    atom[form] -> [get[form; APVAL] -> car[apval];
                  T -> cdr[sassoc[form; a; lambda[[ ]; error[A8]]]]];
    eq[car[form]; QUOTE] -> cadr[form]; 
    eq[car[form]; FUNCTION] -> list[FUNARG; cadr[form]; a];
    eq[car [form]; COND] -> evcon[cdr[form]; a];
    eq[car [form]; PROG] -> prog[cdr[form]; a];
    atom[car[form]] -> [get[car [form]; EXPR] -> 
                            apply[expr; evlis[cdr[form]; a]; a];
                         get[car[form]; FEXPR] ->
                             apply[fexpr; list[cdr[form]; a]; a];
                         get[car[form]; SUBR] -> {spread[evlis[cdr[form]; a]];
                                                   $ALIST := a;
                                                   TSX subr 4};
                        get[car[form]; FSUBR] -> {AC := cdr[form];
                                                  MQ := $ALIST := a;
                                                  TSX fsubr 4}
                        T -> eval[cons[cdr[sassoc[car[form]; a;
                                                  lambda[[];error[A9]]]];
                                  cdr[form]]; a]];
    T-apply [car [form];evlis [cdr [form]; a]; a]]
    
evcon[c; a] = [null[c] -> error[A3];
            eval[caar[c]; a] -> eval[cadar[a]; a];
            T -> evcon[cdr[ c]; a]]

evlis[m; a] = maplist[m; lambda[[j]; eval[car[j]; a]]]
```
### The PROG Feature

The PROG feature is an FSUBR coded into the system. It can best be explained in
English, although it is possible to define it by using M-expressions.

1. As soon as the PROG feature is entered, the list of program variables is used
to make a new list in which each one is paired with NIL. This is then appended to the current a-list. Thus each program variable is set to NIL at the entrance to the program.
2. The remainder of the program is searched for atomic symbols that are under-
stood to be location symbols. A go-list is formed in which each location symbol is paired with a pointer into the remainder of the program.
3. When a set or a setq - is encountered, the name of the variable is located on the a-list. The value of the variable (or cdr of the pair) is actually replaced with the new value.

-----
1. The value of get is set aside. This is the meaning of the apparent free or undefined variable.
2. In the actual system this is handled by an FSUBR rather than as the separate special case as shown here.

<a name="page72">page 72</a>

If the variable is bound several times on the a-list, only the first or most recent occurrence is changed. If the current binding of the variable is at a higher level than the entrance to the prog, then the change will remain in effect throughout the scope of that binding, and the old value will be lost.

If the variable does not occur on the a-list, then error diagnostic `A4` or `A5` will occur.

4. When a return is encountered at any point, its argument is evaluated and returned as the value of the most recent prog that has been entered.
5. The form go may be used only in two ways.
    a. `(GO X)` may occur on the top level of the prog, `x` must be a location symbol of this `prog` and not another one on a higher or lower level.
    b. This form may also occur as one of the value parts of a conditional expression, if this conditional expression occurs on the top level of the `prog`.
    If a `go` is used incorrectly or refers to a nonexistent location, error diagnostic `A6` will occur.

6. When the form cond occurs on the top level of a `prog`, it differs from other
     `cond`s in the following ways.
       a. It is the only instance in which a `go` can occur inside a `cond`.
       b. If the `cond` runs out of clauses, error diagnostic `A3` will not occur. Instead, the `prog` will continue with the next statement.
     
7. When a statement is executed, this has the following meaning, with the exception
     of the special forms `cond`, `go`, `return`, `setq` and the pseudo-function `set`, all of which  are peculiar to `prog`.
     The statement `s` is executed by performing `eval[s;a]`, where `a` is the current a-list, and then ignoring the value.
     
8. If a prog runs out of statements, its value is NIL.
   When a prog - is compiled, it will have the same effect as when it is interpreted, although the method of execution is much different; for example, a go is always cornpiled as a transfer. The following points should be noted concerning declared variables.<sup>1</sup>
     1. Program variables follow the same rules as h variables do.
         a. If a variable is purely local, it need not be declared.
         b. Special variables can be used as free variables in compiled functions. They may be set at a lower level than that at which they are bound.
         c. Common program variables maintain complete communication between compiled programs and the interpreter.
     
     2. & as distinct from setq can only be used to set common variables.
   

-----
1. See Appendix D for an explanation of variable declaration.

<a name="page73">page 73</a>

## APPENDIX C : THE LISP ASSEMBLY PROGRAM (LAP)

lap is a two-pass assembler. It was specifically designed for use by the new com-
piler, but it can also be used for defining functions in machine language, and for making
patches.

* lap is an entirely internal assembler. Its input is in the form of an S-expression
that remains in core memory during the entire assembly. No input tape is moved during
the assembly. - lap does not produce binary output in the form of cards. It assembles
directly into memory during the second pass.

Format

* lap is a pseudo-function with two arguments. The first argument is the listing, the
second argument is the initial symbol table. The value of lap is the final symbol table.
The first item of the listing is always the origin. All remaining items of the listing
are either location symbols if they are atomic symbols other than NIL, or instructions
if they are composite S-expressions or if they are NIL.

Origin

The origin informs the assembler where the assembly is to start, and whether it
is to be made available as a LISP function. The origin must have one of the following
formats.

1. If the origin is an octal or decimal number, then the assembly starts at that
locat ion.
2. If the origin is an atomic symbol other than NIL, then this symbol must have
a permanent value (SYM) on its property list. The value of this SYM is a number speci-
fying the starting location.
3. If the origin is NIL, then the assembly will start in the first available location
in binary program space. If the assembly is successfully completed, then the cell spec-
ifying the first unused location in binary program space is updated. If the assembly
cannot fit in binary program space, an error diagnostic will be given.
4. If the origin is of the form (name type n), then the assembly is in binary pro-
gram space as in the case above. When the assembly is completed, the indicator, type,
is placed on the property list of the atomic symbol name. Following the indicator is a
pointer to a word containing TXL, the first location of the program just assembled in
the address, and the number n in the decrement. type is usually either SUBR or
FSUBR. n is the number of arguments which the subroutine expects.

Symbols

Atomic symbols appearing on the listing (except NIL or the first item on the listing)

<a name="page74">page 74</a>

are treated as location symbols. The appearance of the symbol defines it as the location
of the next instruction in the listing. During pass one, these symbols and their values
are made into a pair list, and appended to the initial symbol table to form the final sym-
bol table. This is used in pass two to evaluate the symbols when they occur in instruc-
tions. It is also the value of lap.
Symbols occurring on this table are defined only for the current assembly. The
symbol table is discarded after each assembly.
Permanent symbols are defined by putting the indicator SYM followed by a pointer
to a value on their property lists.

Instructions

Each instruction is a list of from zero to four fields. Each field is evaluated in the
same manner; however, the fields are combined as follows.

1. The first field is taken as a full word.
2. The second field is reduced algebraically modulo 2 15, and is OR1ed into the
address part of the word. An arithmetic -1 is reduced to 77777Q.
3. The third field is shifted left 15 bits, and then OR1ed into the word. A tag of
four is written "4". A tag of 2 in an instruction with indirect bits is written "602Qt1.
4. The fourth field is reduced modulo 215 and is OR1ed into the decrement.

- Fields
Fields are evaluated by testing for each of the following conditions in the order listed,.

1. If the field is atomic.
a. The atomic symbol NIL has for its value the contents of the cell $ORG.
During an assembly that is not in binary program space, this cell contains the starting
address of the next assembly to go into binary program space.
b. The atomic symbol * has the current location as its value.
c. The symbol table is searched for an atomic symbol that is identical to the
field.
d. If the field is a number, then its numerical value is used.
e. The property list of the atomic field is searched for either a SYM, a SUBR,
or an FSUBR.
2. If the field is of the form (E a ), then the value of the field is the complement of
the address of the S-expression a. The expression a is protected so that it can never
be collected by the garbage collector.
3. If the field is of the form (QUOTE a ), then a literal quantity containing a in
the decrement is created. It is the address of this quantity that is assembled. Quoted
S-expressions are protected against being collected by the garbage collector. A new
literal will not be created if it is equal to one that already exists.
4. If the field is of the form (SPECIAL x), then the value is the address of the
SPECIAL cell on the property list of x. If one does not already exist, it will be created.

<a name="page75">page 75</a>

The SPECIAL cell itself (but not the entire atom) is protected against garbage collection.

5. In all other cases, the field is assumed to be a list of subfields, and their sum
is taken. The subfields must be of types 14 above.

Error Diagnostics
*L 1* Unable to determine origin. No assembly.
*L 2* Out of binary program space. Second pass cancelled.
*L 3 * Undefined symbol. Assembly incomplete.
*L 4* Type five field contains type five fields inside itself. Assembly incomplete.

Opdef ine
opdefine is a pseudo-function for defining new quantities for LAP. It puts a SYM
on the property list of the symbol that is being defined. Its argument is a list of pairs.
Each pair is a symbol and its numerical value. Note that these pairs are not "dotted
pairs.

Example
OPDEFINE ( ( (CLA 500Q8)
(TRA 2Q9)
(LOAD 1000)
(OVBGN 7432Q) ) )
The following op-codes are defined in the standard system:
AXT
CLA
LDQ
LXA
LXD
PAX
PDX
Examples of the Use of LAP
PXA
PXD
STD
ST0
STQ
STR
STZ
SUB
SXA
SXD
TIX
Trn
TNX
TNZ
TRA
TSX
TXH
TXI
TXL
TZE
XCA

Example 1: A LISP function
The predicate greater induces an arbitrary canonical order among atomic symbols.
LAP ( ( (GREATER SUBR 2) (TI& (* 3)) (PXA 0 0)
(TRA 1 4) (CLA (QUOTE *T* ) ) (TRA 1 4) )NIL)
Example 2: A patch

The instruction TSX 6204Q must be inserted after location 62 17Q.^62 17Q contains
CIA 6243Q and this instruction must be moved to the patch.

LAP ( (6217Q (TRA NIL) )NIL)
LAP ( (NIL (CLA A) (TSX 6204Q) (TRA B) )
( (A 6243Q) (B 6220Q) ) )

<a name="page76">page 76</a>

## APPENDIX D : THE LISP COMPILER

The LISP Compiler is a program written in LISP that translates S-expression defi-
nitions of functions into machine language subroutines. It is an optional feature that
makes programs run many times faster than they would if they were to be interpreted
at run time by the interpreter.
When the compiler is called upon to compile a function, it looks for an EXPR or
FEXPR on the property list of the function name. The compiler then translates this
S-expression into an S-expression that represents a subroutine in the LISP Assembly
Language (LAP). LAP then proceeds to assemble this program into binary program
space. Thus an EXPR, or an FEXPR, has been changed to a SUBR or an FSUBR,
respectively.
Experience has shown that compiled programs run anywhere from 10 to 100 times
as fast as interpreted programs, the time depending upon the nature of the program.
Compiled programs are also more economical with memory than their corresponding 
S-expressions, taking only from 50 per cent to 80 per cent as much space.<sup>1</sup>
The major part of the compiler is a translator or function from the S-expression
function notation into the assembly language, LAP. The only reasons why the compiler
is regarded as a pseudo-function are that it calls LAP, and it removes EXPRts and
FEXPR1s when it has finished compiling.

The compiler has an interesting and perhaps unique history. It was developed in
the following steps:

1. The compiler was written and debugged as a LISP program consisting of a set
of S-expression definitions of functions. Any future change or correction to the com-
piler must start with these definitions; therefore they are included in the LISP Library.
2. The compiler was commanded to compile itself. This operation is called boot -
strapping. It takes more than 5 minutes on the IBM 7090 computer to do this, since
most parts of the compiler are being interpreted during most of this time.
3. To avoid having to repeat the slow bootstrapping operation each time a system
tape is created, the entire compiler was punched out in assembly language by using
punc hlap.
4. When a system tape is to be made, the compiler in assembly language is read
in by using readlap.

The compiler is called by using the pseudo-function compile. The argument of compile is a list of the names of functions to be compiled. Each atomic symbol on this list
should have either an EXPR or an FEXPR on its property list before being compiled.
The processing of each function occurs in three steps. First, the S-expression for
the function is translated into assembly language. If no S-expression is found, then the
compiler will print this fact and proceed with the next function. Second, the assembly

-----
1. Since the compiled program is binary program space, which is normally
not otherwise accessible, one gains as free storage the total space formerly occupied
by the S-expression definition.

<a name="page77">page 77</a>

language program is assembled by LAP. Finally, if no error has occurred, then the
EXPR or FEXPR is removed from the property list. When certain errors caused by
undeclared free variables occur, the compiler will print a diagnostic and continue.
This diagnostic will be spuriously produced when programs leaning on APVALs are
compiled.
When writing a large LISP program, it is better to debug the individual function defi-
nitions by using the interpreter, and compile them only when they are known to work.
Persons planning to use the compiler should note the following points:

1. It is not necessary to compile all of the functions that are used in a particular
run. The interpreter is designed to link with compiled functions. Compiled functions
that use interpreted functions will call the interpreter to evaluate these at run time.
2. The order in which functions are compiled is of no significance. It is not even
necessary to have all of the functions defined until they are actually used at run time.
(Special forms are an exception to this rule. They must be defined before any function
that calls them is compiled. )
3. If the form LABEL is used dynamically, the resulting function will not compile
properly.
4. Free variables in compiled functions must be declared before the function is
compiled. This is discussed at length in this appendix.

Excise

The compiler and the assembler LAP can be removed from the system by using
the pseudo-function excise. If excise [NIL] is executed, then the compiler will be
removed. If excise [*T*] is executed, then the compiler and LAP will both be excised.
One may execute excise [NIL] and then excise [*T*] at a later time. When a portion
of the system is excised, the region of memory that it occupied is converted into addi-
tional free-storage space.

Free Variables
A variable is bound in a particular function when it occurs in a list of bound vari-
ables following the word LAMBDA or PROG. Any variable that is not bound is free.

Example

(LAMBDA (A) (PROG (B)
S (SETQ B A)
(COND ((NULL B) (RETURN C)))
(SETQ C (CONS (CAR A) C))
(GO S) ))
A and B are bound variables, C is a free variable.
When a variable is used free, it must have been bound by a higher level function.
If a program is being run interpretively, and a free variable is used without having been
bound on a higher level, error diagnostic *A^89 will occur.

<a name="page78">page 78</a>

If the program is being run compiled, the diagnostic may not occur, and the variable
may have value NIL.
There are three types of variables in compiled functions: ordinary variables,
SPECIAL variables, and COMMON variables. SPECIAL and COMMON variables must
be declared before compiling. Any variable that is not declared will be considered an
ordinary variable.
When functions are translated into subroutines, the concept of a variable is trans-
lated into a location where an argument is stored. If the variable is an ordinary one,
then a storage location for it is set up on the push-down list. Other functions cannot
find this private cell, making it impossible to use it as a. free variable.
SPECIAL variables have the indicator SPECIAL on their property lists. Following
the indicator there is a pointer to a fixed cell. When this variable is bound, the old
value is saved on the push-down list, and the current value is stored in the SPECLAL
cell. When it is no longer bound, the old value must be restored. When a function uses
this variable free, then the quantity in the SPECIAL cell is picked up.
SPECIAL variables are declared by using the pseudo-function special[a], where a
is a list of variable names. This sets up the SPECIAL indicator and creates a SPECIAL
cell. Both the indicator and the cell can be removed by the pseudo-function unspecial[a],
where a is a list of variable names. It is important that the declaration be in effect
at compile time. It may be removed at run time.
The compiler refers to SPECIAL cells, using the LAP field (SPECIAL X) whose
value is the address of the SPECIAL cell. When a variable has been declared, removed,
and then declared again, a new cell is created and is actually a different variable.
SPECIAL variables are inexpensive and will allow free communication among com-
piled functions. They do not increase run time significantly. SPECIAL variables cannot
be communicated between the interpreter and compiled functions.
COMMON variables have the flag COMMON on their property lists; however, this
is only used to inform the compiler that they are COMMON, and is not needed at run
time. COMMON variables are bound on an a-list by the compiled functions. When they
are to be evaluated, is given this a-list. This happens at run time.
The use of COMMON variables will slow down any compiled function using them.
However, they do provide complete communication between interpreted and compiled
functions.
COMMON variables are declared by common[a], where a is a list of variable names.
The declaration can be removed by uncommon[a], where a is a list of variable names.

Functional Constants
Consider the following definition of a function dot by using an S-expression:
(YDOT (LAMBDA (X Y) (MAPLIST X (FUNCTION
(LAMBDA (J) (CONS (CAR J) Y)) ))))

<a name="page79">page 79</a>

Following the word FUNCTION is a functional constant. If we consider it as a sep-
arate function, it is evident that it contains a bound variable "Jtt, and a free variable
"Yfl. This free variable must be declared SPECIAL or COMMON, even though it is
bound in YDOT.

### Functional Arguments

MAPLIST can be defined in S-expressions as follows:
(MAPLIST (LAMBDA (L FN) (COND
((NULL L) NIL)
(T (CONS (FN L) (MAPLIST (CDR L) FN))) )))
The variable FN is used to bind a functional argument. That is, the value of FN
is a function definition. This type of variable must be declared COMMON.

### Link

Link is the routine that creates all linkage of compiled functions at run time.
The normal procedure for calling a compiled function is to place the arguments in
the AC, MQj $ARG3,. .. and then to TSX FN,4. However, the first time any call is
executed, there will be an STR in place of the TSX. The address and the decrement
of the STR specify the name of the function that is being called, and the number of argu-
ments that are being transmitted, respectively. The tag contains a 7. If there is no
SUBR or FSUBR for the function that is being called, then link will call the interpreter
that may find an EXPR or FEXPR. If there is a subroutine available, then link will
form the instruction TSX and plant this on top of the STR.

### Tracing Compiled Functions

- trace will work for compiled functions, subject to the following restrictions.
    1. The trace must be declared after the function has been compiled.

2. Once a direct TSX link is made, this particular calling point will not be traced.
(Link will not make a TSX as long as the called function is being traced. )

<a name="page80">page 80</a>

## APPENDIX E : OVERLORD - THE MONITOR

Overlord is the monitor of the LISP System. It controls the handling of tapes, the
reading and writing of entire core images, the historical memory of the system, and
the taking of dumps.
The LISP System uses 5 tape drives. They are listed here by name together with
their customary addresses.

SYSTAP Contains the System B7
SYSTMP Receives the Core Image B3
SYSPIT Punched Card Input A2
SYSPOT Printed Output A3
SYSPPT Punched Card Output A4
The system tape contains a small bootstrap record with a loader, followed by a very
long record that fills up almost all of the core memory of the machine.
The system is called by the two-card LISP loader which is placed in the on-line card
reader. Octal corrections may be placed between the two cards of this loader. The
format of these will be specified later.
The first loader card causes SYSTAP to be selected and the entire memory is imme-
diately filled. Control then goes to a loader that reads octal correction cards until it
recognizes the second loader card which is a binary transfer card to Overlord.
Overlord reads cards from the input looking for Overlord direction cards. Other
cards are ignored except for the first one which is printed in the output.
Overlord cards either instruct the monitor to perform some specific function or
else signal that a packet of doublets for evaluation is to follow immediately.
Before any packet is read, the entire system is read out onto SYSTMP. It is written
in the same format as SYSTAP, and in fact is a copy of it. After each packet, one of
two things may happen. Either a complete core image is read from SYSTMP, and thus
memory is restored to the condition it was in before the packet was read, or the state
of memory at the finish of the packet is read out onto SYSTMP. In the latter case, all
function definitions and other memory changes are preserved.

Card Format

Octal correction cards can alter up to 4 words of memory per card. Each change
specifies an address (5 octal digits) and a word to be placed there (12 octal digits). The
card columns to use are as follows.

address data word

<a name="page81">page 81</a>

Overlord cards have the Overlord direction beginning in column 8. If the card has
no other field, then comments may begin in column 16. Otherwise, the other fields of
the card begin in column 16 and are separated by commas. The comments may begin
after the first blank past column 16.

### Overlord Cards

#### TAPE SYSPPT, B4

The TAPE Overlord card defines the actual drives to be assigned to the tapes. The
system uses five tapes designated by the names SYSTAP, SYSTMP, SYSPIT, SYSPOT,
and SYSPPT. The actual tape units may range from A0 through C9.

#### SIZE N1, N2, N3, N4

The size card specifies the amount of storage to be allocated to binary program
space, push-down, full words, and free storage in that order. The SIZE card must be
used only once at the time when the system is created from a binary card deck. The
fields are octal or decimal integers.

#### DUMP Ll,L2,0

This Overlord card causes an octal dump of memory to be printed. The first two
fields are octal or decimal integers specifying the range of the dump. The third field
specifies the mode. 0 mode specifies a straight dump. 1 mode specifies that if the
prefix and tag areas of a word are zero, then the complements of the address and decre-
ment are dumped instead.

#### TEST

Specifies that a packet is to follow and that memory is to be restored from SYSTMP
after the packet has been evaluated.

#### TST

Same as TEST

#### SET

The SET card specifies that a packet is to follow and that the memory state following
the evaluation of the packet is to be set onto SYSTMP. If an error occurs during the
evaluation of the packet, then the memory is to be restored from SYSTMP instead.

#### SETSET

The SETSET card is like SET except that it sets even if there has been an error.

#### DEBUG

This direction is like TEST except that after the doublets have been read in the entire
object list is thrown away, making it impossible to do any further reading (except of
numbers). This makes a considerable amount of free storage available but may cause
trouble if certain atoms that are needed are not protected in some manner.

#### FIN

Causes the computer to halt. An end of file mark is written on SYSPOT. An end
of file is written on SYSPPT only if it has been used. If the FIN card was read on-line,
the computer halts after doing these things. If the FIN card came from SYSPIT, then

<a name="page82">page 82</a>

SYSPIT is advanced past the next end of file mark before the halt occurs.

Use of Sense Switches
1 Up-Input comes from SYSPIT.
Down-Input comes from the card reader.
The loader cards and octal correction cards always go on-line.
3 Up-No effect
Down-All output that is written onto either SYSPOT or SYSPPT will also appear
on the on-line printer.
5 Up-No effect
Down-Suppresses output normally written on SYSPOT and SYSPPT.
These switches are interrogated at the beginning of each record.
6 Up-The instruction STR will cause the interpreter to give error diagnostic F 5
and continue with the next doublet.
Down-The instruction STR will cause control to go to Overlord immediately.
The normal terminating condition of a LISP run is an HPR 77777,7 with all bits of
AC and MQ filled with ones. To return control to Overlord from this condition, push
RESET then START.
After a LISP run, the reel of tape that has been mounted on the SYSTMP drive has
become a system tape containing the basic system plus any changes that have been set
onto it. It may be mounted on the SYSTAP drive for some future run to use definitions
that have been set onto it.

<a name="page83">page 83</a>

## APPENDIX F : LISP INPUT AND OUTPUT

This appendix describes the LISP read and write programs and the character-
manipulation programs. The read and write programs allow one to read and write
S-expressions. The character-manipulating programs allow one to read and write indi-
vidual characters, to process them internally, to break atomic symbols into their con-
stituent characters, and to form atomic symbols from individual characters.
The actual input/output routines are identical for both the LISP read and write, and
the character read and write. Input is always from either SYSPIT or the card reader.
Printed output is always written on SYSPOT and/or the on-line printer. Punched output
is always on SYSPPT and/or the on-line printer. The manner in which these choices
are controlled was described in Appendix E.

LISP READ PRINT and PUNCH

The LISP read program reads S-expressions in the form of BCD characters and
translates them into list structures. It recognizes the delimiters ll(lland'l)ll and the
separators. It, and (blank). The comma and blank are completely equivalent.
An atomic symbol, when read in, is compared with existing atomic symbols. If it
has not been encountered previously, a new atomic symbol with its property list is
created. All atomic symbols except numbers and gensyms are on a list called the object
list. This list is made of sublists called buckets. The atomic symbols are thrown into
buckets by a hash process on their names. This speeds up the search that must occur
during reading.
For the purpose of giving a more extended definition of an atomic symbol than was
given in Section I, the 48 BCD characters are divided into the following categories.
ClassA ABC... Z=*/
ClassB 0 12 34 5 6 78 9t -(ll punch)
Class C ( ) ,. (blank)
Class D $
Class E - (4-8 punch)
The 4-8 punch should not be used.
Symbols beginning with a Class B character are interpreted as numbers. Some
sort of number conversion is attempted even if it does not make sense.
An ordinary atomic symbol is a sequence of up to 30 characters from classes A, B,
and Dj with the following restrictions.
a. The first character must not be from class B.
b. The first two characters must not be $ $.
c. It must be delimited on either side by a character from class C.
There is a provision for reading in atomic symbols containing arbitrary characters.

<a name="page84">page 84</a>

This is done by punching the form $$dsd, where s is any string of up to 30 characters,
and d is any character not contained in the string s. Only the string s is used in
forming the print name of the atomic symbol; d and the dollar signs will not appear when
the atomic symbol is printed out.

Examples
Input will print as
$ $XAX A
$ $O))( 1))
$ $-w. )- IJV*)
$$/-*I -.
The operation of the read program is critically dependent upon the parsing of left
and right parentheses. If an S-expression is deficient in one or more right parentheses, '
reading will continue into the next S-expression. An unmatched right parenthesis, or a
dot that is out of context, will terminate reading and cause an error diagnostic.
The read program is called at the beginning of each packet to read doublets for
evalquote until it comes to the S-expression STOP. read may also be used explicitly
by the programmer. In this case, it will begin reading with the card following the STOP
card because the read buffer is cleared by evalquote after the doublets and STOP have
been read. After this, card boundaries are ignored, and one S-expression is read each
time read is called. read has no arguments. Its value is the S-expression that it reads.
The pseudo-functions print and punch write one S-expression on the printed or
pwched output, respectively. In each case, the print or punch buffer is written out
and cleared so that the next section of output begins on a new record.
prinl is a pseudo-function that prints its argument, which must be an atomic sym-
bol, and does not terminate the print line (unless it is full).
terpri prints what is left in the print buffer, and then clears it.

### Characters and Character Objects

Each of the sixty-four 6-bit binary numbers corresponds to a BCD character, if we
include illegal characters. Therefore, in order to manipulate these characters via LISP
functions, each of them has a corresponding object. Of the 64 characters, 48 corre-
spond to characters on the key punch, and the key-punch character is simply that
character. The print names of the remaining characters will be described later. When
a LISP function is described which has a character as either value or argument, we
really mean that it has an object corresponding to a character as value or argument,
respectively.
The first group of legal characters consists of the letters of the alphabet from A
to Z. Each letter is a legitimate atomic symbol, and therefore may be referred to in
a straightforward way, without ambiguity.
The second group of legal characters consists of the digits from 0 to 9. These
must be handled with some care because if a digit is considered as an ordinary integer

<a name="page85">page 85</a>

rather than a character a new nonunique object will be created corresponding to it, and
this object will not be the same as the character object for the same digit, even though
it has the same print name. Since the character-handling programs depend on the char-
acter objects being in specific locations, this will lead to error.
The read program has been arranged so that digits 0 through 9 read in as the cor-
responding character objects. These may be used in arithmetic just as any other num-
ber but, even though the result of an arithmetic operation lies between 0 and 9, it will
not point to the corresponding character object. Thus character objects for 0 through
9 may be obtained only by reading them or by manipulation of print names.
The third group of legal characters is the special characters. These correspond
to the remaining characters on the key punch, such as $ and "= ". Since some of these
characters are not legitimate atomic symbols, there is a set of special character-value
objects which can be used to refer to them.
A typical special character -value object, say DOLLAR, has the following structure

* 1 PNAME I APVAL
    I I

Thus "DOLLAR has value " $ ".
The special character value objects and their permanent values are:
DOLLAR
SLASH
LPAR
RPAR
COMMA
PERIOD
PLUSS
DASH
STAR
BLANK
EQSIGN


)

i

* (11 punch)

*

blank
  - -

The following examples illustrate the use of their objects and their raison datre.
Each example consists of a doublet for evalquote followed by the result.

Examples

EVAL (DOLLAR NIL) value is " $
EVAL ((PRINT PERIOD) NIL) value is ". and If. is also printed.

<a name="page86">page 86</a>

The remaining characters are all illegal as far as the key punch is concerned. The
two characters corresponding to 12 and 72 have been reserved for end-of-file and end-
of-record, respectively, The end-of-file character has print name $EOF$ and the end-
of-record character has print name $EOR $; corresponding to these character objects
are two character value objects EOR and EOF, whose values are $EOR $ and $EOF$
respectively. The rest of the illegal character objects have print names corresponding
to their octal ~epresentations preceded by $IL and followed by $. For instance, the
character 77 corresponds to a character object with print name $IL77$.
The character objects are arranged in the machine so that their first cells occupy
successive storage locations. Thus it is possible to go from a character to the corre-
sponding object or conversely by a single addition or subtraction. This speeds up
character-handling considerably, because it isn't necessary to search property lists
of character objects for their print names; the names may be deduced from the object
locations.

### Packing and Unpacking Characters

When a sequence of characters is to be made into either a print name or a numerical
object, the characters must be put one by one into a buffer called BOFFO. BOFFO is
used to store the characters until they are to be combined. It is not available explicitly
to the LISP programmer, but the character-packing functions are described in terms
of their effects on BOFFO. At any point, BOFFO contains a sequence of characters.
Each operation on BOFFO either adds another character at the end of the sequence or
clears BOFFO, i. e., sets BOFFO to the null sequence. The maximum length of the
sequence is 120 characters; an attempt to add more characters will cause an error.
The character-packing functions are:

1. pack [c] - : SUBR pseudo-function
    The argument of packmust be a character object. - pack adds the character
    c at the end of the sequence of characters in BOFFO. The value of pack - is NIL.
2. clearbuff [ ] SUBR pseudo -func tion
clearbuff is a function of no arguments. It clears BOFFO and has value NIL.
The contents of BOFFO are undefined until a clearbuff has been performed.
3. mknam [ ] SUBR pseudo -function
mknam is a function of no arguments. Its value is a list of full words con-
taining the characters in BOFFO in packed BCD form. The last word is filled
out with the illegal character code 77 if necessary. After mknam is performed,
BOFFO is automatically cleared. Note that intern [mknam[ ]I yields the object
whose print name is in BOFFO.
4. numob [ ] ' SUBR pseudo -function
    numob is a function of no arguments. Its value is the numerical object repre-
    sented by the sequence of characters in BOFFO. (Positive decimal integers from
    0 to 9 are converted so as to point to the corresponding character object. )

<a name="page87">page 87</a>

5. unpack [x]: SUBR pseudo-function
    This function has as argument a pointer to a full word. unpack considers
    the full word to be a set of 6 BCD characters, and has as value a list of these
    char act er s ignoring all characters including and following the first 77.
6. h~tern[~name] : SUBR pseudo-function
    This function has as argument a pointer to a PNAME type structure such as -

Its value is the atomic symbol having this print name. If it does not already
exist, then a new atomic symbol will be created.

### The Character-Classifying Predicates

*. - liter [c]: SUBR predicate
    * liter has as argument a character object. Its value is T if the character
is a letter of the alphabet, and F otherwise.
*. - digit [c]: SUBR predicate

- digit has as argument a character object. Its value is T if the character
is a digit between 0 and 9, and F otherwise.

3. opchar [c]: SUBR predicate
opchar has as argument a character object. Its value is T if the character
is t, -, /, *, or =, and F otherwise. opchar treats both minus signs equiva-
lently.
*. - dash [c]: SUBR predicate

- dash has as argument a character object. Its value is T if the character
is either an 11-punch minus or an 84 punch minus, and F otherwise.

### The Character -Reading Functions

The character-reading functions make it possible to read characters one by one from
input.
There is an object CURCHAR whose value is the character most recently read (as
an object). There is also an object CHARCOUNT whose value is an integer object giving
the column just read on the card, i. e., the column number of the character given by
CURCHAR. There are three functions which affect the value of CURCHAR:

#### 1. startread [ ]  : SUBR ps eudo-function
startread is a function of no arguments which causes a new card to be read. The value of startread is the first character on that card, or more precisely,

<a name="page88">page 88</a>

the object corresponding to the first character on the card. If an end-of-file
condition exists, the value of startread is $EOF$. The value of CURCHAR
becomes the same as the output of startread, and the value of CHARCOUNT
becomes 1. Both CURCHAR and CHARCOUNT are undefined until a startread
is performed. A startread may be performed before the current card has been
completely read.

#### 2. advance [ ] : SUBR pseudo -function

advance is a function of no arguments which causes the next character to be read. The value of advance is that character. After the 72nd character on the card has been read, the next advance will have value $EOR$. After reading $EOR$, the next advance will act like a startread, i. e., will read the first char acter of the next card unless an end-of-file condition exists. The new value of CURCHAR is the same as the output of advance; executing advance also increases the value of CHARCOUNT by 1. However, CHARCOUNT is undefined when CURCHAR is either $EOR $ or $EOF $.
    
#### 3. endread [ ] : SUBR pseudo-function

endread is a function of no arguments which causes the remainder of the card to be read and ignored. endread sets CURCHAR to $EOR$ and leaves CHARCOUNT undefined; the value of endread is always $EOR $. An advance following endread acts like a startread. If CURCHAR already has value $EOR $ and endread is performed, CURCHAR will remain the same and endread will, as usual, have value $EOR $.

### Diagnostic Function

#### error 1 [ ]: SUBR pseudo-function

error1 is a function of no arguments and has value NIL. It should be executed
only while reading characters from a card (or tape). Its effect is to mark the char-
acter just read, i. e., CURCHAR, so that when the end of the card is reached, either
by successive advances or by an endread, the entire card is printed out along with
a visual pointer to the defective character. For a line consisting of ABCDEFG fol-
lowed by blanks, a pointer to C would look like this:

```
  v
ABCDEFG
  A
```
If error 1 is performed an even number of times on the same character, the A will
not appear. If error1 is performed before the first startread or while CURCHAR
has value $EOR $ or $EOF $, it will have no effect. Executing a startread before
the current card has been completed will cause the error1 printout to be lost. The
card is considered to have been completed when CURCHAR has been set to $EOR$.
Successive endreads will cause the error l printout to be reprinted. Any number
of characters in a given line may be marked by error1.

<a name="page89">page 89</a>

## APPENDIX G : MEMORY ALLOCATION AND THE GARBAGE COLLECTOR

The following diagram shows the way in which space is allocated in the LISP System.

| Address (octal) | Assigned to                                                  |
| --------------- | ------------------------------------------------------------ |
| 77777           | -----                                                        |
|                 | Loader                                                       |
| 77600           | -----                                                        |
|                 | LAP                                                          |
|                 | Compiler                                                     |
| 70000           | -----                                                        |
|                 | Free storage                                                 |
|                 | Full words                                                   |
|                 | Pushdown list                                                |
|                 | Binary program space                                         |
| 17000           |                                                              |
|                 | Interpreter, I/O, Read Print, Arithmetic, Overlord, Garbage Collector, and other system coding |
| 00000           |                                                              |

The addresses in this chart are only approximate. The available space is divided
among binary program space, push-down list, full-word space, and free-storage space
as specified on the SIZE card when the system is made.

When the compiler and LAP are not to be used again, they may be eliminated by
executing the pseudo-function excise. This part of the memory is then converted into
free storage.

Free storage is the area in the computer where list structures are stored. This
includes the property lists of atomic symbols, the definitions of all EXPRts and
FEXPR1s, evalquote doublets waiting to be executed, APVALts, and partial results of
the computation that is in progress.

Full-word space is filled with the BCD characters of PNAMEts, the actual numbers

<a name="page90">page 90</a>

of numerical atomic structures, and the TXL words of SUBRtsB FSUBRts, and SYMts.
All available words in the free-storage area that are not in use are strung together
in one long list called the free-storage list. Every time a word is needed (for example,
by s) the first word on the free-storage list is used, and the free-storage list is set
to & of what it formerly was.

Full-word space is handled in the same way. No use is made of consecutive storage
in either of these areas of memory. They are both scrambled. 

When either of these lists is exhausted in the middle of a computation, the garbage
collector is called automatically. Unless the computation is too large for the system,
there are many words in free storage and full-word space that are no longer needed.
The garbage collector locates these by marking those words that are needed. In free
storage, the sign bit is used for marking. In full-word space, there is no room in the
word itself. Marking is done in a bit table which is next to full-word space.

Since it is important that all needed lists be marked, the garbage collector starts
marking from several base positions including the following:

1. The object list that includes all atomic symbols except numbers and generated names. This protects the atomic symbols, and all S-expressions that hang on the property lists of atomic symbols.
2. The portion of the push-down list that is currently being used. This protects partial results of the computation that is in progress.
3. The temlis, which is a list of registers scattered throughout the memory where binary programs store list structures that must be protected.

Marking proceeds as follows. If the cell is in full-word space, then the bit table
is marked. If the cell is in free storage, then the sign is set minus, and car and &
of the cell are marked. If the cell is anywhere else, then nothing is done.
After the marking is done, the new available word lists are made by stringing all
unmarked words together. Finally, the signs in free storage are set plus.

A garbage collection takes approximately 1 second on the IBM 7090 computer. It
can be recognized by the stationary pattern of the MQ lights. Any trap that prevents
completion of a garbage collection will create a panic condition in memory from which
there is no recovery.

<a name="page91">page 91</a>

## APPENDIX H : RECURSION AND THE PUSH-DOWN LIST

One of the most powerful resources of the LISP language is its ability to accept
function definitions that make use of the very function that is being defined. This may
come about either directly by using the name of the function, or indirectly through a
chain of function definitions that eventually return to the original ones. A definition of
this type is called recursive. Its power lies in its ability to define an algorithm in
terms of itself.

A recursive definition always has the possibility of not terminating and of being
infinitely regressive. Some recursive definitions may terminate when given certain
inputs and not terminate for others. It is theoretically impossible to determine whether
a definition will terminate in the general case; however, it is often possible to show
that particular cases will or will not terminate.

LISP is designed in such a way that all functions for which the possibility of recursion
can exist are in fact recursive. This requires that all temporary stored results related
to the computation that is in progress be set aside when a piece of coding is to be used
recursively, and that they be later restored. This is done autorrlatically and need not
be programmed explicitly.

All saving of temporary results in LISP is performed on a linear block of storage
called the push-down list. Each set of stored data that is moved onto the push-down
list is in a block labeled with its size and the name of the subroutine from which it came.
Since it is in the nature of recursion that the first block to be saved is always the last
block to be restored, it is possible to keep the push-down list compact.

The frontier of the push-down list can always be found by referring to the cell CPPI.
The decrement of this cell contains the complementary address of the first available
unused location on the push-down list. Index register 1 also contains this quantity,
except during certain nonrecursive subroutines; in these last cases it must be restored
upon leaving these routines.

There are two types of blocks to be found on the push-down list, those put there by
SAVE, and those put there by MOVE. SAVE blocks are moved from fixed locations
in certain subroutines onto the push-down list, and then moved back to the place where
they came from by UNSAVE. Each block contains parameters that tell UNSAVE how
many words are to be moved, and where they are to be moved to.

Functions compiled by the LISP compiler do not make use of storage cells located
near the actual programming. All data are stored directly on the push-down list and
referenced by using index register 1. MOVE is used to update CPPI and index regis-
ter 1, to place the arguments on the push-down list, and to set up the parameters for
the push-down block.

Because pointers to list structures are normally stored on the push-down list, the

<a name="page92">page 92</a>

garbage collector must mark the currently active portion of the push-down list during a
garbage collection. Sometimes quantities are placed on the push- down list which should
not be marked. In this case, the sign bit must be negative. Cells on the active portion
of the push-down list having a negative sign bit will not be marked.

When an error occurs, an examination of the push-down list is an excellent indica-
tion of what was occurring at the time of the error. Since each block on the push-down
list has the name of the function to which it belongs, it is possible to form a list of
these names. This is called the backtrace, and is normally printed out after error
diagnostics.

<a name="page93">page 93</a>

## APPENDIX I : LISP FOR SHARE DISTRIBUTION

The Artificial Intelligence Project at Stanford University has produced a version of
LISP 1.5 to be distributed by SHARE. In the middle of February 1965 the system is
complete and is available from Stanford. The system should be available from SHARE
by the end of March 1965.

SHARE LISP differs somewhat from the LISP 1.5 system described in the LISP 1.5
Programmer's Manual, but only in (generally) inessential details. It is hoped that
the changes will be widely hailed as improvements.

### Verbos and the Garbage Collector

The garbage collector now prints its message in a single-spaced format; thus, the
amount of paper generated by a program with many constes is somewhat less than for-
merly. Furthermore, the garbage collector printout may be suspended by executing
"VERBOS(N1L)"; and the printout may be reinstated by executing flVERBOS(*T*)tI.

### Flap Trap

Every now and then a state of affairs known as floating-point trap occurs - this re-
sults when a floating-point arithmetic instruction generates a number whose exponent
is too large in magnitude for the eight-bit field reserved for it. When this trap oc-
curs and the offending exponent is negative, the obvious thing to do is to call the re-
sult zero. The old system, however, simply printed out a "FLAP TRAPtt error mes-
sage and went on to the next pair of S-expressions to be evaluated. The new system
stores a floating -point zero in the accumulator when an underflow occurs. (There
has, as yet, been no request to have "infinityIt stored in the accumulator when an
overflow occurs.)

### Time

The new system prints the time upon entering and leaving evalquote. In fact, two
times are printed, but in a neat, concise, impersonal manner which, it is felt, is
more suitable to the "age of automationu than the quote from Lewis Carroll. The
times are printed in minutes and milliseconds; the first time is the age of the packet -
by definition, this is zero when evalquote is first entered - and the second time is
the age of the system being used. Thus, when evalquote is left, the time printout
tells how much time was spent in the execution of the packet and how much time has
been spent in execution of SET or SETSET packets since the birth of the system plus
the time spent in the packet being finished. This time printout, to be meaningful,
requires the computer to have a millisecond clock in cell 5 (RPQ F 89349, with mil-
lisecond feature).

<a name="page94">page 94</a>

It is also possible to determine how much time is required to execute a given func-
tion. llTIMEl()lt initializes two time cells to zero and prints out, in the same format
that is used for the evalquote time printout, two times, and these are both zero.
prints (again in the evalquote time printout format) the time since the last
execution of "TIME()" and the time since the last execution of "TIMEl()". The use
of the time and time1 functions has no effect on the times recorded by evalquote.

### Lap and Symtab

Heretofore, lap has not only returned the symbol table as its value but has printed
it out as well. This phenomenon is familiar to those who have much at all to do with

-- lap or the compiler. The lap in the new system always prints the function name and
the octal location in which the first word of the assembled function is stored. (If the
assembled function is not a SUBR or FSUBR, then only the octal origin of the as-
sembled code is printed.) The printout is left-justified on the output page and has the
form tl<function name) (ORIGIN xxx~xQ)~l.
The value of - lap is still the symbol table, but the printing of the symbol table may
be suspended by executing llSYMTAB(NIL)lt; and the printing may be restored by ex-
ecuting uSYMTAB(*T*)ll.

### Non-Printing Compiler

The problem of the verbosity of the compiler is only slightly abated by the symtab
function. The remainder of the trouble may be cured by executing "LISTING(NIL)ll.
This turns off the printout of the lap code generated by the compiler. And, of course,
the printout may be reinstated by executing llLISTING(*T*)ll. Thus, for a perfectly
quiet compilation (except for the origin printout by lap), one need only execute
I1SYMTAB(NIL)l1 and LISTING(NIL)I1 before compiling.

### Tracecount (Alarm-Clock Trace)

The trace feature of LISP is quite useful; but, with very little encouragement, it
can be made to generate wastebaskets full of useless output. Often a programmer
will find that his output (without tracing) consists of many lines of garbage collector
printout, an error message, and a few cryptic remarks concerning the condition
of the push-down list at the time the error occurred. In such a situation, one wishes
he could begin tracing only a short time before the occurrence of the error. The
tracecount function permits exactly this. " TRACECOUNT(X)  causes the tracing
(of those functions designated to be traced by the trace function call) to begin
after x number of function entrances. Furthermore, when the tracecount mecha-
nism has been activated, by execution of ltTRACECOUNT(x)ll, some of the blank
space in the garbage collector printout will be used to output the number of function
entrances which have taken place up to the time of the garbage collection; each time

<a name="page95">page 95</a>

the arguments or value of a traced function are printed the number of function en-
trances will be printed; and if an error occurs, the number of function entrances ac-
complished before the error will be printed.

The tracecount feature (or alarm-clock trace, as it is called by Marvin Minsky of
M. I. T.) enables a programmer to run a job (preceding the program by "TRACE-
COUNT(O)It), estimate the number of function entrances that occur before the pro-
gram generates an error condition or a wrong answer, and then run the job again,
tracing only the pertinent portion of the execution.

### Space and Eject

A small amount of additional control over the form of the data printed by LISP has
been provided in the space and eject functions.

ttSPACE(*T*)tt causes all output to be double-spaced. nSPACE(NIL)u restores the
spacing to its original status; in particular, the output of the print routine reverts
to single -spacing, and the "END OF EVALQUOTE OPERATORnt printout again ejects
the page before printing.

"EJECT()tt causes a blank line with a carriage control character of 1 to be printed
on the output tape. The result is a skip to the top of the next page of output.

### Untime

This routine is not available to the programmer, but its mention here may prevent
some anxiety. In the event that the program time estimate is exceeded during system
I/O, using the old system, one finds himself in the position of having part of one sys-
tem and part of another stored in core or on the SYSTMP. This situation would be
intolerable if the programmer were trying to save some definitions so that he could
use them later. To avoid this unpleasantness, the system I/O routines have been
modified so that the clock is, in essence, turned off during system 1/0 and three
seconds is automatically added to the elapsed time at the conclusion of the read or
write operation (in a machine with a millisecond core clock this is the case - ma-
chines with 1/60 second core clocks add 50 seconds, but this is easily changed). A
clock trap that would normally have occurred during the execution of the read or
write will be executed before the I/O operation takes place.

### Tape

A few programmers with very large programs have long bemoaned the inability
of LISP to communicate between different systems. The functions tape, -- rewind,
-- mprht, mread, and backspace have been designed to alleviate this difficulty.
ttTAPE(s)tt, where s is a list, allows the user to specify up to ten scratch tapes;
if more than ten are specified, only the first ten are used. The value of tape is its
argument. The initial tape settings are, from one to ten, A4, A5, A6, A7, A8, B2,

<a name="page96">page 96</a>

B3, B4, B5, B6. The tapes must be specified by the octal number that occurs in the
address portion of a machine-language instruction to rewind that tape; that is, a four-
digit octal number is required - the first (high-order) digit is a 1 if channel A is de-
sired, 2 if channel B is desired; the second digit must be a 2; the third and fourth
are the octal representation of the unit number. Thus, to specify that scratch tapes
one, two, and three are to be tapes A4, B1, and A5, respectively, execute "TAPE
((1204Q 2201Q 1205Q))I1. Only the low-order fifteen bits of the numbers in +he tape
list are used by the tape routines, so it is possible to use decimal integers or floating-
point numb.ers in the tape list without generating errors.

### Rewind

llREWIND(x)w rewinds scratch tape x, as specified in the most recently exe-
cuted tape function. For example, if the last tape function executed was 'ITAPE
((1 204Q 2201Q))n, then wREWIND(2)11 will cause tape B1 to be rewound. The value
of rewind is NIL.

### Mprint

"MPRINT(x s)I1 prints the S-expression s on scratch tape x. The format of
the output is identical to the normal LISP output, except that sequence numbers are
printed in the rightmost eight columns of the output line and the output line is only
80 characters long (the scratch tape output is generated by the punch routine), and
is suitable for punching or being read by mread. The value of mprint is the list
printed.

### Mread

NMREAD(x)lq reads one S-expression from scratch tape x. The value of mread is
the S-expression read.

### Backspace

llBACKSPACE(x)" causes scratch tape x to be backspaced one record. Cau-
tion in the use of this function is recommended, for if an S-expression to be read
from tape contains more than 72 characters, then it will occupy more than one record
on the tape, and single backspace will not move the tape all the way back to the be-
ginning of the S-expression. The value of backspace is NIL.

### Evalquote

Evalquote is available to the programmer as a LISP function - thus, one may now
write I1(EVALQUOTE APPEND ((A)(B C D)))I1, rather than "(EVAL (QUOTE (APPEND
(A)(B C D))) NIL)", should one desire to do so.

<a name="page97">page 97</a>

### Backtrace

This function was copied (not quite literally) from M. I. T.'s LISP system on the
time-shared 7094. Backtrace is a function of no arguments in which the manner of
specifying the no arguments constitutes, in effect, an argument. The first call of
backtrace, with any argument, or with none, suspends backtrace printouts when er-
rors occur. Thereafter, the value of "BACKTRACE NILu is the backtrace for the
most recent error; and "BACKTRACE xtl, for x not NIL, restores the backtrace
printout to the error routine. Backtrace should always be evaluated by evalquote.

### Read-In Errors

A common cause of free-storage or circular list printouts is an error (in paren-
thesis count, usually) during the initial read-in of a packet. The new system causes
the accumulator to be cleared if an error occurs during the initial read-in, so that
the contents of the accumulator are printed as ttNIL1t.

### Obkeep

Anyone desperate for a few more words of free storage may make up a list, s, of
all atom names he wants to retain in his personal LISP systems, then execute (in a
SET packet) "OBKEEP(s)". All atoms except those which are members of s will be
eliminated from the object list.

### Reserved

"RESERVED NILtt prints the names of the atoms on the object list in alphabetical
order, along with the indicators (not alphabetized, and flags may be missed) on their
property lists. This function should help to solve some of the problems that arise
involving mysterious behavior of compiled functions that worked fine when inter-
preted.

### Gensym and Symnam

Gensym now omits leading zeroes from the numeric portions of the print-names of
the symbols it generates; thus, what once looked like ltGOOOO1tt now prints as ltGln.
Furthermore, it is possible to specify a heading word of from zero to six characters
for the gensym symbols by executing symnam. vSYMNAM(NIL)tl causes LISP-
generated symbols to have purely numeric print-names (but they are not numbers).
The numeric portions of the print-names are truncated from the left so as not to
overlap the heading characters. Thus, It SY MNAM(AAAAA)" causes gensym to produce
distinct atoms with the following (not necessarily distinct) print-names: AAAAA1,
AAAAA2,... , AAAAA9, AAAAAO, AAAAA1,.... The argument of symnam
must have the indicator PNAME on its property list. tlSYMNAM(l 2)lt will cause un-
defined results.

For the convenience of those who find it difficult to get along with the tlCONDn form
of the conditional statement, the following "IF" forms are provided in the new system.

<a name="page98">page 98</a>

"IF (a THEN b ELSE c)I1 and "IF (a b c)I1 are equivalent to nCOND ((a b)(T c))". "IF
(a THEN b)n and "IF (a b)" are equivalent to "COND ((a b))".

"FOR (index i s u dl... dn)", for n less than 17, sets index to the value of i
and skips out of the following loop as soon as the value of u is not NIL: evaluate u,
evaluate dl,... , evaluate dn, evaluate s, go to the beginning of the loop. If i, s,
and u are numbers, then the for - statement is similar to the ALGOL "for index = i
step s until u do begin dl... dn endN. The value of the for statement is the value
of dn the last time it was evaluated. The final value of index is available outside
the for function because cset is used to set the index.

### Sublis

Sublis has been re-hand-compiled so that it behaves as if it were defined as fol-
lows :

```
null[x] -> e
 eq[caar[x];e] -> cdar[x]
T -> subb[cdr[x]]

111~~1;

###### T -

```
lambda[[u;v]; [
and[equal[car[e];u];equal[cdr[e];v]] -c e;
T -. cons[u;v]
]][suba[car [el]; suba[cdr [el]]
111CeI~
The differences between the new sublis and the old one, as far as the programmer is
concerned, are that the new model is faster and the result shares as much storage
as possible with e.


Characteristics of the System

The set-up deck supplied with the SHARE LISP system produces a system tape
with the following properties:

<a name="page99">page 99</a>

Size (in words) -
Binary Program Space 14000 octal
Push-Down List 5000 octal
Full-Word Space 4220 octal
Free Storage 22400 octal
System Tape (SYSTAP) B7
System Temporary Tape (SYSTMP) B6
System Input Tape (SYSPIT) A2
System Output Tape (SYSPOT) A3
System Punch Tape (SYSPPT) A3

The console switches may be used to obtain special results:

SW1 on for LISP input from on-line card reader
SW2 has no effect
SW3 on for LISP output on on-line printer
SW4 has no effect
SW5 on to suppress SYSPOT output
SW6 on to return to overlord after accumulator printout resulting from
error *I? 5*. SW6 off for error printout.

<a name="page100">page 100</a>

## Index to function descriptions

| Function     | Call type  | Implementation   | Pages                        |
|--------------|------------|------------------|------------------------------|
| ADD1         | SUBR       |                  | [26](#page26), [64](#page64) |
| ADVANCE      | SUBR       | PSEUDO-FUNCTION  | [88](#page88)        |
| AND          | FSUBR      | PREDICATE        | [21](#page21), [58](#page58) |
| APPEND       | SUBR       |                  | [11](#page11), [61](#page61) |
| APPLY        | SUBR       |                  | [70](#page70)        |
| ARRAY        | SUBR       | PSEUDO-FUNCTION  | [27](#page27), [64](#page64) |
| ATOM         | SUBR       | PREDICATE        | [3](#page3), [57](#page57) |
| ATTRIB       | SUBR       | PSEUDO-FUNCTION  | [59](#page59)        |
| BLANK        | APVAL      |                  | [69](#page69), [85](#page85) |
| CAR          | SUBR       |                  | [2](#page2), [56](#page56) |
| CDR          | SUBR       |                  | [3](#page3), [56](#page56) |
| CHARCOUNT    | APVAL      |                  | [69](#page69), [87](#page87) |
| CLEARBUFF    | SUBR       | PSEUDO-FUNCTION  | [86](#page86)        |
| COMMA        | APVAL      |                  | [69](#page69), [85](#page85) |
| COMMON       | SUBR       | PSEUDO-FUNCTION  | [64](#page64), [78](#page78) |
| COMPILE      | SUBR       | PSEUDO-FUNCTION  | [64](#page64), [76](#page76) |
| CONC         | FEXPR      |                  | [61](#page61)        |
| COND         | FSUBR      |                  | [18](#page18)        |
| CONS         | SUBR       |                  | [2](#page2), [56](#page56) |
| COPY         | SUBR       |                  | [62](#page62)        |
| COUNT        | SUBR       | PSEUDO-FUNCTION  | [34](#page34), [66](#page66) |
| CP1          | SUBR       |                  | [66](#page66)        |
| CSET         | EXPR       | PSEUDO-FUNCTION  | [17](#page17), [59](#page59) |
| CSETQ        | FEXPR      | PSEUDO-FUNCTION  | [59](#page59)        |
| CURCHAR      | APVAL      |                  | [69](#page69), [87](#page87) |
| DASH         | SUBR       | PREDICATE APVAL  | [85](#page85), [87](#page87) |
| DEFINE       | EXPR       | PSEUDO-FUNCTION  | [15](#page15), [18](#page18), [58](#page58) |
| DEFLIST      | EXPR       | PSEUDO-FUNCTION  | [41](#page41), [58](#page58) |
| DIFFERENCE   | SUBR       |                  | [26](#page26), [64](#page64) |
| DIGIT        | SUBR       | PREDICATE        | [87](#page87)        |
| DIVIDE       | SUBR       |                  | [26](#page26), [64](#page64) |
| DOLLAR       | APVAL      |                  | [69](#page69), [85](#page85) |
| DUMP         | SUBR       | PSEUDO-FUNCTION  | [67](#page67)        |
| EFFACE       | SUBR       | PSEUDO-FUNCTION  | [63](#page63)        |
| ENDREAD      | SUBR       | PSEUDO-FUNCTION  | [8 8](#page8 8)      |
| EOF          | APVAL      |                  | [69](#page69), [88](#page88) |
| EOR          | APVAL      |                  | [69](#page69), [88](#page88) |
| EQ           | SUBR       | PREDICATE        | [3](#page3), [23](#page23), [57](#page57) |
| EQSIGN       | APVAL      |                  | [69](#page69), [85](#page85) |
| EQUAL        | SUBR       | PREDICATE        | [11](#page11), [26](#page26), [57](#page57) |
| ERROR        | SUBR       | PSEUDO-FUNCTION  | [32](#page32), [66](#page66) |
| ERROR1       | SUBR       | PSEUDO-FUNCTION  | [88](#page88)        |
| ERRORSET     | SUBR       | PSEUDO-FUNCTION  | [35](#page35), [66](#page66) |
| EVAL         | SUBR       |                  | [71](#page71)        |
| EVLIS        | SUBR       |                  | [71](#page71)        |
| EXCISE       | SUBR       | PSEUDO-FUNCTION  | [67](#page67), [77](#page77) |
| EXPT         | SUBR       |                  | [26](#page26), [64](#page64) |
| F            | APVAL      |                  | [22](#page22), [69](#page69) |
| FIXP         | SUBR       | PREDICATE        | [26](#page26), [64](#page64) |
| FLAG         | EXPR       | PSEUDO-FUNCTION  | [41](#page41), [60](#page60) |
| FLOATP       | SUBR       | PREDICATE        | [26](#page26), [64](#page64) |
| FUNCTION     | FSUBR      |                  | [21](#page21), [71](#page71) |
| GENSYM       | SUBR       |                  | [66](#page66)        |
| GET          | SUBR       |                  | [41](#page41), [59](#page59) |
| GO           | FSUBR      | PSEUDO-FUNCTION  | [30](#page30), [72](#page72) |
| GREATERP     | SUBR       | PREDICATE        | [26](#page26), [64](#page64) |
| INTERN       | SUBR       | PSEUDO-FUNCTION  | [67](#page67), [87](#page87) |
| LABEL        | FSUBR      |                  | [8](#page8), [18](#page18), [70](#page70) |
| LAP          | SUBR       | PSEUDO-FUNCTION  | [65](#page65), [73](#page73) |
| LEFTSHIFT    | SUBR       |                  | [27](#page27), [64](#page64) |
| LENGTH       | SUBR       |                  | [62](#page62)        |
| LESSP        | SUBR       | PREDICATE        | [26](#page26), [64](#page64) |
| LIST         | FSUBR      |                  | [57](#page57)        |
| LITER        | SUBR       | PREDICATE        | [87](#page87)        |
| LOAD         | SUBR       | PSEUDO-FUNCTION  | [67](#page67)        |
| LOGAND       | FSUBR      |                  | [27](#page27), [64](#page64) |
| LOGOR        | FSUBR      |                  | [26](#page26), [64](#page64) |
| LOGXOR       | FSUBR      |                  | [27](#page27), [64](#page64) |
| LPAR         | APVAL      |                  | [69](#page69), [85](#page85) |
| MAP          | SUBR       | FUNCTIONAL       | [63](#page63)        |
| MAPCON       | SUBR       | FUNCTIONAL PSEUDO- FUNCTION | [63](#page63)        |
| MAPLIST      | SUBR       | FUNCTIONAL       | [20](#page20), [21](#page21), [63](#page63) |
| MAX          | FSUBR      |                  | [26](#page26), [64](#page64) |
| MEMBER       | SUBR       | PREDICATE        | [11](#page11), [62](#page62) |
| MIN          | FSUBR      |                  | [26](#page26), [64](#page64) |
| MINUS        | SUBR       |                  | [26](#page26), [63](#page63) |
| MINUSP       | SUBR       | PREDICATE        | [26](#page26), [64](#page64) |
| MKNAM        | SUBR       |                  | [86](#page86)        |
| NCONC        | SUBR       | PSEUDO-FUNCTION  | [62](#page62)        |
| NIL          | APVAL      |                  | [22](#page22), [69](#page69) |
| NOT          | SUBR       | PREDICATE        | [21](#page21), [23](#page23), [58](#page58) |
| NULL         | SUBR       | PREDICATE        | [11](#page11), [57](#page57) |
| NUMBERP      | SUBR       | PREDICATE        | [26](#page26), [64](#page64) |
| NUMOB        | SUBR       | PSEUDO-FUNCTION  | [86](#page86)        |
| OBLIST       | APVAL      |                  | [69](#page69)        |
| ONEP         | SUBR       | PREDICATE        | [26](#page26), [64](#page64) |
| OPCHAR       | SUBR       | PREDICATE        | [87](#page87)        |
| OPDEFINE     | EXPR       | PSEUDO-FUNCTION  | [65](#page65), [75](#page75) |
| OR           | FSUBR      | PREDICATE        | [21](#page21), [58](#page58) |
| PACK         | SUBR       | PSEUDO-FUNCTION  | [86](#page86)        |
| PAIR         | SUBR       |                  | [60](#page60)        |
| PAUSE        | SUBR       | PSEUDO-FUNCTION  | [67](#page67)        |
| PERIOD       | APVAL      |                  | [69](#page69), [85](#page85) |
| PLB          | SUBR       | PSEUDO- FUNCTION | [67](#page67)        |
| PLUS         | FSUBR      |                  | [25](#page25), [63](#page63) |
| PLUSS        | APVAL      |                  | [69](#page69), [85](#page85) |
| PRIN1        | SUBR       | PSEUDO-FUNCTION  | [65](#page65), [84](#page84) |
| PRINT        | SUBR       | PSEUDO-FUNCTION  | [65](#page65), [84](#page84) |
| PRINTPROP    | EXPR       | PSEUDO-FUNCTION LIBRARY  | [68](#page68)        |
| PROG         | FSUBR      |                  | [29](#page29), [71](#page71) |
| PROG2        | SUBR       |                  | [42](#page42), [66](#page66) |
| PROP         | SUBR       | FUNCTIONAL       | [59](#page59)        |
| PUNCH        | SUBR       | PSEUDO-FUNCTION  | [65](#page65), [84](#page84) |
| PUNCHDEF     | EXPR       | PSEUDO-FUNCTION LIBRARY | [68](#page68)        |
| PUNCHLAP     | EXPR       | PSEUDO-FUNCTION LIBRARY | [68](#page68), [76](#page76) |
| QUOTE        | FSUBR      |                  | [10](#page10), [22](#page22), [71](#page71) |
| QUOTIENT     | SUBR       |                  | [26](#page26), [64](#page64) |
| READ         | SUBR       | PSEUDO-FUNCTION  | [5](#page5), [84](#page84) |
| READLAP      | SUBR       | PSEUDO-FUNCTION  | [65](#page65), [76](#page76) |
| RECIP        | SUBR       |                  | [26](#page26), [64](#page64) |
| RECLAIM      | SUBR       | PSEUDO-FUNCTION  | [67](#page67)        |
| REMAINDER    | SUBR       |                  | [26](#page26), [64](#page64) |
| REMFLAG      | SUBR       | PSEUDO-FUNCTION  | [41](#page41), [60](#page60) |
| REMOB        | SUBR       | PSEUDO-FUNCTION  | [67](#page67)        |
| REMPROP      | SUBR       | PSEUDO-FUNCTION  | [41](#page41), [59](#page59) |
| RETURN       | SUBR       | PSEUDO-FUNCTION  | [30](#page30), [72](#page72) |
| REVERSE      | SUBR       |                  | [6 2](#page6 2)      |
| RPAR         | APVAL      |                  | [69](#page69), [85](#page85) |
| RPLACA       | SUBR       | PSEUDO-FUNCTION  | [41](#page41), [58](#page58) |
| RPLACD       | SUBR       | PSEUDO-FUNCTION  | [41](#page41), [58](#page58) |
| SASSOC       | SUBR       | FUNCTIONAL       | [60](#page60)        |
| SEARCH       | SUBR       | FUNCTIONAL       | [63](#page63)        |
| SELECT       | FEXPR      |                  | [66](#page66)        |
| SET          | SUBR       | PSEUDO-FUNCTION  | [30](#page30), [71](#page71) |
| SETQ         | FSUBR      | PSEUDO-FUNCTION  | [30](#page30), [71](#page71) |
| SLASH        | APVAL      |                  | [69](#page69), [85](#page85) |
| SPEAK        | SUBR       | PSEUDO-FUNCTION  | [34](#page34), [66](#page66) |
| SPECIAL      | SUBR       | PSEUDO-FUNCTION  | [64](#page64), [78](#page78) |
| STAR         | APVAL      |                  | [69](#page69), [85](#page85) |
| STARTREAD    | SUBR       | PSEUDO-FUNCTION  | [87](#page87)        |
| SUB1         | SUBR       |                  | [26](#page26), [64](#page64) |
| SUBLIS       | SUBR       |                  | [12](#page12), [61](#page61) |
| SUBST        | SUBR       |                  | [11](#page11), [61](#page61) |
| T            | APVAL      |                  | [22](#page22), [69](#page69) |
| TEMPUS-FUGIT | SUBR       | PSEUDO-FUNCTION  | [67](#page67)        |
| TERPRI       | SUBR       | PSEUDO-FUNCTION  | [65](#page65), [84](#page84) |
| TIMES        | FSUBR      |                  | [26](#page26), [64](#page64) |
| TRACE        | EXPR       | PSEUDO-FUNCTION  | [32](#page32), [66](#page66), [79](#page79) |
| TRACESET     | EXPR       | PSEUDO-FUNCTION LIBRARY | [68](#page68)        |
| UNCOMMON     | SUBR       | PSEUDO-FUNCTION  | [64](#page64), [78](#page78) |
| UNCOUNT      | SUBR       | PSEUDO-FUNCTION  | [34](#page34), [66](#page66) |
| UNPACK       | SUBR       | PSEUDO-FUNCTION  | [87](#page87)        |
| UNSPECIAL    | SUBR       |                  | [64](#page64), [78](#page78) |
| UNTRACE      | EXPR       | PSEUDO-FUNCTION  | [32](#page32), [66](#page66) |
| UNTRACESET   | EXPR       | PSEUDO-FUNCTION  | [68](#page68)        |
| ZEROP        | SUBR       | PREDICATE        | [26](#page26), [64](#page64) |

```
Symbol or Term
```

```
GLOSSARY
```

```
Definition
```

Algol 60

algorithm

a-list

assembly program

association list

```
An international standard programming language for
describing numerical computation.
A procedure that is unambiguous and sufficiently mecha-
nized so as to be programmable on a computer.
A synonym for association list.
A program that translates from a symbolic instruction
language such as FAP or LAP into the language of a
machine. The statements in an assembly language, with
few exceptions, are one to one with the machine language
instructions to which they translate. Unlike machine
language, an assembly language allows the programmer
to use symbols with mnemonic significance. LISP was
written in the assembly language, FAP. LISP contains
' a small internal assembly program, LAP.
A list of pairs of terms which is equivalent to a table
with two columns. It is used to pair bound variables
with their values. Example: ((VAR1. VAL1) (B. (U V
(W))) (C 2))
```

at om A synonym for atomic symbol.

atomic symbol

back trace

```
The basic constituent of an S-expression. The legal
atomic symbols are certain strings of letters, digits, and
special characters defined precisely in Appendix F.
Examples: A NIL ATOM A1 METHIMPIKEHOSES
A list of the names of functions that have been entered
but not completed at the time when an error occurs.
```

basic functions: car, cdr, These functions are called basic because the entire class
cons, e3 of computable functions of S-expressions can be built from
andatom them by using composition, conditional expressions, and
recursion.

Boolean form

bound variable

```
compiler
```

```
The special forms involving AND, OR, and NOT, which
can be used to build up propositional expressions.
A variable included in the list of bound variables after
LAMBDA is bound within the scope of the LAMBDA. This
means that its value is the argument corresponding in
position to the occurrence of the variable in the LAMBDA
list. For example, in an expression of the form
((LAMBDA (X Y) E) (QUOTE (A. B)) (QUOTE C)), X has
the value (A. B) and Y the value C at any of their
occurrences in E:.
A program that translates from a source language into
machine (or assembly) language. Unlike most compilers,
the LISP compiler does not need to compile an entire
program before execution. It can compile individual
functions defined by S-expressions into machine language
during a run.
```

```
GLOSSARY
```

```
Symbol or Term Definition
```

composition

conditional expression

constant

dot notation

doublet

flag

form

free-storage list

free variable

functional

```
To compose a function is to use the value of one function
as an argument for another function. This is usually
written with nested brackets or parentheses. Examples:
cons[car[~];cdr[~]], or as an S-expression (CONS (CAR X)
(CDR Y)); and a+(b.(c+d)).
An expression containing a list of propositional expressions
and corresponding forms as values. The value is the
value of the entire conditional expression. Examples of
different notations for conditional expression:
M-expression: [a<0-b;~-C]
S-expression: (COND ((LESSP A 0) B) (T C))
Algol 60: if a<O then b else c.
A symbol whose value does not change during a computa-
tion. For example, the value of F is always NIL, and
the value of 5 is always 5. Other constants in the LISP
system are described in Appendix A under the heading
"APVAL. "
A method of writing S-expressions by combining only
pairs of S-expressions rather than lists of indefinite
length. The dot notation is the fundamental notation of
LISP. The list notation is defined in terms of the dot
notation.
A pair of arguments for evalquote, the LISP interpreter .*
An item on the property list of an atomic symbol. A flag
is not followed by a value as an indicator is. TRACE and
COMMON are flags.
An expression that may be evaluated when some corre-
spondence has been set up between the variables contained
in it and a set of actual arguments. The important dis-
tinction between functions and forms is treated in
section 1.4.
The list of available words in the free storage of the com-
puter memory. Each time a cons is performed the
first word on the free-storage list is removed. When
the free-storage list is exhausted, a new one is created
by the garbage collector.
```

```
A variable that is neither a program variable nor a bound
variable. A variable can be considered bound or free
only within the context in which it appears. If a variable
is to be evaluated by the interpreter, it must be bound
at some level or have a constant value or an assigned
value. For definitions covering these three cases see
bound vme, program variable, and constant.
```

```
A function that can have functions as arguments. apply,
eval, maplist, search, and sassoc are functionals in LISP.
```

```
GLOSSARY
```

```
Symbol or Term Definition
```

functional argument

```
garbage collector
```

```
indicator
```

```
interpreter
```

```
lambda notation
```

```
list
```

```
list notation
```

```
logical form
M-expression
```

```
object
Overlord
```

```
packet
```

```
A function that is an argument for a functional. In LISP,
a functional argument is quoted by using the special form
```

```
The routine in LISP which identifies all active list
structure by tracing it from fixed base cells and marking
it, and then collects all unneeded cells (garbage) into a
free-storage list so that these words can be used again.
An atomic symbol occurring on a property list that speci-
fies that the next item on the list is a certain property.
Unlike a flag, an indicator always has a property following
it. SPECIAL, EXPR, FSUBR, and APVAL are examples
of indicators.
An interpreter executes a source-language program by
examining the source language and performing the speci-
fied algorithms. This is in contrast to a translator or
compiler which translates a source-language program
into machine language for subsequent execution. LISP
has both an interpreter and a compiler.
The notation first used by Church for converting forms
into names of functions. A form in a,,... , an is turned
into the function of n variables whose arguments are in
order ai by writing 1[[al... an];form]
An S-expression satisfying the predicate
listp[x] = null[x] J [not[atom[x]]/\listp[cdr [x]]]
An S-expression of this form can be written (ml, m,,...
mn), which stands for the dot-notation expression (ml.
(m,.... (mn NIL)... )).
```

```
A method of writing S-expressions by using the form
(ml , mZ,... , mn) to stand for the dot -notation expression
(ml (m,. ...( mn NIL) ...))
See Boolian form
An expression in the LISP language for writing functions
of S-expressions. M-expressions cannot be read by the
machine at present but must be hand-translated into
S-expressions.
A synonym for atomic symbol.
The monitor and system tape-handling section of the LISP
system.
A sequence of doublets for the interpreter. Each packet
is preceded by an Overlord control card and ends with
the ward STOP.
```

```
GLOSSARY
```

```
Svmbol or Term Definition
```

partial function

pointer

predicate

program variable

property

property list

```
A function that is defined for only part of its normal domain.
It may not be possible to decide exactly for which arguments
the function is defined because, for such arguments, the
computation may continue indefinitely.
In LISP the complement of the address of a word is called
a pointer to that word.
A function whose value is true or false. In LISP true and
false are represented by tEtomic symbols *T* and NIL.
The symbols T and F have *T* and NIL as values.
A variable that is declared in the list of variables following
the word PROG. Program variables have initially the value
NIL, but they can be assigned arbitrary S-expressions as
values by using SET or SETQ.
An expresssion or quantity associated with an atomic symbol.
It is found on the property list preceded by an indicator.
The list structure associated with an atomic symbol con-
taining its print name and other properties, such as its value
as a constant or its definition as a function.
```

propositional expression An expression that is true or false when evaluated. The
relation between propositional expressions and predicates is
exactly the same as the relation between form and function.

pseudo -function

push-down list

```
reclaimer
recursion
special form
```

```
trace
```

```
translator
```

```
universal function
```

```
A program that is called as if it were a function, but has
effects other than that of delivering its value. For example,
PRINT, READ, RPLACA.
The last-in-first-out memory area for saving partial results
of recursive functions.
A synonym for the garbage collector.
The technique of defining an algorithm in terms of itself.
A form having an indefinite number of arguments and/or
arguments that are understood as being quoted before being
given to the special form to evaluate.
A debugging aid that causes a notice to be printed of every
occurrence of an entry to or exit from any of the specified
functions, with its appropriate arguments or value.
A program whose input is an expression (e. g. , a program)
in one language (the source language) and whose output is a
corresponding expression in another language (the object
language).
A function whose arguments are expressions representing
any computable function and its arguments. The value of
the universal function is the value of the computable function
applied to its arguments.
```
