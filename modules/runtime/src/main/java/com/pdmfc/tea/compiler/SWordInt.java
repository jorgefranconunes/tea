/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
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
 * Represents a word in a TStatement that is an integer literal.
 *
 **************************************************************************/

final class SWordInt
    extends Object
    implements SWord {





    private Integer _value = null;





/**************************************************************************
 *
 * Initializes the object internal state. This word will evaluate into
 * an <code>java.kang.Integer</code> object storing the given
 * <code>value</code>.
 *
 * @param value The integer value of the
 * <code>java.lang.Integer</code> this word will evaluate to.
 *
 **************************************************************************/

    public SWordInt(final int value) {

        _value = Integer.valueOf(value);
    }





/**************************************************************************
 *
 * Evaluates this word.
 *
 * @param context The context where this word is being evaluated.
 *
 * @return The object this word evaluated to. It is an
 * <code>java.lang.Integer</code>.
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

        String msg = "an integer can not be used as a function";
        throw new TeaRunException(msg);
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

    // public void prettyPrint(final PrintStream out,
    //                         final int         indent) {

    //     out.print(_value);
    // }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

