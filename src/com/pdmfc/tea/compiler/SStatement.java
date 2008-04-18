/**************************************************************************
 *
 * Copyright (c) 2001-2008 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SStatement.java,v 1.7 2002/09/17 16:35:27 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/03/28 No longer stores the file name from where the SStatement
 * was compiled. That is now stored by the SCode holding this
 * SStatement. (jfn)
 *
 * 2001/06/14 No longer uses an SObjArrayFactory. (jfn)
 *
 * 2001/05/12 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * Represents the compiled form of a single Tea statement. The
 * statement is stored as a list of words. These words are its
 * arguments, with the first argument representing the function.
 *
 **************************************************************************/

abstract class SStatement
    extends Object {





    // The line number this statement belongs to.
    protected int    _lineNumber;





/**************************************************************************
 *
 * Builds an empty argument list (null command).
 *
 * @param line The source line number where this statement occurs.
 *
 **************************************************************************/

    public SStatement(int line) {

	_lineNumber = line;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public int getLineNumber() {

	return _lineNumber;
    }





/**************************************************************************
 *
 * Executes this statement. The first word must be either a symbol
 * refering to a variable containing an <code>{@link
 * SObjFunction}</code>, or an <code>{@link SObjFunction}</code>
 * object.
 *
 * <p>The following steps are followed, in this order:</p>
 *
 * <ul>
 *
 * <li>The arguments are evaluated, left to right.</li>
 *
 * <li>The command to execute is fecthed from the first argument.</li>
 *
 * <li>The command is asked to executed with the given arguments.</li>
 *
 * </ul>
 *
 * @param context The context where the command will be executed.
 *
 * @return The object returned by the execution of the command.
 *
 * @exception STeaException An exception can be thrown in four cases:
 *
 * <ul>
 *
 * <li>While evaluating one of the arguments.</li>
 *
 * <li>The first word was a symbol but either there was no variable
 * with that name or it did not contain an <TT>SObjFunction</TT>.</li>
 *
 * <li>The first word was neither an <TT>SObjSymbol</TT> nor a
 * <TT>SObjFunction</TT>.</li>
 *
 * <li>an exception was raised during the execution of the command.</li>
 *
 * </ul>
 *
 **************************************************************************/

    public abstract Object exec(SContext context)
	throws STeaException;


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

