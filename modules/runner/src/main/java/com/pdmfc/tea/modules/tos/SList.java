/**************************************************************************
 *
 * Copyright (c) 2001-2013 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import java.util.Iterator;
import java.util.NoSuchElementException;





/**************************************************************************
 *
 * Implements an ordered list of objects. The list can be traversed
 * from its beggining to its end.
 *
 * @param <T> The type of the objects this list will contain.
 *
 **************************************************************************/

public final class SList<T>
    extends Object
    implements Iterable<T> {




    
    private ListNode<T> _head = null;
    private ListNode<T> _tail = null;
    private int         _size = 0;





/**************************************************************************
 *
 * Initializes an empty list.
 *
 **************************************************************************/

    public SList() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * Appends the object given as argument to the end of the list.
 *
 * @param obj Reference to the object to be appended to the end of the
 * list.
 *
 **************************************************************************/

    public void append(final T obj) {

        ListNode<T> node = new ListNode<T>(obj);

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
 * the list.
 *
 **************************************************************************/

    public void prepend(final T obj) {

        ListNode<T> node = new ListNode<T>(obj);

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

    @Override
    public Iterator<T> iterator() {

        Iterator<T> result = new ListIterator<T>(_head);

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static final class ListNode<T>
        extends Object {





        public T        _element = null;
        public ListNode _next    = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public ListNode(final T element) {

            _element = element;
            _next    = null;
        }


    }





/**************************************************************************
 *
 * Implements the <code>Iterator</code> interface for a
 * <code>SList</code> object.
 *
 **************************************************************************/

    private static final class ListIterator<T>
        extends Object
        implements Iterator<T> {





        private ListNode<T> _current;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public ListIterator(final ListNode<T> head) {

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

        public T next() {

            if ( _current == null ) {
                throw new NoSuchElementException("ListIterator");
            }

            ListNode<T> node = _current;

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

