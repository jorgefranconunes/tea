/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import com.pdmfc.tea.TeaRunException;






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class TeaEmptyListException
    extends TeaRunException {




/**************************************************************************
 *
 * For internal use only.
 *
 **************************************************************************/

    public TeaEmptyListException() {

        // Nothing to do.
    }




/**************************************************************************
 *
 * Raises an exception to signal that the argument with the given
 * index is an empty list.
 *
 **************************************************************************/

    public TeaEmptyListException(final Object[] args,
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



