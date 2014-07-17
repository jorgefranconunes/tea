/**************************************************************************
 *
 * Copyright (c) 2007-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.engine;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.SimpleBindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.compiler.TeaCompileException;
import com.pdmfc.tea.runtime.Args;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.SNoSuchVarException;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.TeaRunException;
import com.pdmfc.tea.runtime.TeaRuntime;
import com.pdmfc.tea.runtime.TeaRuntimeConfig;
import com.pdmfc.tea.runtime.TeaScript;
import com.pdmfc.tea.modules.reflect.STeaJavaTypes;





/**************************************************************************
 *
 * The official implementation of the
 * <code>javax.script.ScriptEngine</code> for Tea 4.
 * 
 * This the core class of this implementation of JSR-223 for Tea.
 * 
 * <p>The use of <code>javax.script.SimpleScriptContext</code> is
 * supported, and the full execution context ({@link TeaRuntime}) is
 * saved/restored from the <code>ScriptContext</code> using the
 * reserved attribute name {@link #KEY_RUNTIME}.
 * 
 * <p>Unlike the Rhino engine, the <code>TeaScriptEngine</code>
 * doesn't allow the copying
 * of function (lambda) values into the <code>GLOBAL_SCOPE</code>.
 * (In fact, the copying
 * of any function (lambda) values between scopes should not be allowed,
 * as a function value in Tea is tightly coupled with the scope environment
 * where the function was created.) So, in order to avoid problems, the author
 * recommends that you only put/get simple data values (strings or numerics)
 * into/out of bindings - which are mapped into/from Tea global variables
 * with the same name as the attribute name.
 * 
 * <p>A Tea global variable named <code>context</code>
 * (as required by "SCR.4.3.4.1.2 Script Execution")
 * is introduced. It is a TOS wrapper aroung the <code>ScriptContext</code>
 * object. It behaves just like a regular TOS object, with the methods
 * and fields defined by the <code>javax.script.ScriptContext</code> interface.
 * 
 * <p>See the examples in the programmer's guide (part of the Tea documentation)
 * for more use cases.
 * 
 * <p>Tea script execution is not thread-safe,
 * but you can instantiate as many <code>TeaScriptEngine</code>s as needed
 * for concurrent execution of Tea scripts.
 * 
 * @since 4.0.0
 *
 **************************************************************************/

public final class TeaScriptEngine
    extends AbstractScriptEngine
    implements Compilable {





    /**
     * Reserved binding name which holds the {@link TeaRuntime} for
     * the current <code>ScriptContext</code>.  Every
     * <code>ScriptContext</code> has an associated
     * <code>TeaRuntime</code> stored with this key name in the
     * bindings at <code>ENGINE_SCOPE</code> level.
     *
     * <p><b>Do not attempt to set it.</b> Its purpose is to preserve
     * the full Tea runtime context associated with the
     * <code>ScriptContext</code>, and eventually allow the java
     * programmer to access the <code>TeaRuntime</code> object after
     * the 1st script execution.
     */
    public static final String KEY_RUNTIME =
            "com.pdmfc.tea.engine.runtime";  
    
    /**
     * Reserved binding name for a list of import paths that will make up the
     * the <code>TEA_LIBRARY</code>. The value has the same syntax as the
     * <code>tsh --library</code> command line option: a String with a list
     * of colon separated paths (or URLs).
     * Set this attribute at <code>ENGINE_SCOPE</code>
     * or <code>GLOBAL_SCOPE</code> to define the
     * <code>TEA_LIBRARY</code> import path.
     *
     * <p>This is redundant with modifying the global
     * <code>TEA_LIBRARY</code> Tea variable, which is a list import
     * paths.
     */
    public static final String KEY_LIBRARY =
            "com.pdmfc.tea.engine.library";

    /**
     * Reserved binding name for changing the encoding of imported Tea
     * files.  Has the same syntax as the <code>tsh --encoding</code>
     * command line option: a string with the name of a
     * java.nio.Charset.
     *
     * <p>Examples of accepted values: UTF-8 or ISO-8859-1
     * 
     * <p>If used, it <b>must be set before the 1st execution</b> of
     * any script in the associated <code>ScriptContext</code>.  Any
     * changes made to this attribute afterwards, have no effect.
     */
    public static final String KEY_ENCODING =
            "com.pdmfc.tea.engine.encoding";

    private TeaScriptEngineFactory _factory = null;





/**************************************************************************
 *
 * This is the constructor that gets called when the engine is
 * instantiated through a
 * <code>javax.script.ScriptEngineManager</code>.
 *
 * @param aFactory  The {@link #_factory} field is initialized with this
 * instance of {@link TeaScriptEngineFactory} that instantiated this object.
 *
 * @see #TeaScriptEngine()
 **************************************************************************/

    public TeaScriptEngine(final TeaScriptEngineFactory factory) {

        _factory = factory;
    }





/**************************************************************************
 *
 * Create an empty <code>javax.script.Bindings</code> using this
 * engine's preferred implementation.
 * 
 * "SCR.4.3.4.1.1 Bindings, Bound Values and State" requires that a
 * compliant engine accepts any implementation of a
 * <code>javax.script.Bindings</code>.  As such, at this time, this
 * engine provides a <code>javax.script.SimpleBindings</code> as an
 * implementation of its preferred <code>javax.script.Bindings</code>.
 *
 * @return A new (and empty) <code>javax.script.SimpleBindings</code>.
 **************************************************************************/

    @Override
    public Bindings createBindings() {

        return new SimpleBindings();
    }





/**************************************************************************
 *
 * Evaluates the given Tea script in the given runtime context.
 *
 * <p>If its the first time that this <code>scriptContext</code> is
 * used, the Tea runtime initialization is performed (this usually
 * means that the scripts in the <code>init.tea</code> files in the
 * import paths are executed).</p>
 *
 * <p>Before the script execution begins, all atributes in the
 * <code>GLOBAL_SCOPE</code> and <code>ENGINE_SCOPE</code> of the
 * <code>scriptContext</code> are processed to setup the Tea execution
 * environment.  See the list of public static KEY fields in this
 * class that represent special attribute names for
 * <code>ScriptContext</code>.</p>
 *
 * <p>Non-reserved attribute names are converted into Tea global
 * variables with the same name.  The values of the attributes are
 * converted into Tea using {@link
 * com.pdmfc.tea.modules.reflect.STeaJavaTypes#java2Tea(java.lang.Object,
 * com.pdmfc.tea.runtime.TeaContext)}.  (The top level Tea context
 * associated with the <code>scriptContext</code> is used for fetching
 * TOS classes when converting some specific classes of java objects
 * to TOS objects.  Ex: a <code>java.util.Date</code> is converted to
 * a TOS <code>TDate</code>).</p>
 * 
 *
 * <p>A global variable named <code>context</code> is made available
 * to the Tea script, as a Tea object wrapper to the
 * <code>scriptContext</code> parameter (as required by "SCR.4.3.4.1.2
 * Script Execution").</p>
 *
 * <p>After the script execution ends (regardless if a
 * <code>ScriptException</code> is thrown or not), the values of the
 * Tea global variables are fetched from the internal {@link
 * TeaRuntime} context, and converted back to their correspondent
 * attribute bindings using {@link
 * STeaJavaTypes#tea2Java(java.lang.Object) }.  This means that only
 * attributes that have been <code>put</code> before script execution
 * are fetched from the Tea global variable space. (The method
 * <code>javax.script.Engine.get</code> does not work for fetching any
 * value that was not <code>put</code> before execution of this
 * method).  This also means that if an unchecked exception is thrown,
 * no attributes are updated.</p>
 * 
 *
 * @param script a string with the Tea script to evaluate.
 * 
 * @param scriptContext the execution context.  This implementation
 * accepts a plain <code>javax.script.SimpleScriptContext</code>.  The
 * internal {@link TeaRuntime} is initialized in the first execution
 * using this <code>scriptContext</code>, and it is set as an
 * attribute at the <code>ENGINE_SCOPE</code> level with the name
 * defined by {@link #KEY_RUNTIME}.
 * 
 * @return The result of the last script instruction evaluated,
 * converted to java using {@link
 * STeaJavaTypes#tea2Java(java.lang.Object) }.
 * 
 * @throws ScriptException if a checked exception, or a script
 * execution error is caught during the evaluation of the script. Use
 * the <code>getCause</code> method to obtain the underlying
 * exception.  See {@link
 * #getFullMessage(javax.script.ScriptException) } for details on
 * obtaining the Tea stack trace (for Tea script errors).
 *
 **************************************************************************/

    @Override
    public Object eval(final String        script,
                       final ScriptContext scriptContext)
        throws ScriptException {

        CompiledScript code   = compile(script);
        Object         result = code.eval(scriptContext);

        return result;
    }





/**************************************************************************
 *
 * Reads the file into memory, and converts it to a string for
 * evaluation using {@link #eval(String, ScriptContext)}.  The
 * <code>reader</code> is not closed.
 *
 * @param reader From where the script will be read.
 *
 * @param scriptContext  The context for script execution.
 *
 * @return An <code>Object</code> with the result of the script
 * execution.
 *
 * @throws ScriptException Besides any script execution exception, any
 * <code>java.io.IOException</code> caught while using the
 * <code>reader</code> is also converted to a
 * <code>ScriptException</code>.
 *
 * @see #eval(java.lang.String, javax.script.ScriptContext)
 *
 **************************************************************************/

    @Override
    public Object eval(final Reader        reader,
                       final ScriptContext scriptContext)
        throws ScriptException {
        
        StringBuffer cb = new StringBuffer();
        char[]       ca = new char[4096];
        try {
            while (reader.read(ca) >= 0) {
                cb.append(ca);
            }
        } catch (IOException e) {
            throw new ScriptException(e);
        }
        return this.eval(cb.toString(), scriptContext);
    }





/**************************************************************************
 *
 * Gets the factory that was used to instantiate this engine.  Create
 * a new one, if this engine was instantiated directly.
 * 
 * @return {@link #_factory}. If {@link #_factory} was null (such as
 * when this engine was created using the default constructor) a new
 * {@link TeaScriptEngineFactory} is instantiated and {@link
 * #_factory} updated before returning.
 *
 **************************************************************************/

    @Override
    public ScriptEngineFactory getFactory() {

        return _factory;
    }





/**************************************************************************
 *
 * Read a script and compile it.
 *
 * @param reader used to read the script into a string.
 *
 * @return an instance of a {@link TeaCompiledScript}
 *
 * @throws ScriptException any parsing or compilation errors are wrapped by a
 * <code>javax.script.ScriptException</code>.
 *
 **************************************************************************/

    @Override
    public CompiledScript compile(final Reader reader)
        throws ScriptException {

        StringBuffer cb = new StringBuffer();
        char[]       ca = new char[4096];

        try {
            while (reader.read(ca) >= 0) {
                cb.append(ca);
            }
        } catch (IOException e) {
            throw new ScriptException(e);
        }
        return this.compile(cb.toString());
    }





/**************************************************************************
 *
 * Compile a script.
 *
 * @param scriptString a string with the complete Tea script source to
 * compile.
 *
 * @return an instance of a {@link TeaCompiledScript}.
 *
 * @throws ScriptException any parsing compilation errors are
 * wrapped by a <code>javax.script.ScriptException</code>.
 *
 **************************************************************************/

    @Override
    public CompiledScript compile(final String scriptString)
        throws ScriptException {

        TeaScript script = null;

        try {
            script = compileFromString(scriptString);
        } catch ( TeaCompileException e ) {
            throw new ScriptException(e);
        }

        TeaCompiledScript compiledScript = new TeaCompiledScript(this, script);

        return compiledScript;
    }





/**************************************************************************
 *
 * The {@link TeaRuntime#end()} method should be called before
 * unloading, so that any Tea code callbacks may be called to free
 * allocated resources.
 *
 * As JSR-223 does not support this feature, this method is left as an
 * extension for the programmer to call it explicitely.  (If the Tea
 * code does not defines any callbacks to release resources - and as
 * current Tea version does not track and closes open files forgotten
 * by the programmer - no real harm is done by not calling this method
 * yet.)
 *
 * @throws ScriptException
 *
 **************************************************************************/

    public void end()
        throws ScriptException {

        getTeaRuntime().end();
    }





/**************************************************************************
 *
 * Gets the {@link TeaRuntime} for the current context.
 *
 * Convinience method that calls {@link
 * #getTeaRuntime(javax.script.ScriptContext)} using
 * <code>this.getContext()</code> as an argument.
 *
 * @return The <code>TeaRuntime</code> associated with the default
 * context.
 *
 * @throws ScriptException
 *
 **************************************************************************/

    private TeaRuntime getTeaRuntime()
        throws ScriptException {

        return getTeaRuntime(getContext());
    }
    




/**************************************************************************
 *
 * Get a {@link TeaRuntime} from the given
 * <code>ScriptContext</code>. If none exists, create a new one.
 *
 * <p>The <code>TeaRuntime</code> is stored in the script context, in
 * the <code>ENGINE_SCOPE</code> using the reserved attribute with the
 * name {@link #KEY_RUNTIME}.  It is only set when this method is
 * invoked for the first time, for the givem
 * <code>ScriptContext</code>.
 *
 * @param sc The script context for which the <code>TeaRuntime</code>
 * is needed.
 *
 * @return The <code>TeaRuntime</code> associated with the
 * <code>sc</code> context.
 *
 * @throws ScriptException Current implementation is not expected to
 * throw any checked exception, but future enhancements may require
 * that context initialization code be executed at this time.
 *
 **************************************************************************/

    private TeaRuntime getTeaRuntime(final ScriptContext sc)
        throws ScriptException {

        synchronized (sc) {
            TeaRuntime teaRuntime =
                (TeaRuntime)sc.getAttribute(KEY_RUNTIME, ScriptContext.ENGINE_SCOPE);

            if (teaRuntime == null) {
                TeaRuntimeConfig config =
                    TeaRuntimeConfig.Builder.start()
                    .setArgv0(getArgv0(sc))
                    .setArgv(getArgv(sc))
                    .setSourceCharset(getSourceCharset(sc))
                    .setImportLocationList(getImportLocationList(sc))
                    .build();

                teaRuntime = _factory.newTeaRuntime(config);

                sc.setAttribute(KEY_RUNTIME, teaRuntime, ScriptContext.ENGINE_SCOPE);
            }
            return teaRuntime;
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private String getArgv0(final ScriptContext sc) {

        // TODO - make the argv0 cast error give a more friendly error

        String argv0 = (String)sc.getAttribute(ScriptEngine.FILENAME);

        return argv0;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private String[] getArgv(final ScriptContext sc)
        throws ScriptException {

        // TODO - make the argv cast error give a more friendly error

        String[] argv  = {};
        Object[] oArgv = (Object[])sc.getAttribute(ScriptEngine.ARGV);

        if (oArgv != null) {
            argv = new String[oArgv.length];
            try {
                System.arraycopy(oArgv, 0, argv, 0, argv.length);
            } catch (ArrayStoreException e) {
                String msg = "javax.script.argv must be an array of strings";
                throw new ScriptException(msg);
            }
        }

        return argv;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private List<String> getImportLocationList(final ScriptContext sc) {

        // TODO: talk with jfn to open STeaLauncherArgs.optionSetLibary()

        List<String> libraryList = null;
        String       libraryStr  = (String)sc.getAttribute(KEY_LIBRARY);

        if (libraryStr != null) {
            String pathSep = File.pathSeparator;
            StringTokenizer i = new StringTokenizer(libraryStr, pathSep);

            libraryList = new ArrayList<String>();

            while (i.hasMoreTokens()) {
                String path = i.nextToken();
                path = path.replace('|', ':');
                libraryList.add(path);
            }
        }

        return libraryList;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private Charset getSourceCharset(final ScriptContext sc) {

        String charsetName = (String)sc.getAttribute(KEY_ENCODING);
        Charset charset    = null;

        if ( charsetName != null ) {
            try {
                charset = Charset.forName(charsetName);
            } catch ( UnsupportedCharsetException e ) {
                // Too bad... Should we do something about it?...
            }
        }
        
        return charset;
    }





/**************************************************************************
 *
 * Internal method used to setup the pre-execution Tea runtime
 * environment from the <code>ScriptContext</code>.
 *
 * <p>This is the internal method that converts the reserved
 * attributes names into {@link TeaRuntime} settings, sets up
 * global variables, and prepares it for execution calling {@link
 * TeaRuntime#start() }.</p>
 *
 * @param sc The script context.
 *
 * @return The <code>TeaRuntime</code> associated with
 * <code>sc</code>.
 * 
 * @throws ScriptException
 *
 **************************************************************************/

    public TeaRuntime context2TeaGlobals(final ScriptContext sc)
        throws ScriptException {

        try {
            TeaRuntime teaRuntime = getTeaRuntime(sc);
            // prepare the context for execution of code.
            teaRuntime.start();

            // Run an empty code to force first time initialization
            // before calling ModuleReflect.java2Tea.  (There is no
            // public interface for doing so in TeaRuntime.)
            // Otherwise we will get errors for some convertions
            // resulting in STosObj like SDate.
            // try {
            //     teaRuntime.execute(_emptyCode);
            // } catch (TeaException ex) {
            //     throw new ScriptException(ex);
            // }
            
            TeaContext teaContext = teaRuntime.getToplevelContext();

            // SCR.4.3.1 ScriptContext - $stdin, $stdout and $stderr
            // should be initialized from the appropriate
            // reader/writers in the ScriptContext.  There is no way
            // to check that the reader/writers are associated with
            // the default System.in, .out and .err. If there was,
            // there was no need to set them up again, as Tea has
            // already set them up, and in a way capable of
            // reading/writing binary content (bytes).
            java.io.Reader ir = sc.getReader();
            if (ir!=null) {
                com.pdmfc.tea.modules.io.SInput stdin =
                    (com.pdmfc.tea.modules.io.SInput)teaContext.getVar(TeaSymbol.getSymbol("stdin"));
                stdin.open(ir);
            }
            java.io.Writer ow = sc.getWriter();
            if (ow!=null) {
                com.pdmfc.tea.modules.io.SOutput stdout =
                    (com.pdmfc.tea.modules.io.SOutput)teaContext.getVar(TeaSymbol.getSymbol("stdout"));
                stdout.open(ow);
            }
            java.io.Writer ew = sc.getErrorWriter();
            if (ew!=null) {
                com.pdmfc.tea.modules.io.SOutput stderr =
                    (com.pdmfc.tea.modules.io.SOutput)teaContext.getVar(TeaSymbol.getSymbol("stderr"));
                stderr.open(ew);
            }

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
                    TeaSymbol keySym = TeaSymbol.addSymbol(key);
                    //System.out.println("GLOBAL_SCOPE to tea global " + key + "=" + b.get(key));
                    teaContext.newVar(keySym,
                                      STeaJavaTypes.java2Tea(b.get(key), teaContext));
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
                    TeaSymbol keySym = TeaSymbol.addSymbol(key);
                    //System.out.println("ENGINE_SCOPE to tea global " + key + "=" + b.get(key));
                    teaContext.newVar(keySym,
                                      STeaJavaTypes.java2Tea(b.get(key), teaContext));
                }
            }
            // SCR.4.3.4.1.2 Script Execution - specifies that the context should be
            // available to the script.
            // Together with the SCOPE_ENGINE attribute com.pdmfc.tea.engine.runtime
            // holding the TeaRuntime, it creates a circular reference.
            // But so far, having a circular reference seems better than
            // letting a SimpleScriptContext discard the TeaRuntime context.
            teaContext.newVar(TeaSymbol.addSymbol("context"),
                              STeaJavaTypes.java2Tea(sc, teaContext));

            return teaRuntime;
        } catch (TeaException e) {
            throw new ScriptException(e);
        }
    }





/**************************************************************************
 *
 * Internal method to flush post-execution values back to the
 * <code>ScriptContext</code>, and calls {@link
 * TeaRuntime#stop()}.
 *
 * @param sc The script context.
 *
 * @throws ScriptException
 *
 **************************************************************************/

    void teaGlobals2Context(final ScriptContext sc)
        throws ScriptException {

        TeaRuntime teaRuntime = getTeaRuntime(sc);
        try {
            TeaContext teaContext = teaRuntime.getToplevelContext();
            Bindings b = sc.getBindings(ScriptContext.ENGINE_SCOPE);
            Set<String> esKeys = null;
            if (b != null) {
                esKeys = b.keySet();
                for (String key : esKeys) {
                    // SCR.4.3.4.1.1 Bindings, Bound Values and State
                    // - skip reserved names
                    if (key.startsWith("javax.script")
                            || key.startsWith("com.pdmfc.tea")) {
                        continue;
                    }
                    TeaSymbol keySym = TeaSymbol.addSymbol(key);
                    try {
                        Object teaValue  = teaContext.getVar(keySym);
                        Object javaValue = STeaJavaTypes.tea2Java(teaValue);
                        b.put(key, javaValue);
                    } catch (SNoSuchVarException ex) {
                        // Should we do something?...
                    }
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
                    // SCR.4.3.4.1.1 Bindings, Bound Values and State
                    // - skip reserved names
                    if (key.startsWith("javax.script")
                            || key.startsWith("com.pdmfc.tea")) {
                        continue;
                    }
                    TeaSymbol keySym = TeaSymbol.addSymbol(key);
                    try {
                        Object teaValue  = teaContext.getVar(keySym);
                        Object javaValue = STeaJavaTypes.tea2Java(teaValue);
                        b.put(key, javaValue);
                    } catch (SNoSuchVarException ex) {
                        // Should we do something?...
                    }
                }
            }

        } catch (TeaException e) {
            throw new ScriptException(e);
        } finally {
            // no more Tea code ought to be executed without calling
            // start
            try {
                teaRuntime.stop();
            } catch (IllegalStateException ex) {
                // If an error ocurred before initialization of
                // TeaRuntime we ignore it, but we attempted to stop
                // it anyway.
                throw new ScriptException(ex);
            }
            // teaRuntime.end(); -- not here. On servlet unloading ?
        }
    }





/**************************************************************************
 *
 * Obtain the full error message (including the Tea script's stack
 * trace) from a <code>javax.script.ScriptException</code> thrown
 * while using this framework.
 *
 * If the cause of the <code>ScriptException</code> is a {@link
 * TeaRunException} then there is probably underlying Tea script
 * stack trace information which is included in the returned
 * string value (where the list of messages in the stack trace is
 * separated by a newline).  If not, it just returns the message
 * associated with the cause of the exception.
 *
 * @param ex the <code>ScriptException</code> thrown during the
 * execution of a Tea script thrown while using this framework.
 *
 * @return A string with the message obtained from
 * {@link TeaRunException#getFullMessage} or
 * <code>ex.getCause().getMessage()</code>.
 *
 **************************************************************************/

    public static String getFullMessage(final ScriptException ex) {

        Throwable e = ex.getCause();

        if (e != null && e instanceof TeaRunException) {
            // the full message preserves line numbers in the text of the
            // message itself.
            return ((TeaRunException)e).getFullMessage();
        } else {
            return ex.getMessage();
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private TeaScript compileFromString(final String script)
        throws ScriptException,
               TeaCompileException {

        TeaRuntime teaRuntime = getTeaRuntime();
        TeaScript  result     = null;

        try (
            Reader reader = new StringReader(script)
        ) {
            result = teaRuntime.compile(reader, null);
        } catch ( IOException e ) {
            // This should never happen...
            throw new IllegalStateException(e);
        }

        return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

