/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.io;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.io.SOutput;
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
//* <TeaClass name="TFileOutput"
//*           baseClass="TOutput"
//*           module="tea.io">
//*
//* <Overview>
//* File output stream.
//* </Overview>
//*
//* <Description>
//* Instances of <Func name="TFileOutput"/> represent an output stream
//* associated with a file.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Instances of this class represent file output streams.
 *
 **************************************************************************/

public final class SFileOutput
    extends SOutput {




      
    private static final String     CLASS_NAME   = "TFileOutput";
    private static final SObjSymbol CLASS_NAME_S =
        SObjSymbol.addSymbol(CLASS_NAME);





/**************************************************************************
 *
 * The constructor initializes the object internal state.
 *
 * @param myClass
 *    The <TT>STosClass</TT> object for this object.
 *
 **************************************************************************/

   public SFileOutput(final STosClass myClass)
       throws STeaException {

       super(myClass);
   }





//* 
//* <TeaMethod name="constructor"
//*            arguments="fileName [appendFlag]"
//*                className="TFileOutput">
//* 
//* <Overview>
//* Opens the file for writing.
//* </Overview>
//* 
//* <Parameter name="fileName">
//* String containing the path name of the file to be opened for writing.
//* </Parameter>
//*
//* <Parameter name="appendFlag">
//* Boolean signaling if the file should be opened in append mode.
//* </Parameter>
//* 
//* <Description>
//* Initializes the internal state and opens <Arg name="fileName"/> for
//* writing. If the <Arg name="appendFlag"/> is not specified than it is
//* taken as false. If the file does not exist then it is created with empty
//* contents. If the file already exists then if <Arg name="appendFlag"/>
//* is false then the file contents are erased.
//* The file is opened in buffered mode by default.
//* <P>
//* If the file identified by <Arg name="fileName"/> could not be opened
//* for writing then a runtime error will occur.  This can happen
//* if the process has insufficient previleges or if <Arg name="fileName"/>
//* refers to a nonexisting path.
//* </P>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object constructor(final SObjFunction obj,
                              final SContext     context,
                              final Object[]     args)
        throws SRuntimeException {
        
        int numArgs = args.length;

        if ( (numArgs!=3) && (numArgs!=4) ) {
            throw new SNumArgException(args, "file-name [append]");
        }

        String           fileName  = SArgs.getString(args,2);
        boolean          append    =
            (numArgs==3) ? false : SArgs.getBoolean(args,3).booleanValue();
        FileOutputStream outStream = null;

        try {
            outStream = new FileOutputStream(fileName, append);
        } catch (FileNotFoundException e1) {
            String msg = "file \"{0}\" could not be opened for writing";
            throw new SRuntimeException(msg, fileName);
        }

        open(outStream);

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

    public static SFileOutput newInstance(final SContext context,
                                          final Object[] args)
        throws STeaException {

        STosObj input = STosUtil.newInstance(CLASS_NAME_S, context, args);

        if ( !(input instanceof SFileOutput) ) {
            throw new SRuntimeException("invalid ''{0}'' class", CLASS_NAME);
        }

        return (SFileOutput)input;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

