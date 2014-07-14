/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

final class SWordSymbol
    extends Object
    implements SWord {





    private TeaSymbol _symbol = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SWordSymbol(final String name) {

        _symbol = TeaSymbol.addSymbol(name);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object get(final TeaContext context) {

        return _symbol;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaSymbol getSymbol() {

        return _symbol;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaFunction toFunction(final TeaContext context)
        throws TeaException {

        TeaFunction result = SWordSubstUtils.toFunction(_symbol, context);

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

