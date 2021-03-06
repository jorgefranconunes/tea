/**************************************************************************
 *
 * Copyright (c) 2003-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaRunException;





/**************************************************************************
 *
 * Represents a word in a Tea statement that is a long value literal.
 *
 **************************************************************************/

final class SWordLong
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

    public SWordLong(final long value) {

        _value = Long.valueOf(value);
    }





/**************************************************************************
 *
 * Evaluates this word.
 *
 * @param context The context where this word is being evaluated.
 *
 * @return The object this word evaluated to. It is an
 * <code>java.lang.Long</code> object.
 *
 **************************************************************************/

    public Object get(final TeaContext context) {

        return _value;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaFunction toFunction(final TeaContext context)
        throws TeaException {

        String msg = "a long can not be used as a function";
        throw new TeaRunException(msg);
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

