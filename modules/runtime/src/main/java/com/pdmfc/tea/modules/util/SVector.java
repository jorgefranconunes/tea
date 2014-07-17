/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.util;

import java.util.ArrayList;
import java.util.List;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.Args;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaNull;
import com.pdmfc.tea.runtime.TeaPair;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.TeaRunException;
import com.pdmfc.tea.runtime.Types;




//* 
//* <TeaClass name="TVector"
//*           module="tea.util">
//*
//* <Overview>
//* Represents a ordered set of objects.
//* </Overview>
//*
//* <Description>
//* A <Func name="TVector"/> represents an ordered set of objects.
//* Each object in the set is indexed by an integer value. The indexes
//* range from zero (the first element) to the number of elements minus
//* one.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Represents a TOS object acting like a vector. The elements are
 * actually stored in an underlying <code>java.util.List</code>. This
 * <code>java.util.List</code> can either be internally created or
 * supplied from the outside.
 *
 **************************************************************************/

public final class SVector
    extends STosObj {





    private static final String     CLASS_NAME   = "TVector";
    private static final TeaSymbol CLASS_NAME_S =
        TeaSymbol.addSymbol(CLASS_NAME);

    private List<Object> _vector   = null;
    private TeaContext     _context  = null;
    private TeaFunction _compFunc = null;





/**************************************************************************
 *
 * Initializes the object internal state. The TOS class assigned to
 * this object is supposed to generate instances of TOS objects that
 * are <code>SVector</code> instances.
 *
 * @param myClass The TOS class to assign to the TOS objects.
 *
 **************************************************************************/

    public SVector(final STosClass myClass)
        throws TeaException {

        super(myClass);

        _vector = new ArrayList<Object>();
    }





/**************************************************************************
 *
 * Initializes the object internal state and specifies the
 * <code>java.util.List</code> to be used for storing the vector
 * elements. The <code>contents</code> <code>java.util.List</code> may
 * already contain some elements.
 *
 * @param myClass The TOS class to assign to the TOS objects.
 *
 * @param contents Will store the vector components.
 *
 **************************************************************************/

    private SVector(final STosClass    myClass,
                    final List<Object> contents)
        throws TeaException {

        super(myClass);

        _vector = contents;
    }





/**************************************************************************
 *
 * Fetches the <code>java.util.List</code> used to store the vector
 * elements. This <code>java.util.List</code> may be modified at
 * will. Nevertheless, concurrent use from within multiple threads
 * must be avoided.
 *
 * @return The <code>java.util.List</code> used to store the vector
 * elements.
 *
 **************************************************************************/

    public List<Object> getInternalList() {

        return _vector;
    }





//* 
//* <TeaMethod name="constructor"
//*            arguments="[theSize]"
//*                className="TVector">
//* 
//* <Overview>
//* Initializes a <Func name="TVector"/> with a given number of
//* arguments.
//* </Overview>
//*
//* <Parameter name="theSize">
//* Integer representing the initial size of the vector.
//* </Parameter>
//* 
//* <Description>
//* Initializes the vector with <Arg name="theSize"/> elements. All elements
//* are set to the null object. If <Arg name="theSize"/> is not specified
//* then it is taken to be zero.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "constructor" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object constructor(final TeaFunction obj,
                              final TeaContext     context,
                              final Object[]    args)
        throws TeaRunException {

        if ( (args.length!=2) && (args.length!=3) ) {
            throw new SNumArgException(args, "[size]");
        }

        if ( args.length == 3 ) {
            resize(obj, context, args);
        }

        return obj;
    }





//* 
//* <TeaMethod name="push"
//*            arguments="anObject"
//*                className="TVector">
//* 
//* <Overview>
//* Inserts a new element at the end of the <Func name="TVector"/>.
//* </Overview>
//*
//* <Parameter name="anObject">
//* The object that will be inserted at the end of the vector.
//* </Parameter>
//* 
//* <Returns>
//* A reference to the object for which the method was called.
//* </Returns>
//* 
//* <Description>
//* Inserts a new element into the <Func name="TVector"/>. The new
//* <Arg name="anObject"/> element is inserted at the end of the
//* <Func name="TVector"/>. The number of elements in the
//* <Func name="TVector"/> is increased by one.
//* <P>
//* This method performs the same operation as the <Func name="append"/>
//* method when called with one argument.
//* </P>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "push" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object push(final TeaFunction obj,
                       final TeaContext     context,
                       final Object[]    args)
        throws TeaRunException {

        return append(obj, context, args);
    }





//* 
//* <TeaMethod name="append"
//*            arguments="anObject ..."
//*                className="TVector">
//* 
//* <Overview>
//* Inserts one or more elements at the end of the <Func name="TVector"/>.
//* </Overview>
//*
//* <Parameter name="anObject">
//* The object that will be inserted at the end of the vector.
//* </Parameter>
//* 
//* <Returns>
//* A reference to the object for which the method was called.
//* </Returns>
//* 
//* <Description>
//* Inserts new elements into the <Func name="TVector"/>. Each one of
//* the arguments passed to the method is inserted at the end of the
//* <Func name="TVector"/>. The number of elements in the
//* <Func name="TVector"/> is increased acordingly.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "append" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object append(final TeaFunction obj,
                         final TeaContext     context,
                         final Object[]    args)
        throws TeaRunException {

        if ( args.length < 3 ) {
            throw new SNumArgException(args, "object");
        }

        int count = args.length;

        for ( int i=2; i<count; i++ ) {
            _vector.add(args[i]);
        }

        return obj;
    }





//* 
//* <TeaMethod name="init"
//*            arguments="[obj1 ...]"
//*                className="TVector">
//* 
//* <Overview>
//* Initializes the <Func name="TVector"/> object with a given set of
//* elements.
//* </Overview>
//*
//* <Parameter name="obj1">
//* One of the objects to insert in the <Func name="TVector"/>.
//* </Parameter>
//* 
//* <Returns>
//* A reference to the object for which the method was called.
//* </Returns>
//* 
//* <Description>
//* Replaces the contents of the <Func name="TVector"/> with the set
//* of elements received as arguments.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "init" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object init(final TeaFunction obj,
                       final TeaContext     context,
                       final Object[]    args) {

        int newSize = args.length - 2;

        _vector.clear();

        for ( int i=0; i<newSize; i++ ) {
            _vector.add(args[i+2]);
        }

        return obj;
    }





//* 
//* <TeaMethod name="getSize"
//*                className="TVector">
//* 
//* <Overview>
//* Fetches the number of elements in the <Func name="TVector"/>.
//* </Overview>
//* 
//* <Returns>
//* An integer representing the number of elements in the
//* <Func name="TVector"/>.
//* </Returns>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "getSize" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object getSize(final TeaFunction obj,
                          final TeaContext     context,
                          final Object[]    args) {

        int size = _vector.size();

        return Integer.valueOf(size);
    }





//* 
//* <TeaMethod name="resize"
//*            arguments="newSize"
//*                className="TVector">
//* 
//* <Overview>
//* Changes the size of the <Func name="TVector"/>.
//* </Overview>
//* 
//* <Parameter name="newSize">
//* An integer representing the new size of the <Func name="TVector"/>.
//* </Parameter>
//* 
//* <Returns>
//* A reference to the object for which the method was called.
//* </Returns>
//* 
//* <Description>
//* Changes the <Func name="TVector"/> dimensions. If <Arg name="newSize"/>
//* is smaller than the current size of the <Func name="TVector"/>
//* then the excedent elements are discarded. If <Arg name="newSize"/>
//* is larger than the <Func name="TVector"/> current size then all
//* the existing elements are retained and the new elements are initialized
//* with the null object.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "resize" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object resize(final TeaFunction obj,
                         final TeaContext     context,
                         final Object[]    args)
        throws TeaRunException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "size");
        }

        int newSize = Args.getInt(args,2).intValue();

        if ( newSize < 0 ) {
            String msg = "size must be an integer equal or greater than zero";
            throw new TeaRunException(msg);
        }

        int oldSize = _vector.size();

        if ( newSize > oldSize ) {
            for ( int i=oldSize; i<newSize; i++ ) {
                _vector.add(TeaNull.NULL);
            }
        } else {
            for ( int i=oldSize; (i--)>newSize; ) {
                _vector.remove(i);
            }
        }

        return obj;
    }





//* 
//* <TeaMethod name="getAt"
//*            arguments="index"
//*                className="TVector">
//* 
//* <Overview>
//* Fetches one element of the <Func name="TVector"/> at a given
//* position.
//* </Overview>
//* 
//* <Parameter name="index">
//* An integer representing the index of the element to retrieve.
//* </Parameter>
//* 
//* <Returns>
//* A reference to the object at the <Arg name="index"/> position.
//* </Returns>
//* 
//* <Description>
//* If <Arg name="index"/> is negative or equal to or larger than
//* the size of the <Func name="TVector"/> then a runtime error ocurrs.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "getAt" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object getAt(final TeaFunction obj,
                        final TeaContext     context,
                        final Object[]    args)
        throws TeaRunException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "index");
        }

        Object result = null;
        int    index  = Args.getInt(args,2).intValue();

        try {
            result = _vector.get(index);
        } catch ( IndexOutOfBoundsException e ) {
            String   msg    = "index {0} out of bounds for vector of size {1}";
            Object[] msgFmt =
                { String.valueOf(index), String.valueOf(_vector.size()) };
            throw new TeaRunException(msg, msgFmt);
        }

        return result;
    }





//* 
//* <TeaMethod name="setAt"
//*            arguments="anObject index"
//*                className="TVector">
//* 
//* <Overview>
//* Changes one element of the <Func name="TVector"/> at a given
//* position.
//* </Overview>
//* 
//* <Parameter name="anObject">
//* The object to insert at the <Arg name="index"/> position.
//* </Parameter>
//* 
//* <Parameter name="index">
//* An integer representing the index of the element to retrieve.
//* </Parameter>
//* 
//* <Returns>
//* A reference to the object for which the method was called.
//* </Returns>
//* 
//* <Description>
//* If <Arg name="index"/> is negative or equal to or larger than
//* the size of the <Func name="TVector"/> then a runtime error ocurrs.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "setAt" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object setAt(final TeaFunction obj,
                        final TeaContext     context,
                        final Object[]    args)
        throws TeaRunException {

        if ( args.length != 4 ) {
            throw new SNumArgException(args, "object index");
        }

        Object elem  = args[2];
        int    index = Args.getInt(args,3).intValue();

        try {
            _vector.set(index, elem);
        } catch ( IndexOutOfBoundsException e ) {
            String   msg    = "index {0} out of bounds for vector of size {1}";
            Object[] msgFmt =
                { String.valueOf(index), String.valueOf(_vector.size()) };
            throw new TeaRunException(msg, msgFmt);
        }

        return obj;
    }





//* 
//* <TeaMethod name="getElements"
//*                className="TVector">
//* 
//* <Overview>
//* Fetches a list containing the <Func name="TVector"/> elements.
//* </Overview>
//* 
//* <Returns>
//* A list containing the <Func name="TVector"/> elements.
//* </Returns>
//* 
//* <Description>
//* The order of the elements in the list that is returned is the
//* same order of the elements in the <Func name="TVector"/>.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "getElements" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object getElements(final TeaFunction obj,
                              final TeaContext     context,
                              final Object[]    args) {

        TeaPair    empty  = TeaPair.emptyList();
        TeaPair    head   = empty;
        TeaPair    node   = null;

        for ( Object value : _vector ) {
            TeaPair newNode = new TeaPair(value, empty);

            if ( node == null ) {
                head = newNode;
            } else {
                node.setCdr(newNode);
            }
            node = newNode;
        }

        return head;
    }





//* 
//* <TeaMethod name="pop"
//*                className="TVector">
//* 
//* <Overview>
//* Retrieves and removes the last element of the <Func name="TVector"/>.
//* </Overview>
//* 
//* <Returns>
//* A reference to the object that was the last element in the
//* <Func name="TVector"/>.
//* </Returns>
//* 
//* <Description>
//* If the <Func name="TVector"/> has no elements then a runtime error
//* ocurrs.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "pop" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object pop(final TeaFunction obj,
                      final TeaContext     context,
                      final Object[]    args)
        throws TeaRunException {

        int    lastIndex = _vector.size() - 1;
        Object result    = null;

        if ( lastIndex >= 0 ) {
            result = _vector.get(lastIndex);
            _vector.remove(lastIndex);
        } else {
            throw new TeaRunException("can not pop from an empty vector");
        }

        return result;
    }





//* 
//* <TeaMethod name="clear"
//*                className="TVector">
//* 
//* <Overview>
//* Removes all the elements from the <Func name="TVector"/>.
//* </Overview>
//* 
//* <Returns>
//* A reference to the object for which the method was called.
//* </Returns>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "clear" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object clear(final TeaFunction obj,
                        final TeaContext     context,
                        final Object[]    args) {

        _vector.clear();

        return obj;
    }





//* 
//* <TeaMethod name="sort"
//*            arguments="aFunction"
//*                className="TVector">
//* 
//* <Overview>
//* Sorts the elements of the <Func name="TVector"/> acording to a
//* comparison function.
//* </Overview>
//*
//* <Parameter name="aFunction">
//* The function to be used as comparison function.
//* </Parameter>
//* 
//* <Returns>
//* A reference to the object for which the method was called.
//* </Returns>
//* 
//* <Description>
//* The function <Arg name="aFunction"/> will always be called with
//* two arguments. The arguments will be elements of the
//* <Func name="TVector"/>. The function <Arg name="aFunction"/> is supposed
//* to return a negative, zero or positive value if its first argument is
//* less than, equal to or greater than its second argument.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "sort" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object sort(final TeaFunction obj,
                       final TeaContext     context,
                       final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "comparison-function");
        }

        int size = _vector.size();

        if ( size > 1 ) {
             _compFunc = Args.getFunction(context, args, 2);
             _context  = context;
             quicksort(_vector, 0, size-1);
             _compFunc = null;
             _context  = null;
         }

         return obj;
    }





/**************************************************************************
 *
 * Adapted from "C: The Complete Reference" by Schildt.
 *
 **************************************************************************/

    private void quicksort(final List<Object> v,
                           final int          left,
                           final int          right)
        throws TeaException {

        int    i = left;
        int    j = right;
        Object x = v.get((left+right) / 2);
        Object y;

        do {
            while ( (compare(v.get(i),x)<0) && (i<right) ) {
                i++;
            }
            while ( (compare(x,v.get(j))<0) && (j>left) ) {
                j--;
            }
            if ( i <= j ) {
                y = v.get(i);
                v.set(i, v.get(j));
                v.set(j, y);
                i++;
                j--;
            }
        } while ( i <= j );
        
        if ( left < j ) {
            quicksort(v, left, j);
        }
        if ( i < right ) {
            quicksort(v, i, right);
        }
    }





/**************************************************************************
 *
 * @return -1 if x<y; 0 if x==y; 1 if x>y.
 *
 **************************************************************************/

    private int compare(final Object x,
                        final Object y)
        throws TeaException {

        Object[] args   = new Object[3];
        Object   value  = null;
        Number   result = null;

        args[0] = _compFunc;
        args[1] = x;
        args[2] = y;
        value   = _compFunc.exec(_compFunc, _context, args);

        try {
            result = (Number)value;
        } catch ( ClassCastException e ) {
            String msg = "comparison function must return a number, not a {0}";
            throw new TeaRunException(msg, Types.getTypeName(value));
        }

        return result.intValue();
    }





/**************************************************************************
 *
 * Retrieves the recomended name for the TOS class. This method is
 * expected to exist when the TOS class is loaded into a Tea runtime
 * by calling the Tea "load-class" method.
 *
 * @return The name for the TOS class.
 *
 **************************************************************************/

    public static String getTosClassName() {

        return CLASS_NAME;
    }





/**************************************************************************
 *
 * Creates a TOS <code>TVector</code> instance. The
 * <code>context</code> is used to retrieve the TOS class object to
 * assign to the <code>TVector</code>.
 *
 * <p>The internal <code>java.util.List</code> where the vector
 * elements will be stored is automatically created.</p>
 *
 * @param context The context where the <code>TVector</code> instance
 * is being created.
 *
 * @return A newly created and initialized <code>SVector</code>.
 *
 **************************************************************************/

    public static SVector newInstance(final TeaContext context)
        throws TeaException {

        STosClass theClass = STosUtil.getClass(context, CLASS_NAME_S);
        SVector   vector   = new SVector(theClass);
        Object[]  ctorArgs = { null, null };

        vector.init(context, ctorArgs);

        return vector;
    }





/**************************************************************************
 *
 * Creates a TOS <code>TVector</code> instance and supplies the
 * <code>java.util.List</code> used to store the vector elements.
 *
 * @param context The context where the <code>TVector</code> instance
 * is being created.
 *
 * @param contents The <code>java.util.List</code> that will be used
 * to store the vector elements.
 *
 * @return A newly created and initialized <code>SVector</code>.
 *
 **************************************************************************/

    public static SVector newInstance(final TeaContext     context,
                                      final List<Object> contents)
        throws TeaException {

        STosClass theClass = STosUtil.getClass(context, CLASS_NAME_S);
        SVector   vector   = new SVector(theClass, contents);
        Object[]  ctorArgs = { null, null };

        vector.init(context, ctorArgs);

        return vector;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

