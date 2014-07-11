/**************************************************************************
 *
 * Copyright (c) 2001-2012 PDMFC, All Rights Reserved.
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

public class TeaException
    extends Exception {





    private String   _msgFmt  = null;
    private Object[] _fmtArgs = null;

    // The formated message.
    private String _msg = null;





/**************************************************************************
 *
 * This constructor builds an object with an empty message.
 *
 **************************************************************************/

    protected TeaException() {

        // Nothing to do.
    }






/**************************************************************************
 *
 * Initializes this exception with a
 * <code>java.text.MessageFormat</code> like message.
 *
 * @param msgFmt The format for the error message. This must be in the
 * same format as used by <code>java.text.MessageFormat</code>.
 *
 * @param fmtArgs The arguments used for formating the actual error
 * message.
 *
 **************************************************************************/

    public TeaException(final String    msgFmt,
                         final Object... fmtArgs) {

        init(msgFmt, fmtArgs);
    }





/**************************************************************************
 *
 * Initializes this exception with the message from another exception.
 *
 * @param e The exception whose message will be associated with this
 * object.
 *
 **************************************************************************/

    public TeaException(final Throwable e) {

        String msg = e.getMessage();

        init(msg);
    }






/**************************************************************************
 *
 * Initializes this exception with a
 * <code>java.text.MessageFormat</code> like message.
 *
 * <p>This method is intended for use by derived classes, when the
 * message to be used is not easily given as a base constructor
 * argument.</p>
 *
 * @param msgFmt The format for the error message. This must be in the
 * same format as used by <code>java.text.MessageFormat</code>.
 *
 * @param fmtArgs The arguments used for formating the actual error
 * message.
 *
 **************************************************************************/

    protected final void init(final String    msgFmt,
                              final Object... fmtArgs) {

        assert ( msgFmt != null );

        _msgFmt  = msgFmt;
        _fmtArgs = fmtArgs;
    }






/**************************************************************************
 *
 * Retrieves the formated message for this exception.
 *
 * @return The formated error message.
 *
 **************************************************************************/

    @Override
    public final String getMessage() {

        if ( _msg == null ) {
            if ( (_fmtArgs==null) || (_fmtArgs.length==0) ) {
                _msg = _msgFmt;
            } else {
                try {
                    _msg = MessageFormat.format(_msgFmt, _fmtArgs);
                } catch ( RuntimeException e ) {
                    // Something very bad just happened. Try to
                    // recover to a common sense position...
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

