/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaContext;





/**************************************************************************
 *
 * Tea functions are represented by objects implementing this
 * interface.
 *
 **************************************************************************/

public interface TeaFunction  {





/**************************************************************************
 *
 * Executes the command.
 *
 * @param func The <code>TeaFunction</code> that led to call this
 * function. Most of the time is the same as the <code>this</code>
 * object.
 *
 * @param context The context where the function should be executed.
 *
 * @param args Array with the arguments passed to the function.
 *
 * @return An <code>Object</code> object.
 *
 * @exception com.pdmfc.tea.TeaException Thrown if there were problems
 * executing the function.
 *
 **************************************************************************/

    Object exec(TeaFunction func,
                TeaContext  context,
                Object[]    args)
        throws TeaException;


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

