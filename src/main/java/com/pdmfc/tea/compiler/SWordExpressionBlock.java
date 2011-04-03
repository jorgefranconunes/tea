/**************************************************************************
 *
 * Copyright (c) 2002 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
 *
 *
 * Revisions:
 *
 * 2002/09/11
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import java.io.PrintStream;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SArithExpression;
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

class SWordExpressionBlock
    extends Object
    implements SWord {





    private SArithExpression _expr = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    SWordExpressionBlock(SArithExpression expression) {

	_expr = expression;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object get(final SContext context) {

	SObjBlock block = new SObjBlock() {
		public SContext getContext() {
		    return context;
		}
		public Object exec(SContext cntxt)
		    throws STeaException {
		    return _expr.evaluate(cntxt);
		}
		public Object exec()
		    throws STeaException {
		    return _expr.evaluate(context);
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

	throw new SRuntimeException("an expression block can not be used as a function");
    }






/**************************************************************************
 *
 * This is used for debugging. At the moment it is rather slow.
 *
 **************************************************************************/

    public void prettyPrint(PrintStream out,
			    int         indent) {

	throw new RuntimeException("not yet implemented...");
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

