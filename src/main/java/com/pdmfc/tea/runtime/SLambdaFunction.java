/**************************************************************************
 *
 * Copyright (c) 2001-2010 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjBlock;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SBreakException;
import com.pdmfc.tea.runtime.SContinueException;
import com.pdmfc.tea.runtime.SReturnException;
import com.pdmfc.tea.runtime.STypeException;





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

public class SLambdaFunction
    implements SObjFunction {




    // Names of formal arguments.
    private SObjSymbol[] _argNames = null;

    // Formal argument count plus 1.
    private int _argCountPlusOne = -1;

    // The Tea function body.
    private SObjBlock _body = null;

    //
    private SContext _bodyContext = null;





/**************************************************************************
 *
 * Initializes the new object.
 *
 * @param argNames List of symbols representing the formal parameters.
 *
 * @param body Block of code, representing the body of the command.
 *
 **************************************************************************/

   public SLambdaFunction(SObjSymbol[] argNames,
			  SObjBlock    body) {

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
 * This method is supposed to be called with <TT>args</TT> having at
 * least one element.
 *
 * @param context The context where this command is being invoked.
 *
 * @param args Array with the arguments passed to the command.
 *
 * @exception STeaException Thrown if the number of arguments does not
 * match or if there were any problems executing the function body.
 *
 **************************************************************************/

    public Object exec(SObjFunction func,
		       SContext     context,
		       Object[]     args)
	throws STeaException {

	if ( args.length != _argCountPlusOne ) {
	    throw new SNumArgException(args, parametersText());
	}

	SContext funcContext = new SContext(_bodyContext);
	Object   result      = SObjNull.NULL;

	// Initializes the actual arguments.
	int i = _argCountPlusOne;
	int j = i - 1 ;
	while ( j > 0 ) {
	    --i;
	    --j;
	    funcContext.newVar(_argNames[j], args[i]);
	}

	try {
	    result = _body.exec(funcContext);
	} catch (SReturnException e1) {
	    result = e1._value;
	} catch (SBreakException e2) {
	    result = e2._object;
	} catch (SContinueException e3) {
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

	StringBuffer text = new StringBuffer(_argNames[0].getName());

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

