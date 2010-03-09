/**************************************************************************
 *
 * Copyright (c) 2002 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SSslSocketBase.java,v 1.4 2006/10/11 13:35:58 jpsl Exp $
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
import com.pdmfc.tea.modules.io.SIOException;
import com.pdmfc.tea.modules.io.SInput;
import com.pdmfc.tea.modules.io.SOutput;
import com.pdmfc.tea.modules.net.SPlainSocketFactory;
import com.pdmfc.tea.modules.net.SSocketFactory;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.STypes;





//* 
//* <TeaClass name="TSslSocketBase"
//*           baseClass="TSocketBase"
//*           module="tea.net">
//*
//* <Overview>
//* Client SSL socket.
//* </Overview>
//*
//* <Description>
//* Instances of <Class name="TSslSocketBase"/> are used as client sockets
//* to connect to remote servers using SSL. After the connection is
//* established the methods <MethodRef name="getInput"/> and
//* <MethodRef name="getOutput"/>
//* can be used to retrieve the streams associated with the socket.
//* </Description>
//*
//* <Since version="3.1.2"/>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Represents the TOS object that embodies a socket.
 *
 **************************************************************************/

public class SSslSocketBase
    extends SSocketBase {





    private static final String     CLASS_NAME   = "TSslSocketBase";
    private static final SObjSymbol CLASS_NAME_S =
	SObjSymbol.addSymbol(CLASS_NAME);





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SSslSocketBase(STosClass myClass)
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

	setSocketFactory(SSslSocketFactory.SELF);

	return obj;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

