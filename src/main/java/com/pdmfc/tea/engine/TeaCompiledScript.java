/**************************************************************************
 *
 * Copyright (c) 2007-2012 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.engine;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SCode;
import com.pdmfc.tea.modules.reflect.STeaJavaTypes;
import com.pdmfc.tea.runtime.STeaRuntime;





/**************************************************************************
 *
 * A JSR-223 <code>javax.script.CompiledScript</code>
 * wrapper around an {@link SCode}.
 * It associates the compiled code with the engine
 * that instantiated this object.
 * 
 * @since 4.0.0
 *
 **************************************************************************/

public final class TeaCompiledScript
    extends CompiledScript {





    private TeaScriptEngine    _engine;
    private SCode              _code;





    /**
     * Constructor for this object.
     * 
     * @param engine The engine that has instantiated this object.
     * @param code The compiled code wrapped by this class.
     */
    public TeaCompiledScript(final TeaScriptEngine engine,
                             final SCode code) {
        _engine = engine;
        _code   = code;
    }





    /**
     * @return the engine that created this object.
     */
    public ScriptEngine getEngine() {
        return _engine;
    }





    /**
     * Executes this compiled script in the given script context.
     *
     * See {@link TeaScriptEngine#eval(String, ScriptContext)}
     * for more details on script execution.
     */
    public Object eval(final ScriptContext scriptContext)
        throws ScriptException {

        try {
            //System.out.println("teaCompiledScript.eval("+scriptContext+")");

            STeaRuntime teaRuntime = _engine.context2TeaGlobals(scriptContext);

            //TeaBindings b = (TeaBindings)scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
            //STeaRuntime context    = b.getMyRuntime();
            //STeaRuntime context    = TeaScriptEngine.getRuntime(scriptContext);
            //System.out.println("eval TeaRuntime="+teaRuntime);

            // put Bindings as global vars, and prepare the context for execution
            

            // Run the code
            Object result = teaRuntime.execute(_code); // Tea 3.1.2 or higher.

            return STeaJavaTypes.tea2Java(result);
        } catch (STeaException e) {
            //System.out.println("eval exception "+e.getMessage());
            //for(StackTraceElement ste : e.getStackTrace()) {
            //    System.out.println(ste.toString());
            //}
            //throw TeaScriptEngine.teaException2ScriptException(e);
            throw new ScriptException(e);
        } finally {
            // retrived updated global vars to Bindings.
            _engine.teaGlobals2Context(scriptContext);
        }
    }
}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

