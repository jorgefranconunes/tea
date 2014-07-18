/**************************************************************************
 *
 * Copyright (c) 2001-2013 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.SNoSuchClassException;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaSymbol;
import com.pdmfc.tea.TeaNoSuchVarException;
import com.pdmfc.tea.TeaTypeException;





/**************************************************************************
 *
 * This class implements some utility methods.
 *
 **************************************************************************/

public final class STosUtil
    extends Object {





    /**
     * The name of variable with the missing function callback action.
     */
    public static final TeaSymbol CALLBACK_NAME =
        TeaSymbol.addSymbol("TEA_NOCLASS_CALLBACK");





/**************************************************************************
 *
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private STosUtil() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * Tries to cast one of the elements in the <code>args</code> array
 * into an <code>STosObj</code>.
 *
 * @param args Array of <code>STObj</code> that is tipically the array
 * of arguments passed to a Tea function.
 *
 * @param index Index into the <code>args</code> array of the element
 *to fetch.
 *
 * @return The element at the <code>index</code> position of the
 * <code>args</code> array.
 *
 * @exception com.pdmfc.tea.TeaTypeException Throw if the
 * element at the <code>index</code> position of the <code>args</code>
 * array is not an <code>STosObj</code>.
 *
 **************************************************************************/

    public static STosObj getTosObj(final Object[] args,
                                    final int      index)
        throws TeaTypeException {

        try {
            return (STosObj)args[index];
        } catch ( ClassCastException e ) {
            throw new TeaTypeException(args, index, "TOS object");
      }
    }





/**************************************************************************
 *
 * Retrieves an <code>STosClass</code> object. This is referenced by
 * <code>args[index]</code>, which is either the
 * <code>STosClass</code> object we are looking for or a symbol. In
 * this later case there should be a variable referenced by that
 * symbol containing the <code>STosClass</code>.
 *
 * @param context The context where we will look for a variable, if
 * necessary.
 *
 * @param args Array of to a function or method call.
 *
 * @param index An index into the <code>args</code> array.
 *
 * @exception TeaException Thrown if the <code>STosClass</code>
 * object could not be retrieved.
 *
 **************************************************************************/

    public static STosClass getClass(final TeaContext context,
                                     final Object[] args,
                                     final int      index)
        throws TeaException {

        Object ref = args[index];

        if ( ref instanceof TeaSymbol ) {
            try {
                ref = context.getVar((TeaSymbol)ref);
            } catch ( TeaNoSuchVarException e ) {
                try {
                    ref = getClassWithEffort(context, (TeaSymbol)ref);
                } catch ( TeaNoSuchVarException e2 ) {
                    throw new SNoSuchClassException(args, (TeaSymbol)ref);
                }
            }
        }
        if ( ref instanceof STosClass ) {
            return (STosClass)ref;
        }

        throw new TeaTypeException(args, index, "symbol or a class");
    }





/**************************************************************************
 *
 * Fetches a class object.
 *
 * @param context Context where the class will be searched and its
 * constructor invoked.
 *
 * @param className Symbol identifying the class.
 *
 **************************************************************************/

    public static STosClass getClass(final TeaContext  context,
                                     final TeaSymbol className)
        throws TeaException  {

        Object classObject;

        try {
            classObject = context.getVar(className);
        } catch ( TeaNoSuchVarException e ) {
            try {
                classObject = getClassWithEffort(context, className);
            } catch ( TeaNoSuchVarException e2 ) {
                throw new SNoSuchClassException(className);
            }
        }
        try {
            return (STosClass)classObject;
        } catch ( ClassCastException e ) {
            throw new TeaTypeException("Variable "
                                     + className.getName()
                                     + " does not contain a class object.");
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object getClassWithEffort(final TeaContext  context,
                                             final TeaSymbol name)
        throws TeaException {

        TeaFunction callbackFunc = null;
        Object[]     callbackArgs = new Object[2];
     
        callbackArgs[0] = CALLBACK_NAME;
        callbackArgs[1] = name;
    
        try {
            callbackFunc = (TeaFunction)context.getVar(CALLBACK_NAME);
        } catch ( ClassCastException e1 ) {
            // Variable TEA_NOCLASS_CALLBACK does not containg a Tea function.
            throw new TeaNoSuchVarException(name);
        } catch ( TeaNoSuchVarException e2 ) {
            // Variable TEA_NOCLASS_CALLBACK is not defined.
            throw new TeaNoSuchVarException(name);
        }

        callbackFunc.exec(callbackFunc, context, callbackArgs);
     
        return context.getVar(name);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static STosObj newInstance(final TeaSymbol className,
                                      final TeaContext  context,
                                      final Object[]   constructorArgs)
        throws TeaException {

        STosClass tosClass = getClass(context, className);
        STosObj   tosObj   = tosClass.newInstance(context, constructorArgs);

        return tosObj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static STosObj newInstance(final TeaSymbol className,
                                      final TeaContext  context) 
        throws TeaException {

        Object[] constructorArgs = new Object[2];

        return newInstance(className, context, constructorArgs);
    }

}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

