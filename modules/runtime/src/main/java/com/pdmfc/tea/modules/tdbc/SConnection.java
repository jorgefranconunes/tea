/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.tdbc.SClosedEventListener;
import com.pdmfc.tea.modules.tdbc.SCallableStatement;
import com.pdmfc.tea.modules.tdbc.SPreparedStatement;
import com.pdmfc.tea.modules.tdbc.SStatement;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.Args;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaSymbol;
import com.pdmfc.tea.TeaNumArgException;
import com.pdmfc.tea.TeaRunException;





/**************************************************************************
 *
 * Implements an TOS object that acts like a
 * <code>java.sql.Connection</code>.
 *
 **************************************************************************/

public final class SConnection
    extends STosObj {





    static final String     CLASS_NAME   = "TConnection";
    static final TeaSymbol CLASS_NAME_S =
        TeaSymbol.addSymbol(CLASS_NAME);

    private Connection _connection = null;

    // Signals if the java.sql.Connection was created by itself. When
    // that is the case the java.sql.Connection will be closed when
    // the close() method is invoked.
    private boolean _ownsConnection = false;

    // The SStatement objects opened by this SConnection.
    private Set<SStatement> _statements = new HashSet<SStatement>();

    // The SClosedEventListener registered with this SConnection.
    private Set<SClosedEventListener> _listeners  =
        new HashSet<SClosedEventListener>();
    




/**************************************************************************
 *
 * Initializes the object internal state.
 *
 * @param myClass The TOS class to assign to the TOS objects.
 *
 **************************************************************************/

    public SConnection(final STosClass myClass)
        throws TeaException {

        super(myClass);
    }
    




/**************************************************************************
 *
 * Fetches the <code>java.sql.Connection</code> used to interact with
 * the database.
 *
 * @return The <code>java.sql.Connection</code> being used.
 *
 **************************************************************************/

    public Connection getInternalConnection() {

        return _connection;
    }





/**************************************************************************
 *
 * Retrieves the recomended name for the TOS class.
 *
 * @return The name for the TOS class.
 *
 **************************************************************************/

    public static String getTosClassName() {

        return CLASS_NAME;
    }





/**************************************************************************
 *
 * Registers a listener that is notified when this database connection
 * is closed.
 *
 * @param listener The listener to notify at close time.
 *
 **************************************************************************/

    public void addClosedListener(final SClosedEventListener listener) {

        _listeners.add(listener);
    }





//* 
//* <TeaMethod name="constructor"
//*            arguments="[url [username password]]"
//*                className="TConnection">
//* 
//* <Overview>
//* Initializes the object and, optionally, opens a connection to a
//* database.
//* </Overview>
//*
//* <Parameter name="url">
//* String identifying the database to connect to.
//* </Parameter>
//*
//* <Parameter name="username">
//* String identifying a user connecting to the database.
//* </Parameter>
//*
//* <Parameter name="password">
//* String representing a password associated with <Arg name="username"/>.
//* </Parameter>
//* 
//* <Description>
//* When the constructor receives arguments a call to the
//* <MethodRef name="connect"/> is implicitly performed, thus
//* establishing the connection to the database.
//* <P>
//* When a <Func name="TConnection"/> is instantiated a transaction is
//* also initiated.
//* </P>
//* <P>
//* The <Arg name="url"/> identifying the database is dependent on the
//* type of database. You must consult the documentation for the JDBC
//* driver being used in order to specify a meaningful URL.
//* </P>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "constructor" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object constructor(final TeaFunction obj,
                              final TeaContext     context,
                              final Object[]    args)
        throws TeaException {

        switch ( args.length ) {
        case 2 :
            return obj;
        case 3 :
        case 5 :
            return connect(obj, context, args);
        default :
            throw new TeaNumArgException(args, "[url [username password]]");
        }
    }





//* 
//* <TeaMethod name="connect"
//*            arguments="url [username password]"
//*                className="TConnection">
//* 
//* <Overview>
//* Opens a connection to a database.
//* </Overview>
//*
//* <Parameter name="url">
//* String identifying the database to connect to.
//* </Parameter>
//*
//* <Parameter name="username">
//* String identifying a user connecting to the database.
//* </Parameter>
//*
//* <Parameter name="password">
//* String representing a password associated with <Arg name="username"/>.
//* </Parameter>
//* 
//* <Description>
//* If a connection was previsouly opened then it is automatically
//* closed before atempting to establish the new connection.
//* <P>
//* When a connection to the database is successfully established
//* a database transaction is also implicitly initiated.
//* </P>
//* <P>
//* The <Arg name="url"/> identifying the database is dependent on the
//* type of database. You must consult the documentation for the JDBC
//* driver being used in order to specify a meaningful URL.
//* </P>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "connect" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object connect(final TeaFunction obj,
                          final TeaContext     context,
                          final Object[]    args)
        throws TeaException {

        if ( (args.length!=3) && (args.length!=5) ) {
            throw new TeaNumArgException(args, "url [username password]");
        }

        String url    = Args.getString(args,2);
        String name   = (args.length==3) ? null : Args.getString(args,3);
        String passwd = (args.length==3) ? null : Args.getString(args,4);

        try {
            connect(url, name, passwd);
        } catch ( SQLException e ) {
            // This connection is now as good as closed.
            fireClosedEvent();
            throw new TeaRunException(e);
        }

        return obj;
    }





/**************************************************************************
 *
 * Connects to the specified database. If the connection was
 * previously opened then it is closed before trying to open the new
 * one.
 *
 * @param url The connection url.
 *
 * @param username The database user that is trying to connect.
 *
 * @param password The password of the database user trying to
 * connect.
 *
 * @exception SQLException Thrown if there were any problems
 * connecting to the databae.
 *
 **************************************************************************/

    private void connect(final String url,
                         final String username,
                         final String password)
        throws SQLException {
        
        try {
            close();
        } catch ( SQLException e ) {
            // Never mind...
        }
        
        if ( (username!=null) && (password!=null) ) {
            _connection = DriverManager.getConnection(url, username, password);
        } else {
            _connection = DriverManager.getConnection(url);
        }
        _ownsConnection = true;
        _connection.setAutoCommit(false);
   }





/**************************************************************************
 *
 * Connects to the specified database. If the connection was
 * previously opened then it is closed before trying to open the new
 * one.
 *
 * @param url The connection url.
 *
 * @param username The database user that is trying to connect.
 *
 * @param password The password of the database user trying to
 * connect.
 *
 * @exception com.pdmfc.tea.TeaException Thrown if there were any
 * problems connectiong to the databae.
 *
 **************************************************************************/

    private void connect(final Connection conn) {

        _connection     = conn;
        _ownsConnection = false;
   }





//* 
//* <TeaMethod name="statement"
//*            className="TConnection">
//* 
//* <Overview>
//* Creates a <ClassRef name="TStatement"/> object in order to execute
//* SQL statements.
//* </Overview>
//*
//* <Returns>
//* A reference to the newly created <ClassRef name="TStatement"/> object.
//* </Returns>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "statement" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object statement(final TeaFunction obj,
                            final TeaContext     context,
                            final Object[]    args)
        throws TeaException {

        checkConnection();

        Statement  stat    = null;
        SStatement tosStat = null;

        try {
            stat = _connection.createStatement();
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }
        tosStat = SStatement.newInstance(context);
        tosStat.setStatement(stat);
        tosStat.addClosedListener(new SClosedEventListener() {
                public void closedEvent(Object closedObject) {
                    myClosedEvent((SStatement)closedObject);
                }
            });

        _statements.add(tosStat);
        
        return tosStat;
    }





//* 
//* <TeaMethod name="prepare"
//*            arguments="sqlStatement"
//*            className="TConnection">
//* 
//* <Overview>
//* Creates a pre-compiled statement object.
//* </Overview>
//*
//* <Returns>
//* A reference to a new <ClassRef name="TPreparedStatement"/>
//* representing the precompiled <Arg name="sqlStatement"/>.
//* </Returns>
//* 
//* <Description>
//* Creates an instance of a <ClassRef name="TPreparedStatement"/>
//* representing
//* a pre-compiled SQL statement whose contents are given by the
//* <Arg name="sqlStatement"/> argument.
//*
//* <P>
//* You create a <ClassRef name="TPreparedStatement"/> when you
//* intend to execute multiple SQL statements that differ only
//* in the values of certain parameters. Using a 
//* <ClassRef name="TPreparedStatement"/> in situations as this
//* may bring noticeable performance improvements.
//* </P>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "prepare" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object prepare(final TeaFunction obj,
                          final TeaContext     context,
                          final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "sql-statement");
        }

        String sql    = Args.getString(args, 2);
        Object result = null;

        try {
            result = prepareStatement(context, sql);
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        return result;
    }





/**************************************************************************
 *
 * Creates a new instance of an <code>SPreparedStatement</code>.
 *
 * @return A newly created <code>SPreparedStatement</code> object.
 *
 * @exception TeaException Thrown if there were any problems
 * obtaining a new statement or if there is no connection yet.
 *
 **************************************************************************/

    private SPreparedStatement prepareStatement(final TeaContext context,
                                                final String   sql)
        throws TeaException,
               SQLException {

        checkConnection();

        PreparedStatement  prepStat = _connection.prepareStatement(sql);
        SPreparedStatement stat     =
            (SPreparedStatement)SPreparedStatement.newInstance(context);

        stat.setPreparedStatement(prepStat);
        stat.addClosedListener(new SClosedEventListener() {
                public void closedEvent(Object closedObject) {
                    myClosedEvent((SPreparedStatement)closedObject);
                }
            });

        _statements.add(stat);

        return stat;
    }





//* 
//* <TeaMethod name="prepareCall"
//*            arguments="sqlStatement"
//*            className="TConnection">
//* 
//* <Overview>
//* Creates a pre-compiled statement object to execute stored procedures.
//* </Overview>
//*
//* <Returns>
//* A reference to a new <ClassRef name="TPreparedStatement"/> representing
//* the precompiled <Arg name="sqlStatement"/>.
//* </Returns>
//* 
//* <Description>
//* Creates an instance of a <ClassRef name="TCallableStatement"/>
//* representing
//* a pre-compiled SQL statement whose contents are given by the
//* <Arg name="sqlStatement"/> argument. You use a
//* <ClassRef name="TCallableStatement"/> when executing stored
//* procedures in the database server.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "prepareCall" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object prepareCall(final TeaFunction obj,
                              final TeaContext     context,
                              final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "sql-statement");
        }

        String sql    = Args.getString(args, 2);
        Object result = prepareCall(context, sql);

        return result;
    }





/**************************************************************************
 *
 * Creates a new instance of an <code>SCallableStatement</code>.
 *
 * @return A newly created <code>{@link SCalableStatement}</code>
 * object.
 *
 * @exception TeaException Thrown if there were any problems
 * obtaining a new statemente or if there is no connection yet.
 *
 **************************************************************************/

    private SCallableStatement prepareCall(final TeaContext context,
                                           final String   sql)
        throws TeaException {

        checkConnection();

        CallableStatement  clbStat = null;
        SCallableStatement stat    = null;

        try {
            clbStat = _connection.prepareCall(sql);
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }
        stat = (SCallableStatement)SCallableStatement.newInstance(context);
        stat.setCallableStatement(clbStat);
        stat.addClosedListener(new SClosedEventListener() {
                public void closedEvent(Object closedObject) {
                    myClosedEvent((SCallableStatement)closedObject);
                }
            });

        _statements.add(stat);
        
        return stat;
    }





//* 
//* <TeaMethod name="commit"
//*            className="TConnection">
//* 
//* <Overview>
//* Commits all the changes made to the database since the start of
//* the transaction.
//* </Overview>
//*
//* <Returns>
//* A reference to the <Class name="TConnection"/> for which this method
//* was called.
//* </Returns>
//* 
//* <Description>
//* The database transaction represented by this 
//* <Class name="TConnection"/> was implicitly started when this
//* <Class name="TConnection"/> was created or at the last call
//* to the <MethodRef tosClass="TConnection" name="commit"/> or
//* <MethodRef tosClass="TConnection" name="rollback"/> methods.
//* A new transaction is implicitly started when this method
//* terminates successfully.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "commit" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object commit(final TeaFunction obj,
                         final TeaContext     context,
                         final Object[]    args)
        throws TeaException {

        checkConnection();

        try {
            _connection.commit();
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        return obj;
    }





//* 
//* <TeaMethod name="autocommit"
//*            arguments="flag"
//*                className="TConnection">
//* 
//* <Parameter name="flag">
//* </Parameter>
//* 
//* <Overview>
//* Sets the autocommit mode for this <Class name="TConnection"/>
//* </Overview>
//*
//* <Returns>
//* A reference to the object for which this method was called.
//* </Returns>
//* 
//* <Description>
//* If the <Arg name="flag"/> argument is true then every change in the
//* database made through a <ClassRef name="TStatement"/> created
//* by this <Class name="TConnection"/> will be implicitly followed
//* by a <MethodRef tosClass="TConnection" name="commit"/>. In this
//* mode it is not possible to to a rollback.
//*
//* <P>
//* If the <Arg name="flag"/> argument is false then changes to
//* the database made through a <ClassRef name="TStatement"/> created
//* by this <Class name="TConnection"/> will require a call to
//* <MethodRef tosClass="TConnection" name="commit"/> to become
//* permanent.
//* </P>
//*
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "autocommit" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object autocommit(final TeaFunction obj,
                             final TeaContext     context,
                             final Object[]    args)
        throws TeaRunException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "autocommit-flag");
        }

        boolean flag = Args.getBoolean(args, 2).booleanValue();

        try {
            autocommit(flag);
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        return obj;
    }





/**************************************************************************
 *
 * 
 *
 * @exception com.pdmfc.tea.TeaRunException Thrown if there
 * were any problems.
 *
 * @exception java.sql.SQLException
 *
 **************************************************************************/

    private void autocommit(final boolean flag)
        throws TeaRunException,
               SQLException {

        checkConnection();

        _connection.setAutoCommit(flag);
    }





//* 
//* <TeaMethod name="rollback"
//*            className="TConnection">
//* 
//* <Overview>
//* Undoes all the changes made to the database since the start of
//* the transaction.
//* </Overview>
//*
//* <Returns>
//* A reference to the <Func name="TConnection"/> for which this method
//* was called.
//* </Returns>
//* 
//* <Description>
//* The database transaction represented by this 
//* <Class name="TConnection"/> was implicitly started when this
//* <Class name="TConnection"/> was created or at the last call
//* to the <MethodRef tosClass="TConnection" name="commit"/> or
//* <MethodRef tosClass="TConnection" name="rollback"/> methods.
//* A new transaction is implicitly started when this method
//* terminates successfully.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "rollback" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object rollback(final TeaFunction obj,
                           final TeaContext     context,
                           final Object[]    args)
        throws TeaRunException {

        checkConnection();

        try {
            _connection.rollback();
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        return obj;
   }





//* 
//* <TeaMethod name="close"
//*            className="TConnection">
//* 
//* <Overview>
//* Closes the connection to the database.
//* </Overview>
//* 
//* <Description>
//* Closes the connection to the database. The object can then no longer
//* be used. All the changes made to the database since the start of the
//* current transaction are rolled back. All the
//* <ClassRef name="TStatement"/>,
//* <ClassRef name="TPreparedStatement"/>,
//* <ClassRef name="TCallableStatement"/>
//* objects created by this <Class name="TConnection"/> that are still
//* open are automatically closed.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "close" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object close(final TeaFunction obj,
                        final TeaContext     context,
                        final Object[]    args)
        throws TeaException {

        try {
            close();
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        return obj;
    }





/**************************************************************************
 *
 * Closes the current connection and all its related statements. It
 * has no efect if the connectiont has already been closed.
 *
 * @exception SQLException Thrown if there were problems closing the
 * underlying java.sql.Connection.
 *
 **************************************************************************/

    void close()
        throws SQLException {

        if ( _connection == null ) {
            // Database connection is not open. Just bail out.
            return;
        }

        // We make a copy of the statements list because as we close
        // them we get notified of the close event and myCloseEvent
        // will be called to remove the statement from the _statements
        // set.
        Set<SStatement> myStatements = new HashSet<SStatement>(_statements);

        for ( SStatement stat : myStatements ) {
            try {
                stat.close();
            } catch ( SQLException e ) {
                // There should be a way to log the error message
                // somewhere.
            }
        }

        try {
            _connection.rollback();
        } catch ( SQLException e ) {
            // There should be a way to log the error message
            // somewhere.
        }
        if ( _ownsConnection ) {
            _connection.close();
        }
        fireClosedEvent();
        _connection     = null;
        _ownsConnection = false;
        _listeners.clear();
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void fireClosedEvent() {

        for ( SClosedEventListener listener : _listeners ) {
            listener.closedEvent(this);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void myClosedEvent(final SStatement closedObject) {

        _statements.remove(closedObject);
    }





/**************************************************************************
 *
 * Checks if the database connection is currently opened. If that is
 * not the case then an exception is thrown.
 *
 * @exception TeaRunException Thrown if the database connection is
 * not currently opened.
 *
 **************************************************************************/

    private void checkConnection()
        throws TeaRunException {

        if ( _connection == null ) {
            throw new TeaRunException("connection is closed");
        }
    }





/**************************************************************************
 *
 * Creates a TOS <code>TConnection</code> instance and supplies an
 * external <code>java.sql.Connection</code> to associate with. This
 * <code>java.sql.Connection</code> is not closed when the
 * <code>{@link #close(TeaFunction,TeaContext,Object[])}</code> method
 * (i.e. the <code>close</code> TOS method) is called. It is the
 * responsability of the caller to eventually close the supplied
 * <code>java.sql.Connection</code>.
 *
 * <p>The caller may be notified of when the <code>TConnection</code>
 * is closed by registering an apropriate <code>{@link
 * SClosedEventListener}</code> through the <code>{@link
 * #addClosedListener(SClosedEventListener)}</code> method. When the
 * <code>TConnection</code> gets closed the <code>{@link
 * SClosedEventListener#closedEvent(Object)}</code> is called having
 * as argument the apropriate <code>SConnection</code>.</p>
 *
 * @param context The context where the Tea <code>TConnection</code>
 * object is being created.
 *
 * @param connection The <code>java.sqlConnection</code> to associate
 * with the <code>TConnection</code> being created.
 *
 * @return A newly created and initialized <code>SConnection</code>
 * instance.
 *
 **************************************************************************/

    public static SConnection newInstance(final TeaContext  context,
                                          final Connection connection)
        throws TeaException {

        Object[] ctorArgs = { null, null };
        STosObj  tosConn  =
            STosUtil.newInstance(CLASS_NAME_S, context, ctorArgs);

        if ( !(tosConn instanceof SConnection) ) {
            String   msg     = "Invalid class \"{0}\"";
            Object[] fmtArgs = { CLASS_NAME_S };
            throw new TeaException(msg, fmtArgs);
        }

        SConnection result = (SConnection)tosConn;

        result.connect(connection);

        return result;
    }



}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

