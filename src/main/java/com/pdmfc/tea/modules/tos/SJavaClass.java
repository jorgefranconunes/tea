/**************************************************************************
 *
 * Copyright (c) 2001-2005 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SJavaClass.java,v 1.6 2005/10/31 15:32:55 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2005/10/25 Changed such that methods in derived classes override
 * methods in base classes. (jfn)
 *
 * 2001/05/12 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public class SJavaClass
    extends STosClass {





    private static final String   GET_NAME_METHOD       = "getTosClassName";
    private static final Class[]  GET_NAME_METHOD_TYPES = new Class[0];
    private static final Object[] GET_NAME_METHOD_ARGS  = new Object[0];
    private static final String   CONSTRUCTOR_NAME      = "constructor";
    private static final Class[]  CTOR_TYPES = new Class[] { STosClass.class };

    private Class       _javaClass     = null;
    private Constructor _javaClassCtor = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SJavaClass(String javaClassName)
        throws SRuntimeException {

	this(getJavaClass(javaClassName));
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SJavaClass(Class javaClass)
        throws SRuntimeException {

	_javaClass     = javaClass;
	_javaClassCtor = getJavaClassConstructor(_javaClass);

	if (  !STosObj.class.isAssignableFrom(_javaClass) ) {
	    throw new SRuntimeException("Java class " + javaClass.getName()
					+ " must be derived from "
					+ STosObj.class.getName());
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
	    throw new SRuntimeException("failed to initialize Java class "
					+ javaClassName
					+ " (" + e1.getMessage() + ")");
	} catch (LinkageError e2) {
	    throw new SRuntimeException("failed to link Java class "
					+ javaClassName
					+ " (" + e2.getMessage() + ")");
	} catch (ClassNotFoundException e3) {
	    throw new SRuntimeException("failed to find Java class "
					+ javaClassName
					+ " (" + e3.getMessage() + ")");
	}

	return javaClass;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Constructor getJavaClassConstructor(Class javaClass)
	throws SRuntimeException {

	Constructor ctor = null;

	try {
	    ctor = javaClass.getConstructor(CTOR_TYPES);
	} catch (NoSuchMethodException e1) {
	    throw new SRuntimeException("missing constuctor in Java class "
					+ javaClass.getName());
	} catch (SecurityException e2) {
	    throw new SRuntimeException("no access to constuctor for Java class "
					+ javaClass.getName());
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

	Class superClass = javaClass.getSuperclass();

	if ( superClass != null ) {
	    createMethods(superClass);
	}

	createDeclaredMethods(javaClass);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void createDeclaredMethods(Class javaClass)
	throws SRuntimeException {

	Method[] methods = getJavaClassMethods(javaClass);

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
		if ( methodName.equals(CONSTRUCTOR_NAME) ) {
		    addConstructor(new SJavaMethod(method));
		} else {
		    addMethod(methodName, new SJavaMethod(method));
		}
	    }
	}
    }





/**************************************************************************
 *
 * Returns all the public methods of the given class. This also
 * includes methods in base classes.
 *
 **************************************************************************/

    private static Method[] getJavaClassMethods(Class javaClass)
	throws SRuntimeException {

	Method[] methods = null;

	try {
	    methods = javaClass.getMethods();
	    // We do not call Class.getDeclaredMethods() because it
	    // throws SecurityException when running inside
	    // JavaWebStart.
	} catch (SecurityException e) {
	    throw new SRuntimeException("no access to methods of Java class "
					+ javaClass.getName());
	}

	return methods;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setClassName(Class javaClass) {

	Method getNameMethod = null;
	String tosClassName  = null;

	try {
	    getNameMethod = javaClass.getMethod(GET_NAME_METHOD,
						GET_NAME_METHOD_TYPES);
	} catch (NoSuchMethodException e1) {
	    // Ignore it.
	} catch (SecurityException e2) {
	    // Ignore it.
	}

	if ( getNameMethod != null ) {
	    try {
		tosClassName =
		    (String)getNameMethod.invoke(null, GET_NAME_METHOD_ARGS);
	    } catch (IllegalAccessException e1) {
	    } catch (IllegalArgumentException e2) {
	    } catch (InvocationTargetException e3) {
	    } catch (NullPointerException e4) {
	    } catch (ClassCastException e5) {
	    }
	}

	if ( tosClassName != null ) {
	    setName(tosClassName);
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public STosObj newInstance()
	throws STeaException {

	STosObj obj = null;

	try {
	    obj = (STosObj)_javaClassCtor.newInstance(new Object[] {this});
	} catch (InstantiationException e1) {
	    throw new SRuntimeException("failed to instantiate Java class " 
					+ _javaClass.getName()
					+ "(" + e1.getMessage() + ")");
	} catch (IllegalAccessException e2) {
	    throw new SRuntimeException("failed to instantiate Java class " 
					+ _javaClass.getName()
					+ "(" + e2.getMessage() + ")");
	} catch (IllegalArgumentException e3) {
	    throw new SRuntimeException("failed to instantiate Java class " 
					+ _javaClass.getName()
					+ "(" + e3.getMessage() + ")");
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

    private static void internalError(Throwable error)
	throws SRuntimeException {

	throw new SRuntimeException("internal error - "
				    + error.getClass().getName()
				    + " - " + error.getMessage());
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

