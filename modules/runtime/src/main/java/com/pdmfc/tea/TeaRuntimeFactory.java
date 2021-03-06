/**************************************************************************
 *
 * Copyright (c) 2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import com.pdmfc.tea.TeaRuntime;
import com.pdmfc.tea.TeaRuntimeConfig;
import com.pdmfc.tea.runtime.TeaRuntimeImpl;





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

