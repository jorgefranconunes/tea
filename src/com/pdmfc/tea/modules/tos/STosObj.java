/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: STosObj.java,v 1.7 2002/11/02 16:05:02 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.STypes;
import com.pdmfc.tea.util.SList;
import com.pdmfc.tea.util.SListNode;





/**************************************************************************
 *
 * This class implements a Tea command that behaves like an TOS object.
 *
 **************************************************************************/

public class STosObj
    implements SObjFunction {


    // The TOS class this instance belongs to.
    private STosClass _myClass;

    // An array of SContext containing the members for each level.
    private SMemberSet[] _members;

    // An array with the parts of this TOS object.
    private STosObj[] _parts;

    // The TOS object that this instance is part of.
    private STosObj _selfObj;

    // The part of the TOS object lower in the class hierarchy.
    private STosObj   _superObj;

    // The level of the TOS object in the parts array.
    private int _level;





/**************************************************************************
 *
 * Initializes a new TOS object.
 *
 * <P>The creation of an TOS object is done in two steps. First the
 * object is instantiated. Then it is initialized with a call to the
 * <TT>init()</TT> method.
 *
 * @param theClass
 *    The TOS class of the TOS object represented by this Java object.
 *
 **************************************************************************/

   public STosObj(STosClass theClass)
	 throws STeaException {

      _selfObj = this;
      _myClass = theClass;
      _members = null;
      _level   = theClass.level();

      STosClass base = _myClass.getSuperClass();

      _superObj = (base==null) ? null : base.newInstance();
   }





/**************************************************************************
 *
 * @param context The context where the constructor was called.
 *
 * @param args The arguments to the constructor.
 *
 * @exception com.pdmfc.tea.STeaException Only thrown by the call to
 * the constructor method.
 *
 **************************************************************************/

   public void init(SContext context,
		    Object   args[])
         throws STeaException {

      SObjFunction constructor = _myClass.getConstructor();

      _members         = new SMemberSet[_level+1];
      _members[_level] = instanciateMembers();
      _parts           = new STosObj[_level+1];
      _parts[_level]   = this;

      if ( _superObj != null ) {
	 _superObj.init(_selfObj, _members, _parts);
      }
      // WARNING: No checking should be needed for "context" and "args".
      // CHECK THE CLIENT CODE!!!
      if ( (context!=null) && (args!=null) && (constructor!=null) ) {
	 args[0] = this;
         constructor.exec(_selfObj, context, args);
      }
   }





/**************************************************************************
 *
 * @param selfObj
 *    Reference to the object of whom this one is part of.
 *
 * @param members
 *    The array of lists of members for each part of the object.
 *
 * @param parts
 *    Array containing the parts of this TOS object.
 *
 **************************************************************************/

   private void init(STosObj      selfObj,
		     SMemberSet[] members,
		     STosObj[]    parts) {

      _selfObj = selfObj;
      _members = members;
      _members[_level] = instanciateMembers();
      _parts = parts;
      _parts[_level] = this;

      if ( _superObj != null ) {
	 _superObj.init(selfObj, members, parts);
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

   private SMemberSet instanciateMembers() {

      SMemberSet members = new SMemberSet();
      SListNode node = _myClass.memberNames().head(); 

      while ( node != null ) {
      	  members.newVar((SObjSymbol)node._element, SObjNull.NULL);
	  node = node._next;
      }

      return members;
   }





/**************************************************************************
 *
 * Retrieves the TOS object this object is part of.
 *
 * @return A reference to the TOS object this object is part of.
 *
 **************************************************************************/

   public STosObj selfObj() {

      return _selfObj;
   }





/**************************************************************************
 *
 * Retrieves the members of this TOS object pertaning to the class at
 * level <TT>level</TT> in the hierarchy (level 0 is the base class of
 * them all).
 *
 **************************************************************************/

   SContext members(int level) {

      return _members[level];
   }





/**************************************************************************
 *
 * Retrieves the part of this TOS object at level <code>level</code>.
 *
 * @param level The index of the part to retrieve.
 *
 * @return The part of this TOS object at level <code>level</code>.
 *
 **************************************************************************/

   public STosObj part(int level) {

      return (STosObj)_parts[level];
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

