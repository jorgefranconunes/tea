/**************************************************************************
 *
 * Copyright (c) 2001-2010 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
 *
 *
 * Revisions:
 *
 * 2010/03/03 This class is now an interface. (jfn)
 *
 * 2002/01/10 Created. (jfn)
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
 **************************************************************************/

   public void init(SContext context)
       throws STeaException;





/**************************************************************************
 *
 * Signals that the package will no longer be used. That means that
 * the commands that may have been created inside the
 * <TT>SContext</TT> passed to the <TT>init()</TT> method will not be
 * used again. Resources allocated to the commands may be freed.
 *
 **************************************************************************/

    public void end();





/**************************************************************************
 *
 * Signals that the package will be used shortly after.
 *
 **************************************************************************/

    public void start();





/**************************************************************************
 *
 * Signals that the package will not be used until a call to the
 * <TT>start()</TT> method is made again.
 *
 **************************************************************************/

    public void stop();


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

