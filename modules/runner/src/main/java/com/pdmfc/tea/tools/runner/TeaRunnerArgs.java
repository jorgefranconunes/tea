/**************************************************************************
 *
 * Copyright (c) 2007-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.tools.runner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.pdmfc.tea.STeaException;





/**************************************************************************
 *
 * Parses the command line arguments given to a Tea shell invocation.
 *
 **************************************************************************/

final class TeaRunnerArgs
    extends Object {





    private static final String OPTION_LIBRARY  = "--library";
    private static final String OPTION_LIB_ITEM = "--library-item";
    private static final String OPTION_SCRIPT   = "--script";
    private static final String OPTION_ENCODING = "--encoding";
    private static final String OPTION_END      = "--";





    private boolean      _isParsingOptions  = true;
    private String       _scriptPath        = null;
    private String       _encoding          = null;
    private List<String> _libraryList       = new ArrayList<String>();
    private List<String> _scriptCliArgsList = new ArrayList<String>();
    private String[]     _scriptCliArgs     = null;





/**************************************************************************
 *
 * Instances for external use are only created by the factory method
 * "parse(...)".
 *
 **************************************************************************/

    private TeaRunnerArgs() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * Parses the given command line arguments and returns a new instance
 * dutifully initialized.
 *
 * @param cliArgs The command line arguments to be passed to the Tea
 * runner.
 *
 * @exception STeaException Thrown in the case of an unknown option.
 *
 **************************************************************************/

    public static TeaRunnerArgs parse(final String[] cliArgs)
        throws STeaException {

        TeaRunnerArgs result = new TeaRunnerArgs();

        result.doParse(cliArgs);

        return result;
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
 * @return The value of the "<code>--script</code>" option. Null if
 * that option was not present or if it was an empty string or "-".
 *
 **************************************************************************/

    public String getScriptPath() {

        return _scriptPath;
    }





/**************************************************************************
 *
 * @return The value of the "<code>--encoding</code>" option. Null if
 * the option was not present or if it was en empty string.
 *
 **************************************************************************/

    public String getEncoding() {

        return _encoding;
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
 * Parses the given command line arguments.
 *
 * @param cliArgs The command line arguments passed to the Tea
 * launcher.
 *
 * @exception STeaException Thrown in the case of an unknown option.
 *
 **************************************************************************/

    private void doParse(final String[] cliArgs)
        throws STeaException {

        _isParsingOptions = true;
        _scriptPath       = null;
        _libraryList.clear();
        _scriptCliArgsList.clear();

        for ( String arg : cliArgs ) {
            parseArg(arg);
        }

        _scriptCliArgs = _scriptCliArgsList.toArray(new String[0]);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void parseArg(final String arg)
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

    private void parseOption(final String arg)
        throws STeaException {

        CliOption option      = new CliOption(arg);
        String    optionName  = option.getName();
        String    optionValue = option.getValue();

        if ( OPTION_LIBRARY.equals(optionName) ) {
            optionSetLibrary(optionValue);
        } else if ( OPTION_LIB_ITEM.equals(optionName) ) {
            optionAddLibItem(optionValue);
        } else if ( OPTION_SCRIPT.equals(optionName) ) {
            optionSetScriptPath(optionValue);
        } else if ( OPTION_ENCODING.equals(optionName) ) {
            optionSetEncoding(optionValue);
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

    private void optionSetLibrary(final String libraryStr) {

        String pathSep = File.pathSeparator;

        if ( libraryStr != null ) {
            for ( String path : libraryStr.split(pathSep) ) {
                optionAddLibItem(path);
            }
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void optionAddLibItem(final String path) {

       String processedPath = path.replace('|', ':');

        _libraryList.add(processedPath);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void optionSetScriptPath(final String path) {

        String scriptPath = path;

        if ( scriptPath.equals("-") || scriptPath.equals("") ) {
            // A null path means the script is to be read from stdin.
            scriptPath = null;
        }

        _scriptPath = scriptPath;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void optionSetEncoding(final String enc) {

        String encoding = enc;

        if ( (encoding!=null) && encoding.equals("") ) {
            encoding = null;
        }

        _encoding = encoding;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static final class CliOption
        extends Object {





        private String _optionName  = null;
        private String _optionValue = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public CliOption(final String arg) {

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

