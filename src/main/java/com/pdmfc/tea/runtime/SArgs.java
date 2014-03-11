/**************************************************************************
 *
 * Copyright (c) 2010-2013 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;





/**************************************************************************
 *
 * Provides utility methods for checking and accessing arguments in
 * the implementation of a function.
 *
 **************************************************************************/

public final class SArgs
    extends Object {





/**************************************************************************
 *
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private SArgs() {

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
 * Tries to convert argument <TT>index</TT> into a SObjBlock. If that
 * argument is not a block, an exception is thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 * received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception STypeException Thrown if <TT>args[index]</TT> is not a
 * SObjBlock.
 *
 **************************************************************************/

    public static SObjBlock getBlock(final Object[] args,
                                     final int      index)
        throws STypeException {

        SObjBlock result = null;

        try {
            result = (SObjBlock)args[index];
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
 * Tries to convert argument <TT>index</TT> into a SObjPair. If that
 * argument is not a pair, an exception is thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 *     received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception STypeException Thrown if <TT>args[index]</TT> is not a
 * SObjPair.
 *
 **************************************************************************/

    public static SObjPair getPair(final Object[] args,
                                   final int      index)
        throws STypeException {

        SObjPair result = null;

        try {
            result = (SObjPair)args[index];
        } catch ( ClassCastException e ) {
            throw new STypeException(args, index, "pair");
        }

        return result;
    }





/**************************************************************************
 *
 * This is an utility method, to be used by derived classes. It tries to
 * convert argument <TT>index</TT> into a SObjSymbol. If that argument is
 * not a pair, an exception is thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 *     received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception STypeException Thrown if <code>args[index]</code> is not
 * a <code>SObjSymbol</code>.
 *
 **************************************************************************/

    public static SObjSymbol getSymbol(final Object[] args,
                                       final int      index)
        throws STypeException {

        SObjSymbol result = null;

        try {
            result = (SObjSymbol)args[index];
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
 * to convert argument <TT>index</TT> into a SObjFunction.
 *
 * If <TT>args[index]</TT> is a SObjFunction then everything is fine.
 * If <TT>args[index]</TT> is a SObjSymbol then a variable with the
 * some name is searched in <TT>context</TT>. If it does not exists or
 * if it does not contain a <TT>SObjFunction</TT> an exception is
 * thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 *     received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception com.pdmfc.tea.STeaException Thrown if
 * <TT>args[index]</TT> is neither a <TT>SObjFunction</TT> nor a
 * <TT>SObjSymbol</TT>
 *
 **************************************************************************/

    public static SObjFunction getFunction(final SContext context,
                                           final Object[]  args,
                                           final int       index)
        throws STeaException {

        SObjFunction result = null;
        Object       arg    = args[index];

        if ( arg instanceof SObjFunction ) {
            result = (SObjFunction)arg;
        } else {
            if ( !(arg instanceof SObjSymbol) ) {
                throw new STypeException(args, index, "function or a symbol");
            } else {
                SObjSymbol symbol = (SObjSymbol)arg;
                Object     value  = null;

                try {
                    value = context.getVar(symbol);
                } catch ( SNoSuchVarException e ) {
                    value = STypes.getVarWithEffort(context, symbol);
                }

                if ( value instanceof SObjFunction ) {
                    result = (SObjFunction)value;
                } else {
                    String msg =
                        "variable {0} should contain a function, not a {1}";
                    throw new SRuntimeException(args,
                                                msg,
                                                arg,
                                                STypes.getTypeName(value));
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

