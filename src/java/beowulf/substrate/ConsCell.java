package beowulf.substrate;

import clojure.lang.*;

import java.lang.Number;
//import beowulf.cons_cell.NIL;

/**
 * <p>
 * A cons cell - a tuple of two pointers - is the fundamental unit of Lisp store.
 * </p>
 * <p>
 * Implementing mutable data in Clojure if <em>hard</em> - deliberately so.
 * But Lisp 1.5 cons cells need to be mutable. This class is part of thrashing
 * around trying to find a solution.
 * </p>
 */
public class ConsCell
        implements clojure.lang.IPersistentCollection,
        clojure.lang.ISeq,
        clojure.lang.Seqable,
        clojure.lang.Sequential {

    /**
     * The car of a cons cell can't be just any object; it needs to be
     * a number, a symbol or a cons cell. But as there is no common superclass
     * or interface for those things, we use Object here and specify the
     * types of objects which can be stored in the constructors and setter
     * methods.
     */
    private Object car;

    /**
     * The car of a cons cell can't be just any object; it needs to be
     * a number, a symbol or a cons cell. But as there is no common superclass
     * or interface for those things, we use Object here and specify the
     * types of objects which can be stored in the constructors and setter
     * methods.
     */
    private Object cdr;

    /**
     * Construct a new ConsCell object with this `car` and this `cdr`.
     *
     * @param car
     * @param cdr
     * @throws IllegalArgumentException if either `car` or `cdr` is not one
     *                                  of ConsCell, Symbol, Number
     */
    public ConsCell(Object car, Object cdr) {
        if (car instanceof ConsCell || car instanceof Number || car instanceof Symbol) {
            this.car = car;
        } else {
            StringBuilder bob = new StringBuilder("Invalid CAR value (`")
                    .append(car.toString()).append("`; ")
                    .append(car.getClass().getName()).append(") passed to CONS");
            throw new IllegalArgumentException(bob.toString());
        }
        if (cdr instanceof ConsCell || cdr instanceof Number || cdr instanceof Symbol) {
            this.cdr = cdr;
        } else {
            StringBuilder bob = new StringBuilder("Invalid CDR value (`")
                    .append(cdr.toString()).append("`; ")
                    .append(cdr.getClass().getName()).append(") passed to CONS");
            throw new IllegalArgumentException(bob.toString());
        }
    }

    public Object getCar() {
        return this.car;
    }

    public Object getCdr() {
        return this.cdr;
    }

    public ConsCell setCar(ConsCell c) {
        this.car = c;
        return this;
    }

    public ConsCell setCdr(ConsCell c) {
        this.cdr = c;
        return this;
    }

    public ConsCell setCar(java.lang.Number n) {
        this.car = n;
        return this;
    }

    public ConsCell setCdr(java.lang.Number n) {
        this.cdr = n;
        return this;
    }

    public ConsCell setCar(clojure.lang.Symbol s) {
        this.car = s;
        return this;
    }

    public ConsCell setCdr(clojure.lang.Symbol s) {
        this.cdr = s;
        return this;
    }

    @Override
    public boolean equals(Object other) {
        boolean result;

        if (other instanceof IPersistentCollection) {
            ISeq s = ((IPersistentCollection) other).seq();

            result = this.car.equals(s.first()) &&
                    this.cdr instanceof ConsCell &&
                    ((ISeq) this.cdr).equiv(s.more());
        } else {
            result = false;
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder bob = new StringBuilder("(");

        for (Object d = this; d instanceof ConsCell; d = ((ConsCell) d).cdr) {
            ConsCell cell = (ConsCell) d;
            bob.append(cell.car.toString());

            if (cell.cdr instanceof ConsCell) {
                bob.append(" ");
            } else if (cell.cdr.toString().equals("NIL")) {
                /* That's an ugly hack to work around the fact I can't currently
                 * get a handle on the NIL symbol itself. In theory, nothing else
                 * in Lisp 1.5 should have the print-name `NIL`.*/
                bob.append(")");
            } else {
                bob.append(" . ").append(cell.cdr.toString()).append(")");
            }
        }

        return bob.toString();
    }

    /* IPersistentCollection interface implementation */

    @Override
    public int count() {
      int result = 1;
      ConsCell cell = this;

      while (cell.cdr instanceof ConsCell) {
        result ++;
        cell = (ConsCell)cell.cdr;
      }

      return result;
    }

    @Override
    /**
     * `empty` is completely undocumented, I'll return `null` until something breaks.
     */
    public IPersistentCollection empty() {
        return null;
    }

    /**
     * God alone knows what `equiv` is intended to do; it's completely
     * undocumented. But in PersistentList it's simply a synonym for 'equals',
     * and that's what I'll implement.
     */
    @Override
    public boolean equiv(Object o) {
        return this.equals(o);
    }

    /* ISeq interface implementation */
    @Override
    public Object first() {
        return this.car;
    }

    @Override
    public ISeq next() {
        ISeq result;

        if (this.cdr instanceof ConsCell) {
            result = (ISeq) this.cdr;
        } else {
            result = null;
        }

        return result;
    }

    @Override
    public ISeq more() {
        ISeq result;

        if (this.cdr instanceof ConsCell) {
            result = (ISeq) this.cdr;
        } else {
            result = null;
        }

        return result;
    }

    /**
     * Return a new cons cell comprising the object `o` as car,
     * and myself as cdr. Hopefully by declaring the return value
     * `ConsCell` I'll satisfy both the IPersistentCollection and the
     * ISeq interfaces.
     */
    @Override
    public ConsCell cons(Object o) {
        if (o instanceof ConsCell) {
            return new ConsCell((ConsCell) o, this);
        } else if (o instanceof Number) {
            return new ConsCell((Number) o, this);
        } else if (o instanceof Symbol) {
            return new ConsCell((Symbol) o, this);
        } else {
            throw new IllegalArgumentException("Unrepresentable argument passed to CONS");
        }
    }

    /* Seqable interface */
    @Override
    public ISeq seq() {
        return this;
    }

    /* Sequential interface is just a marker and does not require us to
     * implement anything */


}
