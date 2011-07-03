/**************************************************************************
 *
 * Copyright (c) 2005-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.reflect;

import java.lang.reflect.Method;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.reflect.SMethodFinder;
import com.pdmfc.tea.modules.reflect.SReflectUtils;
import com.pdmfc.tea.modules.reflect.STeaJavaTypes;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

final class JavaWrapperObject 
    extends STosObj {





    private Object _javaObj = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public JavaWrapperObject (final STosClass theClass,
                              final Object javaObj) 
        throws STeaException {
	    
        super(theClass);

        _javaObj = javaObj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object getWrappedObject() {

        return _javaObj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Override
    public Object exec(final SObjFunction func,
                       final SContext     context,
                       final Object[]     args)
        throws STeaException {

        Object result = invokeObjectMethod(_javaObj, func, context, args);

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object getValue(final String memberName) 
        throws NoSuchFieldException,
               IllegalAccessException,
               NullPointerException {

        Object result = SReflectUtils.getFieldValue(_javaObj.getClass(),
                                                    _javaObj,
                                                    memberName);

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object setValue(final String memberName,
                           final Object newValue) 
        throws NoSuchFieldException,
               IllegalAccessException,
               NullPointerException {

        Object result = SReflectUtils.setFieldValue(_javaObj.getClass(),
                                                    _javaObj, 
                                                    memberName,
                                                    newValue);

        return result;
    }





/**************************************************************************
 *
 * Invokes a java method on the given instance from tea arguments
 *
 **************************************************************************/

    private Object invokeObjectMethod(final Object       javaObj,
                                      final SObjFunction func,
                                      final SContext     context,
                                      final Object[]     args) 
	throws STeaException {

	if ( args.length < 2 ) {
	    throw new SNumArgException(args[0],
                                       "methodName [arg1 [arg2 ...]]");
	}

	String   methodName = SReflectUtils.getStringOrSymbol(args,1);
	Class[]  paramTypes = new Class[args.length - 2];
	Object[] mtdArgs    = new Object[args.length - 2];

	// Convert value types to java and create methodArgs array
	for ( int i=2; i<args.length; i++ ) {
            Object o = STeaJavaTypes.tea2Java(args[i]);
	    paramTypes[i-2] = o==null ? null : o.getClass();
	    mtdArgs[i-2] = args[i];
	}

	Method mtd = null;
	try {
	    mtd = SMethodFinder.findMethod(javaObj.getClass(),
                                           methodName,
                                           paramTypes,
                                           true);
	} catch (NoSuchMethodException e) {
	    throw new SRuntimeException(args[0],
					"could not find method '" + 
					methodName + "'");
	}

	Object result =
            SReflectUtils.invokeMethod(javaObj, mtd, context, mtdArgs);

        return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

