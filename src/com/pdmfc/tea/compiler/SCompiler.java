/**************************************************************************
 *
 * Copyright (c) 2001-2008 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SCompiler.java,v 1.18 2003/07/10 18:06:54 jfn Exp $
 *
 * Revisions:
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

import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pdmfc.tea.compiler.SArithExpression;
import com.pdmfc.tea.compiler.SCode;
import com.pdmfc.tea.compiler.SCompileException;
import com.pdmfc.tea.compiler.SStatement;
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





/**************************************************************************
 *
 * Parses a Tea script and produces bytecode that can be executed
 * later. The bytecode is stored inside a <code>{@link SCode}</code>
 * object.
 *
 * <p>Objects of this class should not be used from inside of more
 * then one thread at a time.</p>
 *
 **************************************************************************/

public class SCompiler
    extends Object {


   private SStream _in       = null;
   private String  _fileName = null;





/**************************************************************************
 *
 * Compiles the Tea script read from the file <TT>fileName</TT> and
 * builds an <TT>SCode</TT> object representing the compiled code.
 *
 * @param fileName The name of the file containing the Tea script.
 *
 * @return A <TT>SCode</TT> object containing the bytecode that can be
 * executed later.
 *
 * @exception com.pdmfc.tea.compiler.SCompileException Thrown in the
 * following cases:
 *
 *        <UL>
 *	  <LI>The file could not be opened for reading.
 *	  <LI>There were problems while reading the file.
 *	  <LI>The Tea script had syntax errors.
 *	  </UL>
 *
 **************************************************************************/

   public SCode compile(String fileName)
	 throws SCompileException {

      SCode             code  = null;
      FileInputStream   fIn   = null;
      SCompileException error = null;

      _fileName = fileName;

      try {
	 fIn  = new FileInputStream(fileName);
         _in  = new SStream(fIn);
         code = getBlockWhole();
      } catch (FileNotFoundException e1) {
	 String msg = e1.getMessage();
	 error = new SCompileException("could not open file '" +
				       fileName + "'" +
				       ((e1!=null) ? (" ("+msg+")") : ""));
      } catch (IOException e2) {
	 String msg = e2.getMessage();
	 error = new SCompileException("problems reading from file '" +
				       fileName + "'" +
				       ((e2!=null) ? (" ("+msg+")") : ""));
      } catch (SCompileException e3) {
	 error = e3;
      }
      if ( fIn != null ) {
	 try {
	    fIn.close();
	 } catch (IOException e3) {
	 }
      }
      _in = null;
      if ( error != null ) {
	 throw error;
      }

      return code;
   }





/**************************************************************************
 *
 * Compiles the Tea script read from the an <code>InputStream</code>.
 *
 * @param input Reference to an <code>InputStream</code> where a
 * script will be read from.
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
			String      fileName)
	 throws SCompileException {

      SCode code = null;

      _fileName = fileName;

      try {
         _in  = new SStream(input);
         code = getBlockWhole();
      } catch (IOException e1) {
	 String msg = e1.getMessage();
	 throw new SCompileException("problems reading from input"
				     + " (" + msg + ")");
      }

      return code;
   }





/**************************************************************************
 *
 * Compiles the Tea script read from the an <TT>InputStream</TT>.
 *
 * @param input Reference to an <TT>InputStream</TT> where a script
 * will be read from.
 *
 * @return A <TT>SCode</TT> object containing the bytecode that can be
 * executed later.
 *
 * @exception com.pdmfc.tea.compiler.SCompileException Thrown if a
 * syntax error was found during compilation.
 *
 **************************************************************************/

   public SCode compile(InputStream input)
	 throws SCompileException {

       return compile(input, null);
   }





/**************************************************************************
 *
 * Compiles the Tea script stored in the <TT>script</TT> byte array.
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

   public SCode compile(byte[] script,
			int    offset,
			int    length)
	 throws SCompileException {

      SCode code = null;

      _fileName = null;

      try {
         _in  = new SStream(script, offset, length);
	 code = getBlockWhole();
      } catch (IOException e1) {
	 String msg = e1.getMessage();
	 throw new SCompileException("problems reading from input"
				     + " (" + msg + ")");
      }

      return code;
 }





/**************************************************************************
 *
 * Compiles the Tea script stored in the <TT>script</TT> byte array.
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

   public SCode compile(byte[] script)
	 throws SCompileException {

      return compile(script, 0, script.length);
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
         throw new SCompileException("unexpected '" + peek() +
				     "' at line " + currentLine() +
				     onFileMsg());
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
	    throw new SCompileException("no '" + c +
					"' found before end of script " +
					"for block starting at line " + line +
					onFileMsg());
	}
      if ( peek() != c ) {
         throw new SCompileException("found '" + peek() +
				     "' at line " + currentLine() +
				     " while expecting a '" + c + "' "  +
				     "for block starting at line " + line +
				     onFileMsg());
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
         throw new SCompileException("no ')' found before end of script " +
				     "for list starting at line " + line +
				     onFileMsg());
      }
      if ( peek() != ')' ) {
         throw new SCompileException("found '" + peek() +
				     "' at line " + currentLine() +
				     " while expecting a ')' "  +
				     "for list starting at line " + line +
				     onFileMsg());
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

      int  l = currentLine();
      char c = peek();

      switch ( c ) {
      case '[' :
	  skip();
	  return new SWordCmdSubst(getBlockInner(']', l));
      case '{' :
	  skip();
	  if ( peek() == '`' ) {
	      skip();
	      return new SWordExpressionBlock(getExpressionBlock(l));
	  } else {
	      return new SWordBlock(getBlockInner('}', l));
	  }
      case '$' :
	  skip();
	  return new SWordVarSubst(getSymbolName());
      case '"' :
	  skip();
	  return new SWordString(getString(l));
      case '`' :
	  skip();
	  return new SWordExpression(getExpression(l));
      case '(' :
	  skip();
	  return new SWordList(getList(l));
      case '+' :
	  return objIntOrFloat();
      case '-' :
	  return objIntOrFloat();
      }
      if ( Character.isDigit(c) ) {
	 return objIntOrFloat();
      }

      return new SWordSymbol(getSymbolName());
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

	try {
	    return Integer.parseInt(str);
	} catch (NumberFormatException e) {
	    throw new SCompileException("invalid integer constant (" +
					str + ")" + " at line " +
					currentLine() +
					onFileMsg());
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private long parseLong(String str)
	throws SCompileException {

	try {
	    return Long.parseLong(str.substring(0,str.length()-1));
	} catch (NumberFormatException e) {
	    String   fmt     = "invalid long constant ({0}) at line {1}{2}";
	    Object[] fmtArgs =
		{ str, new Integer(currentLine()), onFileMsg() };
	    throw new SCompileException(fmt, fmtArgs);
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private int parseOct(String str)
	throws SCompileException {

	try {
	    if ( str.length() == 1 ) {
		return 0;
	    } else {
		return Integer.parseInt(str.substring(1), 8);
	    }
	} catch (NumberFormatException e) {
	    throw new SCompileException("invalid octal constant (" +
					str + ")" + " at line " +
					currentLine() +
					onFileMsg());
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private int parseHex(String str)
	throws SCompileException {

	try {
	    return Integer.parseInt(str.substring(2), 16);
	} catch (NumberFormatException e) {
	    throw new SCompileException("invalid hexadecimal constant (" +
					str + ")" + " at line " +
					currentLine() +
					onFileMsg());
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private double parseFloat(String str)
	throws SCompileException {

	try {
	    return Double.parseDouble(str);
	} catch (NumberFormatException e) {
	    throw new SCompileException("invalid float constant (" +
					str + ")" + " at line " +
					currentLine() +
					onFileMsg());
	}
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
	    throw new SCompileException("no '\"' found before end of script " +
					 "for string starting at line " +
					 line + onFileMsg());
	}

	return unescape(name.toString());
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
	    throw new SCompileException("unexpected '" + peek() +
					"' at line " + currentLine() +
					onFileMsg());
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
	    throw new SCompileException("no '`' found before end of script " +
					 "for expression starting at line " +
					 line + onFileMsg());
	}

	result.initialize(buffer.toString());

	return result;
    }





/**************************************************************************
 *
 * Takes a Tea string literal and builds the string represented by
 * that literal.
 *
 * @param s The Tea string literal to unescape.
 *
 * @return The unescaped string.
 *
 **************************************************************************/

    public static String unescape(String s) {

	StringBuffer result    = new StringBuffer();
	int          size      = s.length();
	int          lastIndex = size - 1;

	for ( int i=0; i<size; i++ ) {
	    char c = s.charAt(i);

	    if ( c != '\\' ) {
		result.append(c);
	    } else {
		if ( i == lastIndex ) {
		    result.append(c);
		    break;
		}
		char d3, d2, d1, d0;
		int  d3i, d2i, d1i, d0i;
		switch ( c=s.charAt(++i) ) {
		case 'b' :
		    result.append('\b'); break;
		case 'f' :
		    result.append('\f'); break;
		case 'n'  :
		    result.append('\n'); break;
		case 'r'  :
		    result.append('\r'); break;
		case 't'  :
		    result.append('\t'); break;
		case 'u' :
		    if ( i == lastIndex ) {
			result.append('\\');
			result.append('u');
			break;
		    }
		    d3 = s.charAt(++i);
		    if ( i == lastIndex ) {
			result.append('\\');
			result.append('u');
			result.append(d3);
			break;
		    }
		    d2 = s.charAt(++i);
		    if ( i == lastIndex ) {
			result.append('\\');
			result.append('u');
			result.append(d3);
			result.append(d2);
			break;
		    }
		    d1 = s.charAt(++i);
		    if ( i == lastIndex ) {
			result.append('\\');
			result.append('u');
			result.append(d3);
			result.append(d2);
			result.append(d1);
			break;
		    }
		    d0 = s.charAt(++i);
		    d3i = Character.digit(d3, 16);
		    d2i = Character.digit(d2, 16);
		    d1i = Character.digit(d1, 16);
		    d0i = Character.digit(d0, 16);
		    if ( (d0i==-1) || (d1i==-1) || (d2i==-1) || (d3i==-1) ) {
			result.append('\\');
			result.append('u');
			result.append(d3);
			result.append(d2);
			result.append(d1);
			result.append(d0);
			break;
		    }
		    result.append((char)((d3i<<12) | (d2i<<8) | (d1i<<4)|d0i));
		    break;
		case '"' :
		    result.append('"'); break;
		case '\'' :
		    result.append('\''); break;
		case '\\' :
		    result.append('\\');break;
		case '0' :
		case '1' :
		case '2' :
		case '3' :
		case '4' :
		case '5' :
		case '6' :
		case '7' :
		    d2 = c;
		    if ( i == lastIndex ) {
			result.append('\\');
			result.append(d2);
			break;
		    }
		    d1 = s.charAt(++i);
		    if ( i == lastIndex ) {
			result.append('\\');
			result.append(d2);
			result.append(d1);
			break;
		    }
		    d0 = s.charAt(++i);
		    d2i = Character.digit(d2, 8);
		    d1i = Character.digit(d1, 8);
		    d0i = Character.digit(d0, 8);
		    if ( (d0i==-1) || (d1i==-1) || (d2i==-1) ) {
			result.append('\\');
			result.append(d2);
			result.append(d1);
			result.append(d0);
			break;
		    }
		    result.append((char)((d2i<<6) | (d1i<<3) | d0i));
		    break;
		default   :
		    result.append('\\');
		    result.append(c);
		}
	    }
	}

	return result.toString();
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


}





/**************************************************************************
 *
 * This class operates on a stream of bytes that may come from several
 * sources. It provides very simple methods to iterate over the bytes
 * from the script.
 *
 **************************************************************************/

class SStream
    extends Object {





    private InputStream _in          = null;
    private int         _currentChar = 0;
    private int         _line        = 1;





/**************************************************************************
 *
 * Initializes the stream from a <TT>InputStream</TT>.
 *
 * @param stream The <TT>InputStream</TT> where the bytes will be
 * coming from.
 *
 * @exception IOException Thrown if there were problems reading from
 * the file.
 *
 **************************************************************************/

    public SStream(InputStream stream)
	throws IOException {

	_in          = new BufferedInputStream(stream);
	_currentChar = _in.read();
    }





/**************************************************************************
 *
 * Initializes the stream from the contents of an array of bytes.
 *
 * @param array The array of bytes where the bytes will be coming
 * from.
 *
 * @exception IOException Thrown if there were problems reading from
 * the stream.
 *
 **************************************************************************/

    public SStream(byte[] array,
		   int    offset,
		   int    length)
	throws IOException {

	_in          = new ByteArrayInputStream(array, offset, length);
	_currentChar = _in.read();
    }





/**************************************************************************
 *
 * @return The current char in the stream.
 *
 **************************************************************************/

    public char peek()
	throws IOException {

	if ( _currentChar == -1 ) {
	    throw new IOException("read beyond end of stream");
	}
	return (char)_currentChar;
    }





/**************************************************************************
 *
 * Advances one byte in the stream. It returns the new current byte.
 *
 * @return The new current byte in the stream, or -1 if the end of the
 * stream was reached.
 *
 **************************************************************************/

    public char skip()
	throws IOException {

	int previousChar = _currentChar;

	if ( previousChar == -1 ) {
	    throw new IOException("read beyond end of stream");
	}
	if ( (char)previousChar == '\n' ) {
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

    public boolean atEnd() {

	return _currentChar == -1;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public int currentLine() {

	return _line;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

