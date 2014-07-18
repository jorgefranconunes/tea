/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.modules.tos.SList;
import com.pdmfc.tea.Args;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaBlock;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaNull;
import com.pdmfc.tea.TeaPair;
import com.pdmfc.tea.TeaSymbol;
import com.pdmfc.tea.TeaNumArgException;
import com.pdmfc.tea.TeaRunException;
import com.pdmfc.tea.TeaTypeException;
import com.pdmfc.tea.Types;
import com.pdmfc.tea.TeaFunctionImplementor;
import com.pdmfc.tea.TeaEnvironment;
import com.pdmfc.tea.TeaModule;





//*
//* <TeaModule name="tea.tos">
//* 
//* <Overview>
//* Tea object system.
//* </Overview>
//*
//* <Description>
//* Tea object system.
//* </Description>
//*
//* </TeaModule>
//*





/**************************************************************************
 *
 * Package of commands for object oriented programming.
 *
 **************************************************************************/

public final class ModuleTos
    extends Object
    implements TeaModule {





    // Stores the TOS class objects indexed by the respective Java
    // class name. Used by the "functionLoadClass(...)" method.
    private Map<String,STosClass> _tosClasses =
        new HashMap<String,STosClass>();

    private TeaEnvironment _environment = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public ModuleTos() {

       // Nothing to do.
   }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void init(final TeaEnvironment environment)
        throws TeaException {

        _environment = environment;

        // The functions provided by this module are implemented as
        // methods of this class with the TeaFunction annotation.
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
//* <TeaFunction name="class"
//*              arguments="className [superClass] memberList"
//*              module="tea.tos">
//*
//* <Overview>
//* Creates a new class object.
//* </Overview>
//*
//* <Parameter name="className">
//* Symbol that will identify the new class.
//* </Parameter>
//*
//* <Parameter name="superClass">
//* Symbol identifying the super class of the new class.
//* </Parameter>
//*
//* <Parameter name="memberList">
//* List of symbols representing the members of the new class.
//* </Parameter>
//*
//* <Returns>
//* A reference to the new class object.
//* </Returns>
//*
//* <Description>
//* Creates a new class object and stores a reference to it in the global
//* variable identified by the symbol <Arg name="className"/>.
//*
//* <P>The <Func name="class"/> function could be implemented using the
//* <FuncRef name="new-class"/> function like this:</P>
//*
//* <pre>
//* global class args {
//*     global [car $args] [apply new-class [cdr $args]]
//* }
//* </pre>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>class</code> function.
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

    @TeaFunctionImplementor("class")
    public Object functionClass(final TeaFunction func,
                                final TeaContext  context,
                                final Object[]    args)
        throws TeaException {

        if ( (args.length<3) || (args.length>4) ) {
            String usage = "class-name [base-class] list-of-members";
            throw new TeaNumArgException(args, usage);
        }

        TeaSymbol className          = Args.getSymbol(args, 1);
        STosClass  baseClass          =
            (args.length==3) ? null : STosUtil.getClass(context,args,2);
        TeaPair          memberList  =
            Args.getPair(args, (args.length==3)? 2 : 3);
        SList<TeaSymbol> memberNames =
            new SList<TeaSymbol>();

        for ( TeaPair e=memberList; !e.isEmpty(); e=e.cdr() ) {
            try {
                TeaSymbol memberName = (TeaSymbol)e.car();
                memberNames.prepend(memberName);
            } catch ( ClassCastException exception ) {
                String msg = "found a {0} when expecting a Symbol";
                throw new TeaRunException(args,
                                            msg,
                                            Types.getTypeName(e.car()));
            }
        }

        STosClass theClass     = new STosClass(baseClass, memberNames);
        String    classNameStr = className.getName();

        theClass.setName(classNameStr);
        _environment.addGlobalVar(classNameStr, theClass);

        return theClass;
    }





//* 
//* <TeaFunction name="new-class"
//*              arguments="memberList"
//*              module="tea.tos">
//*
//* <Prototype arguments="superClass memberList"/>
//*
//* <Overview>
//* Creates a new class object.
//* </Overview>
//*
//* <Parameter name="superClass">
//* Symbol identifying the super class of the new class.
//* </Parameter>
//*
//* <Parameter name="memberList">
//* List of symbols representing the members of the new class.
//* </Parameter>
//*
//* <Returns>
//* A reference to the newly created class object.
//* </Returns>
//*
//* <Description>
//* Creates a new class object.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>new-class</code> function.
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

    @TeaFunctionImplementor("new-class")
    public Object functionNewClass(final TeaFunction func,
                                   final TeaContext  context,
                                   final Object[]    args)
        throws TeaException {

        if ( (args.length<2) || (args.length>3) ) {
            throw new TeaNumArgException(args,
                                       "[base-class] list-of-members");
        }

        STosClass         baseClass   = null;
        TeaPair          memberList  = null;
        SList<TeaSymbol> memberNames = new SList<TeaSymbol>();
        STosClass         result      = null;

        switch ( args.length ) {
        case 2:
            baseClass  = null;
            memberList = Args.getPair(args, 1);
            break;
        case 3 :
            baseClass  = STosUtil.getClass(context, args, 1);
            memberList = Args.getPair(args, 2);
            break;
        default :
            throw new TeaNumArgException(args, "[base-class] list-of-members");
        }

        for ( TeaPair e=memberList; !e.isEmpty(); e=e.cdr() ) {
            try {
                TeaSymbol memberName = (TeaSymbol)e.car();
                memberNames.prepend(memberName);
            } catch ( ClassCastException exception ) {
                String msg = "found a {0} when expecting a Symbol";
                throw new TeaRunException(args,
                                            msg,
                                            Types.getTypeName(e.car()));
            }
        }

        result = new STosClass(baseClass, memberNames);

        return result;
    }





//* 
//* <TeaFunction name="new"
//*              arguments="className [arg1 ...]"
//*              module="tea.tos">
//*
//* <Overview>
//* Creates an instance of a previously created class.
//* </Overview>
//*
//* <Parameter name="className">
//* Symbol identifying the class of which a new instance will be created.
//* </Parameter>
//*
//* <Parameter name="arg1">
//* Object passed as argument to the constructor method.
//* </Parameter>
//* 
//* <Returns>
//* A reference to the newly created object.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>new</code> function.
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

    @TeaFunctionImplementor("new")
    public static Object functionNew(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        if ( args.length < 2 ) {
            throw new TeaNumArgException(args, "class-name [constructor-args]");
        }

        return STosUtil.getClass(context, args, 1).newInstance(context, args);
    }





//* 
//* <TeaFunction name="method"
//*              arguments="className methodName argList body"
//*              module="tea.tos">
//*
//* <Overview>
//* Adds a method to a previously created class.
//* </Overview>
//*
//* <Parameter name="className">
//* Symbol identifying the class the method is being added to.
//* </Parameter>
//*
//* <Parameter name="argList">
//* List of symbols representing the formal arguments of the method,
//* or a symbol representing the list of formal arguments.
//* </Parameter>
//*
//* <Parameter name="body">
//* Code block that will be used as the body of the method.
//* </Parameter>
//*
//* <Description>
//* The method named <Func name="constructor"/> has a special meaning.
//* Whenever a new instance of <Arg name="className"/> is created by
//* a call to the <Func name="new"/> function
//* the <Func name="constructor"/> method is automatically called. At the
//* time of invocation it will receive as arguments the same arguments that
//* were passed to the <Func name="new"/> function.
//* It is responsability of the <Func name="constructor"/> method to call
//* the constructor of the super class, if the class <Arg name="className"/>
//* has one.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>method</code> function.
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

    @TeaFunctionImplementor("method")
    public static Object functionMethod(final TeaFunction func,
                                        final TeaContext  context,
                                        final Object[]    args)
        throws TeaException {

        if ( args.length != 5 ) {
            String usage = "class-name method-name formal-param body";
            throw new TeaNumArgException(args, usage);
        }

        Object params = args[3];

        if ( params instanceof TeaPair ) {
            fixedArgsMethod(context, args);
        } else {
            if ( params instanceof TeaSymbol ) {
                varArgsMethod(context, args);
            } else {
                throw new TeaTypeException(args, 3, "symbol or list of symbols");
            }
        }

        return TeaNull.NULL;
    }





/**************************************************************************
 *
 * Creates a method with a fixed number of parameters.
 *
 **************************************************************************/

    private static void fixedArgsMethod(final TeaContext context,
                                        final Object[] args)
        throws TeaException {
        
        STosClass    methodClass = STosUtil.getClass(context, args, 1);
        TeaSymbol   methodName  = Args.getSymbol(args, 2);
        TeaPair     paramList   = Args.getPair(args, 3);
        TeaBlock    body        = Args.getBlock(args, 4);
        int          numParam    = paramList.length();
        TeaSymbol[] parameters  = new TeaSymbol[numParam];
        Iterator     it          = paramList.iterator();

        for ( int i=0; it.hasNext(); i++) {
            try {
                parameters[i] = (TeaSymbol)it.next();
            } catch ( ClassCastException e1 ) {
                String msg = "formal parameters must be symbols";
                throw new TeaRunException(args, msg);
            }
        }

        STosMethod method =
            new STosMethod(methodClass, methodName, parameters, body);

        if ( methodName != STosClass.CONSTRUCTOR_NAME ) {
            methodClass.addMethod(methodName, method);
        } else {
            methodClass.addConstructor(method);
        }
    }





/**************************************************************************
 *
 * Creates a procedure that accpets a variable number of arguments.
 *
 **************************************************************************/

    private static void varArgsMethod(final TeaContext context,
                                      final Object[] args)
        throws TeaException {

        STosClass    methodClass = STosUtil.getClass(context, args, 1);
        TeaSymbol   methodName  = Args.getSymbol(args, 2);
        TeaSymbol   symbol      = Args.getSymbol(args, 3);
        TeaBlock    body        = Args.getBlock(args, 4);
        TeaFunction method      = new STosMethodVarArg(methodClass,
                                                        methodName,
                                                        symbol,
                                                        body);

        if ( methodName != STosClass.CONSTRUCTOR_NAME ) {
            methodClass.addMethod(methodName, method);
        } else {
            methodClass.addConstructor(method);
        }
   }





//* 
//* <TeaFunction name="load-class"
//*              arguments="javaClassName"
//*              module="tea.tos">
//*
//* <Overview>
//* Dynamically loads new TOS class from a Java library.
//* </Overview>
//*
//* <Parameter name="javaClassName">
//* String identifying a Java class name.
//* </Parameter>
//*
//* <Returns>
//* A class object.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>load-class</code> function.
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

    @TeaFunctionImplementor("load-class")
    public Object functionLoadClass(final TeaFunction func,
                                    final TeaContext  context,
                                    final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "java-class-name");
        }

        String    className = Args.getString(args,1);
        Class     javaClass = null;
        STosClass tosClass  = _tosClasses.get(className);
        String    msg       = null;

        if ( tosClass == null ) {
            try {
                javaClass = Class.forName(className);
                if ( STosClass.class.isAssignableFrom(javaClass) ) {
                    tosClass  = (STosClass)javaClass.newInstance();
                } else {
                    tosClass = new SJavaClass(javaClass);
                }
            } catch ( ClassNotFoundException e1 ) {
                msg = "could not find class '" + className + "'";
            } catch ( IllegalArgumentException e2 ) {
                msg = "illegal initializer for class '" + className + "'";
            } catch ( IllegalAccessException e3 ) {
                msg = "class '" + className+"' or its initializer are not accessible";
            } catch ( ClassCastException e4 ) {
                msg = "class '" + className + "' singleton is not of class STosClass";
            } catch ( NoSuchMethodError e5 ) {
                msg = "class '" + className + "' does not have the correct initializer";
            } catch ( InstantiationException e6 ) {
                msg = "instatiation of class '" + className + "' failed";
            } catch ( ExceptionInInitializerError e8 ) {
                msg = "initializer for class '" + className + "' failed";
            } catch ( SecurityException e10 ) {
                msg = "access to class '" + className + "' information is denied";
            }
            if ( msg != null ) {
                throw new TeaRunException(msg);
            }
            _tosClasses.put(className, tosClass);
        }

        return tosClass;
    }





//* 
//* <TeaFunction name="class-base-of"
//*              arguments="classObject"
//*              module="tea.tos">
//*
//* <Overview>
//* Fetches the base class object of a given TOS class.
//* </Overview>
//*
//* <Parameter name="classObject">
//* A TOS class object or a symbol referencing a variable containing
//* a TOS class object.
//* </Parameter>
//*
//* <Returns>
//* The base class object of <Arg name="classObject"/> or null
//* if <Arg name="classObject"/> has no base class.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>class-base-of</code> function.
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

    @TeaFunctionImplementor("class-base-of")
    public static Object functionBaseOf(final TeaFunction func,
                                        final TeaContext  context,
                                        final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "classObject");
        }

        STosClass tosClass  = STosUtil.getClass(context, args, 1);
        STosClass baseClass = tosClass.getSuperClass();

        return (baseClass==null) ? TeaNull.NULL : baseClass;
    }





//* 
//* <TeaFunction name="class-of"
//*              arguments="tosObject"
//*              module="tea.tos">
//*
//* <Overview>
//* Fetches the class object of a given TOS object.
//* </Overview>
//*
//* <Parameter name="tosObject">
//* A TOS object.
//* </Parameter>
//*
//* <Returns>
//* The class object of <Arg name="tosObject"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>class-of</code> function.
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

    @TeaFunctionImplementor("class-of")
    public static Object functionClassOf(final TeaFunction func,
                                         final TeaContext  context,
                                         final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "tosObject");
        }

        return STosUtil.getTosObj(args,1).getTosClass();
    }





//* 
//* <TeaFunction name="class-is-a"
//*              arguments="class1 class2"
//*              module="tea.tos">
//*
//* <Overview>
//* Checks if a class object is the same as or derived from another.
//* </Overview>
//*
//* <Parameter name="class1">
//* The TOS class object that will be checked to see if is derived
//* from <Arg name="class2"/>.
//* </Parameter>
//* 
//* <Parameter name="class2">
//* A TOS class object.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="class1"/> is the same class object as
//* <Arg name="class2"/> or derived from <Arg name="class2"/>.
//* </Returns>
//*
//* <Description>
//* A simple implementation of this function could be as follows.
//* <Code>
//* define class-is-a ( class1 class2 ) {
//*     cond {
//*         same? $class1 $class2
//*     } $true {
//*         null? $class1
//*     } $false {
//*         class-is-a [class-base-of $class1] $class2
//*     }
//* }
//* </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>class-is-a</code> function.
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

    @TeaFunctionImplementor("class-is-a")
    public static Object functionIsA(final TeaFunction func,
                                     final TeaContext  context,
                                     final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "class1 class2");
        }

        STosClass tosClass1 = STosUtil.getClass(context, args, 1);
        STosClass tosClass2 = STosUtil.getClass(context, args, 2);

        while ( tosClass1 != null ) {
            if ( tosClass1 == tosClass2 ) {
                return Boolean.TRUE;
            }
            tosClass1 = tosClass1.getSuperClass();
        }

        return Boolean.FALSE;
    }





//* 
//* <TeaFunction name="class-get-name"
//*              arguments="classObject"
//*              module="tea.tos">
//*
//* <Overview>
//* Fetches the name associated with a class.
//* </Overview>
//*
//* <Parameter name="classObject">
//* A TOS class object or a symbol referencing a variable containing
//* a TOS class object.
//* </Parameter>
//*
//* <Returns>
//* A string object representing the name associated with
//* <Arg name="classObject"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>class-get-name</code> function.
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

    @TeaFunctionImplementor("class-get-name")
    public static Object functionGetName(final TeaFunction func,
                                         final TeaContext  context,
                                         final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "classObject");
        }

        return STosUtil.getClass(context, args, 1).getName();
    }





//* 
//* <TeaFunction name="tos-obj?"
//*              arguments="value"
//*              module="tea.tos">
//*
//* <Overview>
//* Checks if the given value is a TOS object.
//* </Overview>
//*
//* <Parameter name="value">
//* The object being checked.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="value"/> is a TOS instance. False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* <Since version="3.1.10"/>
//*
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>tos-obj?</code> function.
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

    @TeaFunctionImplementor("tos-obj?")
    public static Object functionIsTosObj(final TeaFunction func,
                                          final TeaContext  context,
                                          final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "value");
        }

        Object  value  = args[1];
        boolean result = value instanceof STosObj;

        return result ? Boolean.TRUE : Boolean.FALSE;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

