/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.SBreakException;
import com.pdmfc.tea.runtime.SContinueException;
import com.pdmfc.tea.runtime.TeaBlock;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaNull;
import com.pdmfc.tea.runtime.TeaPair;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.SReturnException;





/**************************************************************************
 *
 * This class implements a TOS method with a variable number of
 * arguments.
 *
 **************************************************************************/

public final class STosMethodVarArg
    extends Object
    implements TeaFunction {




    private TeaSymbol _argName;

    // The TOS method body.
    private TeaBlock _body;

    private int _level;

    private static final TeaSymbol SYMBOL_THIS =TeaSymbol.addSymbol("this");
    private static final TeaSymbol SYMBOL_SUPER=TeaSymbol.addSymbol("super");





/**************************************************************************
 *
 * Defines a method associated with the TOS class <TT>className</TT>.
 *
 * @param methodName The name to associate with this TOS method.
 *
 * @param theClass The TOS class where this TOS method is being
 * defined.
 *
 * @param argName Name of the function formal parameter that is
 * initialized with a list containin the arguments for the invocation.
 *
 * @param body Block of code, representing the body of the method.
 *
 **************************************************************************/

    public STosMethodVarArg(final STosClass  theClass,
                            final TeaSymbol methodName,
                            final TeaSymbol argName,
                            final TeaBlock  body) {

        _argName = argName;
        _body    = body;
        _level   = theClass.level();
    }





/**************************************************************************
 *
 * Executes the TOS method. A new context is created, descending from
 * the context where the block was created. Local variables, named
 * after the formal command parameters are initialized with the values
 * received as arguments.
 *
 * This method is supposed to be called with <TT>args</TT> having at least
 * one element.
 *
 * @param context The context where this command is being invoked.
 *
 * @param args Array with the arguments passed to the command.
 *
 * @exception TeaException Thrown if there is not two arguments for
 * the command.
 *
 **************************************************************************/

    public Object exec(final TeaFunction func,
                       final TeaContext     context,
                       final Object[]    args)
        throws TeaException {

        STosObj   obj          = (STosObj)func;
        TeaContext memberContext = obj.members(_level).clone(_body.getContext());
        TeaContext procContext   = memberContext.newChild();
        Object   result        = TeaNull.NULL;

        // Initializes the "this" and "super", if aplicable, local variables:
        procContext.newVar(SYMBOL_THIS, obj.selfObj());
        if ( _level > 0 ) {
            procContext.newVar(SYMBOL_SUPER, obj.part(_level-1));
        }

        // Initializes the formal parameters with the actual values.
        TeaPair emptyList = TeaPair.emptyList();
        TeaPair head      = emptyList;
        TeaPair element   = null;

        for ( int i=2; i<args.length; i++ ) {
            TeaPair node = new TeaPair(args[i], emptyList);

            if ( element == null ) {
                head = node;
            } else {
                element.setCdr(node);
            }
            element = node;
        }

        procContext.newVar(_argName, head);

        try {
            result = _body.exec(procContext);
        } catch (SReturnException e1) {
            result = e1.getReturnValue();
        } catch (SBreakException e2) {
            result = e2.getBreakValue();
        } catch (SContinueException e3) {
            // We will treat this as a return with a null.
        }

        return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

