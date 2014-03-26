/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.STeaException;





/**************************************************************************
 *
 * Signals an abnormal condition while compiling Tea script.
 *
 **************************************************************************/

public final class TeaCompileException
    extends STeaException {





/**************************************************************************
 *
 * @param msg The message to use as error message.
 *
 **************************************************************************/

    public TeaCompileException(final String msg) {

        super(msg);
    }





/**************************************************************************
 *
 * @param msgFmt The <code>java.text.MessageFormat</code> like format
 * to use as error message.
 *
 * @param fmtArgs Formating arguments to use when building the actual
 * error message.
 *
 **************************************************************************/

    public TeaCompileException(final String    msgFmt,
                               final Object... fmtArgs) {

        super(msgFmt, fmtArgs);
    }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/
