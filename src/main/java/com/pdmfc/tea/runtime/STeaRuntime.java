/**************************************************************************
 *
 * Copyright (c) 2001-2010 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
 *
 *
 * Revisions:
 *
 * 2005/03/09 The "addModule(String)" method now generates the
 * apropriate "STeaException" if there were any problems instantiating
 * the module class. (jfn)
 *
 * 2004/04/03 Added the method "setImportDirs(List)". (jfn)
 *
 * 2002/10/28 The "setImportDirs(...)" methods now automatically add
 * to the end of the list the directory/URL for the Tea core
 * library. (jfn)
 *
 * 2002/08/03 The SList storing the modules was replaced by a
 * java.util.ArrayList. (jfn)
 *
 * 2002/07/18 Removed the "setCliArgs()" method. Its role is now
 * performed from within "STeaShell" itself. (jfn)
 *
 * 2002/01/10 The addJavaFunction(...) methods were moved to the
 * SModuleBase class. (jfn) Now uses SObjPair.iterator() instead of
 * SObjPair.elements(). (jfn)
 *
 * 2001/05/12 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.pdmfc.tea.SConfigInfo;
import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SCode;
import com.pdmfc.tea.compiler.SCompiler;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.runtime.SArgvUtils;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SEncodingUtils;
import com.pdmfc.tea.runtime.SLibVarUtils;
import com.pdmfc.tea.runtime.SModuleUtils;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjPair;





/**************************************************************************
 *
 * Represents a Tea interpreter.
 *
 **************************************************************************/

public class STeaRuntime
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
	"com.pdmfc.tea.modules.SModuleList",
	"com.pdmfc.tea.modules.SModuleMath",
	"com.pdmfc.tea.modules.SModuleString"
    };




    private enum State {
	INITED, STARTED, RUNNING, ENDED
    };





    private List<String> _importLocations    = new ArrayList<String>();
    private List<String> _allImportLocations = null;

    private String   _argv0          = null;
    private String[] _argv           = new String[0];
    private String   _sourceEncoding = null;

    // List of module class names or SModule instances. These were
    // registered by calls to addModule(...) before the first start.
    private List<Object> _modules = new ArrayList<Object>();

    private boolean _isFirstStart = true;
    private boolean _isFirstExec  = true;

    private State _state = State.INITED;

    private SContext _toplevelContext = new SContext();





/**************************************************************************
 *
 * Initializes this toplevel context.
 *
 **************************************************************************/

    public STeaRuntime() {

        for ( String moduleClassName : CORE_MODULES ) {
            _modules.add(moduleClassName);
        }
    }





/**************************************************************************
 *
 * Specifies the list of locations for importing source files from
 * within the Tea script. The locations can be either file system path
 * names or URLs.
 *
 * <p>This method is intended to be called prior to the first call to
 * <code>{@link #execute(SCode)}</code>. After that it will have no
 * effect.</p>
 *
 * @param dirList List of strings representing file system path names
 * or URLs.
 *
 **************************************************************************/

    public void setImportLocations(List<String> dirList) {

        _importLocations.clear();
        _importLocations.addAll(dirList);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void prependImportLocation(String location) {

	_importLocations.add(0, location);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void prependImportLocations(List<String> locations) {

	_importLocations.addAll(0, locations);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void appendImportLocation(String location) {

	_importLocations.add(location);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void appendImportLocations(List<String> locations) {

	_importLocations.addAll(locations);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void setArgv0(String argv0) {

        _argv0 = argv0;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void setArgv(String[] argv) {

        _argv = argv;
    }





/**************************************************************************
 *
 * Specifies the encoding of Tea source files.
 *
 * @param sourceEncoding If null the platform default encoding is
 * assumed.
 *
 **************************************************************************/

    public void setSourceEncoding(String sourceEncoding) {

        _sourceEncoding = sourceEncoding;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void addModule(String className)
        throws STeaException {

        checkState(State.INITED, State.STARTED, State.RUNNING);

        if ( _isFirstStart ) {
            _modules.add(className);
        } else if ( _state == State.RUNNING ) {
            SModuleUtils.addAndStartModule(_toplevelContext, className);
        } else {
            SModuleUtils.addModule(_toplevelContext, className);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void addModule(SModule module)
        throws STeaException {

        checkState(State.INITED, State.STARTED, State.RUNNING);

        if ( _isFirstStart ) {
            _modules.add(module);
        } else if ( _state == State.RUNNING ) {
            SModuleUtils.addAndStartModule(_toplevelContext, module);
        } else {
            SModuleUtils.addModule(_toplevelContext, module);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SContext getToplevelContext() {

        return _toplevelContext;
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
 * <p>Before calling the packages' <code>stop()</code> method the
 * current thread waits until all necessary calls to
 * <code>releaseStop()</code> are performed. The calls to
 * <code>releaseStop()</code> must be as many as the ones to
 * <code>holdStop</code>. Note that as the current thread sleeps, the
 * calls to <code>releaseStop()</code> must be made from another
 * thread.</p>
 *
 **************************************************************************/

    public void stop() {

        checkState(State.STARTED);

        _state       = State.INITED;
        _isFirstExec = true;

        try {
            SModuleUtils.stopModules(_toplevelContext);
        } catch (STeaException e) {
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
            SModuleUtils.endModules(_toplevelContext);
        } catch (STeaException e) {
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

    public Object execute(SCode code)
	throws STeaException {

        checkState(State.STARTED);

        _state = State.RUNNING;

        Object result = null;

	try {
            if ( _isFirstExec ) {
                _isFirstExec = false;
                doStart();
            }

	    result = code.exec(_toplevelContext);
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

        SModuleUtils.startModules(_toplevelContext);

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

        SArgvUtils.setArgv(_toplevelContext, _argv0, _argv);
        setupLibVar(_importLocations);
        SEncodingUtils.setSourceEncoding(_toplevelContext, _sourceEncoding);
        setupModules(_modules);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setupLibVar(List<String> locations) {

	_allImportLocations = new ArrayList<String>();

	_allImportLocations.addAll(locations);
	_allImportLocations.add(CORE_IMPORT_DIR);

        SLibVarUtils.setupLibVar(_toplevelContext, _allImportLocations);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setupModules(List<Object> modules)
        throws STeaException {

        for ( Object moduleOrClassName : modules ) {
            if ( moduleOrClassName instanceof String ) {
                String moduleClassName = (String)moduleOrClassName;

                SModuleUtils.addModule(_toplevelContext, moduleClassName);
            } else {
                SModule module = (SModule)moduleOrClassName;

                SModuleUtils.addModule(_toplevelContext, module);
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

        List<String> dirList  = _allImportLocations;
        SCompiler    compiler = new SCompiler();

	for ( String dirPath : dirList ) {
            String path = INIT_FILE;
            SCode  code = null;
            
            try {
                code = compiler.compile(dirPath, path, _sourceEncoding, path);
                code.exec(_toplevelContext);
            } catch (IOException e) {
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

    private void checkState(State... states) {

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
            throw new IllegalArgumentException(msg);
        }
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/
