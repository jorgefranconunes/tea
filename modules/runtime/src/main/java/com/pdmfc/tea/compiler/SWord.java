/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;





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
 * @exception TeaException Thrown for whatever abnormal condition.
 *
 **************************************************************************/

    Object get(TeaContext context)
        throws TeaException;





/**************************************************************************
 *
 * Evaluates a word and attempts to use as a function.
 *
 * <p>This is used for obtaining the function to execute from the
 * zero-th argument of a statement. Most word types will just throw an
 * exception.</p>
 *
 * @param context
 *
 **************************************************************************/

    TeaFunction toFunction(TeaContext context)
        throws TeaException;

}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

