/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SModuleIO.java,v 1.10 2002/08/02 17:47:24 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/01/20
 * Calls to the "addJavaFunction()" method were replaced by inner
 * classes for performance. (jfn)
 *
 * 2002/01/10
 * This classe now derives from SModuleCore. (jfn)
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.modules.io.SInput;
import com.pdmfc.tea.modules.tos.SJavaClass;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.runtime.STeaRuntime;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SMalformedListException;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;
import com.pdmfc.tea.runtime.SUtils;





//*
//* <TeaModule name="tea.io">
//* 
//* <Overview>
//* File and I/O functions and classes.
//* </Overview>
//*
//* <Description>
//* File and I/O functions and classes.
//* <P>
//* When this module is loaded two global variables will be created:
//* <Var name="stdin"/> and <Var name="stdout"/>. The <Var name="stdin"/>
//* variable contains an instance of <ClassRef name="TInput"/>
//* associated with the process standard input. The <Var name="stdout"/>
//* variable contains an instance
//* of <ClassRef name="TOutput"/> associated with the process standard
//* output.
//* </P>
//* </Description>
//*
//* </TeaModule>
//*





/**************************************************************************
 *
 * Package of I/O related Tea commands.
 *
 **************************************************************************/

public class SModuleIO
    extends SModule {





    private static final int BUFFER_SIZE = 4096;

    private SInput  _stdin  = null;
    private SOutput _stdout = null;
    private SOutput _stderr = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SModuleIO() {
    }





/**************************************************************************
 *
 * Creates commands and global variables related with I/O.
 *
 * @param context
 *    The context where the commands and variables will be created.
 *
 **************************************************************************/

    public void init(STeaRuntime context)
	throws STeaException {

	super.init(context);

	STosClass inClass  = new SJavaClass("com.pdmfc.tea.modules.io.SInput");
	STosClass outClass = new SJavaClass("com.pdmfc.tea.modules.io.SOutput");

	context.newVar(inClass.getName(), inClass);
	context.newVar(outClass.getName(), outClass);

	_stdin  = SInput.newInstance(context);
	_stdout = SOutput.newInstance(context);
	_stderr = SOutput.newInstance(context);
	
	_stdin.open(System.in);
	_stdout.open(System.out);
	_stdout.setLineBuffering(true);
	_stderr.open(System.err);

	context.newVar("stdin",  _stdin);
	context.newVar("stdout", _stdout);
	context.newVar("stderr", _stderr);

	context.addFunction("file-basename",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionBasename(func, context, args);
				    }
				});

	context.addFunction("file-copy",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionCopy(func, context, args);
				    }
				});

	context.addFunction("file-dirname",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionDirname(func, context, args);
				    }
				});

	context.addFunction("file-extension",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionExtension(func, context, args);
				    }
				});

	context.addFunction("file-exists?",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionFileExists(func, context, args);
				    }
				});

	context.addFunction("file-is-dir?",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionIsDir(func, context, args);
				    }
				});

	context.addFunction("file-is-regular?",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionIsRegular(func, context, args);
				    }
				});

	context.addFunction("file-join",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionJoin(func, context, args);
				    }
				});

	context.addFunction("file-mkdir",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionMkdir(func, context, args);
				    }
				});

	context.addFunction("file-rename",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionRename(func, context, args);
				    }
				});

	context.addFunction("file-size",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionSize(func, context, args);
				    }
				});

	context.addFunction("file-split-path-list",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionSplitPathList(func, context, args);
				    }
				});

	context.addFunction("file-unlink",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionUnlink(func, context, args);
				    }
				});
    }





/**************************************************************************
 *
 * Signals that the package will not be used util another call to the
 * <TT>start()</TT> method. It flushes the output buffers.
 *
 **************************************************************************/

    public void stop() {

	if ( _stdout != null ) {
	    try {
		_stdout.flush();
	    } catch (IOException e1) {
	    } catch (STeaException e2) {
	    }
	}
	if ( _stderr != null ) {
	    try {
		_stderr.flush();
	    } catch (IOException e3) {
	    } catch (STeaException e4) {
	    }
	}
    }





//* 
//* <TeaFunction name="file-basename"
//* 		arguments="pathName"
//*             module="tea.io">
//*
//* <Overview>
//* Retrieves the basename component of a path name.
//* </Overview>
//*
//* <Parameter name="pathName">
//* String representing a path name.
//* </Parameter>
//*
//* <Returns>
//* A string representing the basename component of
//* <Arg name="pathName"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionBasename(SObjFunction func,
					   SContext     context,
					   Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args[0], "file-name");
	}

	return (new File(STypes.getString(args,1))).getName();
    }





//* 
//* <TeaFunction name="file-copy"
//* 		arguments="sourcePath destPath"
//*             module="tea.io">
//*
//* <Overview>
//* Makes a copy of a file.
//* </Overview>
//*
//* <Parameter name="sourcePath">
//* The path name of the file to be copied.
//* </Parameter>
//*
//* <Parameter name="destPath">
//* The path name of the file that will be a copy of
//* <Arg name="sourcePath"/>.
//* </Parameter>
//*
//* <Returns>
//* True if the file was successfully copied. False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionCopy(SObjFunction func,
				       SContext     context,
				       Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException(args[0], "src-file dst-file");
	}
	File    srcFile = new File(STypes.getString(args,1));
	File    dstFile = new File(STypes.getString(args,2));
	boolean status  = true;

	try {
	    String srcName = srcFile.getCanonicalPath();
	    String dstName = dstFile.getCanonicalPath();

	    if ( !srcName.equals(dstName) ) {
		copyFile(srcFile, dstFile);
	    }
	} catch (IOException e) {
	    status = false;
	}

	return status ? Boolean.TRUE : Boolean.FALSE;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static void copyFile(File src,
				 File dst)
	throws IOException {

	byte[]           buffer = new byte[BUFFER_SIZE];
	FileInputStream  in     = new FileInputStream(src);
	FileOutputStream out    = new FileOutputStream(dst);
	int              count;

	try {
	    while ( (count=in.read(buffer)) != -1 ) {
		out.write(buffer, 0, count);
	    }
	} catch (IOException e) {
	    try {
		in.close();
		out.close();
	    } catch (Exception e2) {
	    }
	    throw e;
	}
    }





//* 
//* <TeaFunction name="file-dirname"
//* 		arguments="pathName"
//*             module="tea.io">
//*
//* <Overview>
//* Retrieves the directory part of a path name.
//* </Overview>
//*
//* <Parameter name="pathName">
//* String representing a path name.
//* </Parameter>
//*
//* <Returns>
//* A string representing the parent directory of <Arg name="pathName"/>
//* or null if <Arg name="pathName"/> has no parent directory.
//* </Returns>
//*
//* <Description>
//* If <Arg name="pathName"/> is comprised of just a base name
//* (e.g. "hello.com.pdmfc.tea.) then the parent directory is taken to be ".".
//* <P>Some <Arg name="pathName"/> may have no parent directory. This
//* is the case of root directories (e.g. "/" in unix, "c:\" in
//* windows).</P>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionDirname(SObjFunction func,
					  SContext     context,
					  Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args[0], "file-name");
	}

	String path    = STypes.getString(args,1);
	File   aFile   = new File(path);
	String dirName = aFile.getParent();

	if ( (dirName==null) && !aFile.isAbsolute() ) {
	    dirName = ".";
	}

	return (dirName==null) ? SObjNull.NULL : dirName;
    }





//* 
//* <TeaFunction name="file-extension"
//* 		arguments="pathName"
//*             module="tea.io">
//*
//* <Overview>
//* Retrieves the extension of a file name.
//* </Overview>
//*
//* <Parameter name="pathName">
//* String representing a path name.
//* </Parameter>
//*
//* <Returns>
//* A string representing the extension part of <Arg name="pathName"/>.
//* </Returns>
//*
//* <Description>
//* The extension is the part of the path name following the last dot
//* character in the base name of <Arg name="pathName"/>. If the base
//* name has no dot than an empty string returned.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionExtension(SObjFunction func,
					    SContext     context,
					    Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args[0], "file-name");
	}

	String pathName   = STypes.getString(args, 1);
	String baseName   = (new File(pathName)).getName();
	int    indexOfDot = baseName.lastIndexOf('.');
	String extension  = (indexOfDot<0) ?
	    "" : baseName.substring(indexOfDot+1);

	return extension;
    }





//* 
//* <TeaFunction name="file-exists?"
//* 		arguments="pathName"
//*             module="tea.io">
//*
//* <Overview>
//* Checks if a file exists.
//* </Overview>
//*
//* <Parameter name="pathName">
//* String representing the path name of a file.
//* </Parameter>
//*
//* <Returns>
//* True if the file named <Arg name="pathName"/> exists.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionFileExists(SObjFunction func,
					     SContext     context,
					     Object[]      args)
	throws STeaException {

	int numArgs = args.length;

	if ( numArgs != 2 ) {
	    throw new SNumArgException(args[0], "file-name");
	}

	String fileName = STypes.getString(args,1);
	File   file     = new File(fileName);

	return file.exists() ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaFunction name="file-is-dir?"
//* 		arguments="pathName"
//*             module="tea.io">
//*
//* <Overview>
//* Checks if a path name refers to a directory.
//* </Overview>
//*
//* <Parameter name="pathName">
//* String representing a path name.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="pathName"/> refers to a directory.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/
    
    private static Object functionIsDir(SObjFunction func,
					SContext     context,
					Object[]     args)
	throws STeaException {

	int numArgs = args.length;

	if ( numArgs != 2 ) {
	    throw new SNumArgException(args[0], "file-name");
	}

	String fileName = STypes.getString(args,1);
	File   file     = new File(fileName);

	return file.isDirectory() ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaFunction name="file-is-regular?"
//* 		arguments="pathName"
//*             module="tea.io">
//*
//* <Overview>
//* Checks if a path name refers to a regular file.
//* </Overview>
//*
//* <Parameter name="pathName">
//* String representing a path name.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="pathName"/> refers to a regular file.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionIsRegular(SObjFunction func,
					    SContext     context,
					    Object[]     args)
	throws STeaException {

	int numArgs = args.length;
	
	if ( numArgs != 2 ) {
	    throw new SNumArgException(args[0], "file-name");
	}

	String fileName = STypes.getString(args,1);
	File   file     = new File(fileName);

	return file.isFile() ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaFunction name="file-join"
//* 		arguments="component1 [component2 ...]"
//*             module="tea.io">
//*
//* <Overview>
//* Builds a path name by joining the name components.
//* </Overview>
//*
//* <Parameter name="component1">
//* String representing part of a path name.
//* </Parameter>
//*
//* <Returns>
//* A new string reperesenting a path name obtained by joining
//* the different name components received as arguments.
//* </Returns>
//*
//* <Description>
//* The returned string is obtained by concatenating the
//* name components received as arguments separeted by a platform dependent
//* separator.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionJoin(SObjFunction func,
				       SContext     context,
				       Object[]     args)
	throws STeaException {

	int          numArgs       = args.length;
	StringBuffer result        = new StringBuffer();
	int          numArgsMinus1 = args.length - 1;

	try {
	    if ( numArgs > 1 ) {
		append(result, args[1]);
	    }
	    for ( int i=2; i<numArgs; i++ ) {
		if ( result.length() > 0 ) {
		    result.append(File.separatorChar);
		}
		append(result, args[i]);
	    }
	} catch (SRuntimeException e) {
	    throw new SRuntimeException(args[0], e.getMessage());
	}

	return result.toString();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static void append(StringBuffer result,
			       Object       component)
	throws SRuntimeException {

	if ( component instanceof SObjPair ) {
	    result.append(join((SObjPair)component));
	} else {
	    try {
		result.append((String)component);
	    } catch (ClassCastException e) {
		throw new STypeException("components must be strings" +
					 " not " +STypes.getTypeName(component));
	    }
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static String join(SObjPair componentList)
	throws SRuntimeException {

	Iterator     elems  = componentList.iterator();
	StringBuffer result = new StringBuffer();

	if ( elems.hasNext() ) {
	    try {
		append(result, elems.next());
	    } catch (NoSuchElementException e) {
		throw new SMalformedListException(e);
	    }
	}
	while ( elems.hasNext() ) {
	    if ( result.length() > 0 ) {
		result.append(File.separatorChar);
	    }
	    try {
		append(result, elems.next());
	    } catch (NoSuchElementException e) {
		throw new SMalformedListException(e);
	    }
	}

	return result.toString();
    }





//* 
//* <TeaFunction name="file-mkdir"
//* 		arguments="dirPath"
//*             module="tea.io">
//*
//* <Overview>
//* Creates a directory.
//* </Overview>
//*
//* <Parameter name="dirPath">
//* String representing the path name of the directory to create.
//* </Parameter>
//*
//* <Returns>
//* True if the directory was successfully created. False otherwise.
//* </Returns>
//*
//* <Description>
//* The parent directory of <Arg name="dirPath"/> must exist.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionMkdir(SObjFunction func,
					SContext     context,
					Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args[0], "dirPath");
	}
	File dir = new File(STypes.getString(args,1));

	return dir.mkdir() ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaFunction name="file-rename"
//* 		arguments="sourcePath destPath"
//*             module="tea.io">
//*
//* <Overview>
//* Changes the name of a file or directory.
//* </Overview>
//*
//* <Parameter name="sourcePath">
//* String representing original name of the file.
//* </Parameter>
//*
//* <Parameter name="destPath">
//* String representing the new name of the file.
//* </Parameter>
//*
//* <Returns>
//* True if the file was successfully renamed. False otherwise.
//* </Returns>
//*
//* <Description>
//* The <Arg name="destPath"/> may refer to a diferent directory than
//* <Arg name="sourcePath"/> as long as it is in the same file system.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionRename(SObjFunction func,
					 SContext     context,
					 Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException(args[0], "old-name new-name");
	}
	File oldFile = new File(STypes.getString(args,1));
	File newFile = new File(STypes.getString(args,2));

	return oldFile.renameTo(newFile) ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaFunction name="file-size"
//* 		arguments="fileName"
//*             module="tea.io">
//*
//* <Overview>
//* Fetches the size of a file in bytes.
//* </Overview>
//*
//* <Parameter name="fileName">
//* String representing the name of a file.
//* </Parameter>
//*
//* <Returns>
//* An integer object representing the size in bytes of the file. The
//* value 0 if the file does not exist or is inaccessible.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionSize(SObjFunction func,
				       SContext     context,
				       Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args[0], "fileName");
	}
	long size = (new  File(STypes.getString(args,1))).length();

	return new Integer((int)size);
    }





//* 
//* <TeaFunction name="file-split-path-list"
//* 		 arguments="pathList"
//*              module="tea.io">
//*
//* <Overview>
//* Creates a list of strings representing path names from a
//* a string containing a sequence of path names separated by
//* the plataform dependent path separator character.
//* </Overview>
//*
//* <Parameter name="pathList">
//* String object containing a sequence of path names separated by
//* the plataform dependent path separator character.
//* </Parameter>
//*
//* <Returns>
//* A list of strings obtained by spliting the <Arg name="pathList"/>
//* string by the path separator character.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionSplitPathList(SObjFunction func,
						SContext     context,
						Object[]     args)
	throws STeaException {

	int numArgs = args.length;

	if ( numArgs != 2 ) {
	    throw new SNumArgException(args[0], "string-path-list");
	}

	String   pathList = STypes.getString(args,1);
	SObjPair result   = SUtils.buildPathList(pathList);

	return result;
    }





//* 
//* <TeaFunction name="file-unlink"
//* 		arguments="fileName"
//*             module="tea.io">
//*
//* <Overview>
//* Deletes a file.
//* </Overview>
//*
//* <Parameter name="fileName">
//* String representing the name of a file.
//* </Parameter>
//*
//* <Returns>
//* True if the file was successfully removed. False otherwise.
//* </Returns>
//*
//* <Description>
//* Removes the file whose name was given as argument. The name can either
//* refer to a regular file or to a directory. In the case of a directory
//* it must be empty for the function to complete successfully. The 
//* <Func name="file-unlink"/> function
//* returns a boolean value. It returns true if the file was successfully
//* deleted. It returns false if the file could not be deleted. This can
//* happen if there is no file with that name, or if the process does not
//* have the necessary permissions to remove the file.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionUnlink(SObjFunction func,
					 SContext     context,
					 Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args[0], "file-name");
	}

	return (new File(STypes.getString(args,1))).delete() ?
	    Boolean.TRUE : Boolean.FALSE;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

