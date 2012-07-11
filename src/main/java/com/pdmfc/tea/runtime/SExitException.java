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

public final class SExitException
    extends SFlowControlException {





    private Integer _value;




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SExitException(Integer value) {

        _value = value;
    }






/**************************************************************************
 *
 * @return The integer value passed to the exit statement.
 *
 **************************************************************************/

    public Integer getExitValue() {

        return _value;
    }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

