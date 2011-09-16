/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SObjSymbol;





/**************************************************************************
 *
 * Signals an abnormal condition while running a Tea script.
 *
 * <p>This type of excemtpion mantains a list of the messages being
 * generated as the call stack unrolls.</p>
 *
 **************************************************************************/

public class SRuntimeException
    extends STeaException {





    private List<String> _msgList = new ArrayList<String>();






/**************************************************************************
 *
 * Empty constructorm such that derived classes may call one of the
 * init methods.
 *
 **************************************************************************/

    protected SRuntimeException() {

        // Nothing to do.
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SRuntimeException(final String    msgFmt,
			     final Object... fmtArgs) {

	init(msgFmt, fmtArgs);
    }





/**************************************************************************
 *
 * Initializes the message from the zeroth argument of a command and
 * from an error message.
 *
 * @param args The arguments the function was called with.
 *
 * @param msgFmt The template string for the actual log message.
 *
 * @param fmtArgs The formating used when creating the actual log
 * message from the template.
 *
 **************************************************************************/
 
    public SRuntimeException(final Object[]  args,
                             final String    msgFmt,
                             final Object... fmtArgs) {

        initForFunction(args, msgFmt, fmtArgs);
    }





/**************************************************************************
 *
 * Initializes the message with the same message as the exception
 * given as argument.
 *
 * @param e An <TT>java.lang.Exception</TT> from where the message
 * will be retrieved.
 *
 **************************************************************************/

    public SRuntimeException(final Throwable e) {

        String errorType = e.getClass().getName();
        String errorText = e.getMessage();
        String msgFmt    = (errorText!=null) ? "{0} - {1}" : "{0}";

        init(msgFmt, errorType, errorText);
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    protected void initForFunction(final Object[]  args,
                                   final String    msgFmt,
                                   final Object... fmtArgs) {

        Object arg0 = args[0];
        String fmt  = null;

        if ( arg0 instanceof SObjSymbol ) {
            fmt =
                (new StringBuilder())
                .append(arg0)
                .append(" : ")
                .append(msgFmt)
                .toString();
        } else {
            fmt = msgFmt;
        }

        init(fmt, fmtArgs);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/
 
    public void addMessage(final String    msgFmt,
			   final Object... fmtArgs) {

	String msg = null;

        if ( (fmtArgs!=null) && (fmtArgs.length>0) ) {
            msg = MessageFormat.format(msgFmt, fmtArgs);
        } else {
            msg = msgFmt;
        }

	_msgList.add(msg);
    }





/**************************************************************************
 *
 * Builds a <TT>String</TT> containing the text of all the messages on
 * the message list. Each message is set on a line of its own.
 *
 * @return A <TT>String</TT> with the text of all messages.
 *
 **************************************************************************/

    public String getFullMessage() {

        StringBuilder builder    = new StringBuilder();
        String        topMessage = super.getMessage();

        builder
            .append(topMessage)
            .append("\n");
        
        for ( String line : _msgList ) {
            builder
                .append(line)
                .append("\n");
        }

        String result = builder.toString();

        return result;
    }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/
