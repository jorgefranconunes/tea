/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * These exceptions are thrown whenever there is an attempt to
 * reference a variable not defined in the current context.
 *
 **************************************************************************/

public final class SNoSuchVarException
    extends SRuntimeException {





/**************************************************************************
 *
 * Initializes the message from the name of the given symbol.
 *
 * @param symbol An <TT>SObjSymbol</TT> for whom there was no variable
 * defined in the current context.
 *
 **************************************************************************/

   public SNoSuchVarException(final SObjSymbol symbol) {

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



