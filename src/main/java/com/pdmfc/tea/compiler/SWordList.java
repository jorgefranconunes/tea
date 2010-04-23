/**************************************************************************
 *
 * Copyright (c) 2001-2010 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $id$
 *
 *
 * Revisions:
 *
 * 2010/01/28 Minor refactorings to properly use generics. (jfn)
 *
 * 2002/08/03 The constructor now receives a java.util.List as
 * argument instead of a SList. (jfn)
 *
 * 2001/05/12 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

class SWordList
    extends Object
    implements SWord {





    private List<SWord> _list = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    SWordList(List<SWord> list) {

	_list = list;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object get(SContext context)
	throws STeaException {

	SObjPair empty = new SObjPair(null, null);
	SObjPair head  = empty;
	SObjPair elem  = null;

        for ( SWord word : _list ) {
	    Object   car  = word.get(context);
	    SObjPair node = new SObjPair(car, empty);
	    
	    if ( elem == null ) {
		head = node;
	    } else {
		elem._cdr = node;
	    }
	    elem = node;
	}

	return head;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SObjFunction toFunction(SContext context)
	throws STeaException {

	throw new SRuntimeException("a list can not be used as a function");
    }






/**************************************************************************
 *
 * This is used for debugging. At the moment it is rather slow.
 *
 **************************************************************************/

//    public void prettyPrint(PrintStream out,
//			    int         indent) {
//
//	out.print("( ");
//
//	for ( Iterator i=_list.iterator(); i.hasNext(); ) {
//	    ((SWord)i.next()).prettyPrint(out, indent+4);
//	    out. print(' ');
//	}
//
//	out.print(")");
//    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

