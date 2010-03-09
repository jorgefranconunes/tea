/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SConnectionClass.java,v 1.8 2003/05/21 16:56:47 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tdbc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.tdbc.SClosedEventListener;
import com.pdmfc.tea.modules.tdbc.SConnection;
import com.pdmfc.tea.modules.tos.SJavaMethod;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;





//* 
//* <TeaClass name="TConnection"
//*           module="tea.tdbc">
//*
//* <Overview>
//* A connection to a relational database.
//* </Overview>
//*
//* <Description>
//* Represents a connection to a relational
//* database. It is also used to maintain a transaction context.
//* <P>
//* Every <Class name="TConnection"/> starts its life with autocommit mode
//* turned on. See <MethodRef tosClass="TConnection" name="autocommit"/>
//* for a more detailed explanation.
//* </P>
//* <P>
//* Before creating any instances of this class at least one JDBC driver
//* must have been previously successfully registered by a call to
//* <FuncRef name="tdbc-register-driver"/>.
//* </P>
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Represents the TOS <code>TConnection</code> class.
 *
 * <P>A list of all the created connections is mantained
 * internally. All the connections can be closed with a call to the
 * <TT>closeAll()</TT> method.
 *
 **************************************************************************/

class SConnectionClass
    extends STosClass {





    private static final String CLASS_NAME = "TConnection";

    private List _connections = new ArrayList();





/**************************************************************************
 *
 * Initializes the object internal state.
 *
 * @param statamentClass The object that represents the TOS class
 * associated with the statements.
 *
 * @exception SNoSuchClassException Never thrown.
 *
 **************************************************************************/

    public SConnectionClass() {

	setName(SConnection.CLASS_NAME);

	addConstructor(new SJavaMethod(SConnection.class, "constructor"));
	addMethod("connect",   
		  new SJavaMethod(SConnection.class, "connect"));
	addMethod("statement",   
		  new SJavaMethod(SConnection.class, "statement"));
	addMethod("autocommit",  
		  new SJavaMethod(SConnection.class, "autocommit"));
	addMethod("commit",      
		  new SJavaMethod(SConnection.class, "commit"));
	addMethod("rollback",    
		  new SJavaMethod(SConnection.class, "rollback"));
	addMethod("prepare",     
		  new SJavaMethod(SConnection.class, "prepare"));
	addMethod("prepareCall", 
		  new SJavaMethod(SConnection.class, "prepareCall"));
	addMethod("close",       
		  new SJavaMethod(SConnection.class, "close"));
    }





/**************************************************************************
 *
 * Creates a new instance of an <TT>SConnection</TT>.
 *
 * @return A newly created <TT>SConnection</TT> object, but not
 * initialized.
 *
 **************************************************************************/

    public STosObj newInstance()
	throws STeaException {

	SConnection connection = new SConnection(this);

	_connections.add(connection);
	connection.addClosedListener(new SClosedEventListener() {
		public void closedEvent(Object closedObject) {
		    myClosedEvent(closedObject);
		}
	    });
	
	return connection;
    }





/**************************************************************************
 *
 * Closes all the connections created so far.
 *
 **************************************************************************/

    public void closeAll() {

	Object[] conns = _connections.toArray();

	for ( int i=0, count=conns.length;i <count; i++ ) {
	    SConnection conn = (SConnection)conns[i];

	    try {
		conn.close();
	    } catch (SQLException e) {
		// There should be a way to log the error message
		// somewhere.
	    }
	}
    }





/**************************************************************************
 *
 * Retrieves the number of connections currently opened. An open
 * connection is a <code>SConnection</code> instance which has not yet
 * been closed.
 *
 **************************************************************************/

    public int getOpenConnectionsCount() {

	int result = _connections.size();

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void myClosedEvent(Object closedObject) {

	_connections.remove(closedObject);
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

