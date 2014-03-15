/**************************************************************************
 *
 * Copyright (c) 2010-2011 PDMFC, All Rights Reserved.
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
 * Utility methods for managing the <code>TEA_LIBRARY</code> Tea
 * variable.
 *
 **************************************************************************/

public final class SLibVarUtils
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

    public static void setupLibVar(final SContext     context,
                                   final List<String> locations) {

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

    public static void prependImportLocation(final SContext context,
                                             final String   location) {

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

