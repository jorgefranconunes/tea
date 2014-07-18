/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import com.pdmfc.tea.TeaSymbol;
import com.pdmfc.tea.TeaRunException;





/**************************************************************************
 *
 * Exception thrown when trying to call a method a TOS class does not
 * respond to.
 *
 **************************************************************************/

public final class SNoSuchMethodException
    extends TeaRunException {




/**************************************************************************
 *
 * @param symbol Identifies the unknown method.
 *
 * @param className Name of the class. It can be null.
 *
 **************************************************************************/

   public SNoSuchMethodException(final TeaSymbol symbol,
                                 final String     className) {

      super("class "
            + ((className==null) ? "" : ("'" + className + "' "))
            + "has no method '" + symbol.getName() + "'");
   }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

