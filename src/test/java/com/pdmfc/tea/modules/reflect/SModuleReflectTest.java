/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pdmfc.tea.modules.reflect;

import com.pdmfc.tea.engine.TeaScriptEngine;
import com.pdmfc.tea.modules.util.SDate;
import com.pdmfc.tea.modules.util.SHashtable;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.STeaRuntime;
import java.util.HashMap;
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
public class SModuleReflectTest {

    private TeaScriptEngine _engine = null;

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
        _engine = new TeaScriptEngine();
        // In current implementation of SModuleReflect + TeaScriptEngine,
        // we must run a script to force the STeaRuntime initialization
        // before we can test SModuleReflect.java2Tea() regaring convertions
        // to STosObj that require autoloading.
        assertEquals(_engine.eval("is 123"), 123);
    }

    @After
    public void tearDown() throws ScriptException {
        _engine.end();
    }

    /**
     * Test of java2Tea method, of class SModuleReflect.
     */
    @Test
    public void testJava2Tea() throws Exception {
        // java2Tea needs a Tea context
        SContext context = _engine.getTeaRuntime().getToplevelContext();

        // an incomplete list of values/objects that are not converted by java2Tea
        Object anIdentityArray[] = {
            true, // Boolean
            123, // Integer
            123L, // Long
            123.0, // Double
            "aString", // String
            SObjNull.NULL, // Tea runtime
            SObjPair.emptyList(), // Tea runtime
            SObjSymbol.getSymbol("is") // Tea runtime
        };
        for (Object obj : anIdentityArray) {
            Object result = SModuleReflect.java2Tea(obj, context);
            assertSame(obj, result);
        }

        // Float gets to be a Double
        assertSame(
                Double.class,
                SModuleReflect.java2Tea(Float.valueOf(2.33f), context).getClass());

        // a Map gets to be a THashtable
        java.util.HashMap<String, Object> m = new java.util.HashMap<String, Object>();
        m.put("k1", 1.0d);
        String s = "hello";
        m.put("k2", s);
        SHashtable tm = (SHashtable) SModuleReflect.java2Tea(m, context);
        Object args[] = {tm, SObjSymbol.getSymbol("get"), "k1"};
        assertEquals(Double.valueOf(1.0d), (Double) tm.get(tm, context, args));
        Object args2[] = {tm, SObjSymbol.getSymbol("get"), "k2"};
        assertSame(s, tm.get(tm, context, args2));

        // Date
        Object teaObject = SModuleReflect.java2Tea(new java.util.Date(), context);
        assertSame(teaObject.getClass(), SDate.class);

        // TODO: a lot more of objects to test
    }

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
            Object result = SModuleReflect.tea2Java(obj);
            assertSame(obj, result);
        }

        // TODO: a lot more of objects to test
    }

    /**
     * Test execution of methods on java objects
     */
    @Test
    public void testExecMethod() throws Exception {
        // test call to overloaded method with 2 int args
        assertEquals(
                Integer.valueOf(34 + 23),
                _engine.eval("java-exec-method \"SModuleReflectTestTopClass\" adder 34 23"));

        // int obj
        assertEquals(
                Integer.valueOf(32 + 23 + 30),
                _engine.eval("define obj1 [java-new-instance \"SModuleReflectTestTopClass\" 32] ; "
                + "define obj2 [java-new-instance \"SModuleReflectTestTopClass\" 23] ; "
                + "$obj2 adder 30 $obj1"));

        // 
        assertEquals(
                Integer.valueOf((23 + (32 + 30)) + 15),
                _engine.eval("define obj1 [java-new-instance \"SModuleReflectTestTopClass\" 32] ; "
                + "define obj2 [java-new-instance \"SModuleReflectTestTopClass\" 23] ; "
                + "define obj3 [$obj2 getAdder 30 $obj1] ; "
                + "$obj3 adder 15"));

        // test static method with an array return value
        assertTrue((Boolean)
                _engine.eval("global values [java-exec-method \"SModuleReflectTestTopClass\" getArray 34 23] ; "
                + "and {pair? $values} {== 2 [length $values]} {== [car $values] 34} {== [nth $values 1] 23}"));


        // test static method with an array return value
        assertTrue((Boolean)
                _engine.eval("global values [java-exec-method \"SModuleReflectTestTopClass\" getArray \"ola\" \"ole\"] ; "
                + "and {pair? $values} {== 2 [length $values]} {str== [car $values] \"ola\"} {str== [nth $values 1] \"ole\"}"));
    }
}
