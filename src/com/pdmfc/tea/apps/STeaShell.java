/**************************************************************************
 *
 * Copyright (c) 2001, 2002, 2003, 2004 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: STeaShell.java,v 1.12 2005/11/04 05:50:04 jfn Exp $
 *
 *
 * Revisions:
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

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.pdmfc.tea.SConfigInfo;
import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SCode;
import com.pdmfc.tea.compiler.SCompileException;
import com.pdmfc.tea.compiler.SCompiler;
import com.pdmfc.tea.runtime.SExitException;
import com.pdmfc.tea.runtime.SFlowControlException;
import com.pdmfc.tea.runtime.STeaRuntime;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * Implements a Tea shell. 
 *
 **************************************************************************/

public class STeaShell
    extends Object {




    private static final String PROP_ARGV0_VAR =
	SConfigInfo.getProperty("com.pdmfc.tea.argv0VarName");

    private static final SObjSymbol ARGV0_VAR =
	SObjSymbol.addSymbol(PROP_ARGV0_VAR);

    private static final String PROP_ARGV_VAR =
	SConfigInfo.getProperty("com.pdmfc.tea.argvVarName");

    private static final SObjSymbol ARGV_VAR_NAME =
	SObjSymbol.addSymbol(PROP_ARGV_VAR);

    private static final String PROP_LIB_VAR =
	SConfigInfo.getProperty("com.pdmfc.tea.libraryVarName");

    private static final String PROP_USE_RESOURCES =
	SConfigInfo.getProperty("com.pdmfc.tea.useJavaResources");

    private static final int MODE_PATH     = 0;
    private static final int MODE_RESOURCE = 1;





    private URL _scriptUrl  = null;

    // The value passed to the setScriptFile(String) method. Passed to
    // the Tea context as the value of the "argv0" variable.
    private String _scriptFile = null;

    private List _importDirList = new ArrayList();





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public STeaShell() {
    }





/**************************************************************************
 *
 * Specifies the pathname of the file containing the Tea script to be
 * executed. The Tea script will be executed when the <code>{@link
 * #execute(String[])}</code> method is called.
 *
 * @param path A file path name.
 *
 * @exception IOException Thrown if the given <code>path</code> is not
 * valid.
 *
 **************************************************************************/

    public void setScriptFile(String path) {

	_scriptFile = path;
	_scriptUrl  = null;
    }





/**************************************************************************
 *
 * Specifies the URL where the script to be executed is to be read
 * from. The URL contents will only be read when the <code>{@link
 * #execute(String[])}</code> method is called.
 *
 * @param url The URL to read the Tea script from.
 *
 **************************************************************************/

    public void setScriptUrl(URL url) {

	_scriptFile = null;
	_scriptUrl  = url;
    }





/**************************************************************************
 *
 * Specifies a Java resource whose contents are the Tea script to
 * execute. The resource is read only when the <code>{@link
 * #execute(String[])}</code> method is called.
 *
 * @param resourceName The Java resource containing the Tea script to
 * be executed.
 *
 **************************************************************************/

    public void setScriptResource(String resourceName) {

	URL scriptUrl = STeaShell.class.getResource(resourceName);

	setScriptUrl(scriptUrl);
    }





/**************************************************************************
 *
 * Adds a component to the end of the list of paths used by the
 * <code>import</code> function in the Tea program. The given path is
 * added to the list without any modifications.
 *
 * @param path The path being added to the path list.
 *
 **************************************************************************/

    public void addImportDirPath(String path) {

	_importDirList.add(path);
    }





/**************************************************************************
 *
 * Addes a component to the end of the list of paths used by the
 * <code>import</code> function in the Tea program. The given path is
 * assumed to be a Java resource name. The component added to the path
 * list is the URL representing that Java resource.
 *
 * @param resourceName The name of a Java resource that will be added
 * to the list of import directories.
 *
 **************************************************************************/

    public void addImportDirResource(String resourceName) {

	URL    importDirUrl = STeaShell.class.getResource(resourceName);
	String dirComponent = importDirUrl.toString();

	_importDirList.add(dirComponent);
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

	int         retVal     = 0;
	String      errMsg     = null;
	SCode       code       = compileScript();
	STeaRuntime context    = new STeaRuntime();

	try {
	    if ( _scriptFile != null ) {
		context.newVar(ARGV0_VAR, _scriptFile);
	    }
	    setCliArgs(context, args, 0, args.length);
	    context.setImportDirs(_importDirList);
	    context.start();
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

	SCompiler   compiler = new SCompiler();
	SCode       code     = null;

	if ( _scriptFile != null ) {
	    code = compiler.compile(_scriptFile);
	} else {
	    InputStream script = 
		(_scriptUrl==null) ? System.in : _scriptUrl.openStream();
	    code = compiler.compile(script);
	}

	return code;
    }





/**************************************************************************
 *
 * Specifies the command line arguments that are passed to the Tea
 * interpreter. This just entails creating the <code>argv</code> Tea
 * global variable. This Tea variable will contain a list of strings
 * corresponding to the command line arguments.
 *
 * @param runtime The runtime where the variable will be created.
 *
 * @param args Contains the command line arguments.
 *
 * @param start The index in <code>args</code> of the first string to
 * be used as command line argument.
 *
 * @param count The number of elements int <code>args</code> that will
 * be used as command line arguments.
 *
 **************************************************************************/

    private void setCliArgs(STeaRuntime runtime,
			    String[]    args,
			    int         start,
			    int         count) {

      SObjPair head = SObjPair.emptyList();
      SObjPair elem = null;

      for ( int i=start+count-1; i>=start; i-- ) {
	  head = new SObjPair(args[i], head);
      }

      runtime.newVar(ARGV_VAR_NAME, head);
   }





/**************************************************************************
 *
 * Runs a Tea script contained in a file or read from the Java process
 * standard input.
 *
 * <p>The first command line argument is taken to be the pathname of a
 * Tea script and it will be executed. All the remaining arguments are
 * passed as command line arguments to the Tea script. If no command
 * line arguments are given then it reads a Tea script from standard
 * input.</p>
 *
 **************************************************************************/

    public static void main(String[] args) {

	int       mode       = setupMode();
	STeaShell shell      = new STeaShell();
	String    script     = (args.length>0) ? args[0] : null;
	int       argCount   = (args.length<=1) ? 0 : (args.length-1);
	String[]  scriptArgs = new String[argCount];
	String    errMsg     = null;
	int       retVal     = 0;

	if ( script != null ) {
	    System.arraycopy(args, 1, scriptArgs, 0, argCount);
	    setupScript(mode, script, shell);
	}
	setupImportDirList(mode, shell);

	try {
	    retVal = shell.execute(scriptArgs);
	} catch (IOException e) {
	    errMsg = "Failed to read script - " + e.getMessage();
	}  catch (SRuntimeException e1) {
	    errMsg = e1.getFullMessage();
	} catch (STeaException e) {
	    errMsg = e.getMessage();
	}

	if ( errMsg != null ) {
            if ( retVal == 0 ) {
                retVal = -1;
            }
	    System.err.println("\nProblems: " + errMsg);
	}

	System.exit(retVal);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static int setupMode() {

	int mode = MODE_PATH;

	if ( System.getProperty(PROP_USE_RESOURCES) != null ) {
	    mode = MODE_RESOURCE;
	}

	return mode;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static void setupScript(int       mode,
				    String    script,
				    STeaShell shell) {

	if ( mode == MODE_PATH ) {
	    shell.setScriptFile(script);
	} else {
	    shell.setScriptResource(script);
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static void setupImportDirList(int       mode,
					   STeaShell shell) {

	String importDirs = System.getProperty(PROP_LIB_VAR);
	String pathSep    = 
	    (mode==MODE_PATH) ? File.pathSeparator : " ";

	if ( importDirs != null ) {
	    for ( StringTokenizer i=new StringTokenizer(importDirs,pathSep);
		  i.hasMoreTokens(); ) {
		String path = i.nextToken();
		
		path = path.replace('|', ':');

		if ( mode == MODE_PATH ) {
		    shell.addImportDirPath(path);
		} else {
		    shell.addImportDirResource(path);
		}
	    }
	}
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

