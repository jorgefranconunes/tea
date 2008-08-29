/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SZipFile.java,v 1.2 2002/08/02 17:47:24 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/09/14
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.io.SInput;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypes;





//* 
//* <TeaClass name="TZipFile"
//*           module="tea.util">
//*
//* <Overview>
//* Manipulates entries of a zip file.
//* </Overview>
//*
//* <Description>
//* Instances of <Class name="TZipFile"/> give access to the entries
//* of a given ZIP file. It is possible to access the names of all
//* the entries in the ZIP file and it is possible to access an
//* entry as a <ClassRef name="TInput"/>.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public class SZipFile
    extends STosObj {





    ZipFile _zipFile = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SZipFile(STosClass myClass)
	throws STeaException {

	super(myClass);
    }





//* 
//* <TeaMethod name="constructor"
//*            arguments="[pathName]"
//* 	       className="TZipFile">
//* 
//* <Overview>
//* Initializes the object internal state and optionaly opens a ZIP file.
//* </Overview>
//*
//* <Parameter name="pathName">
//* String object representing the path name of a ZIP file.
//* </Parameter>
//* 
//* <Description>
//* If no <Arg name="pathName"/> argument is given this
//* <Class name="TZipFile"/> is closed and the <MethodRef name="open"/>
//* method will eventually have to be called to open a particular
//* ZIP file.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object constructor(SObjFunction obj,
			      SContext     context,
			      Object[]     args)
	throws STeaException {

	switch ( args.length ) {
	case 2 :
	    break;
	case 3 :
	    open(obj, context, args);
	    break;
	default :
	    throw new SNumArgException("[pathName]");
	}

	return obj;
    }





//* 
//* <TeaMethod name="open"
//*            arguments="pathName"
//* 	       className="TZipFile">
//* 
//* <Overview>
//* Opens an existing ZIP file.
//* </Overview>
//*
//* <Parameter name="pathName">
//* String object representing the path name of a ZIP file.
//* </Parameter>
//* 
//* <Returns>
//* A reference to the object for which the method was called.
//* </Returns>
//* 
//* <Description>
//* If a ZIP file was previously opened then it is automatically closed
//* prior to opening the new ZIP file.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object open(SObjFunction obj,
		       SContext     context,
		       Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException("pathName");
	}

	String fileName = STypes.getString(args, 2);
	
	if ( _zipFile != null ) {
	    try {
		_zipFile.close();
	    } catch (IOException e) {
		// Never mind about it.
	    }
	}

	try {
	    _zipFile = new ZipFile(fileName);
	} catch (IOException e) {
	    throw new SRuntimeException("failed to open zip file " + fileName
					+ " : " + e.getMessage());
	}

	return obj;
    }





//* 
//* <TeaMethod name="close"
//* 	       className="TZipFile">
//* 
//* <Overview>
//* Closes the ZIP file currently opened.
//* </Overview>
//* 
//* <Returns>
//* A reference to the object for which the method was called.
//* </Returns>
//* 
//* <Description>
//* If there is no currently opened ZIP file then this method has no
//* effect.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object close(SObjFunction obj,
			SContext     context,
			Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SRuntimeException("no arguments are needed");
	}
	
	if ( _zipFile != null ) {
	    try {
		_zipFile.close();
	    } catch (IOException e) {
		// Never mind about it.
	    }
	}

	return obj;
    }





//* 
//* <TeaMethod name="getEntryNames"
//* 	       className="TZipFile">
//* 
//* <Overview>
//* Fetches the names of all entries in the currently opened ZIP file.
//* </Overview>
//* 
//* <Returns>
//* Returns a list of strings where each element is the name of
//* one of the entries in the ZIP file.
//* </Returns>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object getEntryNames(SObjFunction obj,
				SContext     context,
				Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SRuntimeException("no arguments are needed");
	}
	if ( _zipFile == null ) {
	    throw new SRuntimeException("zip file is not opened");
	}

	SObjPair    empty  = SObjPair.emptyList();
	SObjPair    head   = empty;
	SObjPair    node   = null;
	Enumeration elems  = _zipFile.entries();

	while ( elems.hasMoreElements() ) {
	    ZipEntry entry   = (ZipEntry)elems.nextElement();
	    String   value   = entry.getName();
	    SObjPair newNode = new SObjPair(value, empty);

	    if ( node == null ) {
		head = newNode;
	    } else {
		node._cdr = newNode;
	    }
	    node = newNode;
	}

	return head;
    }





//* 
//* <TeaMethod name="getInput"
//* 	       className="TZipFile">
//* 
//* <Overview>
//* Fetches the contents of one ZIP file entry as a <ClassRef name="TInput"/>.
//* </Overview>
//* 
//* <Returns>
//* An opened <ClassRef name="TInput"/> object.
//* </Returns>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object getInput(SObjFunction obj,
			   SContext     context,
			   Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SRuntimeException("entryName");
	}
	if ( _zipFile == null ) {
	    throw new SRuntimeException("zip file is not opened");
	}

	String   entryName = STypes.getString(args, 2);
	ZipEntry entry     = _zipFile.getEntry(entryName);
	SInput   input     = SInput.newInstance(context);

	if ( entry == null ) {
	    throw new SRuntimeException("entry '" + entryName
					+ "' does not exist");
	}

	try {
	    input.open(_zipFile.getInputStream(entry));
	} catch (IOException e) {
	    throw new SRuntimeException("failed to access entry '"
					+ entryName + "'");
	}

	return input;
    }





//* 
//* <TeaMethod name="isDirectory"
//*            arguments="entryName"
//* 	       className="TZipFile">
//* 
//* <Overview>
//* Checks if a given entry is a directory.
//* </Overview>
//*
//* <Parameter name="entryName">
//* String object representing the name of an entry in the currently
//* opened ZIP file.
//* </Parameter>
//* 
//* <Returns>
//* True if <Arg name="entryName"/> corresponds to an entry that is
//* a directory. False otherwise.
//* </Returns>
//* 
//* <Description>
//* If there is no entry named <Arg name="entryName"/> in the ZIP
//* file then a runtime error will occur.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object isDirectory(SObjFunction obj,
			      SContext     context,
			      Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SRuntimeException("entryName");
	}
	if ( _zipFile == null ) {
	    throw new SRuntimeException("zip file is not opened");
	}

	String   entryName = STypes.getString(args, 2);
	ZipEntry entry     = _zipFile.getEntry(entryName);
	Boolean  result    = Boolean.FALSE;

	if ( entry == null ) {
	    throw new SRuntimeException("entry '" + entryName
					+ "' does not exist");
	}

	result = entry.isDirectory() ? Boolean.TRUE : Boolean.FALSE;

	return result;
    }

    
}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/
