/**************************************************************************
 *
 * Copyright (c) 2002-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import java.io.PrintStream;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SArithExpression;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjBlock;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

final class SWordExpressionBlock
    extends Object
    implements SWord {





    private SArithExpression _expr = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SWordExpressionBlock(final SArithExpression expression) {

        _expr = expression;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object get(final SContext context) {

        SObjBlock block = new SObjBlock() {
                public SContext getContext() {
                    return context;
                }
                public Object exec(final SContext cntxt)
                    throws STeaException {
                    return _expr.evaluate(cntxt);
                }
                public Object exec()
                    throws STeaException {
                    return _expr.evaluate(context);
                }
            };

        return block;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SObjFunction toFunction(final SContext context)
        throws STeaException {

        String msg = "an expression block can not be used as a function";
        throw new SRuntimeException(msg);
    }






/**************************************************************************
 *
 * This is used for debugging. At the moment it is rather slow.
 *
 **************************************************************************/

    public void prettyPrint(final PrintStream out,
                            final int         indent) {

        throw new RuntimeException("not yet implemented...");
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

