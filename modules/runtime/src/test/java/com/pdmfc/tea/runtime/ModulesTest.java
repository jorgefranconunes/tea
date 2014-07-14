/**************************************************************************
 *
 * Copyright (c) 2012-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import org.junit.Test;
import static org.junit.Assert.*;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.TeaEnvironment;
import com.pdmfc.tea.runtime.TeaEnvironmentImpl;
import com.pdmfc.tea.runtime.TeaFunctionImplementor;
import com.pdmfc.tea.runtime.TeaModule;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class ModulesTest
    extends Object {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void addModuleWithTeaFunctionAnnotation()
        throws TeaException {

        TeaSymbol     myFunctionName = TeaSymbol.addSymbol("my-function");
        TeaEnvironment environment    = buildTeaEnvironment();
        Modules        modules        = new Modules(environment);
        TeaContext       context        = environment.getGlobalContext();
        TeaModule      myModule       = new MyTestModule();

        modules.add(myModule);

        TeaFunction myFunction = (TeaFunction)context.getVar(myFunctionName);

        assertNotNull(myFunction);

        Object[] myArgs   = { myFunctionName, "Whatever" };
        String   myResult =
            (String)myFunction.exec(myFunction, context, myArgs);

        assertEquals("Whatever - YES", myResult);
    }


    private static final class MyTestModule
        extends MyAbstractModule {


        @TeaFunctionImplementor("my-function")
        public static Object myFunction(final TeaFunction func,
                                        final TeaContext     context,
                                        final Object[]    args)
            throws TeaException {
            String argument = (String)args[1];
            String result   = argument + " - YES";
            return result;
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test(expected=IllegalArgumentException.class)
    public void addModuleWithTeaFunctionAnnotationFailure01()
        throws TeaException {

        TeaSymbol     myFunctionName = TeaSymbol.addSymbol("my-function");
        TeaEnvironment environment    = buildTeaEnvironment();
        Modules        modules        = new Modules(environment);
        TeaModule      myModule       = new MyTestModuleFailure01();

        modules.add(myModule);

        fail("TeaModule.addModule(...) failed to fail!");
    }


    private static final class MyTestModuleFailure01
        extends MyAbstractModule {


        @TeaFunctionImplementor("my-function")
        public static Object myFunction()
            throws TeaException {
            throw new IllegalStateException("Should not be here!");
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test(expected=IllegalArgumentException.class)
    public void addModuleWithTeaFunctionAnnotationFailure02()
        throws TeaException {

        TeaSymbol     myFunctionName = TeaSymbol.addSymbol("my-function");
        TeaEnvironment environment    = buildTeaEnvironment();
        Modules        modules        = new Modules(environment);
        TeaModule      myModule       = new MyTestModuleFailure02();

        modules.add(myModule);

        fail("TeaModule.addModule(...) failed to fail!");
    }


    private static final class MyTestModuleFailure02
        extends MyAbstractModule {


        @TeaFunctionImplementor("my-function")
        public static Object myFunction(final TeaFunction function,
                                        final Object       wrongType,
                                        final Object[]    args)
            throws TeaException {
            throw new IllegalStateException("Should not be here!");
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test(expected=IllegalArgumentException.class)
    public void addModuleWithTeaFunctionAnnotationFailure03()
        throws TeaException {

        TeaSymbol     myFunctionName = TeaSymbol.addSymbol("my-function");
        TeaEnvironment environment    = buildTeaEnvironment();
        Modules        modules        = new Modules(environment);
        TeaModule      myModule       = new MyTestModuleFailure03();

        modules.add(myModule);

        fail("TeaModule.addModule(...) failed to fail!");
    }


    private static final class MyTestModuleFailure03
        extends MyAbstractModule {


        @TeaFunctionImplementor("my-function")
        public static Object myFunction(final TeaFunction function,
                                        final TeaContext     context,
                                        final Object       wrongType)
            throws TeaException {
            throw new IllegalStateException("Should not be here!");
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test(expected=TeaException.class)
    public void addModuleWithTeaFunctionAnnotationTeaException()
        throws TeaException {

        TeaSymbol     myFunctionName = TeaSymbol.addSymbol("my-function");
        TeaEnvironment environment    = buildTeaEnvironment();
        Modules        modules        = new Modules(environment);
        TeaContext       context        = environment.getGlobalContext();
        TeaModule      myModule       = new MyTestModuleTeaException();

        modules.add(myModule);

        TeaFunction myFunction = (TeaFunction)context.getVar(myFunctionName);

        assertNotNull(myFunction);

        Object[] myArgs   = { myFunctionName, "Whatever" };
        String   myResult =
            (String)myFunction.exec(myFunction, context, myArgs);

        fail("Invocation of function should have generated Tea exception");
    }


    private static final class MyTestModuleTeaException
        extends MyAbstractModule {


        @TeaFunctionImplementor("my-function")
        public static Object myFunction(final TeaFunction func,
                                        final TeaContext     context,
                                        final Object[]    args)
            throws TeaException {

            throw new TeaException("Oops...");
        }


    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test(expected=TeaException.class)
    public void addModuleWithTeaFunctionAnnotationJavaException()
        throws TeaException {

        TeaSymbol     myFunctionName = TeaSymbol.addSymbol("my-function");
        TeaEnvironment environment    = buildTeaEnvironment();
        Modules        modules        = new Modules(environment);
        TeaContext       context        = environment.getGlobalContext();
        TeaModule      myModule       = new MyTestModuleJavaException();

        modules.add(myModule);

        TeaFunction myFunction = (TeaFunction)context.getVar(myFunctionName);

        assertNotNull(myFunction);

        Object[] myArgs   = { myFunctionName, "Whatever" };
        String   myResult =
            (String)myFunction.exec(myFunction, context, myArgs);

        fail("Invocation of function should have generated Tea exception");
    }


    private static final class MyTestModuleJavaException
        extends MyAbstractModule {


        @TeaFunctionImplementor("my-function")
        public static Object myFunction(final TeaFunction func,
                                        final TeaContext     context,
                                        final Object[]    args)
            throws TeaException {

            throw new IllegalStateException("Oops...");
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private TeaEnvironment buildTeaEnvironment() {

        TeaEnvironment environment = new TeaEnvironmentImpl(null);

        return environment;
    }





/**************************************************************************
 *
 * An empty implementation of TeaModule, used by other test classes.
 *
 **************************************************************************/

    private static abstract class MyAbstractModule
        extends Object
        implements TeaModule {

        @Override
        public void init(final TeaEnvironment environment) {}

        @Override
        public void end() {}

        @Override
        public void start() {}

        @Override
        public void stop() {
        }


    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

