/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.SRuntimeException;






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class SEmptyListException
    extends SRuntimeException {




/**************************************************************************
 *
 * For internal use only.
 *
 **************************************************************************/

    private SEmptyListException() {

        // Nothing to do.
    }




/**************************************************************************
 *
 * Raises an exception to signal that the argument with the given
 * index is an empty list.
 *
 **************************************************************************/

    public SEmptyListException(final Object[] args,
                               final int      argIndex) {

        String msgFmt = "list in argument {0} must not be empty";
       
        initForFunction(args, msgFmt, argIndex);
    }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/



