/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: STosUtil.java,v 1.3 2002/11/02 16:05:02 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.SNoSuchClassException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNoSuchVarException;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;





/**************************************************************************
 *
 * This class implements some utility methods to be used by derived classes.
 *
 **************************************************************************/

public class STosUtil
    extends Object {





    /** The name of variable with the missing function callback action. */
    public static final SObjSymbol CALLBACK_NAME = SObjSymbol.addSymbol("TEA_NOCLASS_CALLBACK");





/**************************************************************************
 *
 * Tries to cast one of the elements in the <code>args</code> array
 * into an <code>STosObj</code>.
 *
 * @param args Array of <code>STObj</code> that is tipically the array
 *of arguments passed to a Tea function.
 *
 * @param index Index into the <code>args</code> array of the element
 *to fetch.
 *
 * @return The element at the <code>index</code> position of the
 *<code>args</code> array.
 *
 * @exception com.pdmfc.tea.runtime.STTypeException Throw if the
 *element at the <code>index</code> position of the <code>args</code>
 *array is not an <code>STosObj</code>.
 *
 **************************************************************************/

    public static STosObj getTosObj(Object[] args,
				    int      index)
        throws STypeException {

	try {
	    return (STosObj)args[index];
	} catch (ClassCastException e) {
	    throw new STypeException(args[0],
				     "argument " + index
				     + " should be a TOS object, not a "
				     + STypes.getTypeName(args[index]));
      }
    }





/**************************************************************************
 *
 * Retrieves an <TT>STosClass</TT> object. This is referenced by
 * <TT>args[index]</TT>, which is either the <TT>STosClass</TT> object
 * we are looking for or a symbol. In this later case there should be
 * a variable referenced by that symbol containing the
 * <TT>STosClass</TT>.
 *
 * @param context The context where we will look for a variable, if
 * necessary.
 *
 * @param args Array of to a function or method call.
 *
 * @param index An index into the <code>args</code> array.
 *
 * @exception com.pdmfc.tea.modules.tos.STosNoSuchClassException
 * Thrown if the <TT>STosClass</TT> object could not be retrieved.
 *
 **************************************************************************/

    public static STosClass getClass(SContext context,
				     Object[] args,
				     int      index)
	throws STeaException {

	Object ref = args[index];

	if ( ref instanceof SObjSymbol ) {
	    try {
		ref = context.getVar((SObjSymbol)ref);
	    } catch (SNoSuchVarException e) {
		try {
		    ref = getClassWithEffort(context, (SObjSymbol)ref);
		} catch (SNoSuchVarException e2) {
		    throw new SNoSuchClassException(args[0], (SObjSymbol)ref);
		}
	    }
	}
	if ( ref instanceof STosClass ) {
	    return (STosClass)ref;
	}

	throw new STypeException(args[0],
				 "arg " + index +
				 " should be either a symbol or a class, not a " +
				 STypes.getTypeName(ref));
    }





/**************************************************************************
 *
 * Fetches a class object.
 *
 * @param context Context where the class will be searched and its
 *constructor invoked.
 *
 * @param className Symbol identifying the class.
 *
 **************************************************************************/

    public static STosClass getClass(SContext   context,
				     SObjSymbol className)
	throws STeaException  {

	Object classObject;

	try {
	    classObject = context.getVar(className);
	} catch (SNoSuchVarException e) {
	    try {
		classObject = getClassWithEffort(context, className);
	    } catch (SNoSuchVarException e2) {
		throw new SNoSuchClassException(className);
	    }
	}
	try {
	    return (STosClass)classObject;
	} catch (ClassCastException e) {
	    throw new STypeException("Variable "
				     + className.getName()
				     + " does not contain a class object.");
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object getClassWithEffort(SContext context,
					     SObjSymbol name)
	throws STeaException {

	SObjFunction callbackFunc   = null;
	Object       callbackArgs[] = new Object[2];
     
	callbackArgs[0] = CALLBACK_NAME;
	callbackArgs[1] = name;
    
	try {
	    callbackFunc = (SObjFunction)context.getVar(CALLBACK_NAME);
	} catch (ClassCastException e1) {
	    // Variable TEA_NOCLASS_CALLBACK does not containg a Tea function.
	    throw new SNoSuchVarException(name);
	} catch (SNoSuchVarException e2) {
	    // Variable TEA_NOCLASS_CALLBACK is not defined.
	    throw new SNoSuchVarException(name);
	}

	callbackFunc.exec(callbackFunc, context, callbackArgs);
     
	return context.getVar(name);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static STosObj newInstance(SObjSymbol className,
				      SContext   context,
				      Object[]   constructorArgs)
	throws STeaException {

	STosClass tosClass = getClass(context, className);
	STosObj   tosObj   = tosClass.newInstance(context, constructorArgs);

	return tosObj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static STosObj newInstance(SObjSymbol className,
				      SContext   context) 
	throws STeaException {

	Object[] constructorArgs = new Object[2];

	return newInstance(className, context, constructorArgs);
    }

}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

