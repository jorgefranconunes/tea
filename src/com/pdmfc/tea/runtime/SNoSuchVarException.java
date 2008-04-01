/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SNoSuchVarException.java,v 1.1 2001/07/11 13:58:28 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * These exceptions are thrown whenever there is an attempt to reference
 * a variable not defined in the current context.
 *
 **************************************************************************/

public class SNoSuchVarException
    extends SRuntimeException {





/**************************************************************************
 *
 * Initializes the message from the name of the given symbol.
 *
 * @param symbol
 *    An <TT>SObjSymbol</TT> for whom there was no variable defined in
 *    the current context.
 *
 **************************************************************************/

   public SNoSuchVarException(SObjSymbol symbol) {

      super("Variable '" + symbol.getName() +
	    "' not defined on current context");
   }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/



