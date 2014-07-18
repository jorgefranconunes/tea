/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import com.pdmfc.tea.TeaRunException;






/**************************************************************************
 *
 * Exception thrown to signal that a Tea function is being invoked
 * with the wrong number of arguments.
 *
 **************************************************************************/

public final class TeaNumArgException
    extends TeaRunException {




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private TeaNumArgException() {

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

   public TeaNumArgException(final Object[] args,
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

