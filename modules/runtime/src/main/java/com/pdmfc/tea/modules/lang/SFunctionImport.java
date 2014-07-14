/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.lang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.pdmfc.tea.TeaConfigInfo;
import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.compiler.TeaCode;
import com.pdmfc.tea.compiler.TeaCompiler;
import com.pdmfc.tea.runtime.Args;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.SNoSuchVarException;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaNull;
import com.pdmfc.tea.runtime.TeaPair;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.TeaEnvironment;





//* 
//* <TeaFunction name="import"
//*             arguments="fileName"
//*             module="tea.lang">
//*
//* <Overview>
//* Executes Tea code read from a file.
//* </Overview>
//*
//* <Parameter name="fileName">
//* String representing a pathname relative to one of the Tea library
//* directories.
//* </Parameter>
//* 
//* <Returns>
//* The result of the last top-level statement executed in
//* <Arg name="fileName"/>.
//* If <Arg name="fileName"/> was previously imported
//* and it is not re-executed,
//* it returns <code>$null</code>.
//* (It is not advisable to write code that depends on the return value
//* of <code>import</code>, as this behaviour might change in future versions.)
//* </Returns>
//*
//* <Description>
//* The Tea code is only executed the first time the <Func name="import"/>
//* function is called.
//* 
//* <p>
//* If the <code>import</code> function is called subsequently for the same
//* <Arg name="fileName"/>,
//* its contents will be read and executed again only if the file's last
//* modification timestamp has changed since the last time it was executed. This
//* behaviour only applies when the file is read from the filesystem. If the
//* <Arg name="fileName"/>
//* is found within an archived library (jar) or other URL library source,
//* it will only be imported once.
//* </p>
//* 
//* <p>The global variable <Var name="TEA_LIBRARY"/> should define a list
//* of strings representing URLs where
//* <Arg name="fileName"/> is searched. If <Var name="TEA_LIBRARY"/>
//* is undefined or not a list, <Arg name="fileName"/> is
//* searched in the current directory. If <Var name="TEA_LIBRARY"/>
//* is an empty list, the file is never searched and <Func name="import"/>
//* will fail. If two different <Arg name="fileName"/> are mapped into the
//* same full path name, they are considered to be the same file.</p>
//*
//* <p>The execution of the Tea code is done in a new execution context,
//* descendent from the global context. As such, if the Tea code executed
//* defines new variables with top level <code>define</code> statements,
//* these variable names will not be know outside the scope of
//* <Arg name="fileName"/>, but its lifetime will be the same as the Tea
//* process. (Similar to static variables in the C programming language,
//* wich are only known inside the module that defines them.)
//* </p>
//*
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the "<code>import</code>" function.
 *
 **************************************************************************/

final class SFunctionImport
    extends Object
    implements TeaFunction {





    private static final String PROP_LIB_VAR = "com.pdmfc.tea.libraryVarName";

    // The name of the Tea global variable with the list of directory
    // names where the "import" function looks for Tea
    // source files.
    public static final TeaSymbol LIB_VAR   =
        TeaSymbol.addSymbol(TeaConfigInfo.get(PROP_LIB_VAR));

    // The environment where the code in the imported files will be
    // executed. Each imported file will executed in a separate
    // context descending from the global context.
    private TeaEnvironment _environment = null;

    // Keys are import paths (String). Values are ImportItem
    // instances.
    private Map<String,ImportItem> _itemsByPath =
        new HashMap<String,ImportItem>();

    // Keys are import items full paths (String). Values are
    // ImportItem instances.
    private Map<String,ImportItem> _itemsByFullPath =
        new HashMap<String,ImportItem>();





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SFunctionImport(final TeaEnvironment environment) {

        _environment = environment;
    }





/**************************************************************************
 *
 * This method is supposed to be called with <code>args</code> having
 * at least one element.
 *
 * @exception TeaException Thrown if there is not two arguments for
 * the command or if there were any problems while executing the
 * imported file.
 *
 **************************************************************************/

    public Object exec(final TeaFunction func,
                       final TeaContext     context,
                       final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "file");
        }

        String     fileName = Args.getString(args, 1);
        Object     result   = TeaNull.NULL;
        ImportItem item     = _itemsByPath.get(fileName);

        if ( item != null ) {
            result = item.performImport();
        } else {
            result = searchAndImport(context, fileName);
        }

        if ( result == null ) {
            String msg = "could not import file \"{0}\"";
            throw new SRuntimeException(args, msg, fileName);
        }

        return result;
    }





/**************************************************************************
 *
 * @return The result of evaluating the Tea code read from the
 * file. Or null if no file with the given name could be found.
 *
 **************************************************************************/

    private Object searchAndImport(final TeaContext context,
                                   final String   fileName)
        throws TeaException {

        Object   result  = null;
        TeaPair urlList = null;        

        try {
            urlList = (TeaPair)context.getVar(LIB_VAR);
        } catch (SNoSuchVarException e1) {
            // Variable TEA_LIBRARY has not been defined...
        } catch (ClassCastException e2) {
            // Variable TEA_LIBRARY does not contain a list...
        } 

        if ( urlList == null ) {
            urlList = new TeaPair(".", new TeaPair());
        }

        for ( Iterator i=urlList.iterator(); i.hasNext(); ) {
            String baseDir = null;

            try {
                baseDir = (String)i.next();
            } catch (ClassCastException e3) {
                // One of the elements of the TEA_LIBRARY list is not
                // a string. Just ignore it and continue with the next
                // element. We should probably blow up...
                continue;
            }

            ImportItem item     = new ImportItem(baseDir, fileName);
            String     fullPath = item.getFullPath();
            ImportItem prevItem = _itemsByFullPath.get(fullPath);

            if ( prevItem != null ) {
                // This same file has already been imported through
                // other base directory. It means we have found the
                // file to import and there is no need to search
                // through the remaining directories.  Add it to the
                // _itemsByPath because the same file might be seen by
                // distinct names.  Ex: filename may be
                // "html/HtmlP.tea" or just "HtmlP.tea" but the full
                // path is the same.
                _itemsByPath.put(fileName, prevItem);
                result = prevItem.performImport();
                break;
            } else {
                // Add the item to both hashtables right now to
                // prevent an eventual import infinite recursion.
                _itemsByPath.put(fileName, item);
                _itemsByFullPath.put(fullPath, item);
                if ( (result=item.tryToPerformImport()) != null ) {
                    // File to be imported has been found, and
                    // imported. No need to search through the
                    // remaining directories.
                    break;
                } else {
                    // The file was not found, or there is an
                    // error. Remove the filenames from the
                    // hashtables.
                    _itemsByPath.remove(fileName);
                    _itemsByFullPath.remove(fullPath);
                }
            }
        }

        return result;
    }




/**************************************************************************
 *
 * Represents an item to be imported.
 *
 **************************************************************************/

    private final class ImportItem
        extends Object {





        private String  _importPath     = null;
        private String  _fullPath       = null;
        private long    _lastImportTime = 0;
        private boolean _isFile         = false;
        private File    _file           = null;

        // Used to compile the code in the imported files.
        private TeaCompiler _compiler = new TeaCompiler();




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public ImportItem(final String   baseDir,
                          final String   importPath) {

            _importPath  = importPath;
            _fullPath    = baseDir + "/" + importPath;
            _isFile      = 
                baseDir.startsWith("/")
                || baseDir.startsWith(".")
                || ( (baseDir.length()>1) && (baseDir.charAt(1)==':') );

            if ( _isFile ) {
                _file = new File(_fullPath);
            }
        }




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public String getFullPath() {
            
            return _fullPath;
        }





/**************************************************************************
 *
 * @return Null if the path does not exist or could not be
 * read. Otherwise the result of executing the Tea code will be
 * returned.
 *
 **************************************************************************/

        public Object tryToPerformImport()
            throws TeaException {

            Object  result        = null;
            String  path          = _fullPath;
            Charset sourceCharset = _environment.getSourceCharset();
            TeaCode code          = null;
            
            try {
                code = _compiler.compile(path, sourceCharset, _importPath);
            } catch (IOException e) {
                // The path does not exist or is not accessible.
            }

            // If the input has been opened, try to execute the file.
            if ( code != null ) {
                // Record the import timestamp right now to prevent
                // eventual infinite recursion (if this file is
                // imported again while executing).
                _lastImportTime = 
                    _isFile
                    ? _file.lastModified()
                    : System.currentTimeMillis();

                TeaContext globalContext = _environment.getGlobalContext();
                TeaContext execContext   = globalContext.newChild();
                
                result = code.exec(execContext);
            }

            return result;
        }





/**************************************************************************
 *
 * Will execute the Tea script if both: a) It is a regular file; b)
 * The file modification date differs from the one at the last time the
 * file was imported.
 *
 * @return Null if the path does not exist or could not be
 * read. Otherwise the result of executing the Tea code will be
 * returned.
 *
 **************************************************************************/

        public Object performImport()
            throws TeaException {

            if ( _lastImportTime == 0 ) {
                String   msg     = "file \"{0}\" has changed path";
                Object[] fmtArgs = { _importPath }; 
                throw new SRuntimeException(msg, fmtArgs);
            }

            Object result = TeaNull.NULL;

            if ( _isFile ) {
                long    lastModified  = _file.lastModified();
                boolean fileExists    = lastModified != 0;

                if ( fileExists && (lastModified!=_lastImportTime) ) {
                    result     = tryToPerformImport();
                    fileExists = (result != null);
                }

                if ( !fileExists ) {
                    String   msg     = "file \"{0}\" no longer exists";
                    Object[] fmtArgs = { _importPath }; 
                    throw new SRuntimeException(msg, fmtArgs);
                }
            }

            return result;
        }


    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

