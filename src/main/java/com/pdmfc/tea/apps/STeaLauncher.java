/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.apps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public final class STeaLauncher
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

        // Nothing to do.
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

    public void setScriptLocation(final String location) {

        _scriptLocation = location;
    }





/**************************************************************************
 *
 * Specifies the encoding of the Tea source files to be executed.
 *
 * <p>If this method is not called the platform default encoding will
 * be used.</p>
 *
 * @param encoding The encoding to be assumed for the Tea source
 * files.
 *
 **************************************************************************/

    public void setEncoding(final String encoding) {

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

    public void addImportDirLocation(final String location) {

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
 * compiling or executing the script.
 *
 * @return The system exit status obtained from the execution of the
 * Tea code.
 *
 **************************************************************************/

    public int execute(final String[] args)
        throws IOException,
               STeaException {

        int         retVal  = 0;
        SCode       code    = compileScript();
        STeaRuntime context = new STeaRuntime();

        context.setArgv0(_scriptLocation);
        context.setArgv(args);
        context.setSourceEncoding(_encoding);
        context.setImportLocations(_importDirList);
        context.start();

        try {
            context.execute(code);
        } catch (SExitException e2) {
            retVal = e2.getExitValue().intValue();
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
 * @param args The command line arguments passed to this Java program.
 *
 **************************************************************************/

    public static void main(final String[] args) {

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

