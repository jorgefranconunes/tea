/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;





/**************************************************************************
 *
 * Tea commands are represented by objects derived from this class.
 *
 **************************************************************************/

public interface SObjFunction  {





/**************************************************************************
 *
 * Executes the command.
 *
 * @param func The <code>SObjFunction</code> that led to call this
 * function. Most of the time is the same as the <code>this</code>
 * object.
 *
 * @param context The context where the command should be executed.
 *
 * @param args Array with the arguments passed to the command.
 *
 * @return An <code>Object</code> object.
 *
 * @exception com.pdmfc.tea.STeaException Thrown if there were
 * problems executing the command.
 *
 **************************************************************************/

    Object exec(SObjFunction func,
                SContext     context,
                Object[]     args)
        throws STeaException;


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

