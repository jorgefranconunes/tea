/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tdbc;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.tdbc.SConnectionClass;
import com.pdmfc.tea.modules.tos.SJavaClass;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.Args;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaNumArgException;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaNull;
import com.pdmfc.tea.TeaRunException;
import com.pdmfc.tea.TeaFunctionImplementor;
import com.pdmfc.tea.TeaEnvironment;
import com.pdmfc.tea.TeaModule;





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

public final class ModuleTdbc
    extends Object
    implements TeaModule {





   private SConnectionClass _connClass = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public ModuleTdbc() {

       // Nothing to do.
   }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void init(final TeaEnvironment environment)
        throws TeaException {

        STosClass rSetClass =
            new SJavaClass("com.pdmfc.tea.modules.tdbc.SResultSet");
        STosClass statClass =
            new SJavaClass("com.pdmfc.tea.modules.tdbc.SStatement");
        STosClass prepStatC =
            new SJavaClass("com.pdmfc.tea.modules.tdbc.SPreparedStatement");
        STosClass callStatC =
            new SJavaClass("com.pdmfc.tea.modules.tdbc.SCallableStatement");

        _connClass = new SConnectionClass();

        environment.addGlobalVar(rSetClass.getName(), rSetClass);
        environment.addGlobalVar(statClass.getName(), statClass);
        environment.addGlobalVar(prepStatC.getName(), prepStatC);
        environment.addGlobalVar(callStatC.getName(), callStatC);
        environment.addGlobalVar(_connClass.getName(), _connClass);

        // The other functions provided by this module are implemented
        // as methods of this class with the TeaFunction annotation.
   }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void end() {

        stop();
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void start() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void stop() {

        if ( _connClass != null ) {
            _connClass.closeAll();
        }
    }





//* 
//* <TeaFunction name="tdbc-register-driver"
//*              arguments="javaClassName"
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
 * Implements the Tea <code>tdbc-register-driver</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("tdbc-register-driver")
    public static Object functionRegisterDriver(final TeaFunction func,
                                                final TeaContext  context,
                                                final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "className");
        }

        String className = Args.getString(args,1);

        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            String msg = "could not load class \"{0}\"";
            throw new TeaRunException(args, msg, className);
        }

        return TeaNull.NULL;
    }





//* 
//* <TeaFunction name="sql-encode"
//*                 arguments="aString"
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
 * Implements the Tea <code>sql-encode</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("sql-encode")
    public static Object functionSqlEncode(final TeaFunction func,
                                           final TeaContext  context,
                                           final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "string");
        }

        String str    = Args.getString(args, 1);
        String result = sqlEncode(str);

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static String sqlEncode(final String s) {

        StringBuilder buf = new StringBuilder();

        int size = s.length();

        for ( int i=0; i<size; i++ ) {
            char c = s.charAt(i);

            switch ( c ) {
            case '\'' : buf.append("''"); break;
            case 180  : buf.append("''"); break;
//             case '%'  : buf.append("%%"); break;
//             case '_'  : buf.append("\\_"); break;
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
 * Implements the Tea <code>tdbc-get-open-connections-count</code>
 * function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("tdbc-get-open-connections-count")
    public Object functionGetOpenConnCount(final TeaFunction func,
                                           final TeaContext  context,
                                           final Object[]    args)
        throws TeaException {

        int     count  = _connClass.getOpenConnectionsCount();
        Integer result = Integer.valueOf(count);

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
 * Implements the Tea <code>tdbc-close-all-connections</code>
 * function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("tdbc-close-all-connections")
    public Object functionCloseAllConn(final TeaFunction func,
                                       final TeaContext  context,
                                       final Object[]    args)
        throws TeaException {

        _connClass.closeAll();

        return TeaNull.NULL;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

