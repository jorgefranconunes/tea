/**************************************************************************
 *
 * Copyright (c) 2010-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/
package com.pdmfc.tea.modules.reflect;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.pdmfc.tea.engine.TeaScriptEngine;
import com.pdmfc.tea.engine.TeaScriptEngineFactory;
import com.pdmfc.tea.modules.reflect.STeaJavaTypes;
import com.pdmfc.tea.modules.util.SDate;
import com.pdmfc.tea.modules.util.SHashtable;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaNull;
import com.pdmfc.tea.TeaPair;
import com.pdmfc.tea.TeaSymbol;





/**
 *
 * @author jpsl
 */
public class SModuleReflectTest {

    private ScriptEngineFactory _factory = null;
    private ScriptEngine _engine = null;

    public SModuleReflectTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws ScriptException {
        _factory = new TeaScriptEngineFactory();
        _engine = _factory.getScriptEngine();
    }

    @After
    public void tearDown() throws ScriptException {
        //_engine.end();
    }

    /**
     * Test of java2Tea method, of class SModuleReflect.
     */
    // @Test
    // public void testJava2Tea() throws Exception {
    //     // In current implementation of ModuleReflect + TeaScriptEngine,
    //     // we must run a script to force the TeaRuntime initialization
    //     // before we can test ModuleReflect.java2Tea() regarding convertions
    //     // to STosObj that require autoloading.
    //     assertEquals(_engine.eval("is 123"), 123);

    //     // java2Tea needs a Tea context
    //     TeaContext context = _engine.getTeaRuntime().getToplevelContext();

    //     // an incomplete list of values/objects that are not converted by java2Tea
    //     Object anIdentityArray[] = {
    //         true, // Boolean
    //         123, // Integer
    //         123L, // Long
    //         123.0, // Double
    //         "aString", // String
    //         TeaNull.NULL, // Tea runtime
    //         TeaPair.emptyList(), // Tea runtime
    //         TeaSymbol.getSymbol("is") // Tea runtime
    //     };
    //     for (Object obj : anIdentityArray) {
    //         Object result = STeaJavaTypes.java2Tea(obj, context);
    //         assertSame(obj, result);
    //     }

    //     // Float gets to be a Double
    //     assertSame(
    //             Double.class,
    //             STeaJavaTypes.java2Tea(Float.valueOf(2.33f), context).getClass());

    //     // a Map gets to be a THashtable
    //     java.util.HashMap<String, Object> m = new java.util.HashMap<String, Object>();
    //     m.put("k1", 1.0d);
    //     String s = "hello";
    //     m.put("k2", s);
    //     SHashtable tm = (SHashtable)STeaJavaTypes.java2Tea(m, context);
    //     Object args[] = {tm, TeaSymbol.getSymbol("get"), "k1"};
    //     assertEquals(Double.valueOf(1.0d), (Double) tm.get(tm, context, args));
    //     Object args2[] = {tm, TeaSymbol.getSymbol("get"), "k2"};
    //     assertSame(s, tm.get(tm, context, args2));

    //     // Date
    //     Object teaObject =
    //         STeaJavaTypes.java2Tea(new java.util.Date(), context);
    //     assertSame(teaObject.getClass(), SDate.class);
    // }

    /**
     * Test of tea2Java method, of class SModuleReflect.
     */
    @Test
    public void testTea2Java() throws Exception {

        // an incomplete list of values/objects that are not converted by java2Tea
        Object anIdentityArray[] = {
            true, // Boolean
            123, // Integer
            123L, // Long
            123.0, // Double
            "aString" // String
        };
        for (Object obj : anIdentityArray) {
            Object result = STeaJavaTypes.tea2Java(obj);
            assertSame(obj, result);
        }

        // TODO: a lot more of objects to test
    }

    /**
     * Test instantiation of java objects
     */
    @Test
    public void testNewInstance() throws Exception {
        String script;

        // constructor with double argument
        script = _factory.getProgram(
                "define obj [java-new-instance com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass 3.4]",
                "$obj adder 30");
        assertEquals(33, _engine.eval(script));

        // constructor with string
        script = _factory.getProgram(
                "define obj [java-new-instance com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass \"s1\"]",
                "$obj adder \"s2\"");
        assertEquals("s1+s2", _engine.eval(script));

        // constructor with string, and then method execution with null args
        _engine.eval("global obj [java-new-instance com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass \"s1\"]");
        assertEquals("s1+s2+s3", _engine.eval("$obj adder \"s2\" \"s3\""));
        assertEquals("s1+null+s3", _engine.eval("$obj adder $null \"s3\""));
        assertEquals("s1+s2+null", _engine.eval("$obj adder \"s2\" $null"));
        // The following can match adder(String,String) or adder(Float,Float)
        // so, it is not a proper test.
        // $obj adder String String
        //SModuleReflectTestTopClass obj = new SModuleReflectTestTopClass("s1");
        //System.out.println("$obj adder $null $null - should give="+(obj.adder(null,null)));
        //Object oResult = _engine.eval("$obj adder $null $null");
        //assertEquals("s1+null+null", oResult);


        // constructor with null
        _engine.eval("global obj [java-new-instance com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass $null]");
        assertEquals("0+s2+s3", _engine.eval("$obj adder \"s2\" \"s3\""));
    }

    /**
     * Test execution of methods on java objects
     */
    @Test
    public void testExecMethod() throws Exception {

        // test call to overloaded method with 2 int args
        // static adder int in
        // or
        // static adder Integer Integer
        // current version of SModuleReflect can match any of those methods
        // (not deterministic). TODO make SModuleReflect deterministic in this sense ?
        // what says JLS 15.12 ?
        // http://java.sun.com/docs/books/jls/third_edition/html/expressions.html#15.12
        Integer iResult = (Integer) _engine.eval("java-exec-method \"com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass\" adder 34 23");
        assertTrue(iResult.equals(Integer.valueOf(34 + 23)) || iResult.equals(1 + 34 + 23));


        // $obj adder int obj
        assertEquals(
                Integer.valueOf(32 + 23 + 30),
                _engine.eval("define obj1 [java-new-instance com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass 32] ; "
                + "define obj2 [java-new-instance com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass 23] ; "
                + "$obj2 adder 30 $obj1"));

        //
        assertEquals(
                Integer.valueOf((23 + (32 + 30)) + 15),
                _engine.eval("define obj1 [java-new-instance \"com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass\" 32] ; "
                + "define obj2 [java-new-instance \"com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass\" 23] ; "
                + "define obj3 [$obj2 getAdder 30 $obj1] ; "
                + "$obj3 adder 15"));

        // test static method with an array return value
        assertTrue((Boolean) _engine.eval("global values [java-exec-method com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass getArray 34 23] ; "
                + "and {pair? $values} {== 2 [length $values]} {== [car $values] 34} {== [nth $values 1] 23}"));


        // test static method with an array return value
        assertTrue((Boolean) _engine.eval("global values [java-exec-method com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass getArray \"ola\" \"ole\"] ; "
                + "and {pair? $values} {== 2 [length $values]} {str== [car $values] \"ola\"} {str== [nth $values 1] \"ole\"}"));

        // test static method with null arguments and array return value
        assertTrue((Boolean) _engine.eval("global values [java-exec-method com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass getArray \"s1\" $null] ; "
                + "and {pair? $values} {== 2 [length $values]} {str== [car $values] \"s1\"} {null? [nth $values 1]}"));

        // test static method with null arguments and array return value
        assertTrue((Boolean) _engine.eval("global values [java-exec-method com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass getArray $null \"s2\"] ; "
                + "and {pair? $values} {== 2 [length $values]} {null? [car $values]} {str== [nth $values 1] \"s2\"}"));

        // test static method with null arguments and array return value
        assertTrue((Boolean) _engine.eval("global values [java-exec-method com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass getArray $null $null] ; "
                + "and {pair? $values} {== 2 [length $values]} {null? [car $values]} {null? [nth $values 1]}"));

    }

    /**
     * Test java-get-method.
     * Locate the proper overloaded method, using exact argument types.
     */
    @Test
    public void testGetMethod() throws Exception {
        String script;

        script = _factory.getProgram(
                "define mtd [java-get-method com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass adder \"long\" \"long\"]",
                "$mtd 40 30");
        assertEquals(73L, _engine.eval(script));

        script = _factory.getProgram(
                "define mtd [java-get-method com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass adder int int]",
                "$mtd 2 3");
        assertEquals(5, _engine.eval(script));

        script = _factory.getProgram(
                "define mtd [java-get-method \"com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass\" adder java.lang.Integer java.lang.Integer]",
                "$mtd 2 3");
        assertEquals(6, _engine.eval(script));
    }

    /**
     * Test java-get, for both static and member fields.
     * As the tests operate on complex, this also tests a lot of java2tea on
     * lists and maps.
     */
    @Test
    public void testGetValue() throws Exception {
        assertEquals(
                SModuleReflectTestTopClass._CTE_STRING,
                _engine.eval("java-get-value com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass _CTE_STRING"));

        assertTrue(
                Math.abs(SModuleReflectTestTopClass._CTE_FLOAT
                - (Double) _engine.eval("java-get-value \"com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass\" _CTE_FLOAT"))
                < 0.0001);

        assertEquals(
                SModuleReflectTestTopClass._CTE_INTEGER,
                _engine.eval("java-get-value com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass \"_CTE_INTEGER\""));

        _engine.eval("global obj [java-new-instance com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass]");

        assertTrue((Boolean) _engine.eval("pair? [java-get-value $obj _CTE_LIST]"));

        assertEquals(345, _engine.eval("nth [java-get-value $obj _CTE_LIST] 3"));

        assertEquals("This is a subList", _engine.eval("car [nth [java-get-value $obj _CTE_LIST] 2]"));

        assertTrue((Boolean) _engine.eval("float? [[java-get-value $obj \"_CTE_MAP\"] get \"A float\"]"));

        assertTrue((Boolean) _engine.eval("pair? [[java-get-value $obj \"_CTE_MAP\"] get \"A list\"]"));

        assertTrue((Boolean) _engine.eval("nth [[java-get-value $obj \"_CTE_MAP\"] get \"A list\"] 1"));

        assertTrue((Boolean) _engine.eval("string? [[[java-get-value $obj \"_CTE_MAP\"] get \"A sub hash\"] get \"A String\"]"));

        assertEquals("This is a member string", _engine.eval("java-get-value $obj _aString"));
    }

    /**
     * Test java-get, for both static and member fields.
     * As the tests operate on complex, this also tests a lot of java2tea on
     * lists and maps.
     */
    @Test
    public void testSetValue() throws Exception {

        String oldStr;
        String newStr;

        // set a static field
        oldStr = (String) _engine.eval("java-get-value com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass _CTE_STRING");
        _engine.eval("java-set-value com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass _CTE_STRING [[new TDate] format \"yyyy-MM-dd'T'HH:mm:ss.SSSZ\"]");
        assertTrue(SModuleReflectTestTopClass._CTE_STRING.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}T.*"));
        _engine.eval("java-set-value com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass _CTE_STRING \"" + oldStr + "\"");

        // set an instance field
        _engine.eval("global obj [java-new-instance com.pdmfc.tea.modules.reflect.SModuleReflectTestTopClass]");
        oldStr = (String) _engine.eval("java-get-value $obj _aString");
        assertEquals("This is a member string", oldStr);
        _engine.eval("java-set-value $obj \"_aString\" [[new TDate] format \"yyyy-MM-dd'T'HH:mm:ss.SSSZ\"]");
        newStr = (String) _engine.eval("java-get-value $obj _aString");
        assertTrue(newStr.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}T.*"));
        _engine.eval("java-set-value $obj _aString \"" + oldStr + "\"");
    }

    /**
     * TSK-PDMFC-TEA-0045 InvocationTargetException must use getCause
     */
    @Test
    public void testIoException() throws Exception {

        // Get the message expected to be thrown by the JVM
        String aMsg = null;
        java.io.File f = new java.io.File("aDir");
        try {
            java.io.File f2 = java.io.File.createTempFile("dumbPrefix", ".png", f);
            fail("The above java code ought to throw an exception!");
        } catch (java.io.IOException e) {
            aMsg = e.getMessage();
        }

        String script = _factory.getProgram(
                "define imgDirJObj [java-new-instance java.io.File \"aDir\"]",
                "define imgTmpFileJObj [java-exec-method java.io.File createTempFile \\",
                "                          \"dumbPrefix\"  \\",
                "                          \".png\" \\",
                "                          $imgDirJObj]");
        try {
            _engine.eval(script);
            fail("The script of this test should throw an exception!");
        } catch (ScriptException e) {
            assertTrue(e.getMessage().contains(aMsg));
        }
    }
}
