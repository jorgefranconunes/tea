/**************************************************************************
 *
 * Copyright (c) 2002 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SWordExpression.java,v 1.2 2002/09/13 09:27:38 jfn Exp $
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
import java.text.MessageFormat;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SArithExpression;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

class SWordExpression
    extends Object
    implements SWord {





    private SArithExpression _expr = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    SWordExpression(SArithExpression expression) {

	_expr = expression;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object get(SContext context)
	throws STeaException {

	return _expr.evaluate(context);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SObjFunction toFunction(SContext context)
	throws STeaException {

	throw new SRuntimeException("an expression can not be used as a function");
    }





/**************************************************************************
 *
 * Used for debugging.
 *
 * @param out Stream where the output will be sent.
 *
 * @param indent Number of spaces to prefix each line.
 *
 **************************************************************************/

    public void prettyPrint(PrintStream out,
			    int         indent) {

	throw new RuntimeException("not implemented");
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

