/**************************************************************************
 *
 * Copyright (c) 2011-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import com.pdmfc.tea.TeaRunException;






/**************************************************************************
 *
 * Signals an arithmetic error has occurred.
 *
 **************************************************************************/

public final class TeaArithmeticException
    extends TeaRunException {




/**************************************************************************
 *
 * Instances of this object are only created by us.
 *
 **************************************************************************/

   private TeaArithmeticException() {

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
       throws TeaArithmeticException {

       String               msgFmt = "arithmetic exception - {0}";
       TeaArithmeticException error  = new TeaArithmeticException();

       error.initForFunction(args, msgFmt, exception.getMessage());

       throw error;
   }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

