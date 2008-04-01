/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 * 
 * $Id: SBreakException.java,v 1.1 2001/07/11 13:58:28 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.SFlowControlException;
import com.pdmfc.tea.runtime.SObjNull;






/**************************************************************************
 *
 * These exceptions are thrown to break out of a loop.
 *
 **************************************************************************/

public class SBreakException
    extends SFlowControlException {





  /** The value to return to the inner loop. */
   public Object _object;




/**************************************************************************
 *
 * Initializes the objects internal state.
 *
 * @param obj
 *     The value to return by the command that was performing the loop.
 *
 **************************************************************************/

   public SBreakException(Object obj) {

      _object = obj;
   }




/**************************************************************************
 *
 * Initializes the objects internal state. A null Tea object will be
 * passed to the command performing the loop.
 *
 **************************************************************************/

   public SBreakException() {

      this(SObjNull.NULL);
   }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/



