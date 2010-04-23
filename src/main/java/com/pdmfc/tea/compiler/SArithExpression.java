/**************************************************************************
 *
 * Copyright (c) 2002 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SArithExpression.java,v 1.2 2002/11/23 21:14:12 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/09/11
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import java.text.MessageFormat;

import com.pdmfc.ep.SExpression;
import com.pdmfc.ep.SExpressionException;
import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SCompileException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNoSuchVarException;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

class SArithExpression
    extends Object {





    private SExpression  _expr     = new SExpression();
    private SObjSymbol[] _varNames = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SArithExpression() {
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void initialize(String expression)
	throws SCompileException {

	try {
	    _expr.initialize(expression);
	} catch (SExpressionException e) {
	    throw new SCompileException(e.getMessage());
	}

	String[] varNames = _expr.getVariableNames();

	_varNames = new SObjSymbol[varNames.length];

	for ( int i=varNames.length; (i--)>0; ) {
	    _varNames[i] = SObjSymbol.addSymbol(varNames[i]);
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object evaluate(final SContext context)
	throws STeaException {

	SExpression.SData data = new SExpression.SData() {
		public double getVariable(int index)
		    throws SExpressionException {
		    return myGetVariable(context, index);
		}
		public double callFunction(int index, Object[] args)
		    throws SExpressionException {
		    return myCallFunction(context, index, args);
		}
	    };
	double result;

	try {
	    result = _expr.evaluate(data);
	} catch(SExpressionException e) {
	    throw new SRuntimeException(e.getMessage());
	}

	return new Double(result);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private double myGetVariable(SContext context,
				 int      index)
	throws SExpressionException {

	Object varValue = null;
	double result;

	try {
	    varValue = context.getVar(_varNames[index]);
	} catch (SNoSuchVarException e) {
	    throw new SExpressionException(e.getMessage());
	}
	try {
	    result = ((Double)varValue).doubleValue();
	} catch (ClassCastException e) {
	    String   fmt  = "Variable {0} does not contain a numeric value";
	    Object[] args = { _varNames[index] };
	    String   msg  = MessageFormat.format(fmt, args);
	    throw new SExpressionException(msg);
	}

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private double myCallFunction(SContext context,
				  int      index,
				  Object[] args)
	throws SExpressionException {

	throw new SExpressionException("functions are not yet supported");
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

