/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.pdmfc.tea.runtime.SEmptyListException;





/**************************************************************************
 *
 * Implements a pair. Tea lists are built with this kind of objects.
 *
 **************************************************************************/

public final class SObjPair
    extends Object
    implements Iterable {





    /** The left hand side object of the pair. */
    private Object _car = null;

    /** The right hand side object of the pair. */
    private SObjPair _cdr = null;





/**************************************************************************
 *
 * Builds a pair with nothing inside it.
 *
 **************************************************************************/

    public SObjPair() {

        this(null, null);
    }





/**************************************************************************
 *
 * Builds a pair containing two objects.
 *
 * @param car The left hand side object.
 *
 * @param cdr The right hand side object.
 *
 **************************************************************************/

    public SObjPair(final Object   car,
                    final SObjPair cdr) {

        _car = car;
        _cdr = cdr;
    }





/**************************************************************************
 *
 * Creates a pair representing an empty list.
 *
 * @return A newly created <code>SObjPair</code> object representing
 * the empty list.
 *
 **************************************************************************/

    public static SObjPair emptyList() {

        return new SObjPair();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public boolean isEmpty() {

        boolean result = (_car==null);

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object car()
        throws SEmptyListException {

        if ( _car == null ) {
            throw new SEmptyListException();
        }

        return _car;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SObjPair setCar(final Object car ) {

        _car = car;

        return this;
    }





/**************************************************************************
 *
 * Fetches the pair object to the left of this one.
 *
 * @return The pair object following this one.
 *
 * @exception SEmptyListException Thrown if this pair has no following
 * one.
 *
 **************************************************************************/

    public SObjPair cdr()
        throws SEmptyListException {

        if ( _cdr == null ) {
            throw new SEmptyListException();
        }

        return _cdr;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SObjPair setCdr(final SObjPair cdr) {

        _cdr = cdr;

        return this;
    }





/**************************************************************************
 *
 * Determines the size of the list starting with this node.
 *
 * @return The number of nodes in the list, starting with this node.
 *
 **************************************************************************/

    public int length() {

        int      numNodes = 0;
        SObjPair node     = this;

        while ( node._car != null ) {
            numNodes++;
            node = node._cdr;
        }
        
        return numNodes;
    }





/**************************************************************************
 *
 * Builds an <code>java.util.Iterator</code> that will orderly
 * iterator over the elements in the list whose head is this
 * <code>SObjPair</code> object.
 *
 * @return A newly created <code>java.util.Iterator</code> object that
 * will iterate over the list elements.
 *
 **************************************************************************/

    public Iterator iterator() {

        return new SPairIterator(this);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static final class SPairIterator
        extends Object
        implements Iterator {


        private SObjPair _node = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public SPairIterator(final SObjPair node) {

            _node = node;
            if ( (_node!=null) && (_node._car==null) ) {
                _node = null;
            }
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public boolean hasNext() {

            return (_node!=null) && (_node._car!=null);
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public Object next() {

            if ( (_node==null) || (_node._car==null) ) {
                throw new NoSuchElementException("SObjPairIterator");
            }
            Object element = _node._car;

            _node = _node._cdr;

            return element;
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public void remove() {

            throw new UnsupportedOperationException();
        }


    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

