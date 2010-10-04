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

import javax.script.*;

import com.pdmfc.tea.compiler.SCode;
import com.pdmfc.tea.runtime.STeaRuntime;

public class TeaCompiledScript extends CompiledScript {

    private TeaScriptEngine    _engine;
    private SCode              _code;

    public TeaCompiledScript(TeaScriptEngine engine, SCode code) {
        _engine = engine;
        _code   = code;
    }

    public ScriptEngine getEngine() {
        return _engine;
    }


    /**
     * Initialization of the Tea runtime environment is only performed
     * when this method is 1st called for one engine. So, please keep
     * in mind that, in this case a lot more Tea code might be
     * executed, other than
     * the one you are calling eval for.
     */
    public Object eval(ScriptContext scriptContext)
        throws ScriptException {
        STeaRuntime teaRuntime    = _engine.getRuntime(scriptContext);
        try {
            //System.out.println("teaCompiledScript.eval("+scriptContext+")");
            // TODO: init/get STeaRuntime from ScriptContext
            //TeaBindings b = (TeaBindings)scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
            //STeaRuntime context    = b.getMyRuntime();
            //STeaRuntime context    = TeaScriptEngine.getRuntime(scriptContext);
            //System.out.println("eval TeaRuntime="+teaRuntime);

            teaRuntime.start();

            // put Bindings as global vars.
            _engine.context2TeaGlobals(teaRuntime, scriptContext);

            // Run the code
            Object result = null;
            result = teaRuntime.execute(_code); // Tea 3.1.2 or higher.

            return result;
        } catch (Exception e) {
            //System.out.println("eval exception "+e.getMessage());
            //for(StackTraceElement ste : e.getStackTrace()) {
            //    System.out.println(ste.toString());
            //}
            throw new ScriptException(e);
        } finally {
            // retrived updated global vars to Bindings.
            _engine.teaGlobals2Context(teaRuntime, scriptContext);
            teaRuntime.stop();
            // teaRuntime.end(); -- not here. On servlet unloading ?
        }
    }
}
