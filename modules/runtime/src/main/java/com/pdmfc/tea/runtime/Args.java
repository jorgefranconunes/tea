/**************************************************************************
 *
 * Copyright (c) 2010-2013 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.Types;





/**************************************************************************
 *
 * Provides utility methods for checking and accessing arguments in
 * the implementation of a function.
 *
 **************************************************************************/

public final class Args
    extends Object {





/**************************************************************************
 *
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private Args() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * Checks that argument count in a function call exactly matches a
 * given number.
 *
 * <p>If the argument count is different than the given number then a
 * <code>{@link SNumArgException}</code> will be thrown.</p>
 *
 * @param args The arguments passed to a function call.
 *
 * @param argCount The exact number of arguments the function is
 * expecting.
 *
 * @param usageMessage The usage message given to the <code>{@link
 * SNumArgException}</code> if one is thrown.
 *
 * @exception SNumArgException Thrown if <code>args.length</code> does
 * not equal <code>argCount</code>.
 *
 **************************************************************************/

    public static void checkCount(final Object[] args,
                                  final int      argCount,
                                  final String   usageMessage)
        throws SNumArgException {

        if ( args.length != argCount ) {
            throw new SNumArgException(args, usageMessage);
        }
    }





/**************************************************************************
 *
 * Checks that argument count in a function call is at least a given
 * number.
 *
 * <p>If the argument count is less than the given number then a
 * <code>{@link SNumArgException}</code> will be thrown.</p>
 *
 * @param args The arguments passed to a function call.
 *
 * @param minArgCount The minimum number of arguments the function is
 * expecting.
 *
 * @param usageMessage The usage message given to the <code>{@link
 * SNumArgException}</code> if one is thrown.
 *
 * @exception SNumArgException Thrown if <code>args.length</code> is
 * less than <code>argCount</code>.
 *
 **************************************************************************/

    public static void checkAtLeast(final Object[] args,
                                    final int      minArgCount,
                                    final String   usageMessage)
        throws SNumArgException {

        if ( args.length < minArgCount ) {
            throw new SNumArgException(args, usageMessage);
        }
    }





/**************************************************************************
 *
 * Checks that argument count in a function call is at most a given
 * number.
 *
 * <p>If the argument count is more than the given number then a
 * <code>{@link SNumArgException}</code> will be thrown.</p>
 *
 * @param args The arguments passed to a function call.
 *
 * @param maxArgCount The maximum number of arguments the function is
 * expecting.
 *
 * @param usageMessage The usage message given to the <code>{@link
 * SNumArgException}</code> if one is thrown.
 *
 * @exception SNumArgException Thrown if <code>args.length</code> is
 * more than <code>argCount</code>.
 *
 **************************************************************************/

    public static void checkAtMost(final Object[] args,
                                   final int      maxArgCount,
                                   final String   usageMessage)
        throws SNumArgException {

        if ( args.length > maxArgCount ) {
            throw new SNumArgException(args, usageMessage);
        }
    }





/**************************************************************************
 *
 * Checks that argument count in a function call is in a given range.
 *
 * <p>If the argument count is less than the given number then a
 * <code>{@link SNumArgException}</code> will be thrown.</p>
 *
 * @param args The arguments passed to a function call.
 *
 * @param minArgCount The minimum number of arguments the function
 * accepts.
 *
 * @param maxArgCount The maximum number of arguments the function
 * accepts.
 *
 * @param usageMessage The usage message given to the <code>{@link
 * SNumArgException}</code> if one is thrown.
 *
 * @exception SNumArgException Thrown if <code>args.length</code> is
 * not in the appropriate range.
 *
 **************************************************************************/

    public static void checkBetween(final Object[] args,
                                    final int      minArgCount,
                                    final int      maxArgCount,
                                    final String   usageMessage)
        throws SNumArgException {

        int argCount = args.length;

        if ( (argCount<minArgCount) || (argCount>maxArgCount) ) {
            throw new SNumArgException(args, usageMessage);
        }
    }





/**************************************************************************
 *
 * Tries to convert argument <TT>index</TT> into a TeaBlock. If that
 * argument is not a block, an exception is thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 * received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception STypeException Thrown if <TT>args[index]</TT> is not a
 * TeaBlock.
 *
 **************************************************************************/

    public static TeaBlock getBlock(final Object[] args,
                                     final int      index)
        throws STypeException {

        TeaBlock result = null;

        try {
            result = (TeaBlock)args[index];
        } catch ( ClassCastException e ) {
            throw new STypeException(args, index, "block");
        }

        return result;
    }





/**************************************************************************
 *
 * This is an utility method, to be used by derived classes. It tries
 * to convert argument <TT>index</TT> into a <TT>SObjNumeric</TT>. If
 * that argument is not an int, an exception is thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 *     received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception STypeException Thrown if <TT>args[index]</TT> is not a
 * <TT>SObjNumeric</TT>.
 *
 **************************************************************************/

    public static Number getNumber(final Object[] args,
                                   final int      index)
        throws STypeException {

        Number result = null;

        try {
            result = (Number)args[index];
        } catch ( ClassCastException e ) {
            throw new STypeException(args, index, "numeric");
        }

        return result;
    }





/**************************************************************************
 *
 * Tries to convert argument <TT>index</TT> into a TeaPair. If that
 * argument is not a pair, an exception is thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 *     received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception STypeException Thrown if <TT>args[index]</TT> is not a
 * TeaPair.
 *
 **************************************************************************/

    public static TeaPair getPair(final Object[] args,
                                   final int      index)
        throws STypeException {

        TeaPair result = null;

        try {
            result = (TeaPair)args[index];
        } catch ( ClassCastException e ) {
            throw new STypeException(args, index, "pair");
        }

        return result;
    }





/**************************************************************************
 *
 * This is an utility method, to be used by derived classes. It tries to
 * convert argument <TT>index</TT> into a TeaSymbol. If that argument is
 * not a pair, an exception is thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 *     received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception STypeException Thrown if <code>args[index]</code> is not
 * a <code>TeaSymbol</code>.
 *
 **************************************************************************/

    public static TeaSymbol getSymbol(final Object[] args,
                                       final int      index)
        throws STypeException {

        TeaSymbol result = null;

        try {
            result = (TeaSymbol)args[index];
        } catch ( ClassCastException e ) {
            throw new STypeException(args, index, "symbol");
        }

        return result;
    }





/**************************************************************************
 *
 * This is an utility method, to be used by derived classes. It tries to
 * convert argument <TT>index</TT> into a SObjInt. If that argument is
 * not an int, an exception is thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 *     received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception STypeException Thrown if <code>args[index]</code> is not
 * an <code>SObjInt</code>.
 *
 **************************************************************************/

    public static Integer getInt(final Object[] args,
                                 final int      index)
        throws STypeException {

        Integer result = null;

        try {
            result = (Integer)args[index];
        } catch ( ClassCastException e ) {
            throw new STypeException(args, index, "int");
        }

        return result;
    }





/**************************************************************************
 *
 * This is an utility method, to be used by derived classes. It tries to
 * convert argument <TT>index</TT> into a SObjFloat. If that argument is
 * not a float, an exception is thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 *     received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception STypeException Thrown if <code>args[index]</code> is not
 * a <code>SObjFloat</code>.
 *
 **************************************************************************/

    public static Double getFloat(final Object[] args,
                                  final int      index)
        throws STypeException {

        Double result = null;

        try {
            result = (Double)args[index];
        } catch ( ClassCastException e ) {
            throw new STypeException(args, index, "float");
        }

        return result;
    }





/**************************************************************************
 *
 * This is an utility method, to be used by derived classes. It tries to
 * convert argument <TT>index</TT> into a SObjBool. If that argument is
 * not a boolean, an exception is thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 *     received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception STypeException Thrown if <code>args[index]</code> is not
 * a <code>java.lang.Boolean</code>.
 *
 **************************************************************************/

    public static Boolean getBoolean(final Object[] args,
                                     final int      index)
        throws STypeException {

        Boolean result = null;

        try {
            result = (Boolean)args[index];
        } catch ( ClassCastException e ) {
            throw new STypeException(args, index, "boolean");
        }

        return result;
    }





/**************************************************************************
 *
 * This is an utility method, to be used by derived classes. It tries to
 * convert argument <TT>index</TT> into a SObjString. If that argument is
 * not an int, an exception is thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 *     received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception STypeException Thrown if <code>args[index]</code> is not
 * an <code>SObjString</code>.
 *
 **************************************************************************/

    public static String getString(final Object[] args,
                                   final int      index)
        throws STypeException {

        String result = null;

        try {
            result = (String)args[index];
        } catch ( ClassCastException e ) {
            throw new STypeException(args, index, "string");
        }

        return result;
    }





/**************************************************************************
 *
 * This is an utility method, to be used by derived classes. It tries
 * to convert argument <TT>index</TT> into a TeaFunction.
 *
 * If <TT>args[index]</TT> is a TeaFunction then everything is fine.
 * If <TT>args[index]</TT> is a TeaSymbol then a variable with the
 * some name is searched in <TT>context</TT>. If it does not exists or
 * if it does not contain a <TT>TeaFunction</TT> an exception is
 * thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 *     received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception com.pdmfc.tea.TeaException Thrown if
 * <TT>args[index]</TT> is neither a <TT>TeaFunction</TT> nor a
 * <TT>TeaSymbol</TT>
 *
 **************************************************************************/

    public static TeaFunction getFunction(final TeaContext context,
                                           final Object[]  args,
                                           final int       index)
        throws TeaException {

        TeaFunction result = null;
        Object       arg    = args[index];

        if ( arg instanceof TeaFunction ) {
            result = (TeaFunction)arg;
        } else {
            if ( !(arg instanceof TeaSymbol) ) {
                throw new STypeException(args, index, "function or a symbol");
            } else {
                TeaSymbol symbol = (TeaSymbol)arg;
                Object     value  = null;

                try {
                    value = context.getVar(symbol);
                } catch ( SNoSuchVarException e ) {
                    value = Types.getVarWithEffort(context, symbol);
                }

                if ( value instanceof TeaFunction ) {
                    result = (TeaFunction)value;
                } else {
                    String msg =
                        "variable {0} should contain a function, not a {1}";
                    throw new SRuntimeException(args,
                                                msg,
                                                arg,
                                                Types.getTypeName(value));
                }
            }
        }

        return result;
    }
                                     


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

