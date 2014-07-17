/**************************************************************************
 *
 * Copyright (c) 2011-2012 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.TeaRunException;






/**************************************************************************
 *
 * Signals an arithmetic error has occurred.
 *
 **************************************************************************/

public final class SArithmeticException
    extends TeaRunException {




/**************************************************************************
 *
 * Instances of this object are only created by us.
 *
 **************************************************************************/

   private SArithmeticException() {

        // Nothing to do.
   }




/**************************************************************************
 *
 * Throws an exception properly initialized.
 *
 * @param args The arguments passed to the function that is raising
 * the arithmetic exception.
 *
 * @param exception The underlying Java runtime arithmetic exception.
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

