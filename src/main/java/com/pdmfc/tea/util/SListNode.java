/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
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
 **************************************************************************/

public final class SListNode
    extends Object {





    /**
     * The object this node stores.
     */
    public Object    _element;

    /**
     * A reference to the next node on the list this node belongs to.
     */
    public SListNode _next;





/**************************************************************************
 *
 * Initializes the object internal state.
 *
 * @param obj
 *    The object this node will store.
 *
 **************************************************************************/

    public SListNode(final Object obj) {

        _element = obj;
        _next    = null;
    }





/**************************************************************************
 *
 * Initializes the object internal state.
 *
 * @param obj The object this node will store.
 *
 * @param next The next node this node will point to.
 *
 **************************************************************************/

    public SListNode(final Object obj,
                     final SListNode next) {

        _element = obj;
        _next    = next;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

