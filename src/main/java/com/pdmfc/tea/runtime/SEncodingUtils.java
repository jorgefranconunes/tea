/**************************************************************************
 *
 * Copyright (c) 2010-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.nio.charset.Charset;

import com.pdmfc.tea.SConfigInfo;
import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjSymbol;





/**************************************************************************
 *
 * Utility functions for managing the encoding used for Tea source
 * files.
 *
 **************************************************************************/

public final class SEncodingUtils
    extends Object {





    // The name of the Tea global variable that will contain the
    // encoding used for source files.
    private static final String VAR_SOURCE_ENCODING =
        SConfigInfo.getProperty("com.pdmfc.tea.sourceEncodingVarName");

    private static final SObjSymbol SYMBOL_SOURCE_ENCODING =
        SObjSymbol.addSymbol(VAR_SOURCE_ENCODING);





/**************************************************************************
 *
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private SEncodingUtils() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * Specifies the encoding to be used for Tea source files.
 *
 * <p>The encoding is stored as a string in the Tea variable
 * <code>TEA_ENCODING_SOURCE</code> created in the given context.</p>
 *
 * @param context
 *
 * @param requestedEncoding The encoding to be assumed for the Tea
 * source files.
 *
 **************************************************************************/

    public static void setSourceEncoding(final SContext context,
                                         final String   requestedEncoding) {

        String encoding = requestedEncoding;

        if ( encoding == null ) {
            encoding = Charset.defaultCharset().name();
        }

        context.newVar(SYMBOL_SOURCE_ENCODING, encoding);
    }





/**************************************************************************
 *
 * Retrieves the encoding to be used for source files.
 *
 * <p>The encoding is retrieved from the Tea variable
 * <code>TEA_ENCODING_SOURCE</code> looked up in the given context. If
 * this Tea variable is not defined or if it does not contain a
 * <code>java.nio.charset.Charset</code> instance then the default
 * platform encoding is returned.</p>
 *
 **************************************************************************/

    public static String getSourceEncoding(final SContext context)
        throws STeaException {

        String encoding = null;
        Object obj      = context.getVar(SYMBOL_SOURCE_ENCODING);

        try {
            encoding = (String)obj;
        } catch (ClassCastException e) {
            String msg = "Var {0} should contain a string, not a {1}";
            throw new STeaException(msg,
                                    SYMBOL_SOURCE_ENCODING,
                                    STypes.getTypeName(obj));
        }

        return encoding;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

