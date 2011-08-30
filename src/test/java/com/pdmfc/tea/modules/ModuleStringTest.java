/**************************************************************************
 *
 * Copyright (c) 2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.TestRuntime;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class ModuleStringTest
    extends Object {





    private TestRuntime _runtime = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Before
    public void setUp() {

        _runtime = new TestRuntime();
        _runtime.start();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @After
    public void tearDown() {

        _runtime.stop();
        _runtime.end();
        _runtime = null;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void functionUpper() {

        _runtime.evalAssertException("str-upper", 
                                     SNumArgException.class);

        _runtime.evalAssertException("str-upper 1 2",
                                     SNumArgException.class);

        _runtime.evalAssertException("str-upper 1",
                                     STypeException.class);

        _runtime.evalAssertEquals("str-upper \"hello\"",
                                  "HELLO");
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

