/**************************************************************************
 *
 * Copyright (c) 2001-2012 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.io;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.io.SIOException;
import com.pdmfc.tea.modules.io.SOutput;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.SArgs;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjByteArray;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypeException;





//* 
//* <TeaClass name="TInput"
//*           module="tea.io">
//*
//* <Overview>
//* Input stream.
//* </Overview>
//*
//* <Description>
//* Instances of <Class name="TInput"/> represent an input stream for
//* reading strings from a source. <Class name="TInput"/> is only used
//* as base class for other classes. It is not possible to create instances
//* of it.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Instances of this class represent input streams.
 * 
 * <p>JSR-223 requires the ability to support a
 * <code>java.io.Reader</code> instead of a
 * <code>java.io.InputStream</code>. On the other hand, Tea has
 * methods, such as <code>copyTo</code> that require the reading of
 * bytes.</p>
 *
 * <p>As such, when the underlying input source is a
 * <code>java.io.InputStream</code> methods that read bytes (such as
 * <code>copyTo</code>) work. When the undelying input source is a
 * <code>java.io.Reader</code>, reading bytes is not supported and
 * calling methods such <code>copyTo</code> will fail.</p>
 *
 **************************************************************************/

public class SInput
    extends STosObj {




      
    private static final String     CLASS_NAME   = "TInput";
    private static final SObjSymbol CLASS_NAME_S =
        SObjSymbol.addSymbol(CLASS_NAME);
    private static final SObjSymbol WRITE_METHOD =
        SObjSymbol.addSymbol("write");
    private static final int         BUFFER_SIZE  = 8192;

    private BufferedInputStream _input = null;

    // JSR-223 requires support for a java.io.Reader.  If the
    // _inputReader is used, _input is not used, and vice-versa.
    private Reader _inputReader = null;





/**************************************************************************
 *
 * The constructor initializes the object internal state.
 *
 * @param myClass
 *    The <code>STosClass</code> object for this object.
 *
 **************************************************************************/

    public SInput(final STosClass myClass)
        throws STeaException {

        super(myClass);
    }





/**************************************************************************
 *
 * The implementation for the <code>constructor</code> method.
 *
 * @throws STeaException Never thrown by this implementation. Only
 * exists so derived classes may implement this method declaring it
 * with the exception.
 *
 **************************************************************************/

    public Object constructor(final SObjFunction obj,
                              final SContext     context,
                              final Object[]     args)
        throws STeaException {

        return obj;
    }





/**************************************************************************
 *
 * Sets up the underlying stream. One of the <code>open()</code>
 * methods must be called prior to any invocation of the
 * <code>readln()</code>, <code>close()</code> methods.  <p>Opening
 * with a <code>java.io.InputStream</code> close the underlying
 * <code>java.io.Reader</code>, thus allowing reading of both bytes or
 * characters.</p>
 *
 * @param in The <code>InputStream</code> associated with this object.
 * 
 * @throws SIOException 
 *
 **************************************************************************/

    public void open(final InputStream in)
        throws SIOException {

        _input = new BufferedInputStream(in);
        if (_inputReader != null) {
            try {
                _inputReader.close();
                _inputReader = null;
            } catch (IOException e) {
                throw new SIOException(e);
            }
        }

    }



/**************************************************************************
 *
 * Sets up the underlying stream. One of the <code>open()</code>
 * methods must be called prior to any invocation of the
 * <code>readln()</code>, <code>close()</code> methods.  <p>Opening
 * with a <code>java.io.Reader</code> closes the underlying
 * <code>java.io.InputStream</code>, thus only allowing the reading of
 * characters. Methods that attempt to read bytes will fail.</p>
 *
 * @param inReader The <code>Reader</code> associated with this object.
 * 
 * @throws SIOException 
 *
 **************************************************************************/

    public void open(final Reader inReader)
        throws SIOException {

        _inputReader = inReader;
        if (_input != null) {
            try {
                _input.close();
                _input = null;
            } catch (IOException e) {
                throw new SIOException(e);
            }
        }
    }





/**************************************************************************
 *
 * Fetches the underlying <code>java.io.InputStream</code> associated
 * with this <code>TInput</code>.
 *
 * @return The <code>java.io.InputStream</code> specified in the last
 * call to <code>{@link #open(InputStream)}</code>
 *
 **************************************************************************/

    public InputStream getInputStream() {

        return _input;
    }





//* 
//* <TeaMethod name="readln"
//*                className="TInput">
//* 
//* <Overview>
//* Reads a line from the input stream represented by the object.
//* </Overview>
//*
//* <Returns>
//* The string containing the line that was read. The null object if
//* the stream is at end of file.
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

    public Object readln(final SObjFunction obj,
                         final SContext     context,
                         final Object[]     args)
        throws SIOException {

        String result = null;

        try {
            result = readln();
        } catch (IOException e) {
            throw new SIOException(e);
        }

        return (result==null) ? SObjNull.NULL : result;
    }





/**************************************************************************
 *
 * Reads a line from the underlying input stream. The trailing
 * end-of-line character is discarded. In case of an end-of-file
 * condition a <code>null</code> is returned.
 *
 * @return A <code>String</code> containing a line read from the
 * underlying input stream. When an end-of-file is reached a
 * <code>null</code> is returned.
 *
 * @exception SIOException Thrown if the input stream is not opened.
 *
 * @exception IOException Thrown if there were any problems reading
 * from the stream.
 *
 **************************************************************************/

    public String readln()
        throws IOException,
               SIOException {

        if (_input == null && _inputReader == null) {
            throw new SIOException("stream has not been opened for reading");
        }

        if ( _input != null ) {
            return readLine(_input);
        } else {
            return readLine(_inputReader);            
        }
    }





/**************************************************************************
 *
 * Reads a line from <code>in</code>. The end of a line is signaled by
 * one of three conditions: the '<code>\n</code>' character; the
 * "<code>\r\n</code>" sequence; end of file on <code>in</code>.
 *
 * @param in The <code>InputStream</code> where the line will be read
 * from.
 *
 * @return A <code>String</code> representing the line that was read
 * without the trailing new line.  The null object if <code>in</code>
 * was at end of file.
 *
 **************************************************************************/

    private static String readLine(final InputStream in)
        throws IOException {
       
        StringBuilder buffer = null;
        int           c      = in.read();

        if ( c != -1 ) {
            buffer = new StringBuilder();
        }

        while ( (c!='\n') && (c!=-1) ) {
            if ( c == '\r' ) {
                c = in.read();
                if ( c != '\n' ) {
                    buffer.append('\r');
                    buffer.append((char)c);
                    c = in.read();
                }
            } else {
                buffer.append((char)c);
                c = in.read();
            }
        }

        return (buffer==null) ? null : buffer.toString();
    }






/**************************************************************************
 *
 * Reads a line from <code>in</code>. The end of a line is signaled by
 * one of three conditions: the '<code>\n</code>' character; the
 * "<code>\r\n</code>" sequence; end of file on <code>in</code>.
 *
 * @param in The <code>Reader</code> where the line will be read from.
 *
 * @return A <code>String</code> representing the line that was read
 * without the trailing new line.  The null object if <code>in</code>
 * was at end of file.
 *
 **************************************************************************/

    private static String readLine(final Reader in)
        throws IOException {

        StringBuilder buffer = null;
        int           c      = in.read();

        if ( c != -1 ) {
            buffer = new StringBuilder();
        }
        while ( (c!='\n') && (c!=-1) ) {
            if ( c == '\r' ) {
                c = in.read();
                if ( c != '\n' ) {
                    buffer.append('\r');
                    buffer.append((char)c);
                    c = in.read();
                }
            } else {
                buffer.append((char)c);
                c = in.read();
            }
        }

        return (buffer==null) ? null : buffer.toString();
    }





//* 
//* <TeaMethod name="copyTo"
//*            arguments="output [byteCount]"
//*                className="TInput">
//* 
//* <Overview>
//* Copies the contents of the stream into an output stream.
//* </Overview>
//* 
//* <Parameter name="output">
//* An object that must responde to a <Func name="write"/> method.
//* </Parameter>
//* 
//* <Parameter name="byteCount">
//* Integer representing the maximum number of bytes to copy into the
//* the output stream.
//* </Parameter>
//*
//* <Returns>
//* A reference to the object it was called for.
//* </Returns>
//* 
//* <Description>
//* The <Var name="output"/> object received as argument is expected
//* to respond to a <Func name="write"/> method that receives a byte
//* array for argument.
//* <P>
//* This method works by reading a block from the underlying input stream
//* and passing it to <Var name="output"/> by calling its
//* <Func name="write"/> method. It repeats this two steps until an end
//* of file condition is reached in the input stream or until
//* <Arg name="byteCount"/> bytes are read from the input stream. If
//* there is no <Arg name="byteCount"/> argument then the copy proceeds
//* until the end of file on the input stream.
//* </P>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object copyTo(final SObjFunction obj,
                         final SContext     context,
                         final Object[]     args)
        throws STeaException {

        if ( (args.length!=3) && (args.length!=4) ) {
            throw new SNumArgException(args, "outputStream [byteCount]");
        }
        
        Integer byteCount = (args.length==3) ? null : SArgs.getInt(args, 3);
        STosObj output    = null;

        try {
            output = (STosObj)args[2];
        } catch (ClassCastException e) {
            throw new STypeException(args, 2, "TOutput");
        }
        
        try {
            if ( byteCount == null) {
                copyTo(context, output);
            } else {
                copyTo(context, output, byteCount.intValue());
            }
        } catch (IOException e) {
            throw new SIOException(e);
        }

        return obj;
    }





/**************************************************************************
 *
 * Copies the remaining contents of the underlying input stream into
 * the output stream received as argument.
 *
 **************************************************************************/

    public void copyTo(final SOutput out)
        throws IOException,
               SIOException {

        if (_input == null) {
            String msg = "stream has not been opened for reading bytes";
            throw new SIOException(msg);
        }
        
        byte[] buffer = new byte[BUFFER_SIZE];
        int    count  = 0;

        while ( (count=_input.read(buffer)) > 0 ) {
            out.write(buffer, 0, count);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void copyTo(final SContext context,
                       final STosObj   out)
        throws IOException,
               STeaException {

        SObjFunction  writeMethod = null;
        SObjByteArray byteArray   = new SObjByteArray();
        Object[]      methodArgs  = new Object[3];
        byte[]        buffer      = new byte[BUFFER_SIZE];
        int           count       = 0;

        writeMethod   = out.getTosClass().getMethod(WRITE_METHOD);
        methodArgs[0] = out;
        methodArgs[1] = WRITE_METHOD;
        methodArgs[2] = byteArray;

        if (_input == null) {
            String msg = "stream has not been opened for reading bytes";
            throw new SIOException(msg);
        }

        while ( (count=_input.read(buffer)) > 0 ) {
            byteArray.setContents(buffer, 0, count);
            writeMethod.exec(out, context, methodArgs);
        }
    }





/**************************************************************************
 *
 * @param totalBytesToCopy The maximum number of bytes to copy.
 *
 * @return The number of bytes that was actually copied.
 *
 **************************************************************************/

    public int copyTo(final SContext context,
                      final STosObj  out,
                      final int      totalBytesToCopy)
        throws IOException,
               STeaException {

        SObjFunction  writeMethod = null;
        SObjByteArray byteArray   = new SObjByteArray();
        Object[]      methodArgs  = new Object[3];
        byte[]        buffer      = new byte[BUFFER_SIZE];
        int           totalRead   = 0;
        int           count       = 0;
        int           total       = totalBytesToCopy;

        writeMethod   = out.getTosClass().getMethod(WRITE_METHOD);
        methodArgs[0] = out;
        methodArgs[1] = WRITE_METHOD;
        methodArgs[2] = byteArray;

        if (_input == null) {
            String msg = "stream has not been opened for reading bytes";
            throw new SIOException(msg);
        }

        while ( total > 0 ) {
            int bytesToRead = (total>BUFFER_SIZE) ? BUFFER_SIZE : total;
            if ( (count=_input.read(buffer, 0, bytesToRead)) < 0 ) {
                break;
            }
            byteArray.setContents(buffer, 0, count);
            writeMethod.exec(out, context, methodArgs);
            total     -= count;
            totalRead += count;
        }

        return totalRead;
    }





//* 
//* <TeaMethod name="close"
//*                className="TInput">
//* 
//* <Overview>
//* Closes the input stream represented by the object.
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

    public Object close(final SObjFunction obj,
                        final SContext     context,
                        final Object[]     args)
        throws SIOException {

        try {
            close();
        } catch (IOException e) {
            throw new SIOException(e);
        }

        return obj;
    }





/**************************************************************************
 *
 * Closes the underlying input stream. From now on any attempt to call
 * another method will result in an error.
 *
 * @exception IOException Throw if there where any problems while
 * closing the input stream.
 *
 **************************************************************************/

    public void close()
        throws IOException {

        if ( _input != null ) {
            try {
                _input.close();
            } catch (IOException e) {
                _input = null;
                throw e;
            }
            _input = null;
        }

        if ( _inputReader != null ) {
            try {
                _inputReader.close();
            } catch (IOException e) {
                _inputReader = null;
                throw e;
            }
            _inputReader = null;
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

    public static SInput newInstance(final SContext context)
        throws STeaException {

        STosObj input = STosUtil.newInstance(CLASS_NAME_S, context);

        if ( !(input instanceof SInput) ) {
            throw new SRuntimeException("invalid ''{0}'' class", CLASS_NAME);
        }

        return (SInput)input;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

