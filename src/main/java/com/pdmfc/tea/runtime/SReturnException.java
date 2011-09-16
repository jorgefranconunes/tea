/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.SFlowControlException;






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class SReturnException
    extends SFlowControlException {





    private Object _value;




/**************************************************************************
 *
 * @param value The value being returned by the function.
 *
 **************************************************************************/

    public SReturnException(final Object value) {

        _value = value;
    }






/**************************************************************************
 *
 * @return The value passed to the return statement.
 *
 **************************************************************************/

    public Object getReturnValue() {

        return _value;
    }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

