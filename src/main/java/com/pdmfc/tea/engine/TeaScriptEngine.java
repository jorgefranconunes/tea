/**************************************************************************
 *
 * Copyright (c) 2007 PDM&FC, All Rights Reserved.
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

import com.pdmfc.tea.runtime.SNoSuchVarException;
import java.io.*;
import javax.script.*;

import com.pdmfc.tea.compiler.SCompiler;
import com.pdmfc.tea.runtime.STeaRuntime;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.STypes;
import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;
import java.util.Set;

/**
 * Most of the functionality is implemented, except for
 * 2 getInterface() methods.
 * <p>The use of SimpleScriptContext is supported, and the full execution
 * context (STeaRuntime) is saved/restored from the ScriptContext
 * using the reserved attribute name com.pdmfc.tea.engine.runtime.</p>
 * <p>Unlike the Rhino engine, the TeaScriptEngine doesn't allow the copying
 * of function (lambda) values into the GLOBAL_SCOPE. (In fact, the copying
 * of any function (lambda) values between scopes should not be allowed,
 * as a function value in Tea is tightly coupled with the scope environment
 * where the function was created.) So, in order to avoid problems, the author
 * recommends that you only put/get simple data values (Strings or numerics)
 * into/out of bindings - which are mapped into/from Tea global variables
 * with the same name as the attribute name.</p>
 * <p>A Tea global variable named <b>context</b> is introduced, and it is a
 * wrapper aroung the ScriptContext
 * object. It behaves just like a regular TOS object.</p>
 * <p>See the examples in the programmer's guide (part of the Tea documentation)
 * for more use cases.</p>
 */
public class TeaScriptEngine extends AbstractScriptEngine
        implements Compilable, Invocable {

    /**
     * Reserved binding name for the STeaRuntime.
     * Every ScriptContext has an associated STeaRuntime stored
     * with this key name in the bindings at ENGINE_SCOPE level.
     */
    public static final String KEY_RUNTIME =
            "com.pdmfc.tea.engine.runtime";

    /**
     * A single com.pdmfc.tea.compiler.SCompiler is used to compile
     * Tea code into an internal representation
     * com.pdmfc.tea.runtime.SCode.
     */
    protected SCompiler _compiler;
    /**
     * Reference the factory that instantiated this object.
     */
    protected volatile TeaScriptEngineFactory _factory;

    /**
     * Initialize _fatcory to null. 
     * As we are not allowed to throw a ScriptException, real
     * initialization is delayed until you try to evaluate some Tea
     * code.
     * @see TeaCompiledScript#eval(ScriptContext scriptContext)
     */
    public TeaScriptEngine() {
        //this(null);
    }

    /**
     * Initialize _factory and super-class with
     * <code>AbstractScriptEngine(new TeaBindings())</code>
     * so that the context now has an uninitialized TeaBindings.
     * As we are not allowed to throw a ScriptException, real
     * initialization is delayed until you try to evaluate some Tea
     * code.
     * @see TeaCompiledScript#eval(ScriptContext scriptContext)
     */
    public TeaScriptEngine(TeaScriptEngineFactory aFactory) {
        //super(new TeaBindings());
        //System.out.println("TeaScriptEngine(TeaScriptEngineFactory aFactory)");
        _factory = aFactory;
        // TODO: more constructors with importPath setup ?
    }

    /**
     * @return a new javax.script.SimpleBindings.
     */
    public Bindings createBindings() {
        //System.out.println("TeaScriptEngine.createBindings()");
        return new SimpleBindings();
        //return new TeaBindings();
    }

    /**
     * @return a com.pdmfc.tea.compiler.SCompiler. There is only one
     * per engine.
     */
    public synchronized SCompiler getCompiler() {
        if (_compiler == null) {
            _compiler = new SCompiler();
        }
        return _compiler;
    }

    //protected synchronized ScriptContext getScriptContext(Bindings nn) {
    //    ScriptContext sc = super.getScriptContext(nn);
    //    STeaRuntime context =
    //        (STeaRuntime)sc.getAttribute(KEY_RUNTIME,
    //                                     ScriptContext.ENGINE_SCOPE);
    //    if (context == null) {
    //        try {
    //            context = new STeaRuntime();
    //            sc.setAttribute(KEY_RUNTIME, context, ScriptContext.ENGINE_SCOPE);            // context.start() ?
    //            // setup import paths and readers/writers
    //            // context.stop(); - put where ?
    //            // context.end(); - put where ?
    //        } catch (STeaException e) {
    //            ; // TODO: ?
    //        }
    //    }
    //    return sc;
    //}
    /**
     * Converts the String to
     * <code>byte[]</code> for evaluation.
     * @see #evalBytes(byte[] script, ScriptContext scriptContext).
     */
    public Object eval(String script, ScriptContext scriptContext)
            throws ScriptException {
        //System.out.println("eval \""+script+"\" , scriptContext="+scriptContext);
        CompiledScript code = this.compile(script);
        return code.eval(scriptContext);
    }

    /**
     * Reads the file to memory, and converts it to
     * <code>byte[]</code> for evaluation.
     * @see #evalBytes(byte[] script, ScriptContext scriptContext).
     */
    public Object eval(Reader reader, ScriptContext scriptContext)
            throws ScriptException {
        StringBuffer cb = new StringBuffer();
        char ca[] = new char[4096];
        try {
            while (reader.read(ca) >= 0) {
                cb.append(ca);
            }
        } catch (IOException e) {
            throw new ScriptException(e);
        }
        return this.eval(cb.toString(), scriptContext);
    }

    /**
     * Compiles <code>byte[]</code> as a Tea script and executes it.
     */
    public Object evalBytes(byte script[], ScriptContext scriptContext)
            throws ScriptException {
        CompiledScript code = this.compileBytes(script);
        return code.eval(scriptContext);
    }

    /**
     * @return _factory
     */
    public ScriptEngineFactory getFactory() {
        synchronized (this) {
            if (_factory == null) {
                _factory = new TeaScriptEngineFactory();
            }
        }
        return _factory;
    }

    public CompiledScript compile(Reader reader)
            throws ScriptException {
        StringBuffer cb = new StringBuffer();
        char ca[] = new char[4096];
        try {
            while (reader.read(ca) >= 0) {
                cb.append(ca);
            }
        } catch (IOException e) {
            throw new ScriptException(e);
        }
        return this.compile(cb.toString());
    }

    public CompiledScript compile(String script)
            throws ScriptException {
        try {
            return new TeaCompiledScript(this, getCompiler().compile(script));
        } catch (Exception e) {
            throw new ScriptException(e);
        }
    }

    public CompiledScript compileBytes(byte script[])
            throws ScriptException {
        String scriptString = new String(script);
        return this.compile(scriptString);
    }

    public Object invokeFunction(String name, Object... args)
            throws ScriptException {
        //System.out.println("Invoke using sc=" + this.getContext());
        STeaRuntime teaRuntime = this.getRuntime(this.getContext());
        //System.out.println("invokeFunction TeaRuntime=" + teaRuntime);
        teaRuntime.start();
        this.context2TeaGlobals(teaRuntime, this.getContext());
        SContext teaContext = teaRuntime.getToplevelContext();
        SObjSymbol nameSymbol = SObjSymbol.getSymbol(name);
        try {
            Object[] newArgs = new Object[args.length + 1];
            newArgs[0] = nameSymbol;
            for (int i = 0; i < args.length; i++) {
                newArgs[i + 1] = com.pdmfc.tea.modules.reflect.SModuleReflect.java2Tea(args[i], teaContext);
            }
            SObjFunction obj = STypes.getFunction(teaContext, newArgs, 0);
            Object result = com.pdmfc.tea.modules.reflect.SModuleReflect.tea2Java(obj.exec(obj, teaContext, newArgs));
            return result;
        } catch (STeaException e) {
            throw new ScriptException(e);
        } finally {
            // retrived updated global vars to Bindings.
            this.teaGlobals2Context(teaRuntime, this.getContext());
            teaRuntime.stop();
            // teaRuntime.end(); -- not here. On servlet unloading ?
        }
    }

    public Object invokeMethod(Object thisz, String name, Object... args)
            throws ScriptException {
        STeaRuntime teaRuntime = this.getRuntime(this.getContext());
        teaRuntime.start();
        this.context2TeaGlobals(teaRuntime, this.getContext());
        SContext teaContext = teaRuntime.getToplevelContext();
        SObjSymbol nameSymbol = SObjSymbol.getSymbol(name);
        try {
            Object[] newArgs = new Object[args.length + 2];
            newArgs[0] = thisz;
            newArgs[1] = nameSymbol;
            for (int i = 0; i < args.length; i++) {
                newArgs[i + 2] = com.pdmfc.tea.modules.reflect.SModuleReflect.java2Tea(args[i], teaContext);
            }
            SObjFunction obj = STypes.getFunction(teaContext, newArgs, 0);
            Object result = com.pdmfc.tea.modules.reflect.SModuleReflect.tea2Java(obj.exec(obj, teaContext, newArgs));
            this.teaGlobals2Context(teaRuntime, this.getContext());
            return result;
        } catch (STeaException e) {
            throw new ScriptException(e);
        } finally {
            // retrived updated global vars to Bindings.
            this.teaGlobals2Context(teaRuntime, this.getContext());
            teaRuntime.stop();
            // teaRuntime.end(); -- not here. On servlet unloading ?
        }

    }

    /**
     * Unsupported yet.
     * @throws java.lang.UnsupportedOperationException
     */
    public <T> T getInterface(Class<T> clasz) {
        // TODO
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    /**
     * Unsupported yet.
     * @throws java.lang.UnsupportedOperationException
     */
    public <T> T getInterface(Object thisz, Class<T> clasz) {
        // TODO
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    /**
     * com.pdmfc.tea.runtime.STeaRuntime should have the end() method
     * called before unloading, so that any Tea code callbacks may be
     * called to free allocated resources.
     * As JSR-223 does not support this feature, this method is left
     * as an extension for the programmer to call it explicitely.
     * (If the Tea code does not defines any callbacks to release
     * resources, no real harm is done by not calling this method.)
     */
    public void end() throws ScriptException {
        this.getRuntime(this.getContext()).end();
    }

    /**
     * Get a STeaRuntime from the given engine context.
     * If non exists, create a new one.
     * The STeaRuntime is stored in the script context, in the ENGINE_SCOPE
     * using the reserved attribute name "com.pdmfc.tea.engine.teaRuntime".
     * @throws ScriptException as Tea initialization code might be
     * called to initialize the runtime.
     */
    public STeaRuntime getRuntime(ScriptContext sc) throws ScriptException {
        synchronized (sc) {
            // SCR.4.3.4.1.2 Script Execution - mentions key "context"
            // It says nothing about (dis)allowing the use of a keys named
            // "context" in the GLOBAL_SCOPE. We just recognize it at
            // ENGINE_SCOPE.
            STeaRuntime teaRuntime = (STeaRuntime) sc.getAttribute(KEY_RUNTIME, ScriptContext.ENGINE_SCOPE);
            if (teaRuntime == null) {
                teaRuntime = new STeaRuntime();
                // no need to initialize from context here. It is initialized
                // in TeaCompiledScript.eval().
                //System.out.println("Setting a new runtime on ENGINE_SCOPE of "+sc);
                sc.setAttribute(KEY_RUNTIME, teaRuntime, ScriptContext.ENGINE_SCOPE);
            }
            return teaRuntime;
        }
    }

    public void context2TeaGlobals(STeaRuntime teaRuntime, ScriptContext sc)
            throws ScriptException {
        try {
            SContext teaContext = teaRuntime.getToplevelContext();
            Bindings b = sc.getBindings(ScriptContext.GLOBAL_SCOPE);
            if (b != null) {
                //System.out.println("sc "+sc+" GLOBAL_SCOPE has "+b.keySet().size()+" entries");
                for (String key : b.keySet()) {
                    // SCR.4.3.4.1.1 Bindings, Bound Values and State - skip
                    // reserved names
                    if (key.startsWith("javax.script")
                            || key.startsWith("com.pdmfc.tea")) {
                        continue;
                    }
                    SObjSymbol keySym = SObjSymbol.addSymbol(key);
                    //System.out.println("GLOBAL_SCOPE to tea global " + key + "=" + b.get(key));
                    teaContext.newVar(keySym, com.pdmfc.tea.modules.reflect.SModuleReflect.java2Tea(b.get(key), teaContext));
                }
            }
            b = sc.getBindings(ScriptContext.ENGINE_SCOPE);
            if (b != null) {
                for (String key : b.keySet()) {
                    // SCR.4.3.4.1.1 Bindings, Bound Values and State - skip
                    // reserved names
                    if (key.startsWith("javax.script")
                            || key.startsWith("com.pdmfc.tea")) {
                        continue;
                    }
                    SObjSymbol keySym = SObjSymbol.addSymbol(key);
                    //System.out.println("ENGINE_SCOPE to tea global " + key + "=" + b.get(key));
                    teaContext.newVar(keySym, com.pdmfc.tea.modules.reflect.SModuleReflect.java2Tea(b.get(key), teaContext));
                }
            }
            // SCR.4.3.4.1.2 Script Execution - specifies that the context should be
            // available to the script.
            // Together with the SCOPE_ENGINE attribute com.pdmfc.tea.engine.runtime
            // holding the STeaRuntime, it creates a circular reference.
            // But so far, having a circular reference seems better than
            // letting a SimpleScriptContext discard the STeaRuntime context.
            teaContext.newVar(
                    SObjSymbol.addSymbol("context"),
                    com.pdmfc.tea.modules.reflect.SModuleReflect.java2Tea(sc, teaContext));

            // TODO: SCR.4.3.1 ScriptContext - $stdin, $stdout and $stderr
            // should be initialized from the appropriate reader/writers
            // in the ScriptContext.

            // SCR.4.3.4.1.1 Bindings, Bound Values and State
            // Set argv, argv0, etc, from javax.script.argv, javax.script.filename, etc.
            // TODO - make the argv and argv0 cast errors give a more friendly error
            Object oArgv[] = (Object [])sc.getAttribute("javax.script.argv");
            if (oArgv != null) {
                String [] argv = new String[oArgv.length];
                try {
                    System.arraycopy(oArgv, 0, argv, 0, argv.length);
                } catch (ArrayStoreException e) {
                    throw new ScriptException("javax.script.argv must be an array of strings");
                }
                teaRuntime.setArgv(argv);
            }
            String argv0 = (String)sc.getAttribute("javax.script.filename");
            if (argv0 != null) {
                teaRuntime.setArgv0(argv0);
            }
        } catch (STeaException e) {
            throw new ScriptException(e);
        }
    }

    public void teaGlobals2Context(STeaRuntime teaRuntime, ScriptContext sc)
            throws ScriptException {
        try {
            SContext teaContext = teaRuntime.getToplevelContext();
            Bindings b = sc.getBindings(ScriptContext.ENGINE_SCOPE);
            Set<String> esKeys = null;
            if (b != null) {
                esKeys = b.keySet();
                for (String key : esKeys) {
                    // SCR.4.3.4.1.1 Bindings, Bound Values and State - skip
                    // reserved names
                    if (key.startsWith("javax.script")
                            || key.startsWith("com.pdmfc.tea")) {
                        continue;
                    }
                    SObjSymbol keySym = SObjSymbol.addSymbol(key);
                    try {
                        Object teaValue = teaContext.getVar(keySym);
                        Object javaValue = com.pdmfc.tea.modules.reflect.SModuleReflect.tea2Java(teaValue);
                        b.put(key, javaValue);
                    } catch (SNoSuchVarException ex) {
                        ;
                    }
                    //System.out.println("tea global to ENGINE_SCOPE " + key + "=" + b.get(key));
                }
            }
            b = sc.getBindings(ScriptContext.GLOBAL_SCOPE);
            if (b != null) {
                Set<String> gsKeys = b.keySet();
                // skip all keys that have been set on ENGINE_SCOPE
                //if (esKeys != null) {
                //    gsKeys.removeAll(esKeys);
                //}
                for (String key : gsKeys) {
                    // SCR.4.3.4.1.1 Bindings, Bound Values and State - skip
                    // reserved names
                    if (key.startsWith("javax.script")
                            || key.startsWith("com.pdmfc.tea")) {
                        continue;
                    }
                    SObjSymbol keySym = SObjSymbol.addSymbol(key);
                    try {
                        Object teaValue = teaContext.getVar(keySym);
                        Object javaValue = com.pdmfc.tea.modules.reflect.SModuleReflect.tea2Java(teaValue);
                        b.put(key, javaValue);
                    } catch (SNoSuchVarException ex) {
                        ;
                    }
                    //System.out.println("tea global to GLOBAL_SCOPE " + key + "=" + b.get(key));
                }
            }
        } catch (STeaException e) {
            throw new ScriptException(e);
        }
    }
}
