/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM1FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.SRuntimeException;






/**************************************************************************
 *
 * Exception thrown whenever a function expects a correctly formed
 * list of pairs but one of the cdr is not a pair object.
 *
 **************************************************************************/

public final class SMalformedListException
    extends SRuntimeException {




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SMalformedListException() {

      init("improperly formed list");
   }




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SMalformedListException(final Object[] args,
                                  final String   msg) {

      initForFunction(args, msg);
   }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/



