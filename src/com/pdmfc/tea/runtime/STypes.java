/**************************************************************************
 *
 * Copyright (c) 2001, 2002, 2003 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: STypes.java,v 1.10 2004/09/02 14:24:50 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/02/12 The "getTypeName(...)" methods now recognize TOS objects
 * and TOS classes. (jfn)
 *
 * 2001/05/12 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.SNoSuchVarException;





/**************************************************************************
 *
 * Utility methods for handling Java objects corresponding to Tea
 * basic types.
 *
 **************************************************************************/

public class STypes
    extends Object {





    // The name of variable with the missing function callback action.
    private static final SObjSymbol CALLBACK_NAME =
	SObjSymbol.addSymbol("TEA_NOFUNC_CALLBACK");

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

    private STypes() {
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

    public static String getTypeName(Object obj) {

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

    public static String getTypeName(Class aClass) {

	if ( SObjBlock.class.isAssignableFrom(aClass) ) {
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
	if ( SObjFunction.class.isAssignableFrom(aClass) ) {
	    return TYPE_FUNC;
	}
	if ( SObjPair.class.isAssignableFrom(aClass) ) {
	    return TYPE_PAIR;
	}
	if ( String.class.isAssignableFrom(aClass) ) {
	    return TYPE_STRING;
	}
	if ( SObjSymbol.class.isAssignableFrom(aClass) ) {
	    return TYPE_SYMBOL;
	}
	if ( SObjNull.class.isAssignableFrom(aClass) ) {
	    return TYPE_NULL;
	}
	if ( STosClass.class.isAssignableFrom(aClass) ) {
	    return TYPE_TOSCLASS;
	}
	return aClass.getName();
    }





/**************************************************************************
 *
 * Tries to convert argument <TT>index</TT> into a SObjBlock. If that
 * argument is not a block, an exception is thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 * received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception STeaException Thrown if <TT>args[index]</TT> is not a
 * SObjBlock.
 *
 **************************************************************************/

    static public SObjBlock getBlock(Object[] args,
				     int      index)
	throws STeaException {

	try {
	    return (SObjBlock)args[index];
	} catch (ClassCastException e) {
	    throw new STypeException(args[0],
				     "argument " + index+" should be a block, "+
				     "not a " + getTypeName(args[index]));
	}
    }





/**************************************************************************
 *
 * Tries to convert argument <TT>index</TT> into a SObjPair. If that
 * argument is not a pair, an exception is thrown.
 *
 * @param args
 *     Array of <TT>Object</TT>, supposed to be the arguments received by
 *     a call to the command.
 *
 * @param index
 *     The index of the argument to convert.
 *
 * @exception com.pdmfc.tea.STeaException
 *     Thrown if <TT>args[index]</TT> is not a SObjPair.
 *
 **************************************************************************/

    static public SObjPair getPair(Object[] args,
				   int      index)
	throws STeaException {

	try {
	    return (SObjPair)args[index];
	} catch (ClassCastException e) {
	    throw new STypeException(args[0],
				      "argument "+index+" should be a pair, " +
				      "not a " + getTypeName(args[index]));
	}
    }





/**************************************************************************
 *
 * This is an utility method, to be used by derived classes. It tries to
 * convert argument <TT>index</TT> into a SObjSymbol. If that argument is
 * not a pair, an exception is thrown.
 *
 * @param args
 *     Array of <TT>Object</TT>, supposed to be the arguments received by
 *     a call to the command.
 *
 * @param index
 *     The index of the argument to convert.
 *
 * @exception com.pdmfc.tea.STypeException Thrown if
 * <code>args[index]</code> is not a <code>SObjSymbol</code>.
 *
 **************************************************************************/

    static public SObjSymbol getSymbol(Object[] args,
				       int      index)
	throws STypeException {

	try {
	    return (SObjSymbol)args[index];
	} catch (ClassCastException e) {
	    throw new STypeException(args[0],
				      "argument " +index+" should be a symbol, " +
				      "not a " + getTypeName(args[index]));
	}
    }





/**************************************************************************
 *
 * This is an utility method, to be used by derived classes. It tries
 * to convert argument <TT>index</TT> into a <TT>SObjNumeric</TT>. If
 * that argument is not an int, an exception is thrown.
 *
 * @param args
 *     Array of <TT>Object</TT>, supposed to be the arguments received by
 *     a call to the command.
 *
 * @param index
 *     The index of the argument to convert.
 *
 * @exception com.pdmfc.tea.STeaException
 *     Thrown if <TT>args[index]</TT> is not a <TT>SObjNumeric</TT>.
 *
 **************************************************************************/

    static public Number getNumber(Object[] args,
				   int      index)
	throws STeaException {

	try {
	    return (Number)args[index];
	} catch (ClassCastException e) {
	    throw new STypeException(args[0],
				      "argument " + index+" should be numeric, " +
				      "not a " + getTypeName(args[index]));
	}
    }





/**************************************************************************
 *
 * This is an utility method, to be used by derived classes. It tries to
 * convert argument <TT>index</TT> into a SObjInt. If that argument is
 * not an int, an exception is thrown.
 *
 * @param args
 *     Array of <TT>Object</TT>, supposed to be the arguments received by
 *     a call to the command.
 *
 * @param index
 *     The index of the argument to convert.
 *
 * @exception com.pdmfc.tea.STypeException Thrown if
 * <code>args[index]</code> is not an <code>SObjInt</code>.
 *
 **************************************************************************/

    static public Integer getInt(Object[] args,
				 int      index)
	throws STypeException {

	try {
	    return (Integer)args[index];
	} catch (ClassCastException e) {
	    throw new STypeException(args[0],
				     "argument " + index +" should be an int, " +
				     "not a " + getTypeName(args[index]));
	}
    }





/**************************************************************************
 *
 * This is an utility method, to be used by derived classes. It tries to
 * convert argument <TT>index</TT> into a SObjFloat. If that argument is
 * not a float, an exception is thrown.
 *
 * @param args
 *     Array of <TT>Object</TT>, supposed to be the arguments received by
 *     a call to the command.
 *
 * @param index
 *     The index of the argument to convert.
 *
 * @exception com.pdmfc.tea.STypeException Thrown if
 * <code>args[index]</code> is not a <code>SObjFloat</code>.
 *
 **************************************************************************/

    static public Double getFloat(Object[] args,
				  int      index)
	throws STypeException {

	try {
	    return (Double)args[index];
	} catch (ClassCastException e) {
	    throw new STypeException(args[0],
				      "argument " + index+" should be a float, " +
				      "not a " + getTypeName(args[index]));
	}
    }





/**************************************************************************
 *
 * This is an utility method, to be used by derived classes. It tries to
 * convert argument <TT>index</TT> into a SObjBool. If that argument is
 * not a boolean, an exception is thrown.
 *
 * @param args
 *     Array of <TT>Object</TT>, supposed to be the arguments received by
 *     a call to the command.
 *
 * @param index
 *     The index of the argument to convert.
 *
 * @exception com.pdmfc.tea.STypeException Thrown if
 * <code>args[index]</code> is not a <code>java.lang.Boolean</code>.
 *
 **************************************************************************/

    static public Boolean getBoolean(Object[] args,
				     int      index)
	throws STypeException {

	try {
	    return (Boolean)args[index];
	} catch (ClassCastException e) {
	    throw new STypeException(args[0],
				     "argument "+index+" should be a boolean, " +
				     "not a " + getTypeName(args[index]));
	}
    }





/**************************************************************************
 *
 * This is an utility method, to be used by derived classes. It tries to
 * convert argument <TT>index</TT> into a SObjString. If that argument is
 * not an int, an exception is thrown.
 *
 * @param args
 *     Array of <TT>Object</TT>, supposed to be the arguments received by
 *     a call to the command.
 *
 * @param index
 *     The index of the argument to convert.
 *
 * @exception com.pdmfc.tea.STypeException Thrown if
 * <code>args[index]</code> is not an <code>SObjString</code>.
 *
 **************************************************************************/

    static public String getString(Object[] args,
				   int      index)
	throws STypeException {

	try {
	    return (String)args[index];
	} catch (ClassCastException e) {
	    throw new STypeException(args[0],
				      "argument " +index+" should be a string, " +
				      "not a " + getTypeName(args[index]));
	}
    }





/**************************************************************************
 *
 * This is an utility method, to be used by derived classes. It tries to
 * convert argument <TT>index</TT> into a SObjFunction.
 *
 * If <TT>args[index]</TT> is a SObjFunction then everything is fine.
 * If <TT>args[index]</TT> is a SObjSymbol then a variable with the some
 * name is searched in <TT>context</TT>. If it does not exists or if it does
 * not contain a <TT>SObjFunction</TT> an exception is thrown.
 *
 * @param args
 *     Array of <TT>Object</TT>, supposed to be the arguments received by
 *     a call to the command.
 *
 * @param index
 *     The index of the argument to convert.
 *
 * @exception com.pdmfc.tea.STeaException
 *     Thrown if <TT>args[index]</TT> is neither a <TT>SObjFunction</TT> nor
 *     a <TT>SObjSymbol</TT>
 *
 **************************************************************************/

    static public SObjFunction getFunction(SContext context,
					   Object[]  args,
					   int       index)
	throws STeaException {

	Object obj = args[index];
	Object value;

	if ( obj instanceof SObjFunction ) {
	    return (SObjFunction)obj;
	}

	try {
	    value = context.getVar((SObjSymbol)obj);
	} catch (ClassCastException e1) {
	    throw new STypeException(args[0],
				     "argument " + index +
				     " should be a function or a symbol, " +
				     "not a " + getTypeName(obj));
	} catch (SNoSuchVarException e2) {
	    value = getVarWithEffort(context, (SObjSymbol)obj);
	}
	
	try {
	    return (SObjFunction)value;
	} catch (ClassCastException e1) {
	    throw new STypeException(args[0],
				      "variable " + ((SObjSymbol)obj).getName() +
				      " should contain a function, " +
				      "not a " + getTypeName(value));
	}
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
 * @exception com.pdmfc.tea.runtime.SNoSuchVarException Throw if the
 * variable does not exit before and after the Tea function is run.
 *
 **************************************************************************/

    public static Object getVarWithEffort(SContext   context,
					  SObjSymbol name)
	throws STeaException {

	SObjFunction callbackFunc   = null;
	Object        callbackArgs[] = new Object[2];

	callbackArgs[0] = CALLBACK_NAME;
	callbackArgs[1] = name;
    
	try {
	    callbackFunc = (SObjFunction)context.getVar(CALLBACK_NAME);
	} catch (ClassCastException e1) {
	    // Variable TEA_NOFUNC_CALLBACK does not contain a Tea function.
	    throw new SNoSuchVarException(name);
	} catch (SNoSuchVarException e2) {
	    // Variable TEA_NOFUNC_CALLBACK is not defined.
	    throw new SNoSuchVarException(name);
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

