(ns beowulf.reader.parser
  "The actual parser, supporting both S-expression and M-expression syntax."
  (:require [instaparse.core :as i]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;
;;; Copyright (C) 2022-2023 Simon Brooke
;;;
;;; This program is free software; you can redistribute it and/or
;;; modify it under the terms of the GNU General Public License
;;; as published by the Free Software Foundation; either version 2
;;; of the License, or (at your option) any later version.
;;; 
;;; This program is distributed in the hope that it will be useful,
;;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;; GNU General Public License for more details.
;;; 
;;; You should have received a copy of the GNU General Public License
;;; along with this program; if not, write to the Free Software
;;; Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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
    ;; but I think must represent assembly code(?)

    ;; "assembly := lbrace exprs rbrace;"

    ;; mexprs. I'm pretty clear that Lisp 1.5 could never read these,
    ;; but it's a convenience.

    ;; TODO: this works for now but in fact the Programmer's Manual
    ;; gives a much simpler formulation of M-expression grammar on
    ;; page 9, and of the S-expression grammar on page 8. It would
    ;; be worth going back and redoing this from the book.

    "exprs := expr | exprs;"
    "mexpr := λexpr | fncall | defn | cond | mvar | mconst | iexpr | number | mexpr comment;
      λexpr := λ lsqb bindings semi-colon opt-space body opt-space rsqb;
      λ := 'λ' | 'lambda';
      bindings := lsqb args rsqb | lsqb rsqb;
      body := (opt-space mexpr semi-colon)* opt-space mexpr;
      fncall := fn-name bindings;
      lsqb := '[';
      rsqb := ']';
      lbrace := '{';
      rbrace := '}';
      defn := mexpr opt-space '=' opt-space mexpr;
      cond := lsqb (opt-space cond-clause semi-colon opt-space)* cond-clause rsqb;
      cond-clause := mexpr opt-space arrow opt-space mexpr opt-space;
      arrow := '->';
      args := arg | (opt-space arg semi-colon opt-space)* opt-space arg opt-space;
      arg := mexpr | sexpr;
      fn-name := mvar;
      mvar := #'[a-z][a-z0-9]*';
      mconst := #'[A-Z][A-Z0-9]*';
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
    "sexpr := quoted-expr | atom | number | subr | dotted-pair | list | sexpr comment;
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

    ;; we need a way of representing Clojure functions on the object list;
    ;; subr objects aren't expected to be normally entered on the REPL, but
    ;; must be on the object list or functions to which functions are passed
    ;; won't be able to access them.
    "subr := #'[a-z][a-z.]*/[A-Za-z][A-Za-z0-9]*';"

    ;; Lisp 1.5 supported octal as well as decimal and scientific notation
    "number := integer | decimal | scientific | octal;
      integer := #'-?[0-9]+';
      decimal := integer dot integer;
      scientific := coefficient e exponent;
      coefficient := decimal | integer;
      exponent := integer;
      e := 'E';
      octal := #'[+-]?[0-7]+{1,12}' q scale-factor;
      q := 'Q';
      scale-factor := #'[0-9]*'")))

