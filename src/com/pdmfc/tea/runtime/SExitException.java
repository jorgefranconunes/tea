/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SExitException.java,v 1.2 2001/09/14 12:30:06 jfn Exp $
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






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public class SExitException
    extends SFlowControlException {





   public Integer _value;




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SExitException(Integer value) {

      _value = value;
   }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/



