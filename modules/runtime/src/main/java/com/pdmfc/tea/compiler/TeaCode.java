/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.compiler.SStatement;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaFlowControlException;
import com.pdmfc.tea.TeaNull;
import com.pdmfc.tea.TeaRunException;





/**************************************************************************
 *
 * A Tea program that can be executed in a given context. A Tea
 * program is just a sequence of Tea statements.
 *
 **************************************************************************/

public final class TeaCode
    extends Object {




    private SCodeNode _statsHead = null;
    private SCodeNode _statsTail = null;
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

    TeaCode(final String fileName) {

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
 * @exception TeaException Only thrown by the execution of one of the
 * statements.
 *
 **************************************************************************/

    public Object execute(final TeaContext context)
        throws TeaException {

        Object value = TeaNull.NULL;

        for ( SCodeNode node=_statsHead; node!=null; node=node._next ) {
            SStatement statement = node._element;
            try {
                value = statement.exec(context);
            } catch ( TeaRunException e ) {
                int      lineNum = statement.getLineNumber();
                Object[] fmtArgs = { String.valueOf(lineNum), _fileName };
                String   fmtMsg  = (_fileName==null) ? ERR_STAT :ERR_STAT_FILE;

                e.addMessage(fmtMsg, fmtArgs);
                throw e;
            } catch ( TeaFlowControlException e ) {
                throw e;
            } catch ( TeaException e ) {
                int      lineNum = statement.getLineNumber();
                Object[] fmtArgs = { String.valueOf(lineNum), _fileName };
                String   fmtMsg  = (_fileName==null) ? ERR_STAT :ERR_STAT_FILE;
                TeaRunException error = new TeaRunException(e.getMessage());

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
 * @param statement Reference to the new statement to be appended to
 * the sequence.
 *
 **************************************************************************/

    void addStatement(final SStatement statement) {

        SCodeNode newStat = new SCodeNode(statement);

        if ( _statsHead == null ) {
            _statsHead = newStat;
        } else {
            _statsTail._next = newStat;
        }
        _statsTail = newStat;
    }





/**************************************************************************
 *
 * Nodes of a linked list.
 *
 **************************************************************************/

    private static final class SCodeNode
        extends Object {





        public SStatement _element = null;
        public SCodeNode  _next    = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public SCodeNode(final SStatement statement) {

            _element = statement;
        }

    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

