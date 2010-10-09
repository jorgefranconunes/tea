/**************************************************************************
 *
 * Copyright (c) 2001-2010 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public class SJavaMethod
    extends Object
    implements SObjFunction {





    private static final String LINE_SEPARATOR =
        System.getProperty("line.separator");

    private static final Class[] PARAM_TYPES = new Class[] {
        SObjFunction.class, SContext.class, Object[].class
    };

    private Method _javaMethod = null;
    private String _errorMsg   = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SJavaMethod(Class<?> javaClass,
                       String   javaMethodName) {

        try {
            _javaMethod = javaClass.getMethod(javaMethodName, PARAM_TYPES);
        } catch (NoSuchMethodException e1) {
            _errorMsg = "Java class " + javaClass.getName() + 
                " has no " + javaMethodName + " method";
        } catch (SecurityException e2) {
            _errorMsg = "Java class " + javaClass.getName() + 
                " has no accessible " + javaMethodName + " method";
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SJavaMethod(Method javaMethod) {

        _javaMethod = javaMethod;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object exec(SObjFunction obj,
                       SContext     context,
                       Object[]     args)
        throws STeaException {

        if ( _javaMethod == null ) {
            throw new SRuntimeException(_errorMsg);
        }

        Object   target     = ((STosObj)obj).part(0);
        Object[] targetArgs = new Object[] { obj, context, args };

        try {
            return _javaMethod.invoke(target, targetArgs);
        } catch (IllegalAccessException e1) {
            internalError(e1);
        } catch (IllegalArgumentException e2) {
            internalError(e2);
        } catch (InvocationTargetException e3) {
            Throwable error = e3.getTargetException();
            if ( error instanceof STeaException ) {
                throw (STeaException)error;
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

    private void internalError(Throwable error)
        throws SRuntimeException {

        SRuntimeException rtError  =
            new SRuntimeException("internal error - {0} - {1}",
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

