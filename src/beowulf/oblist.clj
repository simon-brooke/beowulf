(ns beowulf.oblist
  "A namespace mainly devoted to the object list and other top level
   global variables.
   
   Yes, this makes little sense, but if you put them anywhere else you end
   up in cyclic dependency hell."
  )

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

(def NIL
  "The canonical empty list symbol.
   
   TODO: this doesn't really work, because (from Clojure) `(empty? NIL)` throws
   an exception. It might be better to subclass beowulf.cons_cell.ConsCell to create
   a new singleton class Nil which overrides the `empty` method of 
   IPersistentCollection?"
  'NIL)

(def oblist
  "The default environment."
  (atom NIL))

(def ^:dynamic *options*
  "Command line options from invocation."
  {:testing true})

