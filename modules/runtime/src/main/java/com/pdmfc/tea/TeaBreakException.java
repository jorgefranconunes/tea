/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import com.pdmfc.tea.TeaFlowControlException;
import com.pdmfc.tea.TeaNull;






/**************************************************************************
 *
 * These exceptions are thrown to break out of a loop.
 *
 **************************************************************************/

public final class TeaBreakException
    extends TeaFlowControlException {





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

    public TeaBreakException(final Object obj) {

        _object = obj;
    }




/**************************************************************************
 *
 * Initializes the objects internal state. A null Tea object will be
 * passed to the command performing the loop.
 *
 **************************************************************************/

    public TeaBreakException() {

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

