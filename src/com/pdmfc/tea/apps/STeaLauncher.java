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
 * 2009/10/13 Backported to the 3.2.x branch. (TSK-PDMFC-TEA-0044) (jfn)
 *
 * 2009/03/11 Improved documentation. (jfn)
 *
 * 2007/04/18 Refactored to receive the Tea library path list as a
 * command line argument. (jfn)
 *
 * 2004/04/02 Slight API refactoring to make it possible for this
 * class to be used from inside Java code. (jfn)
 *
 * 2002/07/18 It is now responsible for setting the "argv"
 * variable. (jfn)
 *
 * 2001/05/12 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.apps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pdmfc.tea.SConfigInfo;
import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.apps.STeaLauncherArgs;
import com.pdmfc.tea.compiler.SCode;
import com.pdmfc.tea.compiler.SCompiler;
import com.pdmfc.tea.runtime.SExitException;
import com.pdmfc.tea.runtime.SFlowControlException;
import com.pdmfc.tea.runtime.STeaRuntime;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * A Tea shell that runs a Tea program with command line arguments.
 *
 **************************************************************************/

public class STeaLauncher
    extends Object {





    private String       _scriptLocation = null;
    private String       _encoding       = null;
    private List<String> _importDirList  = new ArrayList<String>();





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public STeaLauncher() {
    }





/**************************************************************************
 *
 * Specifies the pathname of the file containing the Tea script to be
 * executed. The Tea script will be executed when the <code>{@link
 * #execute(String[])}</code> method is called.
 *
 * <p>The path of the Tea script is available to Tea code as a string
 * in the <code>argv0</code> variable.</p>
 *
 * @param location An URL or a file system path name. If null then
 * the script will be read from stdin.
 *
 **************************************************************************/

    public void setScriptLocation(String location) {

	_scriptLocation = location;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void setEncoding(String encoding) {

        _encoding = encoding;
    }





/**************************************************************************
 *
 * Adds a component to the end of the list of paths used by the
 * <code>import</code> function in the Tea program. The given path is
 * added to the list without any modifications.
 *
 * @param location The path being added to the path list. It may be an
 * URL or a file system path name.
 *
 **************************************************************************/

    public void addImportDirLocation(String location) {

	_importDirList.add(location);
    }





/**************************************************************************
 *
 * Executes the Tea script previously specified. If no script was
 * specified then it trys to read the script from the Java process
 * standard input.
 *
 * @param args The command line arguments to pass to the script. These
 * will be accesible in the Tea code as a list stored in the
 * <code>argv</code> variable.
 *
 * @exception IOException Thrown if there were any problems reading
 * the Tea script.
 *
 * @exception STeaException Thrown if there were any problems
 * compiling or executing the script. This exception can actually be
 * an instance of <code>{@link SCompileException}</code> or
 * <code>{@link SRuntimeException}</code>.
 *
 **************************************************************************/

    public int execute(String[] args)
	throws IOException,
	       STeaException {

	int         retVal  = 0;
	SCode       code    = compileScript();
	STeaRuntime context = new STeaRuntime();

        context.setArgv0(_scriptLocation);
        context.setArgv(args);
        context.setImportLocations(_importDirList);
        context.start();

	try {
	    context.execute(code);
	} catch (SExitException e2) {
	    retVal = e2._value.intValue();
	} catch (SFlowControlException e3) {
	    // Just ignore it. Somebody did a "return", "break" or
	    // "continue" outside of a function or loop.
	} finally {
	    context.stop();
	    context.end();
	}

	return retVal;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SCode compileScript()
	throws IOException,
	       STeaException {

	SCompiler compiler = new SCompiler();
	SCode     code     = null;

        if ( _scriptLocation == null ) {
            code = compiler.compile(System.in, _encoding, null);
        } else {
            code = compiler.compile(_scriptLocation,_encoding,_scriptLocation);
        }

	return code;
    }





/**************************************************************************
 *
 * Runs a Tea script contained in a file or read from the Java process
 * standard input.
 *
 * <p>The following command line options are supported:</p>
 *
 * <ul>
 *
 * <li><code>--library=<i>DIR_LIST</i></code> - List of directories to
 * be searched by the <code>import</code> function. Elements are
 * separated with a the path separator character (":" in unix, ";" in
 * windows). Each element can be a file system path or an URL. In the
 * case of an URL the character "|" must be used instead of ":", to
 * differentiate it from the unix path separator character. This
 * option may be specified multiple times with the effect of
 * concatening the path elements in the given order.</li>
 *
 * <li><code>--library-item=<i>PATH</i></code> - Adds an item to the
 * end of the list of directories to be searched by the
 * <code>import</code> function. The path may be either a file system
 * path or an URL. This option may be specified multiple
 * times. Contrary to the "--library" option, a URL may contain the
 * ":" character.</li>
 *
 * <li><code>--script=<i>PATH</i></code> - The Tea script to be
 * executed. If not specified or if "<code>-</code>" then the script
 * is read from the process standard input stream.</li>
 *
 * <li><code>--</code> - Signals that there are no more options. All
 * remaining arguments will be passed as command line arguments to the
 * Tea script.</li>
 *
 * </ul>
 *
 **************************************************************************/

    public static void main(String[] args) {

	int              retVal    = 0;
	boolean          isOk      = true;
	String           errorMsg  = null;
	STeaLauncher     shell     = new STeaLauncher();
	STeaLauncherArgs shellArgs = new STeaLauncherArgs();

	if ( isOk ) {
	    try {
		shellArgs.parse(args);
	    } catch (STeaException e) {
		isOk     = false;
		errorMsg = e.getMessage();
	    }
	}

	if ( isOk ) {
	    shell.setScriptLocation(shellArgs.getScriptPath());
            shell.setEncoding(shellArgs.getEncoding());

            for ( String libPath : shellArgs.getLibraryList() ) {
		shell.addImportDirLocation(libPath);
	    }
	}
	
	if ( isOk ) {
	    String[] scriptArgs = shellArgs.getScriptCliArgs();

	    try {
		retVal = shell.execute(scriptArgs);
	    } catch (IOException e) {
		isOk     = false;
		errorMsg = "Failed to read script - " + e.getMessage();
	    }  catch (SRuntimeException e1) {
		isOk     = false;
		errorMsg = e1.getFullMessage();
	    } catch (STeaException e) {
		isOk     = false;
		errorMsg = e.getMessage();
	    }
	}

	if ( !isOk ) {
	    if ( retVal == 0 ) {
		retVal = -1;
	    }
	    System.err.println("\nProblems: " + errorMsg);
	}

	System.exit(retVal);
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/
