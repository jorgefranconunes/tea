/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.io.SIOException;
import com.pdmfc.tea.modules.io.SInput;
import com.pdmfc.tea.modules.io.SOutput;
import com.pdmfc.tea.modules.net.SPlainSocketFactory;
import com.pdmfc.tea.modules.net.SSocketFactory;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.SArgs;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypes;





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

    public SSocketBase(STosClass myClass)
        throws STeaException {

        super(myClass);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    void initialize(SContext     context)
        throws STeaException {

        _input  = SInput.newInstance(context);
        _output = SOutput.newInstance(context);
    }





/**************************************************************************
 *
 * Opens the connection to the remote host. 
 *
 **************************************************************************/

    public Object connect(SObjFunction obj,
                          SContext     context,
                          Object[]     args)
        throws STeaException {

        if ( args.length != 4 ) {
            throw new SNumArgException(args, "host port");
        }

        String host = SArgs.getString(args, 2);
        int    port = SArgs.getInt(args, 3).intValue();

        try {
            close();
        } catch (SIOException e) {
            // Just ignore it.
        }

        try {
            _socket = createSocket(host, port);
            _input.open(_socket.getInputStream());
            _output.open(_socket.getOutputStream());
        } catch (UnknownHostException e1) {
            throw new SIOException("host \"{0}\" is unknown", host);
        } catch (IOException e2) {
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

    public void connect(Socket sock)
        throws SIOException {

        try {
            close();
        } catch (SIOException e) {
            // Just ignore it.
        }

        _socket = sock;

        try {
            _input.open(_socket.getInputStream());
            _output.open(_socket.getOutputStream());
        } catch (IOException e) {
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

    public Object close(SObjFunction obj,
                        SContext     context,
                        Object[]     args)
        throws STeaException {

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
        } catch (IOException e1) {
            error = e1;
        }
        try {
            if ( _socket != null ) {
                _socket.close();
            }
        } catch (IOException e2) {
            if ( error == null ) {
                error = e2;
            }
        }
        try {
            _input.close();
        } catch (IOException e3) {
            // Ignore any error.
        }
        try {
            _output.close();
        } catch (IOException e4) {
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

    public Object getInput(SObjFunction obj,
                           SContext     context,
                           Object[]     args)
        throws STeaException {

        return _input;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object getOutput(SObjFunction obj,
                            SContext     context,
                            Object[]     args)
        throws STeaException {

        return _output;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private Socket createSocket(String host,
                                int    port)
        throws STeaException {

        if ( _sockFactory == null ) {
            throw new SRuntimeException("internal socket factory not set");
        }

        Socket sock = null;

        try {
            sock = _sockFactory.createSocket(host, port);
        } catch (IOException e) {
            String   msg     = "unable to crete socket - {0}";
            Object[] fmtArgs = { e.getMessage() };
            throw new SRuntimeException(msg, fmtArgs);
        }

        return sock;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    void setSocketFactory(SSocketFactory factory) {

        _sockFactory = factory;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

