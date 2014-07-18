/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaNoSuchVarException;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaSymbol;
import com.pdmfc.tea.TeaTypeException;
import com.pdmfc.tea.Types;





/**************************************************************************
 *
 * Utility methods that centralize the conventions for obtaining the
 * function to execute from the first word in a statement.
 *
 **************************************************************************/

final class SWordSubstUtils
    extends Object {





/**************************************************************************
 *
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private SWordSubstUtils() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static TeaFunction toFunction(final Object   firstWord,
                                          final TeaContext context)
        throws TeaException {

        TeaFunction result = null;

        if ( firstWord instanceof TeaFunction ) {
            result = (TeaFunction)firstWord;
        } else {
            try {
                result = toFunction((TeaSymbol)firstWord, context);
            } catch ( ClassCastException e ) {
                String msg =
                    "argument 0 should be a function or a symbol, not a {0}";
                throw new TeaTypeException(msg, Types.getTypeName(firstWord));
            }
        }

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static TeaFunction toFunction(final TeaSymbol firstWord,
                                          final TeaContext  context)
        throws TeaException {

        Object value = null;

        try {
            value = context.getVar(firstWord);
        } catch ( TeaNoSuchVarException e2 ) {
            value = Types.getVarWithEffort(context, firstWord);
        }

        TeaFunction result = null;
        
        try {
            result = (TeaFunction)value;
        } catch ( ClassCastException e ) {
            String msg = "variable {0} should contain a function, not a {1}";
            throw new TeaTypeException(msg, firstWord, Types.getTypeName(value));
        }

        return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

