/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.xml;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.STypeException;





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

public final class SModuleXml
    extends Object
    implements SModule {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SModuleXml() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    public void init(final SContext context)
        throws STeaException {

        context.newVar("xml-encode",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionXmlEncode(func,
                                                        context,
                                                        args);
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
 * @exception STeaException Thrown whenever an argument could not be
 * printed because of its type.
 *
 **************************************************************************/

    private static Object functionXmlEncode(final SObjFunction func,
                                            final SContext     context,
                                            final Object[]     args)
        throws STeaException {

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

        StringBuffer buf  = new StringBuffer();
        int          size = s.length();

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

