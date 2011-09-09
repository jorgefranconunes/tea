/**************************************************************************
 *
 * Copyright (c) 2010-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;





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

    public static SModule addModule(final SContext context,
                                    final String   className)
        throws STeaException {

        SModule module = null;

        try {
            module = (SModule)Class.forName(className).newInstance();
        } catch (Throwable e) {
            String msg = "Failed to create instance for module {0} - {1} - {2}";
            Object[] fmtArgs =
                { className, e.getClass().getName(), e.getMessage() };
            throw new SRuntimeException(msg, fmtArgs);
        }

        addModule(context, module);

        return module;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static SModule addModule(final SContext context,
                                    final SModule  module)
        throws STeaException {

        module.init(context);

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

    public static void addAndStartModule(final SContext context,
                                         final String   className)
        throws STeaException {

        SModule module = addModule(context, className);

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

    public static void addAndStartModule(final SContext context,
                                         final SModule  module)
        throws STeaException {

        addModule(context, module);

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

    public static void startModules(final SContext context)
        throws STeaException {

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

        Object car = modulesHead._car;

        if ( car == null ) {
            // We reached the end of the list.
            return;
        }

        if ( !(car instanceof SModule) ) {
            String msg = "Element {0} of {1} should be an SModule, not a {2}";
            Object[] fmtArgs =
                { String.valueOf(index), VAR_MODULES, STypes.getTypeName(car) };
            throw new SRuntimeException(msg, fmtArgs);
        }

        startModules(index+1, context, modulesHead.nextPair());

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

    public static void stopModules(final SContext context)
        throws STeaException {

        SObjPair modulesHead = getHead(context, SYMBOL_MODULES_STARTED);

        stopModules(0, context, modulesHead);

        context.newVar(SYMBOL_MODULES_STARTED, SObjPair.emptyList());
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static void stopModules(final int      index,
                                    final SContext context,
                                    final SObjPair modulesHead)
        throws STeaException {

        Object car = modulesHead._car;

        if ( car == null ) {
            // We reached the end of the list.
            return;
        }

        if ( !(car instanceof SModule) ) {
            String msg = "Element {0} of {1} should be an SModule, not a {2}";
            throw new SRuntimeException(msg,
                                        String.valueOf(index),
                                        VAR_MODULES_STARTED,
                                        STypes.getTypeName(car));
        }

        SModule module = (SModule)car;

        module.stop();

        stopModules(index+1, context, modulesHead.nextPair());
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static void endModules(final SContext context)
        throws STeaException {

        SObjPair modulesHead = getHead(context, SYMBOL_MODULES);

        endModules(0, context, modulesHead);

        context.newVar(SYMBOL_MODULES, SObjPair.emptyList());
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static void endModules(final int      index,
                                   final SContext context,
                                   final SObjPair modulesHead)
        throws STeaException {

        Object car = modulesHead._car;

        if ( car == null ) {
            // We reached the end of the list.
            return;
        }

        if ( !(car instanceof SModule) ) {
            String msg = "Element {0} of {1} should be an SModule, not a {2}";
            Object[] fmtArgs =
                { String.valueOf(index), VAR_MODULES, STypes.getTypeName(car) };
            throw new SRuntimeException(msg, fmtArgs);
        }

        endModules(index+1, context, modulesHead.nextPair());

        SModule module = (SModule)car;

        module.end();
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

