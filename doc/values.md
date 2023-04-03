# The properties of the system, and their values: here be dragons

Lisp is the list processing language; that is what its name means. It processes data structures built of lists - which may be lists of lists, or lists of numbers, or lists of any other sort of data item provided for by the designers of the system.

But how is a list, in a computer, actually implemented?

They're implemented as pairs, or, as the manual sometimes rather delightfully called them, 'doublets'. Pairs of what? Pairs of pointers. Of the two pointers of a pair, the first points to the current entry of the list, and the second, by default, points to the remainder of the list, or, if the end of the list has been reached, to a special datum known as `NIL` which among other things indicates that the end of the list has been reached. The pair itself is normally referred to as a 'cons cell' for reasons which are nerdy and not important just now (all right, because they are constructed using a function called `cons`, which is in itself believed to be simply an abbreviation of 'construct').

Two functions are used to access the two pointers of the cell. In modern Lisps these functions are called `first` and `rest`, because a lot of people who aren't greybeards find these names easier. But they aren't the original names. The original names were `CAR` and `CDR`.

Why?

## History

Lisp was originally written on an [IBM 704 computer at Massachusetts Institute of Technology](https://comphist.dhlab.mit.edu/archives/story/IBM_mechanics), almost seventy years ago. 

The machine had registers which were not eight, or sixteen, or thirty two, or sixty four, bits wide, or any other number which would seem rational to modern computer scientists, but thirty six. Myth - folk memory - tells us that the machine's memory was arranged in pages. As I understand it (but this truly is folk memory) the offset within the page of the word to be fetched was known as the 'decrement', while the serial number of the page in the addressing sequence was known as the 'address'. To fetch a word from memory, you first had to select the page using the 'address', and secondly the word itself using the 'decrement'. So there were specific instructions for selecting the address, and the decrement, from the register separately.

There were two mnemonics for the machine instructions used to access the content of these registers, respectively:

CAR

: *Contents of the Address part of Register*; and

CDR

: *Contents of the Decrement part of Register*.

Is this actually true?

I think so. If you look at [page 80 of the Lisp 1 Programmer's Manual](https://bitsavers.org/pdf/mit/rle_lisp/LISP_I_Programmers_Manual_Mar60.pdf#page=89), you will see this:

```IBM704
TEN                       (the TEN-Mode is entered)

O CAR   (((A,B),C)) () \
                        |
:1 CDR  ((D,(E,F))) ()  |
                         > Type ins
:2 CONS ((G,H),         |
                        |
230 (I,J)) ()          /
RLN | | oo 7 a |

O14 (read lines O and 1)
```

Of course, this isn't proof. If `CAR` and `CDR` used here are standard IBM 704 assembler mnemonics -- as I believe they are -- then what is `CONS`? It's used in a syntactically identical way. If it also is an assembler mnemonic, then it's hard to believe that, as legend relates, it is short for 'construct'; on the other hand, if it's a label representing an entry point into a subroutine, then why should `CAR` and `CDR` not also be labels?

I think that the answer has to be that if `CAR` and `CDR` had been named by the early Lisp team -- John McCarthy and his immediate colleagues -- they would not have been named as they were. If not `FRST` and `REST`, as in more modern Lisps, then something like `P1` and `P2`. `CAR` and `CDR` are distinctive and memorable (and therefore in my opinion worth preserving) because they very specifically name the parts of a cons cell and of nothing else.

Let's be clear, here: when `CAR` and `CDR` are used in Lisp, they are returning pointers, certainly -- but not in the sense that one points to a page and the other to a word. Each is an offset into a cell array, which is almost certainly an array of single 36 bit words held on a single page. So both are in effect being used as decrements. Their use in Lisp is an overload onto their original semantic meaning; they are no longer being used for the purpose for which they are named.

As far as I can tell, these names first appear in print in 1960, both in the Lisp 1 Programmer's Manual referenced above, and in McCarthy's paper [Recursive Functions of Symbolic Expressions and Their Computation by Machine, Part I](https://web.archive.org/web/20230328222647/http://www-formal.stanford.edu/jmc/recursive.pdf). The paper was published in April so was presumably written in 1959

## Grey Anatomy

### The Object List

Lisp keeps track of values by associating them with names. It does so by having what is in effect a global registry of all the names it knows to which values are attached. This being a list processing language, that was of course, in early Lisps, a list: a single specialised first class list known as the 'object list', or `oblist` for short.

Of course, a list need not just be a list of single items, it can be a list of pairs: it can be a list of pairs of the form `(name . value)`. Hold onto that, because I want to talk about another fundamental part of a working Lisp system, the stack. 

### The Stack

Considering the case of pure interpreter first, let's think about how a function keeps track of the data it's working on. In order to do its work, it probably calls other functions, to which it passes off data, and they in turn probably call further functions. So, when control returns to our first function, how does it know where its data is? The answer is that each function pushes its argument bindings onto the stack when it starts work, and pops them off again when it exits. So when control returns to a function, its own data is still on the top of the stack. Or, to be precise, actually it doesn't; in practice the function [`EVAL`](https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=79) does it for each function in turn. But it doesn't matter: it gets done.

What is this stack? Well, it's a list of `(name . value)` pairs. At least, it is in pure Lisps; [Clojure](https://clojure.org/), because it runs on the [Java Virtual Machine](https://en.wikipedia.org/wiki/Java_virtual_machine) and interoperates with other software running on the JVM, uses the JVM stack which is a permanently reserved vector of memory never used for anything else. Consequently it cannot be very large; and the consequence of that is that it's very easy to crash JVM programs because they've run out of stack space.

The advantage of organising your stack as a vector is that on average it's usually slightly more memory efficient, and that it's somewhat faster to access. The disadvantage is you need a contiguous block of memory for it, and once you've run out, you've at best lost both those advantages but in the normal case your program just crashes. Also, the memory you've reserved for the stack isn't available for any other use, even during the most of the time that the stack isn't using most of it. So of course there's a temptation to keep the amount reserved for the stack as small as possible.

It's this brutal fragility of vector stacks -- which are used by most modern computer languages -- which makes software people so wary of fully exploiting the beauty and power of recursion, and I really think that's a shame.

The advantage of organising your stack as a list is that, while there is any memory left on the machine at all, you cannot run out of stack.

### The Spine

So, there's an object list where you associate names and values, and there's a stack where you associate names and values. But, why do they have to be different? And why do you have to search in two places to find the value of a name?

The answer is -- or it was, and arguably it should be -- that you don't. The stack can simply be pushed onto the front of the object list. This has multiple advantages. The first and most obvious is that you only have to search in one place for the value associated with a name. 

The second is more subtle: a function can mask a variable in the object list by binding the same name to a new value, and the functions to which it then calls will only see that new value. This is useful if, for example, printed output is usually sent to the user's terminal, but for a particular operation you want to send it to a line printer or to a file on disk. You simply rebind the name of the standard output stream to your chosen output stream, and call the function whose output you want to redirect.

So, in summary, there's a lot of merit in making the stack and the object list into a single central structure on which the architecture of our Lisp system is built. But there's more we need to record, and it's important.

### Fields and Properties

No, I'm not banging on about [land reform](https://www.journeyman.cc/blog/tags-output/Levelling/) again! I'm not a total monomaniac!

But there's more than one datum we may want to associate with a single name. A list can be more than a set of bindings between single names and single values. There's more than one thing, for example, that I know about my friend Lucy. I know her age. I know her address. I know her height. I know her children. I know her mother. All of this information -- and much more -- should be associated with her.

In conventional computing systems we'd use a table. We'd put into the table a field -- a column -- for each datum we wanted to store about a class of things of interest. And we'd reserve space to store that datum for every record, whether every record had something to go there of not. Furthermore, we'd have to reserve space in each field for the maximum size of the datum to be stored in it -- so if we needed to store full names for even some of the people we knew, and one of the people whose full name we needed to store (because he's both very important and very irascible) was *Charles Philip Arthur George Windsor*, then we'd have to reserve space for thirty-six characters for the full name of everyone in our records, even if for most of them half that would be enough.

But if instead of storing a table for each sort of thing on which we hold data, and a row in that table for each item of that sort on which we store data, we simply tagged each thing on which we hold data with those things which are interesting about them? We could tag my friend Lucy with the fact she's on pilgrimage, and what her pilgrimage route is. Those aren't things we need to know about most people, it would be absurdly wasteful to add a column to a `person` table to record `pilgrimage route`. So in a conventional data system we would lose that data.

Lisp has had, right back from the days of Lisp 1.5 -- so, for sixty-five years -- a different solution. We can give every symbol arbitrarily many, arbitrarily different, properties. A property is a `(name . value)` pair. We don't have to store the same properties for every object. The values of the properties don't have to have a fixed size, and they don't have to take up space they don't need. It's like having a table with as many fields as we choose, and being able to add more fields at any time.

So, in summary, I knew, in building Beowulf, that I'd have to implement property lists. I just didn't know how I was going to do it.

## Archaeology

What I'm doing with Beowulf is trying to better understand the history of Lisp by reconstructing a very early example; in this case, Lisp 1.5, from about 1962, or sixty one years ago.

I had had the naive assumption that entries on the object list in early Lisps had their `CAR` pointing to the symbol and their `CDR` pointing to the related value. Consequently, in building [beowulf](https://github.com/simon-brooke/beowulf), I could not work out where the property list went. More careful reading of [the text](https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=67) implies, but does not explicitly state, that my naive assumption is wrong.

Instead, it appears that the `CAR` points to the symbol, as expected, but the `CDR` points to the property list; and that on the property list there are privileged properties at least as follows:

APVAL
: the simple straightforward ordinary value of the symbol, considered as a variable;

EXPR
: the definition of the function considered as a normal lambda expression (arguments to be evaluated before applying);

FEXPR
: the definition of a function which should be applied to unevaluated arguments (what InterLisp and Portable Standard Lisp would call ['*nlambda*'](https://citeseerx.ist.psu.edu/document?repid=rep1&type=pdf&doi=fe0f4f19cee0c607d7b229feab26fc9ed559fc9c#page=9));

SUBR
: the definition of a compiled subroutine which should be applied to evaluated arguments;

FSUBR
: the definition of a compiled subroutine which should be applied to unevaluated arguments.

I think there was also another privileged property value which contained the property considered as a constant, but I haven't yet confirmed that.

From this it would seem that Lisp 1.5 was not merely a ['Lisp 2'](http://xahlee.info/emacs/emacs/lisp1_vs_lisp2.html) but in fact a 'Lisp 6', with six effectively first class namespaces. In fact it's not as bad as that, because of the way [`EVAL`](https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=79) is evaluated.

Essentially the properties are tried in turn, and only the first value found is used. Thus the heirarchy is

1. APVAL
2. EXPR
3. FEXPR
4. SUBR
5. FSUBR

This means that, while the other potential values can be retrieved from the property list, interpreted definitions (if present) will always be preferred to uninterpreted definitions, and lambda function definitions (which evaluate their arguments), where present, will always be preferred to non-lamda definitions, which don't.

**BUT NOTE THAT** the `APVAL` value is sought only when seeking a variable value for the symbol, while the others are only when seeking a function value, so Lisp 1.5 is a 'Lisp 2', not a 'Lisp 1'. I strongly believe that this is wrong: a function is a value, and should be treated as such. But at the same time I do acknowledge the benefit of being able to store both source and compiled forms of the function as properties of the same symbol.

## The persistent problem

There's a view in modern software theory -- with which I strongly hold -- that data should be immutable. Data that changes under you is the source of all sorts of bugs. And in modern multi threaded systems, the act of reading a datum whilst some other process is writing it, or worse, two processes attempting simultaneously to write the same datum, is a source of data corruption and even crashes. So I'm very wary of mutable data; and, in modern systems where we normally have a great deal of space and a lot of processor power, making fresh copies of data structures containing the change we wanted to make is a reasonable price to pay for avoiding a whole class of bugs.

But early software was not like that. It was always constrained by the limits of the hardware on which it ran, to a degree that we are not. And the experience that we now have of the problems caused by mutable data, they did not have. So it's core to the design of Lisp 1.5 that its lists are mutable; and, indeed, one of the biggest challenges in writing Beowulf has been [implementing mutable lists in Clojure](https://github.com/simon-brooke/beowulf/blob/master/src/beowulf/cons_cell.clj#L19), a language carefully designed to prevent them.

But, just because Lisp 1.5 lists can be mutable, should they be? And when should they be?

The problem here is that [spine of the system](#the_spine) I talked about earlier. If we build the execution stack on top of the oblist -- as at present I do -- then if we make a new version of the oblist with changes in it, the new changes will not be in the copy of the oblist that the stack is built on top of; and consequently, they'll be invisible to the running program.

What I do at present, and what I think may be good enough, is that each time execution returns to the read-eval-print loop, the REPL, the user's command line, I rebuild a new execution stack on the top of the oblist as it exists now. So, if the last operation modified the oblist, the next operation will see the new, modified version. But if someone tried to run some persistent program which was writing stuff to property values and hoping to read them back in the same computation, that wouldn't work, and it would be a very hard bug to trace down.

So my options are:

1. To implement `PUT` and `GET` in Clojure, so that they can operate on the current copy of the object list, not the one at the base of the stack. I'm slightly unwilling to do that, because my objective is to make Beowulf ultimately as self-hosting as possible.
2. To implement `PUT` and `GET` in Lisp, and have them destructively modify the working copy of the object list.

Neither of these particularly appeal.

## How property lists should work

I'm still not fully understanding how property lists in Lisp 1.5 are supposed to work. 

### List format

Firstly, are they association lists comprising dotted pairs of `(property-name . value)`, i.e.: 

>((property-name<sub>1</sub> . value<sub>1</sub>) (property-name<sub>2</sub> . value<sub>2</sub>) ... (property-name<sub>n</sub> . value<sub>n</sub>))

I have assumed so, and that is what I presently intend to implement, but the diagrams on [pages 59 and 60](https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=67) seem rather to show a flat list of interleaved names and values:

> (property-name<sub>1</sub> value<sub>1</sub> property-name<sub>2</sub> value<sub>2</sub> ... property-name<sub>n</sub> value<sub>n</sub>)

I cannot see what the benefit of this latter arrangement is, and I'm unwilling to do it, although I think it may be what was done. But if it was done that way, *why* was it done that way? These were bright people, and they certainly knew about association lists. So... I'm puzzled.

### Function signatures

To associate the value of a property with a symbol, we need three things: we need the symbol, we need the property name, and we need the value. For this reason, [Portable Standard Lisp](https://www.softwarepreservation.org/projects/LISP/utah/USCP-Portable_Standard_LISP_Users_Manual-TR_10-1984.pdf#page=44) and others has a function `put` with three arguments:

> `(Put U:id IND:id PROP:any)`: any
> The indicator `IND` with the property `PROP` is placed on the property list of
> the id `U`. If the action of Put occurs, the value of `PROP` is returned. If
> either of `U` and `IND` are not ids the type mismatch error occurs and no
> property is placed.
> `(Put 'Jim 'Height 68)`
> The above returns `68` and places `(Height . 68)` on the property list of the id `Jim`

Cambridge Lisp is identical to this except in lower case. [InterLisp](https://larrymasinter.net/86-interlisp-manual-opt.pdf#page=37) and several others have `putprop`:

> `(PUTPROP ATM PROP VAL) [Function]`
> Puts the property `PROP` with value `VAL` on the property list of `ATM`. `VAL` replaces
> any previous value for the property `PROP` on this property list. Returns `VAL`.

The execrable Common Lisp uses its execrable macro [`setf`](https://www.cs.cmu.edu/Groups/AI/html/cltl/clm/node108.html) but really the less said about that the better.

So I was looking for a function of three arguments to set properties, and I didn't find one. 

There's a function `DEFINE` which takes one argument, an association list of pairs:
```lisp
	(function-name . function-definition)`
```
So how does that work, if what it's doing is setting properties? If all you're passing is pairs of name and definition, where does the property name come from?

The answer is as follows, taken from [the manual](https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf#page=66):

> #### define [x] : EXPR pseudo-function

> The argument of `define`, `x`, is a list of pairs

> > ((u<sub>l</sub> v<sub>l</sub>) (u<sub>2</sub> v<sub>2</sub>) ... (u<sub>n</sub> v<sub>n</sub>))

> where each `u` is a name and each `v` is a &lambda;-expression for a function . For each `pair`, define puts an `EXPR` on the property list for `u` pointing to `v`. The function of `define` puts things on at the front of the property list. The value of `define` is the list of `u`s.

So, in fact, the value of the property being set by `define` is fixed: hard wired, not parameterised. That seems an astonishing decision, until you realise that Lisp 1.5's creators weren't creating their functions one by one, in a playful exploration with their system, but entering them in a batch.

## Learning by doing

In fact, when I got over my surprise, I realised that that `(name . function-definition)` list is actually very much like this, which is an excerpt from a sysout from a Beowulf prototype:

```lisp
(...
	(MAPLIST LAMBDA (L F) 
           (COND ((NULL L) NIL) 
                 ((QUOTE T) (CONS (F (CAR L)) (MAPLIST (CDR L) F)))))
  (MEMBER LAMBDA (A X)
            (COND ((NULL X) (QUOTE F))
                  ((EQ A (CAR X)) (QUOTE T)) 
                  ((QUOTE T) (MEMBER A (CDR X)))))
  (MINUSP LAMBDA (X) (LESSP X 0))
  (NOT LAMBDA (X) 
       			(COND (X (QUOTE NIL)) 
                  ((QUOTE T) (QUOTE T))))
  (NULL LAMBDA (X) 
        		(COND ((EQUAL X NIL) (QUOTE T)) 
                  (T (QUOTE F))))
...)
```

I was looking at `DEFINE` and thinking, 'why would one ever want to do that?' and then I found that, behind the scenes, I was actually doing it myself.

Because the point of a sysout is you don't write it. The point about the REPL -- the Read Eval Print Loop which is the heart of the interactive Lisp development cycle, where you sit playing with things and fiddling with them interactively, and where when one thing works you get onto the next without bothering to make some special effort to record it.

The point of a sysout is that, at the end of the working day, you invoke one function

| Function | Type | Signature | Implementation | Documentation |
| -------- | ---- | --------- | -------------- | ------------- |
| SYSOUT       | Host function  | (SYSOUT); (SYSOUT FILEPATH) | SUBR    | Dump the current content of the object list to file. If no `filepath` is    specified, a file name will be constructed of the symbol `Sysout` and the current date. File paths will be considered relative to the filepath set when starting Lisp. |

At the start of the next working day, you load that sysout in and continue your session.

The sysout captures the entire working state of the machine. No-one types it in, as an operation in itself. Instead, data structures -- corpuses of functions among them -- simply build up on the object list almost casually, as a side effect of the fact that you're enjoying exploring your problem and finding elegant ways of solving it. So `SYSOUT` and `SYSIN` seem to me, as someone who all his adult life has worked with Lisp interactively, as just an automatic part of the cycle of the day.

## The process of discovery

The thing is, I don't think anyone is ever going to use Beowulf the way Lisp 1.5 was used. I mean, probably, no one is ever going to use Beowulf at all; but if they did they wouldn't use Beowulf the way Lisp 1.5 was used. 

I'm a second generation software person. I have worked, in my career, with two people who personally knew and had worked with [Alan Turing](https://en.wikipedia.org/wiki/Alan_Turing). I have worked with, and to an extent been mentored by, [Chris Burton](https://www.bcs.org/articles-opinion-and-research/manchester-s-place-in-computing-history-marked/), who in his apprenticeship was part of the team that built the Manchester Mark One, and who in his retirement led the team who restored it. But I never knew the working conditions they were accustomed to. In my first year at university we used card punches, and, later, when we had a bit of seniority, teletypewriters (yes, that's what TTY stands for), but by the time I'd completed my undergraduate degree and become a research associate I had a Xerox 1108 workstation with a huge bitmapped graphic screen, and an optical mouse, goddamit, running InterLisp, all to myself.

People in the heroic age did not have computers all to themselves. They did not have terminals all to themselves. They didn't sit at a terminal experimenting in the REPL. They wrote their algorithms in pencil on paper. When they were certain they'd got it right, they'd use a card punch to punch a deck of cards carrying the text of the program, and then they were certain they'd got *that* right, they'd drop it into the input hopper. Some time later their batch would run, and the operator would put the consequent printout into their pigeon hole for them to collect.

(They wrote amazingly clean code, those old masters. I could tell you a story about Chris Burton, the train, and the printer driver, that software people of today simply would not believe. But it's true. And I think that what taught them that discipline was the high cost of even small errors.)

Lisp 1.5 doesn't have `PUT`, `PUTPROP` or `DEFUN` because setting properties individually, defining functions individually one at a time, was not something they ever thought about doing. And in learning that, I've learned more than I ever expected to about the real nature of Lisp 1.5, and the (great) people who wrote it.

-----

So what this is about is I've spent most of a whole day procrastinating, because I'm not exactly sure how I'm going to make the change I've got to make. Versions of Beowulf up to and including 0.2.1 used the naive understanding of the architecture; version 0.3.0 *should* use the corrected version. But before it can, I need to be reasonably confident that I understand what the correct solution is.

I *shall* implement `PUT`, even though it isn't in the spec, because it's a useful building block on which to build `DEFINE` and `DEFLIS`, both of which are. And also, because `PUT` would have been very easy for the Lisp 1.5 implementers to implement, if it had been relevant to their working environment.
