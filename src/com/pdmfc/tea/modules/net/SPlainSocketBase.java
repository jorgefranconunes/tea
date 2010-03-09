/**************************************************************************
 *
 * Copyright (c) 2002 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SPlainSocketBase.java,v 1.3 2002/11/02 16:05:02 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/10/19
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.net;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.net.SPlainSocketFactory;
import com.pdmfc.tea.modules.net.SSocketFactory;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypes;





//* 
//* <TeaClass name="TSocketBase"
//*           module="tea.net">
//*
//* <Overview>
//* Client socket.
//* </Overview>
//*
//* <Description>
//* Instances of <Class name="TSocketBase"/> are used as client sockets
//* to connect to remote servers. After the connection is
//* established the methods <MethodRef name="getInput"/> and
//* <MethodRef name="getOutput"/>
//* can be used to retrieve the streams associated with the socket.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Represents the TOS object that embodies a socket.
 *
 **************************************************************************/

public class SPlainSocketBase
    extends SSocketBase {





    private static final String     CLASS_NAME   = "TSocketBase";
    private static final SObjSymbol CLASS_NAME_S =
	SObjSymbol.addSymbol(CLASS_NAME);





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SPlainSocketBase(STosClass myClass)
	throws STeaException {

	super(myClass);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object constructor(SObjFunction obj,
			      SContext     context,
			      Object[]     args)
	throws STeaException {

	initialize(context);

	setSocketFactory(SPlainSocketFactory.SELF);

	return obj;
    }





//* 
//* <TeaMethod name="connect"
//*            arguments="host port"
//* 	       className="TSocketBase">
//* 
//* <Overview>
//* Connects the socket to a remote server.
//* </Overview>
//*
//* <Parameter name="host">
//* A string representing the name of the remote server to connect to.
//* </Parameter>
//*
//* <Parameter name="port">
//* An integer representing the port number on the remote server to connect
//* to.
//* </Parameter>
//*
//* <Returns>
//* A reference to the object it was called for.
//* </Returns>
//* 
//* <Description>
//* If the connection can not be successfully established then a runtime
//* error will occur.
//* </Description>
//* 
//* </TeaMethod>
//* 





//* 
//* <TeaMethod name="close"
//* 	       className="TSocketBase">
//* 
//* <Overview>
//* Closes the socket.
//* </Overview>
//*
//* <Returns>
//* A reference to the object it was called for.
//* </Returns>
//* 
//* <Description>
//* Closing the socket automatically closes the input and output streams
//* that were associated with it.
//* </Description>
//* 
//* </TeaMethod>
//* 





//* 
//* <TeaMethod name="getInput"
//* 	       className="TSocketBase">
//* 
//* <Overview>
//* Fetches the input stream associated with the socket.
//* </Overview>
//*
//* <Returns>
//* A reference to a <Func name="TInput"/> instance.
//* </Returns>
//* 
//* <Description>
//* If the socket has not yet been opened then the <Func name="TInput"/>
//* stream that is returned will be closed and can not be used. As soon
//* as the socket is successfully opened the <Func name="TInput"/> will
//* also be automatically opened and associated with the socket input
//* stream.
//* </Description>
//* 
//* </TeaMethod>
//* 





//* 
//* <TeaMethod name="getOutput"
//* 	       className="TSocketBase">
//* 
//* <Overview>
//* Fetches the output stream associated with the socket.
//* </Overview>
//*
//* <Returns>
//* A reference to a <Func name="TOutput"/> instance.
//* </Returns>
//* 
//* <Description>
//* If the socket has not yet been opened then the <Func name="TOutput"/>
//* stream that is returned will be closed and can not be used. As soon
//* as the socket is successfully opened the <Func name="TOutput"/> will
//* also be automatically opened and associated with the socket output
//* stream.
//* </Description>
//* 
//* </TeaMethod>
//* 





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static SPlainSocketBase newInstance(SContext context)
	throws STeaException {

	SPlainSocketBase socket = null;

	try {
	    socket = (SPlainSocketBase)STosUtil.newInstance(CLASS_NAME_S, context);
	} catch (ClassCastException e) {
	    String   msg     = "invalid \"{0}\" class";
	    Object[] fmtArgs = { CLASS_NAME };
	    throw new SRuntimeException(msg, fmtArgs);
	}

	return socket;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

