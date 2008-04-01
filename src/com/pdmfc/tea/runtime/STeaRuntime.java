/**************************************************************************
 *
 * Copyright (c) 2001, 2002, 2003, 2004, 2005 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: STeaRuntime.java,v 1.18 2005/03/09 15:10:36 jfn Exp $
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

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
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
import com.pdmfc.tea.runtime.SUtils;





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

    // List of modules registered by calls to one of the
    // <code>addModule(...)</code> methods.
    private ArrayList _modules = new ArrayList();

    // The modules registered by calls to the <code>{@link
    // #addModule(String)}</code> method. The values are
    // <code>SModule</code> instances and are indexed by the
    // respective Java class name. This is used to assure a module is
    // not added twice.
    private Map _modulesByName = new HashMap();

    // Counts packages preventing this Tea interpreter from stoping.
    private int _holdCounter = 0;

    private boolean _needsToRunInitScripts = true;





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
 * Sets the list of directories from where the <code>import</code> Tea
 * function will try to read files.
 *
 * <p>A Tea global variable named <code>TEA_LIBRARY</code> will be
 * created. This variable will contain a list of strings. Each string
 * element is the pathname of one of the directories represented by
 * <code>dirList</code>.</p>
 *
 * <p>For each directory, a Tea script named <code>init.tea</code>
 * will be executed, if it exits.</p>
 *
 * <p>This method must not be invoked between calls to
 * <code>start()</code> and <code>stop</code>. Internally
 * <code>start()</code> is is called just before executing the
 * <code>init.tea</code> scripts and <code>stop()</code> is called
 * just afterwards.</p>
 *
 * @param dirList List of directories. Each directory is separated by
 * a path separator character.
 *
 * @param compiler Tea compiler that will be used to compile the
 * <code>init.tea</code> scripts.
 *
 * @exception com.pdmfc.tea.SException Thrown if there were any
 * problems executing one of the <code>init.tea</code> scripts.
 *
 * @deprecated A call to this method should be replaced by a call to
 * the <code>{@link #setImportDirs(String)}</code> method and using
 * the <code>{@link #execute(SCode)}</code> method.
 *
 **************************************************************************/

    public void setImportDirs(String    dirList,
			      SCompiler compiler)
	throws STeaException {

	STeaException error = null;

	setImportDirs(dirList);
	start();
	try {
	    runInitScripts();
	} catch (STeaException e) {
	    error = e;
	}
	stop();

	if ( error != null ) {
	    throw error;
	}
    }





/**************************************************************************
 *
 * Utility method for initializing the <code>TEA_LIBRARY</code>
 * variable. This variable will be initialized with a list of
 * strings. These strings are obtained from <code>dirListStr</code> by
 * spliting it using the plataform path separator character as
 * separator between elements.
 *
 * <p>The elements in the list that is stored in the
 * <code>TEA_LIBRARY</code> variable represent directory pathnames or
 * URLs. These directories and URLs will be used to look for files
 * when the Tea <code>import</code> function is called. If a character
 * in a URL is the same as the path separator character
 * (e.g. "<code>:</code>" in unix) then it should be replaced by a
 * "<code>|</code>" character.</p>
 *
 * <p>This method is supposed to be called prior to invoking <code>{@link
 * #execute(SCode)}</code>.</p>
 *
 * @param dirListStr A string representing a list of directory
 * pathnames and URLs. The elements are separated by the platform path
 * separator characeter.
 *
 **************************************************************************/

    public void setImportDirs(String dirListStr) {

	SObjPair dirList = SUtils.buildPathList(dirListStr);

	setImportDirs(dirList);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void setImportDirs(List dirList) {

	SObjPair teaDirList = SUtils.buildPathList(dirList);

	setImportDirs(teaDirList);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setImportDirs(SObjPair dirList) {

	URL      corePathUrl = STeaRuntime.class.getResource(CORE_IMPORT_DIR);
	String   corePath    =
	    (corePathUrl==null) ? null : corePathUrl.toString();

	if ( corePath != null ) {
	    SObjPair lastElement =
		new SObjPair(corePath, SObjPair.emptyList());

	    if ( dirList._car == null ) {
		// dirList is an empty list. It will now contain a
		// single element.
		dirList = lastElement;
	    } else {
		SObjPair root = dirList;
		while ( ((SObjPair)root._cdr)._car != null ) {
		    root = (SObjPair)root._cdr;
		}
		root._cdr = lastElement;
	    }
	}

	newVar(LIB_VAR, dirList);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void runInitScripts()
	throws STeaException {

	if ( !isDefined(LIB_VAR) ) {
	    return;
	}

	Object dirListObj = getVar(LIB_VAR);

	if ( ! (dirListObj instanceof SObjPair) ) {
	    return;
	}

	SObjPair  dirList  = (SObjPair) dirListObj;
	SCompiler compiler = new SCompiler();

	runInitScripts(dirList, compiler);
    }





/**************************************************************************
 *
 * For each directory in <code>dirList</code>, if there is a Tea
 * script named <code>init.tea</code> in that directory then executes
 * it.
 *
 * @param dirList A Tea list that is suposed to contain only strings
 * representing directory path names.
 *
 * @param compiler The Tea compiler that will be used to compile the
 * Tea scripts just before they get executed.
 *
 * @exception STeaException Thrown by any problem inside the script.
 *
 **************************************************************************/

    private void runInitScripts(SObjPair  dirList,
				SCompiler compiler)
	throws STeaException {

	_needsToRunInitScripts = false;

	for ( Iterator i=dirList.iterator(); i.hasNext(); ) {
	    String      urlPrefix = null;
	    String      fullUrl   = null;
	    InputStream input     = null;
	    
	    try {
		urlPrefix = (String)i.next();
	    } catch ( ClassCastException e1) {
		continue;
	    }
	    if ( urlPrefix.startsWith("/") || urlPrefix.startsWith(".") ) {
		fullUrl = "file:" + urlPrefix + "/" + INIT_FILE;
	    } else {
		fullUrl = urlPrefix + "/" + INIT_FILE;
	    }
	    try {
		input = (new URL(fullUrl)).openStream();
	    } catch (IOException e2) {
		continue;
	    }
	    try {
		compiler.compile(input, fullUrl).exec(this);
	    } catch (STeaException e3) {
		try { input.close(); } catch (IOException e4) {}
		throw e3;
	    }
	}
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

	// If was previouslly added, do nothing.
	if ( _modulesByName.containsKey(className) ) {
	    return;
	}

	SModule  pkg     = null;
	String   msg     = null;
	Object[] fmtArgs = null;

	try {
	    pkg = (SModule)Class.forName(className).newInstance();
	} catch (ClassNotFoundException e) {
	    msg    = "could not load class \"{0}\" while loading module";
	    fmtArgs = new Object[] { className };
	} catch (InstantiationException e) {
	    msg     = "could not instantiate \"{0}\" while loading module";
	    fmtArgs = new Object[] { className };
	} catch (IllegalAccessException e) {
	    msg     = "class \"{0}\" is not accessible, while loading module";
	    fmtArgs = new Object[] { className };
	} catch (Throwable e) {
	    msg     = "failed to load class \"{0}\" - {1} - {2}";
	    fmtArgs =
		new Object[] {className,e.getClass().getName(),e.getMessage()};
	}

	if ( msg != null ) {
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

	for ( Iterator i=_modules.iterator(); i.hasNext(); ) {
	    SModule module = (SModule)i.next();
	    module.start();
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

    public synchronized void stop() {

	while ( _holdCounter > 0 ) {
	    try {
		wait();
	    } catch (InterruptedException e) {
	    }
	}

	for ( ListIterator i=_modules.listIterator(_modules.size());
	      i.hasPrevious(); ) {
	    SModule module = (SModule)i.previous();
	    module.stop();
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





/**************************************************************************
 *
 * Signals that this context is no longer to be used. All the packages
 * that had been loaded so far are signaled. Then they are discarded.
 *
 **************************************************************************/

   public void end() {

      for ( ListIterator i=_modules.listIterator(_modules.size());
	    i.hasNext(); ) {
	  SModule module = (SModule)i.previous();
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
 * The Tea variable <code>TEA_LIBRARY</code> is supposed to have been
 * created and contain a list of strings. This variable is tipically
 * set by a call to the <code>{@link #setImportDirs(String)}</code>
 * method.
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

	Object result = null;

	if ( _needsToRunInitScripts ) {
	    runInitScripts();
	}

	result = code.exec(this);

	return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

