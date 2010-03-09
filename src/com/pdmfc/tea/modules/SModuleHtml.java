/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SModuleHtml.java,v 1.2 2005/02/15 17:41:09 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/08/02
 * Moved to the "com.pdmfc.tea.modules" package. (jfn)
 *
 * 2002/03/06
 * Added the "|" character to the set of characters that the
 * "urlEncode(String)" method will encode. (jfn)
 *
 * 2002/01/20
 * Calls to the "addJavaFunction()" method were replaced by inner
 * classes for performance. (jfn)
 *
 * 2002/01/10
 * This classe now derives from SModuleCore. (jfn)
 *
 * 2001/08/18
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.STeaRuntime;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;





//*
//* <TeaModule name="tea.html">
//*
//* <Overview>
//* Utilities for generating HTML.
//* </Overview>
//*
//* <Description>
//* Set of classes and functions for generating HTML content.
//* </Description>
//*
//* </TeaModule>
//*

/**************************************************************************
 *
 * Module of Tea functions related to HTML generation.
 *
 **************************************************************************/

public class SModuleHtml 
    extends SModule {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SModuleHtml() {
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void init(STeaRuntime context)
	throws STeaException {

	super.init(context);

	context.addFunction("html-encode",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionHtmlEncode(func, context, args);
				    }
				});

	context.addFunction("url-encode",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionUrlEncode(func, context, args);
				    }
				});
    }





//* 
//* <TeaFunction name="html-encode"
//* 		arguments="aString"
//*             module="tea.html">
//*
//* <Overview>
//* Encodes a string so it can be used inside an HTML document.
//* </Overview>
//*
//* <Parameter name="aString">
//* The string to be encoded.
//* </Parameter>
//* 
//* <Returns>
//* A new string resulting from encoding <Arg name ="aString"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * @exception STeaException Thrown whenever an argument could not be
 * printed because of its type.
 *
 **************************************************************************/

    private static Object functionHtmlEncode(SObjFunction func,
					     SContext     context,
					     Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args[0], "args: string");
	}

	Object arg = args[1];

	if ( arg instanceof String ) {
	   return htmlEncode((String)arg);
	}
	if ( arg instanceof Integer ) {
	   return String.valueOf(((Integer)arg).intValue());
	}
	if ( arg instanceof Double ) {
	    return String.valueOf(((Double)arg).doubleValue());
	}

	throw new STypeException(args[0],
				 "arg 1 must be a string or an integer, not a "
				 + STypes.getTypeName(arg));
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static String htmlEncode(String s) {

	StringBuffer buf  = new StringBuffer();
	int          size = s.length();

	for ( int i=0; i<size; i++ ) {
	    char c = s.charAt(i);

	    switch ( c ) {
	    case '<' : buf.append("&lt;"); break;
	    case '>' : buf.append("&gt;"); break;
	    case '&' : buf.append("&amp;"); break;
	    case '"' : buf.append("&quot;"); break;
	    default  : buf.append(c); break;
	    }
	}
	return buf.toString();
    }





/**************************************************************************
 *
 * @exception STeaException
 *   Thrown wherener an argument could not be printed because of its type.
 *
 **************************************************************************/

    private static Object functionUrlEncode(SObjFunction func,
					    SContext     context,
					    Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args[0], "args: string");
	}

	Object arg = args[1];

	if ( arg instanceof String ) {
	    return urlEncode((String)arg);
	 }
	 if ( arg instanceof Integer ) {
	   return String.valueOf(((Integer)arg).intValue());
	 }
	if ( arg instanceof Double ) {
	    return String.valueOf(((Double)arg).doubleValue());
	}

	 throw new STypeException(args[0],
				  "arg 1 must be a string or a numric, not a "
				  + STypes.getTypeName(arg));
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static String urlEncode(String s) {

	StringBuffer buf  = new StringBuffer();
	int          size = s.length();

	for ( int i=0; i<size; i++ ) {
	    char c = s.charAt(i);

	    switch ( c ) {
	    case ' ' : buf.append("%20"); break;
	    case '"' : buf.append("%22"); break;
	    case '$' : buf.append("%24"); break;
	    case '%' : buf.append("%25"); break;
	    case '*' : buf.append("%2a"); break;
	    case '+' : buf.append("%2b"); break;
	    case '/' : buf.append("%2f"); break;
	    case '&' : buf.append("%36"); break;
	    case '<' : buf.append("%3c"); break;
	    case '=' : buf.append("%3d"); break;
	    case '>' : buf.append("%3e"); break;
	    case '?' : buf.append("%3f"); break;
	    case '|' : buf.append("%7c"); break;
	    default  : buf.append(c); break;
	    }
	}
	return buf.toString();
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

