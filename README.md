# beowulf

LISP 1.5 is to all Lisp dialects as Beowulf is to English literature.

![Beowulf logo](img/beowulf_logo.png)

## What this is

A work-in-progress towards an implementation of Lisp 1.5 in Clojure. The
objective is to build a complete and accurate implementation of Lisp 1.5
as described in the manual, with, in so far as is possible, exactly the
same bahaviour - except as documented below.

### Status

Working Lisp interpreter, but some key features not yet implemented.

* [Project website](https://simon-brooke.github.io/beowulf/).
* [Source code documentation](https://simon-brooke.github.io/beowulf/docs/codox/index.html).

### Building and Invoking

Build with

    lein uberjar

Invoke with

    java -jar target/uberjar/beowulf-0.3.0-SNAPSHOT-standalone.jar --help

(Obviously, check your version number)

Command line arguments as follows:

```
  -h, --help                               Print this message
  -p PROMPT, --prompt PROMPT               Set the REPL prompt to PROMPT
  -r INITFILE, --read INITFILE             Read Lisp functions from the file INITFILE
  -s, --strict                             Strictly interpret the Lisp 1.5 language, without extensions.
```

To end a session, type `STOP` at the command prompt.

### Reader macros

Currently I don't have 

### Functions and symbols implemented

The following functions and symbols are implemented:

| Function     | Type           | Signature        | Implementation | Documentation        |
|--------------|----------------|------------------|----------------|----------------------|
| NIL          | Lisp variable  |                  |                | ?                    |
| T            | Lisp variable  |                  |                | ?                    |
| F            | Lisp variable  |                  |                | ?                    |
| ADD1         | Host function  | (ADD1 X)         |                | ?                    |
| AND          | Host function  | (AND & ARGS)     | PREDICATE      | `T` if and only if none of my `args` evaluate to either `F` or `NIL`,    else `F`.        In `beowulf.host` principally because I don't yet feel confident to define    varargs functions in Lisp. |
| APPEND       | Lisp function  | (APPEND X Y)     | LAMBDA-fn      | see manual pages <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=19'>11</a>, <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=69'>61</a> |
| APPLY        | Host function  | (APPLY FUNCTION ARGS ENVIRONMENT DEPTH) |                | Apply this `function` to these `arguments` in this `environment` and return    the result.        For bootstrapping, at least, a version of APPLY written in Clojure.    All args are assumed to be symbols or `beowulf.cons-cell/ConsCell` objects.    See page 13 of the Lisp 1.5 Programmers Manual. |
| ATOM         | Host function  | (ATOM X)         | PREDICATE      | Returns `T` if and only if the argument `x` is bound to an atom; else `F`.   It is not clear to me from the documentation whether `(ATOM 7)` should return   `T` or `F`. I'm going to assume `T`. |
| CAR          | Host function  | (CAR X)          |                | Return the item indicated by the first pointer of a pair. NIL is treated   specially: the CAR of NIL is NIL. |
| CAAAAR       | Lisp function  | (CAAAAR X)       | LAMBDA-fn      | ?                    |
| CAAADR       | Lisp function  | (CAAADR X)       | LAMBDA-fn      | ?                    |
| CAAAR        | Lisp function  | (CAAAR X)        | LAMBDA-fn      | ?                    |
| CAADAR       | Lisp function  | (CAADAR X)       | LAMBDA-fn      | ?                    |
| CAADDR       | Lisp function  | (CAADDR X)       | LAMBDA-fn      | ?                    |
| CAADR        | Lisp function  | (CAADR X)        | LAMBDA-fn      | ?                    |
| CAAR         | Lisp function  | (CAAR X)         | LAMBDA-fn      | ?                    |
| CADAAR       | Lisp function  | (CADAAR X)       | LAMBDA-fn      | ?                    |
| CADADR       | Lisp function  | (CADADR X)       | LAMBDA-fn      | ?                    |
| CADAR        | Lisp function  | (CADAR X)        | LAMBDA-fn      | ?                    |
| CADDAR       | Lisp function  | (CADDAR X)       | LAMBDA-fn      | ?                    |
| CADDDR       | Lisp function  | (CADDDR X)       | LAMBDA-fn      | ?                    |
| CADDR        | Lisp function  | (CADDR X)        | LAMBDA-fn      | ?                    |
| CADR         | Lisp function  | (CADR X)         | LAMBDA-fn      | ?                    |
| CDAAAR       | Lisp function  | (CDAAAR X)       | LAMBDA-fn      | ?                    |
| CDAADR       | Lisp function  | (CDAADR X)       | LAMBDA-fn      | ?                    |
| CDAAR        | Lisp function  | (CDAAR X)        | LAMBDA-fn      | ?                    |
| CDADAR       | Lisp function  | (CDADAR X)       | LAMBDA-fn      | ?                    |
| CDADDR       | Lisp function  | (CDADDR X)       | LAMBDA-fn      | ?                    |
| CDADR        | Lisp function  | (CDADR X)        | LAMBDA-fn      | ?                    |
| CDAR         | Lisp function  | (CDAR X)         | LAMBDA-fn      | ?                    |
| CDDAAR       | Lisp function  | (CDDAAR X)       | LAMBDA-fn      | ?                    |
| CDDADR       | Lisp function  | (CDDADR X)       | LAMBDA-fn      | ?                    |
| CDDAR        | Lisp function  | (CDDAR X)        | LAMBDA-fn      | ?                    |
| CDDDAR       | Lisp function  | (CDDDAR X)       | LAMBDA-fn      | ?                    |
| CDDDDR       | Lisp function  | (CDDDDR X)       | LAMBDA-fn      | ?                    |
| CDDDR        | Lisp function  | (CDDDR X)        | LAMBDA-fn      | ?                    |
| CDDR         | Lisp function  | (CDDR X)         | LAMBDA-fn      | ?                    |
| CDR          | Host function  | (CDR X)          |                | Return the item indicated by the second pointer of a pair. NIL is treated   specially: the CDR of NIL is NIL. |
| CONS         | Host function  | (CONS CAR CDR)   |                | Construct a new instance of cons cell with this `car` and `cdr`. |
| COPY         | Lisp function  | (COPY X)         | LAMBDA-fn      | see manual pages <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=70'>62</a> |
| DEFINE       | Host function  | (DEFINE ARGS)    | PSEUDO-FUNCTION | Bootstrap-only version of `DEFINE` which, post boostrap, can be overwritten    in LISP.     The single argument to `DEFINE` should be an assoc list which should be    nconc'ed onto the front of the oblist. Broadly,    (SETQ OBLIST (NCONC ARG1 OBLIST)) |
| DIFFERENCE   | Host function  | (DIFFERENCE X Y) |                | ?                    |
| DIVIDE       | Lisp function  | (DIVIDE X Y)     | LAMBDA-fn      | see manual pages <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=34'>26</a>, <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=72'>64</a> |
| ERROR        | Host function  | (ERROR & ARGS)   | PSEUDO-FUNCTION | Throw an error       |
| EQ           | Host function  | (EQ X Y)         | PREDICATE      | Returns `T` if and only if both `x` and `y` are bound to the same atom,   else `NIL`. |
| EQUAL        | Host function  | (EQUAL X Y)      | PREDICATE      | This is a predicate that is true if its two arguments are identical   S-expressions, and false if they are different. (The elementary predicate   `EQ` is defined only for atomic arguments.) The definition of `EQUAL` is   an example of a conditional expression inside a conditional expression.    NOTE: returns `F` on failure, not `NIL` |
| EVAL         | Host function  | (EVAL EXPR); (EVAL EXPR ENV DEPTH) |                | Evaluate this `expr` and return the result. If `environment` is not passed,    it defaults to the current value of the global object list. The `depth`    argument is part of the tracing system and should not be set by user code.     All args are assumed to be numbers, symbols or `beowulf.cons-cell/ConsCell`     objects. |
| FACTORIAL    | Lisp function  | (FACTORIAL N)    | LAMBDA-fn      | ?                    |
| FIXP         | Host function  | (FIXP X)         | PREDICATE      | ?                    |
| GENSYM       | Host function  | (GENSYM )        |                | Generate a unique symbol. |
| GET          | Lisp function  | (GET X Y)        | LAMBDA-fn      | see manual pages <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=49'>41</a>, <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=67'>59</a> |
| GREATERP     | Host function  | (GREATERP X Y)   | PREDICATE      | ?                    |
| INTEROP      | Host function  | (INTEROP FN-SYMBOL ARGS) | (INTEROP)      | Clojure (or other host environment) interoperation API. `fn-symbol` is expected   to be either    1. a symbol bound in the host environment to a function; or   2. a sequence (list) of symbols forming a qualified path name bound to a      function.    Lower case characters cannot normally be represented in Lisp 1.5, so both the   upper case and lower case variants of `fn-symbol` will be tried. If the   function you're looking for has a mixed case name, that is not currently   accessible.    `args` is expected to be a Lisp 1.5 list of arguments to be passed to that   function. Return value must be something acceptable to Lisp 1.5, so either   a symbol, a number, or a Lisp 1.5 list.    If `fn-symbol` is not found (even when cast to lower case), or is not a function,   or the value returned cannot be represented in Lisp 1.5, an exception is thrown   with `:cause` bound to `:interop` and `:detail` set to a value representing the   actual problem. |
| INTERSECTION | Lisp function  | (INTERSECTION X Y) | LAMBDA-fn      | ?                    |
| LENGTH       | Lisp function  | (LENGTH L)       | LAMBDA-fn      | see manual pages <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=70'>62</a> |
| LESSP        | Host function  | (LESSP X Y)      | PREDICATE      | ?                    |
| MEMBER       | Lisp function  | (MEMBER A X)     | LAMBDA-fn      | see manual pages <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=19'>11</a>, <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=70'>62</a> |
| MINUSP       | Lisp function  | (MINUSP X)       | LAMBDA-fn      | see manual pages <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=34'>26</a>, <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=72'>64</a> |
| NOT          | Lisp function  | (NOT X)          | LAMBDA-fn      | see manual pages <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=29'>21</a>, <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=31'>23</a>, <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=66'>58</a> |
| NULL         | Lisp function  | (NULL X)         | LAMBDA-fn      | see manual pages <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=19'>11</a>, <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=65'>57</a> |
| NUMBERP      | Host function  | (NUMBERP X)      | PREDICATE      | ?                    |
| OBLIST       | Host function  | (OBLIST )        |                | Return a list of the symbols currently bound on the object list.        **NOTE THAT** in the Lisp 1.5 manual, footnote at the bottom of page 69, it implies     that an argument can be passed but I'm not sure of the semantics of    this. |
| ONEP         | Lisp function  | (ONEP X)         | LAMBDA-fn      | see manual pages <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=34'>26</a>, <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=72'>64</a> |
| PAIR         | Lisp function  | (PAIR X Y)       | LAMBDA-fn      | see manual pages <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=68'>60</a> |
| PLUS         | Host function  | (PLUS & ARGS)    |                | ?                    |
| PRETTY       | Lisp variable  |                  | (PRETTY)       | ?                    |
| PRINT        | Lisp variable  |                  | PSEUDO-FUNCTION  | ?                    |
| PROP         | Lisp function  | (PROP X Y U)     | LAMBDA-fn      | see manual pages <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=67'>59</a> |
| QUOTIENT     | Host function  | (QUOTIENT X Y)   |                | I'm not certain from the documentation whether Lisp 1.5 `QUOTIENT` returned   the integer part of the quotient, or a realnum representing the whole   quotient. I am for now implementing the latter. |
| RANGE        | Lisp variable  | ?                | (RANGE (LAMBDA (N M) (COND ((LESSP M N) (QUOTE NIL)) ((QUOTE T) (CONS N (RANGE (ADD1 N) M)))))) | ?                    |
| READ         | Host function  | (READ ); (READ INPUT) | PSEUDO-FUNCTION  | An implementation of a Lisp reader sufficient for bootstrapping; not necessarily   the final Lisp reader. `input` should be either a string representation of a LISP   expression, or else an input stream. A single form will be read. |
| REMAINDER    | Host function  | (REMAINDER X Y)  |                | ?                    |
| REPEAT       | Lisp function  | (REPEAT N X)     | LAMBDA-fn      | ?                    |
| RPLACA       | Host function  | (RPLACA CELL VALUE) | PSEUDO-FUNCTION | Replace the CAR pointer of this `cell` with this `value`. Dangerous, should   really not exist, but does in Lisp 1.5 (and was important for some   performance hacks in early Lisps) |
| RPLACD       | Host function  | (RPLACD CELL VALUE) | PSEUDO-FUNCTION | Replace the CDR pointer of this `cell` with this `value`. Dangerous, should   really not exist, but does in Lisp 1.5 (and was important for some   performance hacks in early Lisps) |
| SET          | Host function  | (SET SYMBOL VAL) | PSEUDO-FUNCTION | Implementation of SET in Clojure. Add to the `oblist` a binding of the    value of `var` to the value of `val`. NOTE WELL: this is not SETQ! |
| SUB1         | Lisp function  | (SUB1 N)         | LAMBDA-fn      | see manual pages <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=34'>26</a>, <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=72'>64</a> |
| SYSIN        | Host function  | (SYSIN ); (SYSIN FILENAME) | (SYSIN)        | Read the contents of the file at this `filename` into the object list.         If the file is not a valid Beowulf sysout file, this will probably     corrupt the system, you have been warned. File paths will be considered     relative to the filepath set when starting Lisp.     It is intended that sysout files can be read both from resources within    the jar file, and from the file system. If a named file exists in both the    file system and the resources, the file system will be preferred.        **NOTE THAT** if the provided `filename` does not end with `.lsp` (which,    if you're writing it from the Lisp REPL, it won't), the extension `.lsp`    will be appended. |
| SYSOUT       | Host function  | (SYSOUT ); (SYSOUT FILEPATH) | (SYSOUT)       | Dump the current content of the object list to file. If no `filepath` is    specified, a file name will be constructed of the symbol `Sysout` and     the current date. File paths will be considered relative to the filepath    set when starting Lisp. |
| TERPRI       | Lisp variable  |                  | PSEUDO-FUNCTION | ?                    |
| TIMES        | Host function  | (TIMES & ARGS)   |                | ?                    |
| TRACE        | Host function  | (TRACE S)        | PSEUDO-FUNCTION | Add this symbol `s` to the set of symbols currently being traced. If `s`    is not a symbol, does nothing. |
| UNTRACE      | Host function  | (UNTRACE S)      | PSEUDO-FUNCTION | ?                    |
| ZEROP        | Lisp function  | (ZEROP N)        | LAMBDA-fn      | see manual pages <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=34'>26</a>, <a href='https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=72'>64</a> |


Functions described as 'Lisp function' above are defined in the default 
sysout file, `resources/lisp1.5.lsp`, which will be loaded by default unless 
you specify another initfile on the command line.

Functions described as 'Host function' are implemented in Clojure, but if you're 
brave you can redefine them in Lisp and the Lisp definitions will take precedence
over the Clojure implementations.

### Architectural plan

Not everything documented in this section is yet built. It indicates the
direction of travel and intended destination, not the current state.

#### resources/lisp1.5.lsp

The objective is to have within `resources/lisp1.5.lsp`, all those functions
defined in the Lisp 1.5 Programmer's Manual which can be implemented in Lisp.

This means that, while Beowulf is hosted on Clojure, all that would be
required to rehost Lisp 1.5 on a different platform would be to reimplement

* bootstrap.clj
* host.clj
* read.clj

The objective this is to make it fairly easy to implement Lisp 1.5 on top of
any of the many [Make A Lisp](https://github.com/kanaka/mal)
implementations.

#### beowulf/boostrap.clj

This file is essentially Lisp as defined in Chapter 1 (pages 1-14) of the
Lisp 1.5 Programmer's Manual; that is to say, a very simple Lisp language,
which should, I believe, be sufficient in conjunction with the functions
provided by `beowulf.host`, to bootstrap the full Lisp 1.5 interpreter.

In addition it contains the function `INTEROP`, which allows host language
functions to be called from Lisp.

#### beowulf/host.clj

This file provides Lisp 1.5 functions which can't be (or can't efficiently
be) implemented in Lisp 1.5, which therefore need to be implemented in the
host language, in this case Clojure.

#### beowulf/read.clj

This file provides the reader required for boostrapping. It's not a bad
reader - it provides feedback on errors found in the input - but it isn't
the real Lisp reader.

Intended deviations from the behaviour of the real Lisp reader are as follows:

1. It reads the meta-expression language `MEXPR` in addition to the
    symbolic expression language `SEXPR`, which I do not believe the Lisp 1.5
    reader ever did;
2. It treats everything between a double semi-colon and an end of line as 
    a comment, as most modern Lisps do; but I do not believe Lisp 1.5 had 
    this feature.

### BUT WHY?!!?!

Because.

Because Lisp is the only computer language worth learning, and if a thing
is worth learning, it's worth learning properly; which means going back to
the beginning and trying to understand that.

Because there is, so far as I know, no working implementation of Lisp 1.5
for modern machines.

Because I'm barking mad, and this is therapy.

### Commentary

What's surprised me in working on this is how much more polished Lisp 1.5 is
than legend had led me to believe. The language is remarkably close to
[Portable Standard Lisp](http://www.softwarepreservation.org/projects/LISP/standard_lisp_family/#Portable_Standard_LISP_)
which is in my opinion one of the best and most usable early Lisp
implementations. 

What's even more surprising is how faithful a reimplementation of Lisp 1.5 
the first Lisp dialect I learned, [Acornsoft Lisp](https://en.wikipedia.org/wiki/Acornsoft_LISP), turns out to have been.

I'm convinced you could still use Lisp 1.5 for interesting
and useful software (which isn't to say that modern Lisps aren't better,
but this is software which is almost sixty years old).

## Installation

At present, clone the source and build it using

`lein uberjar`.

You will require to have [Leiningen](https://leiningen.org/) installed.

### Input/output

Lisp 1.5 greatly predates modern computers. It had a facility to print to a line printer, or to punch cards on a punch-card machine, and it had a facility to read system images in from tape; but there's no file I/O as we would currently understand it, and, because there are no character strings and the valid characters within an atom are limited, it isn't easy to compose a sensible filename.

I've provided two functions to work around this problem.

#### SYSOUT

`SYSOUT` dumps the global object list to disk as a single S Expression (specifically: an association list). This allows you to persist your session, with all your current work, to disk. The function takes one argument, expected to be a symbol, and, if that argument is provided, writes a file whose name is that symbol with `.lsp` appended. If no argument is provided, it will construct a filename comprising the token `Sysout`, followed by the current date, followed by `.lsp`. In either case the file will be written to the directory given in the FILEPATH argument at startup time, or by default the current directory.

Obviously, `SYSOUT` may be called interactively (and this is the expected practice).

#### SYSIN

`SYSIN` reads a file from disk and overwrites the global object list with its contents. The expected practice is that this will be a file created by `SYSOUT`. A command line flag `--read` is provided so that you can specify   

## Learning Lisp 1.5

The `Lisp 1.5 Programmer's Manual` is still
[in print, ISBN 13 978-0-262-13011-0](https://mitpress.mit.edu/books/lisp-15-programmers-manual); but it's also
[available online](http://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf).

## Other Lisp 1.5 resources

The main resource I'm aware of is the Software Preservation Society's site,
[here](http://www.softwarepreservation.org/projects/LISP/lisp1.5). It has lots
of fascinating stuff including full assembler listings for various obsolete
processors, but I failed to find the Lisp source of Lisp functions as a text
file, which is why `resources/lisp1.5.lsp` is largely copytyped and
reconstructed from the manual.

I'm not at this time aware of any other working Lisp 1.5 implementations.

## License

Copyright © 2019 Simon Brooke. Licensed under the GNU General Public License,
version 2.0 or (at your option) any later version.
