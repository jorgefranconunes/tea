/**************************************************************************
 *
 * Copyright (c) 2003 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SWordLong.java,v 1.1 2003/07/10 17:24:25 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2003/07/10 Created. (jfn)
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
 * Represents a word in a Tea statement that is a long value literal.
 *
 **************************************************************************/

class SWordLong
    extends Object
    implements SWord {





    private Long _value = null;





/**************************************************************************
 *
 * Initializes the object internal state.
 *
 * @param value The integer value of the <code>java.lang.Long</code>
 * this word will evaluate to.
 *
 **************************************************************************/

    SWordLong(long value) {

	_value = new Long(value);
    }





/**************************************************************************
 *
 * Evaluates this word.
 *
 * @param context The <code>SContext</code> where this word is being
 * evaluated.
 *
 * @return The object this word evaluated to. It is an
 * <code>java.lang.Long<\code> object.
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

	throw new SRuntimeException("a long can not be used as a function");
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

