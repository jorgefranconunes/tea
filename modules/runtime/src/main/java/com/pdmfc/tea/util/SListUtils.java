/**************************************************************************
 *
 * Copyright (c) 2010-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.util;

import java.util.List;

import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.SNoSuchVarException;
import com.pdmfc.tea.runtime.TeaPair;
import com.pdmfc.tea.runtime.TeaSymbol;
import com.pdmfc.tea.runtime.TeaVar;
import com.pdmfc.tea.runtime.TeaRunException;
import com.pdmfc.tea.runtime.Types;





/**************************************************************************
 *
 * Utility functions for manipulating lists.
 *
 **************************************************************************/

public final class SListUtils
    extends Object {





/**************************************************************************
 *
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private SListUtils() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * Fetches the pair at the head of the list. If the Tea variable
 * <code>varName</code> is not defined it will return null.
 *
 * @exception TeaRunException Throw if the Tea variable
 * <code>varName</code> does not contain an <code>TeaPair</code> instance.
 *
 **************************************************************************/

    public static TeaPair getListHead(final TeaContext  context,
                                       final TeaSymbol varName)
        throws TeaRunException {

        TeaPair result = null;
        TeaVar  var    = context.getVarObjectIfPossible(varName);

        if ( var != null ) {
            Object value = var.get();
            
            if ( !(value instanceof TeaPair) ) {
                String   msg = "Var \"{0}\" should contain a pair, not a {1}";
                Object[] fmtArgs = { varName, Types.getTypeName(value) };
                throw new TeaRunException(msg, fmtArgs);
            }

            result = (TeaPair)value;
        }

        return result;
    }





/**************************************************************************
 *
 * Prepends an element to the list pointed to by the given variable
 * and changes the variable to point to the new head of the list.
 *
 * @exception TeaRunException Thrown if no Tea variable named
 * <code>varName</code> is defined.
 *
 **************************************************************************/

    public static void prepend(final TeaContext  context,
                               final TeaSymbol varName,
                               final Object     element)
        throws TeaRunException {

        TeaVar var = context.getVarObjectIfPossible(varName);

        if ( var != null ) {
            Object   value = var.get();
            TeaPair head  = null;
            
            try {
                head = (TeaPair)value;
            } catch ( ClassCastException e ) {
                String   msg = "Var \"{0}\" should contain a pair, not a {1}";
                Object[] fmtArgs = { varName, Types.getTypeName(value) };
                throw new TeaRunException(msg, fmtArgs);
            }

            TeaPair newHead = new TeaPair(element, head);

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

    public static TeaPair buildTeaList(final List<?> list) {

        TeaPair empty = TeaPair.emptyList();
        TeaPair head  = empty;
        TeaPair elem  = null;

        for ( Object item : list ) {
            TeaPair node = new TeaPair(item, empty);
 
            if ( elem == null ) {
                head = node;
            } else {
                elem.setCdr(node);
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

