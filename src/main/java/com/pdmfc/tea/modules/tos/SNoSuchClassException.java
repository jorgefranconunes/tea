/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class SNoSuchClassException
    extends SRuntimeException {




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SNoSuchClassException(final SObjSymbol symbol) {

       String msg = "class \"{0}\" has not been defined";

       init(msg, symbol.getName());
   }




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SNoSuchClassException(final Object[]   args,
                                final SObjSymbol symbol) {

       String msg = "class \"{0}\" has not been defined";

       initForFunction(args, msg, symbol.getName());
   }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/



