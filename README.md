# beowulf

## Þý liste cræfte spræc

LISP 1.5 is to all Lisp dialects as Beowulf is to English literature.

![Beowulf logo](https://simon-brooke.github.io/beowulf/docs/img/beowulf_logo_med.png)

## Contents
  * [What this is](#what-this-is)
    + [Status](#status)
    + [BUT WHY?!!?!](#but-why-----)
    + [Project Target](#project-target)
    + [Invoking](#invoking)
    + [Building and Invoking](#building-and-invoking)
    + [Reader macros](#reader-macros)
    + [Functions and symbols implemented](#functions-and-symbols-implemented)
    + [Architectural plan](#architectural-plan)
      - [resources/lisp1.5.lsp](#resources-lisp15lsp)
      - [beowulf/boostrap.clj](#beowulf-boostrapclj)
      - [beowulf/host.clj](#beowulf-hostclj)
      - [beowulf/read.clj](#beowulf-readclj)
    + [Commentary](#commentary)
  * [Installation](#installation)
    + [Input/output](#input-output)
      - [SYSOUT](#sysout)
      - [SYSIN](#sysin)
  * [Learning Lisp 1.5](#learning-lisp-15)
  * [Other Lisp 1.5 resources](#other-lisp-15-resources)
    + [Other implmentations](#other-implementations)
    + [History resources](#history-resources)
  * [License](#license)

<small><i><a href='http://ecotrust-canada.github.io/markdown-toc/'>Table of contents generated with markdown-toc</a></i></small>

## What this is

A work-in-progress towards an implementation of Lisp 1.5 in Clojure. The
objective is to build a complete and accurate implementation of Lisp 1.5
as described in the manual, with, in so far as is possible, exactly the
same bahaviour - except as documented below.

### BUT WHY?!!?!

Because.

Because Lisp is the only computer language worth learning, and if a thing
is worth learning, it's worth learning properly; which means going back to
the beginning and trying to understand that.

Because there is, so far as I know, no working implementation of Lisp 1.5
for modern machines.

Because I'm barking mad, and this is therapy.

### Status

Working Lisp interpreter, but some key features not yet implemented.

* [Project website](https://simon-brooke.github.io/beowulf/).
* [Source code documentation](https://simon-brooke.github.io/beowulf/docs/codox/index.html).

### Project Target

The project target is to be able to run the [Wang algorithm for the propositional calculus](https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=52) given in chapter 8 of the *Lisp 1.5 Programmer's Manual*. When that runs, the project is as far as I am concerned feature complete. I may keep tinkering with it after that and I'll certainly accept pull requests which are in the spirit of the project (i.e. making Beowulf more usable, and/or implementing parts of Lisp 1.5 which I have not implemented), but this isn't intended to be a new language for doing real work; it's an
educational and archaeological project, not serious engineering.

Some `readline`-like functionality would be really useful, but my attempt to 
integrate [JLine](https://github.com/jline/jline3) has not (yet) been successful.

An in-core structure editor would be an extremely nice thing, and I may well
implement one.

You are of course welcome to fork the project and do whatever you like with it!

### Invoking

Invoke with

    java -jar target/uberjar/beowulf-0.3.1-standalone.jar --help

(Obviously, check your version number)

Command line arguments as follows:

```
  -h, --help                               Print this message
  -p PROMPT, --prompt PROMPT               Set the REPL prompt to PROMPT
  -r INITFILE, --read SYSOUTFILE           Read Lisp sysout from the file SYSOUTFILE 
                                           (defaults to `resources/lisp1.5.lsp`)
  -s, --strict                             Strictly interpret the Lisp 1.5 language, 
                                           without extensions.
```

To end a session, type `STOP` at the command prompt.

### Building and Invoking

Build with

    lein uberjar


### Reader macros

Currently `SETQ` and `DEFUN` are implemented as reader macros, sort of. It would
now be possible to reimplement them as `FEXPRs` and so the reader macro functionality will probably go away.

### Functions and symbols implemented

| Function     | Type           | Signature        | Implementation | Documentation        |
|--------------|----------------|------------------|----------------|----------------------|
| NIL          | Lisp variable  | ?                |                | see manual pages <a href='#page22'>22</a>, <a href='#page69'>69</a> |
| T            | Lisp variable  | ?                |                | see manual pages <a href='#page22'>22</a>, <a href='#page69'>69</a> |
| F            | Lisp variable  | ?                |                | see manual pages <a href='#page22'>22</a>, <a href='#page69'>69</a> |
| ADD1         | Host lambda function | ?                |                | ?                    |
| AND          | Host lambda function | ?                | PREDICATE      | `T` if and only if none of my `args` evaluate to either `F` or `NIL`,    else `F`.        In `beowulf.host` principally because I don't yet feel confident to define    varargs functions in Lisp. |
| APPEND       | Lisp lambda function | ?                |                | see manual pages <a href='#page11'>11</a>, <a href='#page61'>61</a> |
| APPLY        | Host lambda function | ?                |                | Apply this `function` to these `arguments` in this `environment` and return    the result.        For bootstrapping, at least, a version of APPLY written in Clojure.    All args are assumed to be symbols or `beowulf.cons-cell/ConsCell` objects.    See page 13 of the Lisp 1.5 Programmers Manual. |
| ASSOC        | Lisp lambda function, Host lambda function | ?                | ?              | If a is an association list such as the one formed by PAIRLIS in the above   example, then assoc will produce the first pair whose first term is x. Thus   it is a table searching function.    All args are assumed to be `beowulf.cons-cell/ConsCell` objects.   See page 12 of the Lisp 1.5 Programmers Manual.        **NOTE THAT** this function is overridden by an implementation in Lisp,    but is currently still present for bootstrapping. |
| ATOM         | Host lambda function | ?                | PREDICATE      | Returns `T` if and only if the argument `x` is bound to an atom; else `F`.   It is not clear to me from the documentation whether `(ATOM 7)` should return   `T` or `F`. I'm going to assume `T`. |
| CAR          | Host lambda function | ?                |                | Return the item indicated by the first pointer of a pair. NIL is treated   specially: the CAR of NIL is NIL. |
| CAAAAR       | Lisp lambda function | ?                | ?              | ?                    |
| CAAADR       | Lisp lambda function | ?                | ?              | ?                    |
| CAAAR        | Lisp lambda function | ?                | ?              | ?                    |
| CAADAR       | Lisp lambda function | ?                | ?              | ?                    |
| CAADDR       | Lisp lambda function | ?                | ?              | ?                    |
| CAADR        | Lisp lambda function | ?                | ?              | ?                    |
| CAAR         | Lisp lambda function | ?                | ?              | ?                    |
| CADAAR       | Lisp lambda function | ?                | ?              | ?                    |
| CADADR       | Lisp lambda function | ?                | ?              | ?                    |
| CADAR        | Lisp lambda function | ?                | ?              | ?                    |
| CADDAR       | Lisp lambda function | ?                | ?              | ?                    |
| CADDDR       | Lisp lambda function | ?                | ?              | ?                    |
| CADDR        | Lisp lambda function | ?                | ?              | ?                    |
| CADR         | Lisp lambda function | ?                | ?              | ?                    |
| CDAAAR       | Lisp lambda function | ?                | ?              | ?                    |
| CDAADR       | Lisp lambda function | ?                | ?              | ?                    |
| CDAAR        | Lisp lambda function | ?                | ?              | ?                    |
| CDADAR       | Lisp lambda function | ?                | ?              | ?                    |
| CDADDR       | Lisp lambda function | ?                | ?              | ?                    |
| CDADR        | Lisp lambda function | ?                | ?              | ?                    |
| CDAR         | Lisp lambda function | ?                | ?              | ?                    |
| CDDAAR       | Lisp lambda function | ?                | ?              | ?                    |
| CDDADR       | Lisp lambda function | ?                | ?              | ?                    |
| CDDAR        | Lisp lambda function | ?                | ?              | ?                    |
| CDDDAR       | Lisp lambda function | ?                | ?              | ?                    |
| CDDDDR       | Lisp lambda function | ?                | ?              | ?                    |
| CDDDR        | Lisp lambda function | ?                | ?              | ?                    |
| CDDR         | Lisp lambda function | ?                | ?              | ?                    |
| CDR          | Host lambda function | ?                |                | Return the item indicated by the second pointer of a pair. NIL is treated   specially: the CDR of NIL is NIL. |
| CONS         | Host lambda function | ?                |                | Construct a new instance of cons cell with this `car` and `cdr`. |
| CONSP        | Host lambda function | ?                | ?              | Return `T` if object `o` is a cons cell, else `F`.        **NOTE THAT** this is an extension function, not available in strct mode.     I believe that Lisp 1.5 did not have any mechanism for testing whether an    argument was, or was not, a cons cell. |
| COPY         | Lisp lambda function | ?                |                | see manual pages <a href='#page62'>62</a> |
| DEFINE       | Host lambda function | ?                | PSEUDO-FUNCTION | Bootstrap-only version of `DEFINE` which, post boostrap, can be overwritten    in LISP.     The single argument to `DEFINE` should be an association list of symbols to    lambda functions. See page 58 of the manual. |
| DIFFERENCE   | Host lambda function | ?                |                | ?                    |
| DIVIDE       | Lisp lambda function | ?                |                | see manual pages <a href='#page26'>26</a>, <a href='#page64'>64</a> |
| DOC          | Host lambda function | ?                | ?              | Open the page for this `symbol` in the Lisp 1.5 manual, if known, in the      default web browser.        **NOTE THAT** this is an extension function, not available in strct mode. |
| EFFACE       | Lisp lambda function | ?                | PSEUDO-FUNCTION | see manual pages <a href='#page63'>63</a> |
| ERROR        | Host lambda function | ?                | PSEUDO-FUNCTION | Throw an error       |
| EQ           | Host lambda function | ?                | PREDICATE      | Returns `T` if and only if both `x` and `y` are bound to the same atom,   else `NIL`. |
| EQUAL        | Host lambda function | ?                | PREDICATE      | This is a predicate that is true if its two arguments are identical   S-expressions, and false if they are different. (The elementary predicate   `EQ` is defined only for atomic arguments.) The definition of `EQUAL` is   an example of a conditional expression inside a conditional expression.    NOTE: returns `F` on failure, not `NIL` |
| EVAL         | Host lambda function | ?                |                | Evaluate this `expr` and return the result. If `environment` is not passed,    it defaults to the current value of the global object list. The `depth`    argument is part of the tracing system and should not be set by user code.     All args are assumed to be numbers, symbols or `beowulf.cons-cell/ConsCell`     objects. However, if called with just a single arg, `expr`, I'll assume it's    being called from the Clojure REPL and will coerce the `expr` to `ConsCell`. |
| FACTORIAL    | Lisp lambda function | ?                | ?              | ?                    |
| FIXP         | Host lambda function | ?                | PREDICATE      | ?                    |
| GENSYM       | Host lambda function | ?                |                | Generate a unique symbol. |
| GET          | Host lambda function | ?                |                | From the manual:        '`get` is somewhat like `prop`; however its value is car of the rest of    the list if the `indicator` is found, and NIL otherwise.'        It's clear that `GET` is expected to be defined in terms of `PROP`, but    we can't implement `PROP` here because we lack `EVAL`; and we can't have    `EVAL` here because both it and `APPLY` depends on `GET`.        OK, It's worse than that: the statement of the definition of `GET` (and     of) `PROP` on page 59 says that the first argument to each must be a list;    But the in the definition of `ASSOC` on page 70, when `GET` is called its    first argument is always an atom. Since it's `ASSOC` and `EVAL` which I     need to make work, I'm going to assume that page 59 is wrong. |
| GREATERP     | Host lambda function | ?                | PREDICATE      | ?                    |
| INTEROP      | Host lambda function | ?                | ?              | ?                    |
| INTERSECTION | Lisp lambda function | ?                | ?              | ?                    |
| LENGTH       | Lisp lambda function | ?                |                | see manual pages <a href='#page62'>62</a> |
| LESSP        | Host lambda function | ?                | PREDICATE      | ?                    |
| MAPLIST      | Lisp lambda function | ?                | FUNCTIONAL     | see manual pages <a href='#page20'>20</a>, <a href='#page21'>21</a>, <a href='#page63'>63</a> |
| MEMBER       | Lisp lambda function | ?                | PREDICATE      | see manual pages <a href='#page11'>11</a>, <a href='#page62'>62</a> |
| MINUSP       | Lisp lambda function | ?                | PREDICATE      | see manual pages <a href='#page26'>26</a>, <a href='#page64'>64</a> |
| NOT          | Lisp lambda function | ?                | PREDICATE      | see manual pages <a href='#page21'>21</a>, <a href='#page23'>23</a>, <a href='#page58'>58</a> |
| NULL         | Lisp lambda function | ?                | PREDICATE      | see manual pages <a href='#page11'>11</a>, <a href='#page57'>57</a> |
| NUMBERP      | Host lambda function | ?                | PREDICATE      | ?                    |
| OBLIST       | Host lambda function | ?                |                | Return a list of the symbols currently bound on the object list.        **NOTE THAT** in the Lisp 1.5 manual, footnote at the bottom of page 69, it implies     that an argument can be passed but I'm not sure of the semantics of    this. |
| ONEP         | Lisp lambda function | ?                | PREDICATE      | see manual pages <a href='#page26'>26</a>, <a href='#page64'>64</a> |
| OR           | Host lambda function | ?                | PREDICATE      | `T` if and only if at least one of my `args` evaluates to something other   than either `F` or `NIL`, else `F`.        In `beowulf.host` principally because I don't yet feel confident to define    varargs functions in Lisp. |
| PAIR         | Lisp lambda function | ?                |                | see manual pages <a href='#page60'>60</a> |
| PAIRLIS      | Lisp lambda function, Host lambda function | ?                | ?              | This function gives the list of pairs of corresponding elements of the   lists `x` and `y`, and APPENDs this to the list `a`. The resultant list   of pairs, which is like a table with two columns, is called an   association list.    Eessentially, it builds the environment on the stack, implementing shallow   binding.    All args are assumed to be `beowulf.cons-cell/ConsCell` objects.   See page 12 of the Lisp 1.5 Programmers Manual.        **NOTE THAT** this function is overridden by an implementation in Lisp,    but is currently still present for bootstrapping. |
| PLUS         | Host lambda function | ?                |                | ?                    |
| PRETTY       |                | ?                | ?              | ?                    |
| PRINT        |                | ?                | PSEUDO-FUNCTION  | see manual pages <a href='#page65'>65</a>, <a href='#page84'>84</a> |
| PROG         | Host nlambda function | ?                |                | The accursed `PROG` feature. See page 71 of the manual.        Lisp 1.5 introduced `PROG`, and most Lisps have been stuck with it ever     since. It introduces imperative programming into what should be a pure     functional language, and consequently it's going to be a pig to implement.        Broadly, `PROG` is a variadic pseudo function called as a `FEXPR` (or     possibly an `FSUBR`, although I'm not presently sure that would even work.)     The arguments, which are unevaluated, are a list of forms, the first of     which is expected to be a list of symbols which will be treated as names     of variables within the program, and the rest of which (the 'program body')    are either lists or symbols. Lists are treated as Lisp expressions which    may be evaulated in turn. Symbols are treated as targets for the `GO`     statement.            **GO:**     A `GO` statement takes the form of `(GO target)`, where     `target` should be one of the symbols which occur at top level among that    particular invocation of `PROG`s arguments. A `GO` statement may occur at     top level in a PROG, or in a clause of a `COND` statement in a `PROG`, but    not in a function called from the `PROG` statement. When a `GO` statement    is evaluated, execution should transfer immediately to the expression which    is the argument list immediately following the symbol which is its target.     If the target is not found, an error with the code `A6` should be thrown.     **RETURN:**     A `RETURN` statement takes the form `(RETURN value)`, where     `value` is any value. Following the evaluation of a `RETURN` statement,     the `PROG` should immediately exit without executing any further     expressions, returning the  value.     **SET and SETQ:**    In addition to the above, if a `SET` or `SETQ` expression is encountered    in any expression within the `PROG` body, it should affect not the global    object list but instead only the local variables of the program.     **COND:**    In **strict** mode, when in normal execution, a `COND` statement none of     whose clauses match should not return `NIL` but should throw an error with    the code `A3`... *except* that inside a `PROG` body, it should not do so.    *sigh*.     **Flow of control:**    Apart from the exceptions specified above, expressions in the program body    are evaluated sequentially. If execution reaches the end of the program     body, `NIL` is returned.     Got all that?     Good. |
| PROP         | Lisp lambda function | ?                | FUNCTIONAL     | see manual pages <a href='#page59'>59</a> |
| QUOTE        | Lisp lambda function | ?                |                | see manual pages <a href='#page10'>10</a>, <a href='#page22'>22</a>, <a href='#page71'>71</a> |
| QUOTIENT     | Host lambda function | ?                |                | I'm not certain from the documentation whether Lisp 1.5 `QUOTIENT` returned   the integer part of the quotient, or a realnum representing the whole   quotient. I am for now implementing the latter. |
| RANGE        | Lisp lambda function | ?                | ?              | ?                    |
| READ         | Host lambda function | ?                | PSEUDO-FUNCTION  | An implementation of a Lisp reader sufficient for bootstrapping; not necessarily   the final Lisp reader. `input` should be either a string representation of a LISP   expression, or else an input stream. A single form will be read. |
| REMAINDER    | Host lambda function | ?                |                | ?                    |
| REPEAT       | Lisp lambda function | ?                | ?              | ?                    |
| RPLACA       | Host lambda function | ?                | PSEUDO-FUNCTION | Replace the CAR pointer of this `cell` with this `value`. Dangerous, should   really not exist, but does in Lisp 1.5 (and was important for some   performance hacks in early Lisps) |
| RPLACD       | Host lambda function | ?                | PSEUDO-FUNCTION | Replace the CDR pointer of this `cell` with this `value`. Dangerous, should   really not exist, but does in Lisp 1.5 (and was important for some   performance hacks in early Lisps) |
| SEARCH       | Lisp lambda function | ?                | FUNCTIONAL     | see manual pages <a href='#page63'>63</a> |
| SET          | Host lambda function | ?                | PSEUDO-FUNCTION | Implementation of SET in Clojure. Add to the `oblist` a binding of the    value of `var` to the value of `val`. NOTE WELL: this is not SETQ! |
| SUB1         | Lisp lambda function, Host lambda function | ?                |                | ?                    |
| SUB2         | Lisp lambda function | ?                | ?              | ?                    |
| SUBLIS       | Lisp lambda function | ?                |                | see manual pages <a href='#page12'>12</a>, <a href='#page61'>61</a> |
| SUBST        | Lisp lambda function | ?                |                | see manual pages <a href='#page11'>11</a>, <a href='#page61'>61</a> |
| SYSIN        | Host lambda function | ?                | ?              | Read the contents of the file at this `filename` into the object list.         If the file is not a valid Beowulf sysout file, this will probably     corrupt the system, you have been warned. File paths will be considered     relative to the filepath set when starting Lisp.     It is intended that sysout files can be read both from resources within    the jar file, and from the file system. If a named file exists in both the    file system and the resources, the file system will be preferred.        **NOTE THAT** if the provided `filename` does not end with `.lsp` (which,    if you're writing it from the Lisp REPL, it won't), the extension `.lsp`    will be appended.        **NOTE THAT** this is an extension function, not available in strct mode. |
| SYSOUT       | Host lambda function | ?                | ?              | Dump the current content of the object list to file. If no `filepath` is    specified, a file name will be constructed of the symbol `Sysout` and     the current date. File paths will be considered relative to the filepath    set when starting Lisp.        **NOTE THAT** this is an extension function, not available in strct mode. |
| TERPRI       |                | ?                | PSEUDO-FUNCTION | see manual pages <a href='#page65'>65</a>, <a href='#page84'>84</a> |
| TIMES        | Host lambda function | ?                |                | ?                    |
| TRACE        | Host lambda function | ?                | PSEUDO-FUNCTION | Add this `s` to the set of symbols currently being traced. If `s`    is not a symbol or sequence of symbols, does nothing. |
| UNION        | Lisp lambda function | ?                | ?              | ?                    |
| UNTRACE      | Host lambda function | ?                | PSEUDO-FUNCTION | Remove this `s` from the set of symbols currently being traced. If `s`    is not a symbol or sequence of symbols, does nothing. |
| ZEROP        | Lisp lambda function | ?                | PREDICATE      | see manual pages <a href='#page26'>26</a>, <a href='#page64'>64</a> |

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

Download the latest [release 'uberjar'](https://github.com/simon-brooke/beowulf/releases) and run it using:

```bash
    java -jar <path name of uberjar>
```

Or clone the source and build it using:

```bash
    lein uberjar`
```

To build it you will require to have [Leiningen](https://leiningen.org/) installed.

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

### Other implementations

There's an online (browser native) Lisp 1.5 implementation [here](https://pages.zick.run/ichigo/) (source code [here](https://github.com/zick/IchigoLisp)). It 
even has a working compiler!

### History resources

I'm compiling a [list of links to historical documents on Lisp 1.5](https://simon-brooke.github.io/beowulf/docs/codox/further_reading.html).

## License

Copyright © 2019 Simon Brooke. Licensed under the GNU General Public License,
version 2.0 or (at your option) any later version.
