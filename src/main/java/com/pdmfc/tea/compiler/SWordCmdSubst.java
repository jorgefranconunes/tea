/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SWordCmdSubst.java,v 1.3 2002/09/17 16:35:27 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.compiler.SCode;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SNoSuchVarException;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;






/**************************************************************************
 *
 * Objects of this class represent an argument to a command in the
 * parse tree. This argument is a block. At runtime the block will be
 * executed and the value returned by the last statement is used as
 * the actual argument.
 *
 **************************************************************************/

class SWordCmdSubst
    extends Object
    implements SWord {





    private SCode _code = null;





/**************************************************************************
 *
 * The constructor receives the code block that will be evaluated at
 * runtime.
 *
 * @param code Reference to the object representing the code.
 *
 **************************************************************************/

    SWordCmdSubst(SCode code) {

	_code = code;
    }





/**************************************************************************
 *
 * Returns the result of evaluating the block in the context given as
 * argument.
 *
 * @param context The context where the block will be executed.
 *
 * @return A reference to the object returned by the last statement in
 * the block.
 *
 * @exception STeaException Throw if there were problems evaluating
 * the block.
 *
 **************************************************************************/

    public Object get(SContext context)
	throws STeaException {

	return _code.exec(context);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SObjFunction toFunction(SContext context)
	throws STeaException {

	Object obj = _code.exec(context);
	Object value;

	if ( obj instanceof SObjFunction ) {
	    return (SObjFunction)obj;
	}

	try {
	    value = context.getVar((SObjSymbol)obj);
	} catch (ClassCastException e1) {
	    throw new STypeException("argument 0 should be a function or a symbol, not a " + STypes.getTypeName(obj));
	} catch (SNoSuchVarException e2) {
	    value = STypes.getVarWithEffort(context, (SObjSymbol)obj);
	}
	
	try {
	    return (SObjFunction)value;
	} catch (ClassCastException e1) {
	    throw new STypeException("variable " + ((SObjSymbol)obj).getName()+
				     " should contain a function, " +
				     "not a " + STypes.getTypeName(value));
	}
    }






/**************************************************************************
 *
 * This is used for debugging. At the moment it is rather slow.
 *
 * @param out A stream where the message will be sent to.
 *
 * @param ident Number of padding white spaces inserted into the left.
 *
 **************************************************************************/

//    public void prettyPrint(PrintStream out,
//			    int         indent) {
//
//	out.println("[");
//	_code.prettyPrint(out, indent+4);
//
//	for ( int i=0; i<indent; i++ ) {
//	    out.print(' ');
//	}
//
//	out.print("]");
//    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

