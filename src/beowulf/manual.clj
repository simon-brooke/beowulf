(ns beowulf.manual
  "Experimental code for accessing the manual online."
  (:require [clojure.string :refer [ends-with? join trim]]))

(def ^:dynamic *manual-url*
  "https://www.softwarepreservation.org/projects/LISP/book/LISP%201.5%20Programmers%20Manual.pdf")

(def ^:constant index
  "This is data extracted from the index pages of `Lisp 1.5 Programmer's Manual`.
   It's here in the hope that we can automatically link to an online PDF link
   to the manual when the user invokes a function probably called `DOC` or `HELP`."
  {:RECIP
   {:fn-name "RECIP",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["26" "64"]},
   :QUOTE
   {:fn-name "QUOTE",
    :call-type "FSUBR",
    :implementation "",
    :page-nos ["10" "22" "71"]},
   :RECLAIM
   {:fn-name "RECLAIM",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION ",
    :page-nos ["67"]},
   :NUMOB
   {:fn-name "NUMOB",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION ",
    :page-nos ["86"]},
   :EVLIS
   {:fn-name "EVLIS",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["71"]},
   :DASH
   {:fn-name "DASH",
    :call-type "SUBR",
    :implementation "PREDICATE APVAL",
    :page-nos ["85" "87 "]},
   :EQUAL
   {:fn-name "EQUAL",
    :call-type "SUBR",
    :implementation "PREDICATE",
    :page-nos ["11" "26" "57"]},
   :PRIN1
   {:fn-name "PRIN1",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION ",
    :page-nos ["65" "84"]},
   :REMFLAG
   {:fn-name "REMFLAG",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION ",
    :page-nos ["41" "60"]},
   :DEFINE
   {:fn-name "DEFINE",
    :call-type "EXPR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["15" "18" "58"]},
   :PUNCHLAP
   {:fn-name "PUNCHLAP",
    :call-type "EXPR",
    :implementation "PSEUDO-FUNCTION LIBRARY",
    :page-nos ["68" "76"]},
   :STARTREAD
   {:fn-name "STARTREAD",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["87"]},
   :PERIOD
   {:fn-name "PERIOD",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["69" "85"]},
   :CP1
   {:fn-name "CP1",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["66"]},
   :NCONC
   {:fn-name "NCONC",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION ",
    :page-nos ["62"]},
   :EQ
   {:fn-name "EQ",
    :call-type "SUBR",
    :implementation "PREDICATE",
    :page-nos ["3" "23" "57"]},
   :RPLACD
   {:fn-name "RPLACD",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["41" "58"]},
   :PROG2
   {:fn-name "PROG2",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["42" "66"]},
   :UNCOUNT
   {:fn-name "UNCOUNT",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["34" "66"]},
   :ERROR1
   {:fn-name "ERROR1",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["88"]},
   :EXPT
   {:fn-name "EXPT",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["26" "64"]},
   :NOT
   {:fn-name "NOT",
    :call-type "SUBR",
    :implementation "PREDICATE",
    :page-nos ["21" "23" "58"]},
   :SLASH
   {:fn-name "SLASH",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["69" "85"]},
   :RPLACA
   {:fn-name "RPLACA",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["41" "58"]},
   :QUOTIENT
   {:fn-name "QUOTIENT",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["26" "64"]},
   :UNPACK
   {:fn-name "UNPACK",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["87"]},
   :CONC
   {:fn-name "CONC",
    :call-type "FEXPR",
    :implementation "",
    :page-nos ["61"]},
   :CAR
   {:fn-name "CAR",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["2" "56"]},
   :GENSYM
   {:fn-name "GENSYM",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["66"]},
   :PROP
   {:fn-name "PROP",
    :call-type "SUBR",
    :implementation "FUNCTIONAL ",
    :page-nos [" 59"]},
   :MEMBER
   {:fn-name "MEMBER",
    :call-type "SUBR",
    :implementation "PREDICATE ",
    :page-nos ["11" "62"]},
   :UNTRACESET
   {:fn-name "UNTRACESET",
    :call-type "EXPR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["68"]},
   :UNTRACE
   {:fn-name "UNTRACE",
    :call-type "EXPR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["32" "66"]},
   :MINUSP
   {:fn-name "MINUSP",
    :call-type "SUBR",
    :implementation "PREDICATE ",
    :page-nos ["26" "64"]},
   :F
   {:fn-name "F",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["22" "69"]},
   :SPECIAL
   {:fn-name "SPECIAL",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["64" "78"]},
   :LPAR
   {:fn-name "LPAR",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["69" "85"]},
   :GO
   {:fn-name "GO",
    :call-type "FSUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["30" "72"]},
   :MKNAM
   {:fn-name "MKNAM",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["86"]},
   :COMMON
   {:fn-name "COMMON",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["64" "78"]},
   :NUMBERP
   {:fn-name "NUMBERP",
    :call-type "SUBR",
    :implementation "PREDICATE ",
    :page-nos ["26" "64"]},
   :CONS
   {:fn-name "CONS",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["2" "56"]},
   :PLUS
   {:fn-name "PLUS",
    :call-type "FSUBR",
    :implementation "",
    :page-nos ["25" "63"]},
   :SET
   {:fn-name "SET",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["30" "71"]},
   :DOLLAR
   {:fn-name "DOLLAR",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["69" "85"]},
   :SASSOC
   {:fn-name "SASSOC",
    :call-type "SUBR",
    :implementation "FUNCTIONAL",
    :page-nos ["60"]},
   :SELECT
   {:fn-name "SELECT",
    :call-type "FEXPR",
    :implementation "",
    :page-nos ["66"]},
   :OPDEFINE
   {:fn-name "OPDEFINE",
    :call-type "EXPR",
    :implementation "PSEUDO-FUNCTION ",
    :page-nos ["65" "75"]},
   :PAUSE
   {:fn-name "PAUSE",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["67"]},
   :AND
   {:fn-name "AND",
    :call-type "FSUBR",
    :implementation "PREDICATE",
    :page-nos ["21" "58"]},
   :COMMA
   {:fn-name "COMMA",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["69" "85"]},
   :EFFACE
   {:fn-name "EFFACE",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["63"]},
   :CSETQ
   {:fn-name "CSETQ",
    :call-type "FEXPR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["59"]},
   :OPCHAR
   {:fn-name "OPCHAR",
    :call-type "SUBR",
    :implementation "PREDICATE ",
    :page-nos [" 87"]},
   :PRINTPROP
   {:fn-name "PRINTPROP",
    :call-type "EXPR",
    :implementation "PSEUDO-FUNCTION LIBRARY ",
    :page-nos ["68"]},
   :PLB
   {:fn-name "PLB",
    :call-type "SUBR",
    :implementation "PSEUDO- FUNCTION",
    :page-nos ["67"]},
   :DIGIT
   {:fn-name "DIGIT",
    :call-type "SUBR",
    :implementation "PREDICATE ",
    :page-nos ["87"]},
   :PUNCHDEF
   {:fn-name "PUNCHDEF",
    :call-type "EXPR",
    :implementation "PSEUDO-FUNCTION LIBRARY",
    :page-nos ["68"]},
   :ARRAY
   {:fn-name "ARRAY",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["27" "64"]},
   :MAX
   {:fn-name "MAX",
    :call-type "FSUBR",
    :implementation "",
    :page-nos ["26" "64"]},
   :INTERN
   {:fn-name "INTERN",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["67" "87"]},
   :NIL
   {:fn-name "NIL",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["22" "69"]},
   :TIMES
   {:fn-name "TIMES",
    :call-type "FSUBR",
    :implementation "",
    :page-nos ["26" "64"]},
   :ERROR
   {:fn-name "ERROR",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["32" "66"]},
   :PUNCH
   {:fn-name "PUNCH",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["65" "84"]},
   :REMPROP
   {:fn-name "REMPROP",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["41" "59"]},
   :DIVIDE
   {:fn-name "DIVIDE",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["26" "64"]},
   :OR
   {:fn-name "OR",
    :call-type "FSUBR",
    :implementation "PREDICATE ",
    :page-nos ["21" "58"]},
   :SUBLIS
   {:fn-name "SUBLIS",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["12" "61"]},
   :LAP
   {:fn-name "LAP",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION ",
    :page-nos ["65" "73"]},
   :PROG
   {:fn-name "PROG",
    :call-type "FSUBR",
    :implementation "",
    :page-nos ["29" "71"]},
   :T
   {:fn-name "T",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["22" "69"]},
   :GREATERP
   {:fn-name "GREATERP",
    :call-type "SUBR",
    :implementation "PREDICATE",
    :page-nos ["26" "64"]},
   :CSET
   {:fn-name "CSET",
    :call-type "EXPR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["17" "59"]},
   :FUNCTION
   {:fn-name "FUNCTION",
    :call-type "FSUBR",
    :implementation "",
    :page-nos ["21" "71"]},
   :LENGTH
   {:fn-name "LENGTH",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["62"]},
   :MINUS
   {:fn-name "MINUS",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["26" "63"]},
   :COND
   {:fn-name "COND",
    :call-type "FSUBR",
    :implementation "",
    :page-nos ["18"]},
   :APPEND
   {:fn-name "APPEND",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["11" "61"]},
   :CDR
   {:fn-name "CDR",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["3" "56"]},
   :OBLIST
   {:fn-name "OBLIST",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["69"]},
   :READ
   {:fn-name "READ",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION ",
    :page-nos ["5" "84"]},
   :ERRORSET
   {:fn-name "ERRORSET",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["35" "66"]},
   :UNCOMMON
   {:fn-name "UNCOMMON",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION ",
    :page-nos ["64" "78"]},
   :EVAL
   {:fn-name "EVAL",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["71"]},
   :MIN
   {:fn-name "MIN",
    :call-type "FSUBR",
    :implementation "",
    :page-nos ["26" "64"]},
   :PAIR
   {:fn-name "PAIR",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["60"]},
   :BLANK
   {:fn-name "BLANK",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["69" "85"]},
   :SETQ
   {:fn-name "SETQ",
    :call-type "FSUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["30" "71"]},
   :GET
   {:fn-name "GET",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["41" "59"]},
   :PRINT
   {:fn-name "PRINT",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION ",
    :page-nos ["65" "84"]},
   :ENDREAD
   {:fn-name "ENDREAD",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["8 8"]},
   :RETURN
   {:fn-name "RETURN",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["30" "72"]},
   :LITER
   {:fn-name "LITER",
    :call-type "SUBR",
    :implementation "PREDICATE ",
    :page-nos ["87"]},
   :EOF
   {:fn-name "EOF",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["69" "88"]},
   :TRACE
   {:fn-name "TRACE",
    :call-type "EXPR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["32" "66" "79"]},
   :TRACESET
   {:fn-name "TRACESET",
    :call-type "EXPR",
    :implementation "PSEUDO-FUNCTION LIBRARY",
    :page-nos ["68"]},
   :PACK
   {:fn-name "PACK",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION ",
    :page-nos ["86"]},
   :NULL
   {:fn-name "NULL",
    :call-type "SUBR",
    :implementation "PREDICATE ",
    :page-nos ["11" "57"]},
   :CLEARBUFF
   {:fn-name "CLEARBUFF",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["86"]},
   :LESSP
   {:fn-name "LESSP",
    :call-type "SUBR",
    :implementation "PREDICATE ",
    :page-nos ["26" "64"]},
   :TERPRI
   {:fn-name "TERPRI",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["65" "84"]},
   :ONEP
   {:fn-name "ONEP",
    :call-type "SUBR",
    :implementation "PREDICATE ",
    :page-nos [" 26" "64"]},
   :EXCISE
   {:fn-name "EXCISE",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["67" "77"]},
   :REMOB
   {:fn-name "REMOB",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION ",
    :page-nos ["67"]},
   :MAP
   {:fn-name "MAP",
    :call-type "SUBR",
    :implementation "FUNCTIONAL ",
    :page-nos ["63"]},
   :COMPILE
   {:fn-name "COMPILE",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["64" "76"]},
   :ADD1
   {:fn-name "ADD1",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["26" "64"]},
   :ADVANCE
   {:fn-name "ADVANCE",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["88"]},
   :SEARCH
   {:fn-name "SEARCH",
    :call-type "SUBR",
    :implementation "FUNCTIONAL",
    :page-nos ["63"]},
   :APPLY
   {:fn-name "APPLY",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["70"]},
   :READLAP
   {:fn-name "READLAP",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION ",
    :page-nos ["65" "76"]},
   :UNSPECIAL
   {:fn-name "UNSPECIAL",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["64" "78"]},
   :SUBST
   {:fn-name "SUBST",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["11" "61"]},
   :COPY
   {:fn-name "COPY",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["62"]},
   :LOGOR
   {:fn-name "LOGOR",
    :call-type "FSUBR",
    :implementation "",
    :page-nos ["26" "64"]},
   :LABEL
   {:fn-name "LABEL",
    :call-type "FSUBR",
    :implementation "",
    :page-nos ["8" "18" "70"]},
   :FIXP
   {:fn-name "FIXP",
    :call-type "SUBR",
    :implementation "PREDICATE",
    :page-nos ["26" "64"]},
   :SUB1
   {:fn-name "SUB1",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["26" "64"]},
   :ATTRIB
   {:fn-name "ATTRIB",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["59"]},
   :DIFFERENCE
   {:fn-name "DIFFERENCE",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["26" "64"]},
   :REMAINDER
   {:fn-name "REMAINDER",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["26" "64"]},
   :REVERSE
   {:fn-name "REVERSE",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["6 2"]},
   :EOR
   {:fn-name "EOR",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["69" "88"]},
   :PLUSS
   {:fn-name "PLUSS",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["69" "85"]},
   :TEMPUS-FUGIT
   {:fn-name "TEMPUS-FUGIT",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["67"]},
   :LOAD
   {:fn-name "LOAD",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["67"]},
   :CHARCOUNT
   {:fn-name "CHARCOUNT",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["69" "87"]},
   :RPAR
   {:fn-name "RPAR",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["69" "85"]},
   :COUNT
   {:fn-name "COUNT",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["34" "66"]},
   :SPEAK
   {:fn-name "SPEAK",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["34" "66 "]},
   :LOGXOR
   {:fn-name "LOGXOR",
    :call-type "FSUBR",
    :implementation "",
    :page-nos ["27" "64"]},
   :FLOATP
   {:fn-name "FLOATP",
    :call-type "SUBR",
    :implementation "PREDICATE",
    :page-nos ["26" "64"]},
   :ATOM
   {:fn-name "ATOM",
    :call-type "SUBR",
    :implementation "PREDICATE",
    :page-nos ["3" "57"]},
   :EQSIGN
   {:fn-name "EQSIGN",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["69" "85"]},
   :LIST
   {:fn-name "LIST",
    :call-type "FSUBR",
    :implementation "",
    :page-nos ["57"]},
   :MAPLIST
   {:fn-name "MAPLIST",
    :call-type "SUBR",
    :implementation "FUNCTIONAL ",
    :page-nos ["20" "21" "63"]},
   :LOGAND
   {:fn-name "LOGAND",
    :call-type "FSUBR",
    :implementation "",
    :page-nos ["27" "64"]},
   :FLAG
   {:fn-name "FLAG",
    :call-type "EXPR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["41" "60"]},
   :MAPCON
   {:fn-name "MAPCON",
    :call-type "SUBR",
    :implementation "FUNCTIONAL PSEUDO- FUNCTION",
    :page-nos ["63"]},
   :STAR
   {:fn-name "STAR",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["69" "85"]},
   :CURCHAR
   {:fn-name "CURCHAR",
    :call-type "APVAL",
    :implementation "",
    :page-nos ["69" "87"]},
   :DUMP
   {:fn-name "DUMP",
    :call-type "SUBR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["67"]},
   :DEFLIST
   {:fn-name "DEFLIST",
    :call-type "EXPR",
    :implementation "PSEUDO-FUNCTION",
    :page-nos ["41" "58"]},
   :LEFTSHIFT
   {:fn-name "LEFTSHIFT",
    :call-type "SUBR",
    :implementation "",
    :page-nos ["27" "64"]},
   :ZEROP
   {:fn-name "ZEROP",
    :call-type "SUBR",
    :implementation "PREDICATE",
    :page-nos ["26" "64"]}})

(defn page-url
  "Format the URL for the page in the manual with this `page-no`."
  [page-no]
  (let [n (read-string page-no)
        n' (when (and (number? n)
                      (ends-with? *manual-url* ".pdf"))
             ;; annoyingly, the manual has eight pages of front-matter
             ;; before numbering starts.
             (+ n 8))]
    (format
     (if (ends-with? *manual-url* ".pdf") "%s#page=%s" "%s#page%s")
     *manual-url*
     (or n' (trim (str page-no))))))

(defn format-page-references
  "Format page references from the manual index for the function whose name
   is `fn-symbol`."
  [fn-symbol]
  (let [k (if (keyword? fn-symbol) fn-symbol (keyword fn-symbol))]
    (join ", "
          (doall
           (map
            (fn [n]
              (let [p (trim n)]
                (format "<a href='%s'>%s</a>"
                        (page-url p) p)))
            (:page-nos (index k)))))))