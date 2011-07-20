/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

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

        SObjFunction result = SWordSubstUtils.toFunction(_symbol, context);

        return result;
    }





/**************************************************************************
 *
 * This is used for debugging. At the moment it is rather slow.
 *
 **************************************************************************/

    // public void prettyPrint(PrintStream out,
    //                         int         indent) {

    //     out.print(_symbol.getName());
    // }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

