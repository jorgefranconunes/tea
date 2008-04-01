/**************************************************************************
 *
 * Copyright (c) 2005 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SModuleReflect.java,v 1.8 2007/03/06 19:39:35 pcorreia Exp $
 *
 *
 * Revisions:
 *
 * $Log: SModuleReflect.java,v $
 * Revision 1.8  2007/03/06 19:39:35  pcorreia
 * TSK-PDMFC-TEA-0038 - Added support for array returning methods.
 *
 * Revision 1.7  2006/10/11 14:19:41  jpsl
 * doc updates with the Since tag based on doc/release-notes.txt
 *
 * Revision 1.6  2006/10/11 13:35:58  jpsl
 * TSK-PDMFC-TEA-0033 TeaDoc Since tag
 *
 * Revision 1.5  2006/01/28 16:37:07  pcorreia
 * Better wrapping/unwrapping of objects as parameters to methods
 *
 * Revision 1.4  2006/01/19 18:57:06  jfn
 * Changed names of functions in "tea.java" module from "reflect-*" to "java-*".
 *
 * Revision 1.3  2006/01/18 00:36:50  pcorreia
 * Support for setting member objects. Support to get/set members on instances.
 *
 * Revision 1.2  2005/12/07 16:46:05  jfn
 * Minor teadoc corrections that prevented teadoc generation.
 *
 * Revision 1.1  2005/12/05 20:37:53  pcorreia
 * Support for reflection over Java
 *
 *
 **************************************************************************/

package com.pdmfc.tea.modules.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.modules.tdbc.SConnection;
import com.pdmfc.tea.modules.tdbc.SStatement;
import com.pdmfc.tea.modules.tdbc.SPreparedStatement;
import com.pdmfc.tea.modules.tdbc.SCallableStatement;
import com.pdmfc.tea.modules.tdbc.SResultSet;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.util.SDate;
import com.pdmfc.tea.modules.util.SHashtable;
import com.pdmfc.tea.modules.util.SVector;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.STeaRuntime;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypes;
import com.pdmfc.tea.runtime.STypeException;





//*
//* <TeaModule name="tea.java">
//* 
//* <Overview>
//* Tea reflection.
//* </Overview>
//*
//* <Description>
//* Tea reflection over Java objects.
//* </Description>
//*
//* <Since version="3.2.1"/>
//*
//* </TeaModule>
//*

/**************************************************************************
 *
 * Package of commands for reflection.
 *
 **************************************************************************/

public class SModuleReflect
    extends SModule {





    private static Map _primitiveTypes = new HashMap();

    static {
	_primitiveTypes.put(Boolean.TYPE.getName(),   Boolean.TYPE);
	_primitiveTypes.put(Character.TYPE.getName(), Character.TYPE);
	_primitiveTypes.put(Byte.TYPE.getName(),      Byte.TYPE);
	_primitiveTypes.put(Short.TYPE.getName(),     Short.TYPE);
	_primitiveTypes.put(Integer.TYPE.getName(),   Integer.TYPE);
	_primitiveTypes.put(Long.TYPE.getName(),      Long.TYPE);
	_primitiveTypes.put(Float.TYPE.getName(),     Float.TYPE);
	_primitiveTypes.put(Double.TYPE.getName(),    Double.TYPE);
	_primitiveTypes.put(Void.TYPE.getName(),      Void.TYPE);
    }

    private static Map _primitiveWrappers = new HashMap();

    static {
	_primitiveWrappers.put(Boolean.class,   Boolean.TYPE);
	_primitiveWrappers.put(Character.class, Character.TYPE);
	_primitiveWrappers.put(Byte.class,      Byte.TYPE);
	_primitiveWrappers.put(Short.class,     Short.TYPE);
	_primitiveWrappers.put(Integer.class,   Integer.TYPE);
	_primitiveWrappers.put(Long.class,      Long.TYPE);
	_primitiveWrappers.put(Float.class,     Float.TYPE);
	_primitiveWrappers.put(Double.class,    Double.TYPE);
	_primitiveWrappers.put(Void.class,      Void.TYPE);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SModuleReflect() {
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void init(STeaRuntime context)
	throws STeaException {

	super.init(context);

	context.addFunction("java-get-value",
			    new SObjFunction() {
				public Object exec(SObjFunction func,
						   SContext     context,
						   Object[]     args)
				    throws STeaException {
				    return functionGetValue(func,
							    context,
							    args);
				}
			    });

	context.addFunction("java-set-value",
			    new SObjFunction() {
				public Object exec(SObjFunction func,
						   SContext     context,
						   Object[]     args)
				    throws STeaException {
				    return functionSetValue(func,
							    context,
							    args);
				}
			    });

	context.addFunction("java-get-method",
			    new SObjFunction() {
				public Object exec(SObjFunction func,
						   SContext     context,
						   Object[]     args)
				    throws STeaException {
				    return functionGetMethod(func,
							     context,
							     args);
				}
			    });

	context.addFunction("java-exec-method",
			    new SObjFunction() {
				public Object exec(SObjFunction func,
						   SContext     context,
						   Object[]     args)
				    throws STeaException {
				    return functionExecMethod(func,
							      context,
							      args);
				}
			    });

	context.addFunction("java-new-instance",
			    new SObjFunction() {
				public Object exec(SObjFunction func,
						   SContext     context,
						   Object[]     args)
				    throws STeaException {
				    return functionNewInstance(func,
							       context,
							       args);
				}
			    });
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void stop() {
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void end() {

	stop();
    }





//* 
//* <TeaFunction name="java-get-value"
//* 		 arguments="[javaClassName|wrapperObject] memberName"
//*              module="tea.java">
//*
//* <Overview>
//* Returns the value stored in the member on the given class or object.
//* </Overview>
//*
//* <Parameter name="[javaClassName|wrapperObject]">
//* String or Symbol identifying a Java class, or a wrapperObject, created 
//* with the <code>java-new-instance</code> function.
//* </Parameter>
//*
//* <Parameter name="memberName">
//* String or Symbol identifying a member in the Java class or object.
//* </Parameter>
//*
//* <Description>
//* Returns the value of the given member.
//* </Description>
//* 
//* <Since version="3.2.1"/>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionGetValue(SObjFunction func,
					   SContext     context,
					   Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException(args[0], "[className|wrapperObj] memberName");
	}

	Object            firstArg   = args[1];
	String            className  = null;
	String            memberName = getStringOrSymbol(args,2);

	Object            result     = null;
	JavaWrapperObject targetObj  = null;

	if (firstArg instanceof JavaWrapperObject) {
	    targetObj = (JavaWrapperObject)firstArg;
	} else {
	    className = getStringOrSymbol(args,1);
	}

	try {
	    if ( null == targetObj ) {
		Class cl = Class.forName(className);
		result = getFieldValue(cl, null, memberName);
	    } else {
		result = targetObj.getValue(memberName);
	    }
	} catch (ClassNotFoundException e) {
	    throw new SRuntimeException(args[0],
					"could not load class '" + 
					className + "'");
	} catch (NoSuchFieldException e) {
	    throw new SRuntimeException(args[0],
					"could not find member '" + 
					memberName + "'");
	} catch (IllegalAccessException e) {
	    throw new SRuntimeException(args[0],
					"cannot access member '" + 
					memberName + "'");
	} catch (NullPointerException e) {
	    throw new SRuntimeException(args[0],
					"member '" + 
					memberName + "' is not static");
	}

	return java2Tea(result, context);
    }




    

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object getFieldValue(Class  aClass, 
					Object aObj, 
					String memberName)
	throws NoSuchFieldException,
	       IllegalAccessException,
	       NullPointerException {
	
	Object result     = null;
	
	Field fld = aClass.getField(memberName);
	result = fld.get(aObj);
	
	return result;
    }





//* 
//* <TeaFunction name="java-set-value"
//* 		 arguments="[javaClassName|wrapperObject] memberName value"
//*              module="tea.java">
//*
//* <Overview>
//* Sets the value of a member on the given class or object.
//* </Overview>
//*
//* <Parameter name="[javaClassName|wrapperObject]">
//* String or Symbol identifying a Java class, or a wrapperObject, created 
//* with the <code>java-new-instance</code> function.
//* </Parameter>
//*
//* <Parameter name="memberName">
//* String or Symbol identifying a member in the Java class or object.
//* </Parameter>
//*
//* <Parameter name="value">
//* Value to be set on the member.
//* </Parameter>
//*
//* <Description>
//* Returns the value of the given member.
//* </Description>
//* 
//* <Since version="3.2.1"/>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionSetValue(SObjFunction func,
					   SContext     context,
					   Object[]     args)
	throws STeaException {

	if ( args.length != 4 ) {
	    throw new SNumArgException(args[0], "[className|wrapperObj] memberName value");
	}

	Object            firstArg   = args[1];
	String            className  = null;
	String            memberName = getStringOrSymbol(args,2);
	Object            value      = args[3];

	Object            result     = null;
	JavaWrapperObject targetObj  = null;

	Object            javaObj    = tea2Java(value);

	if (firstArg instanceof JavaWrapperObject) {
	    targetObj = (JavaWrapperObject)firstArg;
	} else {
	    className = getStringOrSymbol(args,1);
	}

	try {
	    if ( null == targetObj ) {
		Class cl = Class.forName(className);
		result = setFieldValue(cl, null, memberName, javaObj);
	    } else {
		result = targetObj.setValue(memberName, javaObj);
	    }
	} catch (ClassNotFoundException e) {
	    throw new SRuntimeException(args[0],
					"could not load class '" + 
					className + "'");
	} catch (NoSuchFieldException e) {
	    throw new SRuntimeException(args[0],
					"could not find member '" + 
					memberName + "'");
	} catch (IllegalAccessException e) {
	    throw new SRuntimeException(args[0],
					"cannot access member '" + 
					memberName + "'");
	} catch (NullPointerException e) {
	    throw new SRuntimeException(args[0],
					"member '" + 
					memberName + "' is not static");
	}

	return java2Tea(result, context);
    }




    

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object setFieldValue(Class  aClass, 
					Object aObj, 
					String memberName,
					Object value)
	throws NoSuchFieldException,
	       IllegalAccessException,
	       NullPointerException {
	
	Object result  = null;
	
	Field fld = aClass.getField(memberName);
	fld.set(aObj, value);
	result = fld.get(aObj);
	
	return result;
    }





//* 
//* <TeaFunction name="java-get-method"
//* 		 arguments="javaClassName methodName [argType1 [argType2 ...]]"
//*              module="tea.java">
//*
//* <Overview>
//* Returns the value stored in the static member on the given class.
//* </Overview>
//*
//* <Parameter name="javaClassName">
//* String or Symbol identifying a Java class.
//* </Parameter>
//*
//* <Parameter name="methodName">
//* String or Symbol identifying a static method in the Java class.
//* </Parameter>
//*
//* <Parameter name="argTypeN">
//* String or Symbol identifying the type of the Nth parameter of the 
//* method (fqcn or primitive type name).
//* </Parameter>
//*
//* <Description>
//* Returns a wrapper around the method.
//* </Description>
//* 
//* <Since version="3.2.1"/>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionGetMethod(SObjFunction func,
					    SContext     context,
					    Object[]     args)
	throws STeaException {

	if ( args.length < 3 ) {
	    throw new SNumArgException(args[0], "className methodName [argType1 [argType2 ...]]");
	}

	String   className  = getStringOrSymbol(args,1);
	String   methodName = getStringOrSymbol(args,2);
	Object   result     = null;
	Class[]  params     = new Class[args.length - 3];
	String   paramClassName = null;

	try {
	    for(int i=3; i<args.length; i++) {
		paramClassName = getStringOrSymbol(args,i);
		params[i-3]=getClassForName(paramClassName);
	    }
	} catch (ClassNotFoundException e) {
	    throw new SRuntimeException(args[0],
					"could not load parameter class '" + 
					paramClassName + "'");
	}
	try {
	    Class  cl  = Class.forName(className);
	    final Method mtd = findMethod(cl,methodName, params, false);
	    result = new SObjFunction() {
		    public Object exec(SObjFunction func,
				       SContext     context,
				       Object[]     args)
			throws STeaException {

			Object[] mtdArgs = new Object[args.length - 1];
			for(int i=1; i<args.length; i++) {
			    mtdArgs[i-1] = args[i];
			}

			return invokeMethod(null, 
					    mtd,
					    context,
					    mtdArgs);
		    }
		};
	} catch (ClassNotFoundException e) {
	    throw new SRuntimeException(args[0],
					"could not load class '" + 
					className + "'");
	} catch (NoSuchMethodException e) {
	    throw new SRuntimeException(args[0],
					"could not find method '" + 
					methodName + "'");
	}

	return result;
    }





/**************************************************************************
 *
 * Invokes a java static method from tea arguments
 *
 **************************************************************************/

    private static Object invokeMethod(Object       javaObj,
				       Method       mtd,
				       SContext     context,
				       Object[]     args) 
	throws STeaException {

	Object  result     = null;
	Class[] paramTypes = mtd.getParameterTypes();

	if ( args.length != paramTypes.length ) {
	    StringBuffer argsTxt = new StringBuffer();
	    for (int i=0; i<paramTypes.length; i++) {
		if (i>0) {
		    argsTxt.append(" ");
		}
		argsTxt.append(paramTypes[i].getName());
	    }
	    throw new SNumArgException(argsTxt.toString());
	}

	// convert values to java
	Object[] javaArgs = new Object[args.length];
	for(int i=0; i<javaArgs.length; i++) {
	    javaArgs[i] = tea2Java(args[i]);
	}

	try {
	    result = mtd.invoke(javaObj, javaArgs);
	} catch (IllegalAccessException e) {
 	    throw new SRuntimeException("cannot access method '" +
  					mtd.getName() + "'");
	} catch (NullPointerException e) {
 	    throw new SRuntimeException("method '" +
  					mtd.getName() + "' is not static");
	} catch (InvocationTargetException e) {
 	    throw new SRuntimeException(e);
 	}

	// convert values back to tea 
	return java2Tea(result, context);
    }





//* 
//* <TeaFunction name="java-new-instance"
//* 		 arguments="javaClassName [arg1 [arg2 ...]]"
//*              module="tea.java">
//*
//* <Overview>
//* Returns a new instance of the given class using the given arguments.
//* </Overview>
//*
//* <Parameter name="javaClassName">
//* String or Symbol identifying a Java class.
//* </Parameter>
//*
//* <Parameter name="argN">
//* Value of the arguments for the constructor.
//* </Parameter>
//*
//* <Description>
//* Returns a wrapper around the new instance.
//* </Description>
//* 
//* <Since version="3.2.1"/>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionNewInstance(SObjFunction func,
					      SContext     context,
					      Object[]     args)
	throws STeaException {

	if ( args.length < 2 ) {
	    throw new SNumArgException(args[0], "className [arg1 [arg2 ...]]");
	}

	String       className  = getStringOrSymbol(args,1);
	Object[]     params     = new Object[args.length - 2];
	Class[]      paramTypes = new Class[args.length - 2];
	Object       result     = null;
	StringBuffer paramsTxt  = new StringBuffer();

	for(int i=2; i<args.length; i++) {
	    params[i-2]     = tea2Java(args[i]);
	    paramTypes[i-2] = params[i-2].getClass();
	    if (i>2) {
		paramsTxt.append(",");
	    }
	    paramsTxt.append(paramTypes[i-2].getName());
	}
	try {
	    Class        cl      = Class.forName(className);
	    Constructor  ctr     = findConstructor(cl, paramTypes);
	    final Object javaObj = ctr.newInstance(params);
	    result = new JavaWrapperObject(new STosClass(), javaObj);
	} catch (ClassNotFoundException e) {
	    throw new SRuntimeException(args[0],
					"could not load class '" + 
					className + "'");
	} catch (NoSuchMethodException e) {
	    throw new SRuntimeException(args[0],
					"could not find constructor for '" + 
					className + "(" + 
					paramsTxt.toString() + ")'");
	} catch (IllegalAccessException e) {
	    throw new SRuntimeException(args[0],
					"cannot access constructor for '" + 
					className + "(" + 
					paramsTxt.toString() + ")'");
	} catch (InstantiationException e) {
	    throw new SRuntimeException(e);
	} catch (InvocationTargetException e) {
	    throw new SRuntimeException(e);
	} catch (IllegalArgumentException e) {
	    throw new SRuntimeException(args[0],
					"problems calling constructor for '" + 
					className + "(" + 
					paramsTxt.toString() + ")' with " +
					params);	    
	}

	return result;
    }





/**************************************************************************
 *
 * Invokes a java method on the given instance from tea arguments
 *
 **************************************************************************/

    private static Object invokeObjectMethod(Object       javaObj,
					     SObjFunction func,
					     SContext     context,
					     Object[]     args) 
	throws STeaException {

	if ( args.length < 2 ) {
	    throw new SNumArgException(args[0], "methodName [arg1 [arg2 ...]]");
	}

	String   methodName = getStringOrSymbol(args,1);
	Class[]  paramTypes = new Class[args.length - 2];
	Object[] mtdArgs    = new Object[args.length - 2];

	// convert value types to java and create methodArgs array
	for(int i=2; i<args.length; i++) {
	    paramTypes[i-2] = tea2Java(args[i]).getClass();
	    mtdArgs[i-2] = args[i];
	}

	Method mtd = null;
	try {
	    mtd = findMethod(javaObj.getClass(), methodName, paramTypes, true);
	} catch (NoSuchMethodException e) {
	    throw new SRuntimeException(args[0],
					"could not find method '" + 
					methodName + "'");
	}

	return invokeMethod(javaObj, mtd, context, mtdArgs);
    }





//* 
//* <TeaFunction name="java-exec-method"
//* 		 arguments="javaClassName methodName [arg1 [arg2 ...]]"
//*              module="tea.java">
//*
//* <Overview>
//* Executes the given static method and returns the result.
//* </Overview>
//*
//* <Parameter name="javaClassName">
//* String or Symbol identifying a Java class.
//* </Parameter>
//*
//* <Parameter name="methodName">
//* String or Symbol identifying a static method in the Java class.
//* </Parameter>
//*
//* <Parameter name="argN">
//* Parameter values for the method.
//* </Parameter>
//*
//* <Description>
//* Executes the static method.
//* </Description>
//* 
//* <Since version="3.2.1"/>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Invokes a java method on the given class from tea arguments
 *
 **************************************************************************/

    public static Object functionExecMethod(SObjFunction func,
					    SContext     context,
					    Object[]     args)
	throws STeaException {

	if ( args.length < 3 ) {
	    throw new SNumArgException(args[0], "className methodName [arg1 [arg2 ...]]");
	}

	String   className  = getStringOrSymbol(args,1);
	String   methodName = getStringOrSymbol(args,2);
	Class[]  paramTypes = new Class[args.length - 3];
	Object[] mtdArgs    = new Object[args.length - 3];

	// convert value types to java
	for(int i=3; i<args.length; i++) {
	    paramTypes[i-3] = tea2Java(args[i]).getClass();
	    mtdArgs[i-3] = args[i];
	}

	Method mtd = null;
	try {
	    Class cl = Class.forName(className);
	    mtd      = findMethod(cl, methodName, paramTypes, true);
	} catch (ClassNotFoundException e) {
	    throw new SRuntimeException(args[0],
					"could not load class '" + 
					className + "'");
	} catch (NoSuchMethodException e) {
	    throw new SRuntimeException(args[0],
					"could not find method '" + 
					methodName + "'");
	}

	return invokeMethod(null, mtd, context, mtdArgs);
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    private static Method findMethod(Class   cl, 
				     String  methodName, 
				     Class[] paramTypes,
				     boolean useVariants) 
	throws NoSuchMethodException {

	Method                mtd           = null;
	NoSuchMethodException lastException = null;

	if (useVariants) {
	    Iterator it = new ClassArrayVariantsIterator(paramTypes);
	    while (mtd == null && it.hasNext()) {
		try {
		    mtd=cl.getMethod(methodName, (Class[])it.next());
		} catch (NoSuchMethodException e) {
		    lastException = e;
		}
	    }
	} else {
	    mtd=cl.getMethod(methodName, paramTypes);
	}

	if (null!=mtd) {
	    return mtd;
	}

	if (null!=lastException) {
	    throw lastException;
	}

	return null;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    private static Constructor findConstructor(Class   cl, 
					       Class[] paramTypes) 
	throws NoSuchMethodException {
	
	Iterator it = new ClassArrayVariantsIterator(paramTypes);
	
	Constructor           ctr           = null;
	NoSuchMethodException lastException = null;
	
	while (ctr == null && it.hasNext()) {
	    try {
		ctr=cl.getConstructor((Class[])it.next());
	    } catch (NoSuchMethodException e) {
		lastException = e;
	    }
	}

	if (null!=ctr) {
	    return ctr;
	}

	if (null!=lastException) {
	    throw lastException;
	}

	return null;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    public static Object java2Tea(Object   obj,
				  SContext context)
	throws STeaException {
	
	if ( null == obj ) {
	    return SObjNull.NULL;
	}
	if ( obj instanceof Date ) {
	    try {
		SDate teaObj = SDate.newInstance(context);
		teaObj.initFromDate((Date)obj);
		return teaObj;
	    } catch (STeaException e) {
		throw new STeaException(e);
	    }
	}
	if ( obj instanceof Float ) {
	    return new Double(((Float)obj).doubleValue());
	}
	if ( obj instanceof Map ) {
	    return javaMap2Tea((Map)obj, context);
	}
	if ( obj instanceof List ) {
	    return javaList2Tea((List)obj, context);
	}
	if ( obj.getClass().isArray() ) {
	    return javaArray2Tea(obj, context);
	}
	if ( obj instanceof Connection ) {
	    return SConnection.newInstance(context, (Connection)obj);
	}
	if ( obj instanceof CallableStatement ) {
	    SCallableStatement teaObj = (SCallableStatement)SCallableStatement.newInstance(context);
	    teaObj.setCallableStatement((CallableStatement) obj);
	    return teaObj;
	}
	if ( obj instanceof PreparedStatement ) {
	    SPreparedStatement teaObj = (SPreparedStatement)SPreparedStatement.newInstance(context);
	    teaObj.setPreparedStatement((PreparedStatement) obj);
	    return teaObj;
	}
	if ( obj instanceof Statement ) {
	    SStatement teaObj = SStatement.newInstance(context);
	    teaObj.setStatement((Statement) obj);
	    return teaObj;
	}
	if ( obj instanceof ResultSet ) {
	    SResultSet teaObj = SResultSet.newInstance(context);
	    try {
		teaObj.setResultSet((ResultSet) obj);
	    } catch (SQLException e) {
		throw new SRuntimeException(e);
	    }
	    return teaObj;
	}
	if ( obj instanceof Double ) {
	    return obj;
	}
	if ( obj instanceof Integer ) {
	    return obj;
	}
	if ( obj instanceof String ) {
	    return obj;
	}
	if ( obj instanceof Boolean ) {
	    return obj;
	}

	// Wrap the object to a tea wrapped obj
	return new JavaWrapperObject(new STosClass(), obj);
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    private static SHashtable javaMap2Tea(Map          map,
					  SContext     context)
	throws STeaException {

	SHashtable teaObj = null;
	Map        teaMap = null;

	try {
	    teaObj = SHashtable.newInstance(context);
	    teaMap = teaObj.getInternalMap();
	} catch (STeaException e) {
	    throw new STeaException(e);
	}
	
	for ( Iterator i=map.entrySet().iterator(); i.hasNext(); ) {
	    Map.Entry entry = (Map.Entry)i.next();
	    Object key      = entry.getKey();
	    Object value    = entry.getValue();
	    Object teaValue = java2Tea(value, context);
	    
	    teaMap.put(key, teaValue);
	}
	

	return teaObj;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/
    
    private static SObjPair javaList2Tea(List     list,
					 SContext context)
	throws STeaException {

	int      size = list.size();
	SObjPair head = SObjPair.emptyList();

	for ( int i=size; (i--)>0; ) {
	    Object value    = list.get(i);
	    Object teaValue = java2Tea(value, context);
	    
	    head = new SObjPair(teaValue, head);
	}

	return head;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/
    
    private static SObjPair javaArray2Tea(Object   anArrayObj,
					  SContext context)
	throws STeaException {

	int      size = Array.getLength(anArrayObj);
	SObjPair head = SObjPair.emptyList();

	for ( int i=size; (i--)>0; ) {
	    Object value    = Array.get(anArrayObj, i);
	    Object teaValue = java2Tea(value, context);
	    
	    head = new SObjPair(teaValue, head);
	}

	return head;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    public static Object tea2Java(Object obj)
	throws STeaException {

	if ( obj == SObjNull.NULL ) {
	    return null;
	}

	if ( obj instanceof String ) {
	    return obj;
	}
	if ( obj instanceof Integer ) {
	    return obj;
	}
	if ( obj instanceof Double ) {
	    return obj;
	}
	if  ( obj instanceof Boolean ) {
	    return obj;
	}
	if  ( obj instanceof SObjSymbol ) {
	    return ((SObjSymbol)obj).getName();
	}
	if ( obj instanceof SObjPair ) {
	    return teaList2List((SObjPair)obj);
	}
	if ( (obj instanceof STosObj) && (obj instanceof JavaWrapperObject) ) {
	    return ((JavaWrapperObject)obj)._javaObj;
	}
	if ( (obj instanceof STosObj)
	     && (((STosObj)obj).part(0) instanceof SHashtable) ) {
	    return teaMap2Map((SHashtable)((STosObj)obj).part(0));
	}
	if ( (obj instanceof STosObj)
	     && (((STosObj)obj).part(0) instanceof SDate) ) {
	    return ((SDate)((STosObj)obj).part(0))._calendar.getTime();
	}
	if ( (obj instanceof STosObj)
	     && (((STosObj)obj).part(0) instanceof SConnection) ) {
	    return ((SConnection)((STosObj)obj)).getInternalConnection();
	}
	if ( (obj instanceof STosObj)
	     && (((STosObj)obj).part(0) instanceof SStatement) ) {
	    return ((SStatement)((STosObj)obj)).getInternalStatement();
	}
	if ( (obj instanceof STosObj)
	     && (((STosObj)obj).part(0) instanceof SResultSet) ) {
	    return ((SResultSet)((STosObj)obj)).getInternalResultSet();
	}
	if ( (obj instanceof STosObj)
	     && (((STosObj)obj).part(0) instanceof SVector) ) {
	    return ((SVector)((STosObj)obj)).getInternalList();
	}

	return obj;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/
    
    private static List teaList2List(SObjPair head)
	throws STeaException {
	
	List list = new ArrayList();
	
	for ( Iterator i=head.iterator(); i.hasNext(); ) {
	    Object teaValue  = i.next();
	    Object iteaValue = tea2Java(teaValue);
	    
	    list.add(iteaValue);
	}
	
	return list;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    private static Map teaMap2Map(SHashtable hashtable)
	throws STeaException {
	
	Map teaMap = hashtable.getInternalMap();
	Map map    = new HashMap();
	
	for ( Iterator i=teaMap.entrySet().iterator(); i.hasNext(); ) {
	    Map.Entry entry   = (Map.Entry)i.next();
	    Object    key     = tea2Java(entry.getKey());
	    Object    wsValue = tea2Java(entry.getValue());
	    
	    map.put(key, wsValue);
	}
	
	return map;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/
    
    private static Class getClassForName (String className) 
	throws ClassNotFoundException {

	// Check primitive types
	Class result = (Class)_primitiveTypes.get(className);
	
	if (null==result) {
	    result = Class.forName(className);
	}

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/
    
    private static String getStringOrSymbol (Object[] args, int index)
	throws STypeException {
	
	if (args[index] instanceof String) {
	    return (String)args[index];
	}
	if (args[index] instanceof SObjSymbol) {
	    return ((SObjSymbol)args[index]).getName();
	}
	throw new STypeException(args[0],
				 "argument " +index+
				 " should be a string or a symbol, " +
				 "not a " + STypes.getTypeName(args[index]));
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    static class JavaWrapperObject 
	extends STosObj {

	private Object _javaObj;

	public JavaWrapperObject (STosClass theClass, Object javaObj) 
	    throws STeaException {
	    
	    super(theClass);

	    _javaObj = javaObj;
	}


	public Object exec(SObjFunction func,
			   SContext     context,
			   Object[]     args)
	    throws STeaException {

	    return invokeObjectMethod(_javaObj, func, context, args);
	}



	public Object getValue(String memberName) 
	    throws NoSuchFieldException,
		   IllegalAccessException,
		   NullPointerException {

	    return getFieldValue(_javaObj.getClass(), _javaObj, memberName);
	}



	public Object setValue(String memberName, Object newValue) 
	    throws NoSuchFieldException,
		   IllegalAccessException,
		   NullPointerException {

	    return setFieldValue(_javaObj.getClass(), _javaObj, 
				 memberName, newValue);
	}
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    static class ClassArrayVariantsIterator 
	implements Iterator {

	private Class[]    _params   = null;
	private Class[]    _current  = null;
	private BigInteger _values   = null;
	private int        _maxValue = 0;

	public ClassArrayVariantsIterator (Class[] params) {

	    if (params.length > 0) {
		_values = new BigInteger(new byte[params.length]);
	    } else {
		_values = null;
	    }
	    _maxValue   = (int)Math.pow(2,params.length);
	    _params     = params;
	    _current    = getCurrentVariant();
	}


	public boolean hasNext() {
	    
	    return _current != null;
	}


	public Object next() {

	    Class[] result = _current;

	    if (_values == null) {
		_current = null;
		return result;
	    }
	    if (_values.intValue() < _maxValue) {
		_values = _values.add(BigInteger.ONE);
		while (_values.intValue() < _maxValue && 
		       null == (_current = getCurrentVariant())) {
		    _values = _values.add(BigInteger.ONE);
		}
	    } else {
		_current = null;
		return null;
	    }

	    return result;
	}


	public void remove() {
	}


	private Class[] getCurrentVariant() {

	    Class[] params = new Class[_params.length];

	    if (_values == null) {
		return params;
	    }
	    if (_values.intValue() == 0) {
		return _params;
	    }
	    
	    for(int i=0; i<_params.length; i++) {
		if (_values.testBit(i)) {
		    Class wrapper = (Class)_primitiveWrappers.get(_params[i]);
		    if (null == wrapper) {
			return null;
		    }
		    params[i] = wrapper;
		} else {
		    params[i] = _params[i];
		}
	    }

	    return params;
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

}
