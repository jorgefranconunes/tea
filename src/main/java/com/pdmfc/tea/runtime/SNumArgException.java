/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.SRuntimeException;






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class SNumArgException
    extends SRuntimeException {




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
 * 
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

