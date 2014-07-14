/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import java.util.List;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.TeaPair;
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

    public Object get(final TeaContext context)
        throws TeaException {

        TeaPair empty = new TeaPair(null, null);
        TeaPair head  = empty;
        TeaPair elem  = null;

        for ( SWord word : _list ) {
            Object   car  = word.get(context);
            TeaPair node = new TeaPair(car, empty);
            
            if ( elem == null ) {
                head = node;
            } else {
                elem.setCdr(node);
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

    public TeaFunction toFunction(final TeaContext context)
        throws TeaException {

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

