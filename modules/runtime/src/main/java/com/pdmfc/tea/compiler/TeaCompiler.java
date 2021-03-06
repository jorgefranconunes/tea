/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.pdmfc.tea.compiler.TeaCode;
import com.pdmfc.tea.TeaCompileException;
import com.pdmfc.tea.compiler.CompilerStream;
import com.pdmfc.tea.compiler.SStatement;
import com.pdmfc.tea.compiler.SWordBlock;
import com.pdmfc.tea.compiler.SWordCmdSubst;
import com.pdmfc.tea.compiler.SWordFloat;
import com.pdmfc.tea.compiler.SWordInt;
import com.pdmfc.tea.compiler.SWordList;
import com.pdmfc.tea.compiler.SWordLong;
import com.pdmfc.tea.compiler.SWordSymbol;
import com.pdmfc.tea.compiler.SWordVarSubst;
import com.pdmfc.tea.compiler.TeaParserUtils;





/**************************************************************************
 *
 * Parses a Tea script and produces code that can be executed
 * later. Compiled code is represented as a <code>{@link TeaCode}</code>
 * object.
 *
 * <p>Objects of this class are intended to be used from within a
 * single thread.</p>
 *
 **************************************************************************/

public final class TeaCompiler
    extends Object {





    private CompilerStream _in       = null;
    private String         _fileName = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaCompiler() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * Compiles the Tea script read from the a <code>Reader</code>.
 *
 * @param reader The <code>Reader</code> where the script will be read
 * from.
 *
 * @param fileName The name to be associated with the compiled
 * code. Compile or runtime error messages will use this name when
 * identifying the source file where the error occurred.
 *
 * @return A <code>{@link TeaCode}</code> object containing the bytecode
 * that can be executed later.
 *
 * @exception IOException Thrown if there were any problems reading
 * from the given reader.
 *
 * @exception TeaCompileException Thrown if a syntax error was found
 * during compilation.
 *
 **************************************************************************/

    public TeaCode compile(final Reader reader,
                           final String fileName)
        throws IOException,
               TeaCompileException {

        TeaCode code = null;

        _fileName = fileName;
        _in       = new CompilerStream(reader);

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

    private TeaCode getBlockWhole()
        throws IOException,
               TeaCompileException {

        TeaCode code = getBlock();

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

    private TeaCode getBlockInner(final char c,
                                  final int  line)
        throws IOException,
               TeaCompileException {

        TeaCode code = getBlock();

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

    private TeaCode getBlock()
        throws IOException,
               TeaCompileException {

        TeaCode code = new TeaCode(_fileName);

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
               TeaCompileException {

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

    private List<SWord> getList(final int line)
        throws IOException,
               TeaCompileException {

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
               TeaCompileException {

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
            result = new SWordBlock(getBlockInner('}', l));
            break;
        case '$' :
            skip();
            result = new SWordVarSubst(getSymbolName());
            break;
        case '"' :
            skip();
            result = new SWordString(getString(l));
            break;
        case '(' :
            skip();
            result = new SWordList(getList(l));
            break;
        case '+' :
            result = objNumber();
            break;
        case '-' :
            result = objNumber();
            break;
        default :
            if ( Character.isDigit(c) ) {
                result = objNumber();
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

   private SWord objNumber()
         throws IOException,
                TeaCompileException {

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

    private int parseDec(final String str)
        throws TeaCompileException {

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

    private long parseLong(final String str)
        throws TeaCompileException {

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

    private int parseOct(final String str)
        throws TeaCompileException {

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

    private int parseHex(final String str)
        throws TeaCompileException {

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

    private double parseFloat(final String str)
        throws TeaCompileException {

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

         if ( isWhiteSpace(c) ) {
             break;
         }
         name.append(c);
      }

      return name.toString();
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private String getSymbolName(final char firstChar)
         throws IOException {

      StringBuilder name = new StringBuilder();

      name.append(firstChar);

      while ( !atEndOfSymbol() ) {
         char c = skip();

         if ( isWhiteSpace(c) ) {
             break;
         }
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

    private String getString(final int line)
        throws IOException,
               TeaCompileException {

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

        String result = TeaParserUtils.parseStringLiteral(name.toString());

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

            if ( c == '\\' ) {
                skip();
                if ( peek() == '\r' ) { skip(); }
                if ( peek() == '\n' ) { skip(); }
            } else {
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

        return ( (c=='\n')
                 || (c=='#')
                 || (c==';')
                 || (c==']')
                 || (c=='}')
                 || (c==')') );
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

      return ((c=='\n')
              || (c=='#')
              || (c==';')
              || (c=='[')
              || (c==']')
              || (c=='{')
              || (c=='}')
              || (c=='(')
              || (c==')'));
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

   private static boolean isWhiteSpace(final char c) {

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

    private void compileError(final String    msg,
                              final Object... fmtArgs)
        throws TeaCompileException {

        throw new TeaCompileException(msg, fmtArgs);
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

