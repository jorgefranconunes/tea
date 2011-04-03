/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import java.util.Enumeration;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SBreakException;
import com.pdmfc.tea.runtime.SContinueException;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjBlock;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SObjVar;
import com.pdmfc.tea.runtime.SReturnException;
import com.pdmfc.tea.util.SList;
import com.pdmfc.tea.util.SListNode;





/**************************************************************************
 *
 * This class implements a TOS method.
 *
 **************************************************************************/

public class STosMethod
    extends Object
    implements SObjFunction {




    // Names of formal arguments.
    private SObjSymbol[] _argNames;

    // The TOS method body.
    private SObjBlock    _body;

    private STosClass _myClass;
    private int       _level;
    private SListNode _memberNames;

    private static final SObjSymbol _thisSymbol =SObjSymbol.addSymbol("this");
    private static final SObjSymbol _superSymbol=SObjSymbol.addSymbol("super");





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

   public STosMethod(STosClass    theClass,
		     SObjSymbol   methodName,
		     SObjSymbol[] argNames,
		     SObjBlock    body) {

      _argNames    = argNames;
      _body        = body;
      _myClass     = theClass;
      _level       = theClass.level();
      _memberNames = theClass.memberNames().head();
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
 * @exception STeaException
 *   Thrown if there is not two arguments for the command.
 *
 **************************************************************************/

    public Object exec(SObjFunction func,
		       SContext     context,
		       Object[]     args)
	throws STeaException {

	if ( args.length != (_argNames.length+2) ) {
	    throw new SNumArgException(args[0], parametersText());
	}

	STosObj   obj          = (STosObj)func;
	SContext memberContext = obj.members(_level).clone(_body.getContext());
	SContext procContext   = memberContext.newChild();
	Object   result        = SObjNull.NULL;

	// Initializes the "this" and "super", if aplicable, local
	// variables:
	procContext.newVar(_thisSymbol, obj.selfObj());
	if ( _level > 0 ) {
	    procContext.newVar(_superSymbol, obj.part(_level-1));
	}

	// Initializes the formal parameters with the actual values.
	for ( int i=_argNames.length; i-->0; ) {
	    procContext.newVar(_argNames[i], args[i+2]);
	}

	try {
	    result = _body.exec(procContext);
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

