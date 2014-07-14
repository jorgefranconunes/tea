/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.runtime.TeaContext;





/**************************************************************************
 *
 * This class represents a block of code. Associated with every block
 * is the context where it was created.
 *
 **************************************************************************/

public interface TeaBlock {





/**************************************************************************
 *
 * The context where the block was created.
 *
 * @return A reference to the object refering to the context where the
 * block was created.
 *
 **************************************************************************/

    TeaContext getContext();





/**************************************************************************
 *
 * Executes the block in the context given as argument. New variables
 * are created inside that context.
 *
 * @param aContext Context inside which the block will be executed.
 *
 * @return The value returned by the last statement in the block.
 *
 * @exception TeaException Thrown if something happened while
 * executing the statements that form the block.
 *
 **************************************************************************/

    Object exec(TeaContext aContext)
        throws TeaException;





/**************************************************************************
 *
 * Executes the block in a new context which is an imediate descendent
 * of the context where the block was created.
 *
 * @return The value returned by the execution of last statement in
 * the block. Null if the block had no statements.
 *
 * @exception TeaException Thrown if something happened while
 * executing the statements that form the block.
 *
 **************************************************************************/

    Object exec()
        throws TeaException;


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

