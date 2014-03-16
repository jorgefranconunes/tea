/**************************************************************************
 *
 * Copyright (c) 2001-2013 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * Implements a pair. Tea lists are built with this kind of objects.
 *
 **************************************************************************/

public final class SObjPair
    extends Object
    implements Iterable {





    /** The left hand side object of the pair. */
    public Object _car = null;

    /** The right hand side object of the pair. */
    public Object _cdr = null;





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

    public SObjPair(final Object car,
                    final Object cdr) {

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
 * Fetches the pair object to the left of this one.
 *
 * @return The pair object following this one.
 *
 * @exception SRuntimeException Thrown if this pair has no following
 * one, or if the object on the left of this pair is not itself a
 * pair.
 *
 **************************************************************************/

    public SObjPair nextPair()
        throws SRuntimeException {

        if ( _cdr == null ) {
            String msg = "No such element";
            throw new SRuntimeException(msg);
        }

        if ( !(_cdr instanceof SObjPair) ) {
            String msg = "Improperly formed list";
            throw new SRuntimeException(msg);
        }

        SObjPair result = (SObjPair)_cdr;

        return result;
    }





/**************************************************************************
 *
 * Determines the size of the list starting with this node.
 *
 * @return The number of nodes in the list, starting with this node.
 *
 * @exception SRuntimeException Thrown if the list starting with this
 * node is not properly formed.
 *
 **************************************************************************/

    public int length()
        throws SRuntimeException {

        int      numNodes = 0;
        SObjPair node     = this;

        while ( node._car != null ) {
            numNodes++;
            try {
                node = (SObjPair)node._cdr;
            } catch (ClassCastException e) {
                throw new SRuntimeException("Improperly formed list");
            }
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

            try {
                _node = (SObjPair)_node._cdr;
            } catch (ClassCastException e) {
                throw new NoSuchElementException("Improperly formed list");
            }

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

