/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.SFlowControlException;
import com.pdmfc.tea.runtime.TeaNull;






/**************************************************************************
 *
 * These exceptions are thrown to break out of a loop.
 *
 **************************************************************************/

public final class SBreakException
    extends SFlowControlException {





    // The value to return to the inner loop.
    private Object _object;




/**************************************************************************
 *
 * Initializes the objects internal state.
 *
 * @param obj The value to return by the command that was performing
 * the loop.
 *
 **************************************************************************/

    public SBreakException(final Object obj) {

        _object = obj;
    }




/**************************************************************************
 *
 * Initializes the objects internal state. A null Tea object will be
 * passed to the command performing the loop.
 *
 **************************************************************************/

    public SBreakException() {

        this(TeaNull.NULL);
    }






/**************************************************************************
 *
 * @return The value passed to the break statement.
 *
 **************************************************************************/

    public Object getBreakValue() {

        return _object;
    }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

