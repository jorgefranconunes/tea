/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tdbc;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.tdbc.SPreparedStatement;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.modules.util.SDate;
import com.pdmfc.tea.Args;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaNull;
import com.pdmfc.tea.TeaSymbol;
import com.pdmfc.tea.TeaVar;
import com.pdmfc.tea.TeaNumArgException;
import com.pdmfc.tea.TeaRunException;





//* 
//* <TeaClass name="TCallableStatement"
//*           baseClass="TPreparedStatement"
//*           module="tea.tdbc">
//*
//* <Overview>
//* Used to execute SQL stored procedures.
//* </Overview>
//*
//* <Description>
//* Used to execute SQL stored procedures.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Implements an TOS object that acts like a java
 * <TT>CallableStatement</TT>.
 *
 **************************************************************************/

public final class SCallableStatement
    extends SPreparedStatement {





    private static final String     CLASS_NAME   = "TCallableStatement";
    private static final TeaSymbol CLASS_NAME_S =
        TeaSymbol.addSymbol(CLASS_NAME);

    private CallableStatement   _callStat = null;
    private List<SOutParameter> _outList  = new ArrayList<SOutParameter>();





/**************************************************************************
 *
 * @param myClass The TOS class associated with instances of this Java
 * class.
 *
 **************************************************************************/

    public SCallableStatement(final STosClass myClass)
        throws TeaException {
        
        super(myClass);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void setCallableStatement(final CallableStatement stat) {

        _callStat = stat;
        setPreparedStatement(_callStat);
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

        STosObj callStat = STosUtil.newInstance(CLASS_NAME_S, context);

        if ( !(callStat instanceof SCallableStatement) ) {
            throw new TeaRunException("invalid " + CLASS_NAME + " class");
        }

        return (SStatement)callStat;
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
//* <TeaMethod name="registerString"
//*            arguments="index symbol"
//*                className="TCallableStatement">
//* 
//* <Overview>
//* Register a variable to receive the value of astring output parameter.
//* </Overview>
//* 
//* <Parameter name="index">
//* The index of a parameter in the SQL statement that will be used
//* as an output parameter.
//* </Parameter>
//* 
//* <Parameter name="symbol">
//* Symbol identifying a variable in the current context.
//* </Parameter>
//*
//* <Returns>
//* The same object for which this method was called.
//* </Returns>
//* 
//* <Description>
//* Registers a variable that will receive the value of an
//* output parameter when the statement gets executed. The value
//* will be a string object. The variable
//* is identifyed by the <Arg name="symbol"/> argument. That
//* variable must alread be known in the current context.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "registerString" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object registerString(final TeaFunction obj,
                                 final TeaContext     context,
                                 final Object[]    args)
        throws TeaRunException {

        if ( args.length != 4 ) {
            throw new TeaNumArgException(args, "index symbol");
        }

        int     index = Args.getInt(args,2).intValue();
        TeaVar var   = context.getVarObject(Args.getSymbol(args,3));

        try {
            registerOutString(index, var);
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }
        
        return obj;
    }





/**************************************************************************
 *
 * Registers an output parameter that is a string. When
 * <TT>query()</TT> or <TT>update()</TT> are called the contents of
 * <TT>var</TT> will contain the value of the associated output
 * parameter of the callable statement.
 *
 * @param index The index of the output parameter. The first is 1, the
 * second 2, ...
 *
 * @param var The <TT>TeaVar</TT> that will receive the value of the
 * output parameter after the statement is called.
 *
 **************************************************************************/

    private void registerOutString(final int     index,
                                   final TeaVar var)
        throws SQLException {

        _callStat.registerOutParameter(index, Types.VARCHAR);
        _outList.add(new SOutParameterString(index, var));
    }





//* 
//* <TeaMethod name="registerInt"
//*            arguments="index symbol"
//*                className="TCallableStatement">
//* 
//* <Overview>
//* Register a variable to receive the value of an integer output
//* parameter.
//* </Overview>
//* 
//* <Parameter name="index">
//* The index of a parameter in the SQL statement that will be used
//* as an output parameter.
//* </Parameter>
//* 
//* <Parameter name="symbol">
//* Symbol identifying a variable in the current context.
//* </Parameter>
//*
//* <Returns>
//* The same object for which this method was called.
//* </Returns>
//* 
//* <Description>
//* Registers a variable that will receive the value of an
//* output parameter when the statement gets executed. The value will
//* be an integer object. The variable
//* is identifyed by the <Arg name="symbol"/> argument. That
//* variable must alread be known in the current context.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "registerInt" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object registerInt(final TeaFunction obj,
                              final TeaContext     context,
                              final Object[]    args)
        throws TeaRunException {

        if ( args.length != 4 ) {
            throw new TeaNumArgException(args, "index symbol");
        }

        int     index = Args.getInt(args,2).intValue();
        TeaVar var   = context.getVarObject(Args.getSymbol(args,3));

        try {
            registerOutInt(index, var);
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }
        
        return obj;
    }





/**************************************************************************
 *
 * Registers an output parameter that is an integer. When
 * <TT>query()</TT> or <TT>update()</TT> are called the contents of
 * <TT>var</TT> will contain the value of the associated output
 * parameter of the callable statement.
 *
 * @param index The index of the output parameter. The first is 1, the
 * second 2, ...
 *
 * @param var The <TT>TeaVar</TT> that will receive the value of the
 * output parameter after the statement is called.
 *
 **************************************************************************/

    private void registerOutInt(final int     index,
                                final TeaVar var)
        throws SQLException {

        _callStat.registerOutParameter(index, Types.INTEGER);
        _outList.add(new SOutParameterInt(index, var));
    }





//* 
//* <TeaMethod name="registerFloat"
//*            arguments="index symbol"
//*                className="TCallableStatement">
//* 
//* <Overview>
//* Register a variable to receive the value of an float output
//* parameter.
//* </Overview>
//* 
//* <Parameter name="index">
//* The index of a parameter in the SQL statement that will be used
//* as an output parameter.
//* </Parameter>
//* 
//* <Parameter name="symbol">
//* Symbol identifying a variable in the current context.
//* </Parameter>
//*
//* <Returns>
//* The same object for which this method was called.
//* </Returns>
//* 
//* <Description>
//* Registers a variable that will receive the value of a
//* float parameter when the statement gets executed. The value will
//* be an floating point object. The variable
//* is identifyed by the <Arg name="symbol"/> argument. That
//* variable must alread exist in the current context.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "registerFloat" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object registerFloat(final TeaFunction obj,
                                final TeaContext     context,
                                final Object[]    args)
        throws TeaRunException {

        if ( args.length != 4 ) {
            throw new TeaNumArgException(args, "index symbol");
        }

        int     index = Args.getInt(args,2).intValue();
        TeaVar var   = context.getVarObject(Args.getSymbol(args,3));

        try {
            registerOutFloat(index, var);
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }
        
        return obj;
    }





/**************************************************************************
 *
 * Registers an output parameter that is a float value. When
 * <TT>query()</TT> or <TT>update()</TT> are called the contents of
 * <TT>var</TT> will contain the value of the associated output
 * parameter of the callable statement.
 *
 * @param index The index of the output parameter. The first is 1, the
 * second 2, ...
 *
 * @param var The <TT>TeaVar</TT> that will receive the value of the
 * output parameter after the statement is called.
 *
 **************************************************************************/

    private void registerOutFloat(final int     index,
                                  final TeaVar var)
        throws SQLException {

        _callStat.registerOutParameter(index, Types.DOUBLE);
        _outList.add(new SOutParameterFloat(index, var));
    }





//* 
//* <TeaMethod name="registerDate"
//*            arguments="index symbol"
//*                className="TCallableStatement">
//* 
//* <Overview>
//* Register a variable to receive the value of an date output
//* parameter.
//* </Overview>
//* 
//* <Parameter name="index">
//* The index of a parameter in the SQL statement that will be used
//* as an output parameter.
//* </Parameter>
//* 
//* <Parameter name="symbol">
//* Symbol identifying a variable in the current context.
//* </Parameter>
//*
//* <Returns>
//* The same object for which this method was called.
//* </Returns>
//* 
//* <Description>
//* Registers a variable that will receive the value of an
//* output parameter when the statement gets executed. The value will
//* be a <ClassRef name="TDate"/> object. The variable
//* is identifyed by the <Arg name="symbol"/> argument. That
//* variable must alread be known in the current context.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "registerInt" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object registerDate(final TeaFunction obj,
                               final TeaContext     context,
                               final Object[]    args)
        throws TeaRunException {

        if ( args.length != 4 ) {
            throw new TeaNumArgException(args, "index symbol");
        }

        int        index   = Args.getInt(args,2).intValue();
        TeaSymbol varName = Args.getSymbol(args,3);
        TeaVar    var     = context.getVarObject(varName);

        try {
            registerOutDate(index, var, context);
        } catch ( SQLException e ) {
            throw new TeaRunException(e);
        }
        
        return obj;
    }





/**************************************************************************
 *
 * Registers an output parameter that is a date. When <TT>query()</TT>
 * or <TT>update()</TT> are called the contents of <TT>var</TT> will
 * contain the value of the associated output parameter of the
 * callable statement.
 *
 * @param index The index of the output parameter. The first is 1, the
 * second 2, ...
 *
 * @param var The <TT>TeaVar</TT> that will receive the value of the
 * output parameter after the statement is called.
 *
 **************************************************************************/

    private void registerOutDate(final int      index,
                                 final TeaVar   var,
                                 final TeaContext context)
        throws SQLException {

        _callStat.registerOutParameter(index, Types.TIMESTAMP);
        _outList.add(new SOutParameterDate(index, var, context));
    }





//* 
//* <TeaMethod name="fetchOutParameters"
//*                className="TCallableStatement">
//* 
//* <Overview>
//* Fetches the values of the output parameters.
//* </Overview>
//*
//* <Returns>
//* The same object for which this method was called.
//* </Returns>
//* 
//* <Description>
//* Fetches the values of the output parameters. The variables registered
//* to receive the values of output parameters will be set with the
//* corresponding values.
//* 
//* <P>This method is supposed to be called only after all the result
//* sets resulting from the stored procedure invocation have been
//* fetched with calls to
//* <MethodRef tosClass="TStatement" name="getResultSet"/>.</P>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "fetchOutputParameters" TOS method.
 *
 **************************************************************************/

    public Object fetchOutParameters(final TeaFunction obj,
                                     final TeaContext     context,
                                     final Object[]    args)
        throws TeaException {

         try {
            for ( SOutParameter outParam : _outList ) {
                outParam.retrieve(_callStat);
            }
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

    private static abstract class SOutParameter
        extends Object {





        protected int     _index = 0;
        protected TeaVar _var   = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        SOutParameter(final int     index,
                      final TeaVar var) {

            _index = index;
            _var   = var;
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public abstract void retrieve(CallableStatement stat)
            throws TeaException,
                   SQLException;


    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static final class SOutParameterString
        extends SOutParameter {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public SOutParameterString(final int     index,
                                   final TeaVar var) {

            super(index, var);
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public void retrieve(final CallableStatement stat)
            throws SQLException {

            _var.set(stat.getString(_index));
        }


    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static final class SOutParameterInt
        extends SOutParameter {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public SOutParameterInt(final int     index,
                                final TeaVar var) {

            super(index, var);
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public void retrieve(final CallableStatement stat)
            throws SQLException {

            _var.set(Integer.valueOf(stat.getInt(_index)));
        }


    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static final class SOutParameterFloat
        extends SOutParameter {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public SOutParameterFloat(final int     index,
                                  final TeaVar var) {

            super(index, var);
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public void retrieve(final CallableStatement stat)
            throws SQLException {

            _var.set(new Double(stat.getDouble(_index)));
        }


    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static final class SOutParameterDate
        extends SOutParameter {





        private TeaContext _context = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public SOutParameterDate(final int      index,
                                 final TeaVar  var,
                                 final TeaContext context) {

            super(index, var);

            _context = context;
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public void retrieve(final CallableStatement stat)
            throws TeaException,
                   SQLException {

            Date  date   = stat.getTimestamp(_index);
            SDate result = null;

            if ( date != null ) {
                result = SDate.newInstance(_context);
                date   = new Date(date.getTime());
                result.initFromDate(date);
            }

            if ( result != null ) {
                _var.set(result);
            } else {
                _var.set(TeaNull.NULL);
            }
        }


    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

