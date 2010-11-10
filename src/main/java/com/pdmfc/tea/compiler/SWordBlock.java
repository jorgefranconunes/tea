/**************************************************************************
 *
 * Copyright (c) 2001-2010 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SCode;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjBlock;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

class SWordBlock
    extends Object
    implements SWord {





    private SCode _code;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    SWordBlock(SCode code) {

	_code = code;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object get(final SContext context) {

	final SCode code = _code;

	SObjBlock block = new SObjBlock() {
		public SContext getContext() {
		    return context;
		}
		public Object exec(SContext cntxt)
		    throws STeaException {
		    return code.exec(cntxt);
		}
		public Object exec()
		    throws STeaException {
		    return code.exec(context.newChild());
		}
	    };

	return block;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SObjFunction toFunction(SContext context)
	throws STeaException {

	throw new SRuntimeException("a block can not be used as a function");
    }






/**************************************************************************
 *
 * This is used for debugging. At the moment it is rather slow.
 *
 **************************************************************************/

//    public void prettyPrint(PrintStream out,
//			    int         indent) {
//
//	out.println("{");
//	_code.prettyPrint(out, indent+4);
//
//	for ( int i=0; i<indent; i++ ) {
//	    out.print(' ');
//	}
//
//	out.print("}");
//    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

