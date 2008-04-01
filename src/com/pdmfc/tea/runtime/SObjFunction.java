/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SObjFunction.java,v 1.1 2001/07/11 13:58:28 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SObjBlock;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNoSuchVarException;
import com.pdmfc.tea.runtime.STypeException;





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
 * @param func
 *    The <TT>SObjFunction</TT> that led to call this function. Most of the
 *    time is the same as the <TT>this</TT> object.
 *
 * @param context
 *    The context where the command should be executed.
 *
 * @param args
 *    Array with the arguments passed to the command.
 *
 * @return
 *    An <TT>Object</TT> object.
 *
 * @exception com.pdmfc.tea.STeaException
 *    Thrown if there were problems executing the command.
 *
 **************************************************************************/

    public Object exec(SObjFunction func,
		       SContext     context,
		       Object[]     args)
    throws STeaException;


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

