/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SWordString.java,v 1.2 2002/04/01 17:17:02 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import java.io.PrintStream;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

class SWordString
    extends Object
    implements SWord {





   private String _string = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    SWordString(String s) {

	_string = s;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object get(SContext context) {

	return _string;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SObjFunction toFunction(SContext context)
	throws STeaException {

	throw new SRuntimeException("a string can not be used as a function");
    }





/**************************************************************************
 *
 * This is used for debugging. At the moment it is rather slow.
 *
 **************************************************************************/

    public void prettyPrint(PrintStream out, int indent) {

	out.print("\"");

	String s = _string;
	for ( int i=0; i<s.length(); i++ ) {
	    char c = s.charAt(i);
	    switch ( c ) {
	    case '"'  :
		out.print("\\\"");
		break;
	    case '\t' :
		out.print("\\t");
		break;
	    case '\n' :
		out.print("\\n");
		break;
	    default   :
		if ( c < ' ' ) {
		    out.print("\\" + ((char)(c+'a')));
		} else {
		    out.print(c);
		}
	    }
	}

	out.print("\"");
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

