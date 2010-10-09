/**************************************************************************
 *
 * Copyright (c) 2001-2010 PDM&FC, All Rights Reserved.
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
 * Parses a Tea script and produces code that can be executed
 * later. Compiled code is represented as a <code>{@link SCode}</code>
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
 * Compiles a Tea script read from a file or URL.
 *
 * @param location File system path or URL identifying the entity to
 * be read and compiled.
 *
 * @param encoding The text encoding of the file to be read. If null
 * the platform default encoding is assumed.
 *
 * @param fileName Name to be associated with the compiled
 * code. Compile or runtime error messages will use this name when
 * identifying the source file where the error occurred. It is only
 * used for informational messages. If null then no references to file
 * names will appear on error messages.
 *
 * @return The object representing the compiled code that can be
 * executed later.
 *
 * @exception IOException Thrown if the provided location could not be
 * opened for reading or if there was any error during reading.
 *
 * @exception SCompileException Thrown the Tea script had syntax
 * errors.
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
 * Compiles a Tea script read from a file or URL.
 *
 * @param locationBase File system path or URL used as base for
 * <code>location</code>.
 *
 * @param location A path relative to <code>baseLocation</code>
 * identifying the entity to be read and compiled.
 *
 * @param encoding The text encoding of the file to be read. If null
 * the platform default encoding is assumed.
 *
 * @param fileName Name to be associated with the compiled
 * code. Compile or runtime error messages will use this name when
 * identifying the source file where the error occurred. It is only
 * used for informational messages. If null then no references to file
 * names will appear on error messages.
 *
 * @return The object representing the compiled code that can be
 * executed later.
 *
 * @exception IOException Thrown if the provided location could not be
 * opened for reading or if there was any error during reading.
 *
 * @exception SCompileException Thrown the Tea script had syntax
 * errors.
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
        SCode   code    = null;

        try {
            code = compile(reader, fileName);
        } finally {
            try { reader.close(); } catch (IOException e) {}
        }

        return code;
    }





/**************************************************************************
 *
 * Compiles the Tea script read from an <code>InputStream</code>.
 *
 * @param input The input stream where the script will be read from.
 *
 * @param encoding The text encoding of the file to be read. If null
 * the platform default encoding is assumed.
 *
 * @param fileName Name to be associated with the compiled
 * code. Compile or runtime error messages will use this name when
 * identifying the source file where the error occurred. It is only
 * used for informational messages. If null then no references to file
 * names will appear on error messages.
 *
 * @return The object representing the compiled code that can be
 * executed later.
 *
 * @exception IOException Thrown if the provided location could not be
 * opened for reading or if there was any error during reading.
 *
 * @exception SCompileException Thrown the Tea script had syntax
 * errors.
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
        SCode   code        = null;

        try {
            code = compile(reader, fileName);
        } finally {
            try { reader.close(); } catch (IOException e) {}
        }

        return code;
    }





/**************************************************************************
 *
 * Compiles the Tea script stored in a string.
 *
 * @param script A Tea script.
 *
 * @return A <code>SCode</code> object containing the bytecode that can be
 * executed later.
 *
 * @exception SCompileException Thrown if a syntax error was found
 * during compilation.
 *
 **************************************************************************/

    public SCode compile(String script)
        throws IOException,
               SCompileException {

        Reader reader = new StringReader(script);
        SCode  code   = null;

        try {
            code = compile(reader, null);
        } finally {
            try { reader.close(); } catch (IOException e) {}
        }

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
 * @exception SCompileException Thrown if a
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
        _in       = new SCompilerStream(reader);

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

        if ( !isAtEnd() ) {
            String msg = "unexpected ''{0}'' at line {1}{2}";
            compileError(msg, peek(), getCurrentLine(), onFileMsg());
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

	if ( isAtEnd() ) {
            String msg = "no ''{0}'' found before end of script for block starting at line {1}{2}";
            compileError(msg, c, line, onFileMsg());
	}
        if ( peek() != c ) {
            String msg = "found ''{0}'' at line {1} while expecting a ''{2}'' for block starting at line {3}{4}";
            compileError(msg, peek(), getCurrentLine(), c, line, onFileMsg());
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

        SStatement.Factory statFact  = new SStatement.Factory(getCurrentLine());
        SStatement         statement = null;

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

        if ( isAtEnd() ) {
            String msg = "no '')'' found before end of script for list starting at line {0}{1}";
            compileError(msg, line, onFileMsg());
        }
        if ( peek() != ')' ) {
            String msg = "found ''{0}'' at line {1} while expecting a '')'' for list starting at line {2}{3}";
            compileError(msg, peek(), getCurrentLine(), line, onFileMsg());
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
        int  l       = getCurrentLine();
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

      boolean       justDigits   = true;
      StringBuilder buffer       = new StringBuilder();
      char          c            = skip();
      int           sign         = 1;

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
            compileError(msg, str, getCurrentLine(), onFileMsg());
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
	    compileError(msg, str, getCurrentLine(), onFileMsg());
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
            compileError(msg, str, getCurrentLine(), onFileMsg());
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
            compileError(msg, str, getCurrentLine(), onFileMsg());
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
            compileError(msg, str, getCurrentLine(), onFileMsg());
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

      StringBuilder name = new StringBuilder();

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

      StringBuilder name = new StringBuilder();

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
 * Fetches a string literal. The double-quote initiating the string
 * has already been consumed.
 *
 * After this method returns the double-quote ending the string will
 * also have been consumed.
 *
 **************************************************************************/

    private String getString(int line)
	throws IOException,
	       SCompileException {

	StringBuilder name        = new StringBuilder();
	boolean       endWasFound = false;

	while ( !isAtEnd() ) {
	    char c = skip();

	    if ( c == '"' ) {
		endWasFound = true;
		break;
	    }
	    if ( c == '\\' ) {
		if ( isAtEnd() ) {
		    name.append('\\');
		} else {
                    name.append('\\');
		    c = skip();
                    name.append(c);
		}
	    } else {
		name.append(c);
	    }
	}

	if ( !endWasFound ) {
            String msg = "no ''\"'' found before end of script for string starting at line {0}{1}";
            compileError(msg, line, onFileMsg());
	}

        String result = STeaParserUtils.parseStringLiteral(name.toString());

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
            compileError(msg, peek(), getCurrentLine(), onFileMsg());
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

	SArithExpression  result      = new SArithExpression();
	StringBuilder     buffer      = new StringBuilder();
	boolean           endWasFound = false;

	while ( !isAtEnd() ) {
	    char c = skip();

	    if ( c == '`' ) {
		endWasFound = true;
		break;
	    }
	    if ( c == '\\' ) {
		if ( isAtEnd() ) {
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
 * 
 *
 **************************************************************************/

    private void skipToEOL()
	throws IOException {

	while ( !isAtEnd() ) {
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

	while ( !isAtEnd() ) {
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

	while ( !isAtEnd() ) {
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

    private boolean isAtEnd() {

	return _in.isAtEnd();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private boolean atEndOfBlock()
	throws IOException {

	if ( isAtEnd() ) {
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

	if ( isAtEnd() ) {
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

      if ( isAtEnd() ) {
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

      if ( isAtEnd() ) {
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

   private int getCurrentLine() {

      return _in.getCurrentLine();
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

