/**************************************************************************
 *
 * Copyright (c) 2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.SContext;





/**************************************************************************
 *
 * The Tea runtime environment accessible by a Tea module.
 *
 **************************************************************************/

public interface TeaEnvironment {





/**************************************************************************
 *
 * Creates a new global variable. If a variable with the same name
 * already exists it will be overwtiten.
 *
 * @param varName The name of the global variable to be created or
 * updated.
 *
 * @param varValue The value to assign to the global variable.
 *
 * @return A reference to the same <code>TeaEnvironment</code> that
 * was called.
 *
 **************************************************************************/

    TeaEnvironment addGlobalVar(String varName,
                                Object varValue);





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    SContext getGlobalContext();


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

