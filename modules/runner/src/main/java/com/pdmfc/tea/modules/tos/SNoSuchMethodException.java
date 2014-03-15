/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * Exception thrown when trying to call a method a TOS class does not
 * respond to.
 *
 **************************************************************************/

public final class SNoSuchMethodException
    extends SRuntimeException {




/**************************************************************************
 *
 * @param symbol Identifies the unknown method.
 *
 * @param className Name of the class. It can be null.
 *
 **************************************************************************/

   public SNoSuchMethodException(final SObjSymbol symbol,
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

