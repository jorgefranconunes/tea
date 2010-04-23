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

package com.pdmfc.tea.util;

import java.util.List;

import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNoSuchVarException;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SObjVar;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypes;





/**************************************************************************
 *
 * Utility functions for manipulating lists.
 *
 **************************************************************************/

public class SListUtils
    extends Object {





/**************************************************************************
 *
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private SListUtils() {
    }





/**************************************************************************
 *
 * Fetches the pair at the head of the list. If the Tea variable
 * <code>varName</code> is not defined it will return null.
 *
 * @exception SRuntimeException Throw if the Tea variable
 * <code>varName</code> does not contain an <code>SObjPair</code> instance.
 *
 **************************************************************************/

    public static SObjPair getListHead(SContext   context,
                                       SObjSymbol varName)
        throws SRuntimeException {

        SObjPair result = null;
        SObjVar  var    = context.getVarObjectIfPossible(varName);

        if ( var != null ) {
            Object value = var.get();
            
            if ( !(value instanceof SObjPair) ) {
                String   msg = "Var \"{0}\" should contain a pair, not a {1}";
                Object[] fmtArgs = { varName, STypes.getTypeName(value) };
                throw new SRuntimeException(msg, fmtArgs);
            }

            result = (SObjPair)value;
        }

        return result;
    }





/**************************************************************************
 *
 * Prepends an element to the list pointed to by the given variable
 * and changes the variable to point to the new head of the list.
 *
 * @exception SRuntimeException Thrown if no Tea variable named
 * <code>varName</code> is defined.
 *
 **************************************************************************/

    public static void prepend(SContext   context,
                               SObjSymbol varName,
                               Object     element)
        throws SRuntimeException {

        SObjVar var = context.getVarObjectIfPossible(varName);

        if ( var != null ) {
            Object   value = var.get();
            SObjPair head  = null;
            
            try {
                head = (SObjPair)value;
            } catch (ClassCastException e) {
                String   msg = "Var \"{0}\" should contain a pair, not a {1}";
                Object[] fmtArgs = { varName, STypes.getTypeName(value) };
                throw new SRuntimeException(msg, fmtArgs);
            }

            SObjPair newHead = new SObjPair(element, head);

            var.set(newHead);
        } else {
	    throw new SNoSuchVarException(varName);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static SObjPair buildTeaList(List<? extends Object> list) {

	SObjPair empty = SObjPair.emptyList();
	SObjPair head  = empty;
	SObjPair elem  = null;

	for ( Object item : list ) {
	    SObjPair node = new SObjPair(item, empty);
 
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

