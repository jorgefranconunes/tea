/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.util;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaRunException;
import com.pdmfc.tea.TeaTypeException;
import com.pdmfc.tea.Types;





/**************************************************************************
 *
 * Implements string formating with printf style formats.
 *
 * <p>This is an abstract class. Derived classes should implement the
 * <code>{@link #append(char)}</code>, <code>{@link
 * #append(String)}</code> methods.
 *
 **************************************************************************/

public abstract class SFormater
    extends Object {


    // The format string suplied by the caller.
    private String _fmtString;

    // Array with the object to format acording to the format string.
    private Object[] _args;
    
    // The index of the next argument to fetch.
    private int    _argIndex;

    // One greater than the index of the last char in the format string.
    private int    _endP;
 
    // The index in the format string of the next char to fetch.
    private int    _p;

    // Flags signaling the format type of the next argument.
    private int    _flags;

    // The width field in the modifier for the next argument.
    private int    _width;

    // The number of decimal places for the next argument
    private int    _sigFigs;

    // Flags.
    /// Zero-fill.
    private static final int ZF = 1;
    /// Left justify.
    private static final int LJ = 2;
    /// Hexadecimal.
    private static final int HX = 4;
    /// Octal.
    private static final int OC = 8;





/**************************************************************************
 *
 * Appends a string to the formated result so far.
 *
 * <p>This is an abstract method and must be implemented in a derived
 * class.</p>
 *
 * @param s The string to be appended.
 *
 **************************************************************************/

    protected abstract void append(String s);





/**************************************************************************
 *
 * Appends a char to the formated result so far.
 *
 * <p>This is an abstract method and must be implemented in a derived
 * class.</p>
 *
 * @param c The char to be appended.
 *
 **************************************************************************/

    protected abstract void append(char c);





/**************************************************************************
 *
 * Initializes the object internal state.
 *
 **************************************************************************/

    public SFormater() {

        init(null, null, 0);
    }





/**************************************************************************
 *
 * Initializes the members to start a formting operation.
 *
 * @param fmt The format string.
 *
 **************************************************************************/

    private void init(final String   fmt,
                      final Object[] args,
                      final int      firstArg) {

        _fmtString = fmt;
        _args      = args;
        _argIndex  = firstArg;
        _endP      = (fmt==null) ? 0 : fmt.length();
        _p         = 0;
        _flags     = 0;
        _width     = -1;
        _sigFigs   = -1;
    }





/**************************************************************************
 *
 * Creates the formated string by sucessive calls to the
 * <TT>append()</TT> method.
 *
 * @param fmtString The printf like format string.
 *
 * @param args Array with the objects to be formated.
 *
 * @param firstArg Index into the <TT>args</TT> array pointing to the
 * first of the objects to be formated.
 *
 * @exception TeaException Thrown if there were not enough arguments
 * acording to the format string or if some of the arguments was of a
 * type not expected by the format string.
 *
 **************************************************************************/

    public final void format(final String   fmtString,
                             final Object[] args,
                             final int      firstArg)
        throws TeaException {

        init(fmtString, args, firstArg);

        while ( !atEnd() ) {
            char c = nextChar();
            
            if ( c == '%' ) {
                processArg();
            } else {
                append(c);
            }
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void processArg()
        throws TeaException {

        if ( atEnd() ) {
            return;
        }

        _flags   = 0;
        _width   = 0;
        _sigFigs = 0;

        char c = nextChar();

        switch ( c ) {
        case '-' :
            _flags |= LJ;
            processFieldWidth();
            break;
        case '%' :
            append('%');
            break;
        default  :
            processFieldWidth(c);
            break;
        }
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void processFieldWidth()
       throws TeaException {

        if ( !atEnd() ) {
            processFieldWidth(nextChar());
        }
    }





/**************************************************************************
 *
 * @param firstFormatChar The char after the '<TT>%</TT>' and a
 * possible '<TT>-</TT>'.
 *
 **************************************************************************/

    private void processFieldWidth(final char firstFormatChar)
        throws TeaException {

        char c = firstFormatChar;

        if ( !Character.isDigit(c) ) {
            processTypeField(c);
        } else {
            if ( c == '0' ) {
                _flags |= ZF;
                _width = 0;
            } else {
                _width = c - '0';
            }
            while ( !atEnd() && Character.isDigit(c=nextChar()) ) {
                _width = _width*10 + (c-'0');
            }
            if ( c == '.' ) {
                processSigFigs();
            } else {
                processTypeField(c);
            }
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void processSigFigs()
        throws TeaException {

        if ( atEnd() ) {
            return;
        }

        char c = 0;

        _sigFigs = 0;

        while ( !atEnd() && Character.isDigit(c=nextChar()) ) {
            _sigFigs = _sigFigs*10 + (c-'0');
        }

        processTypeField(c);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void processTypeField(final char type)
        throws TeaException {

        Object arg = nextArg();

        try {
            switch ( type ) {
            case 'd' : 
                fmt(((Number)arg).intValue(), _width,_flags);
                break;
            case 'x' :
                _flags |= HX;
                fmt(((Number)arg).intValue(), _width,_flags);
                break;
            case 'o' :
                _flags |= OC;
                fmt(((Number)arg).intValue(), _width,_flags);
                break;
            case 'f' :
                fmt(((Double)arg).doubleValue(), _width, _sigFigs, _flags);
                break;
            case 's' :
                fmt((String)arg, _width, _flags);
                break;
            }
        } catch (ClassCastException e) {
            String   fmt     = "wrong argument type - {0}";
            Object[] fmtArgs = { Types.getTypeName(arg) };
            throw new TeaTypeException(fmt, fmtArgs);
        }
    }





/**************************************************************************
 *
 * @return <TT>true</TT> if there ar no more chars to fetch in the
 * format string. <TT>false</TT> otherwise.
 *
 **************************************************************************/

    private boolean atEnd() {

        return _p >= _endP;
    }





/**************************************************************************
 *
 * @return
 *    The next char in the format string.
 *
 **************************************************************************/

    private char nextChar() {

        return _fmtString.charAt(_p++);
    }





/**************************************************************************
 *
 * @exception com.pdmfc.tea.TeaNumArgException
 *    Thrown if there were no more arguments to fetch.
 *
 **************************************************************************/

    private Object nextArg()
        throws TeaException {

        try {
            return _args[_argIndex++];
        } catch (ArrayIndexOutOfBoundsException e) {
            String msg = "missing arguments requested by format string";
            throw new TeaRunException(msg);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void fmt(final int i,
                     final int minWidth,
                     final int flags ) {

        boolean hexadecimal = ( ( flags & HX ) != 0 );
        boolean octal       = ( ( flags & OC ) != 0 );

        if ( hexadecimal ) {
            fmt(Integer.toString(i & 0xffffffff, 16), minWidth, flags);
        } else if ( octal ) {
            fmt(Integer.toString(i & 0xffffffff, 8), minWidth, flags);
        } else {
            fmt(Integer.toString(i), minWidth, flags);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void fmt(final double f,
                     final int    minWidth,
                     final int    sigFigs,
                     final int    flags) {

        if ( sigFigs != 0 ) {
            fmt(sigFigFix(Double.toString(f), sigFigs), minWidth, flags);
        } else {
            fmt(Double.toString(f), minWidth, flags);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void fmt(final String input,
                     final int    minWidth,
                     final int    flags) {

        String s = input;

        if ( s == null ) {
            s = "(null)";
        }

        int     len         = s.length();
        boolean zeroFill    = ( ( flags & ZF ) != 0 );
        boolean leftJustify = ( ( flags & LJ ) != 0 );

        if ( len >= minWidth ) {
            append(s);
            return;
        }

        int  fillWidth = minWidth - len;
        char fillChar  = zeroFill ? '0' : ' ';

        if ( leftJustify ) {
            append(s);
            fill(fillWidth, fillChar);
        } else {
            if ( zeroFill && s.charAt(0)=='-' ) {
                append('-');
                fill(fillWidth, fillChar);
                append(s.substring(1));
            } else {
                fill(fillWidth, fillChar);
                append(s);
            }
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void fill(final int  fillWidth,
                      final char c) {

        int n = fillWidth;

        while ( n-- > 0 ) {
            append(c);
        }
    }





/**************************************************************************
 *
 * From Jef Poskanzer <jef@acme.com>.
 *
 **************************************************************************/

    private static String sigFigFix(final String s,
                                    final int    sigFigs ) {

        // First dissect the floating-point number string into sign,
        // integer part, fraction part, and exponent.
        String sign      = "";
        String unsigned  = s;
        char   firstChar = s.charAt(0);

        switch ( firstChar ) {
        case '-' :
            sign     = "-";
            unsigned = s.substring(1);
            break;
        case '+' :
            sign     = "+";
            unsigned = s.substring(1);
            break;
        }

        String mantissa;
        String exponent;
        int    eInd = unsigned.indexOf('E');

        if ( eInd == -1 ) {
            mantissa = unsigned;
            exponent = "";
        } else {
            mantissa = unsigned.substring(0, eInd);
            exponent = unsigned.substring(eInd);
        }

        StringBuilder number;
        StringBuilder fraction;
        int          dotInd = mantissa.indexOf('.');

        if ( dotInd == -1 ) {
            number   = new StringBuilder(mantissa);
            fraction = new StringBuilder();
        } else {
            number   = new StringBuilder(mantissa.substring(0, dotInd));
            fraction = new StringBuilder(mantissa.substring(dotInd + 1));
        }

        int numFigs  = number.length();
        int fracFigs = fraction.length();

        if ( ((numFigs==0) || number.equals("0")) && (fracFigs>0) ) {
            // Don't count leading zeros in the fraction.
            numFigs = 0;
            for ( int i=0; i<fraction.length(); ++i ) {
                if ( fraction.charAt(i) != '0' ) {
                    break;
                }
                --fracFigs;
            }
        }

        int mantFigs = numFigs + fracFigs;

        if ( sigFigs > mantFigs ) {
            // We want more figures; just append zeros to the fraction.
            for ( int i = mantFigs; i < sigFigs; ++i ) {
                fraction.append('0');
            }
        } else {
            if ( sigFigs < mantFigs && sigFigs >= numFigs ) {
                // Want fewer figures in the fraction; chop.
                int length =
                    fraction.length() - (fracFigs - (sigFigs - numFigs));
                fraction.setLength(length);
                // Round?
            } else {
                if ( sigFigs < numFigs ) {
                    // Want fewer figures in the number; turn them to
                    // zeros.
                    fraction.setLength(0); // Should already be
                                             // zero, but make sure.
                    for ( int i = sigFigs; i < numFigs; ++i ) {
                        number.setCharAt(i, '0');
                    }
                    // Round?
                }
            }
            // Else sigFigs == mantFigs, which is fine.
        }

        if ( fraction.length() == 0 ) {
            return sign + number + exponent;
        } else {
            return sign + number + "." + fraction + exponent;
        }
    }



}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

