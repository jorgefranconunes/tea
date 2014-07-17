/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDM1FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.TeaRunException;






/**************************************************************************
 *
 * Exception thrown whenever a function expects a correctly formed
 * list of pairs but one of the cdr is not a pair object.
 *
 **************************************************************************/

public final class SMalformedListException
    extends TeaRunException {




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



