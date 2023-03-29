# beowulf

LISP 1.5 is to all Lisp dialects as Beowulf is to Emglish literature.

## What this is

A work-in-progress towards an implementation of Lisp 1.5 in Clojure. The
objective is to build a complete and accurate implementation of Lisp 1.5
as described in the manual, with, in so far as is possible, exactly the
same bahaviour - except as documented below.

### Status

Boots to REPL, but few functions yet available.

* [Project website](https://simon-brooke.github.io/beowulf/).
* [Source code documentation](https://simon-brooke.github.io/beowulf/docs/codox/index.html).
* [Test Coverage Report](https://simon-brooke.github.io/beowulf/docs/cloverage/index.html)


### Building and Invoking

Build with

    lein uberjar

Invoke with

    java -jar target/uberjar/beowulf-0.2.1-SNAPSHOT-standalone.jar --help

(Obviously, check your version number)

Command line arguments as follows:

```
  -h, --help                               Print this message
  -p PROMPT, --prompt PROMPT    Sprecan::  Set the REPL prompt to PROMPT
  -r INITFILE, --read INITFILE             Read Lisp functions from the file INITFILE
  -s, --strict                             Strictly interpret the Lisp 1.5 language, without extensions.
  -t, --trace                              Trace Lisp evaluation.
```

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
provided by `beowulf.host`, be sufficient to bootstrap the full Lisp 1.5
interpreter.

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
implementations. I'm convinced you could still use Lisp 1.5 for interesting
and useful software (which isn't to say that some modern Lisps aren't better,
but this is software which is almost sixty years old).

## Installation

At present, clone the source and build it using

`lein uberjar`.

You will require to have [Leiningen](https://leiningen.org/) installed.

## Usage

`java -jar beowulf-0.1.0-standalone.jar`

This will start a Lisp 1.5 read/eval/print loop (REPL).

Command line arguments are as follows:

```
  -f FILEPATH, --file-path FILEPATH                         Set the path to the directory for reading and writing Lisp files.
  -h, --help
  -p PROMPT, --prompt PROMPT         Sprecan::              Set the REPL prompt to PROMPT
  -r INITFILE, --read INITFILE       resources/lisp1.5.lsp  Read Lisp system from file INITFILE
  -s, --strict                                              Strictly interpret the Lisp 1.5 language, without extensions.
  -t, --trace                                               Trace Lisp evaluation.
```

To end a session, type `STOP` at the command prompt.

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
