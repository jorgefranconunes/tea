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

        throw new TeaRunException("a float can not be used as a function");
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

