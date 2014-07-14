/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.xml;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.TeaFunctionImplementor;
import com.pdmfc.tea.runtime.TeaEnvironment;
import com.pdmfc.tea.runtime.TeaModule;





//*
//* <TeaModule name="tea.xml">
//*
//* <Overview>
//* Utilities for processing XML documents.
//* </Overview>
//*
//* <Description>
//* Utilities for processing XML documents.
//* </Description>
//*
//* </TeaModule>
//*

/**************************************************************************
 *
 * Module of Tea functions related to XML processing.
 *
 **************************************************************************/

public final class ModuleXml
    extends Object
    implements TeaModule {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public ModuleXml() {

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
        // all implemented as methods of this class with the
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
//* <TeaFunction name="xml-encode"
//*                 arguments="aString"
//*             module="tea.xml">
//*
//* <Overview>
//* Encodes a string so it can be used inside an XML document.
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
 * Implements the Tea <code>xml-encode</code> function.
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

    @TeaFunctionImplementor("xml-encode")
    public static Object functionXmlEncode(final TeaFunction func,
                                           final TeaContext     context,
                                           final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "string");
        }

        Object arg = args[1];

        if ( arg instanceof String ) {
           return xmlEncode((String)arg);
        }
        if ( arg instanceof Integer ) {
           return String.valueOf(((Integer)arg).intValue());
        }
        if ( arg instanceof Double ) {
            return String.valueOf(((Double)arg).doubleValue());
        }

        throw new STypeException(args, 1, "string or numeric");
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static String xmlEncode(final String s) {

        StringBuilder buf  = new StringBuilder();
        int           size = s.length();

        for ( int i=0; i<size; i++ ) {
            char c = s.charAt(i);

            switch ( c ) {
            case '<' :
                buf.append("&lt;");
                break;
            case '>' :
                buf.append("&gt;");
                break;
            case '&' :
                buf.append("&amp;");
                break;
            case '"' :
                buf.append("&quot;");
                break;
            default  : 
                if (c > 127) {
                  buf.append("&#").append((int)c).append(';');
                } else {
                  buf.append(c);
                }
                break;
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

