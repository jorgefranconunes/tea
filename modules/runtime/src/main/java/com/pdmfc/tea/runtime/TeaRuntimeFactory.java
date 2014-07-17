/**************************************************************************
 *
 * Copyright (c) 2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.TeaRuntime;
import com.pdmfc.tea.runtime.TeaRuntimeImpl;
import com.pdmfc.tea.runtime.TeaRuntimeConfig;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class TeaRuntimeFactory
    extends Object {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaRuntimeFactory() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaRuntime newTeaRuntime(final TeaRuntimeConfig config) {

        TeaRuntime teaRuntime = new TeaRuntimeImpl(config);

        return teaRuntime;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

