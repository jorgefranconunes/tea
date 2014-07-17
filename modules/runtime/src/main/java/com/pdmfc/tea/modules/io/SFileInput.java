/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.io.SInput;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.Args;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.TeaRunException;





//* 
//* <TeaClass name="TFileInput"
//*           baseClass="TInput"
//*           module="tea.io">
//*
//* <Overview>
//* File input stream.
//* </Overview>
//*
//* <Description>
//* Instances of <Func name="TFileInput"/> represent an input stream
//* associated with a file.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Instances of this class represent file input streams.
 *
 **************************************************************************/

public final class SFileInput
    extends SInput {




      
    private static final String     CLASS_NAME   = "TFileInput";
    private static final TeaSymbol CLASS_NAME_S =
        TeaSymbol.addSymbol(CLASS_NAME);





/**************************************************************************
 *
 * The constructor initializes the object internal state.
 *
 * @param myClass The <code>STosClass</code> object for this object.
 *
 * @throws TeaException Thrown if there were problems initializing
 * the base objects. Unless something has gone horribly wrong with the
 * TOS internals it is never thrown.
 *
 **************************************************************************/

   public SFileInput(final STosClass myClass)
       throws TeaException  {

       super(myClass);
   }





//* 
//* <TeaMethod name="constructor"
//*            arguments="fileName"
//*                className="TFileInput">
//* 
//* <Overview>
//* Opens the file for reading.
//* </Overview>
//* 
//* <Parameter name="fileName">
//* String containing the path name of the file to read.
//* </Parameter>
//* 
//* <Description>
//* If the file identified by <Arg name="fileName"/> could not be
//* opened for reading then a runtime error occurs. This can happen
//* if the process has insufficient previleges or if <Arg name="fileName"/>
//* refers to a nonexisting path.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    public Object constructor(final TeaFunction obj,
                              final TeaContext     context,
                              final Object[]    args)
        throws TeaException {

        Args.checkCount(args, 3, "path");

        String          fileName    = Args.getString(args,2);
        FileInputStream aFileInput  = null;

        try {
            aFileInput = new FileInputStream(fileName);
        } catch ( FileNotFoundException e1 ) {
            String msg = "file ''{0}'' could not be opened";
            throw new TeaRunException(msg, fileName);
        }

        open(aFileInput);

        return obj;
    }





/**************************************************************************
 *
 * @return The name of this TOS class.
 *
 **************************************************************************/

    public static String getTosClassName() {

        return CLASS_NAME;
    }





/**************************************************************************
 *
 * @param context The Tea context where the object constructor will be
 * called.
 *
 * @param args The arguments to be passed to the constructor.
 *
 * @return A newly initialized TFileInput TOS object.
 *
 * @throws TeaException Thrown if there were problems instatiating
 * the TOS object or executing the constructor.
 *
 **************************************************************************/

    public static SFileInput newInstance(final TeaContext context,
                                         final Object[] args)
        throws TeaException {

        STosObj input = STosUtil.newInstance(CLASS_NAME_S, context, args);

        if ( !(input instanceof SFileInput) ) {
            String msg = "invalid \"{0}\" class";
            throw new TeaRunException(msg, CLASS_NAME);
        }

        return (SFileInput)input;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

