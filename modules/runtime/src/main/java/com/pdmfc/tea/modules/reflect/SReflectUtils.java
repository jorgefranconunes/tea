/**************************************************************************
 *
 * Copyright (c) 2005-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.reflect.STeaJavaTypes;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaSymbol;
import com.pdmfc.tea.TeaRunException;
import com.pdmfc.tea.TeaTypeException;





/**************************************************************************
 *
 * Utility methods used throught the
 * <code>com.pdmfc.tea.modules.reflect</code> package.
 *
 **************************************************************************/

final class SReflectUtils
    extends Object {





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
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private SReflectUtils() {

        // Nothing to do.
    }




    

/**************************************************************************
 *
 * Retrieves the value of a member in an object or a static member in
 * a Java class.
 *
 **************************************************************************/

    public static Object getFieldValue(final Class<?> klass, 
                                       final Object   obj, 
                                       final String   memberName)
        throws TeaRunException {

        Object result = null;

        try {
            result = doGetFieldValue(klass, obj, memberName);
        } catch ( NoSuchFieldException e ) {
            throw new TeaRunException("could not find member ''{0}''",
                                        memberName);
        } catch ( IllegalAccessException e ) {
            throw new TeaRunException("cannot access member ''{0}''",
                                        memberName);
        } catch ( NullPointerException e ) {
            throw new TeaRunException("member ''{0}''",memberName);
        }
        
        return result;
    }





    

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object doGetFieldValue(final Class<?> klass, 
                                          final Object   obj, 
                                          final String   memberName)
        throws NoSuchFieldException,
               IllegalAccessException {
        
        Field  field  = klass.getField(memberName);
        Object result = field.get(obj);
        
        return result;
    }



    

/**************************************************************************
 *
 * Changes the value of an instance member in an object or static
 * member in a class and returns its previous value.
 *
 **************************************************************************/

    public static Object setFieldValue(final Class<?> klass, 
                                       final Object   obj, 
                                       final String   memberName,
                                       final Object   value)
        throws TeaRunException {

        Object result = null;

        try {
            result = doSetFieldValue(klass, obj, memberName, value);
        } catch ( NoSuchFieldException e ) {
            throw new TeaRunException("could not find member ''{0}''",
                                        memberName);
        } catch ( IllegalAccessException e ) {
            throw new TeaRunException("cannot access member ''{0}''",
                                        memberName);
        } catch ( NullPointerException e ) {
            throw new TeaRunException("member ''{0}'' is not static",
                                        memberName);
        }

        return result;
    }



    

/**************************************************************************
 *
 * Changes the value of an instance member in an object or static
 * member in a class and returns its previous value.
 *
 **************************************************************************/

    private static Object doSetFieldValue(final Class<?> klass, 
                                          final Object   obj, 
                                          final String   memberName,
                                          final Object   value)
        throws NoSuchFieldException,
               IllegalAccessException {
        
        Object result = doGetFieldValue(klass, obj, memberName);
        Field  field  = klass.getField(memberName);

        field.set(obj, value);

        return result;
    }





/**************************************************************************
 *
 * Invokes a java static method from tea arguments.
 *
 * @param methodArgs The arguments the method will be invoked
 * with. These are passed to the method call as is. It is the
 * responsability of the caller to have already converted Tea objects
 * into the corresponding Java objects.
 *
 **************************************************************************/

    public static Object invokeMethod(final Object   javaObj,
                                      final Method   method,
                                      final TeaContext context,
                                      final Object[] methodArgs) 
        throws TeaException {

        Object javaResult = null;

        try {
            javaResult = method.invoke(javaObj, methodArgs);
        } catch ( IllegalAccessException e ) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.close();
            String msg = "cannot access method \"{0}\" - stack trace: {1}";
             throw new TeaRunException(msg, method.getName(), sw.toString());
        } catch ( NullPointerException e ) {
            String msg = "method {0}#{1} is not static";
            throw new TeaRunException(msg,
                                        method.getDeclaringClass().getName(),
                                        method.getName());
        } catch ( IllegalArgumentException e ) {
            String msg = "method {0}#{1} invoked with illegal arguments - {2}";
            throw new TeaRunException(msg,
                                        method.getDeclaringClass().getName(),
                                        method.getName());
        } catch ( InvocationTargetException e ) {
            Throwable cause = e.getCause();
            // Slight hack to accomodate Java code that executed Tea code.
            if ( cause instanceof UndeclaredThrowableException ) {
                cause = cause.getCause();
            }
            if ( cause instanceof TeaException ) {
                throw (TeaException)cause;
            } else {
                throw new TeaRunException(cause);
            }
         }

        Object result = STeaJavaTypes.java2Tea(javaResult, context);

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/
    
    public static String getStringOrSymbol(final Object[] args,
                                           final int      index)
        throws TeaTypeException {
        
        if (args[index] instanceof String) {
            return (String)args[index];
        }
        if (args[index] instanceof TeaSymbol) {
            return ((TeaSymbol)args[index]).getName();
        }

        throw new TeaTypeException(args, index, "string or a symbol");
    }





/**************************************************************************
 *
 * Retrieves a Java class object from one of the arguments passed in
 * the invocation of a Tea function.
 *
 * <p>The argument is supposed to be a Tea symbol or a string. It is
 * also expected to represent the name of an accessible Java
 * class.</p>.
 *
 * @param args The arguments passed to the Tea function.
 *
 * @param index The index of the argument that is suppoed to represent
 * a Java class name.
 *
 * @return The Java class object with the given name.
 *
 * @exception TeaRunException Thrown if there is no Java accessible
 * Java class with the given name.
 *
 **************************************************************************/

    public static Class<?> getClassForName(final Object[] args,
                                           final int      index)
        throws TeaRunException {

        String    className = getStringOrSymbol(args, index);
        Class<?>  result    = null;

        // First look among the primitive types.
        result = _primitiveTypes.get(className);

        if ( result == null ) {
            // And now look for a Java class with the given name.
            try {
                result = Class.forName(className);
            } catch ( ClassNotFoundException e ) {
                String msg = "could not load class \"{0}\"";
                throw new TeaRunException(args, msg, className);    
            } catch ( UnsupportedClassVersionError e ) {
                String msg = "Bad version number in .class file \"{0}\"";
                throw new TeaRunException(args, msg, className);
            }
        }

        return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

