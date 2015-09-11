/**************************************************************************
 *
 * Copyright (c) 2015 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import com.pdmfc.tea.TeaPair;





/**************************************************************************
 *
 * Utility class used for simplifying the creation of Tea lists.
 *
 **************************************************************************/

public final class TeaPairListBuilder
    extends Object {





    private TeaPair _head = TeaPair.emptyList();
    private TeaPair _tail = _head;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaPairListBuilder() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaPairListBuilder append(final Object item) {

        TeaPair newTail = TeaPair.emptyList();

        _tail.setCar(item);
        _tail.setCdr(newTail);
        _tail = newTail;

        return this;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaPairListBuilder prepend(final Object item) {

        TeaPair newHead = new TeaPair(item, _head);

        _head = newHead;

        return this;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaPair build() {

        return _head;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

