/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.tos.STosUtil;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNumArgException;





//* 
//* <TeaClass name="THashtable"
//*           module="tea.util">
//*
//* <Overview>
//* Represents a set of objects that are indexed by objects of any type.
//* </Overview>
//*
//* <Description>
//* A <Class name="THashtable"/> represents a set of unordered objects.
//* Each object in the set is indexed by a key. A key is an object of
//* any type. A <Class name="THashtable"/> can be thought of as a sequence
//* of key, value pairs.
//* </Description>
//*
//* </TeaClass>
//* 

/**************************************************************************
 *
 * Implements an TOS object that acts like a java <code>Map</code>.
 *
 **************************************************************************/

public final class SHashtable
    extends STosObj {





    private static final String     CLASS_NAME   = "THashtable";
    private static final SObjSymbol CLASS_NAME_S =
        SObjSymbol.addSymbol(CLASS_NAME);

    private Map<Object,Object> _hashtable = null;





/**************************************************************************
 *
 * Initializes the object internal state. The TOS class assigned to
 * this object is supposed to generate instances of TOS objects that
 * are <code>SHashtable</code> instances.
 *
 * @param myClass The TOS class to assign to the TOS objects.
 *
 **************************************************************************/

    public SHashtable(final STosClass myClass)
        throws STeaException {

        super(myClass);

        _hashtable = new HashMap<Object,Object>();
    }





/**************************************************************************
 *
 * Initializes the object internal state and specifies the
 * <code>java.util.Map<code> to be used for storing the hashtable
 * elements. The <code>contents</code> <code>java.util.Map</code> may
 * already contain some elements.
 *
 * @param myClass The TOS class to assign to the TOS objects.
 *
 * @param contents Will store the hashtable components.
 *
 **************************************************************************/

    private SHashtable(final STosClass          myClass,
                       final Map<Object,Object> contents)
        throws STeaException {

        super(myClass);

        _hashtable = contents;
    }





/**************************************************************************
 *
 * Fetches the <code>java.util.Map</code> used to store the hashtable
 * elements. The returned <code>java.util.Map</code> may be modified
 * at will. Nevertheless, concurrent use from within multiple threads
 * must be avoided.
 *
 * @return  The <code>java.util.Map</code> used to store the hashtable
 * elements.
 *
 **************************************************************************/

    public Map<Object,Object> getInternalMap() {

        return _hashtable;
    }





//* 
//* <TeaMethod name="constructor"
//*                className="THashtable">
//* 
//* <Overview>
//* Initializes an empty hashtable.
//* </Overview>
//* 
//* <Description>
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

    public Object constructor(final SObjFunction obj,
                              final SContext     context,
                              final Object[]     args)
        throws STeaException {

        return obj;
    }





//* 
//* <TeaMethod name="put"
//*            arguments="key value"
//*                className="THashtable">
//* 
//* <Overview>
//* Adds an object to the <Class name="THashtable"/>.
//* </Overview>
//*
//* <Parameter name="key">
//* An object of any type. It will be used as the index to
//* <Arg name="value"/>.
//* </Parameter>
//*
//* <Parameter name="value">
//* The object being added to the <Class name="THashtable"/>.
//* </Parameter>
//*
//* <Returns>
//* A reference to the <Class name="THashtable"/> object for which
//* the method was called.
//* </Returns>
//* 
//* <Description>
//* If an object with the same key already exists in the
//* <Class name="THashtable"/> then <Arg name="value"/> will take
//* its place.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "put" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object put(final SObjFunction obj,
                      final SContext     context,
                      final Object[]     args)
        throws STeaException {

        if ( args.length != 4 ) {
            throw new SNumArgException(args, "key value");
        }

        _hashtable.put(args[2], args[3]);

        return obj;
    }





//* 
//* <TeaMethod name="get"
//*            arguments="key"
//*                className="THashtable">
//* 
//* <Overview>
//* Fetches an object stored in the <Func name="THashtable"/>.
//* </Overview>
//*
//* <Parameter name="key">
//* An object of any type. It will be used as the index into
//* the object to fetch.
//* </Parameter>
//*
//* <Returns>
//* A reference to the object indexed by <Arg name="key"/>.
//* The null object if there is no object associated with <Arg name="key"/>
//* stored in the <Func name="THashtable"/>. You can check if
//* there is an element indexed by <Arg name="key"/> my calling the
//* <MethodRef tosClass="THashtable" name="isKey"/> method.
//* </Returns>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "get" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object get(final SObjFunction obj,
                      final SContext     context,
                      final Object[]     args)
        throws STeaException {
        
        if ( args.length != 3 ) {
            throw new SNumArgException(args, "key");
        }

        Object result = _hashtable.get(args[2]);

        return (result==null) ? SObjNull.NULL : result;
    }





//* 
//* <TeaMethod name="getKeys"
//*                className="THashtable">
//* 
//* <Overview>
//* Fetches a list of the keys of the objects stored in the
//* <Arg name="THashtable"/>.
//* </Overview>
//*
//* <Returns>
//* A list containing all the keys for the objects stored in the
//* <Func name="THashtable"/>.
//* </Returns>
//* 
//* <Description>
//* There is no guarantee on the order of the elements in the list
//* that is returned.
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "getKeys" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object getKeys(final SObjFunction obj,
                          final SContext     context,
                          final Object[]     args)
        throws STeaException {

        SObjPair    empty   = SObjPair.emptyList();
        SObjPair    head    = empty;
        SObjPair    element = null;

        for ( Iterator i=_hashtable.keySet().iterator(); i.hasNext(); ) {
            Object   key  = i.next();
            SObjPair node = new SObjPair(key, empty);

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
//* <TeaMethod name="getElements"
//*                className="THashtable">
//* 
//* <Overview>
//* Fetches a list of the objects stored in the
//* <Arg name="THashtable"/>.
//* </Overview>
//*
//* <Returns>
//* A list containing all the objects stored in the
//* <Func name="THashtable"/>.
//* </Returns>
//* 
//* <Description>
//* There is no guarantee on the order of the elements in the list
//* that is returned.
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

    public Object getElements(final SObjFunction obj,
                              final SContext     context,
                              final Object[]     args)
        throws STeaException {

        SObjPair   empty     = SObjPair.emptyList();
        SObjPair   head      = empty;
        SObjPair   element   = null;

        for ( Iterator i= _hashtable.values().iterator(); i.hasNext(); ) {
            Object   value = i.next();
            SObjPair node  = new SObjPair(value, empty);

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
//* <TeaMethod name="isKey"
//*            arguments="object"
//*                className="THashtable">
//* 
//* <Overview>
//* Checks if an object is being used as key for one of the elements in
//* the <Func name="THashtable"/>.
//* </Overview>
//*
//* <Returns>
//* True if <Arg name="object"/> is being used as key for one of
//* the elements in the <Func name="THashtable"/>.
//* False otherwise.
//* </Returns>
//* 
//* <Description>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "isKey" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object isKey(final SObjFunction obj,
                        final SContext     context,
                        final Object[]     args)
        throws STeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "key");
        }

        Object key = args[2];

        return _hashtable.containsKey(key) ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaMethod name="clear"
//*                className="THashtable">
//* 
//* <Overview>
//* Removes all the elements from the <Func name="THashtable"/>.
//* </Overview>
//*
//* <Returns>
//* A reference to the <Func name="THashtable"/> object for which
//* the method was called.
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

    public Object clear(final SObjFunction obj,
                        final SContext     context,
                        final Object[]     args)
        throws STeaException {

        _hashtable.clear();

        return obj;
    }





//* 
//* <TeaMethod name="remove"
//*            arguments="[key ...]"
//*                className="THashtable">
//* 
//* <Overview>
//* Removes one or more elements from the <Class name="THashtable"/>.
//* </Overview>
//*
//* <Parameter name="key">
//* The key of the element to remove. It may be an object of any type.
//* </Parameter>
//*
//* <Returns>
//* A reference to the <Class name="THashtable"/> object for which
//* the method was called.
//* </Returns>
//* 
//* <Description>
//* Removes the elements in this <Class name="THashtable"/> associated
//* with each of the keys given as arguments.
//*
//* <P>If there is no object indexed by <Arg name="key"/> in the
//* <Func name="THashtable"/> then it has no effect.</P>
//* </Description>
//* 
//* </TeaMethod>
//* 

/**************************************************************************
 *
 * The implementation for the "remove" TOS method.
 *
 * @param obj Reference to the object the TOS method was invoked for.
 *
 * @param context The context where the method invocation is taking
 * place.
 *
 * @param args The arguments the TOS method was called with.
 *
 **************************************************************************/

    public Object remove(final SObjFunction obj,
                         final SContext     context,
                         final Object[]     args)
        throws STeaException {

        int keyCount = args.length - 2;

        for ( int i=0; i<keyCount; i++ ) {
            _hashtable.remove(args[i+2]);
        }

        return obj;
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
 * Creates a TOS <code>THashtable</code> instance. The
 * <code>context</code> is used to retrieve the TOS class object to
 * assign to the <code>THashtable</code>.
 *
 * <p>The internal <code>java.util.Map</code> where the hashtable
 * elements will be stored is automatically created.</p>
 *
 * @param context The context where the <code>THashtable</code>
 * instance is being created.
 *
 * @return A newly created and initialized <code>SHashtable</code>.
 *
 **************************************************************************/

    public static SHashtable newInstance(final SContext context)
        throws STeaException {

        STosClass  theClass = STosUtil.getClass(context, CLASS_NAME_S);
        SHashtable table    = new SHashtable(theClass);
        Object[]   ctorArgs = { null, null };

        table.init(context, ctorArgs);

        return table;
    }





/**************************************************************************
 *
 * Creates a TOS <code>THashtable</code> instance and supplies the
 * <code>java.util.Map</code> used to store the hashtable elements.
 *
 * @param context The context where the <code>THashtable</code>
 * instance is being created.
 *
 * @param contents The <code>java.util.Map</code> that will be used
 * to store the hashtable elements.
 *
 * @return A newly created and initialized <code>SHashtable</code>.
 *
 **************************************************************************/

    public static SHashtable newInstance(final SContext           context,
                                         final Map<Object,Object> contents)
        throws STeaException {

        STosClass  theClass = STosUtil.getClass(context, CLASS_NAME_S);
        SHashtable table    = new SHashtable(theClass, contents);
        Object[]   ctorArgs = { null, null };

        table.init(context, ctorArgs);

        return table;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

