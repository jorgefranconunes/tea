/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.TeaContext;
import com.pdmfc.tea.runtime.TeaFunction;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

final class SWordString
    extends Object
    implements SWord {





    private String _string = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SWordString(final String s) {

        _string = s;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object get(final TeaContext context) {

        return _string;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public TeaFunction toFunction(final TeaContext context)
        throws TeaException {

        throw new SRuntimeException("a string can not be used as a function");
    }





/**************************************************************************
 *
 * This is used for debugging. At the moment it is rather slow.
 *
 **************************************************************************/

    // public void prettyPrint(final PrintStream out, final int indent) {

    //     out.print("\"");

    //     String s = _string;
    //     for ( int i=0; i<s.length(); i++ ) {
    //         char c = s.charAt(i);
    //         switch ( c ) {
    //         case '"'  :
    //             out.print("\\\"");
    //             break;
    //         case '\t' :
    //             out.print("\\t");
    //             break;
    //         case '\n' :
    //             out.print("\\n");
    //             break;
    //         default   :
    //             if ( c < ' ' ) {
    //                 out.print("\\" + ((char)(c+'a')));
    //             } else {
    //                 out.print(c);
    //             }
    //         }
    //     }

    //     out.print("\"");
    // }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

