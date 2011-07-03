/**************************************************************************
 *
 * Copyright (c) 2005-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.modules.reflect.SMethodFinder;
import com.pdmfc.tea.modules.reflect.SReflectUtils;
import com.pdmfc.tea.modules.reflect.STeaJavaTypes;
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
import com.pdmfc.tea.runtime.SLambdaFunction;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjBlock;
import com.pdmfc.tea.runtime.SObjByteArray;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;





//*
//* <TeaModule name="tea.java">
//* 
//* <Overview>
//* Tea reflection.
//* </Overview>
//*
//* <Description>
//* Tea reflection over Java objects.
//* <P>The functions in this module attempt to ease the use of 
//* a Java code base without having to write Java wrapper classes
//* (wich can be loaded into the Tea runtime using the
//* <FuncRef name="load"/> or <FuncRef name="load-function"/> functions).
//* </P>
//* <P>
//* The functions provided allow for instatiation with
//* constructor invocation, static method invocation, member
//* field get/set access, and instance method invocation.
//* </P>
//* <P>
//* Method invocation provides limited support for method overloading.
//* Method invocation search (as described in the Java Language Specification
//* , Third Edition , section 15.12) is simplified by searching all methods
//* with the same name and number of arguments, 1 - in the class of the
//* object being invoked, then 2 - on the interfaces,
//* and 3 - on the superclass.
//* For the set of method that matches the name and number of arguments,
//* the type of the arguments matches if the Java method argument
//* is assignable (using java.lang.Class.isAssignable()) from the
//* Tea value, or by unboxing to a Java primitive type.
//* (No support is provided for narrowing convertions.)
//* </P>
//* <P>
//* No support is provided for invocation of methods with variable
//* argument lists.
//* </P>
//* <P>
//* Method arguments,
//* return values, and field get/set values are converted from Tea to Java
//* ana Java to Tea using a few simple rules.
//* </P>
//*
//* <Enumeration>
//*
//* <EnumLabel>Tea $null</EnumLabel>
//* <EnumDescription>
//* $null is converted to java's null.
//* </EnumDescription>
//*
//* <EnumLabel>Tea numbers (java.lang.Integer, java.lang.Long,
//* and java.lang.Double) </EnumLabel>
//* <EnumDescription>
//* No convertion is needed.  Unboxing convertion is performed on method invocation arguments when needed. Examples of Tea value literal/expressions and corresponding Java type:
//*    <Enumeration>
//*    <EnumLabel>1</EnumLabel>
//*    <EnumDescription>
//*    java.lang.Integer
//*    </EnumDescription>
//*    <EnumLabel>1L</EnumLabel>
//*    <EnumDescription>
//*    java.lang.Long
//*    </EnumDescription>
//*    <EnumLabel>1.0</EnumLabel>
//*    <EnumDescription>
//*    java.lang.Double
//*    </EnumDescription>
//*    </Enumeration>
//* No support for narrowing convertions (yet).
//* </EnumDescription>
//*
//* <EnumLabel>Tea strings(java.lang.String)</EnumLabel>
//* <EnumDescription>
//* Are assignable to java.lang.String.  Examples of Tea value literal/expressions and corresponding Java type:
//*    <Enumeration>
//*    <EnumLabel>"1"</EnumLabel>
//*    <EnumDescription>
//*    java.lang.String
//*    </EnumDescription>
//*    </Enumeration>
//* </EnumDescription>
//*
//* <EnumLabel>Tea symbols (com.pdmfc.tea.runtime.SObjSymbol)</EnumLabel>
//* <EnumDescription>
//* Are converted to java.lang.String.  Examples of Tea value literal/expressions and corresponding Java type:
//*    <Enumeration>
//*    <EnumLabel>myFunction</EnumLabel>
//*    <EnumDescription>
//*    java.lang.String
//*    </EnumDescription>
//*    </Enumeration>
//* </EnumDescription>
//*
//* <EnumLabel>Tea booleans (java.lang.Boolean)</EnumLabel>
//* <EnumDescription>
//* Are assignable to java.lang.Boolean. Unboxing convertion is performed on method invocation arguments when needed. Examples of Tea value literal/expressions and corresponding Java type:
//*    <Enumeration>
//*    <EnumLabel>$true</EnumLabel>
//*    <EnumDescription>
//*    java.lang.Boolean
//*    </EnumDescription>
//*    <EnumLabel>$false</EnumLabel>
//*    <EnumDescription>
//*    java.lang.Boolean
//*    </EnumDescription>
//*    </Enumeration>
//* </EnumDescription>
//*
//* <EnumLabel>Tea lists (com.pdmfc.tea.runtime.SObjPair)</EnumLabel>
//* <EnumDescription>
//* Are converted to java.util.ArrayList. Each element in the list is recursively converted according to these rules. Examples of Tea value literal/expressions and corresponding Java type:
//*    <Enumeration>
//*    <EnumLabel>( 1 2 "hello" $true 5.0 )</EnumLabel>
//*    <EnumDescription>
//*    <code>java.util.ArrayList&lt;java.lang.Object&gt;</code>
//*    </EnumDescription>
//*    </Enumeration>
//* </EnumDescription>
//*
//* <EnumLabel>TDate (com.pdmfc.tea.runtime.STosObj)</EnumLabel>
//* <EnumDescription>
//* Are assignable to java.util.Date.
//* </EnumDescription>
//*
//* <EnumLabel>THashtable (com.pdmfc.tea.runtime.STosObj)</EnumLabel>
//* <EnumDescription>
//* Are converted to java.util.HashMap. Each element in the hashtable is recursively converted according to these rules.
//* </EnumDescription>
//*
//* <EnumLabel>TVector (com.pdmfc.tea.runtime.STosObj)</EnumLabel>
//* <EnumDescription>
//* Are assignable to java.util.ArrayList. Each element in the vector IS NOT recursively converted according to these rules.
//* </EnumDescription>
//*
//* <EnumLabel>TConnection, TStatement, TResultSet (com.pdmfc.tea.runtime.STosObj)</EnumLabel>
//* <EnumDescription>
//* Are assignable to java.sql.Connection, java.sql.Statement, java.sql.ResultSet.
//* </EnumDescription>
//*
//* </Enumeration>
//*
//* <P>
//* The support for exception handling is limited.
//* All Java Exceptions are converted to Tea runtime errors (as if they
//* were raised using the <FuncRef name="error"/> function) regardless
//* of their original Java Exception class. The only
//* way to catch them is to use the <FuncRef name="catch"/> function,
//* which catches all Tea runtime errors (but does not distinguishes
//* between originating Java Exception classes).
//* </P>
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
    extends Object
    implements SModule {





    private static Map<String,Class<?>> _primitiveTypes =
        new HashMap<String,Class<?>>();





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

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

    public void init(SContext context)
	throws STeaException {

	context.newVar("java-get-value",
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

	context.newVar("java-set-value",
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

	context.newVar("java-get-method",
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

	context.newVar("java-exec-method",
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

	context.newVar("java-new-instance",
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

    public void end() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void start() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void stop() {

        // Nothing to do.
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
//* A String or Symbol identifying a Java class (for access to a
//* static field member), or a wrapperObject created 
//* with the <FuncRef name="java-new-instance"/> function.
//* </Parameter>
//*
//* <Parameter name="memberName">
//* String or Symbol identifying a member in the Java class or object.
//* </Parameter>
//*
//* <Description>
//* Returns the value of the given field member.
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
	String            memberName = SReflectUtils.getStringOrSymbol(args,2);

	Object            result     = null;
	JavaWrapperObject targetObj  = null;

	if (firstArg instanceof JavaWrapperObject) {
	    targetObj = (JavaWrapperObject)firstArg;
	} else {
	    className = SReflectUtils.getStringOrSymbol(args,1);
	}

	try {
	    if ( null == targetObj ) {
		Class cl = Class.forName(className);
		result = SReflectUtils.getFieldValue(cl, null, memberName);
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

	return STeaJavaTypes.java2Tea(result, context);
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
//* A String or Symbol identifying a Java class (for accessing static
//* field members), or a wrapperObject, created 
//* with the <FuncRef name="java-new-instance"/> function.
//* </Parameter>
//*
//* <Parameter name="memberName">
//* String or Symbol identifying a field member in the Java class or object.
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
	String            memberName = SReflectUtils.getStringOrSymbol(args,2);
	Object            value      = args[3];

	Object            result     = null;
	JavaWrapperObject targetObj  = null;

	Object            javaObj    = STeaJavaTypes.tea2Java(value);

	if (firstArg instanceof JavaWrapperObject) {
	    targetObj = (JavaWrapperObject)firstArg;
	} else {
	    className = SReflectUtils.getStringOrSymbol(args,1);
	}

	try {
	    if ( null == targetObj ) {
		Class cl = Class.forName(className);
		result   = SReflectUtils.setFieldValue(cl,
                                                       null,
                                                       memberName,
                                                       javaObj);
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
	} catch (IllegalArgumentException e) {
	    throw new SRuntimeException(args[0],
					e.getMessage());
	} catch (NullPointerException e) {
	    throw new SRuntimeException(args[0],
					"member '" + 
					memberName + "' is not static");
	}

	return STeaJavaTypes.java2Tea(result, context);
    }





//* 
//* <TeaFunction name="java-get-method"
//* 		 arguments="javaClassName methodName [argType1 [argType2 ...]]"
//*              module="tea.java">
//*
//* <Overview>
//* Returns an anonymous function that can be used to wrap Tea calls
//* to the specified static method.
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
//* method (fqcn or primitive type name). Example values are: "int",
//* "long", etc...
//* </Parameter>
//*
//* <Description>
//* Returns a wrapper around the method. The argTypeN type names 
//* help to locate the required method, when default convertion rules
//* are now enough to locate the method using
//* <FuncRef name="java-exec-method"/>.
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

	String   className  = SReflectUtils.getStringOrSymbol(args,1);
	String   methodName = SReflectUtils.getStringOrSymbol(args,2);
	Object   result     = null;
	Class[]  params     = new Class[args.length - 3];
	String   paramClassName = null;

	try {
	    for(int i=3; i<args.length; i++) {
		paramClassName = SReflectUtils.getStringOrSymbol(args,i);
		params[i-3]=getClassForName(paramClassName);
	    }
	} catch (ClassNotFoundException e) {
	    throw new SRuntimeException(args[0],
					"could not load parameter class '" + 
					paramClassName + "'");
	}
	try {
	    Class        cl  = Class.forName(className);
	    final Method mtd =
                SMethodFinder.findMethod(cl, methodName, params, false);

	    result = new SObjFunction() {
		    public Object exec(SObjFunction func,
				       SContext     context,
				       Object[]     args)
			throws STeaException {

			Object[] mtdArgs = new Object[args.length - 1];
			for(int i=1; i<args.length; i++) {
			    mtdArgs[i-1] = args[i];
			}

			return SReflectUtils.invokeMethod(null, 
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

	String       className  = SReflectUtils.getStringOrSymbol(args,1);
	Object[]     params     = new Object[args.length - 2];
	Class[]      paramTypes = new Class[args.length - 2];
	Object       result     = null;
	StringBuilder paramsTxt  = new StringBuilder();

	for(int i=2; i<args.length; i++) {
	    params[i-2]     = STeaJavaTypes.tea2Java(args[i]);
	    paramTypes[i-2] = params[i-2]==null ? null : params[i-2].getClass();
	    if (i>2) {
		paramsTxt.append(",");
	    }
	    paramsTxt.append(params[i-2]==null ? "null" : paramTypes[i-2].getName());
	}
	try {
	    Class        cl      = Class.forName(className);
	    Constructor  ctr     = SMethodFinder.findConstructor(cl,paramTypes);
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
	    throw new SRuntimeException(e.getCause());
	} catch (IllegalArgumentException e) {
	    throw new SRuntimeException(args[0],
					"problems calling constructor for '" + 
					className + "(" + 
					paramsTxt.toString() + ")' with " +
					params);	    
	}

	return result;
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
//* Executes the static method with the supplied arguments.
//* <P>Support for method overloading is limited, as Tea-to-Java argument
//* convertion algorithm only covers a few promotions.
//* For better overloaded method identification, see also <FuncRef
//* name="java-get-method"/>.</P>
//* </Description>
//* 
//* <Returns>
//* The value that the method call returned, converted to Tea.
//* </Returns>
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

	String   className  = SReflectUtils.getStringOrSymbol(args,1);
	String   methodName = SReflectUtils.getStringOrSymbol(args,2);
	Class[]  paramTypes = new Class[args.length - 3];
	Object[] mtdArgs    = new Object[args.length - 3];

	// convert value types to java
	for(int i=3; i<args.length; i++) {
	    mtdArgs[i-3]    = STeaJavaTypes.tea2Java(args[i]);
	    paramTypes[i-3] = mtdArgs[i-3]==null ? null : mtdArgs[i-3].getClass();
	}

	Method mtd = null;
	try {
	    Class cl = Class.forName(className);
	    mtd      =
                SMethodFinder.findMethod(cl, methodName, paramTypes, true);
	} catch (ClassNotFoundException e) {
	    throw new SRuntimeException(args[0],
					"could not load class '" + 
					className + "'");
	} catch (NoSuchMethodException e) {
	    throw new SRuntimeException(args[0],
					"could not find method '" + 
					methodName + "'");
	}

	return SReflectUtils.invokeMethod(null, mtd, context, mtdArgs);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/
    
    private static Class getClassForName (String className) 
	throws ClassNotFoundException {

	// Check primitive types
	Class result = _primitiveTypes.get(className);
	
	if (null==result) {
	    result = Class.forName(className);
	}

	return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

