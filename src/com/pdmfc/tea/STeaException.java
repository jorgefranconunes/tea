/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 * 
 * $Id: STeaException.java,v 1.3 2005/11/04 05:50:04 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
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

