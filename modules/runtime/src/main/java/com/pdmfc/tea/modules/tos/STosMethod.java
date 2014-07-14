/**************************************************************************
 *
 * Copyright (c) 2001-2012 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.SBreakException;
import com.pdmfc.tea.runtime.SContinueException;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.TeaBlock;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaNull;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.SReturnException;





/**************************************************************************
 *
 * This class implements a TOS method.
 *
 **************************************************************************/

public final class STosMethod
    extends Object
    implements TeaFunction {




    // Names of formal arguments.
    private TeaSymbol[] _argNames;

    // The TOS method body.
    private TeaBlock    _body;

    private int       _level;

    private static final TeaSymbol SYMBOL_THIS =TeaSymbol.addSymbol("this");
    private static final TeaSymbol SYMBOL_SUPER=TeaSymbol.addSymbol("super");





/**************************************************************************
 *
 * Defines a method associated with the TOS class
 * <code>className</code>.
 *
 * @param methodName The name to associate with this TOS method.
 *
 * @param theClass The TOS class where this TOS method is being
 * defined.
 *
 * @param argNames The names of the formal parameters.
 *
 * @param body Block of code, representing the body of the method.
 *
 **************************************************************************/

   public STosMethod(final STosClass    theClass,
                     final TeaSymbol   methodName,
                     final TeaSymbol[] argNames,
                     final TeaBlock    body) {

      _argNames = argNames;
      _body     = body;
      _level    = theClass.level();
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
 * @param context
 *    The context where this command is being invoked.
 *
 * @param args
 *    Array with the arguments passed to the command.
 *
 * @exception TeaException
 *   Thrown if there is not two arguments for the command.
 *
 **************************************************************************/

    public Object exec(final TeaFunction func,
                       final TeaContext     context,
                       final Object[]    args)
        throws TeaException {

        if ( args.length != (_argNames.length+2) ) {
            throw new SNumArgException(args, parametersText());
        }

        STosObj   obj          = (STosObj)func;
        TeaContext memberContext = obj.members(_level).clone(_body.getContext());
        TeaContext procContext   = memberContext.newChild();
        Object   result        = TeaNull.NULL;

        // Initializes the "this" and "super", if aplicable, local
        // variables:
        procContext.newVar(SYMBOL_THIS, obj.selfObj());
        if ( _level > 0 ) {
            procContext.newVar(SYMBOL_SUPER, obj.part(_level-1));
        }

        // Initializes the formal parameters with the actual values.
        for ( int i=_argNames.length; i-->0; ) {
            procContext.newVar(_argNames[i], args[i+2]);
        }

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

