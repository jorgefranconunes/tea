/**************************************************************************
 *
 * Copyright (c) 2007-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.engine;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.reflect.STeaJavaTypes;
import com.pdmfc.tea.TeaScript;





/**************************************************************************
 *
 * A JSR-223 <code>javax.script.CompiledScript</code>
 * wrapper around an {@link TeaCode}.
 * It associates the compiled code with the engine
 * that instantiated this object.
 * 
 * @since 4.0.0
 *
 **************************************************************************/

public final class TeaCompiledScript
    extends CompiledScript {





    private TeaScriptEngine _engine;
    private TeaScript       _code;





    /**
     * Constructor for this object.
     * 
     * @param engine The engine that has instantiated this object.
     * @param code The compiled code wrapped by this class.
     */
    public TeaCompiledScript(final TeaScriptEngine engine,
                             final TeaScript       code) {
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
            _engine.context2TeaGlobals(scriptContext);

            Object result = _code.execute();

            return STeaJavaTypes.tea2Java(result);
        } catch (TeaException e) {
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

