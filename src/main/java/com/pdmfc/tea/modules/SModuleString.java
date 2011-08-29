/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules;

import java.text.MessageFormat;
import java.util.Iterator;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.STeaParserUtils;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.modules.SModuleMath;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.util.SDate;
import com.pdmfc.tea.runtime.SArgs;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;
import com.pdmfc.tea.util.SFormater;





//*
//* <TeaModule name="tea.string">
//* 
//* <Overview>
//* String manipulation.
//* </Overview>
//*
//* <Description>
//* Functions for manipulating string objects.
//* </Description>
//*
//* </TeaModule>
//*





/**************************************************************************
 *
 * String manipulation set of functions.
 *
 **************************************************************************/

public class SModuleString
    extends Object
    implements SModule {





    // Used by the functionFormat(...) method.
    private StringBuffer    _formatResult = new StringBuffer();
    private SFormaterString _formater     = new SFormaterString(_formatResult);





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SModuleString() {
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public void init(SContext context)
       throws STeaException {

       SObjFunction fmt = new SObjFunction() {
               public Object exec(SObjFunction func,
                                  SContext     context,
                                  Object[]     args)
                   throws STeaException {
                   return functionPrintf(func, context, args);
               }
           };
       
       context.newVar("str-printf", fmt);

       // For downward compatibility with previous releases.
       context.newVar("str-format", fmt);

       context.newVar("str-fmt",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionFmt(func, context, args);
                          }
                      });

       SObjFunction comp = new SObjFunction() {
               public Object exec(SObjFunction func,
                                  SContext     context,
                                  Object[]     args)
                   throws STeaException {
                   return functionCompare(func, context, args);
               }
           };

       context.newVar("str-cmp", comp);

       // For downward compatibility with Tea 1.x.
       context.newVar("str-comp", comp);

       context.newVar("str>",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionGt(func, context, args);
                          }
                      });

       context.newVar("str>=",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionGe(func, context, args);
                          }
                      });

       SObjFunction eq = new SObjFunction() {
               public Object exec(SObjFunction func,
                                  SContext     context,
                                  Object[]     args)
                   throws STeaException {
                   return functionEq(func, context, args);
               }
           };

       context.newVar("str==", eq);

       // For downward compatibility with Tea 1.x.
       context.newVar("str-eq", eq);
       context.newVar("str-eq?", eq);

       SObjFunction neq = new SObjFunction() {
               public Object exec(SObjFunction func,
                                  SContext     context,
                                  Object[]     args)
                   throws STeaException {
                   return functionNe(func, context, args);
               }
           };

       context.newVar("str!=", neq);

       // For downward compatibility with Tea 1.x.
       context.newVar("str-not-eq?", neq);
       context.newVar("str-neq", neq);

       context.newVar("str<",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionLt(func, context, args);
                          }
                      });

       context.newVar("str<=",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionLe(func, context, args);
                          }
                      });

       context.newVar("str-cat",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionCat(func, context, args);
                          }
                      });

       context.newVar("str-ends-with?",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionEndsWith(func, context, args);
                          }
                      });

       context.newVar("str-starts-with?",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionStartsWith(func, context, args);
                          }
                      });

       context.newVar("str-index-of",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionIndexOf(func, context, args);
                          }
                      });

       context.newVar("str-lower",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionLower(func, context, args);
                          }
                      });

       context.newVar("str-upper",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionUpper(func, context, args);
                          }
                      });

       context.newVar("str-len",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionLen(func, context, args);
                          }
                      });

       context.newVar("str-substring",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionSubString(func, context, args);
                          }
                      });

       context.newVar("str-trim",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionTrim(func, context, args);
                          }
                      });

       context.newVar("str-join",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionJoin(func, context, args);
                          }
                      });

       context.newVar("str-empty?",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionEmpty(func, context, args);
                          }
                      });

       context.newVar("str-not-empty?",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionNotEmpty(func, context, args);
                          }
                      });

       context.newVar("symbol->string",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionSymbolToString(func, context, args);
                          }
                      });

       context.newVar("string->int",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionStringToInt(func, context, args);
                          }
                      });

       context.newVar("string->float",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionStringToFloat(func, context, args);
                          }
                      });

       context.newVar("int->string",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionIntToString(func, context, args);
                          }
                      });

       context.newVar("float->string",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionFloatToString(func, context, args);
                          }
                      });

       context.newVar("str-unescape",
                      new SObjFunction() {
                          public Object exec(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
                              throws STeaException {
                              return functionUnescape(func, context, args);
                          }
                      });
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void end() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void start() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void stop() {

        // Nothing to do.
    }





//* 
//* <TeaFunction name="str-printf"
//*                 arguments="formatString [arg1 ...]"
//*             module="tea.string">
//*
//* <Overview>
//* Builds a string from a template string, in the same way as the C printf
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
//* <Returns>
//* A new string created from the format string and remaining arguments.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private Object functionPrintf(SObjFunction func,
                                  SContext context,
                                  Object[]   args)
        throws STeaException {

        if ( args.length<2 ) {
            throw new SNumArgException(args, "string [object ...]");
        }

        _formatResult.setLength(0);

        try {
            _formater.format(SArgs.getString(args,1), args, 2);
        } catch (SNumArgException e1) {
            throw new SRuntimeException(args, e1.getMessage());
        } catch (STypeException e2) {
            throw new SRuntimeException(args, e2.getMessage());
        }

        return _formatResult.toString();
    }





//* 
//* <TeaFunction name="str-fmt"
//*                 arguments="formatString [arg1 ...]"
//*             module="tea.string">
//*
//* <Overview>
//* Builds a string from a template string, in the same way as the
//* Java <Class name="java.text.MessageFormat"/> class.
//* </Overview>
//*
//* <Parameter name="formatString">
//* A string object representing the format string in the way of
//* the Java <Class name="java.text.MessageFormat"/> class.
//* </Parameter>
//*
//* <Parameter name="arg1">
//* Object used by the format string.
//* </Parameter>
//*
//* <Returns>
//* A new string created from the format string and remaining arguments.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private Object functionFmt(SObjFunction func,
                               SContext context,
                               Object[]   args)
        throws STeaException {

        if ( args.length<2 ) {
            throw new SNumArgException(args, "string [object ...]");
        }

        String   fmt         = SArgs.getString(args, 1);
        int      fmtArgCount = args.length - 2;
        Object[] fmtArgs     = new Object[fmtArgCount];
        String   result      = null;

        System.arraycopy(args, 2, fmtArgs, 0, fmtArgCount);
        convertForFormating(fmtArgs);

        try {
            result = MessageFormat.format(fmt, fmtArgs);
        } catch (Throwable e) {
            throw new SRuntimeException(args,
                                        "failed to format string ({0})",
                                        e.getMessage());
        }

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void convertForFormating(Object[] objs) {

        for ( int i=0,count=objs.length; i<count; i++ ) {
            Object obj = objs[i];

            if ( obj instanceof STosObj ) {
                STosObj tosObj = ((STosObj)obj).part(0);
                if ( tosObj instanceof SDate ) {
                    objs[i] = ((SDate)tosObj).getDate();
                }
            }
        }
    }





//* 
//* <TeaFunction name="str-upper"
//*                 arguments="aString"
//*             module="tea.string">
//*
//* <Overview>
//* Creates the upper case version of a string.
//* </Overview>
//*
//* <Parameter name="aString">
//* A string object.
//* </Parameter>
//*
//* <Returns>
//* A string object containing the upper case version of
//* <Arg name="aString"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionUpper(SObjFunction func,
                                        SContext     context,
                                        Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "string");
        }

        String str    = SArgs.getString(args,1);
        String result = str.toUpperCase();

        return result;
    }





//* 
//* <TeaFunction name="str-lower"
//*                 arguments="aString"
//*             module="tea.string">
//*
//* <Overview>
//* Creates the lower case version of a string.
//* </Overview>
//*
//* <Parameter name="aString">
//* A string object.
//* </Parameter>
//*
//* <Returns>
//* A string object containing the lower case version of
//* <Arg name="aString"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionLower(SObjFunction func,
                                        SContext     context,
                                        Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "string");
        }

        String str    = SArgs.getString(args,1);
        String result = str.toLowerCase();

        return result;
    }





//* 
//* <TeaFunction name="str-cmp"
//*                 arguments="aString1 aString2"
//*             module="tea.string">
//*
//* <Overview>
//* Compares two strings lexicographically.
//* </Overview>
//*
//* <Parameter name="aString1">
//* One of the strings to be compared.
//* </Parameter>
//*
//* <Parameter name="aString2">
//* The other string to be compared.
//* </Parameter>
//*
//* <Returns>
//* Returns -1 if <Arg name="aString1"/> precedes <Arg name="aString2"/>.
//* Returns 0 if <Arg name="aString1"/> is equal to <Arg name="aString"/>.
//* Returns 1 if <Arg name="aString2"/> precedes <Arg name="aString1"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionCompare(SObjFunction func,
                                          SContext     context,
                                          Object[]     args)
        throws STeaException {

        return compare(SModuleMath.MINUS_ONE,
                       SModuleMath.ZERO,
                       SModuleMath.ONE,
                       func, context, args);
    }





//* 
//* <TeaFunction name="str&gt;"
//*                 arguments="aString1 aString2"
//*             module="tea.string">
//*
//* <Overview>
//* Checks if a string is greater than another in the lexicographic sense.
//* </Overview>
//*
//* <Parameter name="aString1">
//* One of the strings to be compared.
//* </Parameter>
//*
//* <Parameter name="aString2">
//* The other string to be compared.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="aString1"/> is greater than <Arg name="aString2"/>.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionGt(SObjFunction func,
                                     SContext     context,
                                     Object[]     args)
        throws STeaException {

        return compare(Boolean.FALSE, Boolean.FALSE, Boolean.TRUE,
                       func, context, args);
    }





//* 
//* <TeaFunction name="str&gt;="
//*                 arguments="aString1 aString2"
//*             module="tea.string">
//*
//* <Overview>
//* Checks if a string is greater than or equal to another in the
//* lexicographic sense.
//* </Overview>
//*
//* <Parameter name="aString1">
//* One of the strings to be compared.
//* </Parameter>
//*
//* <Parameter name="aString2">
//* The other string to be compared.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="aString1"/> is greater than or equal to
//* <Arg name="aString2"/>.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionGe(SObjFunction func,
                                     SContext     context,
                                     Object[]     args)
        throws STeaException {

        return compare(Boolean.FALSE, Boolean.TRUE, Boolean.TRUE,
                       func, context, args);
    }





//* 
//* <TeaFunction name="str=="
//*                 arguments="aString1 aString2"
//*             module="tea.string">
//*
//* <Overview>
//* Checks if two strings are lexicographically the same.
//* </Overview>
//*
//* <Parameter name="aString1">
//* One of the strings to be compared.
//* </Parameter>
//*
//* <Parameter name="aString2">
//* The other string to be compared.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="aString1"/> is equal to <Arg name="aString2"/>.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionEq(SObjFunction func,
                                     SContext     context,
                                     Object[]     args)
        throws STeaException {

        return compare(Boolean.FALSE, Boolean.TRUE, Boolean.FALSE,
                       func, context, args);
    }





//* 
//* <TeaFunction name="str!="
//*                 arguments="aString1 aString2"
//*             module="tea.string">
//*
//* <Overview>
//* Checks if two strings are lexicographically different.
//* </Overview>
//*
//* <Parameter name="aString1">
//* One of the strings to be compared.
//* </Parameter>
//*
//* <Parameter name="aString2">
//* The other string to be compared.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="aString1"/> is different from <Arg name="aString2"/>.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionNe(SObjFunction func,
                                     SContext     context,
                                     Object[]     args)
        throws STeaException {

        return compare(Boolean.TRUE, Boolean.FALSE, Boolean.TRUE,
                       func, context, args);
    }





//* 
//* <TeaFunction name="str&lt;"
//*                 arguments="aString1 aString2"
//*             module="tea.string">
//*
//* <Overview>
//* Checks if a string is less than another in the
//* lexicographic sense.
//* </Overview>
//*
//* <Parameter name="aString1">
//* One of the strings to be compared.
//* </Parameter>
//*
//* <Parameter name="aString2">
//* The other string to be compared.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="aString1"/> is less than
//* <Arg name="aString2"/>.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionLt(SObjFunction func,
                                     SContext     context,
                                     Object[]     args)
        throws STeaException {

        return compare(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE,
                       func, context, args);
    }





//* 
//* <TeaFunction name="str&lt;="
//*                 arguments="aString1 aString2"
//*             module="tea.string">
//*
//* <Overview>
//* Checks if a string is less than or equal to another in the
//* lexicographic sense.
//* </Overview>
//*
//* <Parameter name="aString1">
//* One of the strings to be compared.
//* </Parameter>
//*
//* <Parameter name="aString2">
//* The other string to be compared.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="aString1"/> is less than or equal to
//* <Arg name="aString2"/>.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionLe(SObjFunction func,
                                     SContext     context,
                                     Object[]     args)
        throws STeaException {

        return compare(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE,
                       func, context, args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object compare(Object lt,
                                  Object eq,
                                  Object gt,
                                  SObjFunction func,
                                  SContext     context,
                                  Object[]     args)
        throws STeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "string1 string2");
        }

        String  op1    = SArgs.getString(args,1);
        String  op2    = SArgs.getString(args,2);
        int     result = op1.compareTo(op2);

        if ( result < 0 ) {
            return lt;
        }
        if ( result > 0 ) {
            return gt;
        }

        return eq;
    }





//* 
//* <TeaFunction name="str-cat"
//*                 arguments="aString1 [aString2 ...]"
//*             module="tea.string">
//*
//* <Overview>
//* Concatenates a set of strings.
//* </Overview>
//*
//* <Parameter name="aString1">
//* One of the strings to be concatenated.
//* </Parameter>
//*
//* <Returns>
//* A new string obtained by concatenating the strings received as 
//* arguments.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionCat(SObjFunction func,
                                      SContext     context,
                                      Object[]     args)
        throws STeaException {

        if ( args.length<2 ) {
            throw new SNumArgException(args, "string1 string2 ...");
        }

        StringBuffer buf = new StringBuffer(SArgs.getString(args,1));

        for ( int i=2; i<args.length; i++ ) {
            buf.append(SArgs.getString(args,i));
        }

        return buf.toString();
    }





//* 
//* <TeaFunction name="str-ends-with?"
//*                 arguments="aString1 aString2"
//*             module="tea.string">
//*
//* <Overview>
//* Checks if a string has another string as suffix.
//* </Overview>
//*
//* <Parameter name="aString1">
//* The string being inspected.
//* </Parameter>
//* 
//* <Parameter name="aString2">
//* The suffix string to search for in <Arg name="aString1"/>.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="aString2"/> is a suffix of <Arg name="aString2"/>.
//* False otherwise.
//* 
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static  Object functionEndsWith(SObjFunction func,
                                            SContext     context,
                                            Object[]     args)
        throws STeaException {
        
        if ( args.length!= 3) {
            throw new SNumArgException(args, "string1 string2");
        }

        String str1    = SArgs.getString(args,1);
        String str2    = SArgs.getString(args,2);
        Boolean result = str1.endsWith(str2) ? Boolean.TRUE : Boolean.FALSE;

        return result;
    }





//* 
//* <TeaFunction name="str-starts-with?"
//*                 arguments="aString1 aString2"
//*             module="tea.string">
//*
//* <Overview>
//* Checks if a string has another string as prefix.
//* </Overview>
//*
//* <Parameter name="aString1">
//* The string being inspected.
//* </Parameter>
//* 
//* <Parameter name="aString2">
//* The prefix string to search for in <Arg name="aString1"/>.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="aString2"/> is a prefix of <Arg name="aString2"/>.
//* False otherwise.
//* 
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionStartsWith(SObjFunction func,
                                             SContext     context,
                                             Object[]     args)
        throws STeaException {

        if ( args.length!= 3) {
            throw new SNumArgException(args, "string1 string2");
        }

        String  str1   = SArgs.getString(args,1);
        String  str2   = SArgs.getString(args,2);
        Boolean result = str1.startsWith(str2) ? Boolean.TRUE : Boolean.FALSE;

        return result;
    }





//* 
//* <TeaFunction name="str-index-of"
//*                 arguments="aString1 aString2"
//*             module="tea.string">
//*
//* <Overview>
//* Finds the index of the first occurence of a string inside another string.
//* </Overview>
//*
//* <Parameter name="aString1">
//* The string where the search for the <Arg name="aString2"/> will be
//* performed.
//* </Parameter>
//*
//* <Parameter name="aString2">
//* The string to be searched inside <Arg name="aString1"/>.
//* </Parameter>
//*
//* <Returns>
//* An integer representing the index of the first occurence of
//* <Arg name="aString2"/> inside <Arg name="aString1"/>, or -1
//* if <Arg name="aString1"/> does not contain <Arg name="aString2"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionIndexOf(SObjFunction func,
                                          SContext     context,
                                          Object[]     args)
        throws STeaException {

        if ( (args.length<3) || (args.length>4)) {
            throw new SNumArgException(args, "string sub-string [start-index]");
        }

        String str1       = SArgs.getString(args,1);
        String str2       = SArgs.getString(args,2);
        int    startIndex = (args.length==4) ? SArgs.getInt(args,3).intValue() : 0;
        int    result     = str1.indexOf(str2, startIndex);
        
        return new Integer(result);
    }





//* 
//* <TeaFunction name="str-len"
//*                 arguments="aString"
//*             module="tea.string">
//*
//* <Overview>
//* Determines the number of characters in a string.
//* </Overview>
//*
//* <Parameter name="aString">
//* A string object.
//* </Parameter>
//*
//* <Returns>
//* An integer representing the length of <Arg name="aString"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionLen(SObjFunction func,
                                      SContext context,
                                      Object[]   args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "string");
        }

        String arg    = SArgs.getString(args,1);
        int    result = arg.length();

        return new Integer(result);
    }





//* 
//* <TeaFunction name="str-substring"
//*                 arguments="aString startIndex [endIndex]"
//*             module="tea.string">
//*
//* <Overview>
//* Extracts a substring from a string.
//* </Overview>
//*
//* <Parameter name="aString">
//* A string object.
//* </Parameter>
//* 
//* <Parameter name="startIndex">
//* Integer representing the index of the first character in
//* <Arg name="aString"/> that is part of the substring.
//* The first character is at index 0, the next at index 1,
//* and so on, as for array indexing.
//* </Parameter>
//*
//* <Parameter name="endIndex">
//* Integer representing the index of the character following
//* the last character in <Arg name="aString"/> that is part of the
//* substring. If it is not specified it is taken to refer to the end
//* of the string.
//* </Parameter>
//*
//* <Returns>
//* A new string object containing the substring of <Arg name="aString"/>
//* between <Arg name="startIndex"/> and <Arg name="endIndex"/>.
//* </Returns>
//*
//* <Description>
//* If <Arg name="endIndex"/> precedes <Arg name="startIndex"/> then
//* an empty string is returned.
//* <P>
//* If <Arg name="endIndex"/> is beyond the end of <Arg name="aString"/>
//* then it is taken to refer to the end of the string.
//* </P>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionSubString(SObjFunction func,
                                            SContext     context,
                                            Object[]     args)
        throws STeaException {

        if ( (args.length<3) || (args.length>4) ) {
            throw new SNumArgException(args, "string start-index [end-index]");
        }

        String str    = SArgs.getString(args,1);
        int    start  = SArgs.getInt(args,2).intValue();
        int    size   = str.length();
        int    end    = (args.length==4) ? SArgs.getInt(args,3).intValue() :size;
        String result = null;

        if ( start < 0 ) {
            start = 0;
        }
        if ( end > size ) {
            end = size;
        }
        if ( start > end ) {
            result= "";
        } else {
            result = str.substring(start, end);
        }

        return result;
   }





//* 
//* <TeaFunction name="str-trim"
//*                 arguments="aString"
//*             module="tea.string">
//*
//* <Overview>
//* Trims whitespace off both ends of a string.
//* </Overview>
//*
//* <Parameter name="aString">
//* The string object to be processed.
//* </Parameter>
//*
//* <Returns>
//* A new string object obtained from <Arg name="aString"/> by
//* removing whitespace characters from the begining and the end of
//* it.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionTrim(SObjFunction func,
                                       SContext     context,
                                       Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "string");
        }

        String arg    = SArgs.getString(args,1);
        String result = arg.trim();

        return result;
    }





//* 
//* <TeaFunction name="str-join"
//*                 arguments="stringList separator"
//*             module="tea.string">
//*
//* <Overview>
//* Builds a new string by concatenating strings from a list.
//* </Overview>
//*
//* <Parameter name="stringList">
//* A list of string objects.
//* </Parameter>
//* 
//* <Parameter name="separator">
//* The string object used as separator between the elements of
//* <Arg name="stringList"/>.
//* </Parameter>
//*
//* <Returns>
//* A string object obtained by concatenating the strings from
//* <Arg name="stringList"/> and using <Arg name="separator"/>
//* between each element.
//* 
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionJoin(SObjFunction func,
                                       SContext     context,
                                       Object[]     args)
        throws STeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "string-list string");
        }

        SObjPair list      = SArgs.getPair(args,1);
        String   separator = SArgs.getString(args,2);
        Iterator i         = list.iterator();

        if ( !i.hasNext() ) {
            return "";
        }

        StringBuffer buf = new StringBuffer(256);

        for ( int index=0; i.hasNext(); ++index ) {
            if ( index > 0 ) {
                buf.append(separator);
            }
            Object element = i.next();
            String str     = null;

            try {
                str = (String)element;
            } catch (ClassCastException e) {
                String msg = "list element {0} should be a string, not a {1}";
                throw new SRuntimeException(args,
                                            msg,
                                            String.valueOf(index),
                                            STypes.getTypeName(element));
            }

            buf.append(str);
        }

        return buf.toString();
    }





//* 
//* <TeaFunction name="str-empty?"
//*                 arguments="aString"
//*             module="tea.string">
//*
//* <Overview>
//* Checks if a string as zero length.
//* </Overview>
//*
//* <Parameter name="aString1">
//* A string object.
//* </Parameter>
//*
//* <Returns>
//* True if the <Arg name="aString1"/> argument has no characters.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionEmpty(SObjFunction func,
                                        SContext     context,
                                        Object[]     args)
        throws STeaException {

        if ( args.length!= 2) {
            throw new SNumArgException(args, "string");
        }

        String  str    = SArgs.getString(args,1);
        int     length = str.length();
        Boolean result = (length==0) ? Boolean.TRUE : Boolean.FALSE;

        return result;
    }





//* 
//* <TeaFunction name="str-not-empty?"
//*                 arguments="aString"
//*             module="tea.string">
//*
//* <Overview>
//* Checks if a string is not empty.
//* </Overview>
//*
//* <Parameter name="aString1">
//* A string object.
//* </Parameter>
//*
//* <Returns>
//* True if the <Arg name="aString1"/> argument is not empty.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionNotEmpty(SObjFunction func,
                                           SContext     context,
                                           Object[]     args)
        throws STeaException {

        if ( args.length!= 2) {
            throw new SNumArgException(args, "string");
        }

        String  str    = SArgs.getString(args,1);
        int     length = str.length();
        Boolean result = (length==0) ? Boolean.FALSE : Boolean.TRUE;

        return result;
    }





//* 
//* <TeaFunction name="symbol->string"
//*                 arguments="aSymbol"
//*             module="tea.string">
//*
//* <Overview>
//* Generates a string with the name of a symbol
//* </Overview>
//*
//* <Parameter name="aSymbol">
//* A symbol object.
//* </Parameter>
//*
//* <Returns>
//* A new string object containing name of the symbol received as argument
//* <Arg name="aSymbol"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionSymbolToString(SObjFunction func,
                                                 SContext context,
                                                 Object[]   args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "symbol");
        }

        return SArgs.getSymbol(args,1).getName();
    }





//* 
//* <TeaFunction name="string->int"
//*                 arguments="aString"
//*             module="tea.string">
//*
//* <Overview>
//* Converts a string into an integer object.
//* </Overview>
//*
//* <Parameter name="aString">
//* String object that is supposed to contain the decimal representation
//* of an integer  value. If the string starts with a leading "-" character,
//* then the value is considered negative. A leading "+" character is not
//* accepted.
//* </Parameter>
//*
//* <Returns>
//* An integer object if <Arg name="aString"/> contains the representation
//* of a decimal value. The null object otherwise.
//* </Returns>
//*
//* <Description>
//* This function tries to convert a string containing the representation
//* of an integer value in base 10 to the corresponding integer value.
//* If the string does not contain a valid integer representation than
//* the null value will be returned.
//* 
//* <P>Trailing or leading spaces are nor allowed.</P>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionStringToInt(SObjFunction func,
                                              SContext     context,
                                              Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "string");
        }

        Object result = SObjNull.NULL;

        try {
            result = new Integer(SArgs.getString(args,1));
        } catch (NumberFormatException e) {
        }

        return result;
    }





//* 
//* <TeaFunction name="string->float"
//*                 arguments="aString"
//*             module="tea.string">
//*
//* <Overview>
//* Converts a string into a float object.
//* </Overview>
//*
//* <Parameter name="aString">
//* String object that is supposed to contain the decimal representation
//* of a floating point value.
//* </Parameter>
//*
//* <Returns>
//* A float object if <Arg name="aString"/> contains the representation
//* of a decimal value. The null object otherwise.
//* </Returns>
//*
//* <Description>
//* Traling or leading spaces are nor allowed.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionStringToFloat(SObjFunction func,
                                                SContext     context,
                                                Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "string");
        }

        String str    = SArgs.getString(args,1);
        Object result = SObjNull.NULL;

        try {
            result = new Double(str);
        } catch (NumberFormatException e) {
        }

        return result;
    }





//* 
//* <TeaFunction name="int->string"
//*                 arguments="aValue"
//*             module="tea.string">
//*
//* <Overview>
//* Generates a string with the integer decimal representation of a
//* numeric value.
//* </Overview>
//*
//* <Parameter name="aValue">
//* A numeric object.
//* </Parameter>
//*
//* <Returns>
//* A new string object containing the integer decimal representation
//* of <Arg name="aValue"/>.
//* </Returns>
//*
//* <Description>
//* 
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionIntToString(SObjFunction func,
                                              SContext     context,
                                              Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "integer");
        }

        return String.valueOf(SArgs.getNumber(args,1).intValue());
    }





//* 
//* <TeaFunction name="float->string"
//*                 arguments="aValue"
//*             module="tea.string">
//*
//* <Overview>
//* Generates a string with the decimal representation of a
//* numeric value.
//* </Overview>
//*
//* <Parameter name="aValue">
//* A numeric object that may represent a floating point value.
//* </Parameter>
//*
//* <Returns>
//* A new string object containing the decimal representation
//* of <Arg name="aValue"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionFloatToString(SObjFunction func,
                                                SContext     context,
                                                Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "float");
        }

        return String.valueOf(SArgs.getNumber(args,1).doubleValue());
    }





//* 
//* <TeaFunction name="str-unescape"
//*                 arguments="aString"
//*             module="tea.string">
//*
//* <Overview>
//* Replaces Tea escape sequences by its corresponding characters.
//* </Overview>
//*
//* <Parameter name="aString">
//* A string object.
//* </Parameter>
//*
//* <Returns>
//* A new string object obtained from <Arg name="aString"/> by
//* replacing the Tea escape sequence by its corresponding
//* characters.
//* </Returns>
//*
//* <Description>
//*
//* 
//* The following escape sequences are recognized:
//* 
//* <ul>
//*     <li><b><code>\b</code></b> - Backspace</li>
//*     <li><b><code>\f</code></b> - Form feed.</li>
//*     <li><b><code>\n</code></b> - Newline</li>
//*     <li><b><code>\r</code></b> - Carriage return.</li>
//*     <li><b><code>\&quot;</code></b> - Double quote.</li>
//*     <li><b><code>\'</code></b> - Single quote.</li>
//*     <li><b><code>\\</code></b> - Backslash.</li>
//*     <li><b><code>\xxx</code></b> - Character in octal (000-377).</li>
//*     <li><b><code>\&#117;xxxx</code></b> - Unicode character in hexadecimal (0000-ffff). The hexadecimal letters can be in either upper or lower case.</li>
//* </ul>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionUnescape(SObjFunction func,
                                           SContext     context,
                                           Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "string");
        }

        String str    = SArgs.getString(args,1);
        String result = STeaParserUtils.parseStringLiteral(str);

        return result;
    }





/**************************************************************************
 *
 * This class implements a formatter that stores the result in a
 * <TT>StringBuffer</TT>.
 *
 **************************************************************************/

    private class SFormaterString
        extends SFormater {


        private StringBuffer _result;





/**************************************************************************
 *
 * @param resultString
 *     The <TT>StringBuffer</TT> whre the result is stored.
 *
 **************************************************************************/

        public SFormaterString(StringBuffer resultString) {
        
            _result = resultString;
        }





/**************************************************************************
 *
 * Appends a string to the formated result so far.
 *
 * @param s
 *    The string to be appended.
 *
 **************************************************************************/

        protected void append(String s) {

            _result.append(s);
        }





/**************************************************************************
 *
 * Appends a char to the formated result so far.
 *
 * @param c
 *    The char to be appended.
 *
 **************************************************************************/

        protected void append(char c) {

            _result.append(c);
        }

    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

