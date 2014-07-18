/**************************************************************************
 *
 * Copyright (c) 2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import java.text.MessageFormat;





/**************************************************************************
 *
 * A runtime exception denoting an unrecoverable error in the Tea
 * runtime.
 *
 **************************************************************************/

public final class TeaError
    extends RuntimeException {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private TeaError(final String msg) {

        super(msg);
    }





/**************************************************************************
 *
 * Throws a newly created <code>TeaError</code> exception.
 *
 **************************************************************************/

    public static void raise(final String    msgFmt,
                             final Object... fmtArgs) {

        String msg = null;

        if ( (fmtArgs==null) || (fmtArgs.length==0) ) {
            msg = msgFmt;
        } else {
            msg = MessageFormat.format(msgFmt, fmtArgs);
        }

        throw new TeaError(msg);
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

