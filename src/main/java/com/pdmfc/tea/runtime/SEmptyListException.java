/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public class SEmptyListException
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



