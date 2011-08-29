/**************************************************************************
 *
 * Copyright (c) 2005-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.modules.reflect.SFunctionNewProxy;
import com.pdmfc.tea.modules.reflect.SMethodFinder;
import com.pdmfc.tea.modules.reflect.SReflectUtils;
import com.pdmfc.tea.modules.reflect.STeaJavaTypes;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.util.SDate;
import com.pdmfc.tea.modules.util.SHashtable;
import com.pdmfc.tea.modules.util.SVector;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SRuntimeException;





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





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SModuleReflect() {
   }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void init(final SContext context)
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

        context.newVar("java-new-proxy", new SFunctionNewProxy());
   }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void end() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void start() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void stop() {

        // Nothing to do.
    }





//* 
//* <TeaFunction name="java-get-value"
//*                  arguments="[javaClassName|wrapperObject] memberName"
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

    private static Object functionGetValue(final SObjFunction func,
                                           final SContext     context,
                                           final Object[]     args)
        throws STeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args,
                                       "[className|wrapperObj] memberName");
        }

        Object            firstArg    = args[1];
        String            memberName  = SReflectUtils.getStringOrSymbol(args,2);
        Object            memberValue = null;
        JavaWrapperObject targetObj   = null;
        Class<?>          klass       = null;

        if ( firstArg instanceof JavaWrapperObject ) {
            targetObj = (JavaWrapperObject)firstArg;
        } else {
            klass = SReflectUtils.getClassForName(args, 1);
        }

        if ( null == targetObj ) {
            memberValue = SReflectUtils.getFieldValue(klass, null, memberName);
        } else {
            memberValue = targetObj.getValue(memberName);
        }

        Object result = STeaJavaTypes.java2Tea(memberValue, context);

        return result;
    }





//* 
//* <TeaFunction name="java-set-value"
//*                  arguments="[javaClassName|wrapperObject] memberName value"
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

    private static Object functionSetValue(final SObjFunction func,
                                           final SContext     context,
                                           final Object[]     args)
        throws STeaException {

        if ( args.length != 4 ) {
            throw new SNumArgException(args,
                                       "[className|wrapperObj] memberName value");
        }

        Object            firstArg   = args[1];
        String            memberName = SReflectUtils.getStringOrSymbol(args,2);
        Object            value      = args[3];

        Object            oldValue   = null;
        JavaWrapperObject targetObj  = null;
        Class<?>          klass      = null;

        Object            javaValue    = STeaJavaTypes.tea2Java(value);

        if ( firstArg instanceof JavaWrapperObject ) {
            targetObj = (JavaWrapperObject)firstArg;
        } else {
            klass = SReflectUtils.getClassForName(args, 1);
        }

        if ( null == targetObj ) {
            oldValue =
                SReflectUtils.setFieldValue(klass, null, memberName, javaValue);
        } else {
            oldValue = targetObj.setValue(memberName, javaValue);
        }

        Object result = STeaJavaTypes.java2Tea(oldValue, context);

        return result;
    }





//* 
//* <TeaFunction name="java-get-method"
//*                  arguments="javaClassName methodName [argType1 [argType2 ...]]"
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

    private static Object functionGetMethod(final SObjFunction func,
                                            final SContext     context,
                                            final Object[]     args)
        throws STeaException {

        if ( args.length < 3 ) {
            throw new SNumArgException(args,
                                       "className methodName [argType1 [argType2 ...]]");
        }

        Class<?>   klass      = SReflectUtils.getClassForName(args, 1);
        String     methodName = SReflectUtils.getStringOrSymbol(args,2);
        Class<?>[] paramTypes = new Class[args.length - 3];

        for ( int i=3; i<args.length; i++ ) {
            paramTypes[i-3] = SReflectUtils.getClassForName(args, i);
        }

        final Method method           =
            SMethodFinder.findMethod(klass, methodName, paramTypes, false);
        final String usageMessage     = buildUsageMessage(paramTypes);
        final int    functionArgCount = paramTypes.length + 1;

        Object result =
            new SObjFunction() {
                public Object exec(SObjFunction func,
                                   SContext     context,
                                   Object[]     args)
                    throws STeaException {

                    if ( args.length != functionArgCount ) {
                        throw new SNumArgException(args, usageMessage);
                    }

                    Object[] methodArgs = new Object[args.length - 1];
                    for( int i=1, count=args.length; i<count; i++ ) {
                        methodArgs[i-1] = STeaJavaTypes.tea2Java(args[i]);
                    }

                    return SReflectUtils.invokeMethod(null, 
                                                      method,
                                                      context,
                                                      methodArgs);
                }
            };

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/
    
    private static String buildUsageMessage(final Class<?>[] paramTypes) {

        StringBuilder builder = new StringBuilder();

        for ( int i=0, count=paramTypes.length; i<count; i++) {
                if ( i > 0 ) {
                    builder.append(" ");
                }
                builder.append(paramTypes[i].getName());
        }

        String usageMessage = builder.toString();
    
        return usageMessage;
    }





//* 
//* <TeaFunction name="java-new-instance"
//*                  arguments="javaClassName [arg1 [arg2 ...]]"
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

   private static Object functionNewInstance(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
        throws STeaException {

        if ( args.length < 2 ) {
            throw new SNumArgException(args, "className [arg1 [arg2 ...]]");
        }

        Class<?>   klass      = SReflectUtils.getClassForName(args, 1);
        Object[]   params     = new Object[args.length - 2];
        Class<?>[] paramTypes = new Class<?>[args.length - 2];

        for ( int i=2; i<args.length; i++ ) {
            Object constructorArg = STeaJavaTypes.tea2Java(args[i]);

            params[i-2]     = constructorArg;
            paramTypes[i-2] =
                (constructorArg==null) ? null : constructorArg.getClass();
        }

        Constructor<?>  constructor =
            SMethodFinder.findConstructor(klass, paramTypes);
        Object          javaResult  = null;

        try {            
            javaResult = constructor.newInstance(params);
        } catch (IllegalAccessException e) {
            String paramsTxt = SMethodFinder.buildTypesDescription(paramTypes);
            String msg = "cannot access constructor for {0}({1})";
            throw new SRuntimeException(args, msg, klass.getName(), paramsTxt);
        } catch (InstantiationException e) {
            throw new SRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new SRuntimeException(e.getCause());
        } catch (IllegalArgumentException e) {
            String paramsTxt = SMethodFinder.buildTypesDescription(paramTypes);
            String msg = "problems calling constructor for {0}({1})";
            throw new SRuntimeException(args, msg, klass.getName(), paramsTxt);
        }

        Object result = STeaJavaTypes.java2Tea(javaResult, context);

        return result;
    }





//* 
//* <TeaFunction name="java-exec-method"
//*                  arguments="javaClassName methodName [arg1 [arg2 ...]]"
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

    public static Object functionExecMethod(final SObjFunction func,
                                            final SContext     context,
                                            final Object[]     args)
        throws STeaException {

        if ( args.length < 3 ) {
            throw new SNumArgException(args,
                                       "className methodName [arg1 [arg2 ...]]");
        }

        Class<?>   klass      = SReflectUtils.getClassForName(args, 1);
        String     methodName = SReflectUtils.getStringOrSymbol(args,2);
        Class<?>[] paramTypes = new Class[args.length - 3];
        Object[]   methodArgs = new Object[args.length - 3];

        // Convert value types to java.
        for ( int i=3, count=args.length; i<count; i++ ) {
            Object methodArg = STeaJavaTypes.tea2Java(args[i]); 

            methodArgs[i-3] = methodArg;
            paramTypes[i-3] = (methodArg==null) ? null : methodArg.getClass();
        }

        Method method =
            SMethodFinder.findMethod(klass, methodName, paramTypes, true);

        Object result =
            SReflectUtils.invokeMethod(null, method, context, methodArgs);

        return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

