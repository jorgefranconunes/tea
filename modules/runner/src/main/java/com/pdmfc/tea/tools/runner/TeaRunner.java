/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.tools.runner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.compiler.TeaCode;
import com.pdmfc.tea.compiler.TeaCompiler;
import com.pdmfc.tea.runtime.SExitException;
import com.pdmfc.tea.runtime.SFlowControlException;
import com.pdmfc.tea.runtime.TeaRuntime;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.TeaRuntimeConfig;
import com.pdmfc.tea.tools.runner.TeaRunnerArgs;





/**************************************************************************
 *
 * A Tea shell that runs a Tea program with command line arguments.
 *
 **************************************************************************/

public final class TeaRunner
    extends Object {





/**************************************************************************
 *
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private TeaRunner() {

        // Nothing to do.
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
 * <li><code>--encoding=<i>STRING</i></code> - The character encoding
 * of the input script. If not specified it will default to the
 * platform default encoding.</li>
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

        int           retVal    = -1;
        boolean       isOk      = true;
        String        errorMsg  = null;
        TeaRunnerArgs shellArgs = null;

        if ( isOk ) {
            try {
                shellArgs = TeaRunnerArgs.parse(args);
            } catch (TeaException e) {
                isOk     = false;
                errorMsg = e.getMessage();
            }
        }
        
        if ( isOk ) {
            try {
                retVal = execute(shellArgs);
            } catch ( IOException e ) {
                isOk     = false;
                errorMsg = "Failed to read script - " + e.getMessage();
            }  catch ( SRuntimeException e ) {
                isOk     = false;
                errorMsg = e.getFullMessage();
            } catch ( TeaException e ) {
                isOk     = false;
                errorMsg = e.getMessage();
            }
        }

        if ( !isOk ) {
            System.err.println("\nProblems: " + errorMsg);
        }

        System.exit(retVal);
    }





/**************************************************************************
 *
 * 
 *
 * @param args The command line arguments to pass to the script.
 *
 * @exception IOException Thrown if there were any problems reading
 * the Tea script.
 *
 * @exception TeaException Thrown if there were any problems
 * compiling or executing the script.
 *
 * @return The system exit status obtained from the execution of the
 * Tea code.
 *
 **************************************************************************/

    private static int execute(final TeaRunnerArgs args)
        throws IOException,
               TeaException {

        int              retVal     = 0;
        String           scriptPath = args.getScriptPath();
        Charset          charset    = findCharset(args.getEncoding());
        TeaCode          code       = compileScript(scriptPath, charset);
        TeaRuntimeConfig config     =
            TeaRuntimeConfig.Builder.start()
            .setArgv0(scriptPath)
            .setArgv(args.getScriptCliArgs())
            .setSourceCharset(charset)
            .setImportLocationList(args.getLibraryList())
            .build();

        TeaRuntime context = new TeaRuntime(config);

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

    private static TeaCode compileScript(final String  scriptPath,
                                         final Charset charset)
        throws IOException,
               TeaException {

        TeaCompiler compiler = new TeaCompiler();
        TeaCode     code     = null;

        if ( scriptPath == null ) {
            code = compiler.compile(System.in, charset, null);
        } else {
            code = compiler.compile(scriptPath, charset, scriptPath);
        }

        return code;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Charset findCharset(final String charsetName)
        throws TeaException {

        Charset charset = null;

        if ( charsetName != null ) {
            try {
                charset = Charset.forName(charsetName);
            } catch ( UnsupportedCharsetException e ) {
                String   msg     = "Unsupported charset \"{0}\"";
                Object[] fmtArgs = { charsetName };
                throw new TeaException(msg, fmtArgs);
            }
        }

        return charset;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

