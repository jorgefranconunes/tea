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
 * 2006/12/24 Created. (jpsl)
 *
 **************************************************************************/
package com.pdmfc.tea.engine;

import com.pdmfc.tea.TeaConfigInfo;
import com.pdmfc.tea.runtime.TeaFunction;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
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
public class TeaScriptEngineTest {

    private ScriptEngine _e = null;

    public TeaScriptEngineTest() {
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
        assertNotNull(_e);
    }

    @After
    public void tearDown() throws IOException {
    }

    
    
    @Test
    public void checkEvalString() throws ScriptException {
        _e.eval("global v 1");
    }
    
    @Test
    public void checkEvalStringError() throws ScriptException {
        try {
            _e.eval(_e.getFactory().getProgram(
                    "define f() {",
                    "  + 1 2",
                    "  / 1 0",
                    "  == 1 2",
                    "}",
                    "",
                    "f"
                    ));
            fail("The above code should have thrown a ScriptException on the / 1 0 line.");
        } catch (ScriptException e) {
            String teaMessage = TeaScriptEngine.getFullMessage(e);
            System.out.println(teaMessage);
            assertTrue(teaMessage.toLowerCase().contains("arithmetic exception"));
            assertTrue(teaMessage.toLowerCase().contains("on line 3"));
            assertTrue(teaMessage.toLowerCase().contains("on line 7"));
        }
    }

    @Test
    public void checkEvalStringImportTdbc() throws ScriptException {

        _e.put("tdbc-connection", null);
        TeaFunction r = (TeaFunction) _e.get("tdbc-connection");
        assertNull(r);

        _e.eval("import \"tdbc/tdbc.tea\"");

        r = (TeaFunction) _e.get("tdbc-connection");
        assertNotNull(r);


    }

    @Test
    public void checkEvalStringImportResource() throws ScriptException {

        assertNull(_e.get("A_GLOBAL"));
        _e.put("A_GLOBAL", null);
        URL r = this.getClass().getResource("TeaScriptEngineTestCheckEvalFile.tea");
        assertEquals("file", r.getProtocol());
        File f = new File(r.getFile());
        assertTrue(f.isFile());
        assertTrue(f.canRead());
        _e.put(TeaScriptEngine.KEY_LIBRARY, f.getParent());
        _e.eval("import \"" + f.getName() + "\"");
        assertEquals(2, _e.get("A_GLOBAL"));
        assertEquals(f.getParent(), _e.get(TeaScriptEngine.KEY_LIBRARY));

        // the default build-in paths should not be overwritten
        _e.put("tdbc-connection", null);
        TeaFunction of = (TeaFunction) _e.get("tdbc-connection");
        assertNull(of);

        _e.eval("import \"tdbc/tdbc.tea\"");

        of = (TeaFunction) _e.get("tdbc-connection");
        assertNotNull(of);
    }

    @Test
    public void checkEncoding() throws ScriptException {

        // This test only works if the resource import also works

        assertNull(_e.get("A_GLOBAL"));
        _e.put("A_GLOBAL", null);
        URL r = this.getClass().getResource("TeaScriptEngineTestCheckEncoding88591.tea");
        assertEquals("file", r.getProtocol());
        File f = new File(r.getFile());
        assertTrue(f.isFile());
        assertTrue(f.canRead());
        _e.put(TeaScriptEngine.KEY_LIBRARY, f.getParent());
        _e.put(TeaScriptEngine.KEY_ENCODING, "ISO-8859-1");
        _e.eval("import \"" + f.getName() + "\"");
        String s = (String)_e.get("A_GLOBAL");
        assertNotNull(s);
        assertEquals(225, s.charAt(2)); // a acute

        // change the context, to be able to set a new encoding
        _e.setContext(new SimpleScriptContext());
        _e.put("A_GLOBAL", null);
        r = this.getClass().getResource("TeaScriptEngineTestCheckEncodingUtf8.tea");
        assertEquals("file", r.getProtocol());
        f = new File(r.getFile());
        assertTrue(f.isFile());
        assertTrue(f.canRead());
        _e.put(TeaScriptEngine.KEY_LIBRARY, f.getParent());
        _e.put(TeaScriptEngine.KEY_ENCODING, "UTF-8");
        _e.eval("import \"" + f.getName() + "\"");
        s = (String)_e.get("A_GLOBAL");
        assertNotNull(s);
        assertEquals(225, s.charAt(2)); // a acute
    }

    @Test
    public void checkTosDate() throws ScriptException, InterruptedException {
        _e.put("A_RESULT", null);
        _e.eval("set! A_RESULT [new TDate]");
        Object o = _e.get("A_RESULT");
        assertSame(java.util.Date.class, o.getClass());
        assertTrue((Boolean) _e.eval(">= [[new TDate] compare $A_RESULT] 0"));
        //System.out.println("A_RESULT=" + _e.get("A_RESULT"));
        //System.out.println(_e.eval("$A_RESULT format \"yyyy-MM-dd'T'HH:mm:ss.SSSZ\""));
    }

    @Test
    public void checkTosDateInit() throws ScriptException {
        // check that java2Tea is operational before any Tea code execution
        _e.put("A_RESULT", new java.util.Date());
        Object o = _e.eval("$A_RESULT format \"yyyy-MM-dd'T'HH:mm:ss.SSSZ\"");
        //System.out.println(o);
        assertEquals(String.class, o.getClass());
    }

    //@Test
    public void checkTosMistery() throws ScriptException {

        java.util.Date d = new java.util.Date();
        _e.put("A_RESULT", d);

        System.out.println("A_RESULT=" + _e.eval("$A_RESULT format \"yyyy-MM-dd'T'HH:mm:ss.SSSZ\""));

        // on Fedora 12 prints 1 ??? should print -1 or 0
        System.out.println(_e.eval("$A_RESULT compare [new TDate]"));

        java.util.Date d2 = new java.util.Date();

        // uncomment the following 2 lines and the last 2 prints give expected results
        //java.text.SimpleDateFormat df2 = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        //System.out.println("d2="+df2.format(d2));

        System.out.println(((java.util.Date) _e.get("A_RESULT")).compareTo(new java.util.Date()));
        System.out.println(((java.util.Date) _e.get("A_RESULT")).compareTo(d2));
    }

    @Test
    public void checkEvalFile() throws ScriptException, IOException {
        _e.put("A_GLOBAL", null);

        //System.out.println(TeaScriptEngineTest.class.getResource("TeaScriptEngineTestCheckEvalFile.tea"));

        // eval a resource file
        InputStream is = TeaScriptEngineTest.class.getResourceAsStream("TeaScriptEngineTestCheckEvalFile.tea");
        assertNotNull(is);
        java.io.InputStreamReader ir = new java.io.InputStreamReader(is);
        _e.eval(ir);
        assertEquals(2, _e.get("A_GLOBAL"));
        ir.close();
        is.close();
    }

    @Test
    public void checkInvokableFunction() throws ScriptException, NoSuchMethodException {
        // javax.script.Invocable is an optional interface, but TeaEngine
        // supports it.

        String s = "A test string!";

        _e.put("V1", "not the same as A test string!");

        // define a function that sets/defines a global variable.
        _e.eval("global fhello ( arg ) { set! V1 $arg }");

        // Check whether your script engine implements or not!
        // Note that the JavaScript engine implements Invocable interface.
        Invocable inv = (Invocable) _e;

        //System.out.println("Going to invoke! _e=" + _e + " inv=" + inv);
        // invoke the global function named "fhello"
        inv.invokeFunction("fhello", s);
        //System.out.println("invoked! " + _e);

        String r = (String) _e.get("V1");

        assertEquals(s, r);
    }

    @Test
    public void checkInvokableMethod() throws ScriptException, NoSuchMethodException {

        _e.put("obj", null); // put obj in the ENGINE_SCOPE bindings
        _e.put("Vfresult", null); // put Vfresult in the ENGINE_SCOPE bindings

        // define a function that sets/defines a global variable.
        _e.eval("class Test ( ) ; method Test hello ( arg ) { set! Vfresult $arg } ; global obj [new Test]");        // javax.script.Invocable is an optional interface.

        // Check whether your script engine implements or not!
        // Note that the JavaScript engine implements Invocable interface.
        Invocable inv = (Invocable) _e;

        // invoke the global function named "fhello"
        // get script object on which we want to call the method
        Object obj = _e.get("obj");

        // invoke the method named "hello" on the script object "obj"
        inv.invokeMethod(obj, "hello", "Script Method !!");

        String r = (String) _e.get("Vfresult");

        assertEquals(r, "Script Method !!");
    }

    @Test
    public void checkVars() throws ScriptException {
        String s = "A test string!";

        // put aString and aVarsResult in the bindings
        _e.put("aString", s);
        _e.put("aVarsResult", null);

        // evaluate a script string. The script accesses "file"
        // variable and calls method on it
        _e.eval("global aVarsResult $aString");

        String r = (String) _e.get("aVarsResult");

        assertEquals(s, r);
    }

    @Test
    public void checkMultipleScopes() throws ScriptException {
        _e.put("TEST_GLOBAL_1", "hello");
        // print global variable TEST_GLOBAL_1
        _e.eval("echo $TEST_GLOBAL_1");
        // the above line prints "hello"

        // Now, pass a different script context
        ScriptContext newContext = new SimpleScriptContext();
        Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);

        // add new variable TEST_GLOBAL_1 to the new engineScope
        engineScope.put("TEST_GLOBAL_1", "world");

        // execute the same script - but this time pass a different script context
        _e.eval("echo $TEST_GLOBAL_1", newContext);
        // the above line prints "world"
    }

    @Test(expected = ScriptException.class)
    public void checkBadArgv() throws ScriptException {
        // get a new engine, so we don't leave a bad argv in ScriptContext
        ScriptEngine e = _e.getFactory().getScriptEngine();
        Object[] badArgv = {"arg1", "arg2", 3};
        e.put("javax.script.argv", badArgv);
        e.eval("echo [nth $argv 0]");
    }

    @Test
    public void checkGoodArgv() throws ScriptException {
        String argv0 = "someFilename";
        Object[] argv = {"arg1", "arg2"};

        _e.put("javax.script.argv", argv);
        _e.put("javax.script.filename", argv0);
        //System.out.print("len argv=" + _e.eval("length $argv"));
        assertEquals(2, _e.eval("length $argv"));
        assertEquals(argv[0], _e.eval("nth $argv 0"));
        assertEquals(argv[1], _e.eval("nth $argv 1"));
        assertEquals(argv0, _e.eval("is $argv0"));
    }

    @Test
    public void testFactoryProperties() {
        // get factory
        ScriptEngineFactory f = _e.getFactory();


        assertEquals("Tea Engine", f.getParameter(ScriptEngine.ENGINE));
        assertEquals(TeaConfigInfo.get("com.pdmfc.tea.version"), f.getParameter(ScriptEngine.ENGINE_VERSION));
        assertEquals("Tea Engine", f.getParameter(ScriptEngine.NAME));
        assertEquals("Tea", f.getParameter(ScriptEngine.LANGUAGE));
        assertEquals(TeaConfigInfo.get("com.pdmfc.tea.version"), f.getParameter(ScriptEngine.LANGUAGE_VERSION));
        assertNull(f.getParameter("THREADING"));

//        String propKeys[] = {
//                ScriptEngine.ENGINE,
//                ScriptEngine.ENGINE_VERSION,
//                ScriptEngine.NAME,
//                ScriptEngine.LANGUAGE,
//                ScriptEngine.LANGUAGE_VERSION,
//                "THREADING"
//            };
//        System.out.println("TeaScriptEngine properties:");
        // show it
        // for (int i = 0; i < propKeys.length; i++) {
        //    System.out.println(propKeys[i] + "=" + f.getParameter(propKeys[i]));
        //}
    }

    // The following tests are adapted from an IBM article:
    // "Invoke dynamic languages dynamically, Part 1: Introducing the Java scripting API"
    // by Tom McQueeney (tom.mcqueeney@gmail.com)
    // http://www.ibm.com/developerworks/java/library/j-javascripting1/
    /**
     * Instantiate the included  Tea script engine directly
     * (rather than normally through the ScriptManager) to ensure there is
     * no global scope set.
     * <p>
     * Does this test only works with Sun's JRE ?
     */
    @Test
    public void testNoGlobalScopeForDirectInstantiation() throws Exception {
        Class<?> c = Class.forName("com.pdmfc.tea.engine.TeaScriptEngine");
        ScriptEngine teaEngine = (ScriptEngine) c.newInstance();
        Bindings globalBindings =
                teaEngine.getBindings(ScriptContext.GLOBAL_SCOPE);
        assertNull("No global bindings should be present", globalBindings);
    }

    /**
     * Instantiate the included  Tea script engine through the
     * ScriptManager and ensure there is a global scope.
     */
    @Test
    public void testGlobalScopeForManagedInstantiation() {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine teaEngine = engineManager.getEngineByName("Tea");
        Bindings globalBindings =
                teaEngine.getBindings(ScriptContext.GLOBAL_SCOPE);

        assertNotNull("Global bindings should be present", globalBindings);

        // Global bindings returned by the engine should be the same as the
        // bindings returned by the engine manager.
        assertSame(
                "Engine manager has different global bindings",
                globalBindings, engineManager.getBindings());

        // Spec is silent on global bindings content, but we expect nothing.
        assertEquals(
                "Global bindings has seeded values!",
                0, globalBindings.size());
    }

    @Test
    public void testEngineScopeBindings() {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine teaEngine = engineManager.getEngineByName("Tea");
        Bindings engineBindings =
                teaEngine.getBindings(ScriptContext.ENGINE_SCOPE);

        assertNotNull("Engine bindings should be present", engineBindings);

        // Engine bindings probably empty, but we print it if not.
        for (String key : engineBindings.keySet()) {
            System.out.println(
                    "Key='" + key + "', value='" + engineBindings.get(key) + "'");
        }

        // Test that engine actually stores objects in bindings.
        int currentBindingsSize = engineBindings.size();
        teaEngine.put("testing", "123");
        assertEquals(
                "Bindings size should have increased by 1",
                currentBindingsSize + 1, engineBindings.size());
        assertEquals("123", teaEngine.get("testing"));
        assertSame(
                "Bindings should contain same object we put in it",
                "123", teaEngine.get("testing"));
        assertSame(
                "Engine Bindings should give us same object as Engine",
                "123", engineBindings.get("testing"));
    }

    /**
     * Tests methods on the ScriptContext.
     */
    @Test
    public void testScriptContext() {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine teaEngine = engineManager.getEngineByName("Tea");

        Bindings engineBindings =
                teaEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        Bindings globalBindings =
                teaEngine.getBindings(ScriptContext.GLOBAL_SCOPE);

        engineBindings.put("testing", "123");
        globalBindings.put("testing", "345");

        ScriptContext context = teaEngine.getContext();
        assertSame(
                "Context should give us engine binding before global",
                "123", context.getAttribute("testing"));
        assertSame(
                "Context's global scope has wrong value",
                "345", context.getAttribute("testing", ScriptContext.GLOBAL_SCOPE));

        assertTrue(
                "Engines created by manager should have global and engine scope",
                context.getScopes().size() >= 2);

        // Removing object in engine scope reveals object previously hidden
        // in the global scope when retrieved by ScriptContext.
        engineBindings.remove("testing");
        assertSame(
                "Global object not revealed in context after deleting engine's object",
                "345", context.getAttribute("testing"));

        // But remember, using ScriptEngine.get retrieves values *only*
        // from the engine scope, by definition. Check to verify:
        assertNull(
                "Engine sees global-scope object. API violation",
                teaEngine.get("testing"));

        // See the testScopeAndBindingsState for more Bindings/context fun.
    }

    /**
     * Tests to determine whether a function defined in a script engine using
     * eval is available even if we change the ScriptContext or Bindings on
     * subsequent calls to eval.
     */
    @Test
    public void testExecutionScope() throws ScriptException {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine teaEngine = engineManager.getEngineByName("Tea");

        // this shouldn't be needed :-( 
        // global variables should be available (after execution)
        // in the ScriptContext. But the need to support the SimpleScriptContext
        // implementation makes it hard. So, only the variables that are
        // already in the bindings are copied out, after script execution.
        teaEngine.put("MESSAGE", null);

        teaEngine.eval(
                "global fSayHello ( aName ) {"
                + "    global MESSAGE [str-cat \"Hello, \" $aName] ;"
                + "    return $MESSAGE"
                + "}");

        assertEquals("Hello, Tom", teaEngine.eval("fSayHello \"Tom\""));
        assertEquals("Hello, Tom", teaEngine.get("MESSAGE"));

        // Replace with empty engine bindings and ensure "message" isn't there.
        Bindings emptyBindings1 = teaEngine.createBindings();
        teaEngine.setBindings(emptyBindings1, ScriptContext.ENGINE_SCOPE);
        assertNull(
                "MESSAGE var should not be in engine scope",
                teaEngine.get("MESSAGE"));
        // Ensure fSayHello function isn't in scope anymore, either.
        try {
            teaEngine.eval("fSayHello \"there\"");
            fail("fSayHello method is still in the engine's context");
        } catch (ScriptException se) {
            // Good. Expected.
        }
    }

    @Test
    public void testScopeAndBindingsState() throws ScriptException {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine teaEngine = engineManager.getEngineByName("Tea");

        // this shouldn't be needed :-(
        // global variables should be available (after execution)
        // in the ScriptContext. But the need to support the SimpleScriptContext
        // implementation makes it hard. So, only the variables that are
        // already in the bindings are copied out, after script execution.
        teaEngine.put("MESSAGE", null);
        //teaEngine.put("fSayHello", null);


        // Like testExecutionScope, except move the function to global context.
        teaEngine.eval(
                "global fSayHello ( aName ) {"
                + "    global MESSAGE [str-cat \"Hello, \" $aName]"
                + "}");

        assertEquals("Hello, again", teaEngine.eval("fSayHello \"again\""));
        assertEquals("Hello, again", teaEngine.get("MESSAGE"));

        Bindings bindingsWithFunction =
                teaEngine.getBindings(ScriptContext.ENGINE_SCOPE);
        Bindings emptyBindings = teaEngine.createBindings();
        teaEngine.setBindings(emptyBindings, ScriptContext.ENGINE_SCOPE);

        // Test that fSayHello function no longer is in the engine scope.
        // Note spec does not require engines to maintain global functions
        // in the engine context. Tea offers this feature, however.
        try {
            teaEngine.eval("fSayHello \"there\"");
            fail("fSayHello method is still in the engine's context");
        } catch (ScriptException se) {
            // Good. Expected.
        }

        // Add fSayHello function in saved bindings to global scope and retest
        teaEngine.setBindings(bindingsWithFunction, ScriptContext.GLOBAL_SCOPE);

        // Note how functions are searched for in all scopes in Tea engine.
        // Is this required by spec?
        // Spec does say in 4.3.4.1.3 that global bindings don't need to be
        // accessible to scripts as variables.
        // For now, TeaScriptEngine does not support copying functions between
        // scopes.
//        try {
//            teaEngine.eval("fSayHello \"there again\"");
//        } catch (ScriptException se) {
//            fail("fSayHello should have been found in the global scope");
//        }
//        try {
//            teaEngine.eval("fSayHello \"there yet again\"", emptyBindings);
//        } catch (ScriptException se) {
//            fail("fSayHello should exist in global scope even with empty bindings");
//        }

        // Ensure variable 'MESSAGE' is now in the correct scope.

        // ScriptEngine.get looks *only* in the engine scope.
        assertNull("MESSAGE shouldn't be in engine scope", teaEngine.get("MESSAGE"));

        assertEquals(
                "MESSAGE var *should* be in global scope",
                "Hello, again", // "Hello, there yet again",
                teaEngine.getBindings(ScriptContext.GLOBAL_SCOPE).get("MESSAGE"));

        assertEquals(
                "MESSAGE var/attribute should be in global scope",
                ScriptContext.GLOBAL_SCOPE,
                teaEngine.getContext().getAttributesScope("MESSAGE"));

        // To search for variable in all scopes, use ScriptContext.getAttribute.
        assertEquals(
                "MESSAGE var should be findable searching up thru the context",
                "Hello, again", // "Hello, there yet again",
                teaEngine.getContext().getAttribute("MESSAGE"));
    }

    /**
     * Tests for the existence of the required 'context' variable
     * script engines must set in their context containing the ScriptContext.
     * Spec 4.3.4.1.2
     */
    @Test
    public void testContextAvailable() throws ScriptException {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine teaEngine = engineManager.getEngineByName("Tea");

        assertEquals(
                "Standard 'context' var pre-set in script context",
                ScriptContext.ENGINE_SCOPE,
                teaEngine.eval("java-get-value $context ENGINE_SCOPE"));

        teaEngine.eval("$context setAttribute \"hi\" \"there\" [java-get-value javax.script.ScriptContext ENGINE_SCOPE]");
        assertEquals(
                "'context' didn't allow attribute to be set",
                "there",
                teaEngine.get("hi"));

    }
    
    
    /**
     * Spec 4.3.1 ScriptContext - $stdin, $stdout, $stderr
     * should be initialized from the appropriate reader/writers
     * in the ScriptContext. 
     * @throws IOException 
     */
    @Test
    public void testIORedirect() throws ScriptException, IOException {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine teaEngine = engineManager.getEngineByName("Tea");
        
        // Create a simple context with redirections to String readers/writers
        ScriptContext sc1 = new SimpleScriptContext();
        String anInputString = "A 1st line\nA 2nd line";
        java.io.StringReader srIn1 = new java.io.StringReader(anInputString);
        java.io.StringWriter swOut1 = new java.io.StringWriter();
        java.io.StringWriter swErr1 = new java.io.StringWriter();
        sc1.setReader(srIn1);
        sc1.setWriter(swOut1);
        sc1.setErrorWriter(swErr1);
        teaEngine.setContext(sc1);
        teaEngine.eval(
                "define a1StLine [$stdin readln] ;"
                +"define a2NdLine [$stdin readln] ;"
                +"define a3RdLine [$stdin readln] ;"
                +"$stdout write $a1StLine ;"
                +"$stderr writeln $a2NdLine ;"
                +"if {null? $a3RdLine} { $stdout write \"EOF\" }");
        // No need for flushing, as eval calls STeaRuntime.stop()
        // which in turns calls ModuleIO.stop() which flushes the streams.
        assertEquals("A 1st lineEOF",swOut1.toString());
        assertEquals("A 2nd line\n",swErr1.toString().replaceFirst("\r", ""));

        // Now create another context with other redirections
        ScriptContext sc2 = new SimpleScriptContext();
        String anInputString2 = "B 1st line";
        java.io.StringReader srIn2 = new java.io.StringReader(anInputString2);
        java.io.StringWriter swOut2 = new java.io.StringWriter();
        sc2.setReader(srIn2);
        sc2.setWriter(swOut2);
        teaEngine.setContext(sc2);
        teaEngine.eval("$stdout write [$stdin readln]");
        assertEquals("B 1st line",swOut2.toString());
      
        // Now swicth back to the 1st context
        srIn1.reset(); // reset input stream
        teaEngine.setContext(sc1);
        teaEngine.eval("$stdout write [$stdin readln]");
        assertEquals("A 1st lineEOFA 1st line",swOut1.toString());
    }

}
