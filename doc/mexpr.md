# M-Expressions

M-Expressions ('mexprs') are the grammar which John McCarthy origininally used to write Lisp, and the grammar in which many of the function definitions in the [Lisp 1.5 Programmer's Manual](https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf) are stated. However, I have not seen anywhere a claim that Lisp 1.5 could *read* M-Expressions, and it is not clear to me whether it was even planned that it should do so.

Rather, it seems to me probably that M-Expressions were only ever a grammar intended to be written on paper, like [Backus Naur Form](https://en.wikipedia.org/wiki/Backus%E2%80%93Naur_form), to describe and to reason about algorithms.

I set out to make Beowulf read M-Expressions essentially out of curiousity, to see whether it could be done. I had this idea that if it could be done, I could implement most of Lisp 1.5 simply by copying in the M-Expression definitions out of the manual.

Consequently, the Beowulf parser can parse the M-Expression grammar as stated in the manual, and generate S-Expressions from it according to the table specified on page 10 of the manual.

There are two problems with this.

## Problems with interpreting M-Expressions

### Generating idiomatic Lisp

In the M-Expression notation, a lower case character or sequence of characters represents a variable; an upper case character represents a constant. As the manual says,

> 2 . The obvious translation of letting a constant translate into itself will not work.
Since the translation of `x` is `X`, the translation of `X` must be something else to avoid
ambiguity. The solution is to quote it. Thus `X` is translated into `(QUOTE X)`.

Thus, necessarily, the translation of a constant must always be quoted. In practice, key constants in Lisp such as `T` are bound to themselves, so it is idiomatic in Lisp, certainly in the way we have learned to use it, to write, for example,

```
(SET (QUOTE NULL) 
    (QUOTE (LAMBDA (X) 
        (COND 
            ((EQUAL X NIL) T) (T F)))))
```

However, the literal translation of

```
null[x] = [x = NIL -> T; T -> F]
```

is

```
(SET (QUOTE NULL) 
    (QUOTE (LAMBDA (X) 
        (COND 
            ((EQUAL X (QUOTE NIL)) (QUOTE T))
            ((QUOTE T) (QUOTE F))))))
```

This is certainly more prolix and more awkward, but it also risks being flat wrong.

Is the value of `NIL` the atom `NIL`, or is it the empty list `()`? If the former, then the translation from the M-Expression above is correct. However, that means that recursive functions which recurse down a list seeking the end will fail. So the latter must be the case.

`NULL` is described thus (Ibid, p11):

> This is a predicate useful for deciding when a list is exhausted. It is true if and only if its argument is `NIL`.

I think there is an ambiguity in referencing constants which are not bound to themselves in the M-Expression notation as given in the manual. This is particularly problematic with regards to `NIL`, but there may be others instances.

### Curly braces

The use of curly braces is not defined in the grammar as stated on page 10. They are not used in the initial definition of `APPLY` on page 13, but they are used in the more developed restatement on page 70. I believe they are to be read as indicating a `DO` statement -- a list of function calls to be made sequentially but without strict functional dependence on one another -- but I don't find the exposition here particularly clear and I'm not sure of this.

Consequently, the M-Expression interpreter in Beowulf does not interpret curly braces.