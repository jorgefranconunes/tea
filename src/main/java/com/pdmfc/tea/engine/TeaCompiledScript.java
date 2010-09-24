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
        try {
            // TODO: init/get STeaRuntime from ScriptContext
            TeaBindings b = (TeaBindings)scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
            STeaRuntime context    = b.getMyRuntime();
            //STeaRuntime context    = TeaScriptEngine.getRuntime(scriptContext);
            context.start();

            // put Bindings as global vars.
            //Bindings b = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
            //if (b != null) {
            //    for(Iterator i=b.keySet().iterator(); i.hasNext(); ) {
            //        String key = (String)i.next();
            //        SObjSymbol keySym = SObjSymbol.addSymbol(key);
            //        context.newVar(keySym, b.get(key));
            //    }
            //}

            Object result = null;
            result = context.execute(_code); // Tea 3.1.2 or higher.

            // retrived updated global vars to Bindings.
            //if (b != null) {
            //    for(Iterator i=b.keySet().iterator(); i.hasNext(); ) {
            //        String key = (String)i.next();
            //        SObjSymbol keySym = SObjSymbol.addSymbol(key);
            //        b.put(key, context.getVar(keySym));
            //    }
            //}
            context.stop();
            // context.end(); -- not here. On servlet unloading ?
            return result;
        } catch (Exception e) {
            throw new ScriptException(e);
        }
    }
}
