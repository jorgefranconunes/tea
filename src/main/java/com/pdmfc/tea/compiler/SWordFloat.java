/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
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
 * Represents a word in a SStatement that is a real number literal.
 *
 **************************************************************************/

class SWordFloat
    extends Object
    implements SWord {





    private Double _value = null;





/**************************************************************************
 *
 * Initializes the object internal state. This word will evaluate into
 * an <TT>SObjFloat</TT> object storing the value <TT>value</TT>.
 *
 * @param value
 *    The double value of the <TT>SObjFloat</TT> this word will evaluate
 *    to.
 *
 **************************************************************************/

    SWordFloat(double value) {

	_value = new Double(value);
    }





/**************************************************************************
 *
 * Evaluates this word.
 *
 * @param context
 *    The <TT>SContext</TT> where this word is being evaluated.
 *
 * @return
 *    The <TT>Object</TT> object this word evaluated to. It is an
 *    <TT>SObjFloat<\TT> object.
 *
 **************************************************************************/

    public Object get(SContext context) {

	return _value;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SObjFunction toFunction(SContext context)
	throws STeaException {

	throw new SRuntimeException("a float can not be used as a function");
    }





/**************************************************************************
 *
 * This is used for debugging. At the moment it is rather slow.
 *
 * @param out
 *    A stream where the message will be sent to.
 *
 * @param ident
 *    Number of padding white spaces inserted into the left.
 *
 **************************************************************************/

    public void prettyPrint(PrintStream out,
			    int         indent) {
	
	out.print(_value + "d");
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

