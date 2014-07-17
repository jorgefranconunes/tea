/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.TeaRunException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class SNoSuchClassException
    extends TeaRunException {




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



