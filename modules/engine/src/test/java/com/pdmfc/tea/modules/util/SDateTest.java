/**************************************************************************
 *
 * Copyright (c) 2010 PDMFC, All Rights Reserved.
 *
 **************************************************************************/
/**************************************************************************
 *
 * $HeadURL$
 * $Id$
 *
 * Revisions:
 *
 * 2010/10/23 Created. (jpsl)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.util;

import com.pdmfc.tea.engine.TeaScriptEngine;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;
import java.util.Date;
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
public class SDateTest {

    ScriptEngine _e;

    public SDateTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        // create a script engine manager
        ScriptEngineManager m = new ScriptEngineManager();
        assertNotNull(m);

        // create engine
        _e = m.getEngineByName("tea");
        assertNotNull(_e);
    }

    @After
    public void tearDown() {
    }


    /**
     * Test TDate.format
     */
    @Test
    public void testTDateFormat() throws ScriptException {
        String dfs = (String)_e.eval("[new TDate] format \"yyyy-MM-dd'T'HH:mm:ss.SSSZ\"");
        assertTrue(dfs.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}.[0-9]{4}$"));
    }

    /**
     * Test TDate.format error
     */
    @Test
    public void testTDateFormatError() throws ScriptException {
        try {
            // Note the missing quotes around the 'T'
            String dfs = (String)_e.eval("[new TDate] format \"yyyy-MM-ddTHH:mm:ss.SSSZ\"");
            fail("No ScriptException thrown on invalid TDate.format argument!");
        } catch (ScriptException e) {
            // The above must throw a ScriptException. If it contains a java stack
            // trace in the message for a java.lang.IllegalArgumentException then
            // bug TSK-PDMFC-TEA-0045 is not yet corrected.
            //System.out.println(TeaScriptEngine.getFullMessage(e));
            assertTrue(!TeaScriptEngine.getFullMessage(e).contains("com.pdmfc.tea."));
        }
    }

}