/**************************************************************************
 *
 * Copyright (c) 2002-2008 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SModule.java,v 1.5 2002/07/16 18:50:40 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/01/10 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.STeaRuntime;





/**************************************************************************
 *
 * Provides basic functionality common to most implementations of
 * modules.
 *
 **************************************************************************/

public class SModule
    extends Object {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SModule() {
    }





/**************************************************************************
 *
 * Populates the <code>context</code> with commands and variables. If
 * this method is redefined in a derived class then the implementation
 * in the derived class is always expected to call this
 * implementation.
 *
 * @param context Reference to the <TT>STeaRuntime</TT> object to be
 * populated.
 *
 **************************************************************************/

   public void init(STeaRuntime context)
       throws STeaException {
   }





/**************************************************************************
 *
 * Signals that the package will no longer be used. That means that
 * the commands that may have been created inside the
 * <TT>SContext</TT> passed to the <TT>init()</TT> method will not be
 * used again. Resources allocated to the commands may be freed. This
 * implementation does nothing.
 *
 **************************************************************************/

    public void end() {
    }





/**************************************************************************
 *
 * Signals that the package will be used shortly after. This
 * implementaion does nothing.
 *
 **************************************************************************/

    public void start() {
    }





/**************************************************************************
 *
 * Signals that the package will not be used until a call to the
 * <TT>start()</TT> method is made again. This implementation does
 * nothing.
 *
 **************************************************************************/

    public void stop() {
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

