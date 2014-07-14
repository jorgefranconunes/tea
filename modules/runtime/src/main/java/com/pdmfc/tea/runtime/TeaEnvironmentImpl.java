/**************************************************************************
 *
 * Copyright (c) 2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.nio.charset.Charset;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.runtime.Modules;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.TeaEnvironment;





/**************************************************************************
 *
 * The Tea runtime environment accessible by a Tea module.
 *
 **************************************************************************/

final class TeaEnvironmentImpl
    extends Object
    implements TeaEnvironment {





    private TeaContext _globalContext = null;
    private Modules  _modules       = null;
    private Charset  _sourceCharset = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaEnvironmentImpl(final Charset sourceCharset) {

        _globalContext = new TeaContext();
        _modules       = new Modules(this);
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

        TeaSymbol varSymbol = TeaSymbol.addSymbol(varName);

        _globalContext.newVar(varSymbol, varValue);

        return this;
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public TeaContext getGlobalContext() {

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





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public TeaEnvironment addModule(final TeaModule module)
        throws TeaException {

        _modules.add(module);

        return this;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void start() {

        _modules.start();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void stop() {

        _modules.stop();
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void end() {

        _modules.end();
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

