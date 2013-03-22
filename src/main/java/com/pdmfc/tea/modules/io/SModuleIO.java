/**************************************************************************
 *
 * Copyright (c) 2001-2013 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.modules.io.SInput;
import com.pdmfc.tea.modules.tos.SJavaClass;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SArgs;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.TeaFunction;





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

public final class SModuleIO
    extends Object
    implements SModule {





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

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void init(final SContext context)
        throws STeaException {

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

        // The functions provided by this module are implemented as
        // methods of this with class with the TeaFunction annotation.

    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void end() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void start() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void stop() {

        if ( _stdout != null ) {
            try {
                _stdout.flush();
            } catch (IOException e1) {
                // We do not care.
            } catch (STeaException e2) {
                // We do not care.
            }
        }
        if ( _stderr != null ) {
            try {
                _stderr.flush();
            } catch (IOException e3) {
                // We do not care.
            } catch (STeaException e4) {
                // We do not care.
            }
        }
    }





//* 
//* <TeaFunction name="file-basename"
//*              arguments="pathName"
//*              module="tea.io">
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
 * Implements the Tea <code>file-basename</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("file-basename")
    public static Object functionBasename(final SObjFunction func,
                                          final SContext     context,
                                          final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "path");

        String path     = SArgs.getString(args,1);
        File  file      = new File(path);
        String basename = file.getName();

        return basename;
    }





//* 
//* <TeaFunction name="file-dirname"
//*              arguments="pathName"
//*              module="tea.io">
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
 * Implements the Tea <code>file-dirname</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("file-dirname")
    public static Object functionDirname(final SObjFunction func,
                                         final SContext     context,
                                         final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "path");

        String path    = SArgs.getString(args,1);
        File   file    = new File(path);
        String dirName = file.getParent();

        if ( (dirName==null) && !file.isAbsolute() ) {
             dirName = ".";
        }

        Object result = (dirName==null) ? SObjNull.NULL : dirName;

        return result;
    }





//* 
//* <TeaFunction name="file-extension"
//*                 arguments="pathName"
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
//* name has no dot or if it ends with a dot than an empty string returned.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>file-extension</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("file-extension")
    public static Object functionExtension(final SObjFunction func,
                                           final SContext     context,
                                           final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "path");

        String path       = SArgs.getString(args, 1);
        File   file       = new File(path);
        String baseName   = file.getName();
        int    indexOfDot = baseName.lastIndexOf('.');
        String extension  =
            (indexOfDot<0) ? "" : baseName.substring(indexOfDot+1);

        return extension;
    }





//* 
//* <TeaFunction name="file-copy"
//*              arguments="sourcePath destPath"
//*               module="tea.io">
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
 * Implements the Tea <code>file-copy</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("file-copy")
    public static Object functionCopy(final SObjFunction func,
                                      final SContext     context,
                                      final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 3, "src-path dst-path");

        String  srcPath = SArgs.getString(args, 1);
        File    srcFile = new File(srcPath);
        String  dstPath = SArgs.getString(args,2);
        File    dstFile = new File(dstPath);
        boolean status  = true;

        try {
            String srcName = srcFile.getCanonicalPath();
            String dstName = dstFile.getCanonicalPath();

            if ( !srcName.equals(dstName) ) {
                copyFile(srcFile, dstFile);
            }
        } catch ( IOException e ) {
            status = false;
        }

        Boolean result = status;

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static void copyFile(final File src,
                                 final File dst)
        throws IOException {

        byte[]           buffer = new byte[BUFFER_SIZE];
        FileInputStream  in     = null;
        FileOutputStream out    = null;
        int              count;

        try {
            in  = new FileInputStream(src);
            out = new FileOutputStream(dst);
            while ( (count=in.read(buffer)) != -1 ) {
                out.write(buffer, 0, count);
            }
        } catch ( IOException e ) {
            throw e;
        } finally {
            if (in != null) {
                try { in.close(); } catch (Exception e2) {/* */}
            }
            if (out != null) {
                try { out.close(); } catch (Exception e2) {/* */}
            }
        }
    }





//* 
//* <TeaFunction name="file-exists?"
//*              arguments="pathName"
//*              module="tea.io">
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
 * Implements the Tea <code>file-exists?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("file-exists?")
    public static Object functionFileExists(final SObjFunction func,
                                            final SContext     context,
                                            final Object[]      args)
        throws STeaException {

        SArgs.checkCount(args, 2, "path");

        String  path   = SArgs.getString(args, 1);
        File    file   = new File(path);
        Boolean result = file.exists();

        return result;
    }





//* 
//* <TeaFunction name="file-is-dir?"
//*              arguments="pathName"
//*              module="tea.io">
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
 * Implements the Tea <code>file-is-dir?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/
    
    @TeaFunction("file-is-dir?")
    public static Object functionIsDir(final SObjFunction func,
                                       final SContext     context,
                                       final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "path");

        String  path   = SArgs.getString(args, 1);
        File    file   = new File(path);
        Boolean result = file.isDirectory();

        return result;
    }





//* 
//* <TeaFunction name="file-is-regular?"
//*              arguments="pathName"
//*               module="tea.io">
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
 * Implements the Tea <code>file-is-regular?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("file-is-regular?")
    public static Object functionIsRegular(final SObjFunction func,
                                           final SContext     context,
                                           final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "path");

        String  path   = SArgs.getString(args, 1);
        File    file   = new File(path);
        Boolean result = file.isFile();

        return result;
    }





//* 
//* <TeaFunction name="file-join"
//*              arguments="component1 [component2 ...]"
//*              module="tea.io">
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
 * Implements the Tea <code>file-join</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("file-join")
    public static Object functionJoin(final SObjFunction func,
                                      final SContext     context,
                                      final Object[]     args)
        throws STeaException {

        StringBuilder buffer = new StringBuilder();

        for ( int i=1, count=args.length; i<count; ++i ) {
            String component = SArgs.getString(args, i);

            if ( component.length() > 0 ) {
                if ( buffer.length() > 0 ) {
                    buffer.append(File.separatorChar);
                }
                buffer.append(component);
            }
        }

        String result = buffer.toString();

        return result;
    }





//* 
//* <TeaFunction name="file-mkdir"
//*              arguments="dirPath"
//*              module="tea.io">
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
 * Implements the Tea <code>file-mkdir</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("file-mkdir")
    public static Object functionMkdir(final SObjFunction func,
                                       final SContext     context,
                                       final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "path");

        String  path   = SArgs.getString(args, 1);
        File    dir    = new File(path);
        Boolean result = dir.mkdir();

        return result;
    }





//* 
//* <TeaFunction name="file-rename"
//*              arguments="sourcePath destPath"
//*              module="tea.io">
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
 * Implements the Tea <code>file-rename</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("file-rename")
    public static Object functionRename(final SObjFunction func,
                                        final SContext     context,
                                        final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 3, "old-name new-name");

        String  oldPath = SArgs.getString(args, 1);
        File    oldFile = new File(oldPath);
        String  newPath = SArgs.getString(args, 2);
        File    newFile = new File(newPath);
        Boolean result  = oldFile.renameTo(newFile);

        return result;
    }





//* 
//* <TeaFunction name="file-size"
//*              arguments="fileName"
//*              module="tea.io">
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
 * Implements the Tea <code>file-size</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("file-size")
    public static Object functionSize(final SObjFunction func,
                                      final SContext     context,
                                      final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "path");

        String  path   = SArgs.getString(args, 1);
        File    file   = new  File(path);
        long    size   = file.length();
        Integer result = Integer.valueOf((int)size);

        return result;
    }





//* 
//* <TeaFunction name="file-split-path-list"
//*              arguments="pathList"
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
 * Implements the Tea <code>file-split-path-list</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("file-split-path-list")
    public static Object functionSplitPathList(final SObjFunction func,
                                               final SContext     context,
                                               final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "string-path-list");

        String   pathList = SArgs.getString(args,1);
        SObjPair result   = buildPathList(pathList);

        return result;
    }





/**************************************************************************
 *
 * Creates a Tea list of strings from the <code>pathList</code>
 * argument. The path components in <code>pathList</code> are supposed
 * to be separated with the plataform path separator character.
 *
 * @param pathList Represents a list of paths.
 *
 * @return The head of a Tea list.
 *
 **************************************************************************/

    private static SObjPair buildPathList(final String pathList) {

        SObjPair empty    = SObjPair.emptyList();
        SObjPair head     = empty;
        SObjPair elem     = null;
        String   pathSep  = File.pathSeparator;

        if ( pathList == null ) {
            return empty;
        }

        StringTokenizer st = new StringTokenizer(pathList, pathSep);

        while ( st.hasMoreTokens() ) {
            String   path = st.nextToken();
            SObjPair node = null;

            if ( path.length() == 0 ) {
                continue;
            }
            path = path.replace('|', ':');
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





//* 
//* <TeaFunction name="file-unlink"
//*              arguments="fileName"
//*              module="tea.io">
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
 * Implements the Tea <code>file-unlink</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("file-unlink")
    public static Object functionUnlink(final SObjFunction func,
                                        final SContext     context,
                                        final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "path");

        String  path   = SArgs.getString(args,1);
        File    file   = new File(path);
        Boolean result = file.delete();

        return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

