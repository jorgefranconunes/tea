/**************************************************************************
 *
 * Copyright (c) 2002-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.pdmfc.tea.runtime.SObjPair;





/**************************************************************************
 *
 * Provides utility methods.
 *
 **************************************************************************/

public final class SUtils
    extends Object {





/**************************************************************************
 *
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private SUtils() {

        // Nothing to do...
    }





/**************************************************************************
 *
 * Creates a Tea list of strings from the <code>pathList</code>
 * argument. The path components in <code>pathList</code> are supposed
 * to be separated with the plataform path separator character.
 *
 * @param pathList Represents a list of paths.
 *
 * @return The head of a Tea list.
 *
 **************************************************************************/

    public static SObjPair buildPathList(final String pathList) {

        SObjPair empty    = SObjPair.emptyList();
        SObjPair head     = empty;
        SObjPair elem     = null;
        String   pathSep  = File.pathSeparator;

        if ( pathList == null ) {
            return empty;
        }

        StringTokenizer st = new StringTokenizer(pathList, pathSep);

        while ( st.hasMoreTokens() ) {
            String   path = st.nextToken();
            SObjPair node = null;

            if ( path.length() == 0 ) {
                continue;
            }
            path = path.replace('|', ':');
            node = new SObjPair(path, empty);
 
            if ( elem == null ) {
                head = node;
            } else {
                elem._cdr = node;
            }
            elem = node;
        }

        return head;
    }





/**************************************************************************
 *
 * Creates a Tea list of strings from the given list.
 *
 * @param pathList A list where each element is a string representing
 * a path or URL.
 *
 * @return The head of a Tea list.
 *
 **************************************************************************/

    public static SObjPair buildPathList(final List pathList) {

        SObjPair empty = SObjPair.emptyList();
        SObjPair head  = empty;
        SObjPair elem  = null;

        if ( pathList == null ) {
            return empty;
        }

        for ( Iterator i=pathList.iterator(); i.hasNext(); ) {
            String   path = (String)i.next();
            SObjPair node = null;

            if ( path.length() == 0 ) {
                continue;
            }
            path = path.replace('|', ':');
            node = new SObjPair(path, empty);
 
            if ( elem == null ) {
                head = node;
            } else {
                elem._cdr = node;
            }
            elem = node;
        }

        return head;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

