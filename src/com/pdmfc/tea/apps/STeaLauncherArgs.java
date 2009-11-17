/**************************************************************************
 *
 * Copyright (c) 2007-2009 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
 *
 *
 * Revisions:
 *
 * 2009/1013 Backported to the 3.2.x branch. (TSK-PDMFC-TEA-0044) (jfn)
 *
 * 2009/03/11 Added the "--library-item" option. (jfn)
 *
 * 2008/04/18 Refactored use command lne options. This was done in
 * order to receive the Tea library path list as a command line
 * argument. (TSK-PDMFC-TEA-0044) (jfn)
 *
 * 2007/08/11 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.apps;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.pdmfc.tea.STeaException;





/**************************************************************************
 *
 * Parses the command line arguments given to a Tea shell invocation.
 *
 **************************************************************************/

class STeaLauncherArgs
    extends Object {





    private String OPTION_LIBRARY  = "--library";
    private String OPTION_LIB_ITEM = "--library-item";
    private String OPTION_SCRIPT   = "--script";
    private String OPTION_END      = "--";





    private boolean      _isParsingOptions  = true;
    private String       _scriptPath        = null;
    private List<String> _libraryList       = new ArrayList<String>();
    private List<String> _scriptCliArgsList = new ArrayList<String>();
    private String[]     _scriptCliArgs     = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public STeaLauncherArgs() {
    }





/**************************************************************************
 *
 * Parses the given command line arguments.
 *
 * @param cliArgs 
 *
 * @exception STeaException Thrown in the case of an unknown option.
 *
 **************************************************************************/

    public void parse(String[] cliArgs)
	throws STeaException {

	_isParsingOptions = true;
	_scriptPath       = null;
	_libraryList.clear();
	_scriptCliArgsList.clear();

	for ( int i=0, count=cliArgs.length; i<count; ++i ) {
	    parseArg(cliArgs[i]);
	}

	_scriptCliArgs = _scriptCliArgsList.toArray(new String[0]);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void parseArg(String arg)
	throws STeaException {

	if ( _isParsingOptions ) {
	    parseOption(arg);
	} else {
	    _scriptCliArgsList.add(arg);
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void parseOption(String arg)
	throws STeaException {

	SCliOption option      = new SCliOption(arg);
	String     optionName  = option.getName();
	String     optionValue = option.getValue();

	if ( OPTION_LIBRARY.equals(optionName) ) {
	    optionSetLibrary(optionValue);
        } else if ( OPTION_LIB_ITEM.equals(optionName) ) {
            optionAddLibItem(optionValue);
	} else if ( OPTION_SCRIPT.equals(optionName) ) {
	    optionSetScriptPath(optionValue);
	} else if ( OPTION_END.equals(optionName) ) {
	    _isParsingOptions = false;
	} else {
	    String   msg     = "Unknown option \"{0}\"";
	    Object[] fmtArgs = { optionName };
	    throw new STeaException(msg, fmtArgs);
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void optionSetLibrary(String libraryStr) {

	String pathSep = File.pathSeparator;

	if ( libraryStr != null ) {
	    StringTokenizer i=new StringTokenizer(libraryStr, pathSep);

	    while ( i.hasMoreTokens() ) {
		String path = i.nextToken();
		
		path = path.replace('|', ':');

                _libraryList.add(path);
	    }
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void optionAddLibItem(String path) {

        path = path.replace('|', ':');

        _libraryList.add(path);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void optionSetScriptPath(String path) {

	if ( path.equals("-") || path.equals("") ) {
	    // A null path means the script is to be read from stdin.
	    path = null;
	}

	_scriptPath = path;
    }





/**************************************************************************
 *
 * Retrieves the list of paths to be used for importing files.
 *
 **************************************************************************/

    public List<String> getLibraryList() {

	List<String> result = new ArrayList<String>(_libraryList);

	return result;
    }





/**************************************************************************
 *
 * Retrieves the path of the Tea script to execute. This could be
 * either a path name in the file system or an URL.
 *
 * <p>It returns the value of the "<code>--script</code>" option.
 *
 * @return 
 *
 **************************************************************************/

    public String getScriptPath() {

	return _scriptPath;
    }





/**************************************************************************
 *
 * Retrieves the command line arguments that are to be passed to the
 * Tea script. These are the arguments passed to <code>{@link
 * #parse(String[])}</code> after the "<code>--</code>" option.
 *
 * @return The CLI arguments the Tea script is to be executed with.
 *
 **************************************************************************/

    public String[] getScriptCliArgs() {

	return _scriptCliArgs;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static final class SCliOption
	extends Object {





	private String _optionName  = null;
	private String _optionValue = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

	public SCliOption(String arg) {

	    String name     = null;
	    String value    = null;
	    int    sepIndex = arg.indexOf('=');

	    if ( sepIndex >= 0 ) {
		name  = arg.substring(0, sepIndex);
		value = arg.substring(sepIndex+1);
	    } else {
		name  = arg;
		value = "";
	    }

	    _optionName  = name;
	    _optionValue = value;
	}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

	public String getName() {

	    return _optionName;
	}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

	public String getValue() {

	    return _optionValue;
	}


    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

