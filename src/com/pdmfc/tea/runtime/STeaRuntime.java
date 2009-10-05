/**************************************************************************
 *
 * Copyright (c) 2001-2009 PDM&FC, All Rights Reserved.
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

import java.io.InputStream;
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
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.util.SInputSource;
import com.pdmfc.tea.util.SInputSourceFactory;





/**************************************************************************
 *
 * Represents a Tea interpreter.
 *
 * <p>Usage of this class is preferable to the direct manipulation of
 * <code>SContext</code> objects when running a Tea program.</p>
 *
 **************************************************************************/

public class STeaRuntime
    extends SContext {





    private static final String PROP_LIB_VAR   =
	"com.pdmfc.tea.libraryVarName";

    private static final String PROP_INIT_FILE =
	"com.pdmfc.tea.initFile";

    private static final String PROP_CORE_IMPORT_DIR =
	"com.pdmfc.tea.coreImportDir";


    // The name of the Tea global variable with the list of directory
    // names where the <code>import</code> function looks for Tea
    // source files.
    private static final SObjSymbol LIB_VAR   =
	SObjSymbol.addSymbol(SConfigInfo.getProperty(PROP_LIB_VAR));

    // Name of the file to read from each directory in the TEA_LIBRARY
    // list.
    private static final String INIT_FILE = 
	SConfigInfo.getProperty(PROP_INIT_FILE);

    // The path of Java resource to use as import directory. This path
    // will be added to the end of list of import directories.
    private static final String CORE_IMPORT_DIR =
	SConfigInfo.getProperty(PROP_CORE_IMPORT_DIR);




    private enum State {
	INITED, STARTED, RUNNING, ENDED
    };




    
    private List<String> _importLocations    = new ArrayList<String>();
    private List<String> _allImportLocations = null;

    // List of modules registered by calls to one of the
    // <code>addModule(...)</code> methods.
    private ArrayList<SModule> _modules = new ArrayList<SModule>();

    // The modules registered by calls to the addModule(String)}
    // method. The values are SModule instances and are indexed by the
    // respective Java class name. This is used to ensure a module is
    // not added twice.
    private Map<String,SModule> _modulesByName =
	new HashMap<String,SModule>();

    // Counts packages preventing this Tea interpreter from stoping.
    private int _holdCounter = 0;

    private boolean _isFirstStart = true;
    private boolean _isFirstExec  = true;

    private State _state = State.INITED;





/**************************************************************************
 *
 * Initializes this toplevel context.
 *
 **************************************************************************/

    public STeaRuntime()
	throws STeaException {

	addModule("com.pdmfc.tea.modules.io.SModuleIO");
	addModule("com.pdmfc.tea.modules.lang.SModuleLang");
	addModule("com.pdmfc.tea.modules.SModuleList");
	addModule("com.pdmfc.tea.modules.SModuleMath");
	addModule("com.pdmfc.tea.modules.SModuleString");
    }





/**************************************************************************
 *
 * @deprecated Use method <code>{@link
 * #setImportLocations(List<String>}</code> instead.
 *
 **************************************************************************/

    @Deprecated
    public void setImportDirs(List<String> dirList) {

	setImportLocations(dirList);
    }





/**************************************************************************
 *
 * @param dirListStr A string representing a list of directory
 * pathnames and URLs. The elements are separated by the platform path
 * separator characeter.
 *
 * @deprecated Use method <code>{@link
 * #setImportLocations(List<String>}</code> instead.
 *
 **************************************************************************/

    @Deprecated
    public void setImportDirs(String dirListStr) {

	SObjPair     pairList = SUtils.buildPathList(dirListStr);
	List<String> dirList  = new ArrayList<String>();

	for ( Iterator i=pairList.iterator(); i.hasNext(); ) {
	    SObjPair pair = (SObjPair)i.next();
	    String   dir  = (String)pair._car;

	    dirList.add(dir);
	}

	setImportLocations(dirList);
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

    public final void addFunction(String       functionName,
				  SObjFunction function) {

	newVar(functionName, function);
    }





/**************************************************************************
 *
 * Initializes the package in this context. Tipically this will add
 * new commands to the context.
 *
 * @param pkg Reference to the <code>SModule</code> object to be
 * initialized.
 *
 **************************************************************************/

    public void addModule(SModule pkg)
	throws STeaException {

	_modules.add(pkg);
	pkg.init(this);
    }





/**************************************************************************
 *
 * Initializes a package in this context. An object of the java class
 * named <code>className</code> is instantiated. That class must be
 * derived from the <code>SModule</code> class. The newly created
 * <code>SModule</code> object is initialized inside this context.
 *
 * @param className The name of the java class, derived from
 * <code>SModule</code>, of the object to be inSantiated.
 *
 * @exception STeaException Thrown if there were any problems loading
 * the class or inSantiating the object.
 *
 **************************************************************************/

    public void addModule(String className)
	throws STeaException {

	if ( _modulesByName.containsKey(className) ) {
	    // The module was already previouslly added, do nothing.
	    return;
	}

	SModule pkg = null;

	try {
	    pkg = (SModule)Class.forName(className).newInstance();
	} catch (Throwable e) {
	    String   msg     = "failed to load class \"{0}\" - {1} - {2}";
	    Object[] fmtArgs = {
		className,e.getClass().getName(),e.getMessage()
	    };
	    throw new STeaException(msg, fmtArgs);
	}

	addModule(pkg);
	_modulesByName.put(className, pkg);
    }





/**************************************************************************
 *
 * Signals that this context will be used shortly after. All the
 * packages loaded so far are signaled.
 *
 **************************************************************************/

    public void start() {

	if ( _state != State.INITED ) {
	    String msg = "Action not allowed on state " + _state;
	    throw new IllegalStateException(msg);
	} else {
	    _state = State.STARTED;
	}
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

	if ( _state != State.STARTED ) {
	    String msg = "Action not allowed on state " + _state;
	    throw new IllegalStateException(msg);
	} else {
	    _state       = State.INITED;
	    _isFirstExec = true;
	}

	while ( _holdCounter > 0 ) {
	    try {
		wait();
	    } catch (InterruptedException e) {
	    }
	}

	for ( ListIterator<SModule> i=_modules.listIterator(_modules.size());
	      i.hasPrevious(); ) {
	    SModule module = i.previous();
	    module.stop();
	}
    }





/**************************************************************************
 *
 * Signals that this context is no longer to be used. All the packages
 * that had been loaded so far are signaled. Then they are discarded.
 *
 **************************************************************************/

   public void end() {

	if ( _state != State.INITED ) {
	    String msg = "Action not allowed on state " + _state;
	    throw new IllegalStateException(msg);
	} else {
	    _state = State.ENDED;
	}

      for ( ListIterator<SModule> i=_modules.listIterator(_modules.size());
	    i.hasNext(); ) {
	  SModule module = i.previous();
	  module.end();
      }
      clearAll();
      _modules.clear();
      _modulesByName.clear();
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

	if ( _state != State.STARTED ) {
	    String msg = "Action not allowed on state " + _state;
	    throw new IllegalStateException(msg);
	} else {
	    _state = State.RUNNING;
	}

	if ( _isFirstExec ) {
	    _isFirstExec = false;
	    doStart();
	}

	Object result = null;

	try {
	    result = code.exec(this);
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
	    setupLibVar(_importLocations);
	}

	for ( SModule module : _modules ) {
	    module.start();
	}

	if ( wasFirstStart ) {
	    runInitScripts();
	}
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

        SObjPair teaLocations  = buildTeaList(_allImportLocations);

	newVar(LIB_VAR, teaLocations);
    }





/**************************************************************************
 *
 * Creates a Tea list of strings from the given list.
 *
 * @param pathList A list where each element is a string representing
 * a path or URL.
 *
 * @return The head of a Tea list.
 *
 **************************************************************************/

    private static SObjPair buildTeaList(List<String> pathList) {

	SObjPair empty = SObjPair.emptyList();
	SObjPair head  = empty;
	SObjPair elem  = null;

	if ( pathList == null ) {
	    return empty;
	}

	for ( String path : pathList ) {
	    SObjPair node = null;

	    if ( path.length() == 0 ) {
		continue;
	    }
	    node = new SObjPair(path, empty);
 
	    if ( elem == null ) {
		head = node;
	    } else {
		elem._cdr = node;
	    }
	    elem = node;
	}

	return head;
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
            String      path  = INIT_FILE;
            InputStream input = null;

            try {
                SInputSource inputSource =
                    SInputSourceFactory.createInputSource(dirPath, path);

                input = inputSource.openStream();
            } catch (IOException e) {
                // The given path does not exist or is not
                // readable. Never mind.
            }

            if ( input != null ) {
                try {
                    compiler.compile(input, path).exec(this);
                } finally {
                    try { input.close(); } catch (IOException e) {}
                }
            }
	}
    }





/**************************************************************************
 *
 * Signals this interpreter that it must wait for a call to the
 * <code>releaseStop()</code> method before calling
 * <code>stop()</code> for all packages in the <code>stop()</code>
 * method. Note that for each call to this method there must be a
 * corresponding call to <code>releaseStop()</code> before the
 * <code>stop()</code> method exits.
 *
 **************************************************************************/

    public synchronized void holdStop() {

	_holdCounter++;
    }





/**************************************************************************
 *
 * The complement to <code>holdStop()</code>.
 *
 **************************************************************************/

    public synchronized void releaseStop() {

	if ( _holdCounter > 0 ) {
	    _holdCounter--;
	    notify();
	}
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

