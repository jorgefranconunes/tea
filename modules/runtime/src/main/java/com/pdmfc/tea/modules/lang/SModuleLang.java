/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.lang;

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import com.pdmfc.tea.SConfigInfo;
import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.TeaCode;
import com.pdmfc.tea.compiler.TeaCompiler;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.modules.io.SInput;
import com.pdmfc.tea.modules.math.SModuleMath;
import com.pdmfc.tea.modules.util.SHashtable;
import com.pdmfc.tea.runtime.SArgs;
import com.pdmfc.tea.runtime.SBreakException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SContinueException;
import com.pdmfc.tea.runtime.SExitException;
import com.pdmfc.tea.runtime.SLambdaFunction;
import com.pdmfc.tea.runtime.SLambdaFunctionVarArg;
import com.pdmfc.tea.runtime.SModuleUtils;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SObjBlock;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SObjVar;
import com.pdmfc.tea.runtime.SReturnException;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;
import com.pdmfc.tea.runtime.TeaEnvironment;
import com.pdmfc.tea.runtime.TeaFunctionImplementor;





//*
//* <TeaModule name="tea.lang">
//* 
//* <Overview>
//* Tea core functions.
//* </Overview>
//*
//* <Description>
//* Set of functions that makes up the core functionalities of the Tea
//* language.
//* </Description>
//*
//* </TeaModule>
//*





/**************************************************************************
 *
 * Module of core Tea functions.
 *
 **************************************************************************/

public final class SModuleLang
    extends Object
    implements SModule {




    // Used by the implementation of the Tea "system" function.
    private static final int BUFFER_SIZE = 4096;

    private static final String PROP_TEA_VERSION = "com.pdmfc.tea.version";
    private static final String TEA_VERSION_VAR  = "TEA_VERSION";





    private TeaEnvironment _environment = null;

    // These are used by the implementation of the Tea "source"
    // function.
    private TeaCompiler _compiler = new TeaCompiler();

    // These are used by the implementation of the Tea "load-function"
    // function.
    private Map<String,SObjFunction> _funcs =
        new HashMap<String,SObjFunction>();

    // THashtable containing the Java system properties.
    private SHashtable _systemProps = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SModuleLang() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void init(final TeaEnvironment environment)
        throws STeaException {

        environment.addGlobalVar("true",  Boolean.TRUE);
        environment.addGlobalVar("false", Boolean.FALSE);
        environment.addGlobalVar("null",  SObjNull.NULL);
        environment.addGlobalVar(TEA_VERSION_VAR,
                                  SConfigInfo.getProperty(PROP_TEA_VERSION));

        environment.addGlobalVar("import", new SFunctionImport(environment));

        _environment = environment;

        // The other functions provided by this module are implemented
        // as methods of this class with the TeaFunction annotation.
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
//* <TeaFunction name="apply"
//*              arguments="aFunction [arg1 ...] [aList]"
//*              module="tea.lang">
//*
//* <Overview>
//* Invokes a a function with a given list of arguments.
//* </Overview>
//*
//* <Parameter name="aFunction">
//* Function object that will be called. It can also be a symbol bound
//* to a variable containing a function
//* </Parameter>
//*
//* <Parameter name="arg1">
//* Object that will be passed as the first argument whan calling
//* <Arg name="aFunction"/>.
//* </Parameter>
//*
//* <Parameter name="aList">
//* List whose elements will be passed as arguments to
//* <Arg name="aFunction"/>.
//* </Parameter>
//*
//* <Returns>
//* The value returned by the invocation of <Arg name="aFunction"/>.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>apply</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("apply")
    public static Object functionApply(final SObjFunction func,
                                       final SContext     context,
                                       final Object[]     args)
        throws STeaException {

        SArgs.checkAtLeast(args, 2, "function obj1 ... list");

        Object       result      = null;
        SObjFunction proc        = SArgs.getFunction(context, args, 1);
        Object[]     procArgs    = null;
        int          procNumArgs = 0;
        Object       lastArg     = args[args.length-1];
        boolean      hasList     =
            (args.length>2) && (lastArg instanceof SObjPair);
        int          indexOfLast = args.length - (hasList ? 2 : 1);
        int          index;

        if ( hasList ) {
            procNumArgs = args.length - 2 + ((SObjPair)lastArg).length();
        } else {
            procNumArgs = args.length - 1;
        }

        procArgs    = new Object[procNumArgs];
        procArgs[0] = args[1];
      
        for ( index=2; index<=indexOfLast; index++ ) {
            procArgs[index-1] = args[index];
        }
        if ( hasList ) {
            for ( Iterator i=((SObjPair)lastArg).iterator();
                  i.hasNext(); 
                  ++index ) {
                procArgs[index-1] = i.next();
            }
        }

        result = proc.exec(proc, context, procArgs);

        return result;
    }





//* 
//* <TeaFunction name="break"
//*              arguments="[object]"
//*              module="tea.lang">
//*
//* <Overview>
//* Ends the looping of the flux control functions.
//* </Overview>
//*
//* <Parameter name="object">
//* The object to be returned by the flux control function that got
//* interrupted.
//* </Parameter>
//*
//* <Description>
//* Invoking <Func name="break"/> inside the body of a
//* <FuncRef name="while"/> or <FuncRef name="foreach"/> makes
//* those functions return.
//* <P>
//* If the <Func name="break"/> function is called outside the body
//* of a <FuncRef name="while"/> or <FuncRef name="foreach"/> then
//* the currenly executing function terminates and returns the
//* <Arg name="object"/> that was passed to <Func name="break"/> or
//* null if <Func name="break"/> was invoked without arguments.
//* </P>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>break</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("break")
    public static Object functionBreak(final SObjFunction func,
                                       final SContext     context,
                                       final Object[]     args)
        throws STeaException {

        SArgs.checkAtMost(args, 2, "[object]");

        Object breakValue = (args.length==2) ? args[1] : SObjNull.NULL;

        throw new SBreakException(breakValue);
    }





//* 
//* <TeaFunction name="catch"
//*                 arguments="aBlock [aSymbol [aStSymbol]]"
//*             module="tea.lang">
//*
//* <Overview>
//* Executes a code block and checks if any errors were thrown.
//* </Overview>
//*
//* <Parameter name="aBlock">
//* The code block object that will be executed.
//* </Parameter>
//*
//* <Parameter name="aSymbol">
//* Symbol that identifies the variable that will receive either the result
//* of executing the block or the error message.
//* </Parameter>
//* 
//* <Parameter name="aStSymbol">
//* Symbol that identifies the variable that will receive the stack trace as a
//* string, or null if no error.
//* (Since version 3.2.2).
//* </Parameter>
//* 
//* <Returns>
//* True if the execution of the code block generated a runtime error.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* Executes the code block object received as argument <Arg name="aBlock"/>.
//* If the execution of a <Arg name="aBlock"/> generates a runtime error
//* the error is catched and the <Func name="catch"/> function returns true.
//* In this case, if the <Arg name="aSymbol"/> argument is specified, the variable
//* identified by that symbol will be assigned a reference
//* to a string object representing the message associated with the runtime
//* error that was generated. Also in this case, if the
//* <Arg name="aStSymbol"/> argument is specified, the variable
//* identified by that symbol will be assigned a reference
//* to a multi-line string object with the error message and a
//* human readable stack trace representation.
//* <P>
//* If the execution of <Arg name="aBlock"/> concludes with no error then
//* the variable identified by <Arg name="aSymbol"/> will receive the
//* result of the execution of
//* <Arg name="aBlock"/>. In this case the <Func name="catch"/> will return
//* the false boolean value. The result of the execution of a block is
//* the value returned by the last statement executed inside the block.
//* </P>
//* <P>
//* If there is no variable identified by <Arg name="aSymbol"/> 
//* or <Arg name="aStSymbol"/> 
//* accessible in the current context then one an error will be thrown
//* complaining about the unknown variable.
//* </P>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>catch</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("catch")
    public static Object functionCatch(final SObjFunction func,
                                       final SContext     context,
                                       final Object[]     args)
        throws STeaException {

        SArgs.checkBetween(args, 2, 4, "block [symbol] [st-symbol]");

        SObjBlock  block  = SArgs.getBlock(args, 1);
        SObjSymbol symbol = (args.length>=3) ? SArgs.getSymbol(args, 2) : null;
        SObjSymbol symbSt = (args.length==4) ? SArgs.getSymbol(args, 3) : null;
        SContext   scope  = block.getContext().newChild();
        Object     result = Boolean.FALSE;
        Object     value  = null;
        STeaException error  = null;

        try {
            value = block.exec(scope);
        } catch ( SRuntimeException e ) {
            value  = e.getMessage();
            result = Boolean.TRUE;
            error  = e;
        } catch ( SReturnException e ) {
            // This is not an error.
            value = e.getReturnValue();
        } catch ( SBreakException e ) {
            // This is not an error.
            value = e.getBreakValue();
        } catch ( SContinueException e ) {
            // This is not an error.
            value = SObjNull.NULL;
        } catch ( SExitException e ) {
            value = e.getExitValue();
        } catch ( STeaException e ) {
            value  = e.getMessage();
            result = Boolean.TRUE;
            error  = e;
        }

        if ( symbol != null ) {
            SObjVar var = context.getVarObject(symbol);
            var.set((value==null) ? SObjNull.NULL : value);
        }

        if ( symbSt != null ) {
            SObjVar var2 = context.getVarObject(symbSt);

            if ( error != null ) {
                String stackTraceStr = null;

                if ( error instanceof SRuntimeException ) {
                    stackTraceStr = ((SRuntimeException)error).getFullMessage();
                } else {
                     stackTraceStr = stackTraceToString(error);
                }
                var2.set(stackTraceStr);
            } else {
                var2.set(SObjNull.NULL);
            }
        }        

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static String stackTraceToString(final Throwable error) {

        StringWriter swriter = new StringWriter();
        PrintWriter  pwriter = new PrintWriter(swriter);
        
        error.printStackTrace(pwriter);
        pwriter.close();

        String result = swriter.toString();

        return result;
    }





//* 
//* <TeaFunction
//*     name="cond"
//*     arguments="condition1 anObject1 [condition2 anObject2 ...] [elseObject]"
//*     module="tea.lang">
//*
//* <Overview>
//* Returns one of several possible values, depending on the boolean value
//* of several conditions.
//* </Overview>
//*
//* <Parameter name="condition1">
//* A boolean value or a block whose execution results in a boolean value.
//* </Parameter>
//*
//* <Parameter name="anObject1">
//* An object of any type, whose evaluation is returned if
//* <Arg name="condition1"/> evaluates to the true boolean value.
//* </Parameter>
//*
//* <Parameter name="elseObject">
//* An object of any type, whose evaluation is returned if none of the
//* conditions evaluated to true.
//* </Parameter>
//*
//* <Returns>
//* The result of evaluating the first of the object arguments whose condition
//* evaluated to true.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>cond</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("cond")
    public static Object functionCond(final SObjFunction func,
                                      final SContext     context,
                                      final Object[]     args)
        throws STeaException {

        SArgs.checkAtLeast(args, 3, "condition result [...]");

        Object  result        = SObjNull.NULL;
        int     numArgs       = args.length;
        boolean hasElseClause = (numArgs%2)==0;
        int     numCondArgs   = hasElseClause ? numArgs-1 : numArgs;
        boolean isMatched     = false;

        for ( int i=1; (i<numCondArgs) && !isMatched; i+=2 ) {
            Object condition = evalCondArg(args[i]);

            if ( !(condition instanceof Boolean) ) {
                throw new STypeException(args, i, "boolean or a block");
            }

            if ( ((Boolean)condition).booleanValue() ) {
                result    = evalCondArg(args[i+1]);
                isMatched = true;
            }
        }

        if ( !isMatched ) {
            if ( hasElseClause ) {
                result = evalCondArg(args[numCondArgs]);
            }
        }

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object evalCondArg(final Object condArg)
        throws STeaException {

        Object result = condArg;

        if ( condArg instanceof SObjBlock ) {
            result = ((SObjBlock)condArg).exec();
        }

        return result;
    }





//* 
//* <TeaFunction name="continue"
//*              module="tea.lang">
//*
//* <Overview>
//* Ends the current execution of the body of the flux control functions
//* and continues with the next iteration.
//* </Overview>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>continue</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("continue")
    public static Object functionContinue(final SObjFunction func,
                                          final SContext     context,
                                          final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 1, "No args required");

        throw new SContinueException();
    }





//* 
//* <TeaFunction name="define"
//*              arguments="varName"
//*              module="tea.lang">
//* 
//* <Prototype arguments="varName value"/>
//* 
//* <Prototype arguments="functionName formalArgs body"/>
//*
//* <Overview>
//* Creates a variable in the current context and initializes it with
//* the given value or a newly created function.
//* </Overview>
//*
//* <Parameter name="varName">
//* Symbol identifying the variable to be created in the current context.
//* </Parameter>
//* 
//* <Parameter name="value">
//* The object for which the variable will hold a reference. This argument
//* is optional. If not specified the variable will be assigned the null
//* value.
//* </Parameter>
//*
//* <Parameter name="functionName">
//* Symbol identifying the function to be created in the current context.
//* In other words, the name of the variable to be created in the current
//* context and that will contain a new function object.
//* </Parameter>
//* 
//* <Parameter name="formalArgs">
//* List of symbols representing the function formal arguments, or
//* a symbol representing the list of formal arguments.
//* </Parameter>
//* 
//* <Parameter name="body">
//* Code block object that will be used as the new function body.
//* </Parameter>
//*
//* <Returns>
//* The contents of the newly created variable.
//* </Returns>
//*
//* <Description>
//* Example of defining a numeric variable:
//* <Code>
//*     define pi 3.141516
//* </Code>
//* Example of defining a function that calculates and returns the square
//* value of its argument:
//* <Code>
//*     define square ( x ) { * $x $x }
//* </Code>
//* Example of defining a function which accepts a variable number
//* of arguments:
//* <Code>
//*     define lineEcho argList {
//*         foreach arg $argList {
//*             echo $arg
//*         }
//*     }
//* </Code>
//* Defining the same variable more than once in the same context is
//* legal. Subsequent definitions only change its value, just like if
//* it was done through the function <FuncRef name="set!"/>.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>define</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("define")
    public static Object functionDefine(final SObjFunction func,
                                        final SContext     context,
                                        final Object[]     args)
        throws STeaException {

        Object result = functionDefineVar(context, func, context, args);

        return result;
    }





//* 
//* <TeaFunction name="global"
//*              arguments="varName value"
//*              module="tea.lang">
//*
//* <Prototype arguments="functionName formalArgs body"/>
//*
//* <Overview>
//* Creates a variable in the outermost context.
//* </Overview>
//*
//* <Parameter name="varName">
//* Symbol identifying the variable to be created in the outermost context.
//* </Parameter>
//* 
//* <Parameter name="value">
//* The object for wich the variable will hold a reference.
//* </Parameter>
//*
//* <Parameter name="formalArgs">
//* List of symbols representing the function formal arguments, or
//* a symbol representing the list of formal arguments.
//* </Parameter>
//*
//* <Parameter name="body">
//* Code block object that will be used as the new function body.
//* </Parameter>
//*
//* <Returns>
//* The contents of the variable, that is, a reference to
//* <Arg name="value"/> object.
//* </Returns>
//*
//* <Description>
//* Equivalent to the function <FuncRef name="define"/> but
//* at the outermost context.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>global</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("global")
    public Object functionGlobal(final SObjFunction func,
                                 final SContext     context,
                                 final Object[]     args)
        throws STeaException {

        SContext globalContext = _environment.getGlobalContext();
        Object   result        =
            functionDefineVar(globalContext, func, context, args);

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionDefineVar(final SContext     varContext,
                                            final SObjFunction func,
                                            final SContext     context,
                                            final Object[]     args)
        throws STeaException {

        SArgs.checkBetween(args, 2, 4, "var-name [value]");

        SObjSymbol symbol = SArgs.getSymbol(args, 1);
        Object     result  = null;

        switch ( args.length ) {
        case 2 :
            result = SObjNull.NULL;
            break;
        case 3 :
            result = args[2];
            break;
         case 4 :
             result = newFunction(args);
             break;
        default :
            // Will never happen.
            throw new IllegalStateException("Oops...");
        }

        varContext.newVar(symbol, result);

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static SObjFunction newFunction(final Object[] args)
        throws STeaException {

        Object       formalParam = args[2];
        SObjBlock    body        = SArgs.getBlock(args,3);
        SObjFunction result      = null;

        if ( formalParam instanceof SObjPair ) {
            result = newFixedArgsFunction(args, (SObjPair)formalParam, body);
        } else if ( formalParam instanceof SObjSymbol ) {
            result = newVarArgsFunction((SObjSymbol)formalParam, body);
        } else {
            throw new STypeException(args, 2, "symbol or a symbol list");
        }

        return result;
    }





/**************************************************************************
 *
 * Creates a procedure with a fixed number of parameters.
 *
 * @param args The arguments for the function where this method is
 * invoked.
 *
 * @param paramList List of symbols representing the procedure formal
 * arguments.
 *
 * @param body Block that will be the body of the procedure.
 *
 * @exception com.pdmfc.tea.STeaException Thrown if any of the
 * elements in the formal parameter list is not a symbol.
 *
 **************************************************************************/

    private static SObjFunction newFixedArgsFunction(final Object[]  args,
                                                     final SObjPair  paramList,
                                                     final SObjBlock body)
        throws STeaException {

        int          numParam   = paramList.length();
        SObjSymbol[] parameters = new SObjSymbol[numParam];
        Iterator     it         = paramList.iterator();

        for ( int i=0; it.hasNext(); i++) {
            Object paramName = it.next();

            try {
                parameters[i] = (SObjSymbol)paramName;
            } catch ( ClassCastException e1 ) {
                String msg = "formal parameter {0} must be a symbol, not a {1}";
                throw new SRuntimeException(args,
                                            msg,
                                            i,
                                            STypes.getTypeName(paramName));
            }
        }

        SObjFunction result = new SLambdaFunction(parameters, body);

        return result;
    }





/**************************************************************************
 *
 * Creates a procedure that accpets a variable number of arguments.
 *
 * @param symbol The name of the variable inside the procedure body
 * that is a list containing all the arguments.
 *
 * @param body Block that will be the body of the procedure.
 *
 * @exception com.pdmfc.tea.STeaException
 *
 **************************************************************************/

    private static SObjFunction newVarArgsFunction(final SObjSymbol symbol,
                                                   final SObjBlock  body)
        throws STeaException {

        SObjFunction result = new SLambdaFunctionVarArg(symbol, body);

        return result;
    }





//* 
//* <TeaFunction name="echo"
//*              arguments="[arg1 ...]"
//*              module="tea.lang">
//* 
//* <Overview>
//* Sends a string to the process standard output stream
//* followed by an "end of line" sequence.
//* </Overview>
//* 
//* <Parameter name="arg1">
//* The object to be written. It may be either a string, integer, 
//* float or boolean object.
//* </Parameter>
//*
//* <Returns>
//* The number of arguments that were received.
//* </Returns>
//* 
//* <Description>
//* Sends to the process standard output stream the string obtained
//* by concatenating the arguments that were received. If an argument
//* is a numeric object then the string containing its decimal
//* representation is used. 
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>echo</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("echo")
    public  static Object functionEcho(final SObjFunction func,
                                       final SContext     context,
                                       final Object[]     args)
        throws STeaException {

        int argCount = args.length;

        for ( int i=1; i<argCount; i++ ) {
            Object arg = args[i];

            if ( arg instanceof String ) {
                System.out.print(arg);
            } else if ( arg instanceof Integer ) {
                System.out.print(arg);
            } else if ( arg instanceof Long ) {
                System.out.print(arg);
            } else if ( arg instanceof Double ) {
                System.out.print(arg);
            } else if ( arg instanceof Boolean ) {
                System.out.print(((Boolean)arg).booleanValue() ? "1" : "0");
            } else {
                String msg = "could not print argument {0} is of type {1}";
                throw new STypeException(msg, i, STypes.getTypeName(arg));
            }
        }
        System.out.println();

        Integer result = Integer.valueOf(argCount-1);

        return result;
    }





//* 
//* <TeaFunction name="error"
//*              arguments="message"
//*              module="tea.lang">
//*
//* <Overview>
//* Generates a run-time error.
//* </Overview>
//*
//* <Parameter name="message">
//* String containing the message that will be associated with the error.
//* </Parameter>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>error</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("error")
    public static Object functionError(final SObjFunction func,
                                       final SContext     context,
                                       final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "message");

        String msg = SArgs.getString(args, 1);

        throw new SRuntimeException(msg);
    }





//* 
//* <TeaFunction name="exec"
//*              arguments="block"
//*              module="tea.lang">
//*
//* <Overview>
//* Executes the Tea code represented by a code block object.
//* </Overview>
//*
//* <Parameter name="block">
//* The code block object that will be executed.
//* </Parameter>
//* 
//* <Returns>
//* The result of the block evalution. That will be the value
//* returned by the last function that gets invoked inside the
//* code block.
//* </Returns>
//*
//* <Description>
//* The code block is executed in a new context that is a child of
//* the context where the code block was defined.
//* <P>
//* The <Func name="exec"/> function could be defined like this:
//* <pre>
//* define exec ( block ) {
//*     [lambda () $block]
//* }
//* </pre>
//* </P>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>exec</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("exec")
    public static Object functionExec(final SObjFunction func,
                                      final SContext     context,
                                      final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 2, "block");

        SObjBlock block  = SArgs.getBlock(args, 1);
        Object    result = block.exec();

        return result;
    }





//* 
//* <TeaFunction name="exit"
//*              arguments="status"
//*              module="tea.lang">
//*
//* <Overview>
//* Terminates the execution of the current process.
//* </Overview>
//*
//* <Parameter name="status">
//* The integer value that will be passed as exit status.
//* </Parameter>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>exit</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("exit")
    public static Object functionExit(final SObjFunction func,
                                      final SContext     context,
                                      final Object[]     args)
        throws STeaException {

        SArgs.checkAtMost(args, 2, "[exit-code]");

        Integer retVal =
            (args.length==1) ? SModuleMath.ZERO : SArgs.getInt(args,1);
        
        throw new SExitException(retVal);
    }





//* 
//* <TeaFunction name="foreach"
//*                 arguments="varName aList aBlock"
//*             module="tea.lang">
//*
//* <Overview>
//* Executes a block of code for each of the elements in a list.
//* </Overview>
//*
//* <Parameter name="varName">
//* Symbol identifying a variable inside the code block
//* <Arg name="aBlock"/> that that will take the value of each of the elements
//* in the list.
//* </Parameter>
//*
//* <Parameter name="aList">
//* A list of objects of any type.
//* </Parameter>
//* 
//* <Parameter name="aBlock">
//* Block of code that will be executed for every element in
//* <Arg name="aList"/>.
//* </Parameter>
//*
//* <Returns>
//* The result of the last execution of <Arg name="aBlock"/>
//* or the null object if <Arg name="aBlock"/> never is executed.
//* </Returns>
//*
//* <Description>
//* The <Func name="foreach"/> function executes <Arg name="aBlock"/>
//* as many times as the number of elements in <Arg name="aList"/>.
//* Every time <Arg name="aBlock"/> gets executed a variable
//* identified by the symbol <Arg name="varName"/> is initialized
//* inside <Arg name="aBlock"/> with a reference to one of the
//* elements in <Arg name="aList"/>, in the
//* same order they appear in <Arg name="aList"/>.
//* 
//* <P>If you ignore the return value you could define the
//* <Func name="foreach"/> function like this:</P>
//*         <Code>
//*         define foreach ( varName aList aBlock ) {
//*             map [lambda ( $varName ) $aBlock] $aList
//*         }
//*         </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>foreach</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("foreach")
    public static Object functionForeach(final SObjFunction func,
                                         final SContext     context,
                                         final Object[]     args)
        throws STeaException {

        SArgs.checkCount(args, 4, "var list block");

        SObjSymbol symbol     = SArgs.getSymbol(args, 1);
        SObjPair   list       = SArgs.getPair(args, 2);
        SObjBlock  block      = SArgs.getBlock(args, 3);
        Object     result     = SObjNull.NULL;

        if ( !list.isEmpty() ) {
            SContext   newContext = block.getContext().newChild();
            SObjVar    var        = newContext.newVar(symbol, SObjNull.NULL);

            for ( Object element : list ) {
                var.set(element);
                try {
                    result = block.exec(newContext);
                } catch ( SBreakException e1 ) {
                    result  = e1.getBreakValue();
                    break;
                } catch ( SContinueException e2 ) {
                    // Just continue for the next element.
                }
            }
        }

        return result;
    }





//* 
//* <TeaFunction name="get"
//*                 arguments="varName"
//*             module="tea.lang">
//*
//* <Overview>
//* Fetches the contents of a variable.
//* </Overview>
//*
//* <Parameter name="varName">
//* Symbol identifying the variable whose variable is to be fecthed.
//* </Parameter>
//*
//* <Returns>
//* The contents of the variable.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>get</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("get")
    public static Object functionGet(final SObjFunction func,
                                     final SContext     context,
                                     final Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "symbol");
        }

        return context.getVar(SArgs.getSymbol(args,1));
    }





//* 
//* <TeaFunction name="if"
//*                  arguments="condition trueResult [falseResult]"
//*              module="tea.lang">
//*
//* <Overview>
//* Returns one of two possible values, depending on the boolean value
//* of the first argument.
//* </Overview>
//*
//* <Parameter name="condition">
//* A boolean value or a block whose execution results in a boolean value.
//* </Parameter>
//*
//* <Parameter name="trueResult">
//* An object of any type.
//* </Parameter>
//*
//* <Parameter name="falseResult">
//* An object of any type.
//* </Parameter>
//*
//* <Returns>
//* The evaluation of <Arg name="trueResult"/> if <Arg name="condition"/>
//* evaluates to true, otherwise the evaluation of <Arg name="falseResult"/>
//* if it exists, or the null object if it is not specified.
//* </Returns>
//*
//* <Description>
//* The three argument version of the <Func name="if"/> function could be
//* defined in terms of the <Func name="cond"/> function like this:
//*         <Code>
//*         define if ( aCondition truObj falseObj ) {
//*             cond $aCondition $trueObj $falseObj
//*         }
//*         </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>if</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("if")
    public static Object functionIf(final SObjFunction func,
                                    final SContext     context,
                                    final Object[]     args)
        throws STeaException {

        int numArgs = args.length;

        if ( (numArgs<3) || (numArgs>4) ) {
            throw new SNumArgException(args, "condition yesBlock noBlock");
        }

        Object condition = args[1];
        Object yesResult = args[2];
        Object noResult  = (numArgs==4) ? args[3] : SObjNull.NULL;
        Object result    = SObjNull.NULL;
        
        if ( condition instanceof SObjBlock ) {
            condition = ((SObjBlock)condition).exec();
        }
      
        if ( !(condition instanceof Boolean) ) {
            String expectedTypes = "boolean or a block returning a boolean";
            throw new STypeException(args, 1, expectedTypes);
        }
        result = ((Boolean)condition).booleanValue() ? yesResult : noResult;

        if ( result instanceof SObjBlock ) {
            result = ((SObjBlock)result).exec();
        }

        return result;
    }





//* 
//* <TeaFunction name="is"
//*                 arguments="value"
//*             module="tea.lang">
//*
//* <Overview>
//* Identity function.
//* </Overview>
//*
//* <Parameter name="value">
//* An object of any type.
//* </Parameter>
//* 
//* <Returns>
//* The <Arg name="value"/> argument.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>is</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("is")
    public static Object functionIs(final SObjFunction func,
                                    final SContext     context,
                                    final Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "object");
        }

        return args[1];
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object typeCheck(final Class<?> type,
                                    final Object[] args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "obj");
        }

        return
            type.isAssignableFrom(args[1].getClass())
            ? Boolean.TRUE
            : Boolean.FALSE;
    }





//* 
//* <TeaFunction name="block?"
//*                 arguments="value"
//*             module="tea.lang">
//*
//* <Overview>
//* Checks if a given object is a code block.
//* </Overview>
//*
//* <Parameter name="value">
//* An object of any type.
//* </Parameter>
//* 
//* <Returns>
//* True if the argument <Arg name="value"/> is a code block.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>block?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("block?")
    public static Object functionIsBlock(final SObjFunction func,
                                         final SContext     context,
                                         final Object[]     args)
        throws STeaException {

        return typeCheck(SObjBlock.class, args);
    }





//* 
//* <TeaFunction name="bool?"
//*                 arguments="value"
//*             module="tea.lang">
//*
//* <Overview>
//* Checks if a given object is a boolean value.
//* </Overview>
//*
//* <Parameter name="value">
//* An object of any type.
//* </Parameter>
//* 
//* <Returns>
//* True if the argument <Arg name="value"/> is a boolean value.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>bool?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("bool?")
    public static Object functionIsBoolean(final SObjFunction func,
                                           final SContext     context,
                                           final Object[]     args)
        throws STeaException {

        return typeCheck(Boolean.class, args);
    }





//* 
//* <TeaFunction name="float?"
//*                 arguments="value"
//*             module="tea.lang">
//*
//* <Overview>
//* Checks if a given object is a float object.
//* </Overview>
//*
//* <Parameter name="value">
//* An object of any type.
//* </Parameter>
//* 
//* <Returns>
//* True if the argument <Arg name="value"/> is a float object.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>float?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("float?")
    public static Object functionIsFloat(final SObjFunction func,
                                         final SContext     context,
                                         final Object[]     args)
        throws STeaException {

        return typeCheck(Double.class, args);
    }




//* 
//* <TeaFunction name="function?"
//*                 arguments="value"
//*             module="tea.lang">
//*
//* <Overview>
//* Checks if a given object is a function object.
//* </Overview>
//*
//* <Parameter name="value">
//* An object of any type.
//* </Parameter>
//* 
//* <Returns>
//* True if the argument <Arg name="value"/> is a function object.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>function?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("function?")
    public static Object functionIsFunction(final SObjFunction func,
                                            final SContext     context,
                                            final Object[]     args)
        throws STeaException {

        return typeCheck(SObjFunction.class, args);
    }





//* 
//* <TeaFunction name="int?"
//*                 arguments="value"
//*             module="tea.lang">
//*
//* <Overview>
//* Checks if a given object is an integer  object.
//* </Overview>
//*
//* <Parameter name="value">
//* An object of any type.
//* </Parameter>
//* 
//* <Returns>
//* True if the argument <Arg name="value"/> is an integer object.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>int?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("int?")
    public static Object functionIsInt(final SObjFunction func,
                                       final SContext     context,
                                       final Object[]     args)
        throws STeaException {
        
        return typeCheck(Integer.class, args);
    }





//* 
//* <TeaFunction name="pair?"
//*                 arguments="value"
//*             module="tea.lang">
//*
//* <Overview>
//* Checks if a given object is a pair  object.
//* </Overview>
//*
//* <Parameter name="value">
//* An object of any type.
//* </Parameter>
//* 
//* <Returns>
//* True if the argument <Arg name="value"/> is a pair object.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>pair?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("pair?")
    public static Object functionIsPair(final SObjFunction func,
                                        final SContext     context,
                                        final Object[]     args)
        throws STeaException {
        
        return typeCheck(SObjPair.class, args);
    }





//* 
//* <TeaFunction name="string?"
//*                 arguments="value"
//*             module="tea.lang">
//*
//* <Overview>
//* Checks if a given object is a string object.
//* </Overview>
//*
//* <Parameter name="value">
//* An object of any type.
//* </Parameter>
//* 
//* <Returns>
//* True if the argument <Arg name="value"/> is a string object.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>string?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("string?")
    public static Object functionIsString(final SObjFunction func,
                                          final SContext     context,
                                          final Object[]     args)
        throws STeaException {
        
        return typeCheck(String.class, args);
    }





//* 
//* <TeaFunction name="symbol?"
//*                 arguments="value"
//*             module="tea.lang">
//*
//* <Overview>
//* Checks if a given object is a symbol object.
//* </Overview>
//*
//* <Parameter name="value">
//* An object of any type.
//* </Parameter>
//* 
//* <Returns>
//* True if the argument <Arg name="value"/> is a symbol object.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>symbol?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("symbol?")
    public static Object functionIsSymbol(final SObjFunction func,
                                          final SContext     context,
                                          final Object[]     args)
        throws STeaException {
        
        return typeCheck(SObjSymbol.class, args);
    }





//* 
//* <TeaFunction name="not-null?"
//*                 arguments="value"
//*             module="tea.lang">
//*
//* <Overview>
//* Checks if a given object is not the null object.
//* </Overview>
//*
//* <Parameter name="value">
//* An object of any type.
//* </Parameter>
//* 
//* <Returns>
//* True if the argument <Arg name="value"/> is not the null object.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>not-null?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("not-null?")
    public static Object functionIsNotNull(final SObjFunction func,
                                           final SContext     context,
                                           final Object[]     args)
        throws STeaException {
        
        if ( args.length != 2 ) {
            throw new SNumArgException(args, "object");
        }

        return (args[1]==SObjNull.NULL) ? Boolean.FALSE : Boolean.TRUE;
    }





//* 
//* <TeaFunction name="not-same?"
//*                 arguments="object1 object2"
//*             module="tea.lang">
//*
//* <Overview>
//* Checks if two objects are not the same object.
//* </Overview>
//*
//* <Parameter name="object1">
//* An object of any type.
//* </Parameter>
//*
//* <Parameter name="object2">
//* An object of any type.
//* </Parameter>
//* 
//* <Returns>
//* True if the arguments <Arg name="object1"/> and
//* <Arg name="object2"/> are not the same object.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>not-same?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("not-same?")
    public static Object functionIsNotSame(final SObjFunction func,
                                           final SContext     context,
                                           final Object[]     args)
        throws STeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "obj1 obj2");
        }

        return args[1].equals(args[2]) ? Boolean.FALSE : Boolean.TRUE;
    }





//* 
//* <TeaFunction name="null?"
//*                 arguments="value"
//*             module="tea.lang">
//*
//* <Overview>
//* Checks if a given object is the null object.
//* </Overview>
//*
//* <Parameter name="value">
//* An object of any type.
//* </Parameter>
//* 
//* <Returns>
//* True if the argument <Arg name="value"/> is the null object.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>null?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("null?")
    public static Object functionIsNull(final SObjFunction func,
                                        final SContext context,
                                        final Object[]   args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "object");
        }

        return (args[1]==SObjNull.NULL) ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaFunction name="same?"
//*                 arguments="object1 object2"
//*             module="tea.lang">
//*
//* <Overview>
//* Checks if two objects are the same object.
//* </Overview>
//*
//* <Parameter name="object1">
//* An object of any type.
//* </Parameter>
//*
//* <Parameter name="object2">
//* An object of any type.
//* </Parameter>
//* 
//* <Returns>
//* True if the arguments <Arg name="object1"/> and
//* <Arg name="object2"/> are the same object.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>same?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("same?")
    public static Object functionIsSame(final SObjFunction func,
                                        final SContext     context,
                                        final Object[]     args)
        throws STeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "obj1 obj2");
        }

        return args[1].equals(args[2]) ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaFunction name="lambda"
//*                 arguments="argList body"
//*             module="tea.lang">
//*
//* <Overview>
//* Creates a new function.
//* </Overview>
//*
//* <Parameter name="argList">
//* List of symbols representing the formal arguments of the new function,
//* or a symbol representing the list of formal arguments.
//* </Parameter>
//*
//* <Parameter name="body">
//* A code block that will be the code associated with the function.
//* </Parameter>
//*
//* <Returns>
//* The newly created function objects.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>lambda</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("lambda")
    public static Object functionLambda(final SObjFunction func,
                                        final SContext     context,
                                        final Object[]     args)
        throws STeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "formal-parameters block");
        }

        Object       formalParam  = args[1];
        SObjBlock    body         = SArgs.getBlock(args, 2);
        SObjFunction result       = null;

        if ( formalParam instanceof SObjPair ) {
            result = newFixedArgsFunction(args, (SObjPair)formalParam, body);
        } else if ( formalParam instanceof SObjSymbol ) {
            result = newVarArgsFunction((SObjSymbol)formalParam, body);
        } else {
            throw new STypeException(args, 1, "symbol or a symbol list");
        }

        return result;
    }





//* 
//* <TeaFunction name="load"
//*                 arguments="javaClassName"
//*             module="tea.lang">
//*
//* <Overview>
//* Dynamically loads new Tea functions and objects from a Java class.
//* </Overview>
//*
//* <Parameter name="javaClassName">
//* String identifying a fully qualified Java class name.
//* </Parameter>
//*
//* <Description>
//* The class <Arg name="javaClassName"/> must be derived from
//* 
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>load</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("load")
    public Object functionLoad(final SObjFunction func,
                               final SContext     context,
                               final Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "java-package-name");
        }

        String moduleClassName = SArgs.getString(args, 1);
        
        try {
            SModuleUtils.addAndStartModule(_environment, moduleClassName);
        } catch ( STeaException e ) {
            throw new SRuntimeException(args, e.getMessage());
        }
        
        return SObjNull.NULL;
    }





//* 
//* <TeaFunction name="load-function"
//*                 arguments="javaClassName"
//*             module="tea.lang">
//*
//* <Overview>
//* Dynamically loads new Tea function from a Java class.
//* </Overview>
//*
//* <Parameter name="javaClassName">
//* String identifying a Java class name.
//* </Parameter>
//*
//* <Returns>
//* A function object.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>load-function</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("load-function")
    public Object functionLoadFunction(final SObjFunction func,
                                       final SContext     context,
                                       final Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "java-class-name");
        }

        String       className = SArgs.getString(args,1);
        Class        javaClass = null;
        SObjFunction teaFunc   = _funcs.get(className);
        String       msg       = null;

        if ( teaFunc == null ) {
            try {
                javaClass = Class.forName(className);
                teaFunc = (SObjFunction)javaClass.newInstance();
            } catch ( ClassNotFoundException e1 ) {
                msg = "could not find class '" + className + "'";
            } catch ( InstantiationException e2 ) {
                msg = "failed instantiation for object of class '" + className + "'";
            } catch ( IllegalAccessException e3 ) {
                msg = "class '" + className+"' or its initializer are not accessible";
            } catch ( ClassCastException e4 ) {
                msg = "class '" + className + "' is not a SObjFunction";
            } catch ( NoSuchMethodError e5 ) {
                msg = "class '" + className + "' does not have a default constructor";
            }
            if ( msg != null ) {
                throw new SRuntimeException(msg);
            }
            _funcs.put(className, teaFunc);
        }

        return teaFunc;
    }





//* 
//* <TeaFunction name="map"
//*                 arguments="aFunction argList1 [argList2 ...]"
//*             module="tea.lang">
//*
//* <Overview>
//* Invokes a a function repeatedly for a given set of arguments.
//* </Overview>
//*
//* <Parameter name="aFunction">
//* Function object that will be called.
//* </Parameter>
//*
//* <Parameter name="argList1">
//* List of objects that will be passed as the first argument for
//* each invocation of <Arg name="aFunction"/>.
//* </Parameter>
//*
//* <Parameter name="argList2">
//* List of objects that will be passed as the second argument for
//* each invocation of <Arg name="aFunction"/>.
//* </Parameter>
//*
//* <Returns>
//* A list with the return values of each invocation of
//* <Arg name="aFunction"/>.
//* </Returns>
//*
//* <Description>
//* All the lists <Arg name="argList1"/>, <Arg name="argList2"/>, ...,
//* must have the same number of elements. If that is not the case
//* then a runtime error will occur.
//* <P>
//* The function <Func name="aFunction"/> will be called as many times
//* as the number of elements in one the lists containing the arguments.
//* </P>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>map</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("map")
    public static Object functionMap(final SObjFunction func,
                                     final SContext     context,
                                     final Object[]     args)
        throws STeaException {

        if ( args.length < 3 ) {
            throw new SNumArgException(args, "procedure list1 ...");
        }
        
        SObjFunction proc      = SArgs.getFunction(context, args, 1);
        Iterator[]   iterators = buildListOfI(args);
        Object[]     procArgs  = new Object[args.length-1];
        SObjPair     resHead   = SObjPair.emptyList();
        SObjPair     resElem   = null;
        
        procArgs[0] = args[1];

        while ( iterators[0].hasNext() ) {
            for ( int i=0; i<iterators.length; i++ ) {
                try {
                    procArgs[i+1] = iterators[i].next();
                } catch ( NoSuchElementException e ) {
                    String msg = "lists with diferent sizes";
                    throw new SRuntimeException(args, msg);
                }
            }
            SObjPair node = new SObjPair(proc.exec(proc, context, procArgs),
                                         SObjPair.emptyList());
            if ( resElem == null ) {
                resHead = node;
            } else {
                resElem.setCdr(node);
            }
            resElem = node;
        }

        return resHead;
    }





/**************************************************************************
 *
 * Creates an array of <TT>Enumerator</TT>, each one associated with a
 * list of the arguments.
 *
 * @param args
 *    The arguments received by the command.
 *
 * @return
 *    An array of <TT>Enumerator</TT> objects, each one associated with a
 *    list.
 *
 * @exception STeaException
 *    Thrown if any of the arguments was not a list.
 *
 **************************************************************************/

    private static Iterator[] buildListOfI(final Object[] args)
        throws STeaException {

        Iterator[] iterators = new Iterator[args.length-2];

        for ( int i=0; i<iterators.length; i++ ) {
            iterators[i] = SArgs.getPair(args, i+2).iterator();
        }

        return iterators;
    }



//* 
//* <TeaFunction name="map-apply"
//*                 arguments="aFunction aList"
//*             module="tea.lang">
//*
//* <Overview>
//* Repeatedly executes a function.
//* </Overview>
//*
//* <Parameter name="aFunction">
//* A function object.
//* </Parameter>
//*
//* <Parameter name="aList">
//* A list where each element is also a list. The elements in these list
//* will be passed as arguments to <Arg name="aFunction"/> each time
//* it is called.
//* </Parameter>
//*
//* <Returns>
//* A list containing the successive return values of
//* <Arg name="aFunction"/>.
//* </Returns>
//*
//* <Description>
//* Invokes <Arg name="aFunction"/> as many times as the number of elements
//* in <Arg name="aList"/>. The elements of <Arg name="aList"/> are lists.
//* Each time <Arg name="aFunction"/> is called it will be passed as arguments
//* the elements of one of the lists contained in <Arg name="aList"/>.
//* <P>
//* The <Func name="map-apply"/> function could be defined as follows:
//* </P>
//* <Code>
//* define map-apply ( aFunction aList ) {
//*     map [lambda (x) { apply $aFunction $x }] $aList
//* }
//* </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>map-apply</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("map-apply")
    public static Object functionMapApply(final SObjFunction func,
                                          final SContext     context,
                                          final Object[]     args)
        throws STeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "procedure list-of-arg-list");
        }

        SObjFunction proc       = SArgs.getFunction(context, args, 1);
        Iterator     iterator   = SArgs.getPair(args, 2).iterator();
        int          iterCount  = -1;
        int          numArgs    = 0;
        Object[]     funcArgs   = null;
        SObjPair     emptyList  = SObjPair.emptyList();
        SObjPair     resultHead = emptyList;
        SObjPair     resultTail = null;
        SObjPair     node       = null;

        if ( iterator.hasNext() ) {
            iterCount++;
            Object o = iterator.next();
            SObjPair argList = null;
            try {
                argList = (SObjPair)o;
            } catch ( ClassCastException e ) {
                String msg = "arg 2, element {0} must be a list, not a {1}";
                throw new SRuntimeException(args,
                                            msg,
                                            iterCount,
                                            STypes.getTypeName(o));
            }
            numArgs = argList.length() + 1;
            funcArgs = new Object[numArgs];
            funcArgs[0] = proc;
            fillArgs(funcArgs, argList);
            Object funcResult = proc.exec(proc, context, funcArgs);
            resultHead = new SObjPair(funcResult, emptyList);
            resultTail = resultHead;
        }

        while ( iterator.hasNext() ) {
            iterCount++;
            Object o = iterator.next();
            SObjPair argList = null;
            try {
                argList = (SObjPair)o;
            } catch ( ClassCastException e ) {
                String msg = "arg 2, element {0} must be a list, not a {1}";
                throw new SRuntimeException(args,
                                            msg,
                                            iterCount,
                                            STypes.getTypeName(o));
            }
            fillArgs(funcArgs, argList);
            Object   funcResult = proc.exec(proc, context, funcArgs);
            node = new SObjPair(funcResult ,emptyList);
            resultTail.setCdr(node);
            resultTail = node;
        }

        return resultHead;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static void fillArgs(final Object[] args,
                                 final SObjPair argList)
        throws SRuntimeException {

        int      argCount = args.length;
        Iterator iterator = argList.iterator();

        for ( int i=1; i<argCount; i++ ) {
            try {
                args[i] = iterator.next();
            } catch ( NoSuchElementException e ) {
                throw new SRuntimeException("argument list too short");
            }
        }
    }





//* 
//* <TeaFunction name="return"
//*                 arguments="[anObject]"
//*             module="tea.lang">
//*
//* <Overview>
//* Ends the execution of the body of a function and exits from the
//* function.
//* </Overview>
//*
//* <Parameter name="anObject">
//* The object to be returned by the function.
//* </Parameter>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>return</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("return")
    public static Object functionReturn(final SObjFunction func,
                                        final SContext     context,
                                        final Object[]     args)
        throws STeaException {

        throw new SReturnException((args.length>1) ? args[1] : SObjNull.NULL);
    }





//* 
//* <TeaFunction name="set!"
//*                 arguments="varName value"
//*             module="tea.lang">
//*
//* <Overview>
//* Modifies the contents of a variable.
//* </Overview>
//*
//* <Parameter name="varName">
//* Symbol identifying the variable to be modified.
//* </Parameter>
//* 
//* <Parameter name="value">
//* The object for wich the variable will hold a reference.
//* </Parameter>
//*
//* <Returns>
//* The news contents of the variable, that is, a reference to
//* <Arg name="value"/> object.
//* </Returns>
//*
//* <Description>
//* Changes the contents of a variable accessible in the currente
//* context. A reference to the object passed as the <Arg name="value"/>
//* is stored in the variable identified by the symbol
//* <Arg name="varName"/>. The function <Func name="set!"/> does not
//* create new variables. The variable must have already been created.
//* If that is not the case a runtime exception is thrown. New variables
//* can be created using the <Func name="define"/> function.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>set!</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("set!")
    public static Object functionSet(final SObjFunction func,
                                     final SContext     context,
                                     final Object[]     args)
        throws STeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "symbol value");
        }

        SObjSymbol varName = SArgs.getSymbol(args, 1);
        Object     value   = args[2];

        context.setVar(varName, value);

        return value;
    }





//* 
//* <TeaFunction name="sleep"
//*                 arguments="duration"
//*             module="tea.lang">
//*
//* <Overview>
//* Suspends the execution of the current thread for a given period.
//* </Overview>
//*
//* <Parameter name="duration">
//* Integer representing the number of milliseconds the current thread
//* will be suspended.
//* </Parameter>
//*
//* <Returns>
//* True if the thread was suspended for all of the given period.
//* False if the suspension was interrupted.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>sleep</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("sleep")
    public static Object functionSleep(final SObjFunction func,
                                       final SContext     context,
                                       final Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "millis-to-sleep");
        }

        Boolean result      = Boolean.TRUE;
        int     timeToSleep = SArgs.getInt(args,1).intValue();

        try {
            Thread.sleep(timeToSleep);
        } catch ( InterruptedException e ) {
            result = Boolean.FALSE;
        }

        return result;
    }





//* 
//* <TeaFunction name="source"
//*                 arguments="fileName"
//*             module="tea.lang">
//* 
//* <Prototype arguments="inputStream"/>
//*
//* <Overview>
//* Executes Tea code read from a file or from an input stream.
//* </Overview>
//*
//* <Parameter name="fileName">
//* String representing the pathname of the file to execute.
//* </Parameter>
//*
//* <Parameter name="inputStream">
//* A <ClassRef name="TInput"/> from where the Tea code will be
//* read.
//* </Parameter>
//* 
//* <Returns>
//* The result of the last statement executed.
//* </Returns>
//*
//* <Description>
//* The Tea code contained in <Arg name="fileName"/> is always
//* executed.
//* This is unlike the <Func name="import"/> function, where
//* the file only gets executed the first time.
//* The <Arg name="fileName"/> argument is supposed to be the
//* path name of a file containing Tea code. The path name can
//* be either absolute or relative to the current working directory
//* of the process.
//* <P>
//* The <Func name="source"/> function can also read Tea code
//* from a <ClassRef name="TInput"/>. In this case the
//* <Arg name="inputStream"/> is read until an end-of-file
//* condition is reached. It is the caller responsability to close
//* the <Arg name="inputStream"/>
//* </P>
//* <P>
//* The code is executed in a context descending from
//* the global context. That means variables defined locally in the code
//* (using the <FuncRef name="define"/> function) will only be known
//* inside that code.
//* </P>
//* <P>
//* If the file or the input stream can not be read a runtime error
//* will occur.
//* </P>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 *Implements the Tea <code>source</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("source")
    public Object functionSource(final SObjFunction func,
                                 final SContext     context,
                                 final Object[]     args)
        throws STeaException {

        TeaCode  program       = compileFromSource(context, args);
        SContext globalContext = _environment.getGlobalContext();
        SContext runContext    = globalContext.newChild();
        Object   result        = program.exec(runContext);

        return result;
    }





//* 
//* <TeaFunction name="compile"
//*                 arguments="fileName"
//*             module="tea.lang">
//* 
//* <Prototype arguments="inputStream"/>
//*
//* <Overview>
//* Creates a code block object from Tea code read from a file or from
//* an input stream.
//* </Overview>
//*
//* <Parameter name="fileName">
//* String representing the path name of the file to execute. The path
//* name can be either absolute or relative to the current working
//* directory of the process.
//* </Parameter>
//*
//* <Parameter name="inputStream">
//* A <ClassRef name="TInput"/> from where the Tea code will be
//* read.
//* </Parameter>
//* 
//* <Returns>
//* A code block object.
//* </Returns>
//*
//* <Description>
//* <p>
//* The code block context is a direct descendent of the global
//* context. That means variables defined locally in the code
//* (using the <FuncRef name="define"/> function) will only be known
//* inside that code.
//* </p>
//* <p>
//* The <Func name="compile"/> function can also read Tea code
//* from a <ClassRef name="TInput"/>. In this case the
//* <Arg name="inputStream"/> is read until an end-of-file
//* condition is reached. It is the caller responsability to close
//* the <Arg name="inputStream"/>
//* </p>
//* <p>
//* If the file or the input stream can not be read or if there are
//* syntax errors in the code then a runtime error will occur.
//* </p>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>compile</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("compile")
    public Object functionCompile(final SObjFunction func,
                                  final SContext     context,
                                  final Object[]     args)
        throws STeaException {

        final TeaCode   program      = compileFromSource(context, args);
        final SContext  blockContext = _environment.getGlobalContext();
        SObjBlock       result       =
            new SObjBlock() {
                public SContext getContext() {
                    return blockContext;
                }
                public Object exec(final SContext context)
                    throws STeaException {
                    return program.exec(context);
                }
                public Object exec()
                    throws STeaException {
                    return program.exec(blockContext.newChild());
                }
            };

        return result;
    }





/**************************************************************************
 *
 *
 *
 **************************************************************************/

    private TeaCode compileFromSource(final SContext context,
                                      final Object[] args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "file");
        }
        
        Object  arg     = args[1];
        Charset charset = _environment.getSourceCharset();
        TeaCode program = null;

        if ( arg instanceof String ) {
            String fileName = (String)arg;
            
            try {
                program  = _compiler.compile(fileName, charset, fileName);
            } catch ( IOException e ) {
                String   msg     = "Failed to read \"{0}\" - {1}";
                Object[] fmtArgs = { fileName, e.getMessage() };
                throw new SRuntimeException(msg, fmtArgs);
            }
        } else if ( arg instanceof SInput ) {
            Reader input = ((SInput)arg).getReader();
            if ( input == null ) {
                throw new SRuntimeException("input stream is closed");
            }

            try {
                program = _compiler.compile(input, null);
            } catch ( IOException e ) {
                String   msg     = "Failed to read input stream - {0}";
                Object[] fmtArgs = { e.getMessage() };
                throw new SRuntimeException(msg, fmtArgs);
            } finally {
                try { ((SInput)arg).close(); } catch ( IOException e ) {/* */}
            }
        } else {
            String msg = "argument 1 must be string or input stream, not {0}";
            Object[] fmtArgs = { STypes.getTypeName(arg) };
            throw new STypeException(msg, fmtArgs);
        }

        return program;
    }





//* 
//* <TeaFunction name="system"
//*             arguments="command [arg1 ...]"
//*                 module="tea.lang">
//* 
//* <Overview>
//* Runs a native platform process.
//* </Overview>
//* 
//* <Parameter name="command">
//* String representing the name of the operating system dependent
//* command that will be executed.
//* </Parameter>
//*
//* <Parameter name="arg1">
//* String that will be used as argument for the command.
//* </Parameter>
//*
//* <Returns>
//* The exit status of the operating system process.
//* </Returns>
//* 
//* <Description>
//* The child process standard output is redirected to the current
//* process standard output.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>system</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("system")
    public static Object functionSystem(final SObjFunction func,
                                        final SContext     context,
                                        final Object[]     args)
        throws STeaException {

        if ( args.length < 2 ) {
            throw new SNumArgException(args, "command [arg ...]");
        }

        String[] cmdArgs = new String[args.length-1];

        for ( int i=1; i<args.length; i++ ) {
            cmdArgs[i-1] = SArgs.getString(args, i);
        }
        
        Process     proc   = null;
        InputStream input  = null;  // Linked to the child process stdout.
        byte[]      buffer = new byte[BUFFER_SIZE];
        int         count;
        int         status = 0;

        try {
            proc = Runtime.getRuntime().exec(cmdArgs);
        } catch ( IOException e ) {
            throw new SRuntimeException(e);
        }
        input = proc.getInputStream();

        try {
            while ( (count=input.read(buffer)) > 0 ) {
                System.out.write(buffer, 0, count);
            }
        } catch ( IOException e ) {
            throw new SRuntimeException(e);
        }

        try {
            status = proc.waitFor();
        } catch ( InterruptedException e ) {
            throw new SRuntimeException(e);
        }

        return Integer.valueOf(status);
    }





//* 
//* <TeaFunction name="time"
//*                 arguments="aBlock count"
//*             module="tea.lang">
//*
//* <Overview>
//* Times the execution of a block.
//* </Overview>
//*
//* <Parameter name="aBlock">
//* The code block that will be executed <Arg name="count"/> times.
//* </Parameter>
//*
//* <Parameter name="count">
//* Integer representing the number of times that <Arg name="aBlock"/>
//* will be executed.
//* </Parameter>
//*
//* <Returns>
//* The number of milliseconds that the execution of <Arg name="aBlock"/>
//* <Arg name="count"/> times took.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>time</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("time")
    public static Object functionTime(final SObjFunction func,
                                      final SContext     context,
                                      final Object[]     args)
        throws STeaException {

        if ( (args.length<2) || (args.length>3) ) {
            throw new SNumArgException(args, "block [count]");
        }

        SObjBlock block        = SArgs.getBlock(args, 1);
        SContext  childContext = block.getContext().newChild();
        int       count        =
            (args.length==2) ? 1 : SArgs.getInt(args, 2).intValue();
        long      startTime;
        long      endTime;

        startTime = System.currentTimeMillis();
        for ( int i=0; i<count; i++ ) {
            block.exec(childContext);
        }
        endTime = System.currentTimeMillis();

        return Integer.valueOf((int)(endTime-startTime));
    }





//* 
//* <TeaFunction name="while"
//*                 arguments="condition body"
//*             module="tea.lang">
//*
//* <Overview>
//* Executes a block of code repeatedly while a condition evaluates
//* to the true boolean value.
//* </Overview>
//*
//* <Parameter name="condition">
//* A boolean value or a block whose execution results in a boolean value.
//* </Parameter>
//*
//* <Parameter name="body">
//* The block of code that will be repeatedly executed while
//* <Arg name="condition"/> evaluates to true.
//* </Parameter>
//*
//* <Returns>
//* The result of the last execution of the <Arg name="body"/> block
//* or the null object if the <Arg name="body"/> was never executed.
//* </Returns>
//*
//* <Description>
//* Just for the sake of ilustration, you could define the
//* <Func name="while"/> function like this:
//*         <Code>
//*         define while ( condBlock bodyBlock ) {
//*             if $condBlock {
//*                 [lambda () $bodyBlock]
//*                 while $condBlock $bodyBlock
//*         }
//*         }
//*         </Code>
//* Mind you this would not be particularly efficient, since tail
//* recursion is not implemented in the current version of the Tea
//* interpreter.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>while</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("while")
    public static Object functionWhile(final SObjFunction func,
                                       final SContext     context,
                                       final Object[]     args)
        throws STeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "condition body-block");
        }

        Object condition = args[1];

        if ( condition instanceof Boolean ) {
            if ( ((Boolean)condition).booleanValue() ) {
                return infiniteLoop(args);
            } else {
                return SObjNull.NULL;
            }
        }

        return normalLoop(args);
    }





/**************************************************************************
 *
 * Performes a normal loop. The first argument to the command is
 * supposed to be a block returning a boolean.
 *
 **************************************************************************/

    private static Object normalLoop(final Object[] args)
        throws STeaException {

        SObjBlock condBlock   = SArgs.getBlock(args, 1);
        SObjBlock block       = SArgs.getBlock(args, 2);
        Object    result      = SObjNull.NULL;
        SContext  condContext = condBlock.getContext().newChild();
        SContext  bodyContext = block.getContext().newChild();

        while ( true ) {
            Object condition = condBlock.exec(condContext);
            boolean condValue;

            try {
                condValue = ((Boolean)condition).booleanValue();
            } catch ( ClassCastException e ) {
                throw new STypeException(args, 1, "block returning a bool");
            }
            if ( condValue ) {
                try {
                    result = block.exec(bodyContext);
                } catch ( SContinueException e1 ) {
                    // Continue from the beggining of the block.
                } catch ( SBreakException e2 ) {
                    // Stop looping.
                    result  = e2.getBreakValue();
                    break;
                }
            } else {
                break;
            }
        }

        return result;
    }





/**************************************************************************
 *
 * Performs an infinite loop. The only way to break out of the loop is
 * because of an error or a break statement.
 *
 **************************************************************************/

    private static Object infiniteLoop(final Object[] args)
        throws STeaException {

        SObjBlock block      = SArgs.getBlock(args, 2);
        SContext  newContext = block.getContext().newChild();
        Object    result     = SObjNull.NULL;

        while ( true ) {
            try {
                result = block.exec(newContext);
            } catch ( SContinueException e1 ) {
                // Continue from the beggining of the block.
            } catch ( SBreakException e2 ) {
                // Stop looping.
                result  = e2.getBreakValue();
                break;
            }
        }

        return result;
    }





//* 
//* <TeaFunction name="tea-get-system-property"
//*                  arguments="propertyName"
//*              module="tea.lang">
//*
//* <Overview>
//* Fetches the value of a Java system property.
//* </Overview>
//*
//* <Parameter name="propertyName">
//* A string representing the name of the property to retrieve.
//* </Parameter>
//*
//* <Returns>
//* A string representing the value of the given Java system
//* property or null if the property is not defined.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* <Since version="3.1.5"/>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>tea-get-system-property</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("tea-get-system-property")
    public static Object functionGetProp(final SObjFunction func,
                                         final SContext     context,
                                         final Object[]     args)
        throws STeaException {

        if ( args.length != 2 ) {
            throw new SNumArgException(args, "propertyName");
        }

        String key = SArgs.getString(args, 1);

        if ( key.length() == 0 ) {
            throw new SRuntimeException(args, "Empty property name");
        }

        String value = null;

        try {
            value = System.getProperty(key);
        } catch ( RuntimeException e ) {
            String   msg = "Failed to get system property \"{0}\" - {1} - {2}";
            Object[] fmtArgs = { key, e.getClass().getName(), e.getMessage() };
            throw new SRuntimeException(args, msg, fmtArgs);
        }

        return (value==null) ? SObjNull.NULL : value;
    }





//* 
//* <TeaFunction name="tea-set-system-property"
//*                  arguments="propertyName propertyValue"
//*              module="tea.lang">
//*
//* <Overview>
//* Sets the value of a Java system property.
//* </Overview>
//*
//* <Parameter name="propertyName">
//* A string representing the name of the property to set. It can not
//* be an empty string.
//* </Parameter>
//*
//* <Parameter name="propertyValue">
//* The value of the property being set. This value must be a string.
//* </Parameter>
//*
//* <Returns>
//* A string corresponding to the previous value of the Java system
//* property or null if the property was not defined.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* <Since version="3.1.12"/>
//*
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>tea-set-system-property</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("tea-set-system-property")
    public static Object functionSetProp(final SObjFunction func,
                                         final SContext     context,
                                         final Object[]     args)
        throws STeaException {

        if ( args.length != 3 ) {
            throw new SNumArgException(args, "propertyName propertyValue");
        }

        String key   = SArgs.getString(args, 1);
        String value = SArgs.getString(args, 2);

        if ( key.length() == 0 ) {
            throw new SRuntimeException(args, "Empty property name");
        }

        String prevValue = null;

        try {
            prevValue = System.setProperty(key, value);
        } catch ( RuntimeException e ) {
            String   msg = "Failed to set system property \"{0}\" - {1} - {2}";
            Object[] fmtArgs = { key, e.getClass().getName(), e.getMessage() };
            throw new SRuntimeException(args, msg, fmtArgs);
        }

        return (prevValue==null) ? SObjNull.NULL : prevValue;
    }





//* 
//* <TeaFunction name="tea-get-system-properties"
//*              module="tea.lang">
//*
//* <Overview>
//* Fetches an hashtable containing the Java system properties.
//* </Overview>
//*
//* <Returns>
//* A <ClassRef name="THashtable"/> object containing the Java
//* system properties.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* <Since version="3.1.5"/>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>tea-get-system-properties</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception STeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("tea-get-system-properties")
    public Object functionGetProps(final SObjFunction func,
                                   final SContext     context,
                                   final Object[]     args)
        throws STeaException {

        if ( _systemProps == null ) {
            Properties props = System.getProperties();
            _systemProps = SHashtable.newInstance(context, props);
        }

        return _systemProps;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

