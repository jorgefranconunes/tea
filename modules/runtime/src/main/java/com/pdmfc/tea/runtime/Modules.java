/**************************************************************************
 *
 * Copyright (c) 2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaPair;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.TeaEnvironment;
import com.pdmfc.tea.runtime.TeaFunctionImplementor;
import com.pdmfc.tea.runtime.TeaModule;





/**************************************************************************
 *
 * Used for managing the lifetime of all Tea modules being used in a
 * Tea runtime.
 *
 **************************************************************************/

final class Modules
    extends Object {





    private TeaEnvironment  _environment    = null;
    private boolean         _isStarted      = false;
    private List<TeaModule> _modules        = new ArrayList<TeaModule>();

    // Modules that were successfully started.
    private Deque<TeaModule> _startedModules = new LinkedList<TeaModule>();





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Modules(final TeaEnvironment environment) {

        _environment = environment;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Modules add(final TeaModule module)
        throws TeaException {

        module.init(_environment);
        buildModuleFunctions(_environment, module);
        _modules.add(module);
        startModule(module);

        return this;
    }





/**************************************************************************
 *
 * Create additional functions for methods annotated with the
 * "TeaFunction" annotation.
 *
 **************************************************************************/

    private static void buildModuleFunctions(final TeaEnvironment environment,
                                             final TeaModule      module) {

        Class<TeaFunctionImplementor> annotationClass =
            TeaFunctionImplementor.class;

        for ( Method method : module.getClass().getMethods() ) {
            TeaFunctionImplementor annotation =
                method.getAnnotation(annotationClass);

            if ( annotation != null ) {
                TeaFunction function     = buildTeaFunction(module, method);
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

    private static TeaFunction buildTeaFunction(final TeaModule module,
                                                 final Method    method) {

        checkMethodSignature(method);

        TeaFunction function = new ModuleMethodTeaFunction(module, method);

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
        } else if ( !TeaFunction.class.isAssignableFrom(argTypes[0]) ) {
            isOk = false;
        } else if ( !TeaContext.class.isAssignableFrom(argTypes[1]) ) {
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

    private void startModule(final TeaModule module) {

        if ( _isStarted ) {
            module.start();
            _startedModules.add(module);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void start() {

        for ( TeaModule module : _modules ) {
            startModule(module);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void stop() {

        for ( Iterator<TeaModule> i=_startedModules.descendingIterator();
              i.hasNext(); ) {
            TeaModule module = i.next();
            module.stop();
        }

        _startedModules.clear();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void end() {

        for ( TeaModule module : _modules ) {
            module.end();
        }

        _modules.clear();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static final class ModuleMethodTeaFunction
        extends Object
        implements TeaFunction {





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
        public Object exec(final TeaFunction function,
                           final TeaContext     context,
                           final Object[]    args)
            throws TeaException {

            Object        result = null;
            TeaException error  = null;

            try {
                result = _method.invoke(_object, function, context, args);
            } catch ( InvocationTargetException e ) {
                Throwable cause = e.getCause();
                if ( cause instanceof TeaException ) {
                    error = (TeaException)cause;
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

