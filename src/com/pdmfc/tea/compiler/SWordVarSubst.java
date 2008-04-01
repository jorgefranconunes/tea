/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SWordVarSubst.java,v 1.2 2002/04/01 17:17:02 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import java.io.PrintStream;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SNoSuchVarException;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;






/**************************************************************************
 *
 * Objects of this class represent an argument to a command in the
 * parse tree. This argument is a symbol referencing a variable. At
 * runtime the contents of the variable will be used as the actual
 * argument.
 *
 **************************************************************************/

class SWordVarSubst
    extends Object
    implements SWord {





    private SObjSymbol _symbol = null;





/**************************************************************************
 *
 * The constructor needs the name of a variable.
 *
 * @param name The name of the variable to be substituted at runtime.
 *
 **************************************************************************/

    SWordVarSubst(String name) {

	_symbol = SObjSymbol.addSymbol(name);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    SWordVarSubst(SObjSymbol symbol) {

	_symbol = symbol;
    }





/**************************************************************************
 *
 * Returns the contents of the variable referenced by the symbol in
 * the context given as argument.
 *
 * @param context The context where the variable will be searched for.
 *
 * @return The contents of the variable.
 *
 * @exception com.pdmfc.tea.STeaException A
 * <TT>SNoSuchVarException</TT> is thrown if there is not a variable
 * with the correct name in the <TT>context</TT>.
 *
 **************************************************************************/

    public Object get(SContext context)
	throws STeaException {

	return context.getVar(_symbol);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SObjFunction toFunction(SContext context)
	throws STeaException {

	Object obj = context.getVar(_symbol);
	Object value;

	if ( obj instanceof SObjFunction ) {
	    return (SObjFunction)obj;
	}

	try {
	    value = context.getVar((SObjSymbol)obj);
	} catch (ClassCastException e1) {
	    throw new STypeException("argument 0 should be a function or a symbol, not a " + STypes.getTypeName(obj));
	} catch (SNoSuchVarException e2) {
	    value = STypes.getVarWithEffort(context, (SObjSymbol)obj);
	}
	
	try {
	    return (SObjFunction)value;
	} catch (ClassCastException e1) {
	    throw new STypeException("variable " + ((SObjSymbol)obj).getName()+
				     " should contain a function, " +
				     "not a " + STypes.getTypeName(value));
	}
    }






/**************************************************************************
 *
 * This is used for debugging. At the moment it is rather slow.
 *
 **************************************************************************/

    public void prettyPrint(PrintStream out,
			    int         indent) {

	out.print("$" + _symbol.getName());
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

