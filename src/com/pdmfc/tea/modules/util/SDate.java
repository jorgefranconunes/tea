/**************************************************************************
 *
 * Copyright (c) 2001, 2002, 2003, 2004 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SDate.java,v 1.10 2005/11/04 05:50:04 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2004/11/02 The "TDate format" method now generates a more user
 * friendly message when the formating string is not valid. (jfn)
 *
 * 2003/09/13 Added the "getDate()" method. (jfn)
 *
 * 2003/09/13 The static "getDate(Objtec[],int)" method is now
 * public. (jfn)
 *
 * 2001/05/12 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;





//* 
//* <TeaClass name="TDate"
//*           module="tea.util">
//*
//* <Overview>
//* Represents an instance in time.
//* </Overview>
//*
//* <Description>
//* Instances of <Func name="TDate"/> represent an instant in time,
//* with the precision of a second. It is possible to obtain the
//* system current time by creating an instance without passing any
//* arguments to the constructor.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Implements an TOS object that represents an instant in time.
 *
 **************************************************************************/

public class SDate
    extends STosObj {





    private static final String     CLASS_NAME   = "TDate";
    private static final SObjSymbol CLASS_NAME_S =
	SObjSymbol.addSymbol(CLASS_NAME);

    public final Calendar _calendar = new GregorianCalendar();





/**************************************************************************
 *
 * Initializes the object internal state.
 *
 **************************************************************************/

   public SDate(STosClass myClass)
       throws STeaException {

      super(myClass);
   }





/**************************************************************************
 *
 * Initializes the object to represent the same date/time of another
 * <TT>SDate</TT>.
 *
 * @param aDate The <TT>SDate</TT> object used as source.
 *
 **************************************************************************/

    public void initFromDate(SDate aDate) {

	Calendar source = aDate._calendar;

	_calendar.set(source.get(Calendar.YEAR),
		      source.get(Calendar.MONTH),
		      source.get(Calendar.DAY_OF_MONTH),
		      source.get(Calendar.HOUR_OF_DAY),
		      source.get(Calendar.MINUTE),
		      source.get(Calendar.SECOND));
    }





/**************************************************************************
 *
 * Initializes the object with the date represented by the string
 * argument. The string can only have size 6, 8, 12 or 14, otherwise a
 * <TT>SRuntimeException</TT> is thrown.
 *
 * @throws SRuntimeException When the string does not have size 6, 8,
 *12 or 14, or one of the string components does not represent a valid
 *integer.
 *
 **************************************************************************/

    public void initFromString(String str)
	throws SRuntimeException {

	boolean ok    = true;
	int     size  = str.length();
	int     year  = 0;
	int     month = 0;
	int     day   = 0;
	int     hour  = 0;
	int     min   = 0;
	int     sec   = 0;

	try {
	    switch ( size ) {
	    case 6 :
		year  = Integer.parseInt(str.substring(0,2));
		if ( year < 70 ) {
		    year += 2000;
		} else {
		    year += 1900;
		}
		month = Integer.parseInt(str.substring(2,4)) - 1;
		day   = Integer.parseInt(str.substring(4,6));
		break;
	    case 14 :
		sec = Integer.parseInt(str.substring(12,14));
		// Fallthrough.
	    case 12 :
		hour = Integer.parseInt(str.substring(8,10));
		min  = Integer.parseInt(str.substring(10,12));
		// Fallthrough.
	    case 8 :
		year  = Integer.parseInt(str.substring(0,4));
		month = Integer.parseInt(str.substring(4,6)) - 1;
		day   = Integer.parseInt(str.substring(6,8));
		break;
	    default :
		ok = false;
	    }
	} catch ( NumberFormatException e) {
	    ok = false;
	}
	if ( !ok ) {
	    throw new SRuntimeException("invalid date string (" + str + ")");
	}

	_calendar.set(year, month, day, hour, min, sec);
    }





/**************************************************************************
 *
 * Changes the calendar date this object represents.
 *
 * @param date The new date this object will represent.
 *
 **************************************************************************/

    public void initFromDate(Date date) {

	_calendar.setTime(date);
    }





/**************************************************************************
 *
 * Fetches the <code>java.util.Date</code> represented by this TOS
 * instance.
 *
 * @return The <code>java.util.Date</code> object represented by this
 * TOS <code>TDate</code> instance.
 *
 **************************************************************************/

    public Date getDate() {

	Date result = _calendar.getTime();

	return result;
    }





//* 
//* <TeaMethod name="constructor"
//* 	       className="TDate">
//* 
//* <Prototype arguments="aYear aMonth aDay anHour aMinute aSecond"/>
//* 
//* <Prototype arguments="aYear aMonth aDay"/>
//* 
//* <Prototype arguments="aDate"/>
//* 
//* <Prototype arguments="aString"/>
//* 
//* <Overview>
//* Initializes the <Class name="TDate"/> object with a given date.
//* </Overview>
//*
//* <Parameter name="aYear">
//* Integer representing the year component the <Class name="TDate"/>
//* object will be initialized with.
//* </Parameter>
//*
//* <Parameter name="aMonth">
//* Integer representing the month component the <Class name="TDate"/>
//* object will be initialized with.
//* </Parameter>
//*
//* <Parameter name="aDay">
//* Integer representing the day component the <Class name="TDate"/>
//* object will be initialized with.
//* </Parameter>
//*
//* <Parameter name="anHour">
//* Integer representing the hour component the <Class name="TDate"/>
//* object will be initialized with.
//* </Parameter>
//*
//* <Parameter name="aMinute">
//* Integer representing the minute component the <Class name="TDate"/>
//* object will be initialized with.
//* </Parameter>
//*
//* <Parameter name="aSecond">
//* Integer representing the seconds component the <Class name="TDate"/>
//* object will be initialized with.
//* </Parameter>
//* 
//* <Parameter name="aString">
//* String representing a date in the format "[YY]YYMMDD[hhmm[ss]]".
//* </Parameter>
//* 
//* <Description>
//* The <Arg name="aString"/> argument may have 6, 8, 12 or 14
//* characters of length. When it has 6 characters it is taken to
//* represent a date in the "YYMMDD" format, where values of
//* the year component less than 70 are taken to belong to the XXI
//* century.
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

    public Object constructor(SObjFunction obj,
			      SContext     context,
			      Object[]     args)
	throws SRuntimeException {

	int      numArgs = args.length;

	switch ( numArgs ) {
	case 2 :
	    break;
	case 3 :
	    Object dateArg = args[2];
	    if ( dateArg instanceof SDate ) {
		initFromDate((SDate)dateArg);
	    } else if ( dateArg instanceof String ) {
		initFromString((String)dateArg);
	    } else {
		throw new STypeException("arg 2 should be an TDate or a String " +
					 "not a " + STypes.getTypeName(dateArg));
	    }
	    break;
	case 5 :
	    _calendar.set(STypes.getInt(args, 2).intValue(),
			  STypes.getInt(args, 3).intValue() - 1,
			  STypes.getInt(args, 4).intValue(),
			  0,
			  0,
			  0);
	    break;
	case 8 :
	    _calendar.set(STypes.getInt(args, 2).intValue(),
			  STypes.getInt(args, 3).intValue() - 1,
			  STypes.getInt(args, 4).intValue(),
			  STypes.getInt(args, 5).intValue(),
			  STypes.getInt(args, 6).intValue(),
			  STypes.getInt(args, 7).intValue());
	    break;
	default :
	    throw new SNumArgException("arguments: [TDate] | [year month day [hour minute second]]");
	}
	_calendar.set(Calendar.MILLISECOND, 0);

	return obj;
    }





//* 
//* <TeaMethod name="getYear"
//* 	       className="TDate">
//* 
//* <Overview>
//* Fetches the value of the year component of the
//* <Class name="TDate"/> object.
//* </Overview>
//*
//* <Returns>
//* An integer value representing the year component of this
//* <Class name="TDate"/> object.
//* </Returns>
//* 
//* <Description>
//* Fetches the year component of the <Class name="TDate"/> object and
//* returns an integer with its value.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "getYear" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object getYear(SObjFunction obj,
			  SContext     context,
			  Object[]     args) {

	int year = _calendar.get(Calendar.YEAR);

	return new Integer(year);
    }





//* 
//* <TeaMethod name="getMonth"
//* 	       className="TDate">
//* 
//* <Overview>
//* Fetches the value of the month component of the
//* <Class name="TDate"/> object.
//* </Overview>
//*
//* <Returns>
//* An integer value representing the month component of this
//* <Class name="TDate"/> object
//* </Returns>
//* 
//* <Description>
//* Fetches the month component of the <Class name="TDate"/> object and
//* returns an integer with its value. The value will always be between
//* 1 and 12. January is represented by 1 and December by 12.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "getMonth" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object getMonth(SObjFunction obj,
			   SContext     context,
			   Object[]     args) {

	int month = _calendar.get(Calendar.MONTH) + 1;

	return new Integer(month);
    }





//* 
//* <TeaMethod name="getDay"
//* 	       className="TDate">
//* 
//* <Overview>
//* Fetches the value of the day component of the
//* <Class name="TDate"/> object.
//* </Overview>
//*
//* <Returns>
//* An integer value representing the month component of this
//* <Class name="TDate"/> object.
//* </Returns>
//* 
//* <Description>
//* Fetches the day component of the <Class name="TDate"/> object and
//* returns an integer with its value. The value will always be between
//* 1 and 31.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "getDay" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object getDay(SObjFunction obj,
			 SContext     context,
			 Object[]     args) {

	int day = _calendar.get(Calendar.DAY_OF_MONTH);

	return new Integer(day);
    }





//* 
//* <TeaMethod name="getHour"
//* 	       className="TDate">
//* 
//* <Overview>
//* Fetches the value of the hour component of the
//* <Class name="TDate"/> object.
//* </Overview>
//*
//* <Returns>
//* An integer value representing the hour component of this
//* <Class name="TDate"/> object.
//* </Returns>
//* 
//* <Description>
//* Fetches the hour component of the <Class name="TDate"/> object and
//* returns an integer with its value. The returned value will
//* always be between 0 and 23.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "getHour" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object getHour(SObjFunction obj,
			  SContext     context,
			  Object[]     args) {

	int hour = _calendar.get(Calendar.HOUR_OF_DAY);

	return new Integer(hour);
    }





//* 
//* <TeaMethod name="getMinute"
//* 	       className="TDate">
//* 
//* <Overview>
//* Fetches the value of the minutes component of the
//* <Class name="TDate"/> object.
//* </Overview>
//*
//* <Returns>
//* An integer value representing the minutes component of this
//* <Class name="TDate"/> object
//* </Returns>
//* 
//* <Description>
//* Fetches the minutes component of the <Class name="TDate"/> object and
//* returns an integer with its value. The returned value will
//* always be between 0 and 59.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "getMinute" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object getMinute(SObjFunction obj,
			    SContext     context,
			    Object[]     args) {

	int minute = _calendar.get(Calendar.MINUTE);

	return new Integer(minute);
    }





//* 
//* <TeaMethod name="getSecond"
//* 	       className="TDate">
//* 
//* <Overview>
//* Fetches the value of the seconds component of the
//* <Class name="TDate"/> object.
//* </Overview>
//*
//* <Returns>
//* An integer value representing the seconds component of this
//* <Class name="TDate"/> object
//* </Returns>
//* 
//* <Description>
//* Fetches the seconds component of the <Class name="TDate"/> object and
//* returns an integer with its value. The returned value will always
//* be between 0 and 59.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "getSecond" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object getSecond(SObjFunction obj,
			    SContext     context,
			    Object[]     args) {

	int second = _calendar.get(Calendar.SECOND);

	return new Integer(second);
    }





//* 
//* <TeaMethod name="getDayOfWeek"
//* 	       className="TDate">
//* 
//* <Overview>
//* Fetches the day of the week associated with the
//* <Class name="TDate"/> object.
//* </Overview>
//*
//* <Returns>
//* An integer value representing the day of the week of the
//* <Class name="TDate"/> object.
//* </Returns>
//* 
//* <Description>
//* The returned value will always be between 0 and 6.
//*  Sunday is 0. Saturday is 6.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "getDayOfWeek" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object getDayOfWeek(SObjFunction obj,
			       SContext     context,
			       Object[]     args) {

	int day = _calendar.get(Calendar.DAY_OF_WEEK) - 1;

	return new Integer(day);
    }





//* 
//* <TeaMethod name="setDate"
//*            arguments="aYear aMonth aDay anHour aMinute aSecond"
//* 	       className="TDate">
//* 
//* <Prototype arguments="aYear aMonth aDay"/>
//* 
//* <Prototype arguments="aDate"/>
//* 
//* <Prototype arguments="aString"/>
//* 
//* <Overview>
//* Changes the date the <Class name="TDate"/> object represents.
//* </Overview>
//*
//* <Parameter name="aYear">
//* Integer representing the year component the <Class name="TDate"/>
//* object will be initialized with.
//* </Parameter>
//*
//* <Parameter name="aMonth">
//* Integer representing the month component the <Class name="TDate"/>
//* object will be initialized with.
//* </Parameter>
//*
//* <Parameter name="aDay">
//* Integer representing the day component the <Class name="TDate"/>
//* object will be initialized with.
//* </Parameter>
//*
//* <Parameter name="anHour">
//* Integer representing the hour component the <Class name="TDate"/>
//* object will be initialized with.
//* </Parameter>
//*
//* <Parameter name="aMinute">
//* Integer representing the minute component the <Class name="TDate"/>
//* object will be initialized with.
//* </Parameter>
//*
//* <Parameter name="aSecond">
//* Integer representing the seconds component the <Class name="TDate"/>
//* object will be initialized with.
//* </Parameter>
//* 
//* <Parameter name="aDate">
//* A <Class name="TDate"/> object.
//* </Parameter>
//* 
//* <Parameter name="aString">
//* String representing a date in the format "[YY]YYMMDD[hhmm[ss]]".
//* </Parameter>
//* 
//* <Description>
//* The <Arg name="aString"/> argument may have 6, 8, 12 or 14
//* characters of length. When it has 6 characters it is taken to
//* represent a date in the "YYMMDD" format, where values of
//* the year component less than 70 are taken to belong to the XXI
//* century.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "setDay" TOS method.
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
	throws SRuntimeException {

	int numArgs = args.length;

	switch ( numArgs ) {
	case 3 : 
	    Object dateArg = args[2];
	    if ( dateArg instanceof SDate ) {
		initFromDate((SDate)dateArg);
	    } else if ( dateArg instanceof String ) {
		initFromString((String)dateArg);
	    } else {
		throw new STypeException("arg 2 should be a TDate or a String not a " + STypes.getTypeName(dateArg));
	    }
	    break;
	case 5 :
	    _calendar.set(STypes.getInt(args, 2).intValue(),
			  STypes.getInt(args, 3).intValue() - 1,
			  STypes.getInt(args, 4).intValue(),
			  0,
			  0,
			  0);
	    break;
	case 8 :
	    _calendar.set(STypes.getInt(args, 2).intValue(),
			  STypes.getInt(args, 3).intValue() - 1,
			  STypes.getInt(args, 4).intValue(),
			  STypes.getInt(args, 5).intValue(),
			  STypes.getInt(args, 6).intValue(),
			  STypes.getInt(args, 7).intValue());
	    break;
	default :
	    throw new SNumArgException("arguments: TDate | year month day [hour minute second]");
	}
	
	return obj;
    }





//* 
//* <TeaMethod name="setTime"
//*            arguments="anHour aMinute aSecond"
//* 	       className="TDate">
//* 
//* <Overview>
//* Changes the hour, minute and second components of the
//* <Class name="TDate"/> object, leaving all the others unchanged.
//* </Overview>
//*
//* <Parameter name="anHour">
//* Integer representing the hour component the <Class name="TDate"/>
//* object will be initialized with.
//* </Parameter>
//*
//* <Parameter name="aMinute">
//* Integer representing the minute component the <Class name="TDate"/>
//* object will be initialized with.
//* </Parameter>
//*
//* <Parameter name="aSecond">
//* Integer representing the seconds component the <Class name="TDate"/>
//* object will be initialized with.
//* </Parameter>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "setTime TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object setTime(SObjFunction obj,
			  SContext     context,
			  Object[]     args)
	throws SRuntimeException {

	if ( args.length != 5 ) {
	    throw new SNumArgException("arguments: hour minute second");
	}

	_calendar.set(Calendar.HOUR_OF_DAY, STypes.getInt(args,2).intValue());
	_calendar.set(Calendar.MINUTE, STypes.getInt(args,3).intValue());
	_calendar.set(Calendar.SECOND, STypes.getInt(args,4).intValue());

	return obj;
    }





//* 
//* <TeaMethod name="format"
//*            arguments="formatString"
//* 	       className="TDate">
//* 
//* <Overview>
//* Generates a string with a representation of the date.
//* </Overview>
//*
//* <Parameter name="formatString">
//* Specifies the formating of the date.
//* </Parameter>
//* 
//* <Returns>
//* A string object obtained from the <Arg name="formatString"/>.
//* </Returns>
//* 
//* <Description>
//* The <Arg name="formatString"/> is expected to follow the same
//* rules as the formating string for the Java
//* <Var name="java.text.SimpleDateFormat"/> class. Otherwise a runtime
//* error will occur.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "format" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object format(SObjFunction obj,
			 SContext     context,
			 Object[]     args)
	throws SRuntimeException {

	if ( args.length != 3 ) {
	    throw new SNumArgException("format-string");
	}

	String           fmt       = STypes.getString(args, 2);
	SimpleDateFormat formatter = new SimpleDateFormat(fmt);
	String           result    = null;

	try {
	    result = formatter.format(_calendar.getTime());
	} catch (Throwable e) {
	    String   msg     = "Failed to format date - {0}";
	    Object[] fmtArgs = { e.getMessage() };
	    throw new SRuntimeException(msg, fmtArgs);
	}

	return result;
    }





//* 
//* <TeaMethod name="compare"
//*            arguments="aDate"
//* 	       className="TDate">
//* 
//* <Overview>
//* Compares this date object with another.
//* </Overview>
//*
//* <Parameter name="aDate">
//* The <Class name="TDate"/> object it will be compared against.
//* </Parameter>
//* 
//* <Returns>
//* -1 if this date preceded <Arg name="aDate"/>. 0 if this date is
//* exactly the same as <Arg name="aDate"/>. 1 if this date comes after
//* <Arg name="aDate"/>.
//* </Returns>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "compare" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object compare(SObjFunction obj,
			 SContext     context,
			 Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    new SNumArgException("args: TDate");
	}
	
	Calendar when   = getDate(args, 2)._calendar;
	int      result = 0;

	if ( _calendar.before(when) ) {
	    result = -1;
	} else {
	    if ( _calendar.after(when) ) {
		result = 1;
	    }
	}

	return new Integer(result);
    }





//* 
//* <TeaMethod name="before"
//*            arguments="aDate"
//* 	       className="TDate">
//* 
//* <Overview>
//* Checks if the date represented by the object precedes another date.
//* </Overview>
//*
//* <Parameter name="aDate">
//* The <Class name="TDate"/> object it will be compared against.
//* </Parameter>
//* 
//* <Returns>
//* True if the object represents a date before the date represented
//* by <Arg name="aDate"/>. False otherwise.
//* </Returns>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "before" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object before(SObjFunction obj,
			 SContext     context,
			 Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    new SNumArgException("args: TDate");
	}
	
	Calendar when = getDate(args, 2)._calendar;

	return _calendar.before(when) ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaMethod name="after"
//*            arguments="aDate"
//* 	       className="TDate">
//* 
//* <Overview>
//* Checks if the date represented by the object is more recent than
//* another date.
//* </Overview>
//*
//* <Parameter name="aDate">
//* The <Class name="TDate"/> object it will be compared against.
//* </Parameter>
//* 
//* <Returns>
//* True if the object represents a date after the date represented
//* by <Arg name="aDate"/>. False otherwise.
//* </Returns>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "after" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object after(SObjFunction obj,
			SContext     context,
			Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    new SNumArgException("args: TDate");
	}
	
	Calendar when = getDate(args, 2)._calendar;

	return _calendar.after(when) ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaMethod name="same"
//*            arguments="aDate"
//* 	       className="TDate">
//* 
//* <Overview>
//* Checks if the date represented by the object is the same as
//* another date.
//* </Overview>
//*
//* <Parameter name="aDate">
//* The <Class name="TDate"/> object it will be compared against.
//* </Parameter>
//* 
//* <Returns>
//* True if the object represents a date that is the same date represented
//* by <Arg name="aDate"/>. False otherwise.
//* </Returns>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "same" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object same(SObjFunction obj,
		       SContext     context,
		       Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    new SNumArgException("args: TDate");
	}

	Calendar cal  = _calendar;
	Calendar when = getDate(args, 2)._calendar;

	boolean isEqual =
	    (cal.get(Calendar.YEAR)==when.get(Calendar.YEAR))
	    && (cal.get(Calendar.MONTH)==when.get(Calendar.MONTH))
	    && (cal.get(Calendar.DAY_OF_MONTH)==when.get(Calendar.DAY_OF_MONTH))
	    && (cal.get(Calendar.HOUR_OF_DAY)==when.get(Calendar.HOUR_OF_DAY))
	    && (cal.get(Calendar.MINUTE)==when.get(Calendar.MINUTE))
	    && (cal.get(Calendar.SECOND)==when.get(Calendar.SECOND));
	
	return isEqual ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaMethod name="notSame"
//*            arguments="aDate"
//* 	       className="TDate">
//* 
//* <Overview>
//* Checks if the date represented by the object is not the same as
//* another date.
//* </Overview>
//*
//* <Parameter name="aDate">
//* The <Class name="TDate"/> object it will be compared against.
//* </Parameter>
//* 
//* <Returns>
//* True if the object represents a date that is not the same date represented
//* by <Arg name="aDate"/>. False otherwise.
//* </Returns>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "notSame" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object notSame(SObjFunction obj,
			  SContext     context,
			  Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    new SNumArgException("args: TDate");
	}

	Calendar cal  = _calendar;
	Calendar when = getDate(args, 2)._calendar;

	boolean isEqual =
	    (cal.get(Calendar.YEAR)==when.get(Calendar.YEAR))
	    && (cal.get(Calendar.MONTH)==when.get(Calendar.MONTH))
	    && (cal.get(Calendar.DAY_OF_MONTH)==when.get(Calendar.DAY_OF_MONTH))
	    && (cal.get(Calendar.HOUR_OF_DAY)==when.get(Calendar.HOUR_OF_DAY))
	    && (cal.get(Calendar.MINUTE)==when.get(Calendar.MINUTE))
	    && (cal.get(Calendar.SECOND)==when.get(Calendar.SECOND));
	
	return isEqual ? Boolean.FALSE : Boolean.TRUE;
    }





/**************************************************************************
 *
 * This is an utility method. It tries to convert argument
 * <code>index</code> into a <code>SDate</code>.
 *
 * @param args Array of <code>Object</code>, supposed to be the
 * arguments received by a call to the Tea function.
 *
 * @param index The index of the argument to convert.
 *
 * @exception STypeException Thrown if <code>args[index]</code> is not
 * a SDate
 *
 **************************************************************************/

    public static SDate getDate(Object[] args,
				int      index)
	throws STeaException {

	Object tosDate = args[index];

	try {
	    return (SDate)((STosObj)tosDate).part(0);
	} catch (ClassCastException e) {
	    String   msg     =
		"Argument {0} must contain a {1}, not a \"{2}\"";
	    Object[] fmtArgs =
		{String.valueOf(index),CLASS_NAME,STypes.getTypeName(tosDate)};
	    throw new STeaException(msg, fmtArgs);
	}
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

    public static SDate newInstance(SContext context)
	throws STeaException {

	STosObj date = STosUtil.newInstance(CLASS_NAME_S, context);

	if ( !(date instanceof SDate) ) {
	    throw new SRuntimeException("invalid " + CLASS_NAME + " class");
	}

	return (SDate)date;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

