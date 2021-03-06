/**************************************************************************
 *
 * Copyright (c) 2005-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaEnvironment;
import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaModule;
import com.pdmfc.tea.TeaRunException;
import com.pdmfc.tea.modules.reflect.SFunctionNewProxy;
import com.pdmfc.tea.modules.reflect.SMethodFinder;
import com.pdmfc.tea.modules.reflect.SReflectUtils;
import com.pdmfc.tea.modules.reflect.STeaJavaTypes;
import com.pdmfc.tea.TeaNumArgException;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaFunctionImplementor;





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
//* No convertion is needed.  Unboxing convertion is performed on
//* method invocation arguments when needed. Examples of Tea value
//* literal/expressions and corresponding Java type:
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
//* Are assignable to java.lang.String.  Examples of Tea value
//* literal/expressions and corresponding Java type:
//*    <Enumeration>
//*    <EnumLabel>"1"</EnumLabel>
//*    <EnumDescription>
//*    java.lang.String
//*    </EnumDescription>
//*    </Enumeration>
//* </EnumDescription>
//*
//* <EnumLabel>Tea symbols (com.pdmfc.tea.TeaSymbol)</EnumLabel>
//* <EnumDescription>
//* Are converted to java.lang.String.  Examples of Tea value
//* literal/expressions and corresponding Java type:
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
//* Are assignable to java.lang.Boolean. Unboxing convertion is
//* performed on method invocation arguments when needed. Examples
//* of Tea value literal/expressions and corresponding Java type:
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
//* <EnumLabel>Tea lists (com.pdmfc.tea.TeaPair)</EnumLabel>
//* <EnumDescription>
//* Are converted to java.util.ArrayList. Each element in the list is
//* recursively converted according to these rules. Examples of Tea
//* value literal/expressions and corresponding Java type:
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
//* Are converted to java.util.HashMap. Each element in the hashtable
//* is recursively converted according to these rules.
//* </EnumDescription>
//*
//* <EnumLabel>TVector (com.pdmfc.tea.runtime.STosObj)</EnumLabel>
//* <EnumDescription>
//* Are assignable to java.util.ArrayList. Each element in the vector
//* IS NOT recursively converted according to these rules.
//* </EnumDescription>
//*
//* <EnumLabel>TConnection, TStatement, TResultSet (com.pdmfc.tea.runtime.STosObj)</EnumLabel>
//* <EnumDescription>
//* Are assignable to java.sql.Connection, java.sql.Statement,
//* java.sql.ResultSet.
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

public final class ModuleReflect
    extends Object
    implements TeaModule {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public ModuleReflect() {

       // Nothing to do.
   }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void init(final TeaEnvironment environment)
        throws TeaException {

        environment.addGlobalVar("java-new-proxy", new SFunctionNewProxy());

        // The other functions provided by this module are implemented
        // as methods of this with class with the TeaFunction
        // annotation.        
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
 * Implements the Tea <code>java-get-value</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("java-get-value")
    public static Object functionGetValue(final TeaFunction func,
                                          final TeaContext  context,
                                          final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args,
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
 * Implements the Tea <code>java-set-value</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("java-set-value")
    public static Object functionSetValue(final TeaFunction func,
                                          final TeaContext  context,
                                          final Object[]    args)
        throws TeaException {

        if ( args.length != 4 ) {
            String usage = "[className|wrapperObj] memberName value";
            throw new TeaNumArgException(args, usage);
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
 * Implements the Tea <code>java-get-method</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("java-get-method")
    public static Object functionGetMethod(final TeaFunction func,
                                           final TeaContext  context,
                                           final Object[]    args)
        throws TeaException {

        if ( args.length < 3 ) {
            String usage = "className methodName [argType1 [argType2 ...]]";
            throw new TeaNumArgException(args, usage);
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
            new TeaFunction() {
                public Object exec(final TeaFunction func,
                                   final TeaContext  context,
                                   final Object[]    args)
                    throws TeaException {

                    if ( args.length != functionArgCount ) {
                        throw new TeaNumArgException(args, usageMessage);
                    }

                    Object[] methodArgs = new Object[args.length - 1];
                    for ( int i=1, count=args.length; i<count; i++ ) {
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
 * Implements the Tea <code>java-new-instance</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("java-new-instance")
    public static Object functionNewInstance(final TeaFunction func,
                                             final TeaContext  context,
                                             final Object[]    args)
        throws TeaException {

        if ( args.length < 2 ) {
            throw new TeaNumArgException(args, "className [arg1 [arg2 ...]]");
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
            throw new TeaRunException(args, msg, klass.getName(), paramsTxt);
        } catch (InstantiationException e) {
            throw new TeaRunException(e);
        } catch (InvocationTargetException e) {
            throw new TeaRunException(e.getCause());
        } catch (IllegalArgumentException e) {
            String paramsTxt = SMethodFinder.buildTypesDescription(paramTypes);
            String msg = "problems calling constructor for {0}({1})";
            throw new TeaRunException(args, msg, klass.getName(), paramsTxt);
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
 * Implements the Tea <code>java-exec-method</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("java-exec-method")
    public static Object functionExecMethod(final TeaFunction func,
                                            final TeaContext  context,
                                            final Object[]    args)
        throws TeaException {

        if ( args.length < 3 ) {
            String usage = "className methodName [arg1 [arg2 ...]]";
            throw new TeaNumArgException(args, usage);
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

