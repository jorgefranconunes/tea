/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SReturnException.java,v 1.1 2001/07/11 13:58:29 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SFlowControlException;






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public class SReturnException
    extends SFlowControlException {





    public Object _value;




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SReturnException(Object value) {

      _value = value;
   }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/



