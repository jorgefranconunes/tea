/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import com.pdmfc.tea.TeaBlock;
import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaNull;
import com.pdmfc.tea.TeaPair;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.TeaNoSuchVarException;





/**************************************************************************
 *
 * Provides utility methods for handling Java objects corresponding to
 * Tea basic types.
 *
 **************************************************************************/

public final class Types
    extends Object {





    // The name of variable with the missing function callback action.
    private static final TeaSymbol CALLBACK_NAME =
        TeaSymbol.addSymbol("TEA_NOFUNC_CALLBACK");

    private static final String TYPE_BLOCK    = "Block";
    private static final String TYPE_BOOL     = "Boolean";
    private static final String TYPE_FLOAT    = "Float";
    private static final String TYPE_FUNC     = "Function";
    private static final String TYPE_INT      = "Integer";
    private static final String TYPE_NULL     = "Null";
    private static final String TYPE_PAIR     = "Pair";
    private static final String TYPE_STRING   = "String";
    private static final String TYPE_SYMBOL   = "Symbol";
    private static final String TYPE_TOSOBJ   = "TOS Object";
    private static final String TYPE_TOSCLASS = "TOS Class";





/**************************************************************************
 *
 * No instances of this class can be created. It only provides static
 * methods.
 *
 **************************************************************************/

    private Types() {

        // Nothing to do.
    }




/**************************************************************************
 *
 * Retrives a description for the type of the object given as
 * argument. This description should not be used as an identifier for
 * the object type. It is only meant to be used in text messages.
 *
 * @param obj The object for wich a type description is sought.
 *
 * @return A string with a description for the type of the object
 * received as argument.
 *
 **************************************************************************/

    public static String getTypeName(final Object obj) {

        String typeName = getTypeName(obj.getClass());

        if ( obj instanceof STosObj ) {
            return typeName + " (" +((STosObj)obj).getTosClass().getName()+")";
        }
        if ( obj instanceof STosClass ) {
            return typeName + " (" + ((STosClass)obj).getName() + ")";
        }

        return typeName;
    }





/**************************************************************************
 *
 * Retrives a description for the type given as argument. This
 * description should not be used as an identifier for the type. It is
 * only meant to be used in text messages.
 *
 * @param aClass The type for wich a description is sought.
 *
 * @return A string with a description for the type received as
 * argument.
 *
 **************************************************************************/

    public static String getTypeName(final Class aClass) {

        if ( TeaBlock.class.isAssignableFrom(aClass) ) {
            return TYPE_BLOCK;
        }
        if ( Boolean.class.isAssignableFrom(aClass) ) {
            return TYPE_BOOL;
        }
        if ( Double.class.isAssignableFrom(aClass) ) {
            return TYPE_FLOAT;
        }
        if ( Integer.class.isAssignableFrom(aClass) ) {
            return TYPE_INT;
        }
        if ( STosObj.class.isAssignableFrom(aClass) ) {
            return TYPE_TOSOBJ;
        }
        if ( TeaFunction.class.isAssignableFrom(aClass) ) {
            return TYPE_FUNC;
        }
        if ( TeaPair.class.isAssignableFrom(aClass) ) {
            return TYPE_PAIR;
        }
        if ( String.class.isAssignableFrom(aClass) ) {
            return TYPE_STRING;
        }
        if ( TeaSymbol.class.isAssignableFrom(aClass) ) {
            return TYPE_SYMBOL;
        }
        if ( TeaNull.class.isAssignableFrom(aClass) ) {
            return TYPE_NULL;
        }
        if ( STosClass.class.isAssignableFrom(aClass) ) {
            return TYPE_TOSCLASS;
        }
        return aClass.getName();
    }





/**************************************************************************
 *
 * Invokes a Tea function stored in the "TEA_NOFUNC_CALLBACK" and then
 * tries to retrieve the variable referenced by the symbol
 * <TT>name</TT>.
 *
 * @param context The context where the variable will be searched for.
 *
 * @param name The symbol referencing the variable we are looking for.
 *
 * @return The object stored in the variable.
 *
 * @exception TeaException Throw if the variable does not exit before
 * and after the Tea function is run.
 *
 **************************************************************************/

    public static Object getVarWithEffort(final TeaContext  context,
                                          final TeaSymbol name)
        throws TeaException {

        TeaFunction callbackFunc = null;
        Object[]    callbackArgs = new Object[2];

        callbackArgs[0] = CALLBACK_NAME;
        callbackArgs[1] = name;
    
        try {
            callbackFunc = (TeaFunction)context.getVar(CALLBACK_NAME);
        } catch ( ClassCastException e1 ) {
            // Variable TEA_NOFUNC_CALLBACK does not contain a Tea function.
            throw new TeaNoSuchVarException(name);
        } catch ( TeaNoSuchVarException e2 ) {
            // Variable TEA_NOFUNC_CALLBACK is not defined.
            throw new TeaNoSuchVarException(name);
        }

        callbackFunc.exec(callbackFunc, context, callbackArgs);

        return context.getVar(name);
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

