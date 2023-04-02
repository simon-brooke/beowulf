# Understanding values and properties

I had had the naive assumption that entries on the object list had their CAR pointing to the symbol and their CDR pointing to the related value. Consequently, I could not work out where the property list went. More careful reading of [the text] implies, but does not explicitly state, that my naive assumption is wrong.

Instead, it appears that the `CAR` points to the symbol, as expected, but the `CAR` points to the property list; and that on the property list there are privileged properties at least as follows:

APVAL
: the simple straightforward ordinary value of the symbol, considered as a variable;

EXPR
: the definition of the function considered as a normal lambda expression (arguments to be evaluated before applying);

FEXPR
: the definition of a function which should be applied to unevaluated arguments;

SUBR
: the definition of a complied subroutine which should be applied to evaluated arguments;

FSUBR
: the definition of a complied subroutine which should be applied to unevaluated arguments.

I think there was also another privileged property value which contained the property considered as a constant, but I haven't yet confirmed that.

From this it would seem that Lisp 1.5 was not merely a ['Lisp 2'](http://xahlee.info/emacs/emacs/lisp1_vs_lisp2.html) but in fact a 'Lisp 6', with six effectively first class namespaces. In fact it's not as bad as that, because of the way [`EVAL`](https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=79) is evaluated.

Essentially the properties are tried in turn, and only the first value found is used. Thus the heirarchy is

1. APVAL
2. EXPR
3. FEXPR
4. SUBR
5. FSUBR

This means that, while the other potential values can be retrieved from the property list, interpreted definitions (if present) will always be preferred to uninterpreted definitions, and lambda function definitions (which evaluate their arguments), where present, will always be preferred to non-lamda definitions, which don't.

**BUT NOTE THAT** the `APVAL` value is sought only when seeking a variable value for the symbol, and the others only when seeking a function value, so Lisp 1.5 is a 'Lisp 2', not a 'Lisp 1'.



Versions of Beowulf up to and including 0.2.1 used the naive understanding of the architecture; version 0.3.0 *should* use the corrected version.
