/**************************************************************************
 *
 * Copyright (c) 2010 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
 *
 *
 * Revisions:
 *
 * 2010/03/12 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.SConfigInfo;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjPair;





/**************************************************************************
 *
 * Utility functions to manage the "argv0", "argv" variables in a Tea
 * interpreter.
 *
 **************************************************************************/

public class SArgvUtils
    extends Object {





    // The name of the Tea global variable that will contain the 0th
    // argument.
    private static final String VAR_ARGV0 =
        SConfigInfo.getProperty("com.pdmfc.tea.argv0VarName");

    // The name of the Tea global variable that will contain the list
    // of command line arguments.
    private static final String VAR_ARGV =
	SConfigInfo.getProperty("com.pdmfc.tea.argvVarName");





/**************************************************************************
 *
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private SArgvUtils() {
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static void setArgv(SContext toplevelContext,
                               String   argv0,
                               String[] argv) {

        // Setup the "argv0" variable.
        Object argv0Value = (argv0!=null) ? argv0 : SObjNull.NULL;

	toplevelContext.newVar(VAR_ARGV0, argv0Value);


        // Setup the "argv" variable.
        SObjPair head = SObjPair.emptyList();

        if ( argv != null ) {
            for (int i=argv.length-1; i>=0; --i ) {
                head = new SObjPair(argv[i], head);
            }
        } else {
            // We assume an empty list for "argv".
        }

        toplevelContext.newVar(VAR_ARGV, head);
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

