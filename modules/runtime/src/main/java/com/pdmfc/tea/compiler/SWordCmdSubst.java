/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.compiler.TeaCode;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.compiler.SWordSubstUtils;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;






/**************************************************************************
 *
 * Objects of this class represent an argument to a command in the
 * parse tree. This argument is a block. At runtime the block will be
 * executed and the value returned by the last statement is used as
 * the actual argument.
 *
 **************************************************************************/

final class SWordCmdSubst
    extends Object
    implements SWord {





    private TeaCode _code = null;





/**************************************************************************
 *
 * The constructor receives the code block that will be evaluated at
 * runtime.
 *
 * @param code Reference to the object representing the code.
 *
 **************************************************************************/

    public SWordCmdSubst(final TeaCode code) {

        _code = code;
    }





/**************************************************************************
 *
 * Returns the result of evaluating the block in the context given as
 * argument.
 *
 * @param context The context where the block will be executed.
 *
 * @return A reference to the object returned by the last statement in
 * the block.
 *
 * @exception TeaException Throw if there were problems evaluating
 * the block.
 *
 **************************************************************************/

    public Object get(final TeaContext context)
        throws TeaException {

        return _code.exec(context);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaFunction toFunction(final TeaContext context)
        throws TeaException {

        Object       obj    = _code.exec(context);
        TeaFunction result = SWordSubstUtils.toFunction(obj, context);

        return result;
    }






/**************************************************************************
 *
 * This is used for debugging. At the moment it is rather slow.
 *
 * @param out A stream where the message will be sent to.
 *
 * @param ident Number of padding white spaces inserted into the left.
 *
 **************************************************************************/

//    public void prettyPrint(PrintStream out,
//                            int         indent) {
//
//        out.println("[");
//        _code.prettyPrint(out, indent+4);
//
//        for ( int i=0; i<indent; i++ ) {
//            out.print(' ');
//        }
//
//        out.print("]");
//    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

