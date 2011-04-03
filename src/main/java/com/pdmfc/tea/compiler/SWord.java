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
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;





/**************************************************************************
 *
 * Objects implementing the <TT>SWord</TT> interface represent a word
 * in a Tea statement when evaluating a script.
 *
 **************************************************************************/

interface SWord {





/**************************************************************************
 *
 * Evaluates a word into an <TT>Object</TT>. It is invoked when
 * executing a command while evaluating its arguments but before
 * invoking the code that implements the command.
 *
 * @param context The context where the statement is being evaluated.
 *
 * @return The object representing an argument to the command.
 *
 * @exception STeaException Thrown for whatever abnormal condition.
 *
 **************************************************************************/

    public Object get(SContext context)
	throws STeaException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SObjFunction toFunction(SContext context)
	throws STeaException;

}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

