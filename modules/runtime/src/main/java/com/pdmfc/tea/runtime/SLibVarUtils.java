/**************************************************************************
 *
 * Copyright (c) 2010-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.util.ArrayList;
import java.util.List;

import com.pdmfc.tea.TeaConfigInfo;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaPair;
import com.pdmfc.tea.runtime.TeaSymbol;
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
        TeaConfigInfo.get("com.pdmfc.tea.libraryVarName");

    private static final TeaSymbol SYMBOL_LIBRARY =
        TeaSymbol.addSymbol(VAR_LIBRARY);





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

    public static void setupLibVar(final TeaContext     context,
                                   final List<String> locations) {

        // Remove empty strings from the received list.
        List<String> myLocations = new ArrayList<String>();

        for ( String item : locations ) {
            if ( item.length() > 0 ) {
                myLocations.add(item);
            }
        }

        TeaPair teaLocations  = SListUtils.buildTeaList(myLocations);

        context.newVar(VAR_LIBRARY, teaLocations);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static void prependImportLocation(final TeaContext context,
                                             final String   location) {

        try {
            SListUtils.prepend(context, SYMBOL_LIBRARY, location);
        } catch (SRuntimeException e) {
            // Either TEA_LIBRARY does not exist or it does not point
            // to a list. Either way, we will recreate it with a
            // single element.
            TeaPair head = new TeaPair(location, TeaPair.emptyList());

            context.newVar(SYMBOL_LIBRARY, head);
        }
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

