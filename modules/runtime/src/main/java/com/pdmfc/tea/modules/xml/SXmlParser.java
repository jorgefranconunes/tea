/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.xml;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaRunException;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.Args;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaSymbol;
import com.pdmfc.tea.TeaNumArgException;





//* 
//* <TeaClass name="TXmlParser"
//*           module="tea.xml">
//*
//* <Overview>
//* Used to parse XML files.
//* </Overview>
//*
//* <Description>
//* Instances of <Func name="TXmlParser"/> are used to parse XML files.
//* The interface for this class is similar to the SAX API by W3C.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Represents a TOS object that acts like a XML parser.
 *
 **************************************************************************/

public final class SXmlParser
    extends STosObj {



    
    private static final String     CLASS_NAME   = "TXmlParser";
    private static final TeaSymbol CLASS_NAME_S =
        TeaSymbol.addSymbol(CLASS_NAME);

    // The XML parser.
    private XMLReader _parser = null;
    
    // The TOS object that will be used to handle the parsing events.
    private STosObj _handler = null;





/**************************************************************************
 *
 * @param myClass The TOS class of this object.
 *
 **************************************************************************/

      public SXmlParser(final STosClass myClass)
         throws TeaException {

         super(myClass);
      }





/**************************************************************************
 *
 * Specifies the Java native XML parser to use for parsing XML
 * documents.
 *
 * @param parserClassName Fully qualified Java class name of the
 * object to be used as XML parser. The Java class must implement the
 * "org.xml.sax.XMLReader" interface.
 *
 **************************************************************************/

    public void setNativeParser(final String parserClassName)
        throws TeaRunException {

        try {
            if ( parserClassName != null ) {
                _parser = XMLReaderFactory.createXMLReader(parserClassName);
            } else {
                _parser = XMLReaderFactory.createXMLReader();
            }
        } catch ( SAXException e ) {
            String   msg     = "Failed to create XML parser - {0} - {1}";
            Object[] fmtArgs = { e.getClass().getName(), e.getMessage() };
            throw new TeaRunException(msg, fmtArgs);
        }
    }





//* 
//* <TeaMethod name="constructor"
//*            arguments="[javaClassName]"
//*                className="TXmlParser">
//* 
//* <Overview>
//* Initializes the object and optionally specifies the XML parser to
//* be used internally.
//* </Overview>
//*
//* <Parameter name="javaClassName">
//* Name of a Java class that must implement the
//* <Func name="org.xml.sax.XMLReader"/> interface.
//* </Parameter>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object constructor(final TeaFunction obj,
                              final TeaContext     context,
                              final Object[]    args)
        throws TeaException {

        switch ( args.length ) {
        case 2 :
            break;
        case 3 :
            setNativeParser(obj, context, args);
            break;
        default :
            throw new TeaNumArgException(args, "[native-parser-class-name]");
        }

        return obj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public XMLReader getParser()
        throws TeaRunException {

        if ( _parser == null ) {
            setNativeParser(null);
        }

        return _parser;
    }





//* 
//* <TeaMethod name="setNativeParser"
//*            arguments="javaClassName"
//*                className="TXmlParser">
//* 
//* <Overview>
//* Specifies the XML parser to be used internally.
//* </Overview>
//*
//* <Parameter name="javaClassName">
//* Name of a Java class that must implement the
//* <Func name="org.xml.sax.Parser"/> interface.
//* </Parameter>
//* 
//* <Description>
//* A new instance of <Arg name="javaClassName"/> will be created
//* for each call to the <MethodRef name="parse"/> method.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object setNativeParser(final TeaFunction obj,
                                  final TeaContext     context,
                                  final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "java-class-name");
        }

        String parserClassName = Args.getString(args, 2);

        setNativeParser(parserClassName);

        return obj;
    }





//* 
//* <TeaMethod name="parse"
//*            arguments="xmlFile"
//*                className="TXmlParser">
//*
//* <Prototype arguments="inputStream"/>
//* 
//* <Overview>
//* Parses a XML document.
//* </Overview>
//*
//* <Parameter name="xmlFile">
//* String representing the name of the file to process.
//* </Parameter>
//*
//* <Parameter name="inputStream">
//* A <ClassRef name="TInput"/> with the XML document contents to be
//* read.
//* </Parameter>
//* 
//* <Description>
//* Parses a XML file by reading from a file or input stream.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object parse(final TeaFunction obj,
                        final TeaContext     context,
                        final Object[]    args)
        throws TeaException {

        SXmlParserParse parseMethod = new SXmlParserParse();

        parseMethod.setHandler((_handler==null) ? (STosObj)obj : _handler);

        return parseMethod.exec(obj, context, args);
    }





//* 
//* <TeaMethod name="startDocument"
//*                className="TXmlParser">
//* 
//* <Overview>
//* Called at the start of processing of the XML file.
//* </Overview>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object startDocument(final TeaFunction obj,
                                final TeaContext     context,
                                final Object[]    args)
        throws TeaException {

        return obj;
    }





//* 
//* <TeaMethod name="endDocument"
//*                className="TXmlParser">
//* 
//* <Overview>
//* Called at the end of processing of the XML file.
//* </Overview>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object endDocument(final TeaFunction obj,
                              final TeaContext     context,
                              final Object[]    args)
        throws TeaException {

        return obj;
    }





//* 
//* <TeaMethod name="startElement"
//*            arguments="tagName attributes"
//*                className="TXmlParser">
//* 
//* <Overview>
//* Called at the start of an element.
//* </Overview>
//*
//* <Parameter name="tagName">
//* String representing the name of the element being processed.
//* </Parameter>
//*
//* <Parameter name="attributes">
//* A <ClassRef name="THashtable"/> containing the attributes of the element
//* being processed. The <ClassRef name="THashtable"/> keys are strings
//* representing the attributes
//* names and the values are strings representing
//* the values of the attributes.
//* </Parameter>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object startElement(final TeaFunction obj,
                               final TeaContext     context,
                               final Object[]    args)
        throws TeaException {

        return obj;
    }





//* 
//* <TeaMethod name="endElement"
//*            arguments="tagName"
//*                className="TXmlParser">
//* 
//* <Overview>
//* Called at the end of an element.
//* </Overview>
//*
//* <Parameter name="tagName">
//* String representing the name of the element being processed.
//* </Parameter>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object endElement(final TeaFunction obj,
                             final TeaContext     context,
                             final Object[]    args)
        throws TeaException {

        return obj;
    }





//* 
//* <TeaMethod name="characters"
//*            arguments="aString"
//*                className="TXmlParser">
//* 
//* <Overview>
//* Called at the start of a block of text.
//* </Overview>
//*
//* <Parameter name="aString">
//* String representing the block of text found on the XML document.
//* </Parameter>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object characters(final TeaFunction obj,
                             final TeaContext     context,
                             final Object[]    args)
        throws TeaException {

        return obj;
    }





//* 
//* <TeaMethod name="processingInstruction"
//*            arguments="target data"
//*                className="TXmlParser">
//* 
//* <Overview>
//* Called for every processing instruction in the XML file.
//* </Overview>
//*
//* <Parameter name="target">
//* String representing the processing instruction target.
//* </Parameter>
//*
//* <Parameter name="data">
//* A string representing the data associated with the processing
//* instruction or the null object if no data was specified.
//* </Parameter>
//* 
//* <Description>
//* This implementation does nothing. Derived classes are expected
//* to redefine this method to perform the desired tasks.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object processingInstruction(final TeaFunction obj,
                                        final TeaContext     context,
                                        final Object[]    args)
        throws TeaException {

        return obj;
    }





//* 
//* <TeaMethod name="setHandler"
//*            arguments="obj"
//*                className="TXmlParser">
//* 
//* <Overview>
//* Specifies the object responsible for handling the parsing events.
//* </Overview>
//*
//* <Parameter name="obj">
//* The object responsible for handling the parsing events.
//* </Parameter>
//* 
//* <Description>
//* <P>The <Arg name="obj"/> must respond to the following methods:
//* <MethodRef name="characters"/>, <MethodRef name="endDocument"/>
//* <MethodRef name="endElement"/>, <MethodRef name="processingInstruction"/>,
//* <MethodRef name="startDocument"/>, <MethodRef name="startElement"/>.</P>
//*
//* <P>By default the handler of parsing events is the parser object
//* itself.</P>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object setHandler(final TeaFunction obj,
                             final TeaContext     context,
                             final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "event-handler");
        }

        _handler = STosUtil.getTosObj(args, 2);

        return obj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static String getTosClassName() {

        return CLASS_NAME;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static SXmlParser newInstance(final TeaContext context,
                                         final Object[] args)
        throws TeaException {

        STosObj parser = STosUtil.newInstance(CLASS_NAME_S, context);

        if ( !(parser instanceof SXmlParser) ) {
            throw new TeaRunException("invalid {0} class", CLASS_NAME);
        }

        return (SXmlParser)parser;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

