/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjBlock;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SBreakException;
import com.pdmfc.tea.runtime.SContinueException;
import com.pdmfc.tea.runtime.SReturnException;





/**************************************************************************
 *
 * A function defined inside a Tea program with a variable number of
 * arguments.  A function is defined by a list of symbols, that are
 * its formal parameters, and a block, that is the code to be executed
 * when the command is invoked.
 *
 * <p> The block is executed in a new context. This new context is an
 * imediate descendent of the context where the block was
 * created. Before the block is actually executed, variables named
 * after the formal parameters are created and initialized with the
 * values with which the command was invoked. The return value of the
 * proc is the return value of the last statement of the block.</p>
 *
 **************************************************************************/

public final class SLambdaFunctionVarArg
    extends Object
    implements SObjFunction {





   private SObjSymbol _argName = null;
   private SObjBlock  _body    = null;





/**************************************************************************
 *
 * Initializes the new object.
 *
 * @param argName The name of the local variable that will contain a
 * list with the actual parameters at the time of the command
 * execution.
 *
 * @param body Block of code, representing the body of the command.
 *
 **************************************************************************/

    public SLambdaFunctionVarArg(final SObjSymbol argName,
                                 final SObjBlock  body) {

        _argName = argName;
        _body    = body;
    }





/**************************************************************************
 *
 * Executes the command. A new context is created, descending from the
 * context where the block was created. A local variable, named after
 * the formal command parameter is initialized with the a list where
 * the elements are the values received as arguments.
 *
 * This method is supposed to be called with <TT>args</TT> having at
 * least one element.
 *
 * @param context The context where this command is being invoked.
 *
 * @param args Array with the arguments passed to the command.
 *
 * @exception STeaException Thrown if there is not two arguments for
 * the command.
 *
 **************************************************************************/

    public Object exec(final SObjFunction obj,
                       final SContext     context,
                       final Object[]     args)
        throws STeaException {

        SContext funcContext = new SContext(_body.getContext());
        Object   result      = SObjNull.NULL;
        SObjPair emptyList   = SObjPair.emptyList();
        SObjPair head        = emptyList;
        SObjPair element     = null;

        // Creates the list with the actual function arguments.  The
        // loop starts at 1 because args[0] contains the function
        // being called (or a symbol with its name).
        for ( int i=1; i<args.length; i++ ) {
            SObjPair node = new SObjPair(args[i], emptyList);

            if ( element == null ) {
                head = node;
            } else {
                element._cdr = node;
            }
            element = node;
        }

        funcContext.newVar(_argName, head);

        try {
            result = _body.exec(funcContext);
        } catch (SReturnException e1) {
            result = e1.getReturnValue();
        } catch (SBreakException e2) {
            result = e2.getBreakValue();
        } catch (SContinueException e3) {
            // We will treat this as a return with a a null.
        }

        return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

