/**************************************************************************
 *
 * Copyright (c) 2001-2008 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SFunctionImport.java,v 1.24 2007/07/21 10:50:37 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2008/04/18 Now uses an SInputSource to read from a file or
 * URL. (TSK-PDMFC-TEA-0044) (jfn)
 *
 * 2006/05/02 Changes with hashtables so that it correctly identifies
 * the same file by full pathname even if diferent file paths
 * are given in argument to the import command. (jpsl).
 *
 * 2005/02/14 Corrected bug that led to a stack overflow when doing
 * recursive imports.
 *
 * 2005/01/02 Refactored to check for modifications on the file being
 * imported since the last time that file was imported.
 *
 * 2003/02/24 Corrected a bug that prevented the files opened by the
 * "import" function from being explicitly closed. (jfn)
 *
 * 2002/02/06 Corrected bug that prevented the correct exception from
 * being generated when a file could not be "import"ed. (jfn)
 *
 * 2002/01/10 Now uses SObjPair.iterator() instead of
 * SObjPair.elements(). (jfn)
 *
 * 2001/09/03 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.lang;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.pdmfc.tea.SConfigInfo;
import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SCode;
import com.pdmfc.tea.compiler.SCompiler;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNoSuchVarException;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypes;
import com.pdmfc.tea.util.SInputSource;
import com.pdmfc.tea.util.SInputSourceFactory;





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

class SFunctionImport
    extends Object
    implements SObjFunction {





    private static final String PROP_LIB_VAR = "com.pdmfc.tea.libraryVarName";

    // The name of the Tea global variable with the list of directory
    // names where the "import" function looks for Tea
    // source files.
    public static final SObjSymbol LIB_VAR   =
        SObjSymbol.addSymbol(SConfigInfo.getProperty(PROP_LIB_VAR));

    // The parent context for the context where the code in the
    // imported files will be executed. Each imported file is executed
    // in a separate context.
    private SContext _rootContext = null;

    // Keys are import paths (String). Values are ImportItem
    // instances.
    private HashMap _itemsByPath = new HashMap();

    // Keys are import items full paths (String). Values are
    // ImportItem instances.
    private HashMap _itemsByFullPath = new HashMap();

    // Used to compile the code in the imported files.
    private SCompiler _compiler = new SCompiler();





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SFunctionImport(SContext rootContext) {

        _rootContext = rootContext;
    }





/**************************************************************************
 *
 * This method is supposed to be called with <code>args</code> having
 * at least one element.
 *
 * @exception STeaException Thrown if there is not two arguments for
 * the command or if there were any problems while executing the
 * imported file.
 *
 **************************************************************************/

    public Object exec(SObjFunction func,
                       SContext     context,
                       Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args[0], "file");
        }

        String     fileName = STypes.getString(args, 1);
        Object     result   = SObjNull.NULL;
        ImportItem item     = (ImportItem)_itemsByPath.get(fileName);

        if ( item != null ) {
            result = item.performImport();
        } else {
            result = searchAndImport(context, fileName);
        }

        if ( result == null ) {
            String   msg     = "could not import file \"{0}\"";
            Object[] fmtArgs = { fileName };
            throw new SRuntimeException(args[0], msg, fmtArgs);
        }

        return result;
    }





/**************************************************************************
 *
 * @return The result of evaluating the Tea code read from the
 * file. Or null if no file with the given name could be found.
 *
 **************************************************************************/

    private Object searchAndImport(SContext context,
                                   String   fileName)
        throws STeaException {

        Object   result  = null;
        SObjPair urlList = null;	

        try {
            urlList = (SObjPair)context.getVar(LIB_VAR);
        } catch (SNoSuchVarException e1) {
            // Variable TEA_LIBRARY has not been defined...
        } catch (ClassCastException e2) {
            // Variable TEA_LIBRARY does not contain a list...
        } 

        if ( urlList == null ) {
            urlList = new SObjPair(".", new SObjPair());
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
            ImportItem prevItem = (ImportItem)_itemsByFullPath.get(fullPath);

            if ( prevItem != null ) {
                // This same file has already been imported through
                // other base directory. It means we have found the
                // file to import and there is no need to search
                // through the remaining directories.  Add it to the
                // _itemsByPath because the same file might be seen by
                // distinct names.  Ex: filename may be
                // "html/HtmlP.tea" or just "HtmlP.tea" but the full
                // path is the same.
                //System.out.println("import preItem!=null "+fileName+" fullpath="+fullPath+" prevItemFp="+prevItem.getFullPath());
                _itemsByPath.put(fileName, prevItem);
                result = prevItem.performImport();
                break;
            } else {
                //System.out.println("import preItem==null "+fileName+" fullpath="+fullPath);
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

    private class ImportItem
        extends Object {





        private String  _importPath     = null;
        private String  _fullPath       = null;
        private long    _lastImportTime = 0;
        private boolean _isFile         = false;
        private File    _file           = null;




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public ImportItem(String baseDir,
                          String importPath) {

            _importPath = importPath;
            _fullPath   = baseDir + "/" + importPath;
            _isFile     = 
                baseDir.startsWith("/") ||
                baseDir.startsWith(".") ||
                ( (baseDir.length()>1) && (baseDir.charAt(1)==':') );
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
            throws STeaException {

            Object      result = null;
            String      path   = _fullPath;
            InputStream input  = null;
	    
            try {
                SInputSource inputSource =
                    SInputSourceFactory.createInputSource(path);

                input = inputSource.openStream();
            } catch (IOException e) {
                // The path does not exist or is not accessible.
            }

            // If the imput has been opened, try to compile and
            // execute the file.
            if ( input != null ) {
                // Record the import timestamp right now to prevent
                // eventual infinite recursion (if this file is
                // imported again while executing).
                _lastImportTime = 
		    _isFile ? 
		    _file.lastModified() :
		    System.currentTimeMillis();

                SContext execContext = _rootContext.newChild();
                SCode    code        = null;

                try {
                    code = _compiler.compile(input, _importPath);
                } finally {
                    // Try very hard to close the input.
                    try { input.close(); } catch (IOException e) {}
                }
		
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
            throws STeaException {

            if ( _lastImportTime == 0 ) {
                String   msg     = "file \"{0}\" has changed path";
                Object[] fmtArgs = { _importPath }; 
                throw new SRuntimeException(msg, fmtArgs);
            }

            Object result = SObjNull.NULL;

            if ( _isFile ) {
                long    lastModified  = _file.lastModified();
                boolean fileExists    = lastModified != 0;

                if ( fileExists ) {
                    if ( lastModified != _lastImportTime ) {
                        result     = tryToPerformImport();
                        fileExists = (result != null);
                    }
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

