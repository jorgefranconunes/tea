/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.lang;

import com.pdmfc.tea.TeaEnvironment;
import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaModule;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public class TestModule
    extends Object
    implements TeaModule {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TestModule() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Override
    public void init(final TeaEnvironment environment)
        throws TeaException {

        environment.addGlobalVar("test-module-var", "Hello, world!");
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Override
    public void end() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Override
    public void start() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Override
    public void stop() {
          
        // Nothing to do.
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

