/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
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

final class SEmptyMethod
    extends Object
    implements SObjFunction {





    private static SObjFunction _singleton = null;





/**************************************************************************
 *
 * The TOS method code. It just returns the Tea null object.
 *
 **************************************************************************/

      public Object exec(final SObjFunction obj,
                         final SContext     context,
                         final Object[]     args)
         throws STeaException {

         return SObjNull.NULL;
      }





/**************************************************************************
 *
 * @return Always returns the same instance of
 * <code>SEmptyMethod</code>.
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

