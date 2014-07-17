/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.compiler.TeaCode;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaBlock;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaRunException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

final class SWordBlock
    extends Object
    implements SWord {





    private TeaCode _code;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SWordBlock(final TeaCode code) {

        _code = code;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object get(final TeaContext context) {

        final TeaCode code = _code;

        TeaBlock block = new TeaBlock() {
                public TeaContext getContext() {
                    return context;
                }
                public Object exec(final TeaContext cntxt)
                    throws TeaException {
                    return code.execute(cntxt);
                }
                public Object exec()
                    throws TeaException {
                    return code.execute(context.newChild());
                }
            };

        return block;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaFunction toFunction(final TeaContext context)
        throws TeaException {

        throw new TeaRunException("a block can not be used as a function");
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

