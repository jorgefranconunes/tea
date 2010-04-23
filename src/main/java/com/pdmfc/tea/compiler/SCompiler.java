/**************************************************************************
 *
 * Copyright (c) 2001-2010 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
 *
 * Revisions:
 *
 * 2010/01/28 Minor refactorings to properly use generics. (jfn)
 *
 * 2003/07/10 Added support for long integral literals
 * (e.g. 123L). (jfn)
 *
 * 2002/08/03 The getList() method now returns a java.util.List
 * instead of a com.pdmfc.tea.util.SList. (jfn).
 *
 * 2002/03/07 Added support for supports integer literals with
 * hexadecimal (e.g. 0xab) and octal (e.g. 076) representations. (jfn)
 *
 * 2001/05/12 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;

import com.pdmfc.tea.compiler.SArithExpression;
import com.pdmfc.tea.compiler.SCode;
import com.pdmfc.tea.compiler.SCompileException;
import com.pdmfc.tea.compiler.SCompilerStream;
import com.pdmfc.tea.compiler.SStatement;
import com.pdmfc.tea.compiler.STeaParserUtils;
import com.pdmfc.tea.compiler.SWordBlock;
import com.pdmfc.tea.compiler.SWordCmdSubst;
import com.pdmfc.tea.compiler.SWordExpression;
import com.pdmfc.tea.compiler.SWordExpressionBlock;
import com.pdmfc.tea.compiler.SWordFloat;
import com.pdmfc.tea.compiler.SWordInt;
import com.pdmfc.tea.compiler.SWordList;
import com.pdmfc.tea.compiler.SWordLong;
import com.pdmfc.tea.compiler.SWordSymbol;
import com.pdmfc.tea.compiler.SWordVarSubst;
import com.pdmfc.tea.util.SInputSourceFactory;





/**************************************************************************
 *
 * Parses a Tea script and produces bytecode that can be executed
 * later. The bytecode is stored inside a <code>{@link SCode}</code>
 * object.
 *
 * <p>Objects of this class are intended to be used from within a
 * single thread.</p>
 *
 **************************************************************************/

public class SCompiler
    extends Object {





    private SCompilerStream _in       = null;
    private String          _fileName = null;





/**************************************************************************
 *
 * Compiles the Tea script read from the file <TT>fileName</TT> and
 * builds an <TT>SCode</TT> object representing the compiled code.
 *
 * @param fileName The name of the file containing the Tea script.
 *
 * @param encoding The text encoding of the file to be read. If null
 * the JVM default encoding is assumed.
 *
 * @return A <TT>SCode</TT> object containing the bytecode that can be
 * executed later.
 *
 * @exception SCompileException Thrown in the following cases:
 *        <ul>
 *	  <li>The file could not be opened for reading.</li>
 *	  <li>There were problems while reading the file.</li>
 *	  <li>The Tea script had syntax errors.</li>
 *	  </ul>
 *
 **************************************************************************/

    public SCode compile(String location,
                         String encoding,
                         String fileName)
        throws IOException,
               SCompileException {

        Charset charset = findCharset(encoding);
        Reader  reader  = SInputSourceFactory.openReader(location, charset);
        SCode   code    = compile(reader, fileName);

        return code;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SCode compile(String baseLocation,
                         String location,
                         String encoding,
                         String fileName)
        throws IOException,
               SCompileException {

        Charset charset = findCharset(encoding);
        Reader  reader  =
            SInputSourceFactory.openReader(baseLocation, location, charset);
        SCode   code    = compile(reader, fileName);

        return code;
    }





/**************************************************************************
 *
 * Compiles the Tea script read from the an <code>InputStream</code>.
 *
 * @param input The input stream where the script will be read from.
 *
 * @param encoding The text encoding of the input stream to be
 * read. If null the JVM default encoding is assumed.
 *
 * @param fileName The name to be associated with the compiled
 * code. Compile or runtime error messages will use this name when
 * identifying the source file where the error occurred.
 *
 * @return A <code>{@link SCode}</code> object containing the bytecode
 * that can be executed later.
 *
 * @exception com.pdmfc.tea.compiler.SCompileException Thrown if a
 * syntax error was found during compilation or there were problems
 * reading from the <code>InputStream</code>.
 *
 **************************************************************************/

    public SCode compile(InputStream input,
                         String      encoding,
                         String      fileName)
        throws IOException,
               SCompileException {

        Charset charset     = findCharset(encoding);
        Reader  inputReader = new InputStreamReader(input, charset);
        Reader  reader      = new BufferedReader(inputReader);
        SCode   code        = compile(reader, fileName);

        return code;
    }





/**************************************************************************
 *
 * Compiles the Tea script stored in a string.
 *
 * @param script A Tea script.
 *
 * @return A <TT>SCode</TT> object containing the bytecode that can be
 * executed later.
 *
 * @exception com.pdmfc.tea.compiler.SCompileException Thrown if a
 * syntax error was found during compilation.
 *
 **************************************************************************/

    public SCode compile(String script)
        throws IOException,
               SCompileException {

        Reader reader = new StringReader(script);
        SCode  code   = compile(reader, null);

        return code;
    }





/**************************************************************************
 *
 * Compiles the Tea script read from the an <code>InputStream</code>.
 *
 * @param reader The <code>Reader</code> where the script will be read
 * from.
 *
 * @param fileName The name to be associated with the compiled
 * code. Compile or runtime error messages will use this name when
 * identifying the source file where the error occurred.
 *
 * @return A <code>{@link SCode}</code> object containing the bytecode
 * that can be executed later.
 *
 * @exception com.pdmfc.tea.compiler.SCompileException Thrown if a
 * syntax error was found during compilation or there were problems
 * reading from the <code>InputStream</code>.
 *
 **************************************************************************/

    private SCode compile(Reader reader,
                          String fileName)
        throws IOException,
               SCompileException {

        SCode code = null;

        _fileName = fileName;

        try {
            _in = new SCompilerStream(reader);
        } catch (IOException e) {
            try { reader.close(); } catch (IOException e2) {}
            throw e;
        }

        try {
            code = getBlockWhole();
        } finally {
            _in.close();
            _in       = null;
            _fileName = null;
        }

        return code;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private Charset findCharset(String charsetName)
        throws SCompileException {

        Charset charset = null;

        if ( charsetName == null ) {
            charset = Charset.defaultCharset();
        } else {
            try {
                charset = Charset.forName(charsetName);
            } catch (UnsupportedCharsetException e) {
                String msg = "Unsupported charset \"{0}\"";
                compileError(msg, charsetName);
            }
        }

        return charset;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SCode getBlockWhole()
        throws IOException,
               SCompileException {

        SCode code = getBlock();

        if ( !atEnd() ) {
            String msg = "unexpected ''{0}'' at line {1}{2}";
            compileError(msg, peek(), currentLine(), onFileMsg());
      }

      return code;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SCode getBlockInner(char c,
				int  line)
	throws IOException,
	       SCompileException {

	SCode code = getBlock();

	if ( atEnd() ) {
            String msg = "no ''{0}'' found before end of script for block starting at line {1}{2}";
            compileError(msg, c, line, onFileMsg());
	}
        if ( peek() != c ) {
            String msg = "found ''{0}'' at line {1} while expecting a ''{2}'' for block starting at line {3}{4}";
            compileError(msg, peek(), currentLine(), c, line, onFileMsg());
      }
      skip();

      return code;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SCode getBlock()
        throws IOException,
               SCompileException {

        SCode code = new SCode(_fileName);

        skipToStatement();
        while ( !atEndOfBlock() ) {
            code.addStatement(getStatement());
            skipToStatement();
        }

        return code;
    }





/**************************************************************************
 *
 * The read pointer is expected to be at the beggining of a statement.
 *
 **************************************************************************/

    private SStatement getStatement()
        throws IOException,
               SCompileException {

        SStatementFactory statFact  = new SStatementFactory(currentLine());
        SStatement        statement = null;

        while ( !atEndOfStatement() ) {
            statFact.addWord(getWord());
            skipToWord();
        }
        statement = statFact.createStatement();

        return statement;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private List<SWord> getList(int line)
        throws IOException,
               SCompileException {

        List<SWord> list = new ArrayList<SWord>();

        skipToWordInList();
        while ( !atEndOfList() ) {
            list.add(getWord());
            skipToWordInList();
        }

        if ( atEnd() ) {
            String msg = "no '')'' found before end of script for list starting at line {0}{1}";
            compileError(msg, line, onFileMsg());
        }
        if ( peek() != ')' ) {
            String msg = "found ''{0}'' at line {1} while expecting a '')'' for list starting at line {2}{3}";
            compileError(msg, peek(), currentLine(), line, onFileMsg());
        }
        skip();

        return list;
    }





/**************************************************************************
 *
 * The read pointer is expected to be at the beggining of a word.
 *
 **************************************************************************/

    private SWord getWord()
        throws IOException,
               SCompileException {

        SWord result = null;
        int  l       = currentLine();
        char c       = peek();

        switch ( c ) {
        case '[' :
            skip();
            result = new SWordCmdSubst(getBlockInner(']', l));
            break;
        case '{' :
            skip();
            if ( peek() == '`' ) {
                skip();
                result = new SWordExpressionBlock(getExpressionBlock(l));
            } else {
                result = new SWordBlock(getBlockInner('}', l));
            }
            break;
        case '$' :
            skip();
            result = new SWordVarSubst(getSymbolName());
            break;
        case '"' :
            skip();
            result = new SWordString(getString(l));
            break;
        case '`' :
            skip();
            result = new SWordExpression(getExpression(l));
            break;
        case '(' :
            skip();
            result = new SWordList(getList(l));
            break;
        case '+' :
            result = objIntOrFloat();
            break;
        case '-' :
            result = objIntOrFloat();
            break;
        default :
            if ( Character.isDigit(c) ) {
                result = objIntOrFloat();
            } else {
                result = new SWordSymbol(getSymbolName());
            }
        }

        return result;
    }





/**************************************************************************
 *
 * When this method is called the current character is either one of:
 * '+', '-', a digit.
 *
 **************************************************************************/

   private SWord objIntOrFloat()
         throws IOException,
	        SCompileException {

      boolean      justDigits   = true;
      StringBuffer buffer       = new StringBuffer();
      char         c            = skip();
      int          sign         = 1;

      switch ( c ) {
	 case '-' : sign = -1; break;
	 case '+' : break;
	 default  : buffer.append(c); break;
      }
      if ( atEndOfSymbol() ) {
	  if ( !Character.isDigit(c) ) {
	      return new SWordSymbol(getSymbolName(c));
	  }
      } else {
	  char cNext = peek();
	  if ( !Character.isDigit(c) && !Character.isDigit(cNext) ) {
	      return new SWordSymbol(getSymbolName(c));
	  }
      }
      
      while ( !atEndOfSymbol() ) {
	 c = skip();
	 if ( !Character.isDigit(c) )  {
	     justDigits = false;
	 }
	 buffer.append(c);
      }

      String value = buffer.toString();

      if ( justDigits ) {
	  if ( value.charAt(0)=='0' ) {
	      return new SWordInt(sign * parseOct(value));
	  } else {
	      return new SWordInt(sign * parseDec(value));
	  }
      } else {
	  if ( value.charAt(value.length()-1) == 'L' ) {
	      return new SWordLong(sign * parseLong(value));
	  } else if ( Character.toLowerCase(value.charAt(1)) == 'x' ) {
	      return new SWordInt(sign * parseHex(value));
	  } else {
	      return new SWordFloat(sign * parseFloat(value));
	  }
      }
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private int parseDec(String str)
	throws SCompileException {

        int result = 0;

	try {
	    result = Integer.parseInt(str);
	} catch (NumberFormatException e) {
            String msg = "invalid integer constant ({0}) at line {1}{2}";
            compileError(msg, str, currentLine(), onFileMsg());
	}

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private long parseLong(String str)
	throws SCompileException {

        long result = 0L;

	try {
	    result = Long.parseLong(str.substring(0,str.length()-1));
	} catch (NumberFormatException e) {
	    String msg = "invalid long constant ({0}) at line {1}{2}";
	    compileError(msg, str, currentLine(), onFileMsg());
	}

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private int parseOct(String str)
	throws SCompileException {

        int result = 0;

	try {
	    if ( str.length() == 1 ) {
		result = 0;
	    } else {
		result = Integer.parseInt(str.substring(1), 8);
	    }
	} catch (NumberFormatException e) {
            String msg = "invalid octal constant ({0}) at line {1}{2}";
            compileError(msg, str, currentLine(), onFileMsg());
	}

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private int parseHex(String str)
	throws SCompileException {

        int result = 0;

	try {
	    result = Integer.parseInt(str.substring(2), 16);
	} catch (NumberFormatException e) {
            String msg = "invalid hexadecimal constant ({0}) at line {1}{2}";
            compileError(msg, str, currentLine(), onFileMsg());
	}

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private double parseFloat(String str)
	throws SCompileException {

        double result = 0.0;

	try {
	    result = Double.parseDouble(str);
	} catch (NumberFormatException e) {
            String msg = "invalid float constant ({0}) at line {1}{2}";
            compileError(msg, str, currentLine(), onFileMsg());
	}

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private String getSymbolName()
         throws IOException {

      StringBuffer name = new StringBuffer();

      while ( !atEndOfSymbol() ) {
	 char c = skip();

	 if ( isWhiteSpace(c) ) break;
	 name.append(c);
      }

      return name.toString();
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private String getSymbolName(char c)
         throws IOException {

      StringBuffer name = new StringBuffer();

      name.append(c);

      while ( !atEndOfSymbol() ) {
	 c = skip();

	 if ( isWhiteSpace(c) ) break;
	 name.append(c);
      }

      return name.toString();
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private String getString(int line)
	throws IOException,
	       SCompileException {

	StringBuffer name        = new StringBuffer();
	boolean      endWasFound = false;

	while ( !atEnd() ) {
	    char c = skip();

	    if ( c == '"' ) {
		endWasFound = true;
		break;
	    }
	    if ( c == '\\' ) {
		if ( atEnd() ) {
		    name.append('\\');
		} else {
		    if ( (c=skip()) == '"' ) {
			name.append('"');
		    } else {
			name.append('\\');
			name.append(c);
		    }
		}
	    } else {
		name.append(c);
	    }
	}

	if ( !endWasFound ) {
            String msg = "no ''\"'' found before end of script for string starting at line {0}{1}";
            compileError(msg, line, onFileMsg());
	}

        String result = STeaParserUtils.unescapeString(name.toString());

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SArithExpression getExpressionBlock(int line)
	throws IOException,
	       SCompileException {

	SArithExpression result = getExpression(line);

	if ( peek() != '}' ) {
            String msg = "unexpected ''{0}'' at line {1}{2}";
            compileError(msg, peek(), currentLine(), onFileMsg());
	}
	skip();

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SArithExpression getExpression(int line)
	throws IOException,
	       SCompileException {

	SArithExpression result      = new SArithExpression();
	StringBuffer     buffer      = new StringBuffer();
	boolean          endWasFound = false;

	while ( !atEnd() ) {
	    char c = skip();

	    if ( c == '`' ) {
		endWasFound = true;
		break;
	    }
	    if ( c == '\\' ) {
		if ( atEnd() ) {
		    buffer.append('\\');
		} else {
		    if ( (c=skip()) == '`' ) {
			buffer.append('`');
		    } else {
			buffer.append('\\');
			buffer.append(c);
		    }
		}
	    } else {
		buffer.append(c);
	    }
	}

	if ( !endWasFound ) {
            String msg = "no ''`'' found before end of script for expression starting at line {0}{1}";
            compileError(msg, line, onFileMsg());
	}

	result.initialize(buffer.toString());

	return result;
    }





/**************************************************************************
 *
 * Fetches the next four characters and parses them as an hexadecimal
 * value. The hexadecimal representation can be in either upper or
 * lower case.
 *
 * @return The unicode character whose value corresponds to the
 * hexadecimal value that was fetched.
 *
 * @exception SCompileException Throw if there were not four more
 * characters to read or if any of the four charaters is not an
 * hexadecimal digit.
 *
 **************************************************************************/

    private char getUnicode()
	throws IOException,
	       SCompileException {

	int d3 = skip();
	int d2 = skip();
	int d1 = skip();
	int d0 = skip();

	if ( (d3==-1) || (d2==-1) || (d1==-1) || (d0==-1) ) {
            String msg = "end of script while reading unicode constant{0}";
            compileError(msg, onFileMsg());
	}

	d3 = Character.digit((char)d3, 16);
	d2 = Character.digit((char)d2, 16);
	d1 = Character.digit((char)d1, 16);
	d0 = Character.digit((char)d0, 16);

	if ( (d3==-1) || (d2==-1) || (d1==-1) || (d0==-1) ) {
            String msg = "invalid unicode constant on line {0}{1}";
            compileError(msg, currentLine(), onFileMsg());
	}

	return (char)((d3<<12) | (d2<<8) | (d1<<4) | d0);
    }





/**************************************************************************
 *
 * Fetches the next two characters and parses them as an octal
 * value using <code>c</code> has the first digit.
 * lower case.
 *
 * @return The unicode character whose value corresponds to the octal
 *value that was fetched.
 *
 * @exception SCompileException Throw if there were not two more
 * characters to read or if any of the three charaters is not an octal
 * digit.
 *
 **************************************************************************/

    private char getOctal(char c)
	throws IOException,
	       SCompileException {

	int d2 = c;
	int d1 = skip();
	int d0 = skip();

	if ( (d2==-1) || (d1==-1) || (d0==-1) ) {
            String msg = "end of script while reading octal constant{0}";
	    compileError(msg, onFileMsg());
	}

	d2 = Character.digit((char)d2, 8);
	d1 = Character.digit((char)d1, 8);
	d0 = Character.digit((char)d0, 8);

	if ( (d2==-1) || (d1==-1) || (d0==-1) ) {
            String msg = "invalid octal constant on line {0}{1}";
            compileError(msg, currentLine(), onFileMsg());
	}

	return (char)((d2<<6) | (d1<<3) | d0);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void skipToEOL()
	throws IOException {

	while ( !atEnd() ) {
	    if ( skip() == '\n' ) {
		return;
	    }
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void skipToStatement()
	throws IOException {

	while ( !atEnd() ) {
	    char c = peek();

	    switch ( c ) {
	    case ';' :
	    case '\n' :
		skip();
		break;
	    case '#' :
		skipToEOL();
		break;
	    default :
		if ( isWhiteSpace(c) ) {
		    skip();
		} else {
		    return;
		}
	    }
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void skipToWord()
	throws IOException {

	while ( !atEnd() ) {
	    char c = peek();

	    switch ( c ) {
	    case '\\' :
		skip();
		if ( peek() == '\r' ) { skip(); }
		if ( peek() == '\n' ) { skip(); }
		break;
	    default :
		if ( isWhiteSpace(c) ) {
		    skip();
		} else {
		    return;
		}
	    }
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void skipToWordInList()
	throws IOException {

	skipToStatement();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private boolean atEnd() {

	return _in.atEnd();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private boolean atEndOfBlock()
	throws IOException {

	if ( atEnd() ) {
	    return true;
	}

	char c = peek();

	return ((c==']') || (c=='}') || (c==')'));
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private boolean atEndOfStatement()
	throws IOException {

	if ( atEnd() ) {
	    return true;
	}

	char c = peek();

	return ( (c=='\n') ||
		 (c=='#') ||
		 (c==';') ||
		 (c==']') ||
		 (c=='}') ||
		 (c==')') );
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private boolean atEndOfList()
         throws IOException {

      if ( atEnd() ) {
	 return true;
      }

      char c = peek();

      return ((c==')') || (c==';') || (c==']') || (c=='}'));
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private boolean atEndOfSymbol()
         throws IOException {

      if ( atEnd() ) {
	 return true;
      }

      char c = peek();

      if ( isWhiteSpace(c) ) {
	 return true;
      }

      return ((c=='\n') || (c=='#') || (c==';') || (c=='[') || (c==']') ||
	      (c=='{')  || (c=='}') || (c=='(') || (c==')'));
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private char peek()
         throws IOException {

      return _in.peek();
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private char skip()
         throws IOException {

      return _in.skip();
   }





/**************************************************************************
 *
 * Retrieves the number of the input line being currently read.
 *
 **************************************************************************/

   private int currentLine() {

      return _in.currentLine();
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private static boolean isWhiteSpace(char c) {

      return (c<=' ') && (c!='\n');
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private String onFileMsg() {

      return (_fileName==null) ? "" : (" on file '" + _fileName + "'");
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void compileError(String    msg,
                              Object... fmtArgs)
        throws SCompileException {

        throw new SCompileException(msg, fmtArgs);
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

