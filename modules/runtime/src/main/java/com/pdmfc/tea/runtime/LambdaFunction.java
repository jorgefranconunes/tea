/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaBlock;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaNull;
import com.pdmfc.tea.TeaSymbol;
import com.pdmfc.tea.TeaNumArgException;
import com.pdmfc.tea.TeaBreakException;
import com.pdmfc.tea.TeaContinueException;
import com.pdmfc.tea.TeaReturnException;





/**************************************************************************
 *
 * A function defined inside a Tea program.  A function is defined by
 * a list of symbols and a code block. The list of symbols represent
 * its formal parameters. The code block is the function body that is
 * executed when the function is invoked.
 *
 * <p>The block is executed in a new context. This new context is an
 * imediate descendent of the context where the block was
 * created. Before the block is actually executed, variables named
 * after the formal parameters are created and initialized with the
 * values with which the command was invoked. The return value of the
 * function is the return value of the last statement of the
 * block.</p>
 *
 **************************************************************************/

public final class LambdaFunction
    extends Object
    implements TeaFunction {




    // Names of formal arguments.
    private TeaSymbol[] _argNames = null;

    // Formal argument count plus 1.
    private int _argCountPlusOne = -1;

    // The Tea function body.
    private TeaBlock _body = null;

    //
    private TeaContext _bodyContext = null;





/**************************************************************************
 *
 * Initializes the new object.
 *
 * @param argNames List of symbols representing the formal parameters.
 *
 * @param body Block of code, representing the body of the command.
 *
 **************************************************************************/

   public LambdaFunction(final TeaSymbol[] argNames,
                         final TeaBlock    body) {

      _argNames        = argNames;
      _argCountPlusOne = argNames.length + 1;
      _body            = body;
      _bodyContext     = body.getContext();
   }





/**************************************************************************
 *
 * Executes the Tea function. A new context is created, descending
 * from the context where the block was created. Local variables,
 * named after the formal command parameters are initialized with the
 * values received as arguments.
 *
 * <p>This method is supposed to be called with <code>args</code>
 * having at least one element.</p>
 *
 * @param context The context where this command is being invoked.
 *
 * @param args Array with the arguments passed to the command.
 *
 * @exception TeaException Thrown if the number of arguments does not
 * match or if there were any problems executing the function body.
 *
 **************************************************************************/

    public Object exec(final TeaFunction func,
                       final TeaContext     context,
                       final Object[]    args)
        throws TeaException {

        if ( args.length != _argCountPlusOne ) {
            throw new TeaNumArgException(args, parametersText());
        }

        TeaContext funcContext = _bodyContext.newChild();
        Object   result      = TeaNull.NULL;

        // Initializes the actual arguments.
        int i = _argCountPlusOne;
        int j = i - 1;
        while ( j > 0 ) {
            --i;
            --j;
            funcContext.newVar(_argNames[j], args[i]);
        }

        try {
            result = _body.exec(funcContext);
        } catch (TeaReturnException e1) {
            result = e1.getReturnValue();
        } catch (TeaBreakException e2) {
            result = e2.getBreakValue();
        } catch (TeaContinueException e3) {
            // We will treat this as a return with a null.
        }

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private String parametersText() {

        if ( _argNames.length == 0 ) {
            return "this function takes no arguments";
        }

        StringBuilder text = new StringBuilder(_argNames[0].getName());

        for ( int i=1; i<_argNames.length; i++ ) {
            text.append(' ').append(_argNames[i].getName());
        }

        return text.toString();
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

