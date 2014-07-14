/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import com.pdmfc.tea.runtime.TeaSymbol;
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

   public SNoSuchClassException(final TeaSymbol symbol) {

       String msg = "class \"{0}\" has not been defined";

       init(msg, symbol.getName());
   }




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SNoSuchClassException(final Object[]   args,
                                final TeaSymbol symbol) {

       String msg = "class \"{0}\" has not been defined";

       initForFunction(args, msg, symbol.getName());
   }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/



