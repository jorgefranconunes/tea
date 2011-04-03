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

package com.pdmfc.tea.compiler;

import java.io.PrintStream;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.SNoSuchVarException;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

class SWordSymbol
    extends Object
    implements SWord {





    private SObjSymbol _symbol = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    SWordSymbol(String name) {

	_symbol = SObjSymbol.addSymbol(name);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object get(SContext context) {

	return _symbol;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SObjSymbol getSymbol() {

	return _symbol;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SObjFunction toFunction(SContext context)
	throws STeaException {

	Object value;

	try {
	    value = context.getVar(_symbol);
	} catch (SNoSuchVarException e2) {
	    value = STypes.getVarWithEffort(context, _symbol);
	}
	
	try {
	    return (SObjFunction)value;
	} catch (ClassCastException e1) {
	    throw new STypeException("variable " + _symbol.getName()+
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

	out.print(_symbol.getName());
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

