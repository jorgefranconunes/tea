/**************************************************************************
 *
 * Copyright (c) 2001, 2002, 2003 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SModuleTdbc.java,v 1.13 2004/04/02 19:39:06 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2003/05/21 Added the functions "tdbc-get-open-connections-count",
 * "tdbc-close-all-connections". (jfn)
 *
 * 2002/01/20 Calls to the "addJavaFunction()" method were replaced by
 * inner classes for performance. (jfn)
 *
 * 2002/01/10 This classe now derives from SModuleCore. (jfn)
 *
 * 2001/05/12 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tdbc;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.modules.tdbc.SConnectionClass;
import com.pdmfc.tea.modules.tos.SJavaClass;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.STeaRuntime;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypes;





//*
//* <TeaModule name="tea.tdbc">
//* 
//* <Overview>
//* Tea database connectivity.
//* </Overview>
//*
//* <Description>
//* Tea database connectivity.
//* </Description>
//*
//* </TeaModule>
//*

/**************************************************************************
 *
 * Package of commands for relational databases access.
 *
 **************************************************************************/

public class SModuleTdbc
    extends SModule {





   private SConnectionClass _connClass = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SModuleTdbc() {
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void init(STeaRuntime context)
	throws STeaException {

	super.init(context);

	STosClass rSetClass =
	    new SJavaClass("com.pdmfc.tea.modules.tdbc.SResultSet");
	STosClass statClass =
	    new SJavaClass("com.pdmfc.tea.modules.tdbc.SStatement");
	STosClass prepStatC =
	    new SJavaClass("com.pdmfc.tea.modules.tdbc.SPreparedStatement");
	STosClass callStatC =
	    new SJavaClass("com.pdmfc.tea.modules.tdbc.SCallableStatement");

	_connClass = new SConnectionClass();

	context.newVar(rSetClass.getName(), rSetClass);
	context.newVar(statClass.getName(), statClass);
	context.newVar(prepStatC.getName(), prepStatC);
	context.newVar(callStatC.getName(), callStatC);
	context.newVar(_connClass.getName(), _connClass);

	context.addFunction("tdbc-register-driver",
			    new SObjFunction() {
				public Object exec(SObjFunction func,
						   SContext     context,
						   Object[]     args)
				    throws STeaException {
				    return functionRegisterDriver(func,
								  context,
								  args);
				}
			    });

	context.addFunction("sql-encode",
			    new SObjFunction() {
				public Object exec(SObjFunction func,
						   SContext     context,
						   Object[]     args)
				    throws STeaException {
				    return functionSqlEncode(func,
							     context,
							     args);
				}
			    });

	context.addFunction("tdbc-get-open-connections-count",
			    new SObjFunction() {
				public Object exec(SObjFunction func,
						   SContext     context,
						   Object[]     args)
				    throws STeaException {
				    return functionGetOpenConnCount(func,
								    context,
								    args);
				}
			    });

	context.addFunction("tdbc-close-all-connections",
			    new SObjFunction() {
				public Object exec(SObjFunction func,
						   SContext     context,
						   Object[]     args)
				    throws STeaException {
				    return functionCloseAllConn(func,
								context,
								args);
				}
			    });
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void stop() {

	if ( _connClass != null ) {
	    _connClass.closeAll();
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void end() {

	stop();
    }





//* 
//* <TeaFunction name="tdbc-register-driver"
//* 		 arguments="javaClassName"
//*              module="tea.tdbc">
//*
//* <Overview>
//* Dynamically loads a new JDBC driver.
//* </Overview>
//*
//* <Parameter name="javaClassName">
//* String identifying a Java class.
//* </Parameter>
//*
//* <Description>
//* Loads a Java class that implements a JDBC driver.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionRegisterDriver(SObjFunction func,
						 SContext     context,
						 Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args[0], "className");
	}

	String className = STypes.getString(args,1);

	try {
	    Class.forName(className);
	} catch (ClassNotFoundException e) {
	    throw new SRuntimeException(args[0],
					"could not load class '" + 
					className + "'");
	}

	return SObjNull.NULL;
    }





//* 
//* <TeaFunction name="sql-encode"
//* 		arguments="aString"
//*             module="tea.tdbc">
//*
//* <Overview>
//* Encodes a string so it can be used inside a string element in
//* a SQL statement.
//* </Overview>
//*
//* <Parameter name="aString">
//* The string to be encoded.
//* </Parameter>
//* 
//* <Returns>
//* A new string resulting from encoding <Arg name ="aString"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionSqlEncode(SObjFunction func,
					    SContext     context,
					    Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args[0], "string");
	}

	String str    = STypes.getString(args, 1);
	String result = sqlEncode(str);

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static String sqlEncode(String s) {

	StringBuffer buf = new StringBuffer();

	int size = s.length();

	for ( int i=0; i<size; i++ ) {
	    char c = s.charAt(i);

	    switch ( c ) {
	    case '\'' : buf.append("''"); break;
	    case 180  : buf.append("''"); break;
// 	    case '%'  : buf.append("%%"); break;
// 	    case '_'  : buf.append("\\_"); break;
	    default   : buf.append(c); break;
	    }
	}

	return buf.toString();
    }





//* 
//* <TeaFunction name="tdbc-get-open-connections-count"
//*              module="tea.tdbc">
//*
//* <Overview>
//* Retrieves the number of connections currently open.
//* </Overview>
//* 
//* <Returns>
//* An integer corresponding to the number of currently open connections.
//* </Returns>
//*
//* <Description>
//* Open connections are <ClassRef name="TConnection"/> objects which
//* have not yet been closed.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the "tdbc-get-open-connections-count" Tea function.
 *
 **************************************************************************/

    private Object functionGetOpenConnCount(SObjFunction func,
					    SContext     context,
					    Object[]     args)
	throws STeaException {

	int     count  = _connClass.getOpenConnectionsCount();
	Integer result = new Integer(count);

	return result;
    }





//* 
//* <TeaFunction name="tdbc-close-all-connections"
//*              module="tea.tdbc">
//*
//* <Overview>
//* Closes all the currently open connections.
//* </Overview>
//* 
//* <Returns>
//* An integer corresponding to the number of currently open connections.
//* </Returns>
//*
//* <Description>
//*  Open connections are <ClassRef name="TConnection"/> objects which
//* have not yet been closed.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the "tdbc-close-all-connections" Tea functions.
 *
 **************************************************************************/

    private Object functionCloseAllConn(SObjFunction func,
					SContext     context,
					Object[]     args)
	throws STeaException {

	_connClass.closeAll();

	return SObjNull.NULL;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

