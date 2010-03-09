/**************************************************************************
 *
 * Copyright (c) 2002 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: STosTeaObj.java,v 1.2 2002/09/17 16:35:28 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/06/24
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.tos;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.STypes;
import com.pdmfc.tea.tos.STosClass;
import com.pdmfc.tea.util.SListNode;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public class STosTeaObj
    implements STosObj {


    // The TOS class this instance belongs to.
    private STosClass _myClass = null;

    // Member values.
    private SContext[] _members = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public STosTeaObj()
	 throws STeaException {
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void init(STosClass tosClass,
		     SContext  context,
		     Object    constructorArgs[])
	throws STeaException {

	STosMethod constructor = _myClass.getConstructor();

	instantiateMembers();

	if ( constructor != null ) {
	    constructorArgs[0] = this;
	    constructor.exec(this, context, constructorArgs);
	}
    }





/**************************************************************************
 *
 * Instanciates the member for this object.
 *
 * @return
 *    An <TT>SContext</TT> with the members.
 *
 **************************************************************************/

   private SMemberSet instantiateMembers() {

      SMemberSet members = new SMemberSet();

      // ...

      return members;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SContext getMembers(int level) {

	// ****** ******
	return null;
    }





/**************************************************************************
 *
 * Retrieves the class object this TOS object is an instance of.
 *
 * @return The <code>{@link STosClass}</code> object this TOS object
 * is an instance of.
 *
 **************************************************************************/

      public STosClass getTosClass() {

	 return _myClass;
      }





/**************************************************************************
 *
 * This is the dispatch method. It will call the TOS method of this
 * TOS object. The TOS method is specified by the first argument to
 * the Tea command this object implements. So, <TT>args[0]</TT> is a
 * reference to this Java object, <TT>args[1]</TT> is a symbol that
 * references the TOS method to be called, the remaining arguments are
 * passed to the TOS method.
 *
 * <P>A TOS method is implemented by an <TT>STosMethod</TT> object.
 *
 * @param context
 *    The context inside which this command was invoked.
 *
 * @param args
 *    The array of arguments passed to the command.
 *
 * @return
 *    The object returned by the execution of the method.
 *
 * @exception com.pdmfc.tea.runtime.STException
 *    Only thrown by the execution of the method.
 *
 **************************************************************************/

   public Object exec(SObjFunction obj,
		      SContext     context,
		      Object[]     args)
      throws STeaException {

      if ( args.length < 2 ) {
	 return SObjNull.NULL;
      }

      return _myClass.getMethod(STypes.getSymbol(args,1)).exec(obj, context, args);
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    class SMemberSet
	extends SContext {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

	public SMemberSet() {
	}


    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

