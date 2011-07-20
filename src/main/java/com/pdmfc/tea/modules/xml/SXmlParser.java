/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.xml;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.SArgs;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.STypes;





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

public class SXmlParser
    extends STosObj {



    
    private static final String     CLASS_NAME   = "TXmlParser";
    private static final SObjSymbol CLASS_NAME_S = SObjSymbol.addSymbol(CLASS_NAME);

    // The XML parser.
    private XMLReader _parser = null;
    
    // The TOS object that will be used to handle the parsing events.
    private STosObj _handler = null;





/**************************************************************************
 *
 * @param myClass The TOS class of this object.
 *
 **************************************************************************/

      public SXmlParser(STosClass myClass)
         throws STeaException {

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

    public void setNativeParser(String parserClassName)
	throws SRuntimeException {

	try {
	    if ( parserClassName != null ) {
		_parser = XMLReaderFactory.createXMLReader(parserClassName);
	    } else {
		_parser = XMLReaderFactory.createXMLReader();
	    }
	} catch (SAXException e) {
	    String   msg     = "Failed to create XML parser - {0} - {1}";
	    Object[] fmtArgs = { e.getClass().getName(), e.getMessage() };
	    throw new SRuntimeException(msg, fmtArgs);
	}
    }





//* 
//* <TeaMethod name="constructor"
//*            arguments="[javaClassName]"
//* 	       className="TXmlParser">
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

    public Object constructor(SObjFunction obj,
			      SContext     context,
			      Object[]     args)
	throws STeaException {

	switch ( args.length ) {
	case 2 :
	    break;
	case 3 :
	    setNativeParser(obj, context, args);
	    break;
	default :
	    throw new SNumArgException(args, "[native-parser-class-name]");
	}

	return obj;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public XMLReader getParser()
	throws SRuntimeException {

	if ( _parser == null ) {
	    setNativeParser(null);
	}

	return _parser;
    }





//* 
//* <TeaMethod name="setNativeParser"
//*            arguments="javaClassName"
//* 	       className="TXmlParser">
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

    public Object setNativeParser(SObjFunction obj,
				  SContext     context,
				  Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException(args, "java-class-name");
	}

	String parserClassName = SArgs.getString(args, 2);

	setNativeParser(parserClassName);

	return obj;
    }





//* 
//* <TeaMethod name="parse"
//*            arguments="xmlFile"
//* 	       className="TXmlParser">
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

    public Object parse(SObjFunction obj,
			SContext     context,
			Object[]     args)
	throws STeaException {

	SXmlParserParse parseMethod = new SXmlParserParse();

	parseMethod.setHandler((_handler==null) ? (STosObj)obj : _handler);

	return parseMethod.exec(obj, context, args);
    }





//* 
//* <TeaMethod name="startDocument"
//* 	       className="TXmlParser">
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

    public Object startDocument(SObjFunction obj,
				SContext     context,
				Object[]     args)
	throws STeaException {

	return obj;
    }





//* 
//* <TeaMethod name="endDocument"
//* 	       className="TXmlParser">
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

    public Object endDocument(SObjFunction obj,
			      SContext     context,
			      Object[]     args)
	throws STeaException {

	return obj;
    }





//* 
//* <TeaMethod name="startElement"
//*            arguments="tagName attributes"
//* 	       className="TXmlParser">
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

    public Object startElement(SObjFunction obj,
			       SContext     context,
			       Object[]     args)
	throws STeaException {

	return obj;
    }





//* 
//* <TeaMethod name="endElement"
//*            arguments="tagName"
//* 	       className="TXmlParser">
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

    public Object endElement(SObjFunction obj,
			     SContext     context,
			     Object[]     args)
	throws STeaException {

	return obj;
    }





//* 
//* <TeaMethod name="characters"
//*            arguments="aString"
//* 	       className="TXmlParser">
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

    public Object characters(SObjFunction obj,
			     SContext     context,
			     Object[]     args)
	throws STeaException {

	return obj;
    }





//* 
//* <TeaMethod name="processingInstruction"
//*            arguments="target data"
//* 	       className="TXmlParser">
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

    public Object processingInstruction(SObjFunction obj,
					SContext     context,
					Object[]     args)
	throws STeaException {

	return obj;
    }





//* 
//* <TeaMethod name="setHandler"
//*            arguments="obj"
//* 	       className="TXmlParser">
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

    public Object setHandler(SObjFunction obj,
			     SContext     context,
			     Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException(args, "event-handler");
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

    public static SXmlParser newInstance(SContext context,
					 Object[] args)
	throws STeaException {

	STosObj parser = STosUtil.newInstance(CLASS_NAME_S, context);

	if ( !(parser instanceof SXmlParser) ) {
	    throw new SRuntimeException("invalid " + CLASS_NAME + " class");
	}

	return (SXmlParser)parser;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

