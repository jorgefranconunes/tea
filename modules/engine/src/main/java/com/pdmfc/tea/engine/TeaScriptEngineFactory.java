/**************************************************************************
 *
 * Copyright (c) 2007-2012 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.engine;

import java.util.List;
import java.util.Arrays;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import com.pdmfc.tea.TeaConfig;
import com.pdmfc.tea.runtime.TeaRuntime;
import com.pdmfc.tea.runtime.TeaRuntimeConfig;
import com.pdmfc.tea.runtime.TeaRuntimeFactory;





/**************************************************************************
 *
 * The official factory provided for instantiating a {@link
 * TeaScriptEngine} from a
 * <code>javax.script.ScriptEngineManager</code>.
 * 
 * @since 4.0.0
 *
 **************************************************************************/

public final class TeaScriptEngineFactory
    extends Object
    implements ScriptEngineFactory {





    final private List<String> _extensions = Arrays.asList("tea");
    
    final private List<String> _mimeTypes = Arrays.asList("application/x-tea");

    final private List<String> _names = Arrays.asList("tea", "Tea", "Tea Engine");

    private TeaRuntimeFactory _teaRuntimeFactory =
        new TeaRuntimeFactory();





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaScriptEngineFactory() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * @return A string, "Tea Engine".
 *
 **************************************************************************/

    @Override
    public String getEngineName() {

        return "Tea Engine";
    }





/**************************************************************************
 *
 * @return A string in the format "x.y.z". Since Tea 4, this value is
 * the same as the Tea version.
 *
 **************************************************************************/

    @Override
    public String getEngineVersion() {

        return TeaConfig.get("com.pdmfc.tea.version");
    }





/**************************************************************************
 *
 * @return A List<String> with ("tea", "Tea", "Tea Engine").
 *
 **************************************************************************/

    @Override
    public List<String> getExtensions() {

        return _extensions;
    }





/**************************************************************************
 *
 * @return A List<String> with ("application/x-tea").
 *
 **************************************************************************/

    @Override
    public List<String> getMimeTypes() {

        return _mimeTypes;
    }





/**************************************************************************
 *
 * @return A List<String> with ("tea").
 *
 **************************************************************************/

    @Override
    public List<String> getNames() {

        return _names;
    }





/**************************************************************************
 *
 * @return A string, "Tea".
 *
 **************************************************************************/

    @Override
    public String getLanguageName() {

        return "Tea";
    }





/**************************************************************************
 *
 * @return A string in the format "x.y.z" containing the Tea version
 * number (which is now the same as the Tea Engine version number).
 *
 **************************************************************************/

    @Override
    public String getLanguageVersion() {

        return TeaConfig.get("com.pdmfc.tea.version");
    }





 /**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Override
   public Object getParameter(final String aKey) {

       Object result = null;

        if ( aKey.equals(ScriptEngine.ENGINE) ) {
            result = getEngineName();
        } else if ( aKey.equals(ScriptEngine.ENGINE_VERSION) ) {
            result = getEngineVersion();
        } else if ( aKey.equals(ScriptEngine.NAME) ) {
            result = getEngineName();
        } else if ( aKey.equals(ScriptEngine.LANGUAGE) ) {
            result =  getLanguageName();
        } if ( aKey.equals(ScriptEngine.LANGUAGE_VERSION) ) {
            result = getLanguageVersion();
        } else if ( aKey.equals("THREADING") ) {
            result = null;
        }
        
        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Override
    public String getMethodCallSyntax(final String objectName,
                                      final String methodName,
                                      final String ... args) {

        StringBuffer sb = new StringBuffer();
        if ( objectName != null ) {
            sb.append("$" + objectName);
        }
        sb.append(" " + methodName);
        if ( args.length > 0 ) {
            sb.append(" ");
        }
        // TODO: seems to me (jpsl) that some special
        // encodings might be missing.
        for( int i=0; i<args.length; i++ ) {
            sb.append(((args[i] == null) ? "$null" : args[i]) 
                      + (i<(args.length-1) ? " " : ""));
        }
        return sb.toString();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Override
    public String getOutputStatement(final String message) {

        // TODO: really escape \ and " in the message ?
        // what about \b,\b, etc... (see Tea's str-unescape).
        return "echo \"" +
                message.replaceAll("\"","\\\"").replaceAll("\\","\\\\") +
                "\"";
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Override
    public String getProgram(final String ... statements) {

        StringBuffer sb = new StringBuffer();
        for( int i=0; i< statements.length; i++ ) {
                sb.append( statements[i] );
                sb.append("\n");
        }
        return sb.toString();
    }





/**************************************************************************
 *
 * Tea runtime initialization is delayed until you try to evaluate
 * some Tea code.
 *
 * @see TeaCompiledScript#eval(ScriptContext scriptContext)
 *
 **************************************************************************/

    @Override
    public ScriptEngine getScriptEngine() {

        return new TeaScriptEngine(this);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaRuntime newTeaRuntime(final TeaRuntimeConfig config) {

        TeaRuntime result = _teaRuntimeFactory.newTeaRuntime(config);

        return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/


