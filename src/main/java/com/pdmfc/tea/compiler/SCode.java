/**************************************************************************
 *
 * Copyright (c) 2001-2010 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import java.io.PrintStream;
import java.util.Enumeration;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SStatement;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SFlowControlException;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * A Tea program that can be executed in a given context. A Tea
 * program is just a sequence of Tea statements.
 *
 **************************************************************************/

public class SCode
    extends Object {




    private SCodeNode _statsHead = null;
    private SCodeNode _statsTail = null;
    private int       _statCount = 0;
    private String    _fileName  = null;

    private static final String ERR_STAT =
	"While evaluating statement on line {0}";

    private static final String ERR_STAT_FILE =
	"While evaluating statement on line {0} ({1})";





/**************************************************************************
 *
 * Builds an empty statement list.
 *
 **************************************************************************/

    SCode(String fileName) {

	_fileName = fileName;
    }





/**************************************************************************
 *
 * Executes the sequence of Tea statements in the <TT>context</TT>
 * given as argument.
 *
 * @param context Context inside wich the block will be executed.
 *
 * @return The value returned by the last command in the block or null
 * if it is an empty block.
 *
 * @exception STeaException Only thrown by the execution of one of the
 * statements.
 *
 **************************************************************************/

    public final Object exec(SContext context)
	throws STeaException {

	Object value = SObjNull.NULL;

	for ( SCodeNode node=_statsHead; node!=null; node=node._next ) {
	    SStatement statement = node._element;
	    try {
		value = statement.exec(context);
	    } catch (SRuntimeException e) {
		int      lineNum = statement.getLineNumber();
		Object[] fmtArgs = { String.valueOf(lineNum), _fileName };
		String   fmtMsg  = (_fileName==null) ? ERR_STAT :ERR_STAT_FILE;

		e.addMessage(fmtMsg, fmtArgs);
		throw e;
	    } catch (SFlowControlException e) {
		throw e;
	    } catch (STeaException e) {
		int      lineNum = statement.getLineNumber();
		Object[] fmtArgs = { String.valueOf(lineNum), _fileName };
		String   fmtMsg  = (_fileName==null) ? ERR_STAT :ERR_STAT_FILE;
		SRuntimeException error =new SRuntimeException(e.getMessage());

		error.addMessage(fmtMsg, fmtArgs);
		throw error;		
	    }
	}

	return value;
    }






/**************************************************************************
 *
 * Adds a new Tea statement at the end of the sequence.
 *
 * @param aStatement Reference to the new statement to be appended to
 * the sequence.
 *
 **************************************************************************/

    final void addStatement(SStatement aStatement) {

	SCodeNode newStat = new SCodeNode(aStatement);

	if ( _statsHead == null ) {
	    _statsHead = newStat;
	} else {
	    _statsTail._next = newStat;
	}
	_statsTail = newStat;
	_statCount++;
    }






/**************************************************************************
 *
 * This is used for debugging. At the moment it is rather slow.
 *
 * @param out A stream where the message will be sent to.
 *
 * @param ident Number of padding white spaces inserted into the left.
 *
 **************************************************************************/

//    public final void prettyPrint(PrintStream out, int indent) {
//
//	for (  SCodeNode node=_statsHead; node!=null; node=node._next ) {
//	    node._element.prettyPrint(out, indent);
//	}
//    }


}





/**************************************************************************
 *
 * Nodes of a linked list.
 *
 **************************************************************************/

class SCodeNode
    extends Object {





    public SStatement _element = null;
    public SCodeNode  _next    = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SCodeNode(SStatement statement) {

	_element = statement;
    }

}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

