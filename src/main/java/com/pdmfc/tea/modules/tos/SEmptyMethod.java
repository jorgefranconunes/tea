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

package com.pdmfc.tea.modules.tos;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;





/**************************************************************************
 *
 * Represents a method that does nothing. This is an utility class.
 *
 **************************************************************************/

class SEmptyMethod
    extends Object
    implements SObjFunction {





    private static SObjFunction _singleton = null;





/**************************************************************************
 *
 * The TOS method code. It just returns the Tea null object.
 *
 **************************************************************************/

      public Object exec(SObjFunction obj,
			 SContext     context,
			 Object[]     args)
         throws STeaException {

	 return SObjNull.NULL;
      }





/**************************************************************************
 *
 * @return
 *	Always returns the same instance of <TT>SEmptyMethod</TT>.
 *
 **************************************************************************/

    public static SObjFunction singleton() {

	if ( _singleton == null ) {
	    _singleton = new SEmptyMethod();
	}
	return _singleton;
    }
}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

