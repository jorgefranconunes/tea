/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import java.io.IOException;
import java.io.Reader;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaCompileException;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaScript;





/**************************************************************************
 *
 * A Tea interpreter that can be used to execute compiled code.
 *
 **************************************************************************/

public interface TeaRuntime {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Deprecated
    TeaContext getToplevelContext();





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    TeaScript compile(final Reader reader,
                      final String fileName)
        throws IOException,
               TeaCompileException;





/**************************************************************************
 *
 * Signals that this context will be used shortly after.
 *
 **************************************************************************/

    void start()
        throws TeaException;





/**************************************************************************
 *
 * Signals that this context will not be used until a call to the
 * <code>start()</code> method is made again. All the packages loaded
 * so far are signaled with a call to their <code>stop()</code>
 * method.
 *
 * <p>If this runtime has not been started then an
 * <code>java.lang.IllegalStateException</code> will be thrown.
 *
 **************************************************************************/

    void stop();





/**************************************************************************
 *
 * Signals that this context is no longer to be used. All the packages
 * that had been loaded so far are signaled. Then they are discarded.
 *
 **************************************************************************/

    void end();


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

