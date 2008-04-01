/**************************************************************************
 *
 * Copyright (c) 2002 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SExpression.java,v 1.3 2002/09/11 19:05:43 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/09/08
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.ep;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.pdmfc.ep.SExpressionException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public class SExpression
    extends Object {





    private static final String MSG_EMPTY_EXPR =
	"empty expression";

    private static final String MSG_EXTRA_CHARS =
	"extraneous characters";

    private static final String MSG_END_OF_EXPR =
	"unexpected end of expression";

    private static final String MSG_EXPECT_OTHER =
	"found a \"{0}\" when expecting a \"{1}\".";

    private static final String MSG_INV_NUMBER =
	"invalid number format ({0})";

    private static final String MSG_EXPECT_VALUE =
	"found \"{0}\" when expecting a number or variable";





    private static final String OPS = "+-*/^()";





    private SNode  _root = null;
    private String _expr = null;

    /**
     * The next token to be consumed.
     */
    private String _token = null;

    /**
     * Size of the expression in characters.
     */
    private int _size = 0;

    /**
     * Index into the _expression string pointing to the character
     * being currently accessed.
     */
    private int _pointer = 0;

    List     _varNameList = new ArrayList();
    String[] _varNames    = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SExpression() {
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void initialize(String expression)
	throws SExpressionException {

	_expr    = expression.trim();
	_size    = _expr.length();
	_pointer = 0;

	if ( _size == 0 ) {
	    throw new SExpressionException(MSG_EMPTY_EXPR);
	}

	fetchToken();
	_root = getTerms();

	if ( !atEnd() ) {
	    throw new SExpressionException(MSG_EXTRA_CHARS);
	}

	_varNames    = new String[_varNameList.size()];
	for ( int i=_varNames.length; (i--)>0; ) {
	    _varNames[i] = (String)_varNameList.get(i);
	}
	_varNameList = null;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public String[] getVariableNames() {

	return _varNames;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SNode getTerms()
	throws SExpressionException {

	SNode  leftTerm  = getFactors();
	String token     = getToken();
	SNode  rightTerm = null;
	SNode  result    = null;

	if ( token.equals("+") ) {
	    fetchToken();
	    rightTerm = getTerms();
	    result    = createAddNode(leftTerm, rightTerm);
	} else if ( token.equals("-") ) {
	    fetchToken();
	    rightTerm = getTerms();
	    result    = createSubNode(leftTerm, rightTerm);
	} else {
	    result = leftTerm;
	}

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SNode createAddNode(final SNode left,
				final SNode right) {

	SNode result = new SNode() {
		public double evaluate(SData data)
		    throws SExpressionException {
		    return left.evaluate(data) + right.evaluate(data);
		}
	    };

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SNode createSubNode(final SNode left,
				final SNode right) {

	SNode result = new SNode() {
		public double evaluate(SData data)
		    throws SExpressionException {
		    return left.evaluate(data) - right.evaluate(data);
		}
	    };

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SNode getFactors()
	throws SExpressionException {

	SNode  leftFactor  = getLevel4();
	String token       = getToken();
	SNode  rightFactor = null;
	SNode  result      = null;

	if ( token.equals("*") ) {
	    fetchToken();
	    rightFactor = getFactors();
	    result      = createMulNode(leftFactor, rightFactor);
	} else if ( token.equals("/") ) {
	    fetchToken();
	    rightFactor = getFactors();
	    result      = createDivNode(leftFactor, rightFactor);
	} else {
	    result = leftFactor;
	}

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SNode createMulNode(final SNode left,
				final SNode right) {

	SNode result = new SNode() {
		public double evaluate(SData data)
		    throws SExpressionException {
		    return left.evaluate(data) * right.evaluate(data);
		}
	    };

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SNode createDivNode(final SNode left,
				final SNode right) {

	SNode result = new SNode() {
		public double evaluate(SData data)
		    throws SExpressionException {
		    return left.evaluate(data) / right.evaluate(data);
		}
	    };

	return result;
    }





/**************************************************************************
 *
 * Process an exponent.
 *
 **************************************************************************/

    private SNode getLevel4()
	throws SExpressionException {

	SNode  base     = getLevel5();
	String token    = getToken();
	SNode  exponent = null;
	SNode  result   = null;

	if ( token.equals("^") ) {
	    fetchToken();
	    exponent = getLevel4();
	    result   = createExpNode(base, exponent);
	} else {
	    result = base;
	}

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SNode createExpNode(final SNode base,
				final SNode exp) {

	SNode result = new SNode() {
		public double evaluate(SData data)
		    throws SExpressionException {
		    return Math.pow(base.evaluate(data), exp.evaluate(data));
		}
	    };

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SNode getLevel5()
	throws SExpressionException {

	String token  = getToken();
	SNode  result = null;
	SNode  arg    = null;

	if ( token.equals("+") ) {
	    fetchToken();
	    result = getLevel5();
	} else if ( token.equals("-") ) {
	    fetchToken();
	    arg    = getLevel5();
	    result = createUnMinusNode(arg);
	} else {
	    result = getLevel6();
	}

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SNode createUnMinusNode(final SNode arg) {

	SNode result = new SNode() {
		public double evaluate(SData data)
		    throws SExpressionException {
		    return -arg.evaluate(data);
		}
	    };

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SNode getLevel6()
	throws SExpressionException {

	String token  = getToken();
	SNode  result = null;
	SNode  expr   = null;

	if ( token.equals("(") ) {
	    fetchToken();
	    result = getTerms();
	    token  = getToken();
	    if ( atEnd() ) {
		throw new SExpressionException(MSG_END_OF_EXPR);
	    }
	    if ( !token.equals(")") ) {
		String   fmt  = MSG_EXPECT_OTHER;
		Object[] args = { token, ")" };
		String   msg  = MessageFormat.format(fmt, args);
		throw new SExpressionException(msg);
	    }
	    fetchToken();
	} else {
	    result = getLevel7();
	}

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SNode getLevel7()
	throws SExpressionException {

	String token  = getToken();
	if ( atEnd() ) {
	    throw new SExpressionException(MSG_END_OF_EXPR);
	}
	SNode  result = null;
	char   c      = token.charAt(0);

	if ( Character.isDigit(c) ) {
	    double value = 0;
	    try {
		value = Double.valueOf(token).doubleValue();
	    } catch (NumberFormatException e) {
		String   fmt  = MSG_INV_NUMBER;
		Object[] args = { token };
		String   msg  = MessageFormat.format(fmt, args);
		throw new SExpressionException(msg);
	    }
	    result = createNumberNode(value);
	    fetchToken();
	} else if ( Character.isLetter(c) || (c=='_') ) {
	    int varIndex = registerVar(token);
	    result = createVarNode(varIndex);
	    fetchToken();
	} else {
	    String   fmt  = MSG_EXPECT_VALUE;
	    Object[] args = { token };
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

    private SNode createNumberNode(final double value) {

	SNode result = new SNode() {
		public double evaluate(SData data)
		    throws SExpressionException {
		    return value;
		}
	    };

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SNode createVarNode(final int varIndex) {

	SNode result = new SNode() {
		public double evaluate(SData data)
		    throws SExpressionException {
		    return data.getVariable(varIndex);
		}
	    };

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private int registerVar(String varName) {

	int index = _varNameList.indexOf(varName);

	if ( index == -1 ) {
	    index = _varNameList.size();
	    _varNameList.add(varName);
	}

	return index;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private String getToken() {

	return _token;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void fetchToken() {

	StringBuffer buffer = new StringBuffer();
	char         c      = '\u0000';

	// Skip initial whitespace.
	while (    (_pointer<_size)
		&& Character.isWhitespace(c=_expr.charAt(_pointer)) ) {
	    ++_pointer;
	}

	if ( OPS.indexOf(c) != -1 ) {
	    buffer.append(c);
	    _pointer++;
	} else {
	    while ( _pointer < _size ) {
		c = _expr.charAt(_pointer);
		if ( Character.isWhitespace(c) ) {
		    break;
		}
		if ( OPS.indexOf(c) != -1 ) {
		    break;
		}
		buffer.append(c);
		_pointer++;
	    }
	}

	_token = buffer.toString();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private boolean atEnd() {

	return _token.length() == 0;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public double evaluate(SData data)
	throws SExpressionException {

	return _root.evaluate(data);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static void main(String[] args)
	throws SExpressionException  {

	if ( args.length == 0 ) {
	    System.out.println("Args: expression ...");
	    System.exit(1);
	}

	SData data = new SData() {
		public double getVariable(int index)
		    throws SExpressionException {
		    throw new SExpressionException("No such variable");
		}
		public double callFunction(int funcIndex, Object[] args)
		    throws SExpressionException {
		    throw new SExpressionException("No such function");
		}
	    };

	for ( int i=0; i<args.length; i++ ) {
	    String      str  = args[i];
	    SExpression expr = new SExpression();
	    expr.initialize(str);
	    double      value = expr.evaluate(data);

	    System.out.println(str + " = " + value);
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public interface SData {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

	public double getVariable(int varIndex)
	    throws SExpressionException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

	public double callFunction(int      funcIndex,
				   Object[] args)
	    throws SExpressionException;
	
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private interface SNode {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

	public double evaluate(SData data)
	    throws SExpressionException;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

