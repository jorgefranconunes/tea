/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.xml;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.io.SInput;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypes;
import com.pdmfc.tea.modules.util.SHashtable;





/**************************************************************************
 *
 * Represents the <TT>parse</TT> method for the <TT>TXmlParser</TT>
 * TOS class.
 *
 **************************************************************************/

final class SXmlParserParse
    extends Object {





    // The symbols for the TOS methods that may be called.
    private static final SObjSymbol START_DOC  =
        SObjSymbol.addSymbol("startDocument");
    private static final SObjSymbol END_DOC    =
        SObjSymbol.addSymbol("endDocument");
    private static final SObjSymbol START_ELEM =
        SObjSymbol.addSymbol("startElement");
    private static final SObjSymbol END_ELEM   =
        SObjSymbol.addSymbol("endElement");
    private static final SObjSymbol CHARS      =
        SObjSymbol.addSymbol("characters");
    private static final SObjSymbol PROC_INST  =
        SObjSymbol.addSymbol("processingInstruction");
    
    // The TOS methods that may be called.
    private SObjFunction _startDocumentMethod = null;
    private SObjFunction _endDocumentMethod   = null;
    private SObjFunction _startElementMethod  = null;
    private SObjFunction _endElementMethod    = null;
    private SObjFunction _charactersMethod    = null;
    private SObjFunction _procInstMethod      = null;         

    // The argument arrays passed to the called TOS methods.
    private Object[] _args2 = new Object[2];
    private Object[] _args3 = new Object[3];
    private Object[] _args4 = new Object[4];

    // The TOS object whose "parse" method is being executed.
    private STosObj           _handler         = null;
    private SContext          _context        = null;
    private SAXParseException _parseException = null;


    private ContentHandler _myContentHandler =
        new ContentHandler() {

            public void characters(final char[] ch,
                                   final int    start,
                                   final int    length)
                throws SAXException {
                myCharacters(ch, start, length);
            }

            public void endDocument()
                throws SAXException {
                myEndDocument();
            }

            public void endElement(final String uri,
                                   final String localName,
                                   final String qName)
                throws SAXException {
                myEndElement(uri, localName, qName);
            }

            public void endPrefixMapping(final String prefix)
                throws SAXException {
                myEndPrefixMapping(prefix);
            }

            public void ignorableWhitespace(final char[] ch,
                                            final int    start,
                                            final int length)
                throws SAXException {
                myIgnorableWhitespace(ch, start, length);
            }

            public void processingInstruction(final String target,
                                              final String data)
                throws SAXException {
                myProcessingInstruction(target, data);
            }

            public void setDocumentLocator(final Locator locator) {
                mySetDocumentLocator(locator);
            }

            public void skippedEntity(final String name)
                throws SAXException {
                mySkippedEntity(name);
            }

            public void startDocument()
                throws SAXException {
                myStartDocument();
            }

            public void startElement(final String uri, 
                                     final String localName,
                                     final String qName,
                                     final Attributes atts)
                throws SAXException {
                myStartElement(uri, localName, qName, atts);
            }

            public void startPrefixMapping(final String prefix,
                                           final String uri)
                throws SAXException {
                myStartPrefixMapping(prefix, uri);
            }

        };


    private ErrorHandler _myErrorHandler =
        new ErrorHandler() {

            public void error(final SAXParseException exception)
                throws SAXException {
                myError(exception);
            }

            public void fatalError(final SAXParseException exception)
                throws SAXException {
                myFatalError(exception);
            }

            public void warning(final SAXParseException exception)
                throws SAXException {
                myWarning(exception);
            }

        };





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

      public SXmlParserParse() {

          // Nothing to do.
      }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void setHandler(final STosObj handler) {

        _handler = handler;
    }





/**************************************************************************
 *
 * The TOS method entry point.
 *
 **************************************************************************/

      public Object exec(final SObjFunction obj,
                         final SContext     context,
                         final Object[]     args)
         throws TeaException {

         if ( args.length != 3 ) {
            throw new SNumArgException(args, "xml-file");
         }

         STosObj       parser       = (STosObj)obj;
         SXmlParser    xmlParser    = (SXmlParser)(parser.part(0));
         XMLReader     nativeParser = xmlParser.getParser();
         Object        input        = args[2];
         InputSource   inputSource  = null;

         if ( input instanceof String ) {
             String xmlFile = (String)input;
             inputSource = new InputSource(xmlFile);
         } else if ( input instanceof STosObj ) {
             SInput tosInput = null;
             try {
                 tosInput = (SInput)((STosObj)input).part(0);
             } catch (ClassCastException e) {
                 String   msg     = "expected String or TInput, not a {0}";
                 Object[] fmtArgs = { STypes.getTypeName(input) };
                 throw new SRuntimeException(args, msg, fmtArgs);
             }
             inputSource = new InputSource(tosInput.getReader());
         } else {
             String   msg     = "expected String or TInput, not a {0}";
             Object[] fmtArgs = { STypes.getTypeName(input) };
             throw new SRuntimeException(args, msg, fmtArgs);
         }

         if ( _handler == null ) {
             _handler = parser;
         }
         if ( nativeParser == null ) {
             throw new SRuntimeException("no native parser");
         }

         parse(context, nativeParser, inputSource);

         return obj;
      }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void parse(final SContext    context,
                       final XMLReader   parser,
                       final InputSource input)
        throws TeaException {

         TeaException error = null;

         _context             = context;
         _startDocumentMethod = null;
         _endDocumentMethod   = null;
         _startElementMethod  = null;
         _endElementMethod    = null;
         _charactersMethod    = null;
         _procInstMethod      = null;
         _parseException      = null;

         parser.setContentHandler(_myContentHandler);
         parser.setErrorHandler(_myErrorHandler);

         try {
            parser.parse(input);
         } catch (SAXException e1) {
            error = (TeaException)e1.getException();
         } catch (java.io.IOException e2) {
            error = new SRuntimeException("problems reading input ("
                                          + e2.getMessage() + ")");
         }

         // Release references to the args passed to the TOS methods.
         _args2[0] = _args2[1] = null;
         _args3[0] = _args3[1] = _args3[2] = null;
         _args4[0] = _args4[1] = _args4[2] = _args4[3] = null;

         // Releases all remaining references.
         _handler     = null;
         _context    = null;

         if ( error != null ) {
            throw error;
         }
         if ( _parseException != null ) {
             String msg =  _parseException.getMessage()
                 + " (" + _parseException.getLineNumber() + ")";
             SRuntimeException anException = new SRuntimeException(msg);
             _parseException = null;
             throw anException;
         }
    }





/**************************************************************************
 *
 * Called at the start of processing of the XML document. Calls the
 * <TT>startDocument</TT> TOS method. This method is part of the
 * <TT>org.xml.sax.DocumentHandler</TT> interface.
 *
 * @exception org.xml.sax.SAXException Thrown if there were any
 * problems executing the <TT>startDocument</TT> TOS method. This
 * exception object encapsulates the <TT>TeaException</TT> thrown by
 * the Tea code.
 *
 **************************************************************************/

      private void myStartDocument()
         throws SAXException {

         try {
            if ( _startDocumentMethod == null ) {
               _startDocumentMethod = _handler.getTosClass().getMethod(START_DOC);
            }
            _args2[0] = _handler;
            _args2[1] = START_DOC;
            _startDocumentMethod.exec(_handler, _context, _args2);
         } catch (TeaException e) {
            throw new SAXException(e);
         }
      }





/**************************************************************************
 *
 * Called at the end of processing of the XML document. Calls the
 * <TT>endDocument</TT> TOS method. This method is part of the
 * <TT>org.xml.sax.DocumentHandler</TT> interface.
 *
 * @exception org.xml.sax.SAXException Thrown if there were any
 * problemas executing the <TT>endDocument</TT> TOS method. This
 * exception object encapsulates the <TT>TeaException</TT> thrown by
 * the Tea code.
 *
 **************************************************************************/

      private void myEndDocument()
         throws SAXException {

         try {
            if ( _endDocumentMethod == null ) {
               _endDocumentMethod = _handler.getTosClass().getMethod(END_DOC);
            }
            _args2[0] = _handler;
            _args2[1] = END_DOC;
            _endDocumentMethod.exec(_handler, _context, _args2);
         } catch (TeaException e) {
            throw new SAXException(e);
         }
      }





/**************************************************************************
 *
 * Called at the start of an element while processing the XML
 * document. Calls the <TT>startElement</TT> TOS method passing as
 * arguments a string with the name of the tag and a
 * <TT>THashtable</TT> containing the attributes. This method is part
 * of the <TT>org.xml.sax.DocumentHandler</TT> interface.
 *
 * @param name Name of the tag found in the document.
 *
 * @param attribs Represents the attribute list associated with the
 * tag.
 *
 * @exception org.xml.sax.SAXException Thrown if there were any
 * problemas executing the <TT>endDocument</TT> TOS method. This
 * exception object encapsulates the <TT>TeaException</TT> thrown by
 * the Tea code.
 *
 **************************************************************************/

    private void myStartElement(final String     uri,
                                final String     name,
                                final String     qName,
                                final Attributes attribs)
        throws SAXException {

        SHashtable         attribsTable = null;
        Map<Object,Object> tbl          = null;

        try {
            attribsTable = SHashtable.newInstance(_context);
        } catch (TeaException e) {
            throw new SAXException(e);
        }
        tbl = attribsTable.getInternalMap();

        // Fills the attribute list.
        tbl.clear();
        for ( int i=attribs.getLength(); i-->0; ) {
            tbl.put(attribs.getLocalName(i), attribs.getValue(i));
        }

        try {
            if ( _startElementMethod == null ) {
                _startElementMethod = _handler.getTosClass().getMethod(START_ELEM);
            }
            _args4[0] = _handler;
            _args4[1] = START_ELEM;
            _args4[2] = name;
            _args4[3] = attribsTable;
            _startElementMethod.exec(_handler, _context, _args4);
        } catch (TeaException e) {
            throw new SAXException(e);
        }
    }





/**************************************************************************
 *
 * Called at the end of an element while processing the XML
 * document. Calls the <TT>endElement</TT> TOS method passing as
 * arguments a string with the name of the tag. This method is part of
 * the <TT>org.xml.sax.DocumentHandler</TT> interface.
 *
 * @param name Name of the tag found in the document.
 *
 * @exception org.xml.sax.SAXException Thrown if there were any
 * problemas executing the <TT>endDocument</TT> TOS method. This
 * exception object encapsulates the <TT>TeaException</TT> thrown by
 * the Tea code.
 *
 **************************************************************************/

    private void myEndElement(final String uri,
                              final String name,
                              final String qName)
        throws SAXException {

        try {
            if ( _endElementMethod == null ) {
                _endElementMethod = _handler.getTosClass().getMethod(END_ELEM);
            }
            _args3[0] = _handler;
            _args3[1] = END_ELEM;
            _args3[2] = name;
            _endElementMethod.exec(_handler, _context, _args3);
        } catch (TeaException e) {
            throw new SAXException(e);
        }
    }





/**************************************************************************
 *
 * Called when a CDATA block is found while processing the XML
 * document. Calls the <TT>characters</TT> TOS method passing as
 * arguments a string with the contents of the CDATA block. This
 * method is part of the <TT>org.xml.sax.DocumentHandler</TT>
 * interface.
 *
 * @param ch Char array containing the CDATA block.
 *
 * @param start Index into the <TT>ch</TT> array where the data is.
 *
 * @param length Size of the data sequence contained in <TT>ch</TT>.
 *
 * @exception org.xml.sax.SAXException Thrown if there were any
 * problemas executing the <TT>endDocument</TT> TOS method. This
 * exception object encapsulates the <TT>TeaException</TT> thrown by
 * the Tea code.
 *
 **************************************************************************/

    private void myCharacters(final char[] ch,
                              final int start,
                              final int length)
        throws SAXException {

        try {
            if ( _charactersMethod == null ) {
                _charactersMethod = _handler.getTosClass().getMethod(CHARS);
            }
            _args3[0] = _handler;
            _args3[1] = CHARS;
            _args3[2] = new String(ch, start, length);
            _charactersMethod.exec(_handler, _context, _args3);
        } catch (TeaException e) {
            throw new SAXException(e);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void myIgnorableWhitespace(final char[] ch,
                                       final int start,
                                       final int length)
        throws SAXException {

        // We do nothing.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void myProcessingInstruction(final String target,
                                         final String data)
        throws SAXException {

        try {
            if ( _procInstMethod == null ) {
                _procInstMethod = _handler.getTosClass().getMethod(PROC_INST);
            }
            _args4[0] = _handler;
            _args4[1] = PROC_INST;
            _args4[2] = target;
            _args4[3] = (data==null) ? SObjNull.NULL : data;
            _procInstMethod.exec(_handler, _context, _args4);
        } catch (TeaException e) {
            throw new SAXException(e);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void mySetDocumentLocator(final Locator locator) {

        // We do nothing.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void myEndPrefixMapping(final String prefix)
        throws SAXException {

        // We do nothing.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void mySkippedEntity(final String name)
        throws SAXException {

        // We do nothing.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void myStartPrefixMapping(final String prefix,
                                      final String uri)
        throws SAXException {

        // We do nothing.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void myWarning(final SAXParseException e)
        throws SAXException {

        _parseException = e;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void myError(final SAXParseException e)
        throws SAXException {

        _parseException = e;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void myFatalError(final SAXParseException e)
        throws SAXException {

        _parseException = e;
    }





/**************************************************************************
 *
 * Builds a string containing a URL referencing a file name. If the
 * <TT>fileName</TT> argument already contains a valid URL then this
 * URL will be returned.
 *
 * @param fileName String representing the name of a file.
 *
 * @return A string representing a valid URL referencing the file
 * whose name was given as argument.
 *
 * @exception com.pdmfc.tea.runtime.SRuntimeException Thrown if the
 * <TT>fileName</TT> argument contains a badly formed URL.
 *
 **************************************************************************/

      private String createUrl(final String fileName)
         throws SRuntimeException {

         try {
            return (new URL(fileName)).toString();
         } catch (MalformedURLException e) {
             // Just continue bellow...
         }
         try {
            return (new URL("file:"
                            + (new File(fileName)).getAbsolutePath())).toString();
         } catch (MalformedURLException e) {
            throw new SRuntimeException("Malformed URL (" + fileName + ")");
         }
      }

}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

