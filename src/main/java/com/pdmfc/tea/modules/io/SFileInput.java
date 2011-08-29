/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.io.SInput;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.SArgs;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SRuntimeException;





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

public class SFileInput
    extends SInput {




      
    private static final String     CLASS_NAME   = "TFileInput";
    private static final SObjSymbol CLASS_NAME_S = SObjSymbol.addSymbol(CLASS_NAME);





/**************************************************************************
 *
 * The constructor initializes the object internal state.
 *
 * @param myClass
 *    The <TT>STosClass</TT> object for this object.
 *
 **************************************************************************/

   public SFileInput(STosClass myClass)
       throws STeaException {

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
 * 
 *
 **************************************************************************/

    public Object constructor(SObjFunction obj,
                              SContext     context,
                              Object[]     args)
        throws STeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "file-name");
        }

        String          fileName    = SArgs.getString(args,2);
        FileInputStream aFileInput  = null;

        try {
            aFileInput = new FileInputStream(fileName);
        } catch (FileNotFoundException e1) {
            throw new SRuntimeException("file '" + fileName +
                                        "' could not be opened");
        }

        open(aFileInput);

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

    public static SFileInput newInstance(SContext context,
                                         Object[]  args)
        throws STeaException {

        STosObj input = STosUtil.newInstance(CLASS_NAME_S, context, args);

        if ( !(input instanceof SFileInput) ) {
            throw new SRuntimeException("invalid " + CLASS_NAME + " class");
        }

        return (SFileInput)input;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

