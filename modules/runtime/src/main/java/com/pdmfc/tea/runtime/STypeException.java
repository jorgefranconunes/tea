/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.TeaRunException;






/**************************************************************************
 *
 * Signals an abnormal condition related with mismatched types.
 *
 **************************************************************************/

public final class STypeException
    extends TeaRunException {






/**************************************************************************
 *
 * For internal use only.
 *
 **************************************************************************/

    private STypeException() {

        // Nothing to do.
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public STypeException(final String    msgFmt,
                          final Object... fmtArgs) {

        init(msgFmt, fmtArgs);
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public STypeException(final Object[]  args,
                          final String    msgFmt,
                          final Object... fmtArgs) {

        initForFunction(args, msgFmt, fmtArgs);
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public STypeException(final Object[] args,
                          final int      argIndex,
                          final String   expectedTypeDescription) {

        String msgFmt = "argument {0} should be a {1}, not a {2}";
        String actualTypeDescription = Types.getTypeName(args[argIndex]);

        initForFunction(args,
                        msgFmt,
                        String.valueOf(argIndex),
                        expectedTypeDescription,
                        actualTypeDescription);
    }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

