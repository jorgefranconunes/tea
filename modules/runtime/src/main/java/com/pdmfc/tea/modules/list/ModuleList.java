/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.list;

import java.util.Iterator;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.Args;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaEmptyListException;
import com.pdmfc.tea.TeaNumArgException;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaPair;
import com.pdmfc.tea.TeaRunException;
import com.pdmfc.tea.TeaEnvironment;
import com.pdmfc.tea.TeaFunctionImplementor;
import com.pdmfc.tea.TeaModule;





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
 * Tea package providing functions for manipulating lists.
 *
 **************************************************************************/

public final class ModuleList
    extends Object
    implements TeaModule {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public ModuleList() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void init(final TeaEnvironment teaEnvironment)
        throws TeaException {

        // Nothing to do. The functions provided by this module are
        // all implemented as methods of this with class with the
        // TeaFunction annotation.

    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void end() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void start() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void stop() {

        // Nothing to do.
    }
    




//* 
//* <TeaFunction name="cons"
//*              arguments="obj1 obj2"
//*              module="tea.lists">
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
 * Implements the Tea <code>cons</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("cons")
    public static Object functionCons(final TeaFunction func,
                                      final TeaContext  context,
                                      final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "obj1 obj2");
        }

        Object   car = args[1];
        TeaPair cdr = Args.getPair(args, 2);

        return new TeaPair(car, cdr);
    }





//* 
//* <TeaFunction name="car"
//*              arguments="aPair"
//*              module="tea.lists">
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
 * Implements the Tea <code>car</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("car")
    public static Object functionCar(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "pair");
        }

        TeaPair pair = Args.getPair(args, 1);

        if ( pair.isEmpty() ) {
            throw new TeaEmptyListException(args, 1);
        }

        return pair.car();
    }





//* 
//* <TeaFunction name="cdr"
//*              arguments="aPair"
//*              module="tea.lists">
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
 * Implements the Tea <code>cdr</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("cdr")
    public static Object functionCdr(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "pair");
        }

        TeaPair pair = Args.getPair(args, 1);
        
        if ( pair.isEmpty() ) {
            throw new TeaEmptyListException(args, 1);
        }

        return pair.cdr();
    }





//* 
//* <TeaFunction name="empty?"
//*              arguments="aList"
//*              module="tea.lists">
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
 * Implements the Tea <code>empty?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 * 
 **************************************************************************/

    @TeaFunctionImplementor("empty?")
    public static Object functionEmpty(final TeaFunction func,
                                       final TeaContext  context,
                                       final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "pair");
        }

        TeaPair pair = Args.getPair(args, 1);

        return pair.isEmpty() ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaFunction name="not-empty?"
//*              arguments="aList"
//*              module="tea.lists">
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
 * Implements the Tea <code>not-empty?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("not-empty?")
    public static Object functionNotEmpty(final TeaFunction func,
                                          final TeaContext  context,
                                          final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "pair");
        }

        TeaPair pair = Args.getPair(args, 1);

        return pair.isEmpty() ? Boolean.FALSE : Boolean.TRUE;
    }





//* 
//* <TeaFunction name="set-car!"
//*                 arguments="aPair anObject"
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
 * Implements the Tea <code>set-car!</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("set-car!")
    public static Object functionSetCar(final TeaFunction func,
                                        final TeaContext  context,
                                        final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "pair obj");
        }

        Args.getPair(args, 1).setCar(args[2]);

        return args[2];
    }





//* 
//* <TeaFunction name="set-cdr!"
//*              arguments="aPair anObject"
//*              module="tea.lists">
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
 * Implements the Tea <code>set-cdr!</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("set-cdr!")
    public static Object functionSetCdr(final TeaFunction func,
                                        final TeaContext context,
                                        final Object[]   args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "pair obj");
        }

        Args.getPair(args, 1).setCdr(Args.getPair(args, 2));

        return args[2];
    }





//* 
//* <TeaFunction name="list"
//*              arguments="[obj1 ...]"
//*              module="tea.lists">
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
 * Implements the Tea <code>list</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("list")
    public static Object functionList(final TeaFunction func,
                                      final TeaContext  context,
                                      final Object[]    args)
        throws TeaException {

        TeaPair head    = TeaPair.emptyList();
        TeaPair element = null;

        for ( int i=1; i<args.length; i++ ) {
            TeaPair node = new TeaPair(args[i], TeaPair.emptyList());

            if ( element == null ) {
                head = node;
            } else {
                element.setCdr(node);
            }
            element = node;
        }

        return head;
    }





//* 
//* <TeaFunction name="nth"
//*              arguments="aList index"
//*              module="tea.lists">
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
 * Implements the Tea <code>nth</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("nth")
    public static Object functionNth(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "list index");
        }

        TeaPair pair  = Args.getPair(args, 1);
        int      index = Args.getInt(args, 2).intValue();
        Object   obj   = null;
        Iterator it    = pair.iterator();

        for ( int i=0; it.hasNext(); i++ ) {
            obj = it.next();
            if ( i == index ) {
                return obj;
            }
        }
        
        String msg = "index out of range ({0})";
        throw new TeaRunException(args, msg, index);
    }





//* 
//* <TeaFunction name="prepend"
//*              arguments="anObject aList"
//*              module="tea.lists">
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
//*         <Code>
//*         define prepend ( anObject aList ) {
//*             cons $anObject $aList
//*         }
//*         </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>prepend</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("prepend")
    public static Object functionPrepend(final TeaFunction func,
                                         final TeaContext  context,
                                         final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "object list");
        }

        Object   obj  = args[1];
        TeaPair rest = Args.getPair(args,2);
        TeaPair head = new TeaPair(obj, rest);

        return head;
    }





//* 
//* <TeaFunction name="append"
//*              arguments="anObject aList"
//*              module="tea.lists">
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
 * Implements the Tea <code>append</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("append")
    public static Object functionAppend(final TeaFunction func,
                                        final TeaContext  context,
                                        final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "object list");
        }

        Object   obj  = args[1];
        TeaPair head = Args.getPair(args, 2);
        TeaPair node = head;

        while ( !node.isEmpty() ) {
            node = node.cdr();
        }

        node.setCar(obj);
        node.setCdr(TeaPair.emptyList());

        return head;
    }





//* 
//* <TeaFunction name="length"
//*              arguments="aList"
//*              module="tea.lists">
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
 * Implements the Tea <code>length</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("length")
    public static Object functionLength(final TeaFunction func,
                                        final TeaContext  context,
                                        final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "list");
        }

        TeaPair head   = Args.getPair(args, 1);
        int      length = head.length();
        Integer  result = Integer.valueOf(length);

        return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

