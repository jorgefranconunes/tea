/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import com.pdmfc.tea.TeaSymbol;
import com.pdmfc.tea.TeaRunException;





/**************************************************************************
 *
 * These exceptions are thrown whenever there is an attempt to
 * reference a variable not defined in the current context.
 *
 **************************************************************************/

public final class TeaNoSuchVarException
    extends TeaRunException {





/**************************************************************************
 *
 * Initializes the message from the name of the given symbol.
 *
 * @param symbol An <TT>TeaSymbol</TT> for whom there was no variable
 * defined in the current context.
 *
 **************************************************************************/

   public TeaNoSuchVarException(final TeaSymbol symbol) {

      super("Variable '"
            + symbol.getName()
            + "' not defined on current context");
   }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/



