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
 * 2006/12/24 Created. (jpsl)
 *
 **************************************************************************/

package com.pdmfc.tea.engine;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
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
public class TeaScriptEngineFactoryTest {

    public TeaScriptEngineFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void checkFactory() {
        // create a script engine manager
        ScriptEngineManager m = new ScriptEngineManager();
        assertTrue(m!=null);

        //System.out.println("Listing scripting engines supported:");
        //List<ScriptEngineFactory> sefl=m.getEngineFactories();
        //for(ScriptEngineFactory sef : sefl) {
        //    System.out.println(sef.getEngineName());
        //}

        ScriptEngine e = m.getEngineByName("tea");
        assertTrue(e!=null);

        e = m.getEngineByName("Tea");
        assertTrue(e!=null);
        
        e = m.getEngineByExtension("tea");
        assertTrue(e!=null);

    }

}