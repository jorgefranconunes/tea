/**************************************************************************
 *
 * Copyright (c) 2001-2013 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.io;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.io.PrintWriter;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.io.SIOException;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.Args;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaByteArray;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.util.SFormater;





//* 
//* <TeaClass name="TOutput"
//*           module="tea.io">
//*
//* <Overview>
//* Output stream.
//* </Overview>
//*
//* <Description>
//* Instances of <Func name="TOutput"/> represent an output stream.
//* <Func name="TOutput"/> is only used
//* as base class for other classes. It is not possible to create instances
//* of it.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Instances of this class represent output streams.
 *
 **************************************************************************/

public class SOutput
    extends STosObj {




      
    private static final String     CLASS_NAME   = "TOutput";
    private static final TeaSymbol CLASS_NAME_S =
        TeaSymbol.addSymbol(CLASS_NAME);

    /* The output stream of this object. */
    private OutputStream _outputStream = null; // can be null under JSR-223
    private PrintWriter  _outputWriter = null;

    private boolean _streamNeedsFlush = false;
    private boolean _writerNeedsFlush = false;

    /* Used to output string built from "printf" like formats. */
    private SFormaterOutput _formater = null;

    /* Signals if line buffering should be used. */
    private boolean _lineBuffering = false;





/**************************************************************************
 *
 * The constructor initializes the object internal state.
 *
 * @param myClass The <TT>STosClass</TT> object for this object.
 *
 **************************************************************************/

    public SOutput(final STosClass myClass)
        throws TeaException {

        super(myClass);
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





/**************************************************************************
 *
 * Sets up the underlying stream. One of the <TT>open()</TT> methods
 * must be called prior to any invocation of the <TT>write()</TT>,
 * <TT>writeln()</TT>, <TT>flush()</TT>, <TT>close()</TT> methods.
 *
 * @param out The <TT>OutputStream</TT> associated with this object.
 *
 **************************************************************************/

    public void open(final OutputStream out) {

        try {
            close();
        } catch (IOException e) {
            // Just ignore it...
        }
        _outputStream = new BufferedOutputStream(out);
        _outputWriter =
            new PrintWriter(new BufferedWriter(new OutputStreamWriter(out)));
   }




/**************************************************************************
 * 
 * Sets up the underlying stream. One of the <TT>open()</TT> methods must be
 * called prior to any invocation of the <TT>write()</TT>,
 * <TT>writeln()</TT>, <TT>flush()</TT>, <TT>close()</TT> methods.
 * 
 * @param out The <TT>java.io.Writer</TT> associated with this object,
 * required by JSR-223 SCR.4.3.1.  When using a
 * <TT>java.io.Writer</TT> instead of a <TT>java.io.OutputStream</TT>,
 * binary content output methods will fail.
 * 
 **************************************************************************/

    public void open(final Writer out) {

        try {
            close();
        } catch (IOException e) {
            // Just ignore it...
        }
        _outputStream = null;
        _outputWriter = new PrintWriter(out);
    }


    
    
//* 
//* <TeaMethod name="setLineBuffering"
//*            arguments="flag"
//*                className="TOutput">
//* 
//* <Overview>
//* Sets the buffering mode
//* </Overview>
//* 
//* <Parameter name="flag">
//* A boolean object signaling if the buffering mode should be line
//* buffered.
//* </Parameter>
//*
//* <Returns>
//* A reference to the object it was called for.
//* </Returns>
//* 
//* <Description>
//* By default all the <Class name="TOutput"/> objects are opened in
//* fully buffered mode. When this method is called with a <Arg name="flag"/>
//* argument of true, then the buffering mode is set to line buffered. In
//* line buffered mode, after a call to the
//* <MethodRef tosClass="TOutput" name="writeln"/> method
//* a flush is implicitly performed. When <Arg name="flag"/> is false
//* then the stream is set to fully buffered mode.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object setLineBuffering(final TeaFunction obj,
                                   final TeaContext     context,
                                   final Object[]    args)
        throws SRuntimeException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "boolean-flag");
        }

        boolean flag = Args.getBoolean(args,2).booleanValue();

        setLineBuffering(flag);

        return obj;
    }





/**************************************************************************
 *
 * Sets the buffering mode. It can be either line buffering or fully
 * bubeffring. When in line buffering mode, for every call to one of
 * the <TT>writeln</TT> methods a flush will be performed.
 *
 * @param flag
 *        If true then line buffering should be used.
 *        Otherwise full beffering will be used.
 *
 **************************************************************************/

    public void setLineBuffering(final boolean flag) {

        _lineBuffering = flag;
    }





//* 
//* <TeaMethod name="write"
//*            arguments="[arg1 ...]"
//*                className="TOutput">
//* 
//* <Overview>
//* Sends the contents of a string into the underlying output stream.
//* </Overview>
//* 
//* <Parameter name="arg1">
//* The object to be written. It may be either a string, integer or
//* float object.
//* </Parameter>
//*
//* <Returns>
//* A reference to the object it was called for.
//* </Returns>
//* 
//* <Description>
//* This methods concatenates the contents of its arguments and sends the
//* the result to the underlying stream. If one of the arguments is an
//* integer or a float than its decimal representation is used.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object write(final TeaFunction obj,
                        final TeaContext     context,
                        final Object[]    args)
        throws SRuntimeException {

        int numArgs = args.length;
        
        for ( int i=2; i<numArgs; i++ ) {
            Object arg = args[i];
            
            try {
                if ( arg instanceof String ) {
                    write((String)arg);
                    continue;
                }
                if ( arg instanceof Integer ) {
                    write(((Integer)arg).intValue());
                    continue;
                }
                if ( arg instanceof Long ) {
                    write(arg.toString());
                    continue;
                }
                if ( arg instanceof Double ) {
                    write(((Double)arg).doubleValue());
                    continue;
                }
                if ( arg instanceof TeaByteArray ) {
                    write((TeaByteArray)arg);
                    continue;
                }
            } catch (IOException e) {
                throw new SIOException(e);
            }

            throw new STypeException(args, i, "string or numeric");
        }

        return obj;
    }





/**************************************************************************
 *
 * Writes <TT>len</TT> bytes from the specified byte array starting at
 * offset <TT>off</TT> to the underlying output stream.
 *
 **************************************************************************/

    public void write(final byte[] buffer,
                      final int    off,
                      final int    len)
        throws IOException {

        flushWriter();
        _outputStream.write(buffer, off, len);
        _streamNeedsFlush = true;
    }





/**************************************************************************
 *
 * Writes <TT>len</TT> bytes from the specified byte array starting at
 * offset <TT>off</TT> to the underlying output stream.
 *
 **************************************************************************/

    public void write(final TeaByteArray bytes)
        throws IOException {

        write(bytes.getBytes(), bytes.getOffset(), bytes.getCount());
    }





/**************************************************************************
 *
 * Writes a string into the underlying output stream.
 *
 * @param aString The string to be output.
 *
 * @exception com.pdmfc.tea.modules.io.SIOException Thrown if the
 * stream has not yet been opened or if itwas closed.
 *
 **************************************************************************/

    public void write(final String aString)
        throws IOException,
               SIOException {

        checkOutputOpen();

        flushStream();
        _outputWriter.print(aString);
        _writerNeedsFlush = true;
   }





/**************************************************************************
 *
 * Writes the decimal representation of an integer into the underlying
 * output stream.
 *
 * @param value The value to be output.
 *
 * @exception com.pdmfc.tea.modules.io.SIOException Thrown if the
 * stream has not yet been opened or if it was closed.
 *
 **************************************************************************/

    public void write(final int value)
        throws IOException,
               SIOException {

        checkOutputOpen();

        flushStream();
        _outputWriter.print(value);
        _writerNeedsFlush = true;
    }





/**************************************************************************
 *
 * Writes the decimal representation of a real value into the
 * underlying output stream.
 *
 * @param value The value to be output.
 *
 * @exception com.pdmfc.tea.modules.io.SIOException Thrown if the
 * stream has not yet been opened or if it was closed.
 *
 **************************************************************************/

    public void write(final double value)
        throws IOException,
               SIOException {

        checkOutputOpen();

        flushStream();
        _outputWriter.print(value);
        _writerNeedsFlush = true;
   }





//* 
//* <TeaMethod name="writeln"
//*            arguments="[arg1 ...]"
//*                className="TOutput">
//* 
//* <Overview>
//* Sends the contents of a string into the underlying output stream
//* followed by an "end of line" sequence.
//* </Overview>
//* 
//* <Parameter name="arg1">
//* The object to be written. It may be either a string, integer or
//* float object.
//* </Parameter>
//*
//* <Returns>
//* A reference to the object it was called for.
//* </Returns>
//* 
//* <Description>
//* This methods concatenates the contents of its arguments and sends the
//* the result to the underlying stream. If one of the arguments is an
//* integer or a float than its decimal representation is used.
//* After writing all of the arguments it sends an "end of line" sequence.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object writeln(final TeaFunction obj,
                          final TeaContext     context,
                          final Object[]    args)
        throws SRuntimeException {

        write(obj, context, args);

        try {
            writeln();
        } catch (IOException e) {
            throw new SIOException(e);
        }

        return obj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void writeln()
        throws IOException,
               SIOException {

        checkOutputOpen();

        flushStream();
        _outputWriter.println();
        _writerNeedsFlush = true;
        if ( _lineBuffering ) {
            flushWriter();
        }
    }
   





/**************************************************************************
 *
 * Writes a string into the underlying output stream with a trailing
 * end-of-line.
 *
 * @param aString The string to be output.
 *
 * @exception com.pdmfc.tea.modules.io.SIOException Thrown if the
 * stream has not yet been opened or if it was closed.
 *
 **************************************************************************/

    public void writeln(final String aString)
        throws IOException,
               SIOException {

        checkOutputOpen();

        flushStream();
        _outputWriter.println(aString);
        _writerNeedsFlush = true;
        if ( _lineBuffering ) {
            flushWriter();
        }
    }





/**************************************************************************
 *
 * Writes the decimal representation of an integer value into the
 * underlying output stream with a trailing end-of-line.
 *
 * @param value The value to be output.
 *
 * @exception SIOException Thrown if the stream has not yet been
 * opened or if it was closed.
 *
 **************************************************************************/

    public void writeln(final int value)
        throws IOException,
               SIOException {

        checkOutputOpen();

        flushStream();
        _outputWriter.println(value);
        _writerNeedsFlush = true;
        if ( _lineBuffering ) {
            flushWriter();
        }
    }





/**************************************************************************
 *
 * Writes the decimal representation of an real value into the
 * underlying output stream with a trailing end-of-line.
 *
 * @param value The value to be output.
 *
 * @exception SIOException Thrown if the stream has not yet been
 * opened or if it was closed.
 *
 **************************************************************************/
    
    public void writeln(final double value)
        throws IOException,
               SIOException {

        checkOutputOpen();

        flushStream();
        _outputWriter.println(value);
        _writerNeedsFlush = true;
        if ( _lineBuffering ) {
            flushWriter();
        }
    }





//* 
//* <TeaMethod name="printf"
//*            arguments="formatString [arg1 ...]"
//*                className="TOutput">
//* 
//* <Overview>
//* Outputs a string built from a template string, in the same way
//* as the C printf
//* function.
//* </Overview>
//*
//* <Parameter name="formatString">
//* A string object representing the format string in the way of the C printf
//* function.
//* </Parameter>
//*
//* <Parameter name="arg1">
//* Object of type dependent on the format string.
//* </Parameter>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object printf(final TeaFunction obj,
                         final TeaContext     context,
                         final Object[]    args)
        throws TeaException {

        if ( args.length < 3 ) {
            throw new SNumArgException(args, "format-string [...]");
        }

        try {
            printf(Args.getString(args,2), args, 3);
        } catch (IOException e) {
            throw new SIOException(e);
        }

        return obj;
    }





/**************************************************************************
 *
 * Send a string to the output. The string is build from a "printf"
 * like format string.
 *
 * @param formatString The "printf" like format string.
 *
 * @param args Array of objects that may be referenced by the format
 * string.
 *
 * @param firstArg Index into the <TT>args</TT> array referencing the
 * first element of the array that will be used from the format
 * string.
 *
 **************************************************************************/

    public void printf(final String   formatString,
                       final Object[] args,
                       final int      firstArg)
        throws IOException,
               TeaException {

        checkOutputOpen();
        if ( _formater == null ) {
            _formater = new SFormaterOutput();
        }
        flushStream();
        _formater.setOutput(_outputWriter);
        _formater.format(formatString, args, firstArg);
        _formater.setOutput(null);
        _writerNeedsFlush = true;
    }





//* 
//* <TeaMethod name="flush"
//*                className="TOutput">
//* 
//* <Overview>
//* Flushes the internal buffers by sending its contents into the output
//* stream.
//* </Overview>
//*
//* <Returns>
//* A reference to the object it was called for.
//* </Returns>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object flush(final TeaFunction obj,
                        final TeaContext     context,
                        final Object []    args)
        throws SIOException {

        try {
            flush();
        } catch (IOException e) {
            throw new SIOException(e);
        }

        return obj;
    }





/**************************************************************************
 *
 * Flushes the underlying output stream.
 *
 * @exception SIOException Thrown if there were problems flushing the
 * stream or if the stream has not yet been opened or if it was
 * closed.
 *
 **************************************************************************/

    public void flush()
        throws IOException,
               SIOException {

        if ( _outputWriter != null ) {
            flushStream();
            flushWriter();
        }
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void flushStream()
        throws IOException {

        if ( _streamNeedsFlush ) {
            if (_outputStream != null) {
                _outputStream.flush();
            }
            _streamNeedsFlush = false;
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void flushWriter() {

        if ( _writerNeedsFlush ) {
            _outputWriter.flush();
            _writerNeedsFlush = false;
        }
    }





//* 
//* <TeaMethod name="close"
//*                className="TOutput">
//* 
//* <Overview>
//* Closes the output stream represented by the object.
//* </Overview>
//*
//* <Returns>
//* A reference to the object it was called for.
//* </Returns>
//* 
//* <Description>
//* Closing the stream releases all the underlying operating system
//* resources. After closing the stream no more methods may be called.
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
        throws SIOException{

        try {
            close();
        } catch (IOException e) {
            throw new SIOException(e);
        }

        return obj;
    }





/**************************************************************************
 *
 * Closes the underlying output stream. From now on any attempt to
 * call another method will result in an error.
 *
 * @exception IOException Throw if there where any problems while
 * closing the input stream.
 *
 **************************************************************************/

    public void close()
        throws IOException {

        flushStream();
        flushWriter();
        if ( _outputStream != null ) {
            try {
                _outputStream.close();
            } catch (IOException e) {
                // Just ignore it.
            }
            _outputStream = null;
        }
        if (_outputWriter != null) {
            _outputWriter.close();
            _outputWriter = null;
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

    public static SOutput newInstance(final TeaContext context)
        throws TeaException {

        STosObj output = STosUtil.newInstance(CLASS_NAME_S, context);

        if ( !(output instanceof SOutput) ) {
            throw new SRuntimeException("invalid " + CLASS_NAME + " class");
        }

        return (SOutput)output;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void checkOutputOpen()
        throws SIOException {

        if ( _outputWriter == null ) {
            throw new SIOException("stream is closed");
        }
    }





/**************************************************************************
 *
 * A formatter that sends the result strings to a
 * <TT>PrintWriter</TT>.
 *
 **************************************************************************/

    private static final class SFormaterOutput
        extends SFormater {





        private PrintWriter _out = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public void setOutput(final PrintWriter out) {

            _out = out;
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public void append(final String s) {

            _out.print(s);
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public void append(final char c) {

            _out.print(c);
        }


    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

