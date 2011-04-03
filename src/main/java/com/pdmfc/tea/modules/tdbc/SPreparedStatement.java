/**************************************************************************
 *
 * Copyright (c) 2001, 2002, 2003 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
 *
 *
 * Revisions:
 *
 * 2003/09/13 Added the "setDate(...)" method. (jfn)
 *
 * 2003/02/13 The method "methodSetInt" was renamed to "setInt", in
 * order for the TOS method "setInt" to become available. (jfn)
 *
 * 2003/02/12 The "query(...)" method now correctly inserts into the
 * "_resultSets" vector the "SResultSet" object, instead of the
 * underlying "ResultSet". (jfn)
 *
 * 2001/05/12 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.tdbc.SStatement;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.modules.util.SDate;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypes;





//* 
//* <TeaClass name="TPreparedStatement"
//*           baseClass="TStatement"
//*           module="tea.tdbc">
//*
//* <Overview>
//* Represents a SQL pre-compiled statement.
//* </Overview>
//*
//* <Description>
//* Represents a SQL pre-compiled statement.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Implements an TOS object that acts like a java
 * <code>PreparedStatement</code>. It needs to be derived from <code>
 * because <code>SConnection</code> stores references to all the open
 * <code>SStatement</code>, <code>SPreparedStatement</code>,
 * <code>SCallableStatement</code> with no distinction.
 *
 **************************************************************************/

public class SPreparedStatement
    extends SStatement {





    private static final String     CLASS_NAME   = "TPreparedStatement";
    private static final SObjSymbol CLASS_NAME_S =
	SObjSymbol.addSymbol(CLASS_NAME);

    private PreparedStatement _prepStat = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SPreparedStatement(STosClass myClass)
	throws STeaException {

	super(myClass);
    }





/**************************************************************************
 *
 * Specifies the underlying <code>java.sql.PreparedStatement</code>
 * object to use. The given prepared statement should no longer be
 * used by whoever supplied it.
 *
 * @param stat The prepared statement to use.
 *
 **************************************************************************/

    public void setPreparedStatement(PreparedStatement stat) {

	_prepStat = stat;
	setStatement(stat);
    }





/**************************************************************************
 *
 * Fetches the name of the TOS class implemented by this Java class.
 *
 * @return The name of the TOS class.
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

    public static SStatement newInstance(SContext context)
	throws STeaException {

	STosObj prepStat = STosUtil.newInstance(CLASS_NAME_S, context);

	if ( !(prepStat instanceof SPreparedStatement) ) {
	    throw new SRuntimeException("invalid " + CLASS_NAME + " class");
	}

	return (SStatement)prepStat;
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

    public Object constructor(SObjFunction obj,
			      SContext     context,
			      Object[]     args)
	throws STeaException {

	return obj;
    }





//* 
//* <TeaMethod name="query"
//* 	   className="TPreparedStatement">
//* 
//* <Overview>
//* Executes the SQL query represented by this prepared statement.
//* </Overview>
//*
//* <Returns>
//* An instance of <ClassRef name="TResultSet"/> that represents the result
//* of the execution of this prepared statement.
//* </Returns>
//* 
//* <Description>
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

    public Object query(SObjFunction obj,
			SContext     context,
			Object[]     args)
	throws STeaException {

	ResultSet  rSet    = null;
	SResultSet tosRSet = null;

	try {
	    rSet = _prepStat.executeQuery();
	    tosRSet = SResultSet.newInstance(context);
	    tosRSet.setResultSet(rSet);
	    tosRSet.addClosedListener(this);
	    _resultSets.add(tosRSet);
	} catch (SQLException e) {
	    throw new SRuntimeException(e);
	}

	return tosRSet;
    }





//* 
//* <TeaMethod name="update"
//* 	   className="TPreparedStatement">
//* 
//* <Overview>
//* Executes the SQL statement that modifies the contents of the database
//* represented by this prepared statement.
//* </Overview>
//*
//* <Returns>
//* An integer object representing the number of records modified in
//* the database.
//* </Returns>
//* 
//* <Description>
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

    public Object update(SObjFunction obj,
			 SContext     context,
			 Object[]     args)
	throws STeaException {

	int result = 0;

	try {
	    result = _prepStat.executeUpdate();
	} catch (SQLException e) {
	    throw new SRuntimeException(e);
	}

	return new Integer(result);
    }





//* 
//* <TeaMethod name="execute"
//* 	   className="TPreparedStatement">
//* 
//* <Overview>
//* Executes an arbitrary SQL statement.
//* </Overview>
//* 
//* <Description>
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

    public Object execute(SObjFunction obj,
			  SContext     context,
			  Object[]     args)
	throws STeaException {

	boolean result = false;

	try {
	    result = _prepStat.execute();
	} catch (SQLException e) {
	    throw new SRuntimeException(e);
	}

	return result ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaMethod name="setInt"
//*            arguments="index value"
//* 	   className="TPreparedStatement">
//* 
//* <Overview>
//* Sets the value of one of the value placeholders.
//* </Overview>
//* 
//* <Parameter name="index">
//* Integer representing the index of the placeholder to modify.
//* </Parameter>
//* 
//* <Parameter name="value">
//* Numeric object whose value will be used to initialize the
//* placeholder.
//* </Parameter>
//* 
//* <Returns>
//* A reference to the same object for which the method was called.
//* </Returns>
//* 
//* <Description>
//* This method is used to set the integer value of one of the
//* placeholders.
//* The index of the first placeholder in the SQL statement is taken
//* to be one. If the <Arg name="value"/> argument is null then a SQL NULL
//* is used as the placeholder value.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "setInt" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with. 
 *
 **************************************************************************/

    public Object setInt(SObjFunction obj,
			 SContext     context,
			 Object[]     args)
	throws SRuntimeException {

	if ( args.length != 4 ) {
	    throw new SNumArgException("Args: index value");
	}

	int   index    = STypes.getInt(args, 2).intValue();
	Object valueArg = args[3];

	try {
	    if ( valueArg == SObjNull.NULL ) {
		setIntNull(index);
	    } else {
		setInt(index, STypes.getInt(args,3).intValue());
	    }
	} catch (SQLException e) {
	    throw new SRuntimeException(e);
	}

	return obj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setInt(int index,
		       int value)
	throws SQLException {

	_prepStat.setInt(index, value);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setIntNull(int index)
	throws SQLException {
       
	_prepStat.setNull(index, java.sql.Types.INTEGER);
    }





//* 
//* <TeaMethod name="setFloat"
//*            arguments="index value"
//* 	   className="TPreparedStatement">
//* 
//* <Overview>
//* Sets the value of one of the value placeholders.
//* </Overview>
//* 
//* <Parameter name="index">
//* Integer representing the index of the placeholder to modify.
//* </Parameter>
//* 
//* <Parameter name="value">
//* Numeric object whose value will be used to initialize the
//* placeholder.
//* </Parameter>
//* 
//* <Returns>
//* A reference to the same object for which the method was called.
//* </Returns>
//* 
//* <Description>
//* This method is used to set the floating point value of one of the
//* placeholders.
//* The index of the first placeholder in the SQL statement is taken
//* to be one. If the <Arg name="value"/> argument is null then a SQL NULL
//* is used as the placeholder value.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "setFloat" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with. 
 *
 **************************************************************************/

    public Object setFloat(SObjFunction obj,
			   SContext     context,
			   Object[]     args)
	throws SRuntimeException {

	if ( args.length != 4 ) {
	    throw new SNumArgException("Args: index value");
	}

	int   index    = STypes.getInt(args, 2).intValue();
	Object valueArg = args[3];

	try {
	    if ( valueArg == SObjNull.NULL ) {
		setDoubleNull(index);
	    } else {
		setDouble(index, STypes.getFloat(args,3).doubleValue());
	    }
	} catch (SQLException e) {
	    throw new SRuntimeException(e);
	}

	return obj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setDouble(int    index,
			   double value)
	throws SQLException {

	_prepStat.setDouble(index, value);
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setDoubleNull(int index)
	throws SQLException {

	_prepStat.setNull(index, java.sql.Types.DOUBLE);
    }





//* 
//* <TeaMethod name="setString"
//*            arguments="index value"
//* 	   className="TPreparedStatement">
//* 
//* <Overview>
//* Sets the value of one of the value placeholders.
//* </Overview>
//* 
//* <Parameter name="index">
//* Integer representing the index of the placeholder to modify.
//* </Parameter>
//* 
//* <Parameter name="value">
//* String object whose contents will be used to initialize the
//* placeholder.
//* </Parameter>
//* 
//* <Returns>
//* A reference to the same object for which the method was called.
//* </Returns>
//* 
//* <Description>
//* This method is used to set the string value of one of the placeholders.
//* The index of the first placeholder in the SQL statement is taken
//* to be one. If the <Arg name="value"/> argument is null then a SQL NULL
//* is used as the placeholder value.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "setString" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with. 
 *
 **************************************************************************/

    public Object setString(SObjFunction obj,
			    SContext     context,
			    Object[]     args)
	throws STeaException {

	if ( args.length != 4 ) {
	    throw new SNumArgException("Args: index value");
	}

	int    index    = STypes.getInt(args, 2).intValue();
	Object valueArg = args[3];

	try {
	    if ( valueArg == SObjNull.NULL ) {
		setStringNull(index);
	    } else {
		setString(index, STypes.getString(args,3));
	    }
	} catch (SQLException e) {
	    throw new SRuntimeException(e);
	}

	return obj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setString(int    index,
			   String value)
	throws SQLException {

	_prepStat.setString(index, value);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setStringNull(int index)
	throws SQLException {

	_prepStat.setNull(index, java.sql.Types.VARCHAR);
    }





//* 
//* <TeaMethod name="setDate"
//*            arguments="index value"
//* 	   className="TPreparedStatement">
//* 
//* <Overview>
//* Sets the value of one of the value placeholders as a date object.
//* </Overview>
//* 
//* <Parameter name="index">
//* Integer representing the index of the placeholder to modify.
//* </Parameter>
//* 
//* <Parameter name="value">
//* <ClassRef name="TDate"/> object that will be assigned to the
//* placeholder.
//* </Parameter>
//* 
//* <Returns>
//* A reference to the same object for which the method was called.
//* </Returns>
//* 
//* <Description>
//* This method is used to set the date value of one of the placeholders.
//* The index of the first placeholder in the SQL statement is taken
//* to be one. If the <Arg name="value"/> argument is null then a SQL NULL
//* is used as the placeholder value.
//* </Description>
//*  
//* <Since version="3.1.5"/>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "setString" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with. 
 *
 **************************************************************************/

    public Object setDate(SObjFunction obj,
			  SContext     context,
			  Object[]     args)
	throws STeaException {

	if ( args.length != 4 ) {
	    throw new SNumArgException("Args: index value");
	}

	int    index    = STypes.getInt(args, 2).intValue();
	Object valueArg = args[3];

	try {
	    if ( valueArg == SObjNull.NULL ) {
		setTimestampNull(index);
	    } else {
		setTimestamp(index, SDate.getDate(args,3));
	    }
	} catch (SQLException e) {
	    throw new SRuntimeException(e);
	}

	return obj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setTimestamp(int   index,
			      SDate value)
	throws SQLException {

	Date      date      = value.getDate();
	Timestamp timestamp = new Timestamp(date.getTime());

	_prepStat.setTimestamp(index, timestamp);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void setTimestampNull(int index)
	throws SQLException {

	_prepStat.setNull(index, java.sql.Types.TIMESTAMP);
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

