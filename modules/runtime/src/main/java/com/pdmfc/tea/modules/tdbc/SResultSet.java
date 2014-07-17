/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.tdbc.SClosedEventListener;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.modules.util.SDate;
import com.pdmfc.tea.runtime.Args;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaNull;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.TeaRunException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.Types;





//* 
//* <TeaClass name="TResultSet"
//*           module="tea.tdbc">
//* 
//* <Overview>
//* Represents the set of rows from a SQL query.
//* </Overview>
//* 
//* <Description>
//* Represents the result of a query. The result is the ordered set of
//* records resulting from a SQL query.
//* Instances of <Class name="TResultSet"/> are created by a call to
//* the <MethodRef tosClass="TStatement" name="query"/> method.
//* <P>
//* A <Class name="TResultSet"/> can be thought of as a cursor that at any
//* given moment points to a record of the result. Before the first call
//* to the <MethodRef name="next"/> method the cursor points to just before
//* the first record. That means only after the first call to the
//* <MethodRef name="next"/> method the contents of the first record can be
//* retrieved.
//* </P>
//* <P>
//* When the <Class name="TResultSet"/> is positioned on a record its
//* fields can be retrieved using the <Func name="getXXX"/> methods.
//* </P>
//* </Description>
//* 
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Implements a TOS class <code>TResultSet</code>. Instances of this
 * TOS class act like a Java <code>java.sql.ResultSet</code>.
 *
 **************************************************************************/

public final class SResultSet
    extends STosObj {





    private static final String     CLASS_NAME   = "TResultSet";
    private static final TeaSymbol CLASS_NAME_S =
        TeaSymbol.addSymbol(CLASS_NAME);

    private ResultSet         _resultSet  = null;
    private ResultSetMetaData _metaData   = null;
    private boolean           _rowWasRead = false;
    private boolean           _atEnd      = false;
    private boolean           _hasRows    = false;

    // Listeners for the "closedEvent".
    private List<SClosedEventListener> _listeners  =
        new ArrayList<SClosedEventListener>();





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SResultSet(final STosClass myClass)
        throws TeaException {

        super(myClass);
   }





/**************************************************************************
 *
 * Specifies the underlying <code>java.sql.ResultSet</code> object to
 * use. The given result set should no longer be used by whoever
 * supplied it.
 *
 * @param rSet 
 *
 * @exception SQLException Thrown if there were any problems accessing
 * the given result set.
 *
 **************************************************************************/

    public void setResultSet(final ResultSet rSet)
        throws SQLException {

        _resultSet  = rSet;
        _hasRows    = rSet.next();
        _rowWasRead = _hasRows;
        _atEnd      = !_hasRows;
    }





/**************************************************************************
 *
 * Fetches the <code>java.sql.ResultSet</code> used to interact with
 * the database.
 *
 * @return The <code>java.sql.ResultSet</code> being used.
 *
 **************************************************************************/

    public ResultSet getInternalResultSet() {

        return _resultSet;
    }





/**************************************************************************
 *
 * Fetches the TOS class name implemented by this Java class.
 *
 * @return The TOS class name implemented by this Java class.
 *
 **************************************************************************/

    public static String getTosClassName() {

        return CLASS_NAME;
    }





/**************************************************************************
 *
 * Creates a new TOS instance of the <code>TResultSet</code> class.
 *
 **************************************************************************/

    public static SResultSet newInstance(final TeaContext context)
        throws TeaException {

        STosObj rSet = STosUtil.newInstance(CLASS_NAME_S, context);

        if ( !(rSet instanceof SResultSet) ) {
            throw new TeaRunException("invalid " + CLASS_NAME + " class");
        }

        return (SResultSet)rSet;
    }





/**************************************************************************
 *
 * Registers a new listener interested in being notigied when this
 * result set is closed.
 *
 * @param listener The listener that will be notified when this result
 * set is closed.
 *
 **************************************************************************/

    public void addClosedListener(final SClosedEventListener listener) {

        _listeners.add(listener);
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

        return obj;
    }





//* 
//* <TeaMethod name="hasRows"
//*                className="TResultSet">
//* 
//* <Overview>
//* Checks if this <Class name="TResultSet"/> has any records.
//* </Overview>
//*
//* <Returns>
//* True if there were any records in the result of the query.
//* False otherwise.
//* </Returns>
//* 
//* <Description>
//* This method always return the same value, either true or false,
//* for a given instance of <Func name="TResultSet"/>.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * Implements the <code>hasRows</code> method of the TOS
 * <code>TResultSet</code> class. Checks if the result
 * set as any rows. It always returns the same value.
 *
 **************************************************************************/

    public Object hasRows(final TeaFunction obj,
                          final TeaContext     context,
                          final Object[]    args)
        throws TeaRunException {

        checkResultSet();

        return _hasRows ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaMethod name="next"
//*            className="TResultSet">
//* 
//* <Overview>
//* Advances the cursor current position by one to the next record of
//* the result.
//* </Overview>
//*
//* <Returns>
//* True if the cursor was successfully advanced to the next record.
//* False if there are no more records in the result set.
//* </Returns>
//* 
//* <Description>
//* This method must be called at least once prior to retrieving any
//* record fields by calls to the <Func name="getXXX"/>
//* methods.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * Implements the <code>next</code> method of the TOS
 * <code>TResultSet</code> class. Skips to the next row of the result.
 *
 * @return Returns <TT>true</TT> if the next row was successfuly
 * fecthed.  Returns <TT>false</TT> if all the rows have already been
 * fetched.
 *
 **************************************************************************/

    public Object next(final TeaFunction obj,
                       final TeaContext     context,
                       final Object[]    args)
        throws TeaRunException {

        checkResultSet();

        boolean gotIt = false;

        // We will not call _resultSet.next() any more if it has
        // previously returned false.

        try {
            if ( _rowWasRead ) {
                gotIt = true;
            } else {
                gotIt = _atEnd ? false : _resultSet.next();
            }
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        } finally {
            _rowWasRead = false;
        }
      
        if ( !gotIt ) {
            _atEnd = true;
        }

        return gotIt ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaMethod name="hasMoreRows"
//*                className="TResultSet">
//* 
//* <Overview>
//* Checks if this <Func name="TResultSet"/> still has more records
//* to retrieve.
//* </Overview>
//*
//* <Returns>
//* True if there are more records to retrieve.
//* False otherwise.
//* </Returns>
//* 
//* <Description>
//* This method is similar to the <MethodRef name="next"/> method but it does
//* not advance the cursor. This means that if a call to
//* <Method name="hasMoreRows"/> returns true then the next call
//* to <MethodRef name="next"/> will also return true.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * Implements the <code>hasMoreRows</code> method of the TOS
 * <code>TResultSet</code> class. Checks if there are more rows to be
 * read. If it returns true a call to the <TT>next()</TT> method must
 * be made in order to fecth the row.
 *
 * @return Returns <TT>true</TT> if there are more rows to be fecthed.
 * Returns <TT>false</TT> otherwise.
 *
 **************************************************************************/

    public Object hasMoreRows(final TeaFunction obj,
                              final TeaContext     context,
                              final Object[]    args)
        throws TeaRunException {

        checkResultSet();

        if ( _rowWasRead ) {
            return Boolean.TRUE;
        }

        try {
            _rowWasRead = _atEnd ? false : _resultSet.next();
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }
      
        if ( !_rowWasRead ) {
            _atEnd = true;
        }

        return _rowWasRead ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaMethod name="skip"
//*            arguments="count"
//*            className="TResultSet">
//* 
//* <Overview>
//* Advances the cursor current position by <Arg name="count"/>
//* positions.
//* </Overview>
//*
//* <Parameter name="count">
//* Integer representing the number of positions the cursor will be
//* advanced by.
//* </Parameter>
//* 
//* <Description>
//* This method is equivalent to calling the <MethodRef name="next"/> method
//* <Arg name="count"/> times, after the first call to the
//* <MethodRef name="next"/> method.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * Implements the <code>skip</code> method of the TOS
 * <code>TResultSet</code> class.
 *
 **************************************************************************/

    public Object skip(final TeaFunction obj,
                       final TeaContext     context,
                       final Object[]    args)
        throws TeaRunException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "count");
        }

        int count = Args.getInt(args,2).intValue();

        try {
            skip(count);
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        return obj;
    }





/**************************************************************************
 *
 * Skips over the next <TT>n</TT> rows in the result set, ignoring
 * them.
 *
 * @param rowsToSkip The number of rows to skip.
 *
 **************************************************************************/

    private void skip(final int rowsToSkip)
        throws TeaRunException,
               SQLException {

        checkResultSet();

        if ( _atEnd ) {
            return;
        }

        int n = rowsToSkip;

        if ( n > 0 ) {
            if ( _rowWasRead ) {
                --n;
            }
            while ( n-- > 0 ) {
                if ( !_resultSet.next() ) {
                    _atEnd = true;
                    break;
                }
            }
            _rowWasRead = false;
        }
    }





//* 
//* <TeaMethod name="getInt"
//*            arguments="columnIndex"
//*            className="TResultSet">
//* 
//* <Prototype arguments="columnName"/>
//* 
//* <Overview>
//* Fetches as an integer the contents of one of the columns of the
//* record at the result set current cursor position.
//* </Overview>
//*
//* <Parameter name="columnIndex">
//* Integer representing the index of the column to retrieve. The first
//* field has index 1.
//* </Parameter>
//* 
//* <Parameter name="columnName">
//* The name of the column to retrieve. This should be the same string
//* returned by a call to the
//* <MethodRef tosClass="TResultSet" name="getColumnName"/> method.
//* </Parameter>
//*
//* <Returns>
//* An integer object corresponding to the contents of the field. If
//* the field is null in the database then the zero value is returned.
//* </Returns>
//* 
//* <Description>
//* Retrieves the contents of one of the fields of the record at the
//* result set current cursor position. The field can be designated
//* either by its index or by its name.
//* <P>
//* If the <Arg name="columnIndex"/> argument is less than one or
//* greater than the number of fields then a runtime error is generated.
//* </P>
//* <P>
//* The field to retrieve can be of any type in the database that can
//* be converted to an integer. If that is not the case then a 
//* runtime error is generated.
//* </P>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * Implements the <code>getInt</code> method of the TOS
 * <code>TResultSet</code> class.
 *
 **************************************************************************/

    public Object getInt(final TeaFunction obj,
                         final TeaContext     context,
                         final Object[]    args)
        throws TeaRunException {

      if ( args.length != 3 ) {
          throw new SNumArgException(args, "column-index");
      }

      Object indexObj = args[2];
      int    value    = 0;

      try {
          value = getInt(indexObj);
      } catch ( SQLException e ) {
          throw new TeaRunException(e);
      }

      return Integer.valueOf(value);
    }





/**************************************************************************
 *
 * Retrieves the value of a field in the current row.
 *
 * @param index The index of thw row to retrieve. The first is
 * numbered 1, and so on.
 *
 * @return The value of a field of the current row.
 *
 * @exception TeaException Thrown if the result set has been closed,
 * if there is no current row or if there were any problems accessing
 * the database.
 *
 **************************************************************************/

    private int getInt(final Object indexObj)
        throws TeaRunException,
               SQLException  {

        checkResultSet();

        if ( _atEnd ) {
            throw new TeaRunException("no more rows");
        }

        if ( indexObj instanceof Integer ) {
            int index = ((Integer)indexObj).intValue();
            return _resultSet.getInt(index);
        }
        if ( indexObj instanceof String ) {
            String columnName = (String)indexObj;
            return _resultSet.getInt(columnName);
        }

        throw new STypeException("index must be an int or a string, not a {0}",
                                 Types.getTypeName(indexObj));
    }





//* 
//* <TeaMethod name="getFloat"
//*            arguments="columnIndex"
//*            className="TResultSet">
//* 
//* <Prototype arguments="columnName"/>
//* 
//* <Overview>
//* Fetches as a float the contents of one of the columns of the
//* record at the result set current cursor position.
//* </Overview>
//*
//* <Parameter name="columnIndex">
//* Integer representing the index of the column to retrieve. The first
//* field has index 1.
//* </Parameter>
//* 
//* <Parameter name="columnName">
//* The name of the column to retrieve. This should be the same string
//* returned by a call to the
//* <MethodRef tosClass="TResultSet" name="getColumnName"/> method.
//* </Parameter>
//*
//* <Returns>
//* A float object corresponding to the contents of the field. If
//* the field is null in the database then the zero value is returned.
//* </Returns>
//* 
//* <Description>
//* Retrieves the contents of one of the fields of the record at the
//* result set current cursor position. The field can be designated
//* either by its index or by its name.
//* <P>
//* If the <Arg name="columnIndex"/> argument is less than one or
//* greater than the number of fields then a runtime error is generated.
//* </P>
//* <P>
//* The field to retrieve can be of any type in the database that can
//* be converted to a float. If that is not the case then a 
//* runtime error is generated.
//* </P>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * Implements the <code>getFloat</code> method of the TOS
 * <code>TResultSet</code> class.
 *
 **************************************************************************/

    public Object getFloat(final TeaFunction obj,
                           final TeaContext     context,
                           final Object[]    args)
        throws TeaRunException {

      if ( args.length != 3 ) {
          throw new SNumArgException(args, "column-index");
      }

      Object indexObj = args[2];
      double value    = 0;

      try {
          value = getFloat(indexObj);
      } catch ( SQLException e ) {
          throw new TeaRunException(e);
      }

      return new Double(value);
    }





/**************************************************************************
 *
 * Retrieves the value of a field in the current row.
 *
 * @param index The index of thw row to retrieve. The first is
 * numbered 1, and so on.
 *
 * @return The value of a field of the current row.
 *
 * @exception TeaException Thrown if the result set has been closed,
 * if there is no current row or if there were any problems accessing
 * the database.
 *
 **************************************************************************/

    private double getFloat(final Object indexObj)
        throws TeaRunException,
               SQLException {

        checkResultSet();

        if ( _atEnd ) {
            throw new TeaRunException("no more rows");
        }

        if ( indexObj instanceof Integer ) {
            int index = ((Integer)indexObj).intValue();
            return _resultSet.getDouble(index);
        }
        if ( indexObj instanceof String ) {
            String columnName = (String)indexObj;
            return _resultSet.getDouble(columnName);
        }

        throw new STypeException("index must be an int or a string, not a {0}",
                                 Types.getTypeName(indexObj));
    }





//* 
//* <TeaMethod name="getString"
//*            arguments="columnIndex"
//*            className="TResultSet">
//* 
//* <Prototype arguments="columnName"/>
//* 
//* <Overview>
//* Fetches as a string the contents of one of the columns of the
//* record at the cursor current position.
//* </Overview>
//*
//* <Parameter name="columnIndex">
//* Integer representing the index of the column to retrieve. The first
//* field has index 1.
//* </Parameter>
//* 
//* <Parameter name="columnName">
//* The name of the column to retrieve. This should be the same string
//* returned by a call to the
//* <MethodRef tosClass="TResultSet" name="getColumnName"/> method.
//* </Parameter>
//*
//* <Returns>
//* A string object representing the contents of the field. If the field
//* is null in the database then the null object is returned.
//* </Returns>
//* 
//* <Description>
//* Retrieves the contents of one of the fields of the record at the
//* result set current cursor position. The field can be designated
//* either by its index or by its name.
//* <P>
//* If the <Arg name="columnIndex"/> argument is less than one or
//* greater than the number of fields then a runtime error is generated.
//* </P>
//* <P>
//* The field to retrieve can be of any type in the database that can
//* be converted to a string.
//* </P>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * Implements the <code>getString</code> method of the TOS
 * <code>TResultSet</code> class.
 *
 **************************************************************************/

    public Object getString(final TeaFunction obj,
                            final TeaContext     context,
                            final Object[]    args)
        throws TeaRunException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "column-index");
        }

        Object indexObj = args[2];
        String value    = null;

        try {
            value = getString(indexObj);
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        return (value==null) ? TeaNull.NULL : value;
    }





/**************************************************************************
 *
 * Retrieves the value of a field in the current row.
 *
 * @param index The index of thw row to retrieve. The first is
 * numbered 1, and so on.
 *
 * @return The value of a field of the current row.
 *
 * @exception TeaException Thrown if the result set has been closed,
 * if there is no current row or if there were any problems accessing
 * the database.
 *
 **************************************************************************/

    private String getString(final Object indexObj)
        throws TeaRunException,
               SQLException {

        checkResultSet();

        if ( _atEnd ) {
            throw new TeaRunException("no more rows");
        }

        if ( indexObj instanceof Integer ) {
            int index = ((Integer)indexObj).intValue();
            return _resultSet.getString(index);
        }
        if ( indexObj instanceof String ) {
            String columnName = (String)indexObj;
            return _resultSet.getString(columnName);
        }

        throw new STypeException("index must be an int or a string, not a {0}",
                                 Types.getTypeName(indexObj));
    }





//* 
//* <TeaMethod name="getDate"
//*            arguments="columnIndex"
//*            className="TResultSet">
//* 
//* <Prototype arguments="columnName"/>
//* 
//* <Overview>
//* Fetches as a date object the contents of one of the columns of the
//* record at the cursor current position.
//* </Overview>
//*
//* <Parameter name="columnIndex">
//* Integer representing the index of the column to retrieve. The first
//* field has index 1.
//* </Parameter>
//* 
//* <Parameter name="columnName">
//* The name of the column to retrieve. This should be the same string
//* returned by a call to the
//* <MethodRef tosClass="TResultSet" name="getColumnName"/> method.
//* </Parameter>
//*
//* <Returns>
//* A date object representing the contents of the field. If the field
//* is null in the database then the null object is returned.
//* </Returns>
//* 
//* <Description>
//* Retrieves the contents of one of the fields of the record at the
//* result set current cursor position. The field can be designated
//* either by its index or by its name.
//* <P>
//* If the <Arg name="columnIndex"/> argument is less than one or
//* greater than the number of fields then a runtime error is generated.
//* </P>
//* <P>
//* The field to retrieve can be of any type in the database that can
//* be converted to a date.
//* </P>
//* </Description>
//*  
//* <Since version="3.1.5"/>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * Implements the <code>getDate</code> method of the TOS
 * <code>TResultSet</code> class.
 *
 **************************************************************************/

    public Object getDate(final TeaFunction obj,
                          final TeaContext  context,
                          final Object[]   args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "column-index");
        }

        Object indexObj = args[2];
        Date   date     = null;
        SDate  result   = null;

        try {
            date   = getDate(indexObj);
            if ( date != null ) {
                result = SDate.newInstance(context);
                result.initFromDate(date);
            }
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        return (result==null) ? TeaNull.NULL : result;
    }





/**************************************************************************
 *
 * Retrieves the value of a field in the current row.
 *
 * @param index The index of thw row to retrieve. The first is
 * numbered 1, and so on.
 *
 * @return The value of a field of the current row.
 *
 * @exception TeaException Thrown if the result set has been closed,
 * if there is no current row or if there were any problems accessing
 * the database.
 *
 **************************************************************************/

    private Date getDate(final Object indexObj)
        throws TeaRunException,
               SQLException {

        checkResultSet();

        if ( _atEnd ) {
            throw new TeaRunException("no more rows");
        }

        if ( indexObj instanceof Integer ) {
            int index = ((Integer)indexObj).intValue();
            return _resultSet.getTimestamp(index);
        }
        if ( indexObj instanceof String ) {
            String columnName = (String)indexObj;
            return _resultSet.getTimestamp(columnName);
        }

        throw new STypeException("index must be an int or a string, not a {0}",
                                 Types.getTypeName(indexObj));
    }





//* 
//* <TeaMethod name="close"
//*            className="TResultSet">
//* 
//* <Overview>
//* Closes this result set and releases all the database resources
//* held by the object.
//* </Overview>
//* 
//* <Description>
//* Releases all the database resources used by this object.
//* The object can then no longer be used.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
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
 * 
 *
 **************************************************************************/

    public void close()
        throws SQLException {

        if ( _resultSet == null ) {
            return;
        }

        _resultSet.close();
        _resultSet = null;
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




//* 
//* <TeaMethod name="getColumnCount"
//*            className="TResultSet">
//* 
//* <Overview>
//* Retrieves the number of columns in this result set.
//* </Overview>
//*
//* <Returns>
//* An integer object representing the number of columns in this
//* result set.
//* </Returns>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * Retrieves the number of columns in this result set.
 *
 * @return
 *    The number of columns in this result set.
 *
 * @exception com.pdmfc.tea.TeaException
 *    Thrown if the result set has been closed or if there were any
 *    problem comunicating with the database.
 *
 **************************************************************************/

    public Object getColumnCount(final TeaFunction obj,
                                 final TeaContext     context,
                                 final Object[]    args)
        throws TeaException {

        checkResultSet();

        int result = 0;

        try {
            if ( _metaData == null ) {
                _metaData = _resultSet.getMetaData();
            }
            result = _metaData.getColumnCount();
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }

        return Integer.valueOf(result);
    }





//* 
//* <TeaMethod name="getColumnName"
//*            arguments="columnIndex"
//*            className="TResultSet">
//* 
//* <Overview>
//* Retrieves the name of one of the columns in this result set.
//* </Overview>
//*
//* <Parameter name="columnIndex">
//* Integer representing the index of the column whose name will
//* be  retrieved. The first column has index 1.
//* </Parameter>
//*
//* <Returns>
//* A string with the column name.
//* </Returns>
//*  
//* <Description>
//* The value returned by this method could be used as argument
//* to one of the <Func name="getXXX"/> methods to retrieve the
//* value of one of the fields in the result set.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object getColumnName(final TeaFunction obj,
                                final TeaContext     context,
                                final Object[]    args)
        throws TeaRunException {

      if ( args.length != 3 ) {
          throw new SNumArgException(args, "column-index");
      }

      int    index  = Args.getInt(args,2).intValue();
      String result = null;

      try {
          result = getColumnName(index);
      } catch ( SQLException e ) {
          throw new TeaRunException(e);
      }

      return result;
    }





/**************************************************************************
 *
 * Retrieves the name of a column.
 *
 * @param index The index of the column whose name is to be
 * retrieved. The first column is numbered 1, and so on.
 *
 * @return A <TT>String</TT> with the name of a column of this result
 * set.
 *
 * @exception TeaException Thrown if the result set has been closed
 * or if there were any problem comunicating with the database.
 *
 **************************************************************************/

    String getColumnName(final int index)
        throws TeaRunException,
               SQLException {

        checkResultSet();

        if ( _metaData == null ) {
            _metaData = _resultSet.getMetaData();
        }

        return _metaData.getColumnName(index);
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void checkResultSet()
        throws TeaRunException {

        if ( _resultSet == null ) {
            throw new TeaRunException("result set is closed");
        }
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

