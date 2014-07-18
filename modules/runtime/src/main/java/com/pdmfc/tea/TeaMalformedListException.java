/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDM1FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import com.pdmfc.tea.TeaRunException;






/**************************************************************************
 *
 * Exception thrown whenever a function expects a correctly formed
 * list of pairs but one of the cdr is not a pair object.
 *
 **************************************************************************/

public final class TeaMalformedListException
    extends TeaRunException {




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public TeaMalformedListException() {

      init("improperly formed list");
   }




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public TeaMalformedListException(final Object[] args,
                                    final String   msg) {

      initForFunction(args, msg);
   }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/



