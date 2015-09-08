/**************************************************************************
 *
 * Copyright (c) 2015 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.lang;

import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaFunction;





/**************************************************************************
 *
 * Dummy Tea function obly intended for unit tests.
 *
 **************************************************************************/

public class TestFunction
    extends Object
    implements TeaFunction {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TestFunction() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Override
    public Object exec(final TeaFunction function,
                       final TeaContext  context,
                       final Object[]    args)
        throws TeaException {

        String result = "hello";

        return result;
   }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

