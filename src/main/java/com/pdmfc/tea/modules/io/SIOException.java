/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.io;

import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class SIOException
    extends SRuntimeException {






/**************************************************************************
 *
 * For internal use only.
 *
 **************************************************************************/

    private SIOException() {

        // Nothing to do.
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SIOException(final String    msgFmt,
                        final Object... fmtArgs) {

        init(msgFmt, fmtArgs);
    }




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SIOException(final Throwable error) {

        String msg = error.getMessage();

        init(msg);
    }





/**************************************************************************
 *
 * Initializes the message from the zeroth argument of a command and
 * from an error message.
 *
 * @param args The arguments the function was called with.
 *
 * @param msgFmt A string with an error message.
 *
 **************************************************************************/
 
    public SIOException(final Object[]  args,
                        final String    msgFmt,
                        final Object... fmtArgs) {

        initForFunction(args, msgFmt, fmtArgs);
    }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/



