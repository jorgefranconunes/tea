/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tdbc;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.tdbc.SClosedEventListener;
import com.pdmfc.tea.modules.tdbc.SResultSet;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.Args;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.TeaRunException;





//* 
//* <TeaClass name="TStatement"
//*           module="tea.tdbc">
//*
//* <Overview>
//* Used to execute SQL statements.
//* </Overview>
//*
//* <Description>
//* Used to execute SQL commands.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Implements an TOS object that acts like a java <TT>Statement</TT>.
 *
 **************************************************************************/

public class SStatement
    extends STosObj
    implements SClosedEventListener {





    private static final String     CLASS_NAME   = "TStatement";
    private static final TeaSymbol CLASS_NAME_S =
        TeaSymbol.addSymbol(CLASS_NAME);

    private   Statement        _statement   = null;
    protected List<SResultSet> _resultSets  = new ArrayList<SResultSet>();

    // Listeners for the "closedEvent".
    private List<SClosedEventListener> _listeners  =
        new ArrayList<SClosedEventListener>();





/**************************************************************************
 *
 *
 **************************************************************************/

    public SStatement(final STosClass myClass)
        throws TeaException {

        super(myClass);
    }





/**************************************************************************
 *
 * @param stat
 *    The <TT>Statement</TT> that this TOS objects acts on behalf of.
 *
 **************************************************************************/

    public final void setStatement(final Statement stat) {

        _statement = stat;
    }





/**************************************************************************
 *
 * Fetches the <code>java.sql.Statement</code> used to interact with
 * the database.
 *
 * @return The <code>java.sql.Statement</code> being used.
 *
 **************************************************************************/

    public final Statement getInternalStatement() {

        return _statement;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static String getTosClassName() {

        return CLASS_NAME;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static SStatement newInstance(final TeaContext context)
        throws TeaException {

        STosObj stat = STosUtil.newInstance(CLASS_NAME_S, context);

        if ( !(stat instanceof SStatement) ) {
            throw new TeaRunException("invalid " + CLASS_NAME + " class");
        }

        return (SStatement)stat;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public final void addClosedListener(final SClosedEventListener listener) {

        _listeners.add(listener);
    }





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

        return obj;
    }





//* 
//* <TeaMethod name="query"
//*            arguments="sqlStatement"
//*            className="TStatement">
//* 
//* <Overview>
//* Executes a SQL query.
//* </Overview>
//*
//* <Parameter name="sqlStatement">
//* A string representing a SQL statement with a SELECT command.
//* </Parameter>
//*
//* <Returns>
//* An instance of <ClassRef name="TResultSet"/> that represents the result
//* of the execution of the <Arg name="sqlStatement"/> query.
//* </Returns>
//* 
//* <Description>
//* Executes a SQL query on the database this <Class name="TStatement"/>
//* is associated with. It returns a <ClassRef name="TResultSet"/>
//* object containing the records resulting from the query. If there
//* is something wrong with the
//* <Arg name="sqlStatement"/> string (e.g. a syntax error) then an
//* runtime error ocurrs.
//* <P>
//* The instance of <ClassRef name="TResultSet"/> returned by the method
//* can be used to iterate through the records that resulted from
//* the query.
//* </P>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "query" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object query(final TeaFunction obj,
                        final TeaContext     context,
                        final Object[]    args)
        throws TeaException {

        checkStatement();

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "sqlStatement");
        }

        String     sql     = Args.getString(args, 2);
        ResultSet  rSet    = null;
        SResultSet tosRSet = null;

        try {
            rSet    = _statement.executeQuery(sql);
            tosRSet = SResultSet.newInstance(context);
            tosRSet.setResultSet(rSet);
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        tosRSet.addClosedListener(this);
        _resultSets.add(tosRSet);

        return tosRSet;
    }





//* 
//* <TeaMethod name="update"
//*            arguments="sqlStatement"
//*                className="TStatement">
//* 
//* <Overview>
//* Executes a SQL statement that modifies the contents of the database.
//* </Overview>
//*
//* <Parameter name="sqlStatement">
//* A string representing a SQL statement with an UPDATE, INSERT or
//* DELETE command.
//* </Parameter>
//*
//* <Returns>
//* An integer object representing the number of records modified in
//* the database.
//* </Returns>
//* 
//* <Description>
//* Executes a SQL statement on the database this
//* <Class name="TStatement"/>
//* is associated with. The statement is supposed to modify, insert
//* or remove records in the database. If there is something wrong with the
//* <Arg name="sqlStatement"/> string (e.g. a syntax error) or
//* if the changes would violate an integrity constraint then an
//* runtime error will ocurr.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "update" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object update(final TeaFunction obj,
                         final TeaContext     context,
                         final Object[]    args)
        throws TeaException {

        checkStatement();

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "sqlStatement");
        }

        String sql    = Args.getString(args, 2);
        int    result = 0;

        try {
            result = _statement.executeUpdate(sql);
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        return Integer.valueOf(result);
    }





//* 
//* <TeaMethod name="execute"
//*            arguments="sqlStatement"
//*                className="TStatement">
//* 
//* <Overview>
//* Executes an arbitrary SQL statement.
//* </Overview>
//*
//* <Parameter name="sqlStatement">
//* A string representing a SQL statement.
//* </Parameter>
//* 
//* <Returns>
//* True if there were one or more result sets as a result from the
//* execution of the SQL statement.
//* </Returns>
//* 
//* <Description>
//* Executes a SQL statement on the database this <Class name="TStatement"/>
//* is associated with. This method is tipically used to execute
//* stored procedures, but it can also execute any type os SQL statement.
//* If there is something wrong with the
//* <Arg name="sqlStatement"/> string (e.g. a syntax error) a
//* runtime error will ocurr.
//* 
//* <P>
//* If this method returns true then the result set can be obtained by
//* calling <MethodRef name="getResultSet"/>. Multiple result sets can be
//* obtained by calling <MethodRef name="getMoreResults"/> and 
//* <MethodRef name="getResultSet"/> in sequence as appropriate.
//* </P>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "execute" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object execute(final TeaFunction obj,
                          final TeaContext     context,
                          final Object[]    args)
        throws TeaException {

        checkStatement();

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "sqlStatement");
        }

        String  sql    = Args.getString(args, 2);
        boolean result = false;

        try {
            _statement.execute(sql);
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        return Boolean.valueOf(result);
    }





//* 
//* <TeaMethod name="getResultSet"
//*                className="TStatement">
//* 
//* <Overview>
//* Returns the current result as a <Class name="TResultSet"/>.
//* </Overview>
//* 
//* <Returns>
//* A <ClassRef name="TResultSet"/> instance.
//* </Returns>
//* 
//* <Description>
//* Fetches the current result as a <ClassRef name="TResultSet"/> object.
//* This method is to be called only once per result.
//* 
//* <P>
//* By using the <MethodRef name="execute"/> method it is possible to
//* execute SQL statement that return multipe result sets. This method
//* combined with <MethodRef name="getMoreResults"/> give access to
//* those result sets as instances of <ClasRef name="TResultSet"/>.
//* </P>
//* </Description>
//* 
//* <Since version="3.1.5"/>
//*
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * Implements the <code>getResultSet</code> method of the TOS class
 * <code>TStatement</code>.
 *
 **************************************************************************/

    public final Object getResultSet(final TeaFunction obj,
                                     final TeaContext     context,
                                     final Object[]    args)
        throws TeaException {

        checkStatement();

        ResultSet  rSet    = null;
        SResultSet tosRSet = null;

        try {
            rSet    = _statement.getResultSet();
            tosRSet = SResultSet.newInstance(context);
            tosRSet.setResultSet(rSet);
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        return tosRSet;
    }





//* 
//* <TeaMethod name="getMoreResults"
//*                className="TStatement">
//* 
//* <Overview>
//* Fetches the <Class name="TStatement"/> next result.
//* </Overview>
//* 
//* <Returns>
//* True if the next result is a result set. False otherwise.
//* </Returns>
//* 
//* <Description>
//* Fetches the statement next result. It will return true if the next
//* result is a <ClassRef name="TResultSet"/>. In that case the result
//* set can be obtained by invoking <MethodRef name="getResultSet"/>.
//* This method is to be called only once per result.
//* 
//* <P>
//* By using the <MethodRef name="execute"/> method it is possible to
//* execute SQL statement that return multipe result sets. This method
//* combined with <MethodRef name="getResultSet"/> give access to
//* those result sets as instances of <ClasRef name="TResultSet"/>.
//* </P>
//* </Description>
//* 
//* <Since version="3.1.5"/>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * Implements the <code>getMoreResults</code> method of the TOS class
 * <code>TStatement</code>.
 *
 **************************************************************************/

    public final Object getMoreResults(final TeaFunction obj,
                                       final TeaContext     context,
                                       final Object[]    args)
        throws TeaException {

        checkStatement();

        Boolean result = null;

        try {
            if ( _statement.getMoreResults() ) {
                result = Boolean.TRUE;
            } else {
                result = Boolean.FALSE;
            }
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        return result;
    }






//* 
//* <TeaMethod name="getFetchSize"
//*                className="TStatement">
//* 
//* <Overview>
//* Fetches the number of rows that are internally retrieved for a
//* <ClassRef name="TResultSet"/>.
//* </Overview>
//* 
//* <Returns>
//* An integer.
//* </Returns>
//* 
//* <Description>
//* Retrieves the number of result set rows that is the default fetch
//* size for <ClassRef name="TResultSet"/> objects generated from this
//* <ClassRef name="TStatement"/> object. If this <ClassRef
//* name="TStatement"/> object has not set a fetch size by calling the
//* method <MethodRef name="setFetchSize"/>, the return value is
//* implementation-specific.
//* </Description>
//* 
//* <Since version="3.2.1"/>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * Implements the <code>getFetchSize</code> method of the TOS class
 * <code>TStatement</code>.
 *
 **************************************************************************/

    public final Object getFetchSize(final TeaFunction obj,
                                     final TeaContext     context,
                                     final Object[]    args)
        throws TeaException {

        checkStatement();

        int result = 0;

        try {
            result = _statement.getFetchSize();
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        return Integer.valueOf(result);
    }






//* 
//* <TeaMethod name="setFetchSize"
//*            arguments="numberOfRows"
//*                className="TStatement">
//* 
//* <Overview>
//* Sets the number of rows that should be internally retrieved for a
//* <ClassRef name="TResultSet"/>.
//* </Overview>
//* 
//* <Parameter name="numberOfRows">
//* An integer.
//* </Parameter>
//* 
//* <Returns>
//* An integer.
//* </Returns>
//* 
//* <Description>
//* Gives the JDBC driver a hint as to the number of rows that should
//* be fetched from the database when more rows are needed. The number
//* of rows specified affects only result sets created using this
//* statement. If the value specified is zero, then the hint is
//* ignored. The default value is zero.
//* <P>An error will occur if this value if out of the bounds
//* 0..maxRows where maxRows is the maximum number of rows that a
//* <ClassRef name="TResultSet"/> may return.</P>
//* </Description>
//* 
//* <Since version="3.2.1"/>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * Implements the <code>setFetchSize</code> method of the TOS class
 * <code>TStatement</code>.
 *
 **************************************************************************/

    public final Object setFetchSize(final TeaFunction obj,
                                     final TeaContext     context,
                                     final Object[]    args)
        throws TeaException {

        checkStatement();


        if ( args.length != 3 ) {
            throw new SNumArgException(args, "numberOfRows");
        }

        int numberOfRows = Args.getInt(args, 2).intValue();

        try {
            _statement.setFetchSize(numberOfRows);
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        return obj;
    }





//* 
//* <TeaMethod name="close"
//*            className="TStatement">
//* 
//* <Overview>
//* Releases all the database resources held by this object.
//* </Overview>
//* 
//* <Description>
//* Releases all the database resources used by this object.
//* The object can then no longer
//* be used. All the
//* <ClassRef name="TResultSet"/> created by this object that are
//* still opened are also closed automatically.
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

    public final Object close(final TeaFunction obj,
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
 * 
 *
 **************************************************************************/

    final void close()
        throws SQLException {
        
        if ( _statement == null ) {
            return;
        }

        Object[] rSets = _resultSets.toArray();
        int      count = rSets.length;

        // Close all the still open result sets.
        for ( int i=0; i<count; i++ ) {
            SResultSet rSet = (SResultSet)rSets[i];

            try {
                rSet.close();
            } catch ( SQLException exc ) {
                // There should be a way to log the error message
                // somewhere.
            }
        }

        _statement.close();
        _statement = null;
        fireClosedEvent();
        _listeners.clear();
    }






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void fireClosedEvent() {

        for ( int i=_listeners.size(); (i--)>0; ) {
            SClosedEventListener lstnr = _listeners.get(i);

            lstnr.closedEvent(this);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public final void closedEvent(final Object closedObject) {

        if ( !(closedObject instanceof SResultSet) ) {
            String msg =
                "Expected a " + SResultSet.class
                + " and got a " + closedObject.getClass();
            throw new IllegalArgumentException(msg);
        }

        _resultSets.remove(closedObject);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void checkStatement()
        throws TeaRunException {

        if ( _statement == null ) {
            throw new TeaRunException("statement is closed");
        }
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

