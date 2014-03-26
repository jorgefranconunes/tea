/**************************************************************************
 *
 * Copyright (c) 2010-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;





/**************************************************************************
 *
 * Provides very simple methods to iterate over bytes of a stream.
 *
 **************************************************************************/

final class CompilerStream
    extends Object {





    private Reader _in          = null;
    private int    _currentChar = 0;
    private int    _line        = 1;





/**************************************************************************
 *
 * Initializes the stream from a <code>Reader</code>.
 *
 * @param reader The <code>Reader</code> where the chars will be
 * coming from.
 *
 * @exception IOException Thrown if there were problems reading from
 * the reader.
 *
 **************************************************************************/

    public CompilerStream(final Reader reader)
        throws IOException {

        _in          = new BufferedReader(reader);
        _currentChar = _in.read();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void close() {

        try {
            _in.close();
        } catch (IOException e) {
            // Do nothing.
        }
    }





/**************************************************************************
 *
 * Retrieves the current char in the stream. This will be the char
 * returned by the next call to <code>{@link skip()}</code>.
 *
 * @return The current char in the stream.
 *
 **************************************************************************/

    public char peek()
        throws IOException {

        if ( _currentChar == -1 ) {
            throw new IllegalStateException("read beyond end of stream");
        }
        return (char)_currentChar;
    }





/**************************************************************************
 *
 * Advances one byte in the stream. It returns the previous current
 * byte.
 *
 * @return The previous current byte in the stream.
 *
 **************************************************************************/

    public char skip()
        throws IOException {

        int previousChar = _currentChar;

        if ( previousChar == -1 ) {
            throw new IllegalStateException("read beyond end of stream");
        }
        if ( previousChar == '\n' ) {
            _line++;
        }
        _currentChar = _in.read();

        return (char)previousChar;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public boolean isAtEnd() {

        return _currentChar == -1;
    }





/**************************************************************************
 *
 * Retrieves the number of the line where the current position is
 * located. Line numbers start at one.
 *
 **************************************************************************/

    public int getCurrentLine() {

        return _line;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

