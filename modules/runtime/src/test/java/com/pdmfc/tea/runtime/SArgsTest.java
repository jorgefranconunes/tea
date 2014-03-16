/**************************************************************************
 *
 * Copyright (c) 2013 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.pdmfc.tea.runtime.SArgs;
import com.pdmfc.tea.runtime.SObjBlock;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.STypeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class SArgsTest
    extends Object {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void testGetXxx(final Object value,
                            final String methodName)
        throws Throwable {

        Class<?>[] paramTypes = {
            Object[].class,
            Integer.TYPE
        };
        Method method = SArgs.class.getMethod(methodName, paramTypes);

        Object[]  args       = { value };
        Object    result     = null;

        try {
            result = method.invoke(null, args, 0);
        } catch ( InvocationTargetException e ) {
            Throwable error = e.getCause();
            throw error;
        }

        assertSame(value, result);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void testGetString(final Object value)
        throws Throwable {
        testGetXxx(value, "getString");
    }


    @Test
    public void getString()
        throws Throwable {

        testGetString("Hello");
        testGetString(null);
    }


    @Test(expected=STypeException.class)
    public void getStringWithError01()
        throws Throwable {

        testGetString(Integer.valueOf(0));
    }


    @Test(expected=STypeException.class)
    public void getStringWithError02()
        throws Throwable {

        testGetString(SObjNull.NULL);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void testGetBlock(final Object value)
        throws Throwable {
        testGetXxx(value, "getBlock");
    }


    @Test
    public void getBlock()
        throws Throwable {

        SObjBlock dummyBlock =
            new SObjBlock() {
                @Override
                public SContext getContext() { return null; }

                @Override
                public Object exec(final SContext context) { return null; }

                @Override
                public Object exec() { return null; }
            };

        testGetBlock(dummyBlock);
        testGetBlock(null);
    }


    @Test(expected=STypeException.class)
    public void getBlockWithError01()
        throws Throwable {

        testGetBlock(Integer.valueOf(0));
    }


    @Test(expected=STypeException.class)
    public void getBlockWithError02()
        throws Throwable {

        testGetBlock(SObjNull.NULL);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void testGetNumber(final Object value)
        throws Throwable {
        testGetXxx(value, "getNumber");
    }


    @Test
    public void getNumber()
        throws Throwable {

        testGetNumber(Integer.valueOf(0));
        testGetNumber(Long.valueOf(0L));
        testGetNumber(Double.valueOf(0.0));
        testGetNumber(null);
    }


    @Test(expected=STypeException.class)
    public void getNumberWithError01()
        throws Throwable {

        testGetNumber("fail");
    }


    @Test(expected=STypeException.class)
    public void getNumberWithError02()
        throws Throwable {

        testGetNumber(SObjNull.NULL);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void testGetPair(final Object value)
        throws Throwable {
        testGetXxx(value, "getPair");
    }


    @Test
    public void getPair()
        throws Throwable {

        SObjPair dummyPair = new SObjPair();

        testGetPair(dummyPair);
        testGetPair(null);
    }


    @Test(expected=STypeException.class)
    public void getPairWithError01()
        throws Throwable {

        testGetPair(Integer.valueOf(0));
    }


    @Test(expected=STypeException.class)
    public void getPairWithError02()
        throws Throwable {

        testGetPair(SObjNull.NULL);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void testGetSymbol(final Object value)
        throws Throwable {
        testGetXxx(value, "getSymbol");
    }


    @Test
    public void getSymbol()
        throws Throwable {

        SObjSymbol dummySymbol = SObjSymbol.addSymbol("dummy");

        testGetSymbol(dummySymbol);
        testGetSymbol(null);
    }


    @Test(expected=STypeException.class)
    public void getSymbolWithError01()
        throws Throwable {

        testGetSymbol(Integer.valueOf(0));
    }


    @Test(expected=STypeException.class)
    public void getSymbolWithError02()
        throws Throwable {

        testGetSymbol(SObjNull.NULL);
    }

}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

