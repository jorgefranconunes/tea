/**************************************************************************
 *
 * Copyright (c) 2010-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.TeaConfigInfo;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjPair;





/**************************************************************************
 *
 * Utility functions to manage the "argv0", "argv" variables in a Tea
 * interpreter.
 *
 **************************************************************************/

final class SArgvUtils
    extends Object {





    // The name of the Tea global variable that will contain the 0th
    // argument.
    private static final String VAR_ARGV0 =
        TeaConfigInfo.get("com.pdmfc.tea.argv0VarName");

    // The name of the Tea global variable that will contain the list
    // of command line arguments.
    private static final String VAR_ARGV =
        TeaConfigInfo.get("com.pdmfc.tea.argvVarName");





/**************************************************************************
 *
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private SArgvUtils() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static void setArgv(final SContext toplevelContext,
                               final String   argv0,
                               final String[] argv) {

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

