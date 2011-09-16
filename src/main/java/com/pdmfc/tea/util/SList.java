/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.pdmfc.tea.util.SListNode;





/**************************************************************************
 *
 * Implements an ordered list of objects. The list can be traversed
 * from its beggining to its end.
 *
 **************************************************************************/

public final class SList
    extends Object {




    
    protected SListNode _head;
    protected SListNode _tail;
    protected int       _size;





/**************************************************************************
 *
 * This constructor builds an empty list.
 *
 **************************************************************************/

    public SList() {

        _head = null;
        _tail = null;
        _size = 0;
    }





/**************************************************************************
 *
 * Appends the object given as argument to the end of the list.
 *
 * @param obj Reference to the object to be appended to the end of the
 *    list.
 *
 **************************************************************************/

    public void append(final Object obj) {

        SListNode node = new SListNode(obj);

        if ( _head == null ) {
            _head = node;
            _tail = node;
        } else {
            _tail._next = node;
            _tail       = node;
        }
        _size++;
    }





/**************************************************************************
 *
 * Inserts the object given as argument to the start of the list.
 *
 * @param obj Reference to the object to be inserted at the start of
 *    the list.
 *
 **************************************************************************/

    public void prepend(final Object obj) {

        SListNode node = new SListNode(obj);

        if ( _head == null ) {
            _head = node;
            _tail = node;
        } else {
            node._next = _head;
            _head      = node;
        }
        _size++;
    }





/**************************************************************************
 *
 * Emptys the list.
 *
 **************************************************************************/

    public void empty() {

        _head = null;
        _tail = null;
        _size = 0;
    }





/**************************************************************************
 *
 * Returns the number of elements on the list.
 *
 * @return The number of elements on the list.
 *
 **************************************************************************/

    public int size() {

        return _size;
    }





/**************************************************************************
 *
 * 
 *
 * @return An <TT>Enumeration</TT> object that may be used to iterate
 * over the list elements.
 *
 **************************************************************************/

    public Iterator iterator() {

        return new SListIterator(_head);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SListNode head() {

        return _head;
    }





/**************************************************************************
 *
 * Implements the <TT>Enumeration</TT> interface for a <TT>SList</TT>
 * object.
 *
 **************************************************************************/

    private static final class SListIterator
	extends Object
	implements Iterator {





	private SListNode _current;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

	public SListIterator(final SListNode head) {

	    _current = head;
	}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

	public boolean hasNext() {

	    return _current!=null;
	}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

	public Object next() {

	    if ( _current == null ) {
		throw new NoSuchElementException("SListIterator");
	    }

	    SListNode node = _current;

	    _current = _current._next;

	    return node._element;
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

