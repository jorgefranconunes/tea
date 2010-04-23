/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SNoSuchMethodException.java,v 1.3 2002/07/24 16:28:46 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
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

public class SNoSuchMethodException
    extends SRuntimeException {




/**************************************************************************
 *
 * @param symbol Identifies the unknown method.
 *
 * @param className Name of the class. It can be null.
 *
 **************************************************************************/

   public SNoSuchMethodException(SObjSymbol symbol,
				 String     className) {

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

