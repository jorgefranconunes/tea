/**************************************************************************
 *
 * Copyright (c) 2001-2012 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tdbc;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.modules.tdbc.SConnectionClass;
import com.pdmfc.tea.modules.tos.SJavaClass;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.runtime.SArgs;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.TeaFunction;





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

public final class SModuleTdbc
    extends Object
    implements SModule {





   private SConnectionClass _connClass = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SModuleTdbc() {

       // Nothing to do.
   }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void init(final SContext context)
        throws STeaException {

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
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("tdbc-register-driver")
    public static Object functionRegisterDriver(final SObjFunction func,
                                                final SContext     context,
                                                final Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "className");
        }

        String className = SArgs.getString(args,1);

        try {
            Class.forName(className);
        } catch (ClassNotFoundException e) {
            String msg = "could not load class \"{0}\"";
            throw new SRuntimeException(args, msg, className);
        }

        return SObjNull.NULL;
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
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("sql-encode")
    public static Object functionSqlEncode(final SObjFunction func,
                                           final SContext     context,
                                           final Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "string");
        }

        String str    = SArgs.getString(args, 1);
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
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("tdbc-get-open-connections-count")
    public Object functionGetOpenConnCount(final SObjFunction func,
                                           final SContext     context,
                                           final Object[]     args)
        throws STeaException {

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
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunction("tdbc-close-all-connections")
    public Object functionCloseAllConn(final SObjFunction func,
                                       final SContext     context,
                                       final Object[]     args)
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

