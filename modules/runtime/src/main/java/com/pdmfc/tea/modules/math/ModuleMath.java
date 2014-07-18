/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.math;

import java.util.Random;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.Args;
import com.pdmfc.tea.TeaArithmeticException;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaBlock;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaSymbol;
import com.pdmfc.tea.TeaVar;
import com.pdmfc.tea.TeaNumArgException;
import com.pdmfc.tea.TeaTypeException;
import com.pdmfc.tea.Types;
import com.pdmfc.tea.TeaEnvironment;
import com.pdmfc.tea.TeaFunctionImplementor;
import com.pdmfc.tea.TeaModule;





//*
//* <TeaModule name="tea.math">
//* 
//* <Overview>
//* Numeric functions.
//* </Overview>
//*
//* <Description>
//* Functions for numeric processing.
//* </Description>
//*
//* </TeaModule>
//*

/**************************************************************************
 *
 * Tea package providing math related commands.
 *
 **************************************************************************/

public final class ModuleMath
    extends Object
    implements TeaModule {





    //The value zero.
    private static final Integer ZERO = Integer.valueOf(0);

    // The value one.
    private static final Integer ONE = Integer.valueOf(1);

    
    /**
     *
     * Types of numeric comparisons.
     *
     */
    private enum Comparison {
        EQ,
        NE,
        LT,
        LE,
        GT,
        GE
    };


    /**
     *
     * Types of arithmetic operations.
     *
     */
    private enum ArithOp {
        ADD,
        SUB,
        MUL,
        DIV
    };

    private static final SComparator C_GT = new SGt();
    private static final SComparator C_LT = new SLt();

    // Used by the implementation of the Tea "rand-int" function.
    private Random _generator = new Random();






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public ModuleMath() {

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

        // Nothing to do. The functions provided by this module are
        // all implemented as methods of this class with the
        // TeaFunctionImplementor annotation.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void end() {

        // Nothing to do.
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

        // Nothing to do.
    }





//* 
//* <TeaFunction name="=="
//*              arguments="[value1 ...]"
//*              module="tea.math">
//*
//* <Overview>
//* Compares two numeric values for equality.
//* </Overview>
//*
//* <Parameter name="value1">
//* A numeric object.
//* </Parameter>
//*
//* <Returns>
//* True if its numeric arguments all represent the same value.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>==</code> function.
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

    @TeaFunctionImplementor("==")
    public static Object functionEq(final TeaFunction func,
                                    final TeaContext  context,
                                    final Object[]    args)
        throws TeaException {

        return compare(Comparison.EQ, func, context, args);
    }





//* 
//* <TeaFunction name="!="
//*              arguments="value1 value2"
//*              module="tea.math">
//*
//* <Overview>
//* Compares two numeric values for non-equality.
//* </Overview>
//*
//* <Parameter name="value1">
//* A numeric object.
//* </Parameter>
//*
//* <Parameter name="value2">
//* A numeric object.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="value1"/> is different from <Arg name="value2"/>.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>!=</code> function.
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

    @TeaFunctionImplementor("!=")
    public static Object functionNe(final TeaFunction func,
                                    final TeaContext  context,
                                    final Object[]    args)
        throws TeaException {

        return compare(Comparison.NE, func, context, args);
    }





//* 
//* <TeaFunction name="&gt;"
//*              arguments="value1 value2"
//*              module="tea.math">
//*
//* <Overview>
//* Compares two numeric values.
//* </Overview>
//*
//* <Parameter name="value1">
//* A numeric object.
//* </Parameter>
//*
//* <Parameter name="value2">
//* A numeric object.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="value1"/> is larger than <Arg name="value2"/>.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>&gt;</code> function.
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

    @TeaFunctionImplementor(">")
    public static Object functionGt(final TeaFunction func,
                                    final TeaContext  context,
                                    final Object[]    args)
        throws TeaException {

        return compare(Comparison.GT, func, context, args);
    }





//* 
//* <TeaFunction name="&gt;="
//*              arguments="value1 value2"
//*              module="tea.math">
//*
//* <Overview>
//* Compares two numeric values.
//* </Overview>
//*
//* <Parameter name="value1">
//* A numeric object.
//* </Parameter>
//*
//* <Parameter name="value2">
//* A numeric object.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="value1"/> is equal to or larger than
//* <Arg name="value2"/>.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>&gt;=</code> function.
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

    @TeaFunctionImplementor(">=")
    public static Object functionGe(final TeaFunction func,
                                    final TeaContext  context,
                                    final Object[]    args)
        throws TeaException {

        return compare(Comparison.GE, func, context, args);
    }





//* 
//* <TeaFunction name="&lt;"
//*              arguments="value1 value2"
//*              module="tea.math">
//*
//* <Overview>
//* Compares two numeric values.
//* </Overview>
//*
//* <Parameter name="value1">
//* A numeric object.
//* </Parameter>
//*
//* <Parameter name="value2">
//* A numeric object.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="value1"/> is smaller than <Arg name="value2"/>.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>&lt;</code> function.
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

    @TeaFunctionImplementor("<")
    public static Object functionLt(final TeaFunction func,
                                    final TeaContext  context,
                                    final Object[]    args)
        throws TeaException {

        return compare(Comparison.LT, func, context, args);
    }





//* 
//* <TeaFunction name="&lt;="
//*              arguments="value1 value2"
//*              module="tea.math">
//*
//* <Overview>
//* Compares two numeric values.
//* </Overview>
//*
//* <Parameter name="value1">
//* A numeric object.
//* </Parameter>
//*
//* <Parameter name="value2">
//* A numeric object.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="value1"/> is equal to or smaller than
//* <Arg name="value2"/>.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>&lt;=</code> function.
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

    @TeaFunctionImplementor("<=")
    public static Object functionLe(final TeaFunction func,
                                    final TeaContext  context,
                                    final Object[]    args)
        throws TeaException {

        return compare(Comparison.LE, func, context, args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object compare(final Comparison   compOp,
                                  final TeaFunction func,
                                  final TeaContext  context,
                                  final Object[]    args)
        throws TeaException {

        Args.checkAtLeast(args, 3, "num1 num2 ...");

        Object  op1         = args[1];
        boolean op1IsInt    = op1 instanceof Integer;
        boolean op1IsFloat  = op1 instanceof Double;
        int     op1IntVal   = op1IsInt ? ((Integer)op1).intValue() : 0;
        double  op1FloatVal = op1IsFloat ? ((Double)op1).doubleValue() : 0.0;

        if ( !op1IsInt && !op1IsFloat ) {
            throw new TeaTypeException(args, 1, "numeric");
        }

        for ( int i=2; i<args.length; i++ ) {
            boolean result;
            Object   op2        = args[i];
            boolean op2IsInt    = op2 instanceof Integer;
            boolean op2IsFloat  = op2 instanceof Double;
            int     op2IntVal   = op2IsInt ? ((Integer)op2).intValue() : 0;
            double  op2FloatVal = op2IsFloat ? ((Double)op2).doubleValue():0.0;

            if ( !op2IsInt && !op2IsFloat ) {
                throw new TeaTypeException(args, i, "numeric");
            }
            if ( op1IsInt ) {
                if ( op2IsInt ) {
                    result = compareI(compOp, op1IntVal, op2IntVal);
                } else {
                    result = compareF(compOp, op1IntVal, op2FloatVal);
                }
            } else {
                if ( op2IsInt ) {
                    result = compareF(compOp, op1FloatVal, op2IntVal);
                } else {
                    result = compareF(compOp, op1FloatVal, op2FloatVal);
                }
            }
            if ( result == false ) {
                return Boolean.FALSE;
            }
            op1         = op2;
            op1IsInt    = op2IsInt;
            op1IntVal   = op2IntVal;
            op1FloatVal = op2FloatVal;
        }

        return Boolean.TRUE;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static boolean compareI(final Comparison compOp,
                                    final int        op1,
                                    final int        op2) {

        switch ( compOp ) {
        case EQ :
            return op1 == op2;
        case NE :
            return op1 != op2;
        case GT :
            return op1 > op2;
        case GE :
            return op1 >= op2;
        case LT :
            return op1 < op2;
        case LE :
            return op1 <= op2;
        default :
            throw new IllegalArgumentException(compOp.toString());
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static boolean compareF(final Comparison compOp,
                                    final double     op1,
                                    final double     op2) {

        switch ( compOp ) {
        case EQ :
            return op1 == op2;
        case NE :
            return op1 != op2;
        case GT :
            return op1 > op2;
        case GE :
            return op1 >= op2;
        case LT :
            return op1 < op2;
        case LE :
            return op1 <= op2;
        default :
            throw new IllegalArgumentException(compOp.toString());
        }
    }





//* 
//* <TeaFunction name="+"
//*              arguments="[value ...]"
//*              module="tea.math">
//*
//* <Overview>
//* Calculates the sum of its arguments.
//* </Overview>
//*
//* <Parameter name="value">
//* A numeric object, either integer or real.
//* </Parameter>
//*
//* <Returns>
//* A numeric object representing the sum of its arguments.
//* </Returns>
//*
//* <Description>
//* If all of the arguments are integer objects then <Arg name="+"/>
//* returns an integer object. If any of the arguments is a real
//* object then it returns a float object. If no arguments are given
//* then it returns the zero value.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>+</code> function.
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

    @TeaFunctionImplementor("+")
    public static Object functionAdd(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        return arithmOp(ArithOp.ADD, func, context, args);
    }





//* 
//* <TeaFunction name="-"
//*              arguments="[value ...]"
//*              module="tea.math">
//*
//* <Overview>
//* Calculates the difference between two values.
//* </Overview>
//*
//* <Parameter name="value">
//* A numeric object, either integer or real.
//* </Parameter>
//*
//* <Returns>
//* A numeric object representing the difference between the first
//* argument and the sum of the rest of the arguments.
//* </Returns>
//*
//* <Description>
//* If all of the arguments are integer objects then <Arg name="-"/>
//* returns an integer object. If any of the arguments is a real
//* object then it returns a float object. If no arguments are given
//* then it returns the zero value.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>-</code> function.
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

    @TeaFunctionImplementor("-")
    public static Object functionSub(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        return arithmOp(ArithOp.SUB, func, context, args);
    }





//* 
//* <TeaFunction name="*"
//*              arguments="[value ...]"
//*              module="tea.math">
//*
//* <Overview>
//* Calculates the product of a set of values.
//* </Overview>
//*
//* <Parameter name="value">
//* A numeric object, either integer or real.
//* </Parameter>
//*
//* <Returns>
//* A numeric object representing the product of its arguments.
//* </Returns>
//*
//* <Description>
//* If all of the arguments are integer objects then <Arg name="*"/>
//* returns an integer object. If any of the arguments is a real
//* object then it returns a float object. If no arguments are given
//* then it returns one.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>*</code> function.
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

    @TeaFunctionImplementor("*")
    public static Object functionMul(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        return arithmOp(ArithOp.MUL, func, context, args);
    }





//* 
//* <TeaFunction name="/"
//*              arguments="[value ...]"
//*              module="tea.math">
//*
//* <Overview>
//* Calculates the quocient of two values.
//* </Overview>
//*
//* <Parameter name="value">
//* A numeric object, either integer or real.
//* </Parameter>
//*
//* <Returns>
//* A numeric object representing the quocient between the first
//* argument and the product of all other arguments.
//* </Returns>
//*
//* <Description>
//* If all of the arguments are integer objects then <Arg name="/"/>
//* returns an integer object. If any of the arguments is a real
//* object then it returns a float object. If no arguments are given
//* then it returns one.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>/</code> function.
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

    @TeaFunctionImplementor("/")
    public static Object functionDiv(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        return arithmOp(ArithOp.DIV, func, context, args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object arithmOp(final ArithOp      op,
                                   final TeaFunction func,
                                   final TeaContext  context,
                                   final Object[]    args)
        throws TeaException {

        if ( args.length < 2 ) {
            return opNullValue(op);
        }
        if ( args.length == 2 ) {
            return unaryOp(op, args);
        }

        Object operand = args[1];

        try {
            if ( operand instanceof Integer ) {
                return calcIntOp(op, ((Integer)operand).intValue(), args, 2);
            }
            if ( operand instanceof Double ) {
                return calcFloatOp(op, ((Double)operand).doubleValue(),args,2);
            }
        } catch ( ArithmeticException e ) {
            TeaArithmeticException.raise(args, e);
        }
        
        throw new TeaTypeException(args, 1, "numeric");
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object calcIntOp(final ArithOp  op,
                                    final int      input,
                                    final Object[] args,
                                    final int      first)
        throws TeaException {

        int result = input;

        for ( int i=first; i<args.length; i++ ) {
            Object operand = args[i];

            if ( operand instanceof Integer ) {
                result = doOp(op, result, ((Integer)operand).intValue());
            } else {
                if ( operand instanceof Double ) {
                    return calcFloatOp(op,
                                       doOp(op,
                                            (double)result,
                                            ((Double)operand).doubleValue()),
                                       args,
                                       i+1);
                } else {
                    throw new TeaTypeException(args, i, "numeric");
                }
            }
        }

        return Integer.valueOf(result);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object calcFloatOp(final ArithOp  op,
                                      final double   input,
                                      final Object[] args,
                                      final int      first)
        throws TeaException {

        double result = input;

        for ( int i=first; i<args.length; i++ ) {
            Object operand = args[i];

            if ( operand instanceof Number ) {
                result = doOp(op, result, ((Number)operand).doubleValue());
            } else {
                throw new TeaTypeException(args, i, "numeric");
            }
        }

        return new Double(result);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static int doOp(final ArithOp operator,
                            final int     operand1,
                            final int     operand2) {

        switch ( operator ) {
        case ADD :
            return operand1 + operand2;
        case SUB :
            return operand1 - operand2;
        case MUL :
            return operand1 * operand2;
        case DIV :
            return operand1 / operand2;
        default :
            throw new IllegalArgumentException(operator.toString());
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static double doOp(final ArithOp operator,
                               final double  operand1,
                               final double  operand2) {

        switch ( operator ) {
        case ADD :
            return operand1 + operand2;
        case SUB :
            return operand1 - operand2;
        case MUL :
            return operand1 * operand2;
        case DIV :
            return operand1 / operand2;
        default :
            throw new IllegalArgumentException(operator.toString());
        }
    }





/**************************************************************************
 *
 * Returns the null element of the selected arithmetic operation.
 * 
 * @return
 *    The null element of the selected arithmetic operation.
 *
 **************************************************************************/

    private static Object opNullValue(final ArithOp operator) {

        switch ( operator ) {
        case ADD :
            return ZERO;
        case SUB :
            return ZERO;
        case MUL :
            return ONE;
        case DIV :
            return ONE;
        default :
            throw new IllegalArgumentException(operator.toString());
        }
    }





/**************************************************************************
 *
 * Performs the unary operation on the object received as argument.
 *
 * @param arg0 The zeroth actual argument of the procedure. It is only
 * used to compose the error message, if necessary.
 *
 * @param op The operand on which the unary operation will be
 * performed.
 *
 * @return The result of the unary operation. It will be of the same
 * type as <code>op</code>.
 *
 * @exception TeaException Throws if <code>op</code> is neither an
 * int nor a float and the selected arithmetic operation is "minus".
 *
 **************************************************************************/

    private static Object unaryOp(final ArithOp  operator,
                                  final Object[] args)
        throws TeaException {

        Object result  = null;
        Object operand = args[1];

        if ( operand instanceof Integer ) {
            if ( operator == ArithOp.SUB ) {
                result = Integer.valueOf(-((Integer)operand).intValue());
            } else {
                result = operand;
            }
        } else if ( operand instanceof Double ) {
            if ( operator == ArithOp.SUB ) {
                result = new Double(-((Double)operand).doubleValue());
            } else {
                result = operand;
            }
        } else {
            throw new TeaTypeException(args, 1, "float or an int");
        }

        return result;
    }





//* 
//* <TeaFunction name="%"
//*              arguments="dividend divisor"
//*              module="tea.math">
//*
//* <Overview>
//* Calculates the remainder of an integer division.
//* </Overview>
//*
//* <Parameter name="dividend">
//* A numeric object.
//* </Parameter>
//*
//* <Parameter name="divisor">
//* A numeric object.
//* </Parameter>
//*
//* <Returns>
//* An integer representing the remainder of the integer division
//* between <Arg name="dividend"/> and <Arg name="divisor"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>%</code> function.
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

    @TeaFunctionImplementor("%")
    public static Object functionMod(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        Args.checkCount(args, 3, "dividend divisor");

        int dividend = Args.getInt(args,1).intValue();
        int divisor  = Args.getInt(args,2).intValue();
        int result   = 0;

        try {
            result = dividend % divisor;
        } catch ( ArithmeticException e ) {
            TeaArithmeticException.raise(args, e);
        }

        return Integer.valueOf(result);
    }





//* 
//* <TeaFunction name="and"
//*              arguments="[arg ...]"
//*              module="tea.math">
//*
//* <Overview>
//* Calculates the logical conjunction of a set of boolean values.
//* </Overview>
//*
//* <Parameter name="arg">
//* A boolean value or a block that evaluates to a boolean value.
//* </Parameter>
//*
//* <Returns>
//* A boolean value representing the logical conjunction of its
//* arguments.
//* </Returns>
//*
//* <Description>
//* The <Func name="and"/> function can receive code blocks as arguments. The
//* arguments are inspected from left to right. If one argument is a code
//* block then it is evaluated and the result must be a boolean object. If
//* that code bock evaluated to false then no more arguements are
//* checked. That means that if there are more arguments that are code
//* blocks then they will not be evaluated. This is usefull if you have a
//* condition that may only be evaluated if a previous condition is
//* true. For example:
//* 
//* <Code>
//* if { and {not-null? $x} {== $x 0} {
//*     do-whatever
//* }
//* </Code>
//* the second condition will only be evaluated if the first condition is
//* true. The use of code blocks as arguments insures that the <Func
//* name="and"/> function behaves the intended way.
//* 
//* <P>
//* If the <Func name="and"/> function is called with no arguments it
//* will return true.
//* </P>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>and</code> function.
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

    @TeaFunctionImplementor("and")
    public static Object functionAnd(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        for ( int i=1, count=args.length; i<count; i++ ) {
            Object obj = args[i];

            if ( obj instanceof TeaBlock ) {
                obj = ((TeaBlock)obj).exec();
            }
            if ( obj instanceof Boolean ) {
                if ( !((Boolean)obj).booleanValue() ) {
                    // One of the arguments evaluated to false. We
                    // will return from this function and will not
                    // evaluate the remaining arguments.
                    return obj;
                }
            } else {
                throw new TeaTypeException(args, i, "bool or a block");
            }
        }

        return Boolean.TRUE;
    }





//* 
//* <TeaFunction name="or"
//*              arguments="[arg ...]"
//*              module="tea.math">
//*
//* <Overview>
//* Calculates the logical disjunction of a set of boolean values.
//* </Overview>
//*
//* <Parameter name="arg">
//* A boolean object or a block that evaluates to a boolean value.
//* </Parameter>
//*
//* <Returns>
//* A boolean value representing the logical disjunction of its
//* arguments.
//* </Returns>
//*
//* <Description>
//* The arguments are inspected from left to right. The inspection ends
//* as soon as one of the arguments evaluates to true. If an argument
//* is a block then that block is evaluated.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>or</code> function.
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

    @TeaFunctionImplementor("or")
    public static Object functionOr(final TeaFunction func,
                                    final TeaContext  context,
                                    final Object[]    args)
        throws TeaException {

        for ( int i=1, count=args.length; i<count; i++ ) {
            Object obj = args[i];

            if ( obj instanceof TeaBlock ) {
                obj = ((TeaBlock)obj).exec();
            }
            if ( obj instanceof Boolean ) {
                if ( ((Boolean)obj).booleanValue() ) {
                    // One of the arguments evaluated to true. We
                    // will return from this function and will not
                    // evaluate the remaining arguments.
                    return Boolean.TRUE;
                }
            } else {
                throw new TeaTypeException(args, i, "bool or a block");
            }
        }

        return Boolean.FALSE;
    }





//* 
//* <TeaFunction name="not"
//*                 arguments="aBoolean"
//*             module="tea.math">
//*
//* <Overview>
//* Calculates the logical complement of a boolean value.
//* </Overview>
//*
//* <Parameter name="aBoolean">
//* Boolean object.
//* </Parameter>
//*
//* <Returns>
//* A boolean object representing the logical complement of
//* <Arg name="aBoolean"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>not</code> function.
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

    @TeaFunctionImplementor("not")
    public static Object functionNot(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "boolean");
        }

        boolean operand = Args.getBoolean(args,1).booleanValue();
        Boolean result  = operand ? Boolean.FALSE : Boolean.TRUE;

        return result;
    }





//* 
//* <TeaFunction name="abs"
//*                 arguments="aValue"
//*             module="tea.math">
//*
//* <Overview>
//* Calculates the absolute value of its argument.
//* </Overview>
//*
//* <Parameter name="aValue">
//* A numeric object.
//* </Parameter>
//*
//* <Returns>
//* A numeric object represeting the absolute value of
//* <Arg name="aValue"/>. If <Arg name="aValue"/> is an integer then
//* the returned value is also an integer. If <Arg name="aValue"/>
//* is a real value then the returned value is also real.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>abs</code> function.
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

    @TeaFunctionImplementor("abs")
    public static Object functionAbs(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "value");
        }
        Object arg = args[1];

        if ( arg instanceof Integer ) {
            return Integer.valueOf(Math.abs(((Integer)arg).intValue()));
        }
        if ( arg instanceof Double ) {
            return new Double(Math.abs(((Double)arg).floatValue()));
        }

        throw new TeaTypeException(args, 1, "int or a float");
    }





//* 
//* <TeaFunction name="round"
//*                  arguments="aValue"
//*              module="tea.math">
//*
//* <Overview>
//* Calculates the nearest integer to a numeric value.
//* </Overview>
//*
//* <Parameter name="aValue">
//* A numeric object.
//* </Parameter>
//*
//* <Returns>
//* An integer object representing the nearest integer to
//* <Arg name="aValue"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>round</code> function.
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

    @TeaFunctionImplementor("round")
    public static Object functionRound(final TeaFunction func,
                                       final TeaContext  context,
                                       final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "value");
        }

        double operand = Args.getFloat(args, 1).doubleValue();
        int    result  = (int)Math.round(operand);

        return Integer.valueOf(result);
    }





//* 
//* <TeaFunction name="floor"
//*                 arguments="aValue"
//*             module="tea.math">
//*
//* <Overview>
//* Calculates the largest integer not larger than a value.
//* </Overview>
//*
//* <Parameter name="aValue">
//* A numeric object.
//* </Parameter>
//*
//* <Returns>
//* An integer object representing the largest integer not larger than
//* <Arg name="aValue"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>floor</code> function.
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

    @TeaFunctionImplementor("floor")
    public static Object functionFloor(final TeaFunction func,
                                       final TeaContext  context,
                                       final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "value");
        }

        double operand = Args.getFloat(args, 1).doubleValue();
        int    result  = (int)Math.floor(operand);

        return Integer.valueOf(result);
    }





//* 
//* <TeaFunction name="ceil"
//*                 arguments="aValue"
//*             module="tea.math">
//*
//* <Overview>
//* Calculates the smallest integer larger than or equal to a given value.
//* </Overview>
//*
//* <Parameter name="aValue">
//* A numeric object.
//* </Parameter>
//*
//* <Returns>
//* An integer object representing the smallest integer larger than or
//* equal to <Arg name="aValue"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>ceil</code> function.
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

    @TeaFunctionImplementor("ceil")
    public static Object functionCeil(final TeaFunction func,
                                      final TeaContext  context,
                                      final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "value");
        }

        double operand = Args.getNumber(args, 1).doubleValue();
        int    result  = (int)Math.ceil(operand);

        return Integer.valueOf(result);
    }





//* 
//* <TeaFunction name="sqrt"
//*                  arguments="aValue"
//*              module="tea.math">
//*
//* <Overview>
//* Calculates the square root of a given value.
//* </Overview>
//*
//* <Parameter name="aValue">
//* A numeric object.
//* </Parameter>
//*
//* <Returns>
//* A float object representing the square root of <Arg name="aValue"/>.
//* </Returns>
//*
//* <Description>
//* If the argument is negative a runtime error is generated.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>sqrt</code> function.
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

    @TeaFunctionImplementor("sqrt")
    public static Object functionSqrt(final TeaFunction func,
                                      final TeaContext  context,
                                      final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "value");
        }

        double result = 0;
        
        try {
            result = Math.sqrt(Args.getNumber(args,1).doubleValue());
        } catch (ArithmeticException e) {
            TeaArithmeticException.raise(args, e);
        }

        return new Double(result);
    }





/**************************************************************************
 *
 * Implements the Tea <code>min</code> function.
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

    @TeaFunctionImplementor("min")
    public static Object functionMin(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        return findExtreme(C_LT, func, context, args);
    }





/**************************************************************************
 *
 * Implements the Tea <code>max</code> function.
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

    @TeaFunctionImplementor("max")
    public static Object functionMax(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        return findExtreme(C_GT, func, context, args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object findExtreme(final SComparator  comparator,
                                      final TeaFunction func,
                                      final TeaContext  context,
                                      final Object[]    args)
        throws TeaException {

        if ( args.length < 2 ) {
            throw new TeaNumArgException(args, "value ...");
        }
        Object valueObj = args[1];

        if ( valueObj instanceof Integer ) {
            int operand1 = ((Integer)valueObj).intValue();
            return calcExtremeInt(comparator, operand1, args, 2);
        }
        if ( valueObj instanceof Double ) {
            double operand1 = ((Double)valueObj).doubleValue();
            return calcExtremeDouble(comparator, operand1, args, 2);
        }
        throw new TeaTypeException(args, 1, "int or a float");
    }





/**************************************************************************
 *
 * @param firstIndex
 *    The index of the first elementof <TT>args</TT> to inspect.
 *
 **************************************************************************/

    private static Object calcExtremeInt(final SComparator comparator,
                                         final int         input,
                                         final Object[]    args,
                                         final int         firstIndex)
        throws TeaTypeException{

        int min = input;

        for ( int i=args.length; (--i)>=firstIndex; ) {
            Object valueObj = args[i];
            
            if ( valueObj instanceof Integer ) {
                int value = ((Integer)valueObj).intValue();
                
                if ( comparator.compareInt(value, min) ) {
                    min = value;
                }
                continue;
            }
            if ( valueObj instanceof Double ) {
                return calcExtremeDouble(comparator, min, args, i);
            }
            throw new TeaTypeException(args, i, "int or a float");
        }

        return Integer.valueOf(min);
    }





/**************************************************************************
 *
 * @param firstIndex
 *    The index of the first elementof <TT>args</TT> to inspect.
 *
 **************************************************************************/

    private static Object calcExtremeDouble(final SComparator comparator,
                                            final double      input,
                                            final Object[]    args,
                                            final int         firstIndex)
        throws TeaTypeException{

        double min = input;

        for ( int i=args.length; (--i)>=firstIndex; ) {
            Object valueObj = args[i];

            if ( valueObj instanceof Number ) {
                double value = ((Number)valueObj).doubleValue();

                if ( comparator.compareDouble(value, min) ) {
                    min = value;
                }
            } else {
                throw new TeaTypeException(args, i, "int or a float");
            }
        }
        return new Double(min);
    }





//* 
//* <TeaFunction name="int"
//*              arguments="[aSymbol] aValue"
//*              module="tea.math">
//*
//* <Overview>
//* Creates a new variable containing a numeric object.
//* </Overview>
//*
//* <Parameter name="aSymbol">
//* A symbol object identifying the variable that will be created in the
//* current context.
//* </Parameter>
//*
//* <Parameter name="aValue">
//* A numeric object.
//* </Parameter>
//*
//* <Returns>
//* The result of converting the given argument to an integer value.
//* </Returns>
//*
//* <Description>
//* Creates a new variable in the current context and initializes it
//* with <Arg name="aValue"/>.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>int</code> function.
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

    @TeaFunctionImplementor("int")
    public static Object functionInt(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        if ( args.length == 3 ) {
            TeaSymbol symbol = Args.getSymbol(args, 1);
            int        value  = Args.getNumber(args, 2).intValue();
            Integer    theInt = Integer.valueOf(value);

            context.newVar(symbol, theInt);

            return theInt;
        }
        if ( args.length == 2 ) {
            return Integer.valueOf(Args.getNumber(args, 1).intValue());
        }

        throw new TeaNumArgException(args, "[symbol] value");
    }





//* 
//* <TeaFunction name="rand-int"
//*             module="tea.math">
//*
//* <Overview>
//* Generates an integer random value.
//* </Overview>
//*
//* <Returns>
//* An integer object representing a randomly chosen value.
//* </Returns>
//*
//* <Description>
//* The sequence of values returned by this function is always different
//* for each execution of the process that uses this function.
//* 
//* <p>All 2^32 possible int values are produced with (approximately)
//* equal probability. This means negative values may also be
//* generated.</p>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>rand-int</code> function.
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

    @TeaFunctionImplementor("rand-int")
    public Object functionRandInt(final TeaFunction func,
                                  final TeaContext  context,
                                  final Object[]    args)
        throws TeaException {

        return Integer.valueOf(_generator.nextInt());
    }





//* 
//* <TeaFunction name="="
//*                 arguments="symbol value"
//*             module="tea.math">
//*
//* <Overview>
//* Sets the contents of a variable to a numeric value.
//* </Overview>
//*
//* <Parameter name="symbol">
//* Identifies the variable in the current context to be modified.
//* </Parameter>
//*
//* <Parameter name="value">
//* The new value for the variable. It must be a numeric object.
//* </Parameter>
//*
//* <Returns>
//* A reference to the <Arg name="value"/> argument.
//* </Returns>
//*
//* <Description>
//* Modifies the contents of the variable identified by 
//* <Arg name="symbol"/> in the current context. This function
//* is similar to the <FuncRef name="set!"/> function, but the
//* values must be numeric.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>=</code> function.
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

    @TeaFunctionImplementor("=")
    public static Object functionSetValue(final TeaFunction func,
                                          final TeaContext  context,
                                          final Object[]    args)
        throws TeaException {

      if ( args.length != 3 ) {
         throw new TeaNumArgException(args, "symbol value");
      }

      TeaSymbol symbol = Args.getSymbol(args, 1);
      TeaVar    var    = context.getVarObject(symbol);
      Number     val    = Args.getNumber(args, 2);

      var.set(val);

      return val;
    }





//* 
//* <TeaFunction name="+="
//*                 arguments="symbol value"
//*             module="tea.math">
//*
//* <Overview>
//* Modifies the value stored in a variable by adding it a value.
//* </Overview>
//*
//* <Parameter name="symbol">
//* A symbol object identifying a variable in the current context. The
//* variable must contain a numeric value.
//* </Parameter>
//*
//* <Parameter name="value">
//* A numeric object represeting the value to be added to
//* <Arg name="symbol"/>.
//* </Parameter>
//*
//* <Returns>
//* The new value of the variable.
//* </Returns>
//*
//* <Description>
//* Modifies the contents of the variable identified by
//* <Arg name="symbol"/> by adding
//* <Arg name="value"/> to the value it had previously stored.
//* The variable binded to <Arg name="symbol"/> is expected to
//* contain a numeric object.
//* <P>
//* The <Func name="+="/> is a short way to write
//* </P>
//* <Code>
//*     set! symbol [+ $symbol $value]
//* </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>+=</code> function.
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

    @TeaFunctionImplementor("+=")
    public static Object functionIncBy(final TeaFunction func,
                                       final TeaContext  context,
                                       final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "symbol value");
        }

        TeaSymbol symbol  = Args.getSymbol(args, 1);
        TeaVar    var     = context.getVarObject(symbol);
        Object     value   = var.get();
        Number     incrVal = Args.getNumber(args, 2);

        if ( value instanceof Integer ) {
            int x = ((Integer)value).intValue() + incrVal.intValue();
            value = Integer.valueOf(x);
        } else if ( value instanceof Double ) {
            double x = ((Double)value).doubleValue() + incrVal.doubleValue();
            value = new Double(x);
        } else {
            String   msg     =
                "Variable \"{0}\" must contain a numeric, not a {1}";
            Object[] fmtArgs = { symbol, Types.getTypeName(value) };
            throw new TeaException(msg, fmtArgs);
        }

        var.set(value);

        return value;
    }





//* 
//* <TeaFunction name="-="
//*                 arguments="symbol value"
//*             module="tea.math">
//*
//* <Overview>
//* Modifies the value stored in a variable by subtracting a given
//* value from it.
//* </Overview>
//*
//* <Parameter name="symbol">
//* A symbol object identifying a variable in the current context. The
//* variable must contain a numeric value.
//* </Parameter>
//*
//* <Parameter name="value">
//* A numeric object represeting the value to be decremented from
//* <Arg name="symbol"/>.
//* </Parameter>
//*
//* <Returns>
//* The new value of the variable.
//* </Returns>
//*
//* <Description>
//* Modifies the contents of the variable identified by
//* <Arg name="symbol"/> by subtracting
//* <Arg name="value"/> from the value it had previously stored.
//* The variable binded to <Arg name="symbol"/> is expected to
//* contain a numeric object.
//* <P>
//* The <Func name="-="/> is a short way to write
//* </P>
//* <Code>
//*     set! symbol [- $symbol $value]
//* </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>-=</code> function.
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

    @TeaFunctionImplementor("-=")
    public static Object functionDecBy(final TeaFunction func,
                                       final TeaContext  context,
                                       final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "symbol value");
        }

        TeaSymbol symbol  = Args.getSymbol(args, 1);
        TeaVar    var     = context.getVarObject(symbol);
        Object     value   = var.get();
        Number     incrVal = Args.getNumber(args, 2);

        if ( value instanceof Integer ) {
            int x = ((Integer)value).intValue() - incrVal.intValue();
            value = Integer.valueOf(x);
        } else if ( value instanceof Double ) {
            double x = ((Double)value).doubleValue() - incrVal.doubleValue();
            value = new Double(x);
        } else {
            String   msg     =
                "Variable \"{0}\" must contain a numeric, not a {1}";
            Object[] fmtArgs = { symbol, Types.getTypeName(value) };
            throw new TeaException(msg, fmtArgs);
        }

        var.set(value);

        return value;
    }





//* 
//* <TeaFunction name="*="
//*                 arguments="symbol value"
//*             module="tea.math">
//*
//* <Overview>
//* Modifies the value stored in a variable by multiplying
//* it by a given value.
//* </Overview>
//*
//* <Parameter name="symbol">
//* A symbol object identifying a variable in the current context. The
//* variable must contain a numeric value.
//* </Parameter>
//*
//* <Parameter name="aValue">
//* A numeric object represeting the value to be multiplyed by
//* <Arg name="symbol"/>.
//* </Parameter>
//*
//* <Returns>
//* The new value of the variable.
//* </Returns>
//*
//* <Description>
//* Modifies the contents of the variable identifyed by
//* <Arg name="symbol"/> by multiplying the
//* value it contains with <Arg name="value"/>.
//* The variable binded to <Arg name="symbol"/> is expected to
//* contain a numeric object.
//* <P>
//* The <Func name="*="/> is a short way to write
//* </P>
//* <Code>
//*     set! symbol [* $symbol $value]
//* </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>*=</code> function.
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

    @TeaFunctionImplementor("*=")
    public static Object functionMulBy(final TeaFunction func,
                                       final TeaContext  context,
                                       final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "symbol value");
        }

        TeaSymbol symbol  = Args.getSymbol(args, 1);
        TeaVar    var     = context.getVarObject(symbol);
        Object     value   = var.get();
        Number     incrVal = Args.getNumber(args, 2);

        if ( value instanceof Integer ) {
            int x = ((Integer)value).intValue() * incrVal.intValue();
            value = Integer.valueOf(x);
        } else if ( value instanceof Double ) {
            double x = ((Double)value).doubleValue() * incrVal.doubleValue();
            value = new Double(x);
        } else {
            String   msg     =
                "Variable \"{0}\" must contain a numeric, not a {1}";
            Object[] fmtArgs = { symbol, Types.getTypeName(value) };
            throw new TeaException(msg, fmtArgs);
        }

        var.set(value);

        return value;
    }





//* 
//* <TeaFunction name="/="
//*                 arguments="symbol value"
//*             module="tea.math">
//*
//* <Overview>
//* Modifies the value stored in a variable by divinding
//* it by a given value.
//* </Overview>
//*
//* <Parameter name="symbol">
//* A symbol object identifying a variable in the current context. The
//* variable must contain a numeric value.
//* </Parameter>
//*
//* <Parameter name="aValue">
//* A numeric object represeting the value that
//* <Arg name="symbol"/> will be devided with.
//* </Parameter>
//*
//* <Returns>
//* The new value of the variable.
//* </Returns>
//*
//* <Description>
//* Modifies the contents of the variable identifyed by
//* <Arg name="symbol"/> by dividing the
//* value it contains with <Arg name="value"/>.
//* The variable binded to <Arg name="symbol"/> is expected to
//* contain a numeric object.
//* <P>
//* The <Func name="/="/> is a short way to write
//* </P>
//* <Code>
//*     set! symbol [/ $symbol $value]
//* </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>/*</code> function.
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

    @TeaFunctionImplementor("/=")
    public static Object functionDivBy(final TeaFunction func,
                                       final TeaContext  context,
                                       final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "symbol value");
        }

        TeaSymbol symbol  = Args.getSymbol(args, 1);
        TeaVar    var     = context.getVarObject(symbol);
        Object     value   = var.get();
        Number     incrVal = Args.getNumber(args, 2);

        if ( value instanceof Integer ) {
            int x = ((Integer)value).intValue() / incrVal.intValue();
            value = Integer.valueOf(x);
        } else if ( value instanceof Double ) {
            double x = ((Double)value).doubleValue() / incrVal.doubleValue();
            value = new Double(x);
        } else {
            String   msg     =
                "Variable \"{0}\" must contain a numeric, not a {1}";
            Object[] fmtArgs = { symbol, Types.getTypeName(value) };
            throw new TeaException(msg, fmtArgs);
        }

        var.set(value);
        
        return value;
    }





//* 
//* <TeaFunction name="++"
//*                 arguments="symbol"
//*             module="tea.math">
//*
//* <Overview>
//* Modifies the contents of a variable containing a numeric object
//* by adding one to its previous value.
//* </Overview>
//*
//* <Parameter name="symbol">
//* A symbol object identifying a variable in the current context. The
//* variable must contain a numeric object.
//* </Parameter>
//*
//* <Returns>
//* The new value of the variable.
//* </Returns>
//*
//* <Description>
//* Modifies the contents of the variable identified by
//* <Arg name="symbol"/> by incrementing by one the value it had
//* previously stored.
//* The variable binded to <Arg name="symbol"/> is expected to
//* contain a numeric object.
//* <P>
//* The <Func name="++"/> is a short way to write
//* </P>
//* <Code>
//*     set! symbol [+ $symbol 1]
//* </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>++</code> function.
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

    @TeaFunctionImplementor("++")
    public static Object functionIncr(final TeaFunction func,
                                      final TeaContext  context,
                                      final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "symbol");
        }

        TeaSymbol symbol  = Args.getSymbol(args, 1);
        TeaVar    var     = context.getVarObject(symbol);
        Object     value   = var.get();
        
        if ( value instanceof Integer ) {
            int x = ((Integer)value).intValue() + 1;
            value = Integer.valueOf(x);
        } else if ( value instanceof Double ) {
            double x = ((Double)value).doubleValue() + 1.0;
            value = new Double(x);
        } else {
            String   msg     =
                "Variable \"{0}\" must contain a numeric, not a {1}";
            Object[] fmtArgs = { symbol, Types.getTypeName(value) };
            throw new TeaException(msg, fmtArgs);
        }

        var.set(value);
        
        return value;
    }





//* 
//* <TeaFunction name="--"
//*                 arguments="symbol"
//*             module="tea.math">
//*
//* <Overview>
//* Modifies the contents of a variable containing a numeric object
//* by subtracting one from its previous value.
//* </Overview>
//*
//* <Parameter name="symbol">
//* A symbol object identifying a variable in the current context. The
//* variable must contain a numeric object.
//* </Parameter>
//*
//* <Returns>
//* The new value of the variable.
//* </Returns>
//*
//* <Description>
//* Modifies the contents of the variable identified by
//* <Arg name="symbol"/> by decrementing by one the value it had
//* previously stored.
//* The variable binded to <Arg name="symbol"/> is expected to
//* contain a numeric object.
//* <P>
//* The <Func name="--"/> is a short way to write
//* </P>
//* <Code>
//*     set! symbol [- $symbol 1]
//* </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>--</code> function.
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

    @TeaFunctionImplementor("--")
    public static Object functionDecr(final TeaFunction func,
                                      final TeaContext  context,
                                      final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "symbol");
        }

        TeaSymbol symbol  = Args.getSymbol(args, 1);
        TeaVar    var     = context.getVarObject(symbol);
        Object     value   = var.get();
        
        if ( value instanceof Integer ) {
            int x = ((Integer)value).intValue() - 1;
            value = Integer.valueOf(x);
        } else if ( value instanceof Double ) {
            double x = ((Double)value).doubleValue() - 1;
            value = new Double(x);
        } else {
            String   msg     =
                "Variable \"{0}\" must contain a numeric, not a {1}";
            Object[] fmtArgs = { symbol, Types.getTypeName(value) };
            throw new TeaException(msg, fmtArgs);
        }

        var.set(value);
        
        return value;
    }





//* 
//* <TeaFunction name="~"
//*                 arguments="value"
//*             module="tea.math">
//*
//* <Overview>
//* Calculates the binary negation of an integer value.
//* </Overview>
//*
//* <Parameter name="value">
//* An integer value.
//* </Parameter>
//*
//* <Returns>
//* The result of performing a binary negation in the <Arg name="value"/>
//* argument.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>~</code> function.
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

    @TeaFunctionImplementor("~")
    public Object functionBinNeg(final TeaFunction func,
                                 final TeaContext  context,
                                 final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "value");
        }

        int value  = Args.getInt(args, 1).intValue();
        int result = ~value;

        return Integer.valueOf(result);
    }





//* 
//* <TeaFunction name="&amp;"
//*                 arguments="[value ...]"
//*             module="tea.math">
//*
//* <Overview>
//* Calculates the binary and operation on a set of integer values.
//* </Overview>
//*
//* <Parameter name="value">
//* An integer value.
//* </Parameter>
//*
//* <Returns>
//* The result of performing a binary and on the values of the
//* arguments. If no arguments are given then the result value is
//* zero.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>&amp;</code> function.
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

    @TeaFunctionImplementor("&")
    public Object functionBinAnd(final TeaFunction func,
                                 final TeaContext  context,
                                 final Object[]    args)
        throws TeaException {

        int lastIndex = args.length;
        int result    = (lastIndex==1) ? 0 : Args.getInt(args,1).intValue();

        for ( int i=2; i<lastIndex; i++ ) {
            int operand = Args.getInt(args,i).intValue();

            result &= operand;
        }

        return Integer.valueOf(result);
    }





//* 
//* <TeaFunction name="|"
//*                 arguments="[value ...]"
//*             module="tea.math">
//*
//* <Overview>
//* Calculates the binary or operation on a set of integer values.
//* </Overview>
//*
//* <Parameter name="value">
//* An integer value.
//* </Parameter>
//*
//* <Returns>
//* The result of performing a binary or on the values of the
//* arguments. If no arguments are given then the result value is
//* zero.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>|</code> function.
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

    @TeaFunctionImplementor("|")
    public Object functionBinOr(final TeaFunction func,
                                final TeaContext  context,
                                final Object[]    args)
        throws TeaException {

        int lastIndex = args.length;
        int result    = (lastIndex==1) ? 0 : Args.getInt(args,1).intValue();

        for ( int i=2; i<lastIndex; i++ ) {
            int operand = Args.getInt(args,i).intValue();

            result |= operand;
        }

        return Integer.valueOf(result);
    }





//* 
//* <TeaFunction name="^"
//*                 arguments="[value ...]"
//*             module="tea.math">
//*
//* <Overview>
//* Calculates the binary xor operation on a set of integer values.
//* </Overview>
//*
//* <Parameter name="value">
//* An integer value.
//* </Parameter>
//*
//* <Returns>
//* The result of performing a binary xor on the values of the
//* arguments. If no arguments are given then the result value is
//* zero.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>^</code> function.
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

    @TeaFunctionImplementor("^")
    public Object functionBinXor(final TeaFunction func,
                                 final TeaContext  context,
                                 final Object[]    args)
        throws TeaException {

        int lastIndex = args.length;
        int result    = (lastIndex==1) ? 0 : Args.getInt(args,1).intValue();

        for ( int i=2; i<lastIndex; i++ ) {
            int operand = Args.getInt(args,i).intValue();

            result ^= operand;
        }

        return Integer.valueOf(result);
    }





//* 
//* <TeaFunction name="&lt;&lt;"
//*                 arguments="value shift"
//*             module="tea.math">
//*
//* <Overview>
//* Calculates the binary shift left operation on an integer value.
//* </Overview>
//*
//* <Parameter name="value">
//* An integer value.
//* </Parameter>
//* 
//* <Parameter name="shift">
//* An integer value representing the number of bits to shift left.
//* </Parameter>
//*
//* <Returns>
//* The result of performing a shift left operation by <Arg name="shift"/>
//* bits on <Arg name="value"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>&lt;&lt;</code> function.
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

    @TeaFunctionImplementor("<<")
    public Object functionBinSl(final TeaFunction func,
                                final TeaContext  context,
                                final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "value shift");
        }

        int value = Args.getInt(args, 1).intValue();
        int shift = Args.getInt(args, 2).intValue();
        int result = value << shift;

        return Integer.valueOf(result);
    }





//* 
//* <TeaFunction name="&gt;&gt;"
//*                 arguments="value shift"
//*             module="tea.math">
//*
//* <Overview>
//* Calculates the binary shift right operation on an integer value.
//* </Overview>
//*
//* <Parameter name="value">
//* An integer value.
//* </Parameter>
//* 
//* <Parameter name="shift">
//* An integer value representing the number of bits to shift right.
//* </Parameter>
//*
//* <Returns>
//* The result of performing a shift right operation by <Arg name="shift"/>
//* bits on <Arg name="value"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>&gt;&gt;</code> function.
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

    @TeaFunctionImplementor(">>")
    public Object functionBinSr(final TeaFunction func,
                                final TeaContext  context,
                                final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "value shift");
        }

        int value = Args.getInt(args, 1).intValue();
        int shift = Args.getInt(args, 2).intValue();
        int result = value >> shift;

        return Integer.valueOf(result);
    }





//* 
//* <TeaFunction name="&amp;="
//*                 arguments="symbol value"
//*             module="tea.math">
//*
//* <Overview>
//* Modifies the value stored in a variable by performing a binary
//* and operation with it.
//* </Overview>
//*
//* <Parameter name="symbol">
//* A symbol object identifying a variable in the current context. The
//* variable must contain a numeric value.
//* </Parameter>
//*
//* <Parameter name="value">
//* A numeric object represeting the value to be binary anded with
//* the value stored in variable <Arg name="symbol"/>.
//* </Parameter>
//*
//* <Returns>
//* The new value of the variable.
//* </Returns>
//*
//* <Description>
//* Modifies the contents of the variable identified by
//* <Arg name="symbol"/> by performing a binary and between
//* <Arg name="value"/> and the value it had previously stored.
//* The variable binded to <Arg name="symbol"/> is expected to
//* contain an integer object.
//* <P>
//* The <Func name="&amp;="/> is a short way to write
//* </P>
//* <Code>
//*     set! symbol [&amp; $symbol $value]
//* </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>&amp;=</code> function.
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

    @TeaFunctionImplementor("&=")
    public static Object functionBinAndBy(final TeaFunction func,
                                          final TeaContext  context,
                                          final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "symbol value");
        }

        TeaSymbol symbol  = Args.getSymbol(args, 1);
        TeaVar    var     = context.getVarObject(symbol);
        Object     value   = var.get();
        int        operand = Args.getInt(args, 2).intValue();
        int        result  = 0;
        Object     resObj  = null;

        try {
            result = ((Integer)value).intValue() & operand;
        } catch (ClassCastException e) {
            String   msg     =
                "Variable \"{0}\" must contain a numeric, not a {1}";
            Object[] fmtArgs = { symbol, Types.getTypeName(value) };
            throw new TeaException(msg, fmtArgs);
        }
        resObj = Integer.valueOf(result);

        var.set(resObj);

        return resObj;
    }





//* 
//* <TeaFunction name="|="
//*                 arguments="symbol value"
//*             module="tea.math">
//*
//* <Overview>
//* Modifies the value stored in a variable by performing a binary
//* OR operation with it.
//* </Overview>
//*
//* <Parameter name="symbol">
//* A symbol object identifying a variable in the current context. The
//* variable must contain a numeric value.
//* </Parameter>
//*
//* <Parameter name="value">
//* A numeric object represeting the value to be binary ORed with
//* the value stored in variable <Arg name="symbol"/>.
//* </Parameter>
//*
//* <Returns>
//* The new value of the variable.
//* </Returns>
//*
//* <Description>
//* Modifies the contents of the variable identified by
//* <Arg name="symbol"/> by performing a binary OR between
//* <Arg name="value"/> and the value it had previously stored.
//* The variable binded to <Arg name="symbol"/> is expected to
//* contain an integer object.
//* <P>
//* The <Func name="|="/> is a short way to write
//* </P>
//* <Code>
//*     set! symbol [| $symbol $value]
//* </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>|=</code> function.
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

    @TeaFunctionImplementor("|=")
    public static Object functionBinOrBy(final TeaFunction func,
                                         final TeaContext  context,
                                         final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "symbol value");
        }

        TeaSymbol symbol  = Args.getSymbol(args, 1);
        TeaVar    var     = context.getVarObject(symbol);
        Object     value   = var.get();
        int        operand = Args.getInt(args, 2).intValue();
        int        result  = 0;
        Object     resObj  = null;

        try {
            result = ((Integer)value).intValue() | operand;
        } catch (ClassCastException e) {
            String   msg     =
                "Variable \"{0}\" must contain a numeric, not a {1}";
            Object[] fmtArgs = { symbol, Types.getTypeName(value) };
            throw new TeaException(msg, fmtArgs);
        }
        resObj = Integer.valueOf(result);

        var.set(resObj);

        return resObj;
    }





//* 
//* <TeaFunction name="^="
//*                 arguments="symbol value"
//*             module="tea.math">
//*
//* <Overview>
//* Modifies the value stored in a variable by performing a binary
//* XOR operation with it.
//* </Overview>
//*
//* <Parameter name="symbol">
//* A symbol object identifying a variable in the current context. The
//* variable must contain a numeric value.
//* </Parameter>
//*
//* <Parameter name="value">
//* A numeric object represeting the value to be binary XORed with
//* the value stored in variable <Arg name="symbol"/>.
//* </Parameter>
//*
//* <Returns>
//* The new value of the variable.
//* </Returns>
//*
//* <Description>
//* Modifies the contents of the variable identified by
//* <Arg name="symbol"/> by performing a binary XOR between
//* <Arg name="value"/> and the value it had previously stored.
//* The variable binded to <Arg name="symbol"/> is expected to
//* contain an integer object.
//* <P>
//* The <Func name="^="/> is a short way to write
//* </P>
//* <Code>
//*     set! symbol [^ $symbol $value]
//* </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>^=</code> function.
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

    @TeaFunctionImplementor("^=")
    public static Object functionBinXorBy(final TeaFunction func,
                                          final TeaContext  context,
                                          final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "symbol value");
        }

        TeaSymbol symbol  = Args.getSymbol(args, 1);
        TeaVar    var     = context.getVarObject(symbol);
        Object     value   = var.get();
        int        operand = Args.getInt(args, 2).intValue();
        int        result  = 0;
        Object     resObj  = null;

        try {
            result = ((Integer)value).intValue() ^ operand;
        } catch (ClassCastException e) {
            String   msg     =
                "Variable \"{0}\" must contain a numeric, not a {1}";
            Object[] fmtArgs = { symbol, Types.getTypeName(value) };
            throw new TeaException(msg, fmtArgs);
        }
        resObj = Integer.valueOf(result);

        var.set(resObj);

        return resObj;
    }





//* 
//* <TeaFunction name="&lt;&lt;="
//*                 arguments="symbol value"
//*             module="tea.math">
//*
//* <Overview>
//* Modifies the value stored in a variable by performing a binary
//* shift left operation with it.
//* </Overview>
//*
//* <Parameter name="symbol">
//* A symbol object identifying a variable in the current context. The
//* variable must contain a numeric value.
//* </Parameter>
//*
//* <Parameter name="shift">
//* A numeric object represeting the number of bits to shift left
//* the value stored in variable <Arg name="symbol"/>.
//* </Parameter>
//*
//* <Returns>
//* The new value of the variable.
//* </Returns>
//*
//* <Description>
//* Modifies the contents of the variable identified by
//* <Arg name="symbol"/> by performing a binary shift left by
//* <Arg name="shift"/> bits on the value it had previously stored.
//* The variable binded to <Arg name="symbol"/> is expected to
//* contain an integer object.
//* <P>
//* The <Func name="&lt;&lt;="/> is a short way to write
//* </P>
//* <Code>
//*     set! symbol [&amp;lt;&amp;lt; $symbol $shift]
//* </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>&lt;&lt;=</code> function.
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

    @TeaFunctionImplementor("<<=")
    public static Object functionBinSlBy(final TeaFunction func,
                                         final TeaContext  context,
                                         final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "symbol value");
        }

        TeaSymbol symbol = Args.getSymbol(args, 1);
        TeaVar    var    = context.getVarObject(symbol);
        Object     value  = var.get();
        int        shift  = Args.getInt(args, 2).intValue();
        int        result = 0;
        Object     resObj = null;

        try {
            result = ((Integer)value).intValue() << shift;
        } catch (ClassCastException e) {
            String   msg     =
                "Variable \"{0}\" must contain a numeric, not a {1}";
            Object[] fmtArgs = { symbol, Types.getTypeName(value) };
            throw new TeaException(msg, fmtArgs);
        }
        resObj = Integer.valueOf(result);

        var.set(resObj);

        return resObj;
    }





//* 
//* <TeaFunction name="&gt;&gt;="
//*                 arguments="symbol value"
//*             module="tea.math">
//*
//* <Overview>
//* Modifies the value stored in a variable by performing a binary
//* shift right operation with it.
//* </Overview>
//*
//* <Parameter name="symbol">
//* A symbol object identifying a variable in the current context. The
//* variable must contain a numeric value.
//* </Parameter>
//*
//* <Parameter name="shift">
//* A numeric object represeting the number of bits to shift right
//* the value stored in variable <Arg name="symbol"/>.
//* </Parameter>
//*
//* <Returns>
//* The new value of the variable.
//* </Returns>
//*
//* <Description>
//* Modifies the contents of the variable identified by
//* <Arg name="symbol"/> by performing a binary shift right by
//* <Arg name="shift"/> bits on the value it had previously stored.
//* The variable binded to <Arg name="symbol"/> is expected to
//* contain an integer object.
//* <P>
//* The <Func name="&gt;&gt;="/> is a short way to write
//* </P>
//* <Code>
//*     set! symbol [&gt;&gt; $symbol $shift]
//* </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>&gt;&gt;=</code> function.
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

    @TeaFunctionImplementor(">>=")
    public static Object functionBinSrBy(final TeaFunction func,
                                         final TeaContext  context,
                                         final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "symbol value");
        }

        TeaSymbol symbol = Args.getSymbol(args, 1);
        TeaVar    var    = context.getVarObject(symbol);
        Object     value  = var.get();
        int        shift  = Args.getInt(args, 2).intValue();
        int        result = 0;
        Object     resObj = null;

        try {
            result = ((Integer)value).intValue() >> shift;
        } catch (ClassCastException e) {
            String   msg     =
                "Variable \"{0}\" must contain a numeric, not a {1}";
            Object[] fmtArgs = { symbol, Types.getTypeName(value) };
            throw new TeaException(msg, fmtArgs);
        }
        resObj = Integer.valueOf(result);

        var.set(resObj);

        return resObj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private interface SComparator {

        boolean compareInt(int x, int y);
        boolean compareDouble(double x, double y);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static final class SGt
        extends Object
        implements SComparator {
    
        public boolean compareInt(final int x,
                                  final int y) {
            return x > y;
        }
    
        public boolean compareDouble(final double x,
                                     final double y) {
            return x > y;
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static final class SLt
        extends Object
        implements SComparator {
    
        public boolean compareInt(final int x,
                                  final int y) {
            return x < y;
        }
    
        public boolean compareDouble(final double x,
                                     final double y) {
            return x < y;
        }
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

