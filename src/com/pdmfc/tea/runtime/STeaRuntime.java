/**************************************************************************
 *
 * Copyright (c) 2001-2008 PDM&FC, All Rights Reserved.
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




    
    // List of Strings.
    private List _importDirList = new ArrayList();

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
 * Specifies the list of locations for importing source files from
 * within the Tea script. The locations can be either file system path
 * names or URLs.
 *
 * <p>This method is intended to be called prior to the first call to
 * <code>{@link #execute(SCode)}</code>. After that it will have no
 * effect.</p>
 *
 * @param dirList List of strings represeting file system path names
 * or URLs.
 *
 **************************************************************************/

    public void setImportDirList(List dirList)
        throws STeaException {

        _importDirList.clear();
        _importDirList.addAll(dirList);
        _importDirList.add(CORE_IMPORT_DIR);
        
        setupLibVar(_importDirList);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setupLibVar(List dirList)
        throws STeaException {

        SObjPair teaDirList  = buildTeaList(dirList);

	newVar(LIB_VAR, teaDirList);
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

    public static SObjPair buildTeaList(List pathList) {

	SObjPair empty    = SObjPair.emptyList();
	SObjPair head     = empty;
	SObjPair elem     = null;

	if ( pathList == null ) {
	    return empty;
	}

	for ( Iterator i=pathList.iterator(); i.hasNext(); ) {
	    String   path = (String)i.next();
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
 * For each directory in <code>_importDirList</code>, if there is a
 * Tea script named <code>init.tea</code> in that directory then
 * executes it.
 *
 **************************************************************************/

    private void runInitScripts()
	throws STeaException {

        List      dirList  = _importDirList;
        SCompiler compiler = new SCompiler();

	_needsToRunInitScripts = false;

	for ( Iterator i=dirList.iterator(); i.hasNext(); ) {
            String      dirPath = (String)i.next();
            String      path    = INIT_FILE;
            InputStream input   = null;

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

