/**************************************************************************
 *
 * Copyright (c) 2010-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.TeaEnvironment;
import com.pdmfc.tea.runtime.TeaFunction;





/**************************************************************************
 *
 * Utility functions for managing the Tea modules in a Tea
 * interpreter.
 *
 **************************************************************************/

public final class SModuleUtils
    extends Object {





    // Name of Tea variable that will store a list of the SModule
    // instances registered with calls to addModule(...). The module
    // objects are ordered in the reverse order they were registered.
    private static final String VAR_MODULES = "TEA_MODULES";

    private static final SObjSymbol SYMBOL_MODULES =
        SObjSymbol.addSymbol(VAR_MODULES);

    private static final String VAR_MODULES_STARTED = "TEA_MODULES_STARTED";

    private static final SObjSymbol SYMBOL_MODULES_STARTED =
        SObjSymbol.addSymbol(VAR_MODULES_STARTED);





/**************************************************************************
 *
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private SModuleUtils() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static SModule addModule(final TeaEnvironment environment,
                                    final String         className)
        throws STeaException {

        SModule module = null;

        try {
            module = (SModule)Class.forName(className).newInstance();
        } catch ( Throwable e ) {
            String msg = "Failed to create instance for module {0} - {1} - {2}";
            Object[] fmtArgs =
                { className, e.getClass().getName(), e.getMessage() };
            throw new SRuntimeException(msg, fmtArgs);
        }

        addModule(environment, module);

        return module;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static SModule addModule(final TeaEnvironment environment,
                                    final SModule        module)
        throws STeaException {

        initializeModule(environment, module);

        SContext context = environment.getGlobalContext();
        SObjPair head    = getHead(context, SYMBOL_MODULES);
        SObjPair newHead =
            new SObjPair(module, (head!=null) ? head : SObjPair.emptyList());

        context.newVar(SYMBOL_MODULES, newHead);

        return module;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static void initializeModule(final TeaEnvironment environment,
                                         final SModule        module)
        throws STeaException {

        module.init(environment);

        // Create additional functions for methods annotated with the
        // "TeaFunction" annotation.

        Class<TeaFunction> annotationClass = TeaFunction.class;

        for ( Method method : module.getClass().getMethods() ) {
            TeaFunction annotation = method.getAnnotation(annotationClass);

            if ( annotation != null ) {
                SObjFunction function     = buildTeaFunction(module, method);
                String       functionName = annotation.value();

                environment.addGlobalVar(functionName, function);
            }
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static SObjFunction buildTeaFunction(final SModule module,
                                                 final Method  method) {

        checkMethodSignature(method);

        SObjFunction function = new ModuleMethodTeaFunction(module, method);

        return function;
    }





/**************************************************************************
 *
 * Checks if the given method as the right signature for a method that
 * can be used as the implementation of a Tea function.
 *
 * If the method does not have the appropriate signature then an
 * IllegalArgumentException will be thrown.
 *
 **************************************************************************/

    private static void checkMethodSignature(final Method method) {

        boolean    isOk     = true;
        Class<?>[] argTypes = method.getParameterTypes();

        if ( argTypes.length != 3 ) {
            isOk = false;
        } else if ( !SObjFunction.class.isAssignableFrom(argTypes[0]) ) {
            isOk = false;
        } else if ( !SContext.class.isAssignableFrom(argTypes[1]) ) {
            isOk = false;
        } else if ( !argTypes[2].isArray() ) {
            isOk = false;
        }

        if ( !isOk ) {
            String msgFmt = "Method {0}.{1} has invalid signature";
            String msg    =
                MessageFormat.format(msgFmt,
                                     method.getDeclaringClass().getName(),
                                     method.getName());

            throw new IllegalArgumentException(msg);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static void addAndStartModule(final TeaEnvironment environment,
                                         final String         className)
        throws STeaException {

        SModule module = addModule(environment, className);

        module.start();

        SContext context = environment.getGlobalContext();
        SObjPair head    = getHead(context, SYMBOL_MODULES_STARTED);
        SObjPair newHead = new SObjPair(module, head);

        context.newVar(SYMBOL_MODULES_STARTED, newHead);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static void addAndStartModule(final TeaEnvironment environment,
                                         final SModule        module)
        throws STeaException {

        addModule(environment, module);

        module.start();

        SContext context = environment.getGlobalContext();
        SObjPair head    = getHead(context, SYMBOL_MODULES_STARTED);
        SObjPair newHead = new SObjPair(module, head);

        context.newVar(SYMBOL_MODULES_STARTED, newHead);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static SObjPair getHead(final SContext   context,
                                    final SObjSymbol var)
        throws STeaException {

        SObjPair result = null;

        if ( context.isDefined(var) ) {
            Object   value  = context.getVar(var);
            
            if ( !(value instanceof SObjPair) ) {
                String   msg = "Var \"{0}\" should contain a pair, not a {1}";
                Object[] fmtArgs = { var, STypes.getTypeName(value) };
                throw new SRuntimeException(msg, fmtArgs);
            }

            result = (SObjPair)value;
        } else {
            result = SObjPair.emptyList();
        }

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static void startModules(final TeaEnvironment environment)
        throws STeaException {

        SContext context     = environment.getGlobalContext();
        SObjPair modulesHead = getHead(context, SYMBOL_MODULES);

        startModules(0, context, modulesHead);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static void startModules(final int      index,
                                     final SContext context,
                                     final SObjPair modulesHead)
        throws STeaException {

        if ( modulesHead.isEmpty() )  {
            // We reached the end of the list.
            return;
        }

        Object car = modulesHead.car();

        if ( !(car instanceof SModule) ) {
            String msg = "Element {0} of {1} should be an SModule, not a {2}";
            Object[] fmtArgs =
                { String.valueOf(index), VAR_MODULES, STypes.getTypeName(car) };
            throw new SRuntimeException(msg, fmtArgs);
        }

        startModules(index+1, context, modulesHead.cdr());

        SModule module = (SModule)car;

        module.start();

        SObjPair head    = getHead(context, SYMBOL_MODULES_STARTED);
        SObjPair newHead = new SObjPair(module, head);

        context.newVar(SYMBOL_MODULES_STARTED, newHead);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static void stopModules(final TeaEnvironment environment)
        throws STeaException {

        SContext context     = environment.getGlobalContext();
        SObjPair modulesHead = getHead(context, SYMBOL_MODULES_STARTED);

        stopModules(0, modulesHead);

        context.newVar(SYMBOL_MODULES_STARTED, SObjPair.emptyList());
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static void stopModules(final int      index,
                                    final SObjPair modulesHead)
        throws STeaException {

        if ( modulesHead.isEmpty() ) {
            // We reached the end of the list.
            return;
        }

        Object car = modulesHead.car();

        if ( !(car instanceof SModule) ) {
            String msg = "Element {0} of {1} should be an SModule, not a {2}";
            throw new SRuntimeException(msg,
                                        String.valueOf(index),
                                        VAR_MODULES_STARTED,
                                        STypes.getTypeName(car));
        }

        SModule module = (SModule)car;

        module.stop();

        stopModules(index+1, context, modulesHead.cdr());
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static void endModules(final TeaEnvironment environment)
        throws STeaException {

        SContext context     = environment.getGlobalContext();
        SObjPair modulesHead = getHead(context, SYMBOL_MODULES);

        endModules(0, modulesHead);

        context.newVar(SYMBOL_MODULES, SObjPair.emptyList());
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static void endModules(final int      index,
                                   final SObjPair modulesHead)
        throws STeaException {

        if ( modulesHead.isEmpty() ) {
            // We reached the end of the list.
            return;
        }

        Object car = modulesHead.car();

        if ( !(car instanceof SModule) ) {
            String msg = "Element {0} of {1} should be an SModule, not a {2}";
            Object[] fmtArgs =
                { String.valueOf(index), VAR_MODULES, STypes.getTypeName(car) };
            throw new SRuntimeException(msg, fmtArgs);
        }

        endModules(index+1, context, modulesHead.cdr());

        SModule module = (SModule)car;

        module.end();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static final class ModuleMethodTeaFunction
        extends Object
        implements SObjFunction {





        private Object _object = null;
        private Method _method = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public ModuleMethodTeaFunction(final Object object,
                                       final Method method) {

            _object = object;
            _method = method;
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        @Override
        public Object exec(final SObjFunction function,
                           final SContext     context,
                           final Object[]     args)
            throws STeaException {

            Object        result = null;
            STeaException error  = null;

            try {
                result = _method.invoke(_object, function, context, args);
            } catch ( InvocationTargetException e ) {
                Throwable cause = e.getCause();
                if ( cause instanceof STeaException ) {
                    error = (STeaException)cause;
                } else {
                    error = new SRuntimeException(cause);
                }
            } catch ( IllegalAccessException e ) {
                error = new SRuntimeException(e);
            }

            if ( error != null ) {
                throw error;
            }

            return result;
        }

 
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

