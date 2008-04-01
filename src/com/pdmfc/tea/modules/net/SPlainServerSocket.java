/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SPlainServerSocket.java,v 1.3 2005/01/20 14:20:58 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/10/20
 * Renamed from "SServerSocketPlain" to "SPlainServerSocket". (jfn)
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.io.SIOException;
import com.pdmfc.tea.modules.net.SPlainSocketBase;
import com.pdmfc.tea.modules.net.SSocketBase;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypes;





/**************************************************************************
 *
 * Represents the TOS object that embodies a server socket.
 *
 **************************************************************************/

public class SPlainServerSocket
    extends STosObj {





    private int               _port         = -1;
    private ServerSocket      _serverSocket = null;

    private static final String     CLASS_NAME   = "TServerSocketPlain";
    private static final SObjSymbol CLASS_NAME_S = SObjSymbol.addSymbol(CLASS_NAME);





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

    public SPlainServerSocket(STosClass myClass)
	throws STeaException {

	super(myClass);
    }





//* 
//* <TeaMethod name="constructor"
//*            arguments="portNumber"
//* 	       className="TServerSocketPlain">
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

    public Object constructor(SObjFunction obj,
			      SContext     context,
			      Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException("port-number");
	}
	
	_port = STypes.getInt(args,2).intValue();

	return obj;
    }





//* 
//* <TeaMethod name="accept"
//* 	       className="TServerSocketPlain">
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

    public Object accept(SObjFunction obj,
			 SContext     context,
			 Object[]     args)
	throws STeaException {

	if ( _serverSocket == null ) {
	    try {
		_serverSocket = new ServerSocket(_port);
	    } catch (IOException e) {
		throw new SIOException("failed to create server socket: "
				       + e.getMessage());
	    }
	}

	Socket      sock    = null;
	SSocketBase tosSock = SPlainSocketBase.newInstance(context);

	try {
	    sock = _serverSocket.accept();
	} catch (IOException e) {
	    throw new SIOException("failed to accept on server socket: "
				   + e.getMessage());
	}

	tosSock.connect(sock);

	return tosSock;
    }





//* 
//* <TeaMethod name="close"
//* 	       className="TServerSocketPlain">
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

    public Object close(SObjFunction obj,
			SContext     context,
			Object[]     args)
	throws STeaException {

	try {
	    close();
	} catch (IOException e) {
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

    public static Object newInstance(SContext context,
				     Object[] args)
	throws STeaException {

	STosObj servSock = STosUtil.newInstance(CLASS_NAME_S, context, args);

	if ( !(servSock instanceof SPlainServerSocket) ) {
	    throw new SRuntimeException("invalid " + CLASS_NAME + " class");
	}

	return servSock;
    }



}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

