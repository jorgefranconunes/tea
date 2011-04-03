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

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public class SNoSuchFunctionException
    extends SRuntimeException {




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SNoSuchFunctionException(SObjSymbol symbol) {

      super("Function '" + symbol.getName() +
	    "' not defined on current context");
   }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/



