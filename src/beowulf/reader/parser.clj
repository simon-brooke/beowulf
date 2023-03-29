(ns beowulf.reader.parser
  "The actual parser, supporting both S-expression and M-expression syntax."
  (:require [instaparse.core :as i]))

(def parse
  "Parse a string presented as argument into a parse tree which can then
  be operated upon further."
  (i/parser
   (str
    ;; we tolerate whitespace and comments around legitimate input
    "raw := expr | opt-comment expr opt-comment;"
        ;; top level: we accept mexprs as well as sexprs.
    "expr := mexpr | sexpr ;"

      ;; comments. I'm pretty confident Lisp 1.5 did NOT have these.
    "comment := opt-space <';;'> opt-space #'[^\\n\\r]*';"

     ;; there's a notation comprising a left brace followed by mexprs
     ;; followed by a right brace which doesn't seem to be documented 
     ;; but I think must represent a prog(?)

    ;; "prog := lbrace exprs rbrace;"
      ;; mexprs. I'm pretty clear that Lisp 1.5 could never read these,
      ;; but it's a convenience.

    "exprs := expr | exprs;"
    "mexpr := λexpr | fncall | defn | cond | mvar | mconst | iexpr | number | mexpr comment;
      λexpr := λ lsqb bindings semi-colon body rsqb;
      λ := 'λ';
      bindings := lsqb args rsqb;
      body := (mexpr semi-colon opt-space)* mexpr;
      fncall := fn-name lsqb args rsqb;
      lsqb := '[';
      rsqb := ']';
       lbrace := '{';
       rbrace := '}';
      defn := mexpr opt-space '=' opt-space mexpr;
      cond := lsqb (opt-space cond-clause semi-colon opt-space)* cond-clause rsqb;
      cond-clause := mexpr opt-space arrow opt-space mexpr opt-space;
      arrow := '->';
      args := (opt-space mexpr semi-colon opt-space)* mexpr;
      fn-name := mvar;
      mvar := #'[a-z]+';
      mconst := #'[A-Z]+';
      semi-colon := ';';"

    ;; Infix operators appear in mexprs, e.g. on page 7. Ooops!
    ;; I do not know what infix operators are considered legal.
    ;; In particular I do not know what symbol was used for
    ;; multiply
    "iexpr := iexp iop iexp;
     iexp := mexpr | number | opt-space iexp opt-space;
    iop := '>' | '<' | '+' | '-' | '*' '/' | '=' ;"

      ;; comments. I'm pretty confident Lisp 1.5 did NOT have these.
    "opt-comment := opt-space | comment;"
    "comment := opt-space <';;'> #'[^\\n\\r]*' opt-space;"

      ;; sexprs. Note it's not clear to me whether Lisp 1.5 had the quote macro,
      ;; but I've included it on the basis that it can do little harm.
    "sexpr := quoted-expr | atom | number | dotted-pair | list | sexpr comment;
      list := lpar sexpr rpar | lpar (sexpr sep)* rpar | lpar (sexpr sep)* dot-terminal | lbrace exprs rbrace;
      list := lpar opt-space sexpr rpar | lpar opt-space (sexpr sep)* rpar | lpar opt-space (sexpr sep)* dot-terminal;
      dotted-pair := lpar dot-terminal ;
      dot := '.';
      lpar := '(';
      rpar := ')';
      quoted-expr := quote sexpr;
      quote := '\\'';
      dot-terminal := sexpr space dot space sexpr rpar;
      space := #'\\p{javaWhitespace}+';
      opt-space := #'\\p{javaWhitespace}*';
      sep := ',' | opt-space;
      atom := #'[A-Z][A-Z0-9]*';"

      ;; Lisp 1.5 supported octal as well as decimal and scientific notation
    "number := integer | decimal | scientific | octal;
      integer := #'-?[1-9][0-9]*';
      decimal := integer dot integer;
      scientific := coefficient e exponent;
      coefficient := decimal | integer;
      exponent := integer;
      e := 'E';
      octal := #'[+-]?[0-7]+{1,12}' q scale-factor;
      q := 'Q';
      scale-factor := #'[0-9]*'")))

