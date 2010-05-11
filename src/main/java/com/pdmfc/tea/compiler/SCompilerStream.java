/**************************************************************************
 *
 * Copyright (c) 2010 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
 *
 *
 * Revisions:
 *
 * 2010/03/02 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;





/**************************************************************************
 *
 * Provides very simple methods to iterate over bytes of a stream.
 *
 **************************************************************************/

class SCompilerStream
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

    public SCompilerStream(Reader reader)
	throws IOException {

	_in          = new BufferedReader(reader);
	_currentChar = _in.read();
    }





/**************************************************************************
 *
 * Initializes the stream from the contents of a string.
 *
 * @param str 
 *
 * @exception IOException Thrown if there were problems reading from
 * the stream.
 *
 **************************************************************************/

    public SCompilerStream(String str)
	throws IOException {

	_in          = new StringReader(str);
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
 * 
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

