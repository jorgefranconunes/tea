/**************************************************************************
 *
 * Copyright (c) 2001-2012 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.string;

import java.text.MessageFormat;
import java.util.Iterator;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.STeaParserUtils;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.modules.math.SModuleMath;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.util.SDate;
import com.pdmfc.tea.runtime.SArgs;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
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
 * Tea module that provides functions for manipulating strings.
 *
 **************************************************************************/

public final class SModuleString
    extends Object
    implements SModule {





    // Used by the functionFormat(...) method.
    private StringBuilder   _formatResult = new StringBuilder();
    private SFormaterString _formater     = new SFormaterString(_formatResult);





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SModuleString() {

       // Nothing to do.
   }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void init(final SContext context)
        throws STeaException {
       
        context.newVar("str-printf",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionPrintf(func, context, args);
                           }
                       });

        context.newVar("str-fmt",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionFmt(func, context, args);
                           }
                       });

        context.newVar("str-cmp",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionCompare(func, context, args);
                           }
                       });

        context.newVar("str>",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionGt(func, context, args);
                           }
                       });

        context.newVar("str>=",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionGe(func, context, args);
                           }
                       });

       context.newVar("str==",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionEq(func, context, args);
                          }
                      });
       
       context.newVar("str!=",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionNe(func, context, args);
                          }
                      });

       context.newVar("str<",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionLt(func, context, args);
                          }
                      });

       context.newVar("str<=",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionLe(func, context, args);
                          }
                      });

       context.newVar("str-cat",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionCat(func, context, args);
                          }
                      });

       context.newVar("str-ends-with?",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionEndsWith(func, context, args);
                          }
                      });

       context.newVar("str-starts-with?",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionStartsWith(func, context, args);
                          }
                      });

       context.newVar("str-index-of",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionIndexOf(func, context, args);
                          }
                      });

       context.newVar("str-last-index-of",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionLastIndexOf(func, context, args);
                          }
                      });

       context.newVar("str-lower",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionLower(func, context, args);
                          }
                      });

       context.newVar("str-upper",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionUpper(func, context, args);
                          }
                      });

       context.newVar("str-len",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionLen(func, context, args);
                          }
                      });

       context.newVar("str-substring",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionSubString(func, context, args);
                          }
                      });

       context.newVar("str-trim",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionTrim(func, context, args);
                          }
                      });

       context.newVar("str-join",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionJoin(func, context, args);
                          }
                      });

       context.newVar("str-empty?",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionEmpty(func, context, args);
                          }
                      });

       context.newVar("str-not-empty?",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionNotEmpty(func, context, args);
                          }
                      });

       context.newVar("symbol->string",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionSymbolToString(func,context,args);
                          }
                      });

       context.newVar("string->int",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionStringToInt(func, context, args);
                          }
                      });

       context.newVar("string->float",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionStringToFloat(func, context, args);
                          }
                      });

       context.newVar("int->string",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionIntToString(func, context, args);
                          }
                      });

       context.newVar("float->string",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionFloatToString(func, context, args);
                          }
                      });

       context.newVar("str-unescape",
                      new SObjFunction() {
                          public Object exec(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
                              throws STeaException {
                              return functionUnescape(func, context, args);
                          }
                      });
   }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void end() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    public void start() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void stop() {

        // Nothing to do.
    }





//* 
//* <TeaFunction name="str-printf"
//*              arguments="formatString [arg1 ...]"
//*              module="tea.string">
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

    private Object functionPrintf(final SObjFunction func,
                                  final SContext     context,
                                  final Object[]     args)
        throws STeaException {

        SArgs.checkCountAtLeast(args, 2, "string [object ...]");

        _formatResult.setLength(0);

        try {
            _formater.format(SArgs.getString(args,1), args, 2);
        } catch ( SNumArgException e1 ) {
            throw new SRuntimeException(args, e1.getMessage());
        } catch ( STypeException e2 ) {
            throw new SRuntimeException(args, e2.getMessage());
        }

        return _formatResult.toString();
    }





//* 
//* <TeaFunction name="str-fmt"
//*              arguments="formatString [arg1 ...]"
//*              module="tea.string">
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

    private Object functionFmt(final SObjFunction func,
                               final SContext context,
                               final Object[]   args)
        throws STeaException {

        SArgs.checkCountAtLeast(args, 2, "string [object ...]");

        String   fmt         = SArgs.getString(args, 1);
        int      fmtArgCount = args.length - 2;
        Object[] fmtArgs     = new Object[fmtArgCount];
        String   result      = null;

        System.arraycopy(args, 2, fmtArgs, 0, fmtArgCount);
        convertForFormating(fmtArgs);

        try {
            result = MessageFormat.format(fmt, fmtArgs);
        } catch ( Throwable e ) {
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

    private void convertForFormating(final Object[] objs) {

        for ( int i=0, count=objs.length; i<count; i++ ) {
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
//*              arguments="str"
//*              module="tea.string">
//*
//* <Overview>
//* Creates the upper case version of a string.
//* </Overview>
//*
//* <Parameter name="str">
//* A string object.
//* </Parameter>
//*
//* <Returns>
//* A string object containing the upper case version of
//* <Arg name="str"/>.
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

    private static Object functionUpper(final SObjFunction func,
                                        final SContext     context,
                                        final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "string");

        String str    = SArgs.getString(args, 1);
        String result = str.toUpperCase();

        return result;
    }





//* 
//* <TeaFunction name="str-lower"
//*              arguments="aString"
//*              module="tea.string">
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

    private static Object functionLower(final SObjFunction func,
                                        final SContext     context,
                                        final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "string");

        String str    = SArgs.getString(args,1);
        String result = str.toLowerCase();

        return result;
    }





//* 
//* <TeaFunction name="str-cmp"
//*              arguments="aString1 aString2"
//*              module="tea.string">
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

    private static Object functionCompare(final SObjFunction func,
                                          final SContext     context,
                                          final Object[]     args)
        throws STeaException {

        return compare(SModuleMath.MINUS_ONE,
                       SModuleMath.ZERO,
                       SModuleMath.ONE,
                       func,
                       context,
                       args);
    }





//* 
//* <TeaFunction name="str&gt;"
//*              arguments="aString1 aString2"
//*              module="tea.string">
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

    private static Object functionGt(final SObjFunction func,
                                     final SContext     context,
                                     final Object[]     args)
        throws STeaException {

        return compare(Boolean.FALSE,
                       Boolean.FALSE,
                       Boolean.TRUE,
                       func,
                       context,
                       args);
    }





//* 
//* <TeaFunction name="str&gt;="
//*              arguments="aString1 aString2"
//*              module="tea.string">
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

    private static Object functionGe(final SObjFunction func,
                                     final SContext     context,
                                     final Object[]     args)
        throws STeaException {

        return compare(Boolean.FALSE,
                       Boolean.TRUE,
                       Boolean.TRUE,
                       func,
                       context,
                       args);
    }





//* 
//* <TeaFunction name="str=="
//*              arguments="aString1 aString2"
//*              module="tea.string">
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

    private static Object functionEq(final SObjFunction func,
                                     final SContext     context,
                                     final Object[]     args)
        throws STeaException {

        return compare(Boolean.FALSE,
                       Boolean.TRUE,
                       Boolean.FALSE,
                       func,
                       context,
                       args);
    }





//* 
//* <TeaFunction name="str!="
//*              arguments="aString1 aString2"
//*              module="tea.string">
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

    private static Object functionNe(final SObjFunction func,
                                     final SContext     context,
                                     final Object[]     args)
        throws STeaException {

        return compare(Boolean.TRUE,
                       Boolean.FALSE,
                       Boolean.TRUE,
                       func,
                       context,
                       args);
    }





//* 
//* <TeaFunction name="str&lt;"
//*              arguments="aString1 aString2"
//*              module="tea.string">
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

    private static Object functionLt(final SObjFunction func,
                                     final SContext     context,
                                     final Object[]     args)
        throws STeaException {

        return compare(Boolean.TRUE,
                       Boolean.FALSE,
                       Boolean.FALSE,
                       func,
                       context,
                       args);
    }





//* 
//* <TeaFunction name="str&lt;="
//*              arguments="aString1 aString2"
//*              module="tea.string">
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

    private static Object functionLe(final SObjFunction func,
                                     final SContext     context,
                                     final Object[]     args)
        throws STeaException {

        return compare(Boolean.TRUE,
                       Boolean.TRUE,
                       Boolean.FALSE,
                       func,
                       context,
                       args);
    }





/**************************************************************************
 *
 * Utility method that performs most of the work for functions
 * comparing strings.
 *
 * @param lt The object returned if first string is less than the
 * second.
 *
 * @param eq The object returned if the first string is equal to the
 * second.
 *
 * @param gt The object returned if the first string is greater than
 * the second.
 *
 * @param context The scope where the original Tea function is being
 * called.
 *
 * @param args The arguments received by the original Tea function
 * being called.
 *
 * @return One of the <code>lt</code>, <code>eq</code>,
 * <code>gt</code> arguments.
 *
 **************************************************************************/

    private static Object compare(final Object       lt,
                                  final Object       eq,
                                  final Object       gt,
                                  final SObjFunction func,
                                  final SContext     context,
                                  final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 3, "string1 string2");

        String  op1        = SArgs.getString(args,1);
        String  op2        = SArgs.getString(args,2);
        int     comparison = op1.compareTo(op2);
        Object  result     = null;

        if ( comparison < 0 ) {
            result = lt;
        } else if ( comparison > 0 ) {
            result = gt;
        } else {
            result = eq;
        }

        return result;
    }





//* 
//* <TeaFunction name="str-cat"
//*              arguments="aString1 [aString2 ...]"
//*              module="tea.string">
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

    private static Object functionCat(final SObjFunction func,
                                      final SContext     context,
                                      final Object[]     args)
        throws STeaException {

        SArgs.checkCountAtLeast(args, 2, "string ...");

        String        arg1   = SArgs.getString(args,1);
        StringBuilder buffer = new StringBuilder(arg1);

        for ( int i=2; i<args.length; i++ ) {
            String arg = SArgs.getString(args, i);
            buffer.append(arg);
        }

        String result = buffer.toString();

        return result;
    }





//* 
//* <TeaFunction name="str-ends-with?"
//*              arguments="aString1 aString2"
//*              module="tea.string">
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

    private static  Object functionEndsWith(final SObjFunction func,
                                            final SContext     context,
                                            final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 3, "string1 string2");

        String str1    = SArgs.getString(args,1);
        String str2    = SArgs.getString(args,2);
        Boolean result = Boolean.valueOf(str1.endsWith(str2));

        return result;
    }





//* 
//* <TeaFunction name="str-starts-with?"
//*              arguments="aString1 aString2"
//*              module="tea.string">
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

    private static Object functionStartsWith(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 3, "string1 string2");

        String  str1   = SArgs.getString(args,1);
        String  str2   = SArgs.getString(args,2);
        Boolean result = Boolean.valueOf(str1.startsWith(str2));

        return result;
    }





//* 
//* <TeaFunction name="str-index-of"
//*              arguments="string sub-string"
//*              module="tea.string">
//* 
//* <Prototype arguments="string sub-string start-index"/>
//*
//* <Overview>
//* Finds the index of the first occurence of a string inside another string.
//* </Overview>
//*
//* <Parameter name="string">
//* The string where the search for the <Arg name="sub-string"/> will be
//* performed.
//* </Parameter>
//*
//* <Parameter name="sub-string">
//* The string to be searched inside <Arg name="string"/>.
//* </Parameter>
//*
//* <Parameter name="start-index">
//* Optional argument that gives the starting position in <Arg name="string"/>
//* to search for the <Arg name="sub-string"/>. If not given it will default
//* to zero.
//* </Parameter>
//*
//* <Returns>
//* An integer representing the index of the first occurence of
//* <Arg name="sub-string"/> inside <Arg name="string"/>, or -1
//* if <Arg name="string"/> does not contain <Arg name="sub-string"/>.
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

    private static Object functionIndexOf(final SObjFunction func,
                                          final SContext     context,
                                          final Object[]     args)
        throws STeaException {

        SArgs.checkCountBetween(args,3,4, "string sub-string [start-index]");

        String str1       = SArgs.getString(args,1);
        String str2       = SArgs.getString(args,2);
        int    startIndex =
            (args.length==4) ? SArgs.getInt(args,3).intValue() : 0;
        int     indexOf   = str1.indexOf(str2, startIndex);
        Integer result    = Integer.valueOf(indexOf);

        return result;
    }





//* 
//* <TeaFunction name="str-last-index-of"
//*              arguments="string sub-string"
//*              module="tea.string">
//* 
//* <Prototype arguments="string sub-string end-index"/>
//*
//* <Overview>
//* Finds the index of the last occurence of a string inside another string.
//* </Overview>
//*
//* <Parameter name="string">
//* The string where the search for the <Arg name="sub-string"/> will be
//* performed.
//* </Parameter>
//*
//* <Parameter name="sub-string">
//* The string to be searched inside <Arg name="string"/>.
//* </Parameter>
//*
//* <Parameter name="end-index">
//* Optional argument that gives the last position in <Arg name="string"/>
//* to search for the <Arg name="sub-string"/>. If not given it will default
//* to the length of <Arg name="string"/>.
//* </Parameter>
//*
//* <Returns>
//* An integer representing the index of the last occurence of
//* <Arg name="sub-string"/> inside <Arg name="string"/>, or -1
//* if <Arg name="string"/> does not contain <Arg name="sub-string"/>.
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

    private static Object functionLastIndexOf(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
        throws STeaException {

        SArgs.checkCountBetween(args,3,4, "string sub-string [start-index]");

        String str1       = SArgs.getString(args,1);
        String str2       = SArgs.getString(args,2);
        int    startIndex =
            (args.length==4) ? SArgs.getInt(args,3).intValue() : str1.length();
        int    indexOf    = str1.lastIndexOf(str2, startIndex);
        int    result     = Integer.valueOf(indexOf);
        
        return result;
    }





//* 
//* <TeaFunction name="str-len"
//*              arguments="str"
//*              module="tea.string">
//*
//* <Overview>
//* Determines the number of characters in a string.
//* </Overview>
//*
//* <Parameter name="str">
//* A string object.
//* </Parameter>
//*
//* <Returns>
//* An integer representing the length of <Arg name="str"/>.
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

    private static Object functionLen(final SObjFunction func,
                                      final SContext context,
                                      final Object[]   args)
        throws STeaException {

        SArgs.checkCount(args, 2, "string");

        String  str    = SArgs.getString(args,1);
        int     strLen = str.length();
        Integer result = Integer.valueOf(strLen);

        return result;
    }





//* 
//* <TeaFunction name="str-substring"
//*              arguments="aString startIndex"
//*              module="tea.string">
//* 
//* <Prototype arguments="endIndex"/>
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

    private static Object functionSubString(final SObjFunction func,
                                            final SContext     context,
                                            final Object[]     args)
        throws STeaException {

        SArgs.checkCountBetween(args, 3, 4, "string start-index [end-index]");

        String str    = SArgs.getString(args,1);
        int    start  = SArgs.getInt(args,2).intValue();
        int    size   = str.length();
        int    end    =
            (args.length==4) ? SArgs.getInt(args,3).intValue() : size;
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
//*              arguments="aString"
//*              module="tea.string">
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

    private static Object functionTrim(final SObjFunction func,
                                       final SContext     context,
                                       final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "string");

        String arg    = SArgs.getString(args,1);
        String result = arg.trim();

        return result;
    }





//* 
//* <TeaFunction name="str-join"
//*              arguments="stringList separator"
//*              module="tea.string">
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

    private static Object functionJoin(final SObjFunction func,
                                       final SContext     context,
                                       final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 3, "string-list string");

        SObjPair list      = SArgs.getPair(args,1);
        String   separator = SArgs.getString(args,2);
        Iterator i         = list.iterator();

        if ( !i.hasNext() ) {
            return "";
        }

        StringBuilder buffer = new StringBuilder(256);

        for ( int index=0; i.hasNext(); ++index ) {
            if ( index > 0 ) {
                buffer.append(separator);
            }
            Object element = i.next();
            String str     = null;

            try {
                str = (String)element;
            } catch ( ClassCastException e ) {
                String msg = "list element {0} should be a string, not a {1}";
                throw new STypeException(args,
                                         msg,
                                         String.valueOf(index),
                                         STypes.getTypeName(element));
            }

            buffer.append(str);
        }

        String result = buffer.toString();

        return result;
    }





//* 
//* <TeaFunction name="str-empty?"
//*              arguments="aString"
//*              module="tea.string">
//*
//* <Overview>
//* Checks if a string has zero length.
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

    private static Object functionEmpty(final SObjFunction func,
                                        final SContext     context,
                                        final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "string");

        String  str     = SArgs.getString(args,1);
        boolean isEmpty = str.isEmpty();
        Boolean result  = Boolean.valueOf(isEmpty);

        return result;
    }





//* 
//* <TeaFunction name="str-not-empty?"
//*              arguments="aString"
//*              module="tea.string">
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

    private static Object functionNotEmpty(final SObjFunction func,
                                           final SContext     context,
                                           final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "string");

        String  str        = SArgs.getString(args,1);
        boolean isNotEmpty = !str.isEmpty();
        Boolean result     = Boolean.valueOf(isNotEmpty);

        return result;
    }





//* 
//* <TeaFunction name="symbol->string"
//*              arguments="aSymbol"
//*              module="tea.string">
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

    private static Object functionSymbolToString(final SObjFunction func,
                                                 final SContext context,
                                                 final Object[]   args)
        throws STeaException {

        SArgs.checkCount(args, 2, "symbol");

        SObjSymbol symbol = SArgs.getSymbol(args, 1);
        String     result = symbol.getName();

        return result;
    }





//* 
//* <TeaFunction name="string->int"
//*              arguments="aString"
//*              module="tea.string">
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

    private static Object functionStringToInt(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "string");

        Object result = null;
        String str    = SArgs.getString(args, 1);

        try {
            result = Integer.valueOf(str);
        } catch ( NumberFormatException e ) {
            result = SObjNull.NULL;
        }

        return result;
    }





//* 
//* <TeaFunction name="string->float"
//*              arguments="aString"
//*              module="tea.string">
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

    private static Object functionStringToFloat(final SObjFunction func,
                                                final SContext     context,
                                                final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "string");

        Object result = null;
        String str    = SArgs.getString(args,1);

        try {
            result = Double.valueOf(str);
        } catch ( NumberFormatException e ) {
            result = SObjNull.NULL;
        }

        return result;
    }





//* 
//* <TeaFunction name="int->string"
//*              arguments="aValue"
//*              module="tea.string">
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

    private static Object functionIntToString(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "integer");

        Number value  = SArgs.getNumber(args, 1);
        String result = String.valueOf(value.intValue());

        return result;
    }





//* 
//* <TeaFunction name="float->string"
//*              arguments="aValue"
//*              module="tea.string">
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

    private static Object functionFloatToString(final SObjFunction func,
                                                final SContext     context,
                                                final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "float");

        Number value  = SArgs.getNumber(args, 1);
        String result = String.valueOf(value.doubleValue());

        return result;
    }





//* 
//* <TeaFunction name="str-unescape"
//*              arguments="aString"
//*              module="tea.string">
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
//*     <li><b><code>\&#117;xxxx</code></b> - Unicode character
//*         in hexadecimal (0000-ffff). The hexadecimal letters can be
//*         in either upper or lower case.</li>
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

    private static Object functionUnescape(final SObjFunction func,
                                           final SContext     context,
                                           final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "string");

        String str    = SArgs.getString(args, 1);
        String result = STeaParserUtils.parseStringLiteral(str);

        return result;
    }





/**************************************************************************
 *
 * This class implements a formatter that stores the result in a
 * <code>StringBuilder</code>.
 *
 **************************************************************************/

    private static final class SFormaterString
        extends SFormater {


        private StringBuilder _result = null;





/**************************************************************************
 *
 * @param resultString
 *     The <TT>StringBuilder</TT> whre the result is stored.
 *
 **************************************************************************/

        public SFormaterString(final StringBuilder resultString) {
        
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

        protected void append(final String s) {

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

        protected void append(final char c) {

            _result.append(c);
        }

    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

