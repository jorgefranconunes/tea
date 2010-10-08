/**************************************************************************
 *
 * Copyright (c) 2001-2010 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.tos;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.text.MessageFormat;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.tos.SNoSuchMethodException;
import com.pdmfc.tea.tos.STosClass;
import com.pdmfc.tea.tos.STosObj;





/**************************************************************************
 *
 * A TOS class implemented as a Java class.
 *
 * <p>If the Java class has a static method named "getTosClassName()"
 * returning a string then that method will be invoked and its return
 * value will be used as the name of the TOS class.</p>
 *
 **************************************************************************/

public class STosJavaClass
    implements STosClass {





    private static final String   GET_NAME_METHOD       = "getTosClassName";
    private static final Class[]  GET_NAME_METHOD_TYPES = new Class[0];
    private static final Object[] GET_NAME_METHOD_ARGS  = new Object[0];

    private static final Class[]  CTOR_TYPES = new Class[] { STosClass.class };

    private static final SObjSymbol[] MEMBER_NAMES = new SObjSymbol[0];

    private Class       _javaClass     = null;
    private Constructor _javaClassCtor = null;
    private String      _tosClassName  = null;

    // It is also stored in the _methods Map having "constructor" for
    // key.
    private STosMethod  _constructor   = null;

    // Keys are SObjSymbol. Values are STosMethod.
    private Map<SObjSymbol,STosMethod> _methods =
        new HashMap<SObjSymbol,STosMethod>();





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public STosJavaClass(String javaClassName)
        throws SRuntimeException {

	this(getJavaClass(javaClassName));
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public STosJavaClass(Class javaClass)
        throws SRuntimeException {

	_javaClass     = javaClass;
	_javaClassCtor = getJavaClassConstructor(_javaClass);

	if (  !STosObj.class.isAssignableFrom(_javaClass) ) {
	    throw new SRuntimeException("Java class {0} must implement {1}",
					new Object[] { javaClass.getName(),
						       STosObj.class.getName() });
	}
	createMethods(_javaClass);
	setClassName(_javaClass);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Class getJavaClass(String javaClassName)
	throws SRuntimeException {

	Class javaClass = null;

	try {
	    javaClass = Class.forName(javaClassName);
	} catch (ExceptionInInitializerError e1) {
	    String   fmt  = "failed to initialize class {0} - {1}";
	    Object[] args = { javaClassName, e1.getMessage() };
	    String   msg  = MessageFormat.format(fmt, args);
	    throw new SRuntimeException(msg);
	} catch (LinkageError e2) {
	    String    fmt  = "failed to link class {0} - {1}";
	    Object [] args = { javaClassName, e2.getMessage() };
	    String    msg  = MessageFormat.format(fmt, args);
	    throw new SRuntimeException(msg);
	} catch (ClassNotFoundException e3) {
	    String fmt = "failed to find class {0} - {1}";
	    Object[] args = { javaClassName, e3.getMessage() };
	    String   msg  = MessageFormat.format(fmt, args);
	    throw new SRuntimeException(msg);
	}

	return javaClass;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Constructor getJavaClassConstructor(Class<?> javaClass)
	throws SRuntimeException {

	Constructor ctor = null;

	try {
	    ctor = javaClass.getConstructor(CTOR_TYPES);
	} catch (NoSuchMethodException e1) {
	    throw new SRuntimeException("missing constuctor in class {0}",
					javaClass.getName());
	} catch (SecurityException e2) {
	    throw new SRuntimeException("no access to constuctor for class {0}",
					javaClass.getName());
	}

	return ctor;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void createMethods(Class javaClass)
	throws SRuntimeException {

	Method[] methods   = getJavaClassMethods(javaClass);

	for ( int i=methods.length; (i--)>0; ) {
	    Method  method     = methods[i];
	    String  methodName = method.getName();
	    Class[] argTypes   = method.getParameterTypes();
	    Class   retType    = method.getReturnType();

	    if ( (argTypes.length==3)
		 && (argTypes[0]==SObjFunction.class)
		 && (argTypes[1]==SContext.class)
		 && (argTypes[2]==Object[].class)
		 && (retType==Object.class)
		 && !methodName.equals("exec") ) {
		
		STosMethod tosMethod     = new STosJavaMethod(this, method);
		SObjSymbol tosMethodName = SObjSymbol.addSymbol(methodName);

		if ( tosMethodName == STosClass.CONSTRUCTOR_NAME ) {
		    setConstructor(tosMethod);
		} else {
		    addMethod(tosMethodName, tosMethod);
		}
	    }
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Method[] getJavaClassMethods(Class javaClass)
	throws SRuntimeException {

	Method[] methods = null;

	try {
	    methods = javaClass.getMethods();
	} catch (SecurityException e) {
	    throw new SRuntimeException("no access to methods of class {0}",
					javaClass.getName());
	}

	return methods;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setClassName(Class<?> javaClass) {

	Method getNameMethod = null;

	try {
	    getNameMethod = javaClass.getMethod(GET_NAME_METHOD,
						GET_NAME_METHOD_TYPES);
	} catch (NoSuchMethodException e1) {
	    // Does not matter.
	} catch (SecurityException e2) {
	    // Does not matter.
	}

	if ( getNameMethod != null ) {
	    try {
		_tosClassName = (String)getNameMethod.invoke(null,
							     GET_NAME_METHOD_ARGS);
	    } catch (Throwable e) {
		// Does not matter. A name will be automatically
		// chosen.
	    }
	}

	if ( _tosClassName == null ) {
	    String javaName = javaClass.getName();
	    _tosClassName = javaName.substring(1+javaName.lastIndexOf('.'));
	}
    }





/**************************************************************************
 *
 * @return The null value, meaning this TOS class has no base class.
 *
 **************************************************************************/

    public STosClass getSuperClass() {

	return null;
    }





/**************************************************************************
 *
 * Fetches the member names for this TOS class. It has no members, so
 * it just returns an empty array of member names.
 *
 * @return An empty array, meaning this TOS class does not define any
 * members.
 *
 **************************************************************************/

    public SObjSymbol[] getMemberNames() {

	return MEMBER_NAMES;
    }





/**************************************************************************
 *
 * Specifies the constructor for this TOS class.
 *
 * @param method A reference to the <TT>SObjFunction</TT> object that
 * implements the TOS method.
 *
 **************************************************************************/

    public void setConstructor(STosMethod method) {

	addMethod(STosClass.CONSTRUCTOR_NAME, method);
	_constructor = method;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public STosMethod getConstructor() {

	return _constructor;
    }





/**************************************************************************
 *
 * Associates a new TOS method with this TOS class. If a method with
 * the same name already existed then it is superceded by this new
 * method.
 *
 * @param methodName A symbol standing for the name of the method
 * being defined.
 *
 * @param method A reference to the <TT>SObjFunction</TT> object that
 * implements the TOS method.
 *
 **************************************************************************/

    public void addMethod(SObjSymbol methodName,
			  STosMethod method) {

	_methods.put(methodName, method);
    }





/**************************************************************************
 *
 * Fecthes the named method.
 *
 * @return The <code>{@link STosMethod}</code> of this TOS class named
 * <code>methodName</code>.
 *
 * @exception SNoSuchMethodException Thrown if there is no name in
 * this tos class named <code>methodName</code>.
 *
 **************************************************************************/

    public STosMethod getMethod(SObjSymbol methodName)
	throws SNoSuchMethodException {

	STosMethod method = _methods.get(methodName);

	if ( method == null ) {
	    throw new SNoSuchMethodException(_tosClassName, methodName);
	}

	return method;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Iterator getMethods() {

	return _methods.values().iterator();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public STosObj newInstance(SContext context,
			       Object[] args)
	throws STeaException {

	STosObj obj = null;

	try {
	    obj = (STosObj)_javaClassCtor.newInstance(new Object[] {this});
	} catch (InstantiationException e1) {
	    internalError(e1);
	} catch (IllegalAccessException e2) {
	    internalError(e2);
	} catch (IllegalArgumentException e3) {
	    internalError(e3);
	} catch (InvocationTargetException e4) {
	    Throwable error = e4.getTargetException();
	    if ( error instanceof STeaException ) {
		throw (STeaException)error;
	    } else {
		internalError(error);
	    }
	}

	return obj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void internalError(Throwable error)
	throws SRuntimeException {

	String   fmt  = "Java class {0} instantiation failed - {1} - {2}";
	Object[] args = { _javaClass.getName(),
			  error.getClass().getName(),
			  error.getMessage() };
	String   msg  = MessageFormat.format(fmt, args);

	throw new SRuntimeException(msg);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public String getName() {

	return _tosClassName;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

