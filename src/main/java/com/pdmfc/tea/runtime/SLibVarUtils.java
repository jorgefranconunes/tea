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
 * 2010/03/04 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.util.ArrayList;
import java.util.List;

import com.pdmfc.tea.SConfigInfo;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.util.SListUtils;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public class SLibVarUtils
    extends Object {





    // The name of the Tea global variable with the list of directory
    // names where the <code>import</code> function looks for Tea
    // source files.
    private static final String VAR_LIBRARY   =
        SConfigInfo.getProperty("com.pdmfc.tea.libraryVarName");

    private static final SObjSymbol SYMBOL_LIBRARY =
        SObjSymbol.addSymbol(VAR_LIBRARY);





/**************************************************************************
 *
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private SLibVarUtils() {
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static void setupLibVar(SContext     context,
                                   List<String> locations) {

        // Remove empty strings from the received list.
        List<String> myLocations = new ArrayList<String>();

        for ( String item : locations ) {
            if ( item.length() > 0 ) {
                myLocations.add(item);
            }
        }

        SObjPair teaLocations  = SListUtils.buildTeaList(myLocations);

        context.newVar(VAR_LIBRARY, teaLocations);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static void prependImportLocation(SContext context,
                                             String   location) {

        try {
            SListUtils.prepend(context, SYMBOL_LIBRARY, location);
        } catch (SRuntimeException e) {
            // Either TEA_LIBRARY does not exist or it does not point
            // to a list. Either way, we will recreate it with a
            // single element.
            SObjPair head = new SObjPair(location, SObjPair.emptyList());

            context.newVar(SYMBOL_LIBRARY, head);
        }
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

