/**************************************************************************
 *
 * Copyright (c) 2002-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.net;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaSymbol;





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
    private static final TeaSymbol CLASS_NAME_S =
        TeaSymbol.addSymbol(CLASS_NAME);





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SSslSocketBase(final STosClass myClass)
        throws TeaException {

        super(myClass);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object constructor(final TeaFunction obj,
                              final TeaContext     context,
                              final Object[]    args)
        throws TeaException {

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

