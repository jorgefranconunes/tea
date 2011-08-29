/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;





/**************************************************************************
 *
 * Provides basic functionality common to most implementations of
 * modules.
 *
 **************************************************************************/

public interface SModule {





/**************************************************************************
 *
 * Populates the <code>context</code> with commands and variables.
 *
 * @param context The root context to be populated.
 *
 * @throws STeaException If there were any problems initializing the
 * module.
 *
 **************************************************************************/

   void init(SContext context)
       throws STeaException;





/**************************************************************************
 *
 * Signals that the package will no longer be used. That means that
 * the commands that may have been created inside the
 * <code>SContext</code> passed to the <code>{@link
 * #init(SContext)}</code> method will not be used again. Resources
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

