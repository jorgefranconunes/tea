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

public final class TeaPair
    extends Object
    implements Iterable {





    /** The left hand side object of the pair. */
    private Object _car = null;

    /** The right hand side object of the pair. */
    private TeaPair _cdr = null;





/**************************************************************************
 *
 * Builds a pair with nothing inside it.
 *
 **************************************************************************/

    public TeaPair() {

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

    public TeaPair(final Object   car,
                    final TeaPair cdr) {

        _car = car;
        _cdr = cdr;
    }





/**************************************************************************
 *
 * Creates a pair representing an empty list.
 *
 * @return A newly created <code>TeaPair</code> object representing
 * the empty list.
 *
 **************************************************************************/

    public static TeaPair emptyList() {

        return new TeaPair();
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

    public TeaPair setCar(final Object car ) {

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

    public TeaPair cdr()
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

    public TeaPair setCdr(final TeaPair cdr) {

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
        TeaPair node     = this;

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
 * <code>TeaPair</code> object.
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


        private TeaPair _node = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public SPairIterator(final TeaPair node) {

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
                throw new NoSuchElementException("TeaPairIterator");
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

