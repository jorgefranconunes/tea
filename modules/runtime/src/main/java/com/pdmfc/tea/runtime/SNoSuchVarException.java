/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.TeaRunException;





/**************************************************************************
 *
 * These exceptions are thrown whenever there is an attempt to
 * reference a variable not defined in the current context.
 *
 **************************************************************************/

public final class SNoSuchVarException
    extends TeaRunException {





/**************************************************************************
 *
 * Initializes the message from the name of the given symbol.
 *
 * @param symbol An <TT>TeaSymbol</TT> for whom there was no variable
 * defined in the current context.
 *
 **************************************************************************/

   public SNoSuchVarException(final TeaSymbol symbol) {

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



