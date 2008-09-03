/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SRuntimeException.java,v 1.5 2002/08/03 17:53:30 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/08/03
 * The list of messages is now a java.util.List, instead of an
 * SList. (jfn)
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SObjSymbol;





/**************************************************************************
 *
 * Signals an abnormal condition while running a Tea script. An
 * <TT>SRuntimeException</TT> mantains a list of the messages being
 * generated as the call stack unrolls.
 *
 **************************************************************************/

public class SRuntimeException
    extends STeaException {





    private List<String> _msgList = new ArrayList<String>();





/**************************************************************************
 *
 * These exceptions are thrown while executing a Tea script and
 * represents abnormal conditions.
 *
 **************************************************************************/

    public SRuntimeException(String msg) {

	super(msg);

	_msgList.add(msg);
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SRuntimeException(String   msgFmt,
			     Object[] fmtArgs) {

	super(msgFmt, fmtArgs);

	_msgList.add(getMessage());
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

    public SRuntimeException(Throwable e) {

	this(e.getMessage());
    }




/**************************************************************************
 *
 * Initializes the message from the zeroth argument of a command and
 * from an error message.
 *
 * @param arg0 The zeroth argument of a command.
 *
 * @param msg A string with an error message.
 *
 **************************************************************************/

    public SRuntimeException(Object arg0,
			     String msg) {

	this(((arg0 instanceof SObjSymbol) ?
	      (((SObjSymbol)arg0).getName()+": ") : "") + msg);
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/
 
    public SRuntimeException(Object   arg0,
			     String   msgFmt,
			     Object[] fmtArgs) {

	this((arg0 instanceof SObjSymbol) ? (((SObjSymbol)arg0).getName()+": "+msgFmt) : msgFmt,
	      fmtArgs);
    }





/**************************************************************************
 *
 * Adds a message to the end of the message list.
 *
 * @param msg Text of the message to be added at the end of the
 * message list.
 *
 **************************************************************************/

   public void addMessage(String msg) {

      _msgList.add(msg);
   }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/
 
    public void addMessage(String   msgFmt,
			   Object[] fmtArgs) {

	String msg = MessageFormat.format(msgFmt, fmtArgs);

	addMessage(msg);
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

      StringBuffer message = new StringBuffer();

      for ( Iterator i=_msgList.iterator(); i.hasNext(); ) {
	 message.append((String)i.next() + "\n");
      }

      return message.toString();
   }

}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

