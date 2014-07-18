/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaRunException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class SJavaClass
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

    public SJavaClass(final String javaClassName)
        throws TeaRunException {

        this(getJavaClass(javaClassName));
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SJavaClass(final Class javaClass)
        throws TeaRunException {

        _javaClass     = javaClass;
        _javaClassCtor = getJavaClassConstructor(_javaClass);

        if (  !STosObj.class.isAssignableFrom(_javaClass) ) {
            throw new TeaRunException("Java class " + javaClass.getName()
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

    private static Class getJavaClass(final String javaClassName)
        throws TeaRunException {

        Class javaClass = null;

        try {
            javaClass = Class.forName(javaClassName);
        } catch ( ExceptionInInitializerError e1 ) {
            throw new TeaRunException("failed to initialize Java class "
                                        + javaClassName
                                        + " (" + e1.getMessage() + ")");
        } catch ( LinkageError e2 ) {
            throw new TeaRunException("failed to link Java class "
                                        + javaClassName
                                        + " (" + e2.getMessage() + ")");
        } catch ( ClassNotFoundException e3 ) {
            throw new TeaRunException("failed to find Java class "
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

    private static Constructor getJavaClassConstructor(final Class<?> javaClass)
        throws TeaRunException {

        Constructor ctor = null;

        try {
            ctor = javaClass.getConstructor(CTOR_TYPES);
        } catch ( NoSuchMethodException e1 ) {
            String msg = "missing constuctor in class ''{0}''";
            throw new TeaRunException(msg, javaClass.getName());
        } catch ( SecurityException e2 ) {
            String msg = "no access to constuctor of class ''{0}''";
            throw new TeaRunException(msg, javaClass.getName());
        }

        return ctor;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void createMethods(final Class javaClass)
        throws TeaRunException {

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

    private void createDeclaredMethods(final Class javaClass)
        throws TeaRunException {

        Method[] methods = getJavaClassMethods(javaClass);

        for ( int i=methods.length; (i--)>0; ) {
            Method  method     = methods[i];
            String  methodName = method.getName();
            Class[] argTypes   = method.getParameterTypes();
            Class   retType    = method.getReturnType();

            if ( (argTypes.length==3)
                 && (argTypes[0]==TeaFunction.class)
                 && (argTypes[1]==TeaContext.class)
                 && (argTypes[2]==Object[].class)
                 && (retType==Object.class)
                 && !"exec".equals(methodName) ) {
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

    private static Method[] getJavaClassMethods(final Class javaClass)
        throws TeaRunException {

        Method[] methods = null;

        try {
            methods = javaClass.getMethods();
            // We do not call Class.getDeclaredMethods() because it
            // throws SecurityException when running inside
            // JavaWebStart.
        } catch ( SecurityException e ) {
            throw new TeaRunException("no access to methods of Java class "
                                        + javaClass.getName());
        }

        return methods;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setClassName(final Class<?> javaClass) {

        Method getNameMethod = null;
        String tosClassName  = null;

        try {
            getNameMethod = javaClass.getMethod(GET_NAME_METHOD,
                                                GET_NAME_METHOD_TYPES);
        } catch ( NoSuchMethodException e1 ) {
            // Ignore it.
        } catch ( SecurityException e2 ) {
            // Ignore it.
        }

        if ( getNameMethod != null ) {
            try {
                tosClassName =
                    (String)getNameMethod.invoke(null, GET_NAME_METHOD_ARGS);
            } catch ( IllegalAccessException e1 ) {
                // The same as not existing the named method.
            } catch ( IllegalArgumentException e2 ) {
                // The same as not existing the named method.
            } catch ( InvocationTargetException e3 ) {
                // The same as not existing the named method.
            } catch ( NullPointerException e4 ) {
                // The same as not existing the named method.
            } catch ( ClassCastException e5 ) {
                // The same as not existing the named method.
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
        throws TeaException {

        STosObj obj = null;

        try {
            obj = (STosObj)_javaClassCtor.newInstance(new Object[] {this});
        } catch ( InstantiationException e1 ) {
            throw new TeaRunException("failed to instantiate Java class " 
                                        + _javaClass.getName()
                                        + "(" + e1.getMessage() + ")");
        } catch ( IllegalAccessException e2 ) {
            throw new TeaRunException("failed to instantiate Java class " 
                                        + _javaClass.getName()
                                        + "(" + e2.getMessage() + ")");
        } catch ( IllegalArgumentException e3 ) {
            throw new TeaRunException("failed to instantiate Java class " 
                                        + _javaClass.getName()
                                        + "(" + e3.getMessage() + ")");
        } catch ( InvocationTargetException e4 ) {
            Throwable error = e4.getTargetException();
            if ( error instanceof TeaException ) {
                throw (TeaException)error;
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

    private static void internalError(final Throwable error)
        throws TeaRunException {

        throw new TeaRunException("internal error - "
                                    + error.getClass().getName()
                                    + " - " + error.getMessage());
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

