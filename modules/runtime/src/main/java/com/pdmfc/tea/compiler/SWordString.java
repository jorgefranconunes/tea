/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaRunException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

final class SWordString
    extends Object
    implements SWord {





    private String _string = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SWordString(final String s) {

        _string = s;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object get(final TeaContext context) {

        return _string;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaFunction toFunction(final TeaContext context)
        throws TeaException {

        throw new TeaRunException("a string can not be used as a function");
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

