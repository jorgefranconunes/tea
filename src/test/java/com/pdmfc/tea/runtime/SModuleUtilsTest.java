/**************************************************************************
 *
 * Copyright (c) 2012 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import org.junit.Test;
import static org.junit.Assert.*;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.TeaFunction;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class SModuleUtilsTest
    extends Object {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void addModuleWithTeaFunctionAnnotation()
        throws STeaException {

        SObjSymbol myFunctionName = SObjSymbol.addSymbol("my-function");
        SContext   toplevel       = new SContext();
        SModule    myModule       = new MyTestModule();

        SModuleUtils.addModule(toplevel, myModule);

        SObjFunction myFunction = (SObjFunction)toplevel.getVar(myFunctionName);

        assertNotNull(myFunction);

        Object[] myArgs   = { myFunctionName, "Whatever" };
        String   myResult =
            (String)myFunction.exec(myFunction, toplevel, myArgs);

        assertEquals("Whatever - YES", myResult);
    }


    private static final class MyTestModule
        extends MyAbstractModule {


        @TeaFunction("my-function")
        public static Object myFunction(final SObjFunction func,
                                        final SContext     context,
                                        final Object[]     args)
            throws STeaException {
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
        throws STeaException {

        SObjSymbol myFunctionName = SObjSymbol.addSymbol("my-function");
        SContext   toplevel       = new SContext();
        SModule    myModule       = new MyTestModuleFailure01();

        SModuleUtils.addModule(toplevel, myModule);

        fail("SModule.addModule(...) failed to fail!");
    }


    private static final class MyTestModuleFailure01
        extends MyAbstractModule {


        @TeaFunction("my-function")
        public static Object myFunction()
            throws STeaException {
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
        throws STeaException {

        SObjSymbol myFunctionName = SObjSymbol.addSymbol("my-function");
        SContext   toplevel       = new SContext();
        SModule    myModule       = new MyTestModuleFailure02();

        SModuleUtils.addModule(toplevel, myModule);

        fail("SModule.addModule(...) failed to fail!");
    }


    private static final class MyTestModuleFailure02
        extends MyAbstractModule {


        @TeaFunction("my-function")
        public static Object myFunction(final SObjFunction function,
                                        final Object       wrongType,
                                        final Object[]     args)
            throws STeaException {
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
        throws STeaException {

        SObjSymbol myFunctionName = SObjSymbol.addSymbol("my-function");
        SContext   toplevel       = new SContext();
        SModule    myModule       = new MyTestModuleFailure03();

        SModuleUtils.addModule(toplevel, myModule);

        fail("SModule.addModule(...) failed to fail!");
    }


    private static final class MyTestModuleFailure03
        extends MyAbstractModule {


        @TeaFunction("my-function")
        public static Object myFunction(final SObjFunction function,
                                        final SContext     context,
                                        final Object       wrongType)
            throws STeaException {
            throw new IllegalStateException("Should not be here!");
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test(expected=STeaException.class)
    public void addModuleWithTeaFunctionAnnotationTeaException()
        throws STeaException {

        SObjSymbol myFunctionName = SObjSymbol.addSymbol("my-function");
        SContext   toplevel       = new SContext();
        SModule    myModule       = new MyTestModuleTeaException();

        SModuleUtils.addModule(toplevel, myModule);

        SObjFunction myFunction = (SObjFunction)toplevel.getVar(myFunctionName);

        assertNotNull(myFunction);

        Object[] myArgs   = { myFunctionName, "Whatever" };
        String   myResult =
            (String)myFunction.exec(myFunction, toplevel, myArgs);

        fail("Invocation of function should have generated Tea exception");
    }


    private static final class MyTestModuleTeaException
        extends MyAbstractModule {


        @TeaFunction("my-function")
        public static Object myFunction(final SObjFunction func,
                                        final SContext     context,
                                        final Object[]     args)
            throws STeaException {

            throw new STeaException("Oops...");
        }


    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test(expected=STeaException.class)
    public void addModuleWithTeaFunctionAnnotationJavaException()
        throws STeaException {

        SObjSymbol myFunctionName = SObjSymbol.addSymbol("my-function");
        SContext   toplevel       = new SContext();
        SModule    myModule       = new MyTestModuleJavaException();

        SModuleUtils.addModule(toplevel, myModule);

        SObjFunction myFunction = (SObjFunction)toplevel.getVar(myFunctionName);

        assertNotNull(myFunction);

        Object[] myArgs   = { myFunctionName, "Whatever" };
        String   myResult =
            (String)myFunction.exec(myFunction, toplevel, myArgs);

        fail("Invocation of function should have generated Tea exception");
    }


    private static final class MyTestModuleJavaException
        extends MyAbstractModule {


        @TeaFunction("my-function")
        public static Object myFunction(final SObjFunction func,
                                        final SContext     context,
                                        final Object[]     args)
            throws STeaException {

            throw new IllegalStateException("Oops...");
        }
    }





/**************************************************************************
 *
 * An empty implementation of SModule, used by other test classes.
 *
 **************************************************************************/

    private static abstract class MyAbstractModule
        extends Object
        implements SModule {

        @Override
        public void init(SContext context) {}

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

