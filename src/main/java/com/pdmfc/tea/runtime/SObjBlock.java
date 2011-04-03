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

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;





/**************************************************************************
 *
 * This class represents a block of code. Associated with every block
 * is the context where it was created.
 *
 **************************************************************************/

public interface SObjBlock {





/**************************************************************************
 *
 * The context where the block was created.
 *
 * @return A reference to the object refering to the context where the
 * block was created.
 *
 **************************************************************************/

    public SContext getContext();





/**************************************************************************
 *
 * Executes the block in the context given as argument. New variables
 * are created inside that context.
 *
 * @param aContext Context inside which the block will be executed.
 *
 * @return The value returned by the last statement in the block.
 *
 * @exception STeaException Thrown if something happened while
 * executing the statements that form the block.
 *
 **************************************************************************/

    public Object exec(SContext aContext)
	throws STeaException;





/**************************************************************************
 *
 * Executes the block in a new context which is an imediate descendent
 * of the context where the block was created.
 *
 * @return The value returned by the execution of last statement in
 * the block. Null if the block had no statements.
 *
 * @exception STeaException Thrown if something happened while
 * executing the statements that form the block.
 *
 **************************************************************************/

    public Object exec()
	throws STeaException;


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

