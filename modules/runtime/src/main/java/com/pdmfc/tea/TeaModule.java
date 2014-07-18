/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaEnvironment;





/**************************************************************************
 *
 * Defines the lifecycle methods that a Tea module must implement. 
 *
 **************************************************************************/

public interface TeaModule {





/**************************************************************************
 *
 * Populates the <code>context</code> with commands and variables.
 *
 * @param environment The Tea environment the module may interact
 * with.
 *
 * @throws TeaException If there were any problems initializing the
 * module.
 *
 **************************************************************************/

   void init(TeaEnvironment environment)
       throws TeaException;





/**************************************************************************
 *
 * Signals that the package will no longer be used. Resources
 * allocated to the commands may be freed.
 *
 **************************************************************************/

    void end();





/**************************************************************************
 *
 * Signals that the package will be used shortly after.
 *
 **************************************************************************/

    void start();





/**************************************************************************
 *
 * Signals that the package will not be used until a call to the
 * <code>{@link #start()}</code> method is made again.
 *
 **************************************************************************/

    void stop();


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

