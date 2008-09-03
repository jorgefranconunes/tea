/**************************************************************************
 *
 * Copyright (c) 2001-2008 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 * 
 * $Id$
 *
 *
 * Revisions:
 *
 * 2008/09/02 Added the methods getMessageFormat(),
 * getMessageArguments(). (jfn)
 *
 * 2001/05/12 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea;

import java.text.MessageFormat;





/**************************************************************************
 *
 * Signals an abnormal condition while compiling or running a Tea
 * script.
 *
 **************************************************************************/

public class STeaException
    extends Exception {





    private String   _msgFmt  = null;
    private Object[] _fmtArgs = null;
    private String   _msg     = null;





/**************************************************************************
 *
 * This constructor builds an object with an empty message.
 *
 **************************************************************************/

    public STeaException() {
    }





/**************************************************************************
 *
 * This constructor builds an object with the message given as argument.
 *
 * @param msg
 *    The message to be associated with this object.
 *
 **************************************************************************/

    public STeaException(String msg) {

	_msgFmt = msg;
    }





/**************************************************************************
 *
 * This constructor builds an object with the message from another
 * exception.
 *
 * @param e The exception whose mmessage will be associated with this
 * object.
 *
 **************************************************************************/

    public STeaException(Exception e) {

	_msgFmt = e.getMessage();
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public STeaException(String   msgFmt,
			 Object[] fmtArgs) {

	_msgFmt  = msgFmt;
	_fmtArgs = fmtArgs;
    }





/***************************************************************************
 *
 * Fetches the formating string passed to one of the constructors.
 *
 * @return The formating string passed to one of the constructors.
 *
 ***************************************************************************/

    public String getMessageFormat() {

        return _msgFmt;
    }





/***************************************************************************
 *
 * Fetches the array of formating arguments that were passed to the
 * constructor.
 *
 * @return The array of message arguments that were passed to the
 * constructor.
 *
 ***************************************************************************/

    public Object[] getMessageArguments() {

        return _fmtArgs;
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public String getMessage() {

	if ( _msg == null ) {
	    if ( _fmtArgs == null ) {
		_msg = _msgFmt;
	    } else {
		try {
		    _msg = MessageFormat.format(_msgFmt, _fmtArgs);
		} catch (RuntimeException e) {
		    _msg = _msgFmt;
		}
	    }
	}

	return _msg;
    }


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

