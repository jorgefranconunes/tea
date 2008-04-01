/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SListNode.java,v 1.3 2004/10/01 15:32:10 jfn Exp $
 *
 *
 * Revisions:
 *
 * 1998/09/13 Added the constructor SListNode(Object obj, SListNode
 * next).
 *
 * 1998/07/19 Creation. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.util;





/**************************************************************************
 *
 * Objects of the classe are used as the nodes in the list represented
 * by <TT>SList</TT> objects. It is a public class because classes derived
 * from <TT>SList</TT> may want to access the node directly for performance
 * reasons.
 *
 * @deprecated It is better to use one of the Java classes
 * implementing the <code>java.util.List</code> interface. If speed is
 * an issue then create a list implementation private to your package.
 *
 **************************************************************************/

public class SListNode extends Object {

    /** The object this node stores. */
    public Object    _element;

    /** A reference to the next node on the list this node belongs to. */
    public SListNode _next;





/**************************************************************************
 *
 * Initializes the object internal state.
 *
 * @param obj
 *    The object this node will store.
 *
 **************************************************************************/

   public SListNode(Object obj) {

      _element = obj;
      _next    = null;
   }





/**************************************************************************
 *
 * Initializes the object internal state.
 *
 * @param obj
 *    The object this node will store.
 *
 * @param next
 *    The next node this node will point to.
 *
 **************************************************************************/

   public SListNode(Object obj, SListNode next) {

      _element = obj;
      _next    = next;
   }

}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

