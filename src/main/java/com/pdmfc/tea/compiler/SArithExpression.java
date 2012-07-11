/**************************************************************************
 *
 * Copyright (c) 2002-2011 PDMFC, All Rights Reserved.
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
 * An arithmetic expression.
 *
 **************************************************************************/

final class SArithExpression
    extends Object {





    private SExpression  _expr     = new SExpression();
    private SObjSymbol[] _varNames = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SArithExpression() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * Initializes this object by parsing an arithmetic expression from a
 * string.
 *
 * @param expression The arithmetic expresssion that will be parsed.
 *
 * @exception SCompileException Thrown if there was a syntax error in
 * the given arithmentic expression.
 *
 **************************************************************************/

    public void initialize(final String expression)
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
                public double getVariable(final int index)
                    throws SExpressionException {
                    return myGetVariable(context, index);
                }
                public double callFunction(final int index, final Object[] args)
                    throws SExpressionException {
                    return myCallFunction(context, index, args);
                }
            };
        double result;

        try {
            result = _expr.evaluate(data);
        } catch (SExpressionException e) {
            throw new SRuntimeException(e.getMessage());
        }

        return new Double(result);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private double myGetVariable(final SContext context,
                                 final int      index)
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

    private double myCallFunction(final SContext context,
                                  final int      index,
                                  final Object[] args)
        throws SExpressionException {

        throw new SExpressionException("functions are not yet supported");
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

