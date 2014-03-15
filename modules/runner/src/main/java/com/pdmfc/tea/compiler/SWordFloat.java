/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
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
 * Represents a word in a SStatement that is a real number literal.
 *
 **************************************************************************/

final class SWordFloat
    extends Object
    implements SWord {





    private Double _value = null;





/**************************************************************************
 *
 * Initializes the object internal state. This word will evaluate into
 * an <code>java.lang.Double</code> object storing the given
 * <code>value</code>.
 *
 * @param value The double value of the <code>java.lang.Double</code>
 * this word will evaluate to.
 *
 **************************************************************************/

    public SWordFloat(final double value) {

        _value = new Double(value);
    }





/**************************************************************************
 *
 * Evaluates this word.
 *
 * @param context The context where this word is being evaluated.
 *
 * @return The object this word evaluated to. It is a
 * <code>java.lang.Double</code> object.
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

    // public void prettyPrint(final PrintStream out,
    //                         final int         indent) {
        
    //     out.print(_value + "d");
    // }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

