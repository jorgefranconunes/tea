/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pdmfc.tea.TeaConfig;
import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaRuntime;
import com.pdmfc.tea.TeaRuntimeConfig;
import com.pdmfc.tea.TeaCompileException;
import com.pdmfc.tea.runtime.ArgvUtils;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.runtime.LibVarUtils;
import com.pdmfc.tea.TeaEnvironment;
import com.pdmfc.tea.runtime.TeaEnvironmentImpl;
import com.pdmfc.tea.TeaModule;
import com.pdmfc.tea.TeaScript;
import com.pdmfc.tea.util.SInputSourceFactory;





/**************************************************************************
 *
 * A Tea interpreter that can be used to execute compiled code.
 *
 **************************************************************************/

public final class TeaRuntimeImpl
    extends Object
    implements TeaRuntime {





    // Name of the file to read from each directory in the TEA_LIBRARY
    // list.
    private static final String INIT_FILE = 
        TeaConfig.get("com.pdmfc.tea.initFile");

    // The path of a Java resource to use as import directory. This
    // path will be added to the end of list of import directories.
    private static final String CORE_IMPORT_DIR =
        TeaConfig.get("com.pdmfc.tea.coreImportDir");




    private enum State {
        INITED,
        STARTED,
        RUNNING,
        ENDED
    };





    private TeaRuntimeConfig _config             = null;
    private List<String>     _allImportLocations = null;

    // List of TeaModule instances. These were registered by calls to
    // addModule(...) before the first start.
    private List<TeaModule> _modules = new ArrayList<TeaModule>();

    private boolean _isFirstStart = true;

    private State _state = State.INITED;

    private TeaEnvironmentImpl _environment = null;





/**************************************************************************
 *
 * Initializes this toplevel context.
 *
 * @param config Configuration parameters.
 *
 **************************************************************************/

    public TeaRuntimeImpl(final TeaRuntimeConfig config) {

        _config      = config;
        _environment = new TeaEnvironmentImpl(config.getSourceCharset());

        // Add the core modules.
        Collections.addAll(_modules,
                           new com.pdmfc.tea.modules.io.ModuleIO(),
                           new com.pdmfc.tea.modules.lang.ModuleLang(),
                           new com.pdmfc.tea.modules.list.ModuleList(),
                           new com.pdmfc.tea.modules.math.ModuleMath(),
                           new com.pdmfc.tea.modules.string.ModuleString());

        // And the modules provided from outside.
        _modules.addAll(config.getModules());
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Deprecated
    @Override
    public TeaContext getToplevelContext() {

        TeaContext globalContext = _environment.getGlobalContext();

        return globalContext;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Override
    public TeaScript compile(final Reader reader,
                             final String fileName)
        throws IOException,
               TeaCompileException {

        TeaScript script = _environment.compile(reader, fileName);

        return script;
    }





/**************************************************************************
 *
 * Signals that this context will be used shortly after.
 *
 **************************************************************************/

    @Override
    public void start()
        throws TeaException {

        checkState(State.INITED);

        doStart();

        _state = State.STARTED;
    }





/**************************************************************************
 *
 * Signals that this context will not be used until a call to the
 * <code>start()</code> method is made again. All the packages loaded
 * so far are signaled with a call to their <code>stop()</code>
 * method.
 *
 * <p>If this runtime has not been started then an
 * <code>java.lang.IllegalStateException</code> will be thrown.
 *
 **************************************************************************/

    @Override
    public void stop() {

        checkState(State.STARTED);

        _state = State.INITED;

        _environment.stop();
    }





/**************************************************************************
 *
 * Signals that this context is no longer to be used. All the packages
 * that had been loaded so far are signaled. Then they are discarded.
 *
 **************************************************************************/

    @Override
   public void end() {

       checkState(State.INITED);

       _state = State.ENDED;

       _environment.end();
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void doStart()
        throws TeaException {

        boolean wasFirstStart = _isFirstStart;

        _isFirstStart = false;

        if ( wasFirstStart ) {
            doFirstStartInitializations();
        }

        _environment.start();

        if ( wasFirstStart ) {
            runInitScripts();
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void doFirstStartInitializations()
        throws TeaException {

        String       argv0           = _config.getArgv0();
        String[]     argv            = _config.getArgv();
        List<String> importLocations = _config.getImportLocationList();

        TeaContext globalContext = _environment.getGlobalContext();

        ArgvUtils.setArgv(globalContext, argv0, argv);
        setupLibVar(importLocations);
        setupModules(_modules);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setupLibVar(final List<String> locations) {

        _allImportLocations = new ArrayList<String>();

        _allImportLocations.addAll(locations);
        _allImportLocations.add(CORE_IMPORT_DIR);

        TeaContext globalContext = _environment.getGlobalContext();

        LibVarUtils.setupLibVar(globalContext, _allImportLocations);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setupModules(final List<TeaModule> modules)
        throws TeaException {

        for ( TeaModule module : modules ) {
            _environment.addModule(module);
        }
    }





/**************************************************************************
 *
 * For each directory in <code>_importLocations</code>, if there is a
 * Tea script named <code>init.tea</code> in that directory then
 * executes it.
 *
 **************************************************************************/

    private void runInitScripts()
        throws TeaException {

        Charset      sourceCharset = _config.getSourceCharset();
        List<String> dirList       = _allImportLocations;

        for ( String dirPath : dirList ) {
            String path = INIT_FILE;

            try (
                Reader reader =
                   SInputSourceFactory.openReader(dirPath, path, sourceCharset);
            ) {
                TeaScript script = compile(reader, path);
                script.execute();
            } catch ( IOException e ) {
                // The given path does not exist or is not
                // readable. Go ahead and just ignore it.
            }
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void checkState(final State... states) {

        boolean currentStateIsValid = false;
        State   currentState        = _state;

        for ( State state : states ) {
            if ( state == currentState ) {
                currentStateIsValid = true;
                break;
            }
        }

        if ( !currentStateIsValid ) {
            String msg = "Action not allowed on state " + currentState;
            throw new IllegalStateException(msg);
        }
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

