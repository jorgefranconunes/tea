/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.STypeException;





//*
//* <TeaModule name="tea.html">
//*
//* <Overview>
//* Utilities for generating HTML.
//* </Overview>
//*
//* <Description>
//* Set of classes and functions for generating HTML content.
//* </Description>
//*
//* </TeaModule>
//*

/**************************************************************************
 *
 * Module of Tea functions related to HTML generation.
 *
 **************************************************************************/

public final class SModuleHtml 
    extends Object
    implements SModule {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SModuleHtml() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    public void init(final SContext context)
        throws STeaException {

        context.newVar("html-encode",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionHtmlEncode(func, context, args);
                           }
                       });

        context.newVar("url-encode",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionUrlEncode(func, context, args);
                           }
                       });
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

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

    public void stop() {

        // Nothing to do.
    }





//* 
//* <TeaFunction name="html-encode"
//*                 arguments="aString"
//*             module="tea.html">
//*
//* <Overview>
//* Encodes a string so it can be used inside an HTML document.
//* </Overview>
//*
//* <Parameter name="aString">
//* The string to be encoded.
//* </Parameter>
//* 
//* <Returns>
//* A new string resulting from encoding <Arg name ="aString"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * @exception STeaException Thrown whenever an argument could not be
 * printed because of its type.
 *
 **************************************************************************/

    private static Object functionHtmlEncode(final SObjFunction func,
                                             final SContext     context,
                                             final Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "string");
        }

        Object arg = args[1];

        if ( arg instanceof String ) {
           return htmlEncode((String)arg);
        }
        if ( arg instanceof Integer ) {
           return String.valueOf(((Integer)arg).intValue());
        }
        if ( arg instanceof Double ) {
            return String.valueOf(((Double)arg).doubleValue());
        }

        throw new STypeException(args, 1, "string or an integer");
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static String htmlEncode(final String s) {

        StringBuffer buf  = new StringBuffer();
        int          size = s.length();

        for ( int i=0; i<size; i++ ) {
            char c = s.charAt(i);

            switch ( c ) {
            case '<' : buf.append("&lt;"); break;
            case '>' : buf.append("&gt;"); break;
            case '&' : buf.append("&amp;"); break;
            case '"' : buf.append("&quot;"); break;
            default  : buf.append(c); break;
            }
        }
        return buf.toString();
    }









//* 
//* <TeaFunction name="url-encode"
//*                 arguments="aString"
//*             module="tea.html">
//*
//* <Overview>
//* Encodes a string so it can be used as an URL GET parameter.
//* </Overview>
//*
//* <Parameter name="aString">
//* The string to be encoded.
//* </Parameter>
//* 
//* <Returns>
//* A new string resulting from encoding <Arg name ="aString"/>.
//* </Returns>
//*
//* <Description>
//* Characters
//* " ", "?", "/", "+", "&lt;", "&gt;", "&amp;", "&quot;",
//* "$" and "%" are encoded in x-www-form-urlencoded format.
//* </Description>
//* 
//* <Since version="3.1.0"/>
//*
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * @exception STeaException
 *   Thrown wherener an argument could not be printed because of its type.
 *
 **************************************************************************/

    private static Object functionUrlEncode(final SObjFunction func,
                                            final SContext     context,
                                            final Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "string");
        }

        Object arg = args[1];
        
        if ( arg instanceof String ) {
            return urlEncode((String)arg);
        }
        if ( arg instanceof Integer ) {
            return String.valueOf(((Integer)arg).intValue());
        }
        if ( arg instanceof Double ) {
            return String.valueOf(((Double)arg).doubleValue());
        }

        throw new STypeException(args, 1, "string or a numeric");
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static String urlEncode(final String s) {

        StringBuffer buf  = new StringBuffer();
        int          size = s.length();

        for ( int i=0; i<size; i++ ) {
            char c = s.charAt(i);

            switch ( c ) {
            case ' ' : buf.append("%20"); break;
            case '"' : buf.append("%22"); break;
            case '$' : buf.append("%24"); break;
            case '%' : buf.append("%25"); break;
            case '*' : buf.append("%2a"); break;
            case '+' : buf.append("%2b"); break;
            case '/' : buf.append("%2f"); break;
            case '&' : buf.append("%26"); break;
            case '<' : buf.append("%3c"); break;
            case '=' : buf.append("%3d"); break;
            case '>' : buf.append("%3e"); break;
            case '?' : buf.append("%3f"); break;
            case '|' : buf.append("%7c"); break;
            default  : buf.append(c); break;
            }
        }
        return buf.toString();
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

