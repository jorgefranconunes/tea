/**************************************************************************
 *
 * Copyright (c) 2001-2008 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
 *
 *
 * Revisions:
 *
 * 2002/02/19 Readded the "elements()" method for compatibility with
 * Java code written for Tea 3.0.0. The method is deprecated. (jfn)
 *
 * 2002/01/10 The elements() method was replaced by iterator(). (jfn)
 *
 * 2001/05/12 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;





/**************************************************************************
 *
 * Implements an ordered list of objects. The list can be traversed
 * from its beggining to its end.
 *
 * @deprecated It is better to use one of the Java classes
 * implementing the <code>java.util.List</code> interface. If speed is
 * an issue then create a list implementation private to your package.
 *
 **************************************************************************/

public class SList
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
 * @param obj
 *    Reference to the object to be appended to the end of the list.
 *
 **************************************************************************/

   public void append(Object obj) {

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
 * @param obj
 *    Reference to the object to be inserted at the start of the list.
 *
 **************************************************************************/

   public void prepend(Object obj) {

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
 * @return
 *    The number of elements on the list.
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
 * Builds an <TT>Enumeration</TT> to iterate over the list elements. The
 * <TT>Enumeration</TT> will retrieve the elements by their order in the
 * list.
 *
 * @return An <TT>Enumeration</TT> object that may be used to iterate
 * over the list elements.
 *
 * @deprecated Use the <code>{@link #iterator()}</code> method
 * instead.
 *
 **************************************************************************/

   public Enumeration elements() {

      return new SListEnumeration(_head);
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

    class SListIterator
	extends Object
	implements Iterator {





	private SListNode _current;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

	public SListIterator(SListNode head) {

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





/**************************************************************************
 *
 * Implements the <TT>Enumeration</TT> interface for a <TT>SList</TT>
 * object.
 *
 **************************************************************************/

    class SListEnumeration
	extends Object
	implements Enumeration {





	private SListNode _current;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

	public SListEnumeration(SListNode head) {

	    _current = head;
	}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

	public boolean hasMoreElements() {

	    return _current!=null;
	}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

	public Object nextElement() {

	    if ( _current == null ) {
		throw new NoSuchElementException("SListEnumeration");
	    }

	    SListNode node = _current;

	    _current = _current._next;

	    return node._element;
	}


    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

