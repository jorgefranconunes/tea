/**************************************************************************
 *
 * Copyright (c) 2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.pdmfc.tea.runtime.STeaRuntime;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public class TeaRuntimeTest
    extends Object {





    private STeaRuntime _runtime = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Before
    public void setUp() {

        _runtime = new STeaRuntime();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @After
    public void tearDown() {

        _runtime = null;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void create() {

        // Everything in this test is done on the setup.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void startAndStop() {

        _runtime.start();
        _runtime.stop();
    }


    @Test(expected=IllegalStateException.class)
    public void startTwice() {

        _runtime.start();
        _runtime.start();
    }


    @Test(expected=IllegalStateException.class)
    public void stopWithoutStart() {

        _runtime.stop();
    }


    @Test(expected=IllegalStateException.class)
    public void stopTwice() {

        _runtime.start();
        _runtime.stop();
        _runtime.stop();
    }


    @Test
    public void endWithoutStart() {

        _runtime.end();
    }

    
    @Test(expected=IllegalStateException.class)
    public void endTwice() {

        _runtime.end();
        _runtime.end();
    }


    @Test(expected=IllegalStateException.class)
    public void endAfterStart() {

        _runtime.start();
        _runtime.end();
    }


    @Test
    public void endAfterStop() {

        _runtime.start();
        _runtime.stop();
        _runtime.end();
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

