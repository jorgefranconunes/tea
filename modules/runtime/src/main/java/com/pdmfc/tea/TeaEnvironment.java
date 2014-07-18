/**************************************************************************
 *
 * Copyright (c) 2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaCompileException;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaScript;





/**************************************************************************
 *
 * The Tea runtime environment accessible by a Tea module.
 *
 **************************************************************************/

public interface TeaEnvironment {





/**************************************************************************
 *
 * Creates a new global variable. If a variable with the same name
 * already exists it will be overwriten.
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
 * Retrieves the Tea global context. This is the root context from
 * which all other contexts descend. Tea global variables exist in the
 * global context.
 *
 * @return The Tea global context.
 *
 **************************************************************************/

    TeaContext getGlobalContext();





/**************************************************************************
 *
 * Retrieves the charset assumed for the Tea source code.
 *
 * @return The charset for Tea source code to be executed.
 *
 **************************************************************************/

    public Charset getSourceCharset();





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaEnvironment addModule(TeaModule module)
        throws TeaException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaScript compile(final Reader reader,
                             final String fileName)
        throws IOException,
               TeaCompileException;


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

