/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SShared.java,v 1.4 2002/08/02 17:47:24 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.lang;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;





/**************************************************************************
 *
 * Implements the Tea functions <code>tea-shared-define</code>,
 * <code>tea-shared-defined?</code>, <code>tea-shared-get</code>,
 * <code>tea-shared-set!</code>.
 *
 **************************************************************************/

class SShared
    implements SObjFunction {





    private final static int DEFINE  = 0;
    private final static int GET     = 1;
    private final static int SET     = 2;
    private final static int DEFINED = 3;


    private SContext _sharedContext;
    private int      _op;


    private static final String MSG_INV_VAL_TYPE = "value must be a symbol, a string, a boolean or a numeric, not a ";





/**************************************************************************
 *
 * @param op
 *    Defines the command that is actually implemented.
 *
 **************************************************************************/

    private SShared(int      op,
		   SContext sharedContext) {

	_sharedContext = sharedContext;
	_op     = op;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static SObjFunction functionDefine(SContext sharedContext) {

	return new SShared(DEFINE, sharedContext);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static SObjFunction functionGet(SContext sharedContext) {

	return new SShared(GET, sharedContext);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static SObjFunction functionSet(SContext sharedContext) {

	return new SShared(SET, sharedContext);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static SObjFunction functionIsDefined(SContext sharedContext) {

	return new SShared(DEFINED, sharedContext);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public Object exec(SObjFunction func,
		      SContext     context,
		      Object[]     args)
      throws STeaException {

       switch ( _op ) {
       case DEFINE :
	   return define(func, context, args);
       case GET :
	   return get(func, context, args);
       case SET :
	   return set(func, context, args);
       case DEFINED :
	   return isDefined(func, context, args);
       default  :
	   new SRuntimeException("Internal error: bad op");
      }

      return SObjNull.NULL;
   }





//* 
//* <TeaFunction name="tea-shared-set!"
//* 		arguments="key value"
//*             module="tea.lang">
//*
//* <Overview>
//* Modifies the value of a shared variable.
//* </Overview>
//*
//* <Parameter name="key">
//* Symbol identifying the shared variable being modified.
//* </Parameter>
//* 
//* <Parameter name="value">
//* The new value of the shared variable. It can be a
//* symbol, a string, a boolean, a numeric object or the null object.
//* </Parameter>
//*
//* <Returns>
//* The new value of the shared variable.
//* </Returns>
//*
//* <Description>
//* If there is no shared variable identified by the <Arg name="key"/>
//* symbol a runtime error will occur. The function
//* <FuncRef name="tea-shared-define"/> is used to create a shared variable.
//* You can also check for the existence of a shared variable by
//* calling <FuncRef name="tea-shared-defined?"/>.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private Object set(SObjFunction func,
		      SContext     context,
		      Object[]     args)
      throws STeaException {

      if ( args.length != 3 ) {
	 throw new SNumArgException(args[0], "key value");
      }

      SObjSymbol key   = STypes.getSymbol(args, 1);
      Object     value = args[2];

      if ( !validType(value) ) {
	  throw new STypeException(args[0], MSG_INV_VAL_TYPE + STypes.getTypeName(value));
      }

      synchronized (_sharedContext) {
	  _sharedContext.setVar(key, value);
      }

      return value;
   }





//* 
//* <TeaFunction name="tea-shared-get"
//* 		arguments="key"
//*             module="tea.lang">
//*
//* <Overview>
//* Fetches the value of a shared variable.
//* </Overview>
//*
//* <Parameter name="key">
//* Symbol identifying the shared variable whose value is being fetched.
//* </Parameter>
//*
//* <Returns>
//* The object stored in the shared variable.
//* </Returns>
//*
//* <Description>
//* If there is no shared variable identified by the <Arg name="key"/>
//* symbol a runtime error will occur. The function
//* <FuncRef name="tea-shared-define"/> is used to create a shared variable.
//* You can also check for the existence of a shared variable by
//* calling <FuncRef name="tea-shared-defined?"/>.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private Object get(SObjFunction func,
		      SContext     context,
		      Object[]     args)
      throws STeaException {

      if ( args.length != 2 ) {
	 throw new SNumArgException(args[0], "key");
      }

      SObjSymbol key   = STypes.getSymbol(args, 1);
      Object     value = null;

      synchronized (_sharedContext) {
	  value = _sharedContext.getVar(key);
      }

      return value;
   }





//* 
//* <TeaFunction name="tea-shared-define"
//* 		arguments="key value"
//*             module="tea.lang">
//*
//* <Overview>
//* Creates a variable shared between all the Tea interpreters running
//* in a JVM.
//* </Overview>
//*
//* <Parameter name="key">
//* Symbol identifying the shared variable being defined.
//* </Parameter>
//*
//* <Parameter name="value">
//* The initial value of the shared variable being created. It can be a
//* symbol, a string, a boolean, a numeric object or the null object.
//* </Parameter>
//*
//* <Returns>
//* The <Arg name="value"/> argument.
//* </Returns>
//*
//* <Description>
//* This function creates a data area shared among all the Tea
//* interpreters running in the JVM. This shared data area can be compared
//* to a variable. Each data area holds an object and is identified
//* by a symbol but it can only be accessed with specific functions.
//* To modify the shared variable contents the function
//* <FuncRef name="tea-shared-set!"/> is used. The function
//* <FuncRef name="tea-shared-get"/> is used to retrieve a previously stored
//* value.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private Object define(SObjFunction func,
			 SContext     context,
			 Object[]     args)
      throws STeaException {

      if ( args.length != 3 ) {
	 throw new SNumArgException(args[0], "key value");
      }

      SObjSymbol key   = STypes.getSymbol(args, 1);
      Object     value = args[2];

      if ( !validType(value) ) {
	  throw new STypeException(args[0], MSG_INV_VAL_TYPE + STypes.getTypeName(value));
      }

      synchronized (_sharedContext) {
	  _sharedContext.newVar(key, value);
      }

      return value;
   }





//* 
//* <TeaFunction name="tea-shared-defined?"
//* 		arguments="key"
//*             module="tea.lang">
//*
//* <Overview>
//* Checks for the existence of a shared variable.
//* </Overview>
//*
//* <Parameter name="key">
//* Symbol identifing the shared variable.
//* </Parameter>
//*
//* <Returns>
//* True if a shared variable identified by <Arg name="key"/>
//* already exists.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   private Object isDefined(SObjFunction func,
			    SContext     context,
			    Object[]     args)
      throws STeaException {

      if ( args.length != 2 ) {
	 throw new SNumArgException(args[0], "key");
      }

      SObjSymbol key    = STypes.getSymbol(args, 1);
      boolean    result = false;

      synchronized (_sharedContext) {
	  result = _sharedContext.isDefined(key);
      }

      return (result==true) ? Boolean.TRUE : Boolean.FALSE;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private boolean validType(Object obj) {

	return
	       (obj instanceof SObjSymbol)
	    || (obj instanceof String)
	    || (obj instanceof Number)
	    || (obj instanceof Boolean)
	    || (obj == SObjNull.NULL);
    }
}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

