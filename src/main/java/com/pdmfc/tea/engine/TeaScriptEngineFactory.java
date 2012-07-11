/**************************************************************************
 *
 * Copyright (c) 2007 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
 *
 * Revisions:
 *
 * 2006/12/24 Created. (jpsl)
 *
 **************************************************************************/

package com.pdmfc.tea.engine;

import java.util.List;
import java.util.Arrays;
import javax.script.*;

import com.pdmfc.tea.SConfigInfo;

/**
 * The official factory provided for instantiating a {@link TeaScriptEngine} from
 * a <code>javax.script.ScriptEngineManager</code>.
 * 
 * @since 4.0.0
 *
 */
public class TeaScriptEngineFactory implements ScriptEngineFactory {

    final List<String> _extensions = Arrays.asList("tea");
    
    final List<String> _mimeTypes = Arrays.asList("application/x-tea");

    final List<String> _names = Arrays.asList("tea", "Tea", "Tea Engine");

    public TeaScriptEngineFactory() {
    }

    /**
     * @return A string, "Tea Engine".
     */
    public String getEngineName() {
        return "Tea Engine";
    }

    /**
     * @return A string in the format "x.y.z". Since Tea 4, this
     * value is the same as the Tea version.
     */
    public String getEngineVersion() {
        // Since Tea 4, that the engine version is the same as the language version
        return SConfigInfo.getProperty("com.pdmfc.tea.version");
    }

    /**
     * @return A List<String> with ("tea", "Tea", "Tea Engine").
     */
    public List<String> getExtensions() {
        return _extensions;
    }

    /**
     * @return A List<String> with ("application/x-tea").
     */
    public List<String> getMimeTypes() {
        return _mimeTypes;
    }

    /**
     * @return A List<String> with ("tea").
     */
    public List<String> getNames() {
        return _names;
    }

    /**
     * @return A string, "Tea".
     */
    public String getLanguageName() {
        return "Tea";
    }

    /**
     * @return A string in the format "x.y.z" containing the Tea
     * version number (which is now the same as the Tea Engine
     * version number).
     */
    public String getLanguageVersion() {
        return SConfigInfo.getProperty("com.pdmfc.tea.version");
    }


    public Object getParameter(String aKey) {
        if (aKey.equals(ScriptEngine.ENGINE))
            return getEngineName();
        if (aKey.equals(ScriptEngine.ENGINE_VERSION))
            return getEngineVersion();
        if (aKey.equals(ScriptEngine.NAME))
            return getEngineName();
        if (aKey.equals(ScriptEngine.LANGUAGE))
            return getLanguageName();
        if (aKey.equals(ScriptEngine.LANGUAGE_VERSION))
            return getLanguageVersion();
        if (aKey.equals("THREADING"))
            return null;
        
        return null;
    }

    public String getMethodCallSyntax( 
        String objectName, String methodName, String ... args
    ) {
        StringBuffer sb = new StringBuffer();
        if ( objectName != null )
            sb.append("$" + objectName);
        sb.append(" " + methodName);
        if ( args.length > 0 )
            sb.append(" ");
        // TODO: seems to me (jpsl) that some special
        // encodings might be missing.
        for( int i=0; i<args.length; i++ )
            sb.append(((args[i] == null) ? "$null" : args[i]) 
                      + (i<(args.length-1) ? " " : ""));
        return sb.toString();
    }

    public String getOutputStatement( String message ) {
        // TODO: really escape \ and " in the message ?
        // what about \b,\b, etc... (see Tea's str-unescape).
        return "echo \"" +
                message.replaceAll("\"","\\\"").replaceAll("\\","\\\\") +
                "\"";
    }

    public String getProgram(String ... statements) {
        StringBuffer sb = new StringBuffer();
        for( int i=0; i< statements.length; i++ ) {
                sb.append( statements[i] );
                sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Tea runtime initialization is delayed until you try to evaluate some Tea
     * code.
     * @see TeaCompiledScript#eval(ScriptContext scriptContext)
     */
    public ScriptEngine getScriptEngine() {
        return new TeaScriptEngine(this);
    }

}
