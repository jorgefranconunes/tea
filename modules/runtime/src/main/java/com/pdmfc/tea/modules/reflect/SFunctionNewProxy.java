/**************************************************************************
 *
 * Copyright (c) 2011-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.reflect.JavaWrapperObject;
import com.pdmfc.tea.modules.reflect.SReflectUtils;
import com.pdmfc.tea.modules.reflect.STeaJavaTypes;
import com.pdmfc.tea.runtime.SArgs;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SRuntimeException;





//* 
//* <TeaFunction name="java-new-proxy"
//*              arguments="function javaInterface [javaInterface ...]"
//*              module="tea.java">
//*
//* <Overview>
//* Creates a proxy object implementing a set of Java interfaces where
//* all method calls are redirected to a Tea function.
//* </Overview>
//*
//* <Parameter name="function">
//* The Tea function that gets called for each invocation of a method on
//* the returned object.
//* </Parameter>
//*
//* <Parameter name="javaInterface">
//* String or Symbol identifying a Java interface.
//* </Parameter>
//* 
//* <Returns>
//* An object that implements the given Java interfaces.
//* </Returns>
//*
//* <Description>
//* The created proxy object can be passed to Java methods that accept as
//* argument the same type as one of the Java interfaces specified in the
//* arguments. That is because the proxy object actually implements all
//* those Java interfaces.
//* 
//* <p>The given Tea <Arg name="function" /> will be invoked for each method
//* call on the proxy Java object. The first argument will be the method
//* name (as a String) and the remaining arguments will be the same arguments
//* the Java method was invoked with.</p>
//* </Description>
//*
//* <Since version="4.0.0"/>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class SFunctionNewProxy
    extends Object
    implements SObjFunction {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SFunctionNewProxy() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public Object exec(final SObjFunction function,
                       final SContext     context,
                       final Object[]     args)
        throws TeaException {

        String usageMessage = "function javaInterface [javaInterface ...]";
        SArgs.checkAtLeast(args, 3, usageMessage);

        SObjFunction proxyFunction  = SArgs.getFunction(context, args, 1);
        int          interfaceCount = args.length-2;
        Class<?>[]   javaInterfaces = new Class<?>[interfaceCount];

        for ( int argIndex=2, i=0; i<interfaceCount; ++argIndex, ++i ) {
            Class<?> klass = SReflectUtils.getClassForName(args, argIndex);

            javaInterfaces[i] = klass;
        }

        InvocationHandler handler = createHandler(proxyFunction, context);
        Object            proxy   = createProxy(javaInterfaces, handler);
        Object            result  =
            new JavaWrapperObject(proxy);

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private InvocationHandler createHandler(final SObjFunction proxyFunction,
                                            final SContext     context) {

        InvocationHandler result =
            new InvocationHandler() {
                public Object invoke(final Object   proxy,
                                     final Method   method,
                                     final Object[] args) {
                    return
                        invokeProxyMethod(proxyFunction, context, method, args);
                }
                                     
            };

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object invokeProxyMethod(final SObjFunction proxyFunction,
                                            final SContext     context,
                                            final Method       method,
                                            final Object[]     args) {

        Object        result = null;
        TeaException error  = null;

        try {
            result = doInvokeProxyMethod(proxyFunction, context, method, args);
        } catch ( TeaException e ) {
            error = e;
        }

        if ( error != null ) {
            String errorMessage = null;

            if ( error instanceof SRuntimeException ) {
                errorMessage = ((SRuntimeException)error).getFullMessage();
            } else {
                errorMessage = error.getMessage();
            }

            throw new RuntimeException(errorMessage);
        }

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object doInvokeProxyMethod(final SObjFunction proxyFunction,
                                              final SContext     context,
                                              final Method       method,
                                              final Object[]     args)
        throws TeaException {

        int      argCount         = (args==null) ? 0 : args.length;
        int      functionArgCount = argCount + 2;
        Object[] functionArgs     = new Object[functionArgCount];

        // Prepare the function arguments for the Tea function call.
        functionArgs[0] = proxyFunction;
        functionArgs[1] = method.getName();

        if ( args != null ) {
            int functionArgIndex=2;
            for ( Object arg : args ) {
                Object functionArg = STeaJavaTypes.java2Tea(arg, context);
                functionArgs[functionArgIndex] = functionArg;
                ++functionArgIndex;
            }
        }

        Object teaResult =
            proxyFunction.exec(proxyFunction, context, functionArgs);
        Object result    =
            STeaJavaTypes.tea2Java(teaResult);

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private Object createProxy(final Class<?>[]        javaInterfaces,
                               final InvocationHandler handler)
        throws SRuntimeException {

        Object      result      = null;
        ClassLoader classLoader = this.getClass().getClassLoader();

        try {
            result =
                Proxy.newProxyInstance(classLoader, javaInterfaces, handler);
        } catch ( IllegalArgumentException e ) {
            String   msg     = "Failed to create proxy instance - {0}";
            Object[] fmtArgs = { e.getMessage() };
            throw new SRuntimeException(msg, fmtArgs);
        }

        return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

