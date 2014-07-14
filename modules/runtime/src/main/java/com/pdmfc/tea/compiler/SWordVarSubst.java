/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.compiler.SWordSubstUtils;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaSymbol;





/**************************************************************************
 *
 * Objects of this class represent an argument to a command in the
 * parse tree. This argument is a symbol referencing a variable. At
 * runtime the contents of the variable will be used as the actual
 * argument.
 *
 **************************************************************************/

final class SWordVarSubst
    extends Object
    implements SWord {





    private TeaSymbol _symbol = null;





/**************************************************************************
 *
 * The constructor needs the name of a variable.
 *
 * @param name The name of the variable to be substituted at runtime.
 *
 **************************************************************************/

    public SWordVarSubst(final String name) {

        _symbol = TeaSymbol.addSymbol(name);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SWordVarSubst(final TeaSymbol symbol) {

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
 * @exception com.pdmfc.tea.TeaException A
 * <TT>SNoSuchVarException</TT> is thrown if there is not a variable
 * with the correct name in the <TT>context</TT>.
 *
 **************************************************************************/

    public Object get(final TeaContext context)
        throws TeaException {

        return context.getVar(_symbol);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaFunction toFunction(final TeaContext context)
        throws TeaException {

        Object       obj    = context.getVar(_symbol);
        TeaFunction result = SWordSubstUtils.toFunction(obj, context);

        return result;
    }






/**************************************************************************
 *
 * This is used for debugging. At the moment it is rather slow.
 *
 **************************************************************************/

    // public void prettyPrint(final PrintStream out,
    //                         final int         indent) {

    //     out.print("$" + _symbol.getName());
    // }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

