/**************************************************************************
 *
 * Copyright (c) 2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.nio.charset.Charset;

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
    private Charset  _sourceCharset = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaEnvironmentImpl(final Charset sourceCharset) {

        _globalContext = new SContext();
        _sourceCharset = sourceCharset;

        if ( _sourceCharset == null ) {
            _sourceCharset = Charset.defaultCharset();
        }
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
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public SContext getGlobalContext() {

        return _globalContext;
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public Charset getSourceCharset() {

        return _sourceCharset;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

