/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.runtime.SArgs;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjBlock;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;
import com.pdmfc.tea.util.SList;





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

public final class SModuleTos
    extends Object
    implements SModule {





    // Stores the TOS class objects indexed by the respective Java
    // class name. Used by the "functionLoadClass(...)" method.
    private Map<String,STosClass> _tosClasses =
        new HashMap<String,STosClass>();

    private SContext _globalContext = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SModuleTos() {

       // Nothing to do.
   }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    public void init(final SContext context)
        throws STeaException {

        _globalContext = context;

        context.newVar("class",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionClass(func, context, args);
                           }
                       });

        context.newVar("new-class",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionNewClass(func, context, args);
                           }
                       });

        context.newVar("new",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionNew(func, context, args);
                           }
                       });

        context.newVar("method",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionMethod(func, context, args);
                           }
                       });

        context.newVar("load-class",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionLoadClass(func, context, args);
                           }
                       });

        context.newVar("class-base-of",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionBaseOf(func, context, args);
                           }
                       });

        context.newVar("class-of",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionClassOf(func, context, args);
                           }
                       });

        context.newVar("class-is-a",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionIsA(func,
                                                  context,
                                                  args);
                           }
                       });

        context.newVar("class-get-name",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionGetName(func,
                                                      context,
                                                      args);
                           }
                       });

        context.newVar("tos-obj?",
                       new SObjFunction() {
                           public Object exec(final SObjFunction func,
                                              final SContext     context,
                                              final Object[]     args)
                               throws STeaException {
                               return functionIsTosObj(func,
                                                       context,
                                                       args);
                           }
                       });
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    public void end() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    public void start() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    public void stop() {

        // Nothing to do.
    }





//* 
//* <TeaFunction name="class"
//*                 arguments="className [superClass] memberList"
//*             module="tea.tos">
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
 * 
 *
 **************************************************************************/

    private Object functionClass(final SObjFunction func,
                                 final SContext     context,
                                 final Object[]     args)
        throws STeaException {

        if ( (args.length<3) || (args.length>4) ) {
            String usage = "class-name [base-class] list-of-members";
            throw new SNumArgException(args, usage);
        }

        SObjSymbol className   = SArgs.getSymbol(args, 1);
        STosClass  baseClass   = (args.length==3)
            ? null : STosUtil.getClass(context,args,2);
        SObjPair   memberList  = SArgs.getPair(args, (args.length==3)? 2 : 3);
        SList      memberNames = new SList();

        for ( SObjPair e=memberList; e._car!=null; e=(SObjPair)e._cdr ) {
            Object memberName = e._car;
            if ( ! (memberName instanceof SObjSymbol) ) {
                String msg = "found a {0} when expecting a Symbol";
                throw new SRuntimeException(args,
                                            msg,
                                            STypes.getTypeName(memberName));
            }
            memberNames.prepend(e._car);
        }

        STosClass theClass = new STosClass(baseClass, memberNames);

        theClass.setName(className.getName());
        _globalContext.newVar(className, theClass);

        return theClass;
    }





//* 
//* <TeaFunction name="new-class"
//*                 arguments="memberList"
//*             module="tea.tos">
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
 * 
 *
 **************************************************************************/

    private Object functionNewClass(final SObjFunction func,
                                    final SContext     context,
                                    final Object[]     args)
        throws STeaException {

        if ( (args.length<2) || (args.length>3) ) {
            throw new SNumArgException(args,
                                       "[base-class] list-of-members");
        }

        STosClass baseClass   = null;
        SObjPair  memberList  = null;
        SList     memberNames = new SList();
        STosClass result      = null;

        switch ( args.length ) {
        case 2:
            baseClass  = null;
            memberList = SArgs.getPair(args, 1);
            break;
        case 3 :
            baseClass  = STosUtil.getClass(context, args, 1);
            memberList = SArgs.getPair(args, 2);
            break;
        default :
            throw new SNumArgException(args, "[base-class] list-of-members");
        }

        for ( SObjPair e=memberList; e._car!=null; e=(SObjPair)e._cdr ) {
            Object memberName = e._car;
            if ( ! (memberName instanceof SObjSymbol) ) {
                String msg = "found a {0} when expecting a Symbol";
                throw new SRuntimeException(args,
                                            msg,
                                            STypes.getTypeName(memberName));
            }
            memberNames.prepend(e._car);
        }

        result = new STosClass(baseClass, memberNames);

        return result;
    }





//* 
//* <TeaFunction name="new"
//*                 arguments="className [arg1 ...]"
//*             module="tea.tos">
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
 * 
 *
 **************************************************************************/

    private static Object functionNew(final SObjFunction func,
                                      final SContext     context,
                                      final Object[]     args)
        throws STeaException {

        if ( args.length < 2 ) {
            throw new SNumArgException(args, "class-name [constructor-args]");
        }

        return STosUtil.getClass(context, args, 1).newInstance(context, args);
    }





//* 
//* <TeaFunction name="method"
//*                 arguments="className methodName argList body"
//*             module="tea.tos">
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
 * 
 * 
 **************************************************************************/

    private static Object functionMethod(final SObjFunction func,
                                         final SContext     context,
                                         final Object[]     args)
        throws STeaException {

        if ( args.length != 5 ) {
            String usage = "class-name method-name formal-param body";
            throw new SNumArgException(args, usage);
        }

        Object params = args[3];

        if ( params instanceof SObjPair ) {
            fixedArgsMethod(context, args);
        } else {
            if ( params instanceof SObjSymbol ) {
                varArgsMethod(context, args);
            } else {
                throw new STypeException(args, 3, "symbol or list of symbols");
            }
        }

        return SObjNull.NULL;
    }





/**************************************************************************
 *
 * Creates a method with a fixed number of parameters.
 *
 * @param arg0
 *    The name of the command from where this method is invoked.
 *
 * @paramList
 *    List of symbols representing the procedure formal arguments.
 *
 * @param body
 *    Block that will be the body of the procedure.
 *
 * @exception com.pdmfc.tea.STeaException
 *    Thrown if any of the elements in the formal parameter list is not
 *    a symbol.
 *
 **************************************************************************/

    private static void fixedArgsMethod(final SContext context,
                                        final Object[] args)
        throws STeaException {
        
        STosClass    methodClass = STosUtil.getClass(context, args, 1);
        SObjSymbol   methodName  = SArgs.getSymbol(args, 2);
        SObjPair     paramList   = SArgs.getPair(args, 3);
        SObjBlock    body        = SArgs.getBlock(args, 4);
        int          numParam    = paramList.length();
        SObjSymbol[] parameters  = new SObjSymbol[numParam];
        Iterator     it          = paramList.iterator();

        for ( int i=0; it.hasNext(); i++) {
            try {
                parameters[i] = (SObjSymbol)it.next();
            } catch (ClassCastException e1) {
                String msg = "formal parameters must be symbols";
                throw new SRuntimeException(args, msg);
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
 * @param symbol
 *    The name of the variable inside the procedure body that is a list
 *    containing all the arguments.
 *
 * @param body
 *    Block that will be the body of the procedure.
 *
 * @exception com.pdmfc.tea.STeaException
 *
 **************************************************************************/

    private static void varArgsMethod(final SContext context,
                                      final Object[] args)
        throws STeaException {

        STosClass    methodClass = STosUtil.getClass(context, args, 1);
        SObjSymbol   methodName  = SArgs.getSymbol(args, 2);
        SObjSymbol   symbol      = SArgs.getSymbol(args, 3);
        SObjBlock    body        = SArgs.getBlock(args, 4);
        SObjFunction method      = new STosMethodVarArg(methodClass,
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
//*                 arguments="javaClassName"
//*             module="tea.tos">
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
 * 
 *
 **************************************************************************/

    private Object functionLoadClass(final SObjFunction func,
                                     final SContext     context,
                                     final Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "java-class-name");
        }

        String    className = SArgs.getString(args,1);
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
            } catch (ClassNotFoundException e1) {
                msg = "could not find class '" + className + "'";
            } catch (IllegalArgumentException e2) {
                msg = "illegal initializer for class '" + className + "'";
            } catch (IllegalAccessException e3) {
                msg = "class '" + className+"' or its initializer are not accessible";
            } catch (ClassCastException e4) {
                msg = "class '" + className + "' singleton is not of class STosClass";
            } catch (NoSuchMethodError e5) {
                msg = "class '" + className + "' does not have the correct initializer";
            } catch (InstantiationException e6) {
                msg = "instatiation of class '" + className + "' failed";
            } catch (ExceptionInInitializerError e8) {
                msg = "initializer for class '" + className + "' failed";
            } catch (SecurityException e10) {
                msg = "access to class '" + className + "' information is denied";
            }
            if ( msg != null ) {
                throw new SRuntimeException(msg);
            }
            _tosClasses.put(className, tosClass);
        }

        return tosClass;
    }





//* 
//* <TeaFunction name="class-base-of"
//*                 arguments="classObject"
//*             module="tea.tos">
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
 * 
 *
 **************************************************************************/

    private static Object functionBaseOf(final SObjFunction func,
                                         final SContext     context,
                                         final Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "classObject");
        }

        STosClass tosClass  = STosUtil.getClass(context, args, 1);
        STosClass baseClass = tosClass.getSuperClass();

        return (baseClass==null) ? SObjNull.NULL : baseClass;
    }





//* 
//* <TeaFunction name="class-of"
//*                 arguments="tosObject"
//*             module="tea.tos">
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
 * 
 *
 **************************************************************************/

    private static Object functionClassOf(final SObjFunction func,
                                          final SContext     context,
                                          final Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "tosObject");
        }

        return STosUtil.getTosObj(args,1).getTosClass();
    }





//* 
//* <TeaFunction name="class-is-a"
//*                 arguments="class1 class2"
//*             module="tea.tos">
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
 * 
 *
 **************************************************************************/

    private static Object functionIsA(final SObjFunction func,
                                      final SContext     context,
                                      final Object[]     args)
        throws STeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "class1 class2");
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
//*                 arguments="classObject"
//*             module="tea.tos">
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
 * 
 *
 **************************************************************************/

    private static Object functionGetName(final SObjFunction func,
                                          final SContext     context,
                                          final Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "classObject");
        }

        return STosUtil.getClass(context, args, 1).getName();
    }





//* 
//* <TeaFunction name="tos-obj?"
//*                 arguments="value"
//*             module="tea.tos">
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
 * 
 *
 **************************************************************************/

    private static Object functionIsTosObj(final SObjFunction func,
                                           final SContext     context,
                                           final Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "value");
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

