/**************************************************************************
 *
 * Copyright (c) 2003-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

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

    public Object get(final SContext context) {

        return _value;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SObjFunction toFunction(final SContext context)
        throws STeaException {

        String msg = "a long can not be used as a function";
        throw new SRuntimeException(msg);
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

