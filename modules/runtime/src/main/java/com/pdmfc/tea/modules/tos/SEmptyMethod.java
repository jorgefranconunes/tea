/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaNull;





/**************************************************************************
 *
 * Represents a method that does nothing. This is an utility class.
 *
 **************************************************************************/

final class SEmptyMethod
    extends Object
    implements TeaFunction {





    private static TeaFunction _singleton = null;





/**************************************************************************
 *
 * The TOS method code. It just returns the Tea null object.
 *
 **************************************************************************/

      public Object exec(final TeaFunction obj,
                         final TeaContext     context,
                         final Object[]    args)
         throws TeaException {

         return TeaNull.NULL;
      }





/**************************************************************************
 *
 * @return Always returns the same instance of
 * <code>SEmptyMethod</code>.
 *
 **************************************************************************/

    public static TeaFunction singleton() {

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

