/**************************************************************************
 *
 * Copyright (c) 2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.TeaEnvironment;





/**************************************************************************
 *
 * The Tea runtime environment accessible by a Tea module.
 *
 **************************************************************************/

final class TeaEnvironmentImpl
    extends Object
    implements TeaEnvironment {





    private SContext _globalContext = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaEnvironmentImpl() {

        _globalContext = new SContext();
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public TeaEnvironment addGlobalVar(final String varName,
                                       final Object varValue) {

        SObjSymbol varSymbol = SObjSymbol.addSymbol(varName);

        _globalContext.newVar(varSymbol, varValue);

        return this;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SContext getGlobalContext() {

        return _globalContext;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

