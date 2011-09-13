/**************************************************************************
 *
 * Copyright (c) 2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import static org.junit.Assert.*;

import java.text.MessageFormat;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SCode;
import com.pdmfc.tea.compiler.SCompiler;
import com.pdmfc.tea.runtime.STeaRuntime;





/**************************************************************************
 *
 * Utility class that encapsulates functionlity used in multiple
 * tests.
 *
 **************************************************************************/

public class TestRuntime
    extends Object {





    private STeaRuntime _teaRuntime = new STeaRuntime();
    private SCompiler   _compiler   = new SCompiler();





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TestRuntime() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void start() {

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

    public Object eval(final String script)
        throws STeaException {

        SCode  code   = _compiler.compile(script);
        Object result = _teaRuntime.execute(code);

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
        } catch (STeaException e) {
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

