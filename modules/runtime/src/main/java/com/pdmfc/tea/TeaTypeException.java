/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import com.pdmfc.tea.TeaRunException;
import com.pdmfc.tea.Types;






/**************************************************************************
 *
 * Signals an abnormal condition related with mismatched types.
 *
 **************************************************************************/

public final class TeaTypeException
    extends TeaRunException {






/**************************************************************************
 *
 * For internal use only.
 *
 **************************************************************************/

    private TeaTypeException() {

        // Nothing to do.
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaTypeException(final String    msgFmt,
                            final Object... fmtArgs) {

        init(msgFmt, fmtArgs);
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaTypeException(final Object[]  args,
                            final String    msgFmt,
                            final Object... fmtArgs) {

        initForFunction(args, msgFmt, fmtArgs);
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaTypeException(final Object[] args,
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

