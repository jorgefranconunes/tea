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
 * 
 *
 **************************************************************************/

public final class SNoSuchFunctionException
    extends SRuntimeException {




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SNoSuchFunctionException(SObjSymbol symbol) {

      super("Function '"
            + symbol.getName()
            + "' not defined on current context");
   }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/



