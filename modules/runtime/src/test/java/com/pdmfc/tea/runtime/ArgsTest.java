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

import com.pdmfc.tea.Args;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaBlock;
import com.pdmfc.tea.TeaNull;
import com.pdmfc.tea.TeaPair;
import com.pdmfc.tea.TeaSymbol;
import com.pdmfc.tea.TeaTypeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class ArgsTest
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
        Method method = Args.class.getMethod(methodName, paramTypes);

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


    @Test(expected=TeaTypeException.class)
    public void getStringWithError01()
        throws Throwable {

        testGetString(Integer.valueOf(0));
    }


    @Test(expected=TeaTypeException.class)
    public void getStringWithError02()
        throws Throwable {

        testGetString(TeaNull.NULL);
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

        TeaBlock dummyBlock =
            new TeaBlock() {
                @Override
                public TeaContext getContext() { return null; }

                @Override
                public Object exec(final TeaContext context) { return null; }

                @Override
                public Object exec() { return null; }
            };

        testGetBlock(dummyBlock);
        testGetBlock(null);
    }


    @Test(expected=TeaTypeException.class)
    public void getBlockWithError01()
        throws Throwable {

        testGetBlock(Integer.valueOf(0));
    }


    @Test(expected=TeaTypeException.class)
    public void getBlockWithError02()
        throws Throwable {

        testGetBlock(TeaNull.NULL);
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


    @Test(expected=TeaTypeException.class)
    public void getNumberWithError01()
        throws Throwable {

        testGetNumber("fail");
    }


    @Test(expected=TeaTypeException.class)
    public void getNumberWithError02()
        throws Throwable {

        testGetNumber(TeaNull.NULL);
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

        TeaPair dummyPair = new TeaPair();

        testGetPair(dummyPair);
        testGetPair(null);
    }


    @Test(expected=TeaTypeException.class)
    public void getPairWithError01()
        throws Throwable {

        testGetPair(Integer.valueOf(0));
    }


    @Test(expected=TeaTypeException.class)
    public void getPairWithError02()
        throws Throwable {

        testGetPair(TeaNull.NULL);
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

        TeaSymbol dummySymbol = TeaSymbol.addSymbol("dummy");

        testGetSymbol(dummySymbol);
        testGetSymbol(null);
    }


    @Test(expected=TeaTypeException.class)
    public void getSymbolWithError01()
        throws Throwable {

        testGetSymbol(Integer.valueOf(0));
    }


    @Test(expected=TeaTypeException.class)
    public void getSymbolWithError02()
        throws Throwable {

        testGetSymbol(TeaNull.NULL);
    }

}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

