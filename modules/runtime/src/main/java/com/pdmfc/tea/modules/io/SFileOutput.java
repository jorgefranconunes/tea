/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.io;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.io.SOutput;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.Args;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaSymbol;
import com.pdmfc.tea.TeaRunException;





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

   public SFileOutput(final STosClass myClass)
       throws TeaException {

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
 * @param obj The function object whose execution resulted in the
 * invocation of this method.
 *
 * @param context The Tea context where the function is being executed.
 *
 * @param args The actual arguments the function was called with.
 *
 * @return The value received in the <code>obj</code> argument.
 *
 * @throws TeaRunException Thrown when the TFileOutput constructor
 * is supposed to fail.
 *
 **************************************************************************/

    public Object constructor(final TeaFunction obj,
                              final TeaContext     context,
                              final Object[]    args)
        throws TeaRunException {
        
        int numArgs = args.length;

        Args.checkBetween(args, 3, 4, "path [append]");

        String           fileName  = Args.getString(args,2);
        boolean          append    =
            (numArgs==3) ? false : Args.getBoolean(args,3).booleanValue();
        FileOutputStream outStream = null;

        try {
            outStream = new FileOutputStream(fileName, append);
        } catch ( FileNotFoundException e1 ) {
            String msg = "file \"{0}\" could not be opened for writing";
            throw new TeaRunException(msg, fileName);
        }

        open(outStream);

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

    public static SFileOutput newInstance(final TeaContext context,
                                          final Object[] args)
        throws TeaException {

        STosObj output = STosUtil.newInstance(CLASS_NAME_S, context, args);

        if ( !(output instanceof SFileOutput) ) {
            String msg = "invalid \"{0}\" class";
            throw new TeaRunException(msg, CLASS_NAME);
        }

        return (SFileOutput)output;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

