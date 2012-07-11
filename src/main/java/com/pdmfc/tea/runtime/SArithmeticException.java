/**************************************************************************
 *
 * Copyright (c) 2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.SRuntimeException;






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class SArithmeticException
    extends SRuntimeException {




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private SArithmeticException() {

        // Nothing to do.
   }




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public static void raise(final Object[]            args,
                            final ArithmeticException exception)
       throws SArithmeticException {

       String               msgFmt = "arithmetic exception - {0}";
       SArithmeticException error  = new SArithmeticException();

       error.initForFunction(args, msgFmt, exception.getMessage());

       throw error;
   }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

