/**************************************************************************
 *
 * Copyright (c) 2005-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.reflect.STeaJavaTypes;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;





/**************************************************************************
 *
 * Utility methods used throught the
 * <code>com.pdmfc.tea.modules.reflect</code> package.
 *
 **************************************************************************/

final class SReflectUtils
    extends Object {





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
 * 
 *
 **************************************************************************/

    public static Object getFieldValue(final Class  aClass, 
                                       final Object aObj, 
                                       final String memberName)
	throws NoSuchFieldException,
	       IllegalAccessException,
	       NullPointerException {
	
	Field  fld    = aClass.getField(memberName);
	Object result = fld.get(aObj);
	
	return result;
    }




    

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static Object setFieldValue(final Class  aClass, 
                                       final Object aObj, 
                                       final String memberName,
                                       final Object value)
	throws NoSuchFieldException,
	       IllegalAccessException,
               IllegalArgumentException,
	       NullPointerException {
	
	Field fld = aClass.getField(memberName);
	fld.set(aObj, value);

	Object result = fld.get(aObj);
	
	return result;
    }





/**************************************************************************
 *
 * Invokes a java static method from tea arguments
 *
 **************************************************************************/

    public static Object invokeMethod(final Object   javaObj,
                                      final Method   mtd,
                                      final SContext context,
                                      final Object[] args) 
	throws STeaException {

	Object  result     = null;
	Class[] paramTypes = mtd.getParameterTypes();

	if ( args.length != paramTypes.length ) {
	    StringBuilder argsTxt = new StringBuilder();
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
	    javaArgs[i] = STeaJavaTypes.tea2Java(args[i]);
	}

	try {
	    result = mtd.invoke(javaObj, javaArgs);
	} catch (IllegalAccessException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.close();
 	    throw new SRuntimeException("cannot access method '" +
  					mtd.getName() + "' - stack trace: " +
                                        sw.toString());
	} catch (NullPointerException e) {
 	    throw new SRuntimeException("method '" +
  					mtd.getName() + "' is not static");
	} catch (InvocationTargetException e) {
 	    throw new SRuntimeException(e.getCause());
 	}

	// convert values back to tea 
	result = STeaJavaTypes.java2Tea(result, context);

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/
    
    public static String getStringOrSymbol(final Object[] args,
                                           final int      index)
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


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

