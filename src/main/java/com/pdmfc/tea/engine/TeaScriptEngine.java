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

import java.io.*;
import javax.script.*;

import com.pdmfc.tea.compiler.SCompiler;
import com.pdmfc.tea.runtime.STeaRuntime;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.STypes;
import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;

/**
 * Most of the functionality is implemented, except for
 * 2 Invocable methods.
 */
public class TeaScriptEngine extends AbstractScriptEngine
   implements Compilable, Invocable {

    //private static final String KEY_RUNTIME =
    //    "com.pdmfc.tea.runtime";

    /**
     * A single com.pdmfc.tea.compiler.SCompiler is used to compile
     * Tea code into an internal representation
     * com.pdmfc.tea.runtime.SCode.
     */
    protected SCompiler              _compiler;

    /**
     * Reference the factory that instantiated this object.
     */
    protected TeaScriptEngineFactory _factory;

    /**
     * Initialize _fatcory to null. 
     * As we are not allowed to throw a ScriptException, real
     * initialization is delayed until you try to evaluate some Tea
     * code.
     * @see TeaCompiledScript#eval(ScriptContext scriptContext)
     */
    public TeaScriptEngine() {
        this(null);
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
        super(new TeaBindings());
        //System.out.println("TeaScriptEngine(TeaScriptEngineFactory aFactory)");
        _factory = aFactory;
        // TODO: more constructors with importPath setup ?
    }

    /**
     * @return a new TeaBindings.
     */
    public Bindings createBindings() {
        //System.out.println("TeaScriptEngine.createBindings()");
        //return new SimpleBindings();
        return new TeaBindings();
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
        
        return evalBytes(script.getBytes(), scriptContext);
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
            while(reader.read(ca) >= 0) {
                cb.append(ca);
            }
        } catch (IOException e) {
            throw new ScriptException(e);
        }
        return evalBytes(cb.toString().getBytes(), scriptContext);
    }

    /**
     * Compiles <code>byte[]</code> as a Tea script and executes it.
     */
    public Object evalBytes(byte script[], ScriptContext scriptContext)
        throws ScriptException {
        CompiledScript code = compileBytes(script);
        return code.eval(scriptContext);
    }

    /**
     * @return _factory
     */
    public ScriptEngineFactory getFactory() {
        if ( _factory == null )
            _factory = new TeaScriptEngineFactory();
        return _factory;
    }

    public CompiledScript compile(Reader reader)
        throws ScriptException {
        StringBuffer cb = new StringBuffer();
        char ca[] = new char[4096];
        try {
            while(reader.read(ca) >= 0) {
                cb.append(ca);
            }
        } catch (IOException e) {
            throw new ScriptException(e);
        }
        return compile(cb.toString());
    }

    public CompiledScript compile(String script)
        throws ScriptException {
        return compileBytes(script.getBytes());
    }

    public CompiledScript compileBytes(byte script[])
        throws ScriptException {
        try {
            String scriptString = new String(script);
            return new TeaCompiledScript(this,getCompiler().compile(scriptString));
        } catch (Exception e) {
            throw new ScriptException(e);
        }
    }

    public Object invokeFunction(String name, Object ... args)
        throws ScriptException {
        SContext context = getMyRuntime().getToplevelContext();
        SObjSymbol nameSymbol = SObjSymbol.getSymbol(name);
        try {
            Object[] newArgs = new Object[args.length+1];
            newArgs[0] = nameSymbol;
            for(int i=0; i<args.length; i++) {
                newArgs[i+1]=args[i];
            }
            SObjFunction obj = STypes.getFunction(context, newArgs, 0);
            return obj.exec(obj, context, newArgs);
        } catch (STeaException e) {
            throw new ScriptException(e);
        }
    }

    public Object invokeMethod(Object thisz, String name, Object ... args)
        throws ScriptException {
        SContext context = getMyRuntime().getToplevelContext();
        SObjSymbol nameSymbol = SObjSymbol.getSymbol(name);
        try {
            Object[] newArgs = new Object[args.length+2];
            newArgs[0] = thisz;
            newArgs[1] = nameSymbol;
            for(int i=0; i<args.length; i++) {
                newArgs[i+2]=args[i];
            }
            SObjFunction obj = STypes.getFunction(context, newArgs, 0);
            return obj.exec(obj, context, newArgs);
        } catch (STeaException e) {
            throw new ScriptException(e);
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
        getMyRuntime().end();
    }

    /**
     * Get (and initialize if necessary) a
     * com.pdmfc.tea.runtime.STeaRuntime from the current engine
     * context.
     * @see #getRuntime(ScriptContext sc)
     */
    public STeaRuntime getMyRuntime() throws ScriptException {
        return TeaScriptEngine.getRuntime(getContext());
    }


    /**
     * Get (and initialize if necessary) a
     * com.pdmfc.tea.runtime.STeaRuntime from the given engine
     * context.
     * @see TeaBindings#getMyRuntime()
     * @throws ScriptException as Tea initialization code might be
     * called to initialize the runtime.
     */
    public static STeaRuntime getRuntime(ScriptContext sc)
        throws ScriptException {
    //    synchronized (sc) {
    //        STeaRuntime c = (STeaRuntime)sc.getAttribute(KEY_RUNTIME,
    //                                         ScriptContext.ENGINE_SCOPE);
    //        // on the 1st tine instantiate an STeaRuntime
    //        if (c == null) {
    //            try {
    //                c = new STeaRuntime();
    //                sc.setAttribute(KEY_RUNTIME, c,
    //                                ScriptContext.ENGINE_SCOPE);
    //                // setup import paths and readers/writers from other
    //                // properties ?
    //                // context.start() ? No ? In TeaCompiledScript.eval ?
    //                // context.stop(); - put where ?
    //                // context.end(); - put where ?
    //            } catch (STeaException e) {
    //                throw new ScriptException(e);
    //            }
    //        }
    //        return c;    
    //    }
        return ((TeaBindings)(sc.getBindings(ScriptContext.ENGINE_SCOPE))).getMyRuntime();
    }
}
