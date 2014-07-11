/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNoSuchVarException;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;





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

    public static SObjFunction toFunction(final Object   firstWord,
                                          final SContext context)
        throws TeaException {

        SObjFunction result = null;

        if ( firstWord instanceof SObjFunction ) {
            result = (SObjFunction)firstWord;
        } else {
            try {
                result = toFunction((SObjSymbol)firstWord, context);
            } catch (ClassCastException e) {
                String msg =
                    "argument 0 should be a function or a symbol, not a {0}";
                throw new STypeException(msg, STypes.getTypeName(firstWord));
            }
        }

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static SObjFunction toFunction(final SObjSymbol firstWord,
                                          final SContext   context)
        throws TeaException {

        Object value = null;

        try {
            value = context.getVar(firstWord);
        } catch (SNoSuchVarException e2) {
            value = STypes.getVarWithEffort(context, firstWord);
        }

        SObjFunction result = null;
        
        try {
            result = (SObjFunction)value;
        } catch (ClassCastException e) {
            String msg = "variable {0} should contain a function, not a {1}";
            throw new STypeException(msg, firstWord, STypes.getTypeName(value));
        }

        return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

