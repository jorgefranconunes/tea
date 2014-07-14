/**************************************************************************
 *
 * Copyright (c) 2001-2012 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.io;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.io.SIOException;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaNull;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;





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
    private static final TeaSymbol CLASS_NAME_S =
        TeaSymbol.addSymbol(CLASS_NAME);





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
        throws TeaException {

        super(myClass);
    }





/**************************************************************************
 *
 * The implementation for the <code>constructor</code> method.
 *
 * @throws TeaException Never thrown by this implementation. Only
 * exists so derived classes may implement this method declaring it
 * with the exception.
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
 * Sets up the underlying stream. One of the <code>open()</code>
 * methods must be called prior to any invocation of the
 * <code>readln()</code>, <code>close()</code> methods.
 *
 * @param in The <code>InputStream</code> associated with this object.
 * 
 * @throws SIOException 
 *
 **************************************************************************/

    public void open(final InputStream in)
        throws SIOException {

        Reader reader = new InputStreamReader(in);

        _inputReader = new BufferedReader(reader);
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
    }





/**************************************************************************
 *
 * Fetches the underlying <code>java.io.Reader</code> associated
 * with this <code>TInput</code>.
 *
 * @return The <code>java.io.Reader</code> specified in the last call
 * to <code>{@link #open(Reader)}</code>
 *
 **************************************************************************/

    public Reader getReader() {

        return _inputReader;
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

    public Object readln(final TeaFunction obj,
                         final TeaContext     context,
                         final Object[]    args)
        throws SIOException {

        String result = null;

        try {
            result = readln();
        } catch (IOException e) {
            throw new SIOException(e);
        }

        return (result==null) ? TeaNull.NULL : result;
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

        if ( _inputReader == null ) {
            throw new SIOException("stream has not been opened for reading");
        }

        String result = readLine(_inputReader);            
        
        return result;
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

    public Object close(final TeaFunction obj,
                        final TeaContext     context,
                        final Object[]    args)
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

    public static SInput newInstance(final TeaContext context)
        throws TeaException {

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

