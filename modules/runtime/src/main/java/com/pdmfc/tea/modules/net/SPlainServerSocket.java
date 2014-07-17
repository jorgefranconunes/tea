/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.io.SIOException;
import com.pdmfc.tea.modules.net.SPlainSocketBase;
import com.pdmfc.tea.modules.net.SSocketBase;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.Args;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.TeaRunException;





/**************************************************************************
 *
 * Represents the TOS object that embodies a server socket.
 *
 **************************************************************************/

public final class SPlainServerSocket
    extends STosObj {





    private int               _port         = -1;
    private ServerSocket      _serverSocket = null;

    private static final String     CLASS_NAME   = "TServerSocketPlain";
    private static final TeaSymbol CLASS_NAME_S =
        TeaSymbol.addSymbol(CLASS_NAME);





//* 
//* <TeaClass name="TServerSocketPlain"
//*           module="tea.net">
//*
//* <Overview>
//* Server socket.
//* </Overview>
//*
//* <Description>
//* Instances of <Class name="TServerSocketPlain"/> are used as
//* server sockets that listen for connections in a given port.
//* For each connection accepted by calling the <MethodRef name="accept"/>
//* method a new <ClassRef name="TSocket"/> is created. This
//* <ClassRef name="TSocket"/> represents the connection with the client
//* socket.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SPlainServerSocket(final STosClass myClass)
        throws TeaException {

        super(myClass);
    }





//* 
//* <TeaMethod name="constructor"
//*            arguments="portNumber"
//*                className="TServerSocketPlain">
//* 
//* <Overview>
//* Initializes the server socket.
//* </Overview>
//*
//* <Parameter name="portNumber">
//* Integer representing the port number where the server socket will
//* listen for connections.
//* </Parameter>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object constructor(final TeaFunction obj,
                              final TeaContext     context,
                              final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "port-number");
        }
        
        _port = Args.getInt(args,2).intValue();

        return obj;
    }





//* 
//* <TeaMethod name="accept"
//*                className="TServerSocketPlain">
//* 
//* <Overview>
//* Waits for an incoming connection.
//* </Overview>
//*
//* <Returns>
//* A new <Func name="TSocketPlain"/> object associated with the connection.
//* </Returns>
//* 
//* <Description>
//* This method blocks until a connection is made to the port the server
//* socket is listening in.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object accept(final TeaFunction obj,
                         final TeaContext     context,
                         final Object[]    args)
        throws TeaException {

        if ( _serverSocket == null ) {
            try {
                _serverSocket = new ServerSocket(_port);
            } catch ( IOException e ) {
                throw new SIOException("failed to create server socket: "
                                       + e.getMessage());
            }
        }

        Socket      sock    = null;
        SSocketBase tosSock = SPlainSocketBase.newInstance(context);

        try {
            sock = _serverSocket.accept();
        } catch ( IOException e ) {
            throw new SIOException("failed to accept on server socket: "
                                   + e.getMessage());
        }

        tosSock.connect(sock);

        return tosSock;
    }





//* 
//* <TeaMethod name="close"
//*                className="TServerSocketPlain">
//* 
//* <Overview>
//* Closes this server socket.
//* </Overview>
//*
//* <Returns>
//* A reference to the object for which the method was invoked.
//* </Returns>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object close(final TeaFunction obj,
                        final TeaContext     context,
                        final Object[]    args)
        throws TeaException {

        try {
            close();
        } catch ( IOException e ) {
            throw new SIOException("failed to close socket: " 
                                   + e.getMessage());
        }

        return obj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void close()
        throws IOException {

        if ( _serverSocket != null ) {
            _serverSocket.close();
            _serverSocket = null;
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static Object newInstance(final TeaContext context,
                                     final Object[] args)
        throws TeaException {

        STosObj servSock = STosUtil.newInstance(CLASS_NAME_S, context, args);

        if ( !(servSock instanceof SPlainServerSocket) ) {
            throw new TeaRunException("invalid {0} class", CLASS_NAME);
        }

        return servSock;
    }



}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

