/**************************************************************************
 *
 * Copyright (c) 2002 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: STosJavaMethod.java,v 1.2 2002/09/17 16:35:27 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/06/24
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.tos;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.tos.STosClass;
import com.pdmfc.tea.tos.STosMethod;





/**************************************************************************
 *
 * A TOS method implemented as a method of a Java class.
 *
 **************************************************************************/

class STosJavaMethod
    extends Object
    implements STosMethod {





    private static final String LINE_SEPARATOR =
	System.getProperty("line.separator");

    private STosClass _declaringClass = null;
    private Method    _javaMethod     = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public STosJavaMethod(STosClass declaringClass,
			  Method    javaMethod) {

	_declaringClass = declaringClass;
	_javaMethod     = javaMethod;
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

	Object[] targetArgs = new Object[] { obj, context, args };
	Object   result     = null;

	try {
	    result = _javaMethod.invoke(obj, targetArgs);
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

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void internalError(Throwable error)
	throws SRuntimeException {

	SRuntimeException rtError  =
	    new SRuntimeException("internal error calling {0} {1} method - {2} - {3}",
				  new Object[] { _declaringClass.getName(),
						 _javaMethod.getName(),
						 error.getClass().getName(),
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

