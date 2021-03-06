/**************************************************************************
 *
 * Copyright (c) 2005-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.reflect;

import java.lang.reflect.Method;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.reflect.SMethodFinder;
import com.pdmfc.tea.modules.reflect.SReflectUtils;
import com.pdmfc.tea.modules.reflect.STeaJavaTypes;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaNumArgException;
import com.pdmfc.tea.TeaRunException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

final class JavaWrapperObject
    extends Object
    implements TeaFunction {





    private Object   _javaObject     = null;
    private Class<?> _javaObjectType = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public JavaWrapperObject(final Object javaObject) {

        _javaObject     = javaObject;
        _javaObjectType = javaObject.getClass();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object getWrappedObject() {

        return _javaObject;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Override
    public Object exec(final TeaFunction func,
                       final TeaContext     context,
                       final Object[]    args)
        throws TeaException {

        if ( args.length < 2 ) {
            throw new TeaNumArgException(args, "methodName [arg1 [arg2 ...]]");
        }

        String   methodName = SReflectUtils.getStringOrSymbol(args,1);
        Class[]  paramTypes = new Class[args.length - 2];
        Object[] methodArgs = new Object[args.length - 2];

        // Convert value types to java and create methodArgs array
        for ( int i=2; i<args.length; i++ ) {
            Object javaArg = STeaJavaTypes.tea2Java(args[i]);
            paramTypes[i-2] = (javaArg==null) ? null : javaArg.getClass();
            methodArgs[i-2] = javaArg;
        }

        Method method =
            SMethodFinder.findMethod(_javaObjectType,
                                     methodName, 
                                     paramTypes,
                                     true);
        Object result =
            SReflectUtils.invokeMethod(_javaObject,
                                       method,
                                       context,
                                       methodArgs);

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object getValue(final String memberName) 
        throws TeaRunException {

        Object result =
            SReflectUtils.getFieldValue(_javaObjectType, 
                                        _javaObject,
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
        throws TeaRunException {

        Object result =
            SReflectUtils.setFieldValue(_javaObjectType,
                                        _javaObject, 
                                        memberName,
                                        newValue);

        return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

