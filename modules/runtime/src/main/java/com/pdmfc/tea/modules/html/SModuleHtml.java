/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.html;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.runtime.SArgs;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.TeaEnvironment;
import com.pdmfc.tea.runtime.TeaFunctionImplementor;
import com.pdmfc.tea.runtime.TeaModule;





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
    implements TeaModule {





    private static final String DEFAULT_CHARSET = "UTF-8";





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

    @Override
    public void init(final TeaEnvironment environment)
        throws TeaException {

        // Nothing to do. The functions provided by this module are
        // all implemented as methods of this with class with the
        // TeaFunction annotation.
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

    @Override
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
//* <TeaFunction name="html-encode"
//*              arguments="aString"
//*              module="tea.html">
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
 * Implements the Tea <code>html-encode</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("html-encode")
    public static Object functionHtmlEncode(final SObjFunction func,
                                            final SContext     context,
                                            final Object[]     args)
        throws TeaException {

        SArgs.checkCount(args, 2, "object");

        String result = null;
        Object arg    = args[1];

        if ( arg instanceof String ) {
            result = htmlEncode((String)arg);
        } else if ( arg instanceof Number ) {
            result = arg.toString();
        } else {
            throw new STypeException(args, 1, "string or numeric");
        }

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static String htmlEncode(final String s) {

        StringBuilder buf  = new StringBuilder();
        int           size = s.length();

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
//*              arguments="aString"
//*              module="tea.html">
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
 * Implements the Tea <code>url-encode</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("url-encode")
    public static Object functionUrlEncode(final SObjFunction func,
                                           final SContext     context,
                                           final Object[]     args)
        throws TeaException {

        SArgs.checkCount(args, 2, "object");

        String result   = null;
        Object arg      = args[1];
        
        if ( arg instanceof String ) {
            result = urlEncode((String)arg);
        } else if ( arg instanceof Number ) {
            result = arg.toString();
        } else {
            throw new STypeException(args, 1, "string or a numeric");
        }

        return result;
   }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    private static String urlEncode(final String s) {

        String result = null;

        try {
            result = URLEncoder.encode(s, DEFAULT_CHARSET);
        } catch ( UnsupportedEncodingException e ) {
            // Should never happen...
            result = s;
        }

        return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

