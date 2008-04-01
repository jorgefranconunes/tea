/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SUrlInput.java,v 1.3 2002/08/02 17:47:24 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/09/16
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.io;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.io.SInput;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypes;





//* 
//* <TeaClass name="TUrlInput"
//*           baseClass="TInput"
//*           module="tea.io">
//*
//* <Overview>
//* Input stream from a URL.
//* </Overview>
//*
//* <Description>
//* Instances of <Func name="TUrlInput"/> represent an input stream
//* associated with an URL.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Instances of this class represent file input streams.
 *
 **************************************************************************/

public class SUrlInput
    extends SInput {




      
    private static final String     CLASS_NAME   = "TUrlInput";
    private static final SObjSymbol CLASS_NAME_S = SObjSymbol.addSymbol(CLASS_NAME);





/**************************************************************************
 *
 * The constructor initializes the object internal state.
 *
 * @param myClass The <code>STosClass</code> object for this object.
 *
 **************************************************************************/

   public SUrlInput(STosClass myClass)
       throws STeaException {

       super(myClass);
   }





//* 
//* <TeaMethod name="constructor"
//*            arguments="url"
//* 	       className="TUrlInput">
//* 
//* <Overview>
//* Opens the a connection to the URL and initializes the object for
//* reading from that connection.
//* </Overview>
//* 
//* <Parameter name="url">
//* String representing a URL.
//* </Parameter>
//* 
//* <Description>
//* If the URL identified by <Arg name="url"/> could not be
//* opened for reading then a runtime error occurs.
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

	if ( args.length != 3 ) {
	    throw new SNumArgException("args: file-name");
	}

	String      url   = STypes.getString(args,2);
	InputStream input = null;

	if ( url.startsWith("/") || url.startsWith(".") ) {
	    url = "file:" + url;
	}

	try {
	    input = (new URL(url)).openStream();
	} catch (IOException e) {
	    throw new SRuntimeException("url '" + url +
					"' could not be opened for reading - "
					+ e.getMessage());
	}

	open(input);

	return obj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static String getTosClassName() {

	return CLASS_NAME;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static SUrlInput newInstance(SContext context,
					 Object[]  args)
	throws STeaException {

	STosObj input = STosUtil.newInstance(CLASS_NAME_S, context, args);

	if ( !(input instanceof SUrlInput) ) {
	    throw new SRuntimeException("invalid " + CLASS_NAME + " class");
	}

	return (SUrlInput)input;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

