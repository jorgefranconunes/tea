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

import com.pdmfc.tea.runtime.SObjFunction;
import java.io.IOException;
import java.io.InputStream;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
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
public class TestTeaScriptEngine {

    private ScriptEngine _e = null;
    private InputStream _is = null;

    public TestTeaScriptEngine() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws IOException {
        // create a script engine manager
        ScriptEngineManager m = new ScriptEngineManager();
        assertNotNull(m);

        // create engine
        _e = m.getEngineByName("tea");
        assertNotNull(m);

        // create a temporary file
        _is = TestTeaScriptEngine.class.getResourceAsStream("TestTeaScriptEngineScript1.tea");
        assertNotNull(_is);
    }

    @After
    public void tearDown() throws IOException {
        _is.close();
    }

    @Test
    public void checkEvalString() throws ScriptException {
        _e.eval("global v 1");
    }

    @Test
    public void checkEvalStringImportTdbc() throws ScriptException {

        SObjFunction r = (SObjFunction) _e.get("tdbc-connection");
        assertNull(r);

        _e.eval("import \"tdbc/tdbc.tea\"");

        r = (SObjFunction) _e.get("tdbc-connection");
        assertNotNull(r);
    }

    @Test
    public void checkEvalFile() throws ScriptException, IOException {
        java.io.InputStreamReader ir = new java.io.InputStreamReader(_is);
        _e.eval(ir);
        ir.close();
    }


    @Test
    public void checkInvokableFunction() throws ScriptException, NoSuchMethodException {
        String s = "A test string!";

        // define a function that sets/defines a global variable.
        _e.eval("global fhello ( arg ) { global Vfhello $arg }");        // javax.script.Invocable is an optional interface.

        // Check whether your script engine implements or not!
        // Note that the JavaScript engine implements Invocable interface.
        Invocable inv = (Invocable) _e;

        // invoke the global function named "fhello"
        inv.invokeFunction("fhello", s );

        String r = (String)_e.get("Vfhello");

        assertEquals(r, s);
    }

    @Test
    public void checkInvokableMethod() throws ScriptException, NoSuchMethodException {
        String s = "A test string!";

        // define a function that sets/defines a global variable.
        _e.eval("class Test ( ) ; method Test hello ( arg ) { echo \"hello \" $arg } ; global obj [new Test]");        // javax.script.Invocable is an optional interface.

        // Check whether your script engine implements or not!
        // Note that the JavaScript engine implements Invocable interface.
        Invocable inv = (Invocable) _e;

        // invoke the global function named "fhello"
        // get script object on which we want to call the method
        Object obj = _e.get("obj");

        // invoke the method named "hello" on the script object "obj"
        inv.invokeMethod(obj, "hello", "Script Method !!" );

//        String r = (String)_e.get("Vfhello");

//        assertEquals(r, s);
    }

    @Test
    public void checkVars() throws ScriptException {
        String s = "A test string!";

        // expose File object as variable to script
        _e.put("aString", s);

        // evaluate a script string. The script accesses "file"
        // variable and calls method on it
        _e.eval("global aVarsResult $aString");

        String r = (String)_e.get("aVarsResult");
        
        assertEquals(s, r);
    }

    @Test
    public void printProperties() {
        // get factory
        ScriptEngineFactory f = _e.getFactory();
        
        String propKeys[] = {
            ScriptEngine.ENGINE, 
            ScriptEngine.ENGINE_VERSION,
            ScriptEngine.NAME,
            ScriptEngine.LANGUAGE,
            ScriptEngine.LANGUAGE_VERSION,
            "THREADING"
        };
        
        System.out.println("TeaScriptEngine properties:");
        for(int i=0; i<propKeys.length; i++) {
            System.out.println(propKeys[i]+"="+f.getParameter(propKeys[i]));
        }
    }

}