/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules;

import java.util.Iterator;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.runtime.SArgs;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SEmptyListException;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypes;





//*
//* <TeaModule name="tea.lists">
//* 
//* <Overview>
//* List processing.
//* </Overview>
//*
//* <Description>
//* Functions for manipulating lists and pair objects.
//* </Description>
//*
//* </TeaModule>
//*





/**************************************************************************
 *
 * Package of list related commands.
 *
 **************************************************************************/

public class SModuleList
    extends Object
    implements SModule {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SModuleList() {
    }





/**************************************************************************
 *
 * 
 *
 * @param context The context where the commands and variables will be
 * created.
 *
 **************************************************************************/

    public void init(SContext context)
	throws STeaException {

	context.newVar("cons",
                       new SObjFunction() {
                           public Object exec(SObjFunction func,
                                              SContext     context,
                                              Object[]     args)
                               throws STeaException {
                               return functionCons(func, context, args);
                           }
                       });

	context.newVar("car",
                       new SObjFunction() {
                           public Object exec(SObjFunction func,
                                              SContext     context,
                                              Object[]     args)
                               throws STeaException {
                               return functionCar(func, context, args);
                           }
                       });
	
	context.newVar("cdr",
                       new SObjFunction() {
                           public Object exec(SObjFunction func,
                                              SContext     context,
                                              Object[]     args)
                               throws STeaException {
                               return functionCdr(func, context, args);
                           }
                       });

	context.newVar("empty?",
                       new SObjFunction() {
                           public Object exec(SObjFunction func,
                                              SContext     context,
                                              Object[]     args)
                               throws STeaException {
                               return functionEmpty(func, context, args);
                           }
                       });
	
	context.newVar("not-empty?",
                       new SObjFunction() {
                           public Object exec(SObjFunction func,
                                              SContext     context,
                                              Object[]     args)
                               throws STeaException {
                               return functionNotEmpty(func, context, args);
                           }
                       });
	
	context.newVar("set-car!",
                       new SObjFunction() {
                           public Object exec(SObjFunction func,
                                              SContext     context,
                                              Object[]     args)
                               throws STeaException {
                               return functionSetCar(func, context, args);
                           }
                       });
	
	context.newVar("set-cdr!",
                       new SObjFunction() {
                           public Object exec(SObjFunction func,
                                              SContext     context,
                                              Object[]     args)
                               throws STeaException {
                               return functionSetCdr(func, context, args);
                           }
                       });
	
	context.newVar("list",
                       new SObjFunction() {
                           public Object exec(SObjFunction func,
                                              SContext     context,
                                              Object[]     args)
                               throws STeaException {
                               return functionList(func, context, args);
                           }
                       });
	
	context.newVar("nth",
                       new SObjFunction() {
                           public Object exec(SObjFunction func,
                                              SContext     context,
                                              Object[]     args)
                               throws STeaException {
                               return functionNth(func, context, args);
                           }
                       });
	
	context.newVar("prepend",
                       new SObjFunction() {
                           public Object exec(SObjFunction func,
                                              SContext     context,
                                              Object[]     args)
                               throws STeaException {
                               return functionPrepend(func, context, args);
                           }
                       });
	
	context.newVar("append",
                       new SObjFunction() {
                           public Object exec(SObjFunction func,
                                              SContext     context,
                                              Object[]     args)
                               throws STeaException {
                               return functionAppend(func, context, args);
                           }
                       });
	
	context.newVar("length",
                       new SObjFunction() {
                           public Object exec(SObjFunction func,
                                              SContext     context,
                                              Object[]     args)
                               throws STeaException {
                               return functionLength(func, context, args);
                           }
                       });
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void end() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void start() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void stop() {

        // Nothing to do.
    }
    




//* 
//* <TeaFunction name="cons"
//* 		arguments="obj1 obj2"
//*             module="tea.lists">
//*
//* <Overview>
//* Creates a new pair object.
//* </Overview>
//*
//* <Parameter name="obj1">
//* Object of any type that will be stored in the left side of the pair
//* </Parameter>
//*
//* <Parameter name="obj2">
//* Object of any type that will be stored in the right side of the pair
//* </Parameter>
//*
//* <Returns>
//* A reference to the newly created pair object.
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

    private static Object functionCons(SObjFunction func,
				       SContext     context,
				       Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException(args, "obj1 obj2");
	}

	return new SObjPair(args[1], args[2]);
    }





//* 
//* <TeaFunction name="car"
//* 		arguments="aPair"
//*             module="tea.lists">
//*
//* <Overview>
//* Fetches the left element of a pair object.
//* </Overview>
//*
//* <Parameter name="aPair">
//* A pair object.
//* </Parameter>
//*
//* <Returns>
//* A reference to the object stored in the left element of
//* <Arg name="aPair"/>.
//* </Returns>
//*
//* <Description>
//* Fetches and returns the object in the left element of the pair
//* object received as argument. The pair object remains unchanged.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionCar(SObjFunction func,
				      SContext     context,
				      Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args, "pair");
	}

	SObjPair pair = SArgs.getPair(args, 1);

	if ( pair._car == null ) {
	    throw new SEmptyListException(args, 1);
	}

	return pair._car;
    }





//* 
//* <TeaFunction name="cdr"
//* 		arguments="aPair"
//*             module="tea.lists">
//*
//* <Overview>
//* Fetches the right element of a pair object.
//* </Overview>
//*
//* <Parameter name="aPair">
//* A pair object.
//* </Parameter>
//*
//* <Returns>
//* A reference to the object stored in the right element of
//* <Arg name="aPair"/>.
//* </Returns>
//*
//* <Description>
//* Fetches and returns the object in the right element of the pair
//* object received as argument. The pair object remains unchanged.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionCdr(SObjFunction func,
				      SContext     context,
				      Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args, "pair");
	}

	SObjPair pair = SArgs.getPair(args, 1);
	
	if ( pair._car == null ) {
	    throw new SEmptyListException(args, 1);
	}

	return pair._cdr;
    }





//* 
//* <TeaFunction name="empty?"
//* 		arguments="aList"
//*             module="tea.lists">
//*
//* <Overview>
//* Checks if a list as no elements.
//* </Overview>
//*
//* <Parameter name="aList">
//* A pair object that must be the head of a list.
//* </Parameter>
//*
//* <Returns>
//* True if the list has no elements. False otherwise.
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

    private static Object functionEmpty(SObjFunction func,
					SContext     context,
					Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args, "pair");
	}

	SObjPair pair = SArgs.getPair(args, 1);

	return (pair._car==null) ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaFunction name="not-empty?"
//* 		arguments="aList"
//*             module="tea.lists">
//*
//* <Overview>
//* Checks if a list has elements.
//* </Overview>
//*
//* <Parameter name="aList">
//* A pair object that must be the head of a list.
//* </Parameter>
//*
//* <Returns>
//* True if the list has elements. False otherwise.
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

    private static Object functionNotEmpty(SObjFunction func,
					   SContext     context,
					   Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args, "pair");
	}

	SObjPair pair = SArgs.getPair(args, 1);

	return (pair._car==null) ? Boolean.FALSE : Boolean.TRUE;
    }





//* 
//* <TeaFunction name="set-car!"
//* 		arguments="aPair anObject"
//*             module="tea.lists">
//*
//* <Overview>
//* Modifies the contents of the left side of a pair.
//* </Overview>
//*
//* <Parameter name="aPair">
//* Reference to the pair object whose left side will be modified.
//* </Parameter>
//*
//* <Parameter name="anObject">
//* Reference to the object to be stored in the left side of
//* <Arg name="aPair"/>.
//* </Parameter>
//*
//* <Returns>
//* A reference to the pair object that was modified, that is,
//* <Arg name="aPair"/>.
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

    private static Object functionSetCar(SObjFunction func,
					 SContext     context,
					 Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException(args, "pair obj");
	}

	SArgs.getPair(args, 1)._car = args[2];

	return args[2];
    }





//* 
//* <TeaFunction name="set-cdr!"
//* 		arguments="aPair anObject"
//*             module="tea.lists">
//*
//* <Overview>
//* Modifies the contents of the right side of a pair.
//* </Overview>
//*
//* <Parameter name="aPair">
//* Reference to the pair object whose right side will be modified.
//* </Parameter>
//*
//* <Parameter name="anObject">
//* Reference to the object to be stored in the right side of
//* <Arg name="aPair"/>.
//* </Parameter>
//*
//* <Returns>
//* A reference to the pair object that was modified, that is,
//* <Arg name="aPair"/>.
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

    private static Object functionSetCdr(SObjFunction func,
					 SContext context,
					 Object[]   args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException(args, "pair obj");
	}

	SArgs.getPair(args, 1)._cdr = args[2];

	return args[2];
    }





//* 
//* <TeaFunction name="list"
//* 		arguments="[obj1 ...]"
//*             module="tea.lists">
//*
//* <Overview>
//* Creates a list object.
//* </Overview>
//*
//* <Parameter name="obj1">
//* Object of any type.
//* </Parameter>
//*
//* <Returns>
//* A reference to the pair object that is the head of the newly created
//* list.
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

    private static Object functionList(SObjFunction func,
				       SContext     context,
				       Object[]     args)
	throws STeaException {

	SObjPair head    = SObjPair.emptyList();
	SObjPair element = null;

	for ( int i=1; i<args.length; i++ ) {
	    SObjPair node = new SObjPair(args[i], SObjPair.emptyList());

	    if ( element == null ) {
		head = node;
	    } else {
		element._cdr = node;
	    }
	    element = node;
	}

	return head;
    }





//* 
//* <TeaFunction name="nth"
//* 		arguments="aList index"
//*             module="tea.lists">
//*
//* <Overview>
//* Fetches an object in a list in a certain position.
//* </Overview>
//*
//* <Parameter name="aList">
//* A pair object that must be the head of a list.
//* </Parameter>
//*
//* <Parameter name="index">
//* Integer identifying the object to be retrieved. For the first element
//* in <Arg name="aList"/> the value of <Arg name="index"/> would be zero.
//* </Parameter>
//*
//* <Returns>
//* A reference to the object in the <Arg name="index"/> position.
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

    private static Object functionNth(SObjFunction func,
				      SContext     context,
				      Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException(args, "list index");
	}

	SObjPair pair  = SArgs.getPair(args, 1);
	int      index = SArgs.getInt(args, 2).intValue();
	Object   obj   = null;
	Iterator it    = pair.iterator();

	for ( int i=0; it.hasNext(); i++ ) {
	    obj = it.next();
	    if ( i == index ) {
		return obj;
	    }
	}
        
        String msg = "index out of range ({0})";
	throw new SRuntimeException(args, msg, index);
    }





//* 
//* <TeaFunction name="prepend"
//* 		arguments="anObject aList"
//*             module="tea.lists">
//*
//* <Overview>
//* Creates a new list obtained by inserting a new element at the head
//* of an existing list.
//* </Overview>
//*
//* <Parameter name="anObject">
//* The object to be inserted at the head of <Arg name="aList"/>.
//* </Parameter>
//*
//* <Parameter name="aList">
//* A pair object that must be the head of a list.
//* </Parameter>
//*
//* <Returns>
//* A reference to a new pair object that is the head of a list
//* where <Arg name="anObject"/> is the first element and
//* <Arg name="aList"/> is the rest of that list.
//* </Returns>
//*
//* <Description>
//* The <Func name="prepend"/> function could be defined as follows:
//* 	<Code>
//* 	define prepend ( anObject aList ) {
//* 	    cons $anObject $aList
//* 	}
//* 	</Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionPrepend(SObjFunction func,
					  SContext     context,
					  Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException(args, "object list");
	}

	Object   obj  = args[1];
	SObjPair rest = SArgs.getPair(args,2);
	SObjPair head = new SObjPair(obj, rest);

	return head;
    }





//* 
//* <TeaFunction name="append"
//* 		arguments="anObject aList"
//*             module="tea.lists">
//*
//* <Overview>
//* Appends a new element to the end of a list.
//* </Overview>
//*
//* <Parameter name="anObject">
//* The object being added to the end of the list.
//* </Parameter>
//*
//* <Parameter name="aList">
//* The list object the object will be appended to.
//* </Parameter>
//*
//* <Returns>
//* A reference to the modified list object.
//* </Returns>
//*
//* <Description>
//* Appends a new element, containing <Arg name="anObject"/>, to the
//* end of the list received as the <Arg name="aList"/> argument. The
//* list is modified in the sense that it has a new element, its length
//* having increased by one, as reported by the <Func name="length"/>
//* function.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionAppend(SObjFunction func,
					 SContext     context,
					 Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException(args, "object list");
	}

	Object   obj  = args[1];
	SObjPair head = SArgs.getPair(args, 2);
	SObjPair node = head;

	while ( node._car != null ) {
	    try {
		node = (SObjPair)node._cdr;
	    } catch (ClassCastException e) {
                String msg="invalid list. Each cdr should be a pair, not a {0}";
                String invalidTypeName = STypes.getTypeName(node._cdr);
		throw new SRuntimeException(args,msg, invalidTypeName);
	    }
	}
	node._car = obj;
	node._cdr = SObjPair.emptyList();

	return head;
    }





//* 
//* <TeaFunction name="length"
//* 		arguments="aList"
//*             module="tea.lists">
//*
//* <Overview>
//* Counts the number of elements in a list.
//* </Overview>
//*
//* <Parameter name="aList">
//* A pair object that must be the head of a list.
//* </Parameter>
//*
//* <Returns>
//* Returns an integer representing the number of elements in the list.
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

    private static Object functionLength(SObjFunction func,
					SContext     context,
					Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args, "list");
	}

	return new Integer(SArgs.getPair(args,1).length());
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

