/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.TeaRunException;






/**************************************************************************
 *
 * Exception thrown to signal that a Tea function is being invoked
 * with the wrong number of arguments.
 *
 **************************************************************************/

public final class SNumArgException
    extends TeaRunException {




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private SNumArgException() {

        // Nothing to do.
   }




/**************************************************************************
 *
 * @param args The arguments the function is being invoked with.
 *
 * @param usage Message describing the arguments expected by the
 * function. It will be used to construct the exception message.
 *
 **************************************************************************/

   public SNumArgException(final Object[] args,
                           final String   usage) {

       String msgFmt = "args : {0}";

       initForFunction(args, msgFmt, usage);
   }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

