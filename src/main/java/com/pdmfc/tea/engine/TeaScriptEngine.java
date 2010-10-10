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

import com.pdmfc.tea.compiler.SCompileException;
import com.pdmfc.tea.runtime.SNoSuchVarException;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.*;

import com.pdmfc.tea.compiler.SCompiler;
import com.pdmfc.tea.runtime.STeaRuntime;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.STypes;
import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SCode;
import com.pdmfc.tea.runtime.SContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

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
     * <code>com.pdmfc.tea.engine.runtime</code><br />
     * Reserved binding name for the STeaRuntime.
     * Every ScriptContext has an associated STeaRuntime stored
     * with this key name in the bindings at ENGINE_SCOPE level.
     * <p><b>Do not attempt to set it.</b> Its purpose is to preserve
     * the full Tea runtime context associated with the ScriptContext,
     * and eventually allow the java programmer to access
     * the STeaRuntime object after the 1st script execution. </p>
     */
    public static final String KEY_RUNTIME =
            "com.pdmfc.tea.engine.runtime";  
    
    /**
     * <code>com.pdmfc.tea.engine.library</code><br />
     * Reserved binding name for a list of import paths that will make up the
     * the TEA_LIBRARY. The value has the same syntax as the
     * <code>tsh --library</code> command line option: a String with a list
     * of colon separated paths (or URLs).
     * Set this attribute at ENGINE_SCOPE or GLOBAL_SCOPE to define the
     * TEA_LIBRARY import path.
     * <p>This is redundant with modifying the global <code>TEA_LIBRARY</code>
     * Tea variable, which is a list import paths.</p>
     */
    public static final String KEY_LIBRARY =
            "com.pdmfc.tea.engine.library";

    /**
     * <code>com.pdmfc.tea.engine.encoding</code><br />
     * Reserved binding name for changing the encoding of imported Tea files.
     * Has the same syntax as the
     * <code>tsh --encoding</code> command line option: a string with the name
     * of a java.nio.Charset.
     * <p>
     * Examples: UTF-8 or ISO-8859-1
     * </p>
     * <p>If used, it <b>must be set before the 1st execution</b> of any script
     * in the associated ScriptContext. Any changes made to this attribute
     * afterwards, have no effect.</p>
     */
    public static final String KEY_ENCODING =
            "com.pdmfc.tea.engine.encoding";

    /**
     * A single com.pdmfc.tea.compiler.SCompiler is used to compile
     * Tea code into an internal representation
     * com.pdmfc.tea.runtime.SCode.
     */
    protected SCompiler _compiler;

    /**
     * A private empty compiled code. Used internally
     * to force initialization of the STeaRuntime for the 1st time.
     */
    protected SCode _emptyCode;

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
        try {
            _compiler = new SCompiler();
            _emptyCode = _compiler.compile("");
        } catch (SCompileException ex) {
            // This can never happen. But we can't go on if it does.
            throw new java.lang.RuntimeException(ex);
        } catch (IOException ex) {
            // This can never happen. But we can't go on if it does.
            throw new java.lang.RuntimeException(ex);
        }
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
        this();
        //System.out.println("TeaScriptEngine(TeaScriptEngineFactory aFactory)");
        _factory = aFactory;
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
        return _compiler;
    }


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
        STeaRuntime teaRuntime = this.context2TeaGlobals(this.getContext());
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
            this.teaGlobals2Context(this.getContext());
        }
    }

    public Object invokeMethod(Object thisz, String name, Object... args)
            throws ScriptException {
        STeaRuntime teaRuntime = this.context2TeaGlobals(this.getContext());
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
            return result;
        } catch (STeaException e) {
            throw new ScriptException(e);
        } finally {
            // retrived updated global vars to Bindings.
            this.teaGlobals2Context(this.getContext());
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
        this.getTeaRuntime().end();
    }


    /**
     * Get the STeaRuntime for the current context.
     * Convinience method that returns the same as
     * <code>getTeaRuntime(getContext())</code>.
     */
    public STeaRuntime getTeaRuntime() throws ScriptException {
        return this.getTeaRuntime(this.getContext());
    }
    
    /**
     * Get a STeaRuntime from the given engine context.
     * If non exists, create a new one.
     * The STeaRuntime is stored in the script context, in the ENGINE_SCOPE
     * using the reserved attribute name "com.pdmfc.tea.engine.teaRuntime".
     * @throws ScriptException as Tea initialization code might be
     * called to initialize the runtime.
     */
    public STeaRuntime getTeaRuntime(ScriptContext sc) throws ScriptException {
        synchronized (sc) {
            STeaRuntime teaRuntime = (STeaRuntime) sc.getAttribute(KEY_RUNTIME, ScriptContext.ENGINE_SCOPE);
            if (teaRuntime == null) {
                teaRuntime = new STeaRuntime();
                //System.out.println("Setting a new runtime on ENGINE_SCOPE of "+sc);
                sc.setAttribute(KEY_RUNTIME, teaRuntime, ScriptContext.ENGINE_SCOPE);
            }
            return teaRuntime;
        }
    }


    /* This internal method out to be called before execution of Tea code
     * completes inside this ScriptContext.
     */
    public STeaRuntime context2TeaGlobals(ScriptContext sc)
            throws ScriptException {
        try {
            STeaRuntime teaRuntime = this.getTeaRuntime(sc);
            // prepare the context for execution of code.

            // TODO: SCR.4.3.1 ScriptContext - $stdin, $stdout and $stderr
            // should be initialized from the appropriate reader/writers
            // in the ScriptContext.

            // SCR.4.3.4.1.1 Bindings, Bound Values and State
            // Set argv, argv0, etc, from javax.script.argv, javax.script.filename, etc.
            // TODO - make the argv and argv0 cast errors give a more friendly error
            Object oArgv[] = (Object[]) sc.getAttribute("javax.script.argv");
            if (oArgv != null) {
                String[] argv = new String[oArgv.length];
                try {
                    System.arraycopy(oArgv, 0, argv, 0, argv.length);
                } catch (ArrayStoreException e) {
                    throw new ScriptException("javax.script.argv must be an array of strings");
                }
                teaRuntime.setArgv(argv);
            }
            String argv0 = (String) sc.getAttribute("javax.script.filename");
            if (argv0 != null) {
                teaRuntime.setArgv0(argv0);
            }

            // Set import library from com.pdmfc.tea.engine.library
            // TODO: talk with jfn to open STeaLauncherArgs.optionSetLibary()
            String libraryStr = (String) sc.getAttribute(KEY_LIBRARY);
            if (libraryStr != null) {
                String pathSep = File.pathSeparator;
                StringTokenizer i = new StringTokenizer(libraryStr, pathSep);
                List<String> libraryList = new ArrayList<String>();
                while (i.hasMoreTokens()) {
                    String path = i.nextToken();
                    path = path.replace('|', ':');
                    libraryList.add(path);
                }
                teaRuntime.setImportLocations(libraryList);
            }

            // Set source encoding com.pdmfc.tea.engine.encoding
            String encoding = (String) sc.getAttribute(KEY_ENCODING);
            if (encoding != null) {
                teaRuntime.setSourceEncoding(encoding);
            }

            // argv0 and argv must be set before.
            teaRuntime.start();
            // Run an empty code to force 1st time initialization
            // before calling SModuleReflect.java2Tea.
            // (There is no public interface for doing so in STeaRuntime.)
            // Otherwise we will get errors for some convertions resulting
            // in STosObj like SDate.
            try {
                teaRuntime.execute(_emptyCode);
            } catch (STeaException ex) {
                throw new ScriptException(ex);
            }

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

            return teaRuntime;
        } catch (STeaException e) {
            throw new ScriptException(e);
        }
    }

    /* This internal method out to be called after the execution of Tea code
     * completes inside this ScriptContext.
     */
    public void teaGlobals2Context(ScriptContext sc)
            throws ScriptException {

        STeaRuntime teaRuntime = this.getTeaRuntime(sc);
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
        } finally {
            // no more Tea code ought to be executed without calling start
            try {
                teaRuntime.stop();
            } catch (IllegalArgumentException ex) {
                // If an error ocurred before initialization of STeaRuntime
                // we ignore it, but we attempted to stop it anyway.
                throw new ScriptException(ex);
            }
            // teaRuntime.end(); -- not here. On servlet unloading ?
        }
    }
}
