/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.io.SIOException;
import com.pdmfc.tea.modules.io.SInput;
import com.pdmfc.tea.modules.io.SOutput;
import com.pdmfc.tea.modules.net.SSocketFactory;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.Args;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaNumArgException;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaRunException;





/**************************************************************************
 *
 * Represents the TOS object that embodies a socket.
 *
 **************************************************************************/

public class SSocketBase
    extends STosObj {





    private SSocketFactory _sockFactory = null;
    private Socket         _socket      = null;
    private SInput         _input       = null;
    private SOutput        _output      = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SSocketBase(final STosClass myClass)
        throws TeaException {

        super(myClass);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    void initialize(final TeaContext context)
        throws TeaException {

        _input  = SInput.newInstance(context);
        _output = SOutput.newInstance(context);
    }





/**************************************************************************
 *
 * Opens the connection to the remote host. 
 *
 **************************************************************************/

    public Object connect(final TeaFunction obj,
                          final TeaContext     context,
                          final Object[]    args)
        throws TeaException {

        if ( args.length != 4 ) {
            throw new TeaNumArgException(args, "host port");
        }

        String host = Args.getString(args, 2);
        int    port = Args.getInt(args, 3).intValue();

        try {
            close();
        } catch ( SIOException e ) {
            // Just ignore it.
        }

        try {
            _socket = createSocket(host, port);
            _input.open(_socket.getInputStream());
            _output.open(_socket.getOutputStream());
        } catch ( UnknownHostException e1 ) {
            throw new SIOException("host \"{0}\" is unknown", host);
        } catch ( IOException e2 ) {
            String msg = "could not connect to host \"{0}\" on port {1} : {2}";
            throw new SIOException(msg,
                                   host,
                                   String.valueOf(port),
                                   e2.getMessage());
        }
        _output.setLineBuffering(true);

        return obj;
    }





/**************************************************************************
 *
 * Uses the socket received as argument.
 *
 **************************************************************************/

    public void connect(final Socket sock)
        throws SIOException {

        try {
            close();
        } catch ( SIOException e ) {
            // Just ignore it.
        }

        _socket = sock;

        try {
            _input.open(_socket.getInputStream());
            _output.open(_socket.getOutputStream());
        } catch ( IOException e ) {
            throw new SIOException("could not associate with native socket: "
                                    + e.getMessage());
        }
        _output.setLineBuffering(true);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object close(final TeaFunction obj,
                        final TeaContext     context,
                        final Object[]    args)
        throws TeaException {

        close();

        return obj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void close()
        throws SIOException {

        IOException error = null;

        try {
            _output.flush();
        } catch ( IOException e1 ) {
            error = e1;
        }
        try {
            if ( _socket != null ) {
                _socket.close();
            }
        } catch ( IOException e2 ) {
            if ( error == null ) {
                error = e2;
            }
        }
        try {
            _input.close();
        } catch ( IOException e3 ) {
            // Ignore any error.
        }
        try {
            _output.close();
        } catch ( IOException e4 ) {
            // Ignore any error.
        }
        _socket = null;
        if ( error != null ) {
            throw new SIOException(error);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object getInput(final TeaFunction obj,
                           final TeaContext     context,
                           final Object[]    args)
        throws TeaException {

        return _input;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object getOutput(final TeaFunction obj,
                            final TeaContext     context,
                            final Object[]    args)
        throws TeaException {

        return _output;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private Socket createSocket(final String host,
                                final int    port)
        throws TeaException {

        if ( _sockFactory == null ) {
            throw new TeaRunException("internal socket factory not set");
        }

        Socket sock = null;

        try {
            sock = _sockFactory.createSocket(host, port);
        } catch ( IOException e ) {
            String   msg     = "unable to crete socket - {0}";
            Object[] fmtArgs = { e.getMessage() };
            throw new TeaRunException(msg, fmtArgs);
        }

        return sock;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    void setSocketFactory(final SSocketFactory factory) {

        _sockFactory = factory;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

