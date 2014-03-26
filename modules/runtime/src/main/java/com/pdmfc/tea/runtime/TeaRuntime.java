/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.pdmfc.tea.SConfigInfo;
import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.TeaCode;
import com.pdmfc.tea.compiler.TeaCompiler;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.runtime.SArgvUtils;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SLibVarUtils;
import com.pdmfc.tea.runtime.SModuleUtils;
import com.pdmfc.tea.runtime.TeaRuntimeConfig;





/**************************************************************************
 *
 * A Tea interpreter that can be used to execute compiled code.
 *
 **************************************************************************/

public final class TeaRuntime
    extends Object {





    // Name of the file to read from each directory in the TEA_LIBRARY
    // list.
    private static final String INIT_FILE = 
        SConfigInfo.getProperty("com.pdmfc.tea.initFile");

    // The path of a Java resource to use as import directory. This
    // path will be added to the end of list of import directories.
    private static final String CORE_IMPORT_DIR =
        SConfigInfo.getProperty("com.pdmfc.tea.coreImportDir");

    private static final String[] CORE_MODULES = {
        "com.pdmfc.tea.modules.io.SModuleIO",
        "com.pdmfc.tea.modules.lang.SModuleLang",
        "com.pdmfc.tea.modules.list.SModuleList",
        "com.pdmfc.tea.modules.math.SModuleMath",
        "com.pdmfc.tea.modules.string.SModuleString"
    };




    private enum State {
        INITED,
        STARTED,
        RUNNING,
        ENDED
    };





    private TeaRuntimeConfig _config             = null;
    private List<String>     _allImportLocations = null;

    // List of module class names or SModule instances. These were
    // registered by calls to addModule(...) before the first start.
    private List<Object> _modules = new ArrayList<Object>();

    private boolean _isFirstStart = true;
    private boolean _isFirstExec  = true;

    private State _state = State.INITED;

    private TeaEnvironment _environment = null;





/**************************************************************************
 *
 * Initializes this toplevel context.
 *
 * @param config Configuration parameters.
 *
 **************************************************************************/

    public TeaRuntime(final TeaRuntimeConfig config) {

        _config      = config;
        _environment = new TeaEnvironmentImpl(config.getSourceCharset());

        for ( String moduleClassName : CORE_MODULES ) {
            _modules.add(moduleClassName);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void addModule(final String className)
        throws STeaException {

        checkState(State.INITED, State.STARTED, State.RUNNING);

        if ( _isFirstStart ) {
            _modules.add(className);
        } else if ( _state == State.RUNNING ) {
            SModuleUtils.addAndStartModule(_environment, className);
        } else {
            SModuleUtils.addModule(_environment, className);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void addModule(final SModule module)
        throws STeaException {

        checkState(State.INITED, State.STARTED, State.RUNNING);

        if ( _isFirstStart ) {
            _modules.add(module);
        } else if ( _state == State.RUNNING ) {
            SModuleUtils.addAndStartModule(_environment, module);
        } else {
            SModuleUtils.addModule(_environment, module);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Deprecated
    public SContext getToplevelContext() {

        SContext globalContext = _environment.getGlobalContext();

        return globalContext;
    }





/**************************************************************************
 *
 * Signals that this context will be used shortly after.
 *
 **************************************************************************/

    public void start() {

        checkState(State.INITED);

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

    public void stop() {

        checkState(State.STARTED);

        _state       = State.INITED;
        _isFirstExec = true;

        try {
            SModuleUtils.stopModules(_environment);
        } catch ( STeaException e ) {
            // How should we report this to the caller?...
        }
    }





/**************************************************************************
 *
 * Signals that this context is no longer to be used. All the packages
 * that had been loaded so far are signaled. Then they are discarded.
 *
 **************************************************************************/

   public void end() {

       checkState(State.INITED);

       _state = State.ENDED;

        try {
            SModuleUtils.endModules(_environment);
        } catch ( STeaException e ) {
            // How should we report this to the caller?...
        }
   }





/**************************************************************************
 *
 * Executes the given Tea program.
 *
 * @param code The Tea program to execute.
 *
 * @return The result of executing the Tea code. This will be the
 * value returned by the last function invocation in the program.
 *
 * @exception STeaException Thrown if there were any errors while
 * executing the Tea program. It may also be thrown the first time
 * this method is called if there were errors executing the
 * <code>init.tea</code> scripts in the import directories.
 *
 **************************************************************************/

    public Object execute(final TeaCode code)
        throws STeaException {

        checkState(State.STARTED);

        _state = State.RUNNING;

        Object result = null;

        try {
            if ( _isFirstExec ) {
                _isFirstExec = false;
                doStart();
            }

            SContext globalContext = _environment.getGlobalContext();

            result = code.exec(globalContext);
        } finally {
            _state = State.STARTED;
        }

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void doStart()
        throws STeaException {

        boolean wasFirstStart = _isFirstStart;

        _isFirstStart = false;

        if ( wasFirstStart ) {
            doFirstStartInitializations();
        }

        SModuleUtils.startModules(_environment);

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
        throws STeaException {

        String       argv0           = _config.getArgv0();
        String[]     argv            = _config.getArgv();
        List<String> importLocations = _config.getImportLocationList();

        SContext globalContext = _environment.getGlobalContext();

        SArgvUtils.setArgv(globalContext, argv0, argv);
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

        SContext globalContext = _environment.getGlobalContext();

        SLibVarUtils.setupLibVar(globalContext, _allImportLocations);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setupModules(final List<Object> modules)
        throws STeaException {

        for ( Object moduleOrClassName : modules ) {
            if ( moduleOrClassName instanceof String ) {
                String moduleClassName = (String)moduleOrClassName;

                SModuleUtils.addModule(_environment, moduleClassName);
            } else {
                SModule module = (SModule)moduleOrClassName;

                SModuleUtils.addModule(_environment, module);
            }

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
        throws STeaException {

        Charset      sourceCharset = _config.getSourceCharset();
        List<String> dirList       = _allImportLocations;
        TeaCompiler    compiler      = new TeaCompiler();

        for ( String dirPath : dirList ) {
            String   path          = INIT_FILE;
            SContext globalContext = _environment.getGlobalContext();
            TeaCode  code          = null;
            
            try {
                code = compiler.compile(dirPath, path, sourceCharset, path);
                code.exec(globalContext);
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

