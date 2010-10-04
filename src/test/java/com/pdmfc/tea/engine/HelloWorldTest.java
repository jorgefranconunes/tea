/**************************************************************************
 *
 * Copyright (c) 2010 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $HeadURL$
 * $Id$
 *
 * Revisions:
 *
 * 2010/09/23 Created. (jpsl)
 *
 **************************************************************************/


package com.pdmfc.tea.engine;

import java.io.ByteArrayOutputStream;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jpsl
 */
public class HelloWorldTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    public HelloWorldTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        //System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        //System.setOut(null);
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void hello() throws ScriptException {
        
        // create a script engine manager
        ScriptEngineManager m = new ScriptEngineManager();
        assertTrue(m!=null);

        // create engine
        ScriptEngine engine = m.getEngineByName("tea");
        assertTrue(engine!=null);
        
        // evaluate Tea code from string.
        engine.eval("echo \"Tea says Hello Java Standard World!\"");
    }

}