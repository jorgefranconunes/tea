/**************************************************************************
 *
 * Copyright (c) 2002-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SArithExpression;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

final class SWordExpression
    extends Object
    implements SWord {





    private SArithExpression _expr = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SWordExpression(final SArithExpression expression) {

        _expr = expression;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object get(final SContext context)
        throws STeaException {

        return _expr.evaluate(context);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SObjFunction toFunction(final SContext context)
        throws STeaException {

        String msg = "an expression can not be used as a function";
        throw new SRuntimeException(msg);
    }





/**************************************************************************
 *
 * Used for debugging.
 *
 * @param out Stream where the output will be sent.
 *
 * @param indent Number of spaces to prefix each line.
 *
 **************************************************************************/

    // public void prettyPrint(final PrintStream out,
    //                         final int         indent) {

    //     throw new RuntimeException("not implemented");
    // }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

