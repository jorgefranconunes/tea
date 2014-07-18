/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.io;

import java.io.InputStream;
import java.io.IOException;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.io.SInput;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.Args;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaSymbol;
import com.pdmfc.tea.TeaNumArgException;
import com.pdmfc.tea.TeaRunException;
import com.pdmfc.tea.util.SInputSource;
import com.pdmfc.tea.util.SInputSourceFactory;





//* 
//* <TeaClass name="TUrlInput"
//*           baseClass="TInput"
//*           module="tea.io">
//*
//* <Overview>
//* Input stream from a URL.
//* </Overview>
//*
//* <Description>
//* Instances of <Func name="TUrlInput"/> represent an input stream
//* associated with an URL.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Instances of this class represent file input streams.
 *
 **************************************************************************/

public final class SUrlInput
    extends SInput {




      
    private static final String     CLASS_NAME   = "TUrlInput";
    private static final TeaSymbol CLASS_NAME_S =
        TeaSymbol.addSymbol(CLASS_NAME);





/**************************************************************************
 *
 * The constructor initializes the object internal state.
 *
 * @param myClass The <code>STosClass</code> object for this object.
 *
 **************************************************************************/

   public SUrlInput(final STosClass myClass)
       throws TeaException {

       super(myClass);
   }





//* 
//* <TeaMethod name="constructor"
//*            arguments="url"
//*                className="TUrlInput">
//* 
//* <Overview>
//* Opens the a connection to the URL and initializes the object for
//* reading from that connection.
//* </Overview>
//* 
//* <Parameter name="url">
//* String representing a URL.
//* </Parameter>
//* 
//* <Description>
//* If the URL identified by <Arg name="url"/> could not be
//* opened for reading then a runtime error occurs.
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

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "file-name");
        }

        String      url   = Args.getString(args,2);
        InputStream input = null;

        try {
            SInputSource inputSource =
                SInputSourceFactory.createInputSource(url);

            input = inputSource.openStream();
        } catch ( IOException e ) {
            String   msg     = "URL \"{0}\" could not be opened for reading";
            Object[] fmtArgs = { url, e.getMessage() };
            throw new TeaRunException(msg, fmtArgs);
        }

        open(input);

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

    public static SUrlInput newInstance(final TeaContext context,
                                        final Object[] args)
        throws TeaException {

        STosObj input = STosUtil.newInstance(CLASS_NAME_S, context, args);

        if ( !(input instanceof SUrlInput) ) {
            throw new TeaRunException("invalid ''{0}'' class", CLASS_NAME);
        }

        return (SUrlInput)input;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

