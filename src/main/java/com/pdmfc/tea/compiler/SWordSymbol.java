/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

final class SWordSymbol
    extends Object
    implements SWord {





    private SObjSymbol _symbol = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SWordSymbol(final String name) {

        _symbol = SObjSymbol.addSymbol(name);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object get(final SContext context) {

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

    public SObjFunction toFunction(final SContext context)
        throws STeaException {

        SObjFunction result = SWordSubstUtils.toFunction(_symbol, context);

        return result;
    }





/**************************************************************************
 *
 * This is used for debugging. At the moment it is rather slow.
 *
 **************************************************************************/

    // public void prettyPrint(final PrintStream out,
    //                         final int         indent) {

    //     out.print(_symbol.getName());
    // }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

