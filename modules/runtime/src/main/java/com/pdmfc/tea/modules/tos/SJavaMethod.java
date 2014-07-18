/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaRunException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class SJavaMethod
    extends Object
    implements TeaFunction {





    private static final String LINE_SEPARATOR =
        System.getProperty("line.separator");

    private static final Class[] PARAM_TYPES = new Class[] {
        TeaFunction.class, TeaContext.class, Object[].class
    };

    private Method _javaMethod = null;
    private String _errorMsg   = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SJavaMethod(final Class<?> javaClass,
                       final String   javaMethodName) {

        try {
            _javaMethod = javaClass.getMethod(javaMethodName, PARAM_TYPES);
        } catch ( NoSuchMethodException e1 ) {
            _errorMsg = "Java class " + javaClass.getName()
                + " has no " + javaMethodName + " method";
        } catch ( SecurityException e2 ) {
            _errorMsg = "Java class " + javaClass.getName()
                + " has no accessible " + javaMethodName + " method";
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SJavaMethod(final Method javaMethod) {

        _javaMethod = javaMethod;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object exec(final TeaFunction obj,
                       final TeaContext     context,
                       final Object[]    args)
        throws TeaException {

        if ( _javaMethod == null ) {
            throw new TeaRunException(_errorMsg);
        }

        Object   target     = ((STosObj)obj).part(0);
        Object[] targetArgs = new Object[] { obj, context, args };

        try {
            return _javaMethod.invoke(target, targetArgs);
        } catch ( IllegalAccessException e1 ) {
            internalError(e1);
        } catch ( IllegalArgumentException e2 ) {
            internalError(e2);
        } catch ( InvocationTargetException e3 ) {
            Throwable error = e3.getTargetException();
            if ( error instanceof TeaException ) {
                throw (TeaException)error;
            } else {
                internalError(error);
            }
        }

        // Never reached.
        return null;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void internalError(final Throwable error)
        throws TeaRunException {

        TeaRunException rtError  =
            new TeaRunException("internal error - {0} - {1}",
                                  new Object[] { error.getClass().getName(),
                                                 error.getMessage() });
        StringWriter    buffer     = new StringWriter();
        PrintWriter     bufPrinter = new PrintWriter(buffer);
        StringTokenizer st         = null;

        error.printStackTrace(bufPrinter);
        st = new StringTokenizer(buffer.toString(), LINE_SEPARATOR);

        while ( st.hasMoreTokens() ) {
            rtError.addMessage(st.nextToken());
        }

        throw rtError;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

