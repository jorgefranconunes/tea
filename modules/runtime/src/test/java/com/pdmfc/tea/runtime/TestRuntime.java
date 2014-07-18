/**************************************************************************
 *
 * Copyright (c) 2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.text.MessageFormat;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaRuntime;
import com.pdmfc.tea.TeaRuntimeConfig;
import com.pdmfc.tea.TeaRuntimeFactory;
import com.pdmfc.tea.TeaScript;





/**************************************************************************
 *
 * Utility class that encapsulates functionlity used in multiple
 * tests.
 *
 **************************************************************************/

public class TestRuntime
    extends Object {





    private TeaRuntime _teaRuntime = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TestRuntime() {

        TeaRuntimeFactory factory = new TeaRuntimeFactory();
        TeaRuntimeConfig config = TeaRuntimeConfig.Builder.start().build();

        _teaRuntime = factory.newTeaRuntime(config);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void start()
        throws TeaException {

        _teaRuntime.start();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void stop() {

        _teaRuntime.stop();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void end() {

        _teaRuntime.end();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object eval(final String scriptString)
        throws TeaException {

        TeaScript script = compileFromString(scriptString);
        Object    result = script.execute();

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private TeaScript compileFromString(final String script)
        throws TeaException {

        TeaScript result = null;

        try (
            Reader reader = new StringReader(script)
        ) {
            result = _teaRuntime.compile(reader, null);
        } catch ( IOException e ) {
            // This should never happen...
            throw new IllegalStateException(e);
        }

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void evalAssertException(final String   script,
                                    final Class<?> expectedErrorClass) {

        Throwable error = null;

        try {
            eval(script);
        } catch (Throwable e) {
            error = e;
        }

        if ( error == null ) {
            failWithMsg("Expected exception {0} and no exception was thrown",
                        expectedErrorClass.getName());
        }

        if ( !expectedErrorClass.isInstance(error) ) {
            failWithMsg("Expected exception {0} but got {1}",
                        expectedErrorClass.getName(),
                        error.getClass().getName());
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void evalAssertEquals(final String script,
                                 final Object expectedResult) {

        Object result = null;

        try {
            result = eval(script);
        } catch (TeaException e) {
            failWithMsg("Unexpected Tea exception - {0} - {1}",
                        e.getClass().getName(),
                        e.getMessage());
        }

        assertEquals(expectedResult, result);
    }





/**************************************************************************
 *
 * Throws a JUnit fail exception with a message formatted from the
 * given arguments.
 *
 **************************************************************************/

    private void failWithMsg(final String    fmt,
                             final Object... fmtArgs) {

        String msg =
            (fmtArgs.length>0)
            ? MessageFormat.format(fmt, fmtArgs)
            : fmt;

        fail(msg);
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

