/**************************************************************************
 *
 * Copyright (c) 2002-2008 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SUtils.java,v 1.3 2004/04/05 19:26:05 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2004/04/03 Added the method "buildPathList(List)". (jfn)
 *
 * 2002/07/24 Created. (jfn)
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

public class SUtils
    extends Object {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private SUtils() {
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

    public static SObjPair buildPathList(String pathList) {

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

    public static SObjPair buildPathList(List pathList) {

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

