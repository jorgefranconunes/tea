/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
 *
 *
 * Revisions:
 *
 * 2002/07/04
 * Moved to package "com.pdmfc.tea.tos". (jfn)
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.tos;

import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * Exception signaling an attempt to call a method on a TOS class that
 * does not respond to it.
 *
 **************************************************************************/

public class SNoSuchMethodException
    extends SRuntimeException {




/**************************************************************************
 *
 * @param methodName Identifies the unknown method.
 *
 * @param className Name of the class. It can be null.
 *
 **************************************************************************/

    public SNoSuchMethodException(String     className,
				  SObjSymbol methodName) {

	super("TOS class {0} has no {1} method",
	      new Object[] {((className==null) ? "" : className), methodName});
    }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

