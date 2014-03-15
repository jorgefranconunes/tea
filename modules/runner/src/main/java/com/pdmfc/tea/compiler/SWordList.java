/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import java.util.List;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

final class SWordList
    extends Object
    implements SWord {





    private List<SWord> _list = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SWordList(final List<SWord> list) {

        _list = list;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object get(final SContext context)
        throws STeaException {

        SObjPair empty = new SObjPair(null, null);
        SObjPair head  = empty;
        SObjPair elem  = null;

        for ( SWord word : _list ) {
            Object   car  = word.get(context);
            SObjPair node = new SObjPair(car, empty);
            
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
 * 
 *
 **************************************************************************/

    public SObjFunction toFunction(final SContext context)
        throws STeaException {

        throw new SRuntimeException("a list can not be used as a function");
    }






/**************************************************************************
 *
 * This is used for debugging. At the moment it is rather slow.
 *
 **************************************************************************/

//    public void prettyPrint(final PrintStream out,
//                            final int         indent) {
//
//        out.print("( ");
//
//        for ( Iterator i=_list.iterator(); i.hasNext(); ) {
//            ((SWord)i.next()).prettyPrint(out, indent+4);
//            out. print(' ');
//        }
//
//        out.print(")");
//    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

