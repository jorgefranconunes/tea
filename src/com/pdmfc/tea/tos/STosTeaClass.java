/**************************************************************************
 *
 * Copyright (c) 2002-2008 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
 *
 *
 * Revisions:
 *
 * 2002/06/24 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.tos;

import java.util.Iterator;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNoSuchVarException;
import com.pdmfc.tea.tos.SNoSuchMethodException;
import com.pdmfc.tea.tos.STosClass;
import com.pdmfc.tea.tos.STosMethod;
import com.pdmfc.tea.tos.STosObj;
import com.pdmfc.tea.tos.STosTeaObj;





/**************************************************************************
 *
 * Represents a TOS class implemented in Tea.
 *
 **************************************************************************/

public class STosTeaClass
    extends Object
    implements STosClass {


    


    private STosClass    _superClass  = null;
    private SObjSymbol[] _memberNames = null;

    SMethodSet _methods      = null;
    STosMethod _constructor  = null;
    String     _tosClassName = null;

    // Counter of instances. Used for automatic creation of this TOS
    // class name.
    private static int _counter = 0;

    // Used for automatic creation of TOS class name.
    private static final String NAME_PREFIX = "Class";





/**************************************************************************
 *
 * @param superClass The TOS base classe for this TOS class. If null
 * it means this TOS class is a root class.
 *
 * @param members The member names for this class.
 *
 **************************************************************************/

    public STosTeaClass(STosClass    superClass,
			SObjSymbol[] members) {

	_superClass  = superClass;
	_memberNames = members;
	_constructor = null;
	_methods     = null; // ****** ******
	    //new SMethodSet((superClass==null) ? null : superClass._methods);
    }





/**************************************************************************
 *
 * Retrieves the TOS base class of this TOS class. This will be the
 * same <code>{@link STosClass}</code> object that was passed to the
 * constructor.
 *
 * @return An <code>STosClass</code> object representing the TOS base
 * class of this TOS class. If it is <code>null</code> it means this
 * TOS class has no base class.
 *
 **************************************************************************/

    public STosClass getSuperClass() {

	return _superClass;
    }





/**************************************************************************
 *
 * Retrieves the member names of this TOS class. This set of names
 * does not include the member names of the TOS base class.
 *
 * @return An array with the member names for this class.
 *
 **************************************************************************/

   public SObjSymbol[] getMemberNames() {

      return _memberNames;
   }





/**************************************************************************
 *
 * Specifies the constructor for this TOS class.
 *
 * @param method A reference to the <TT>SObjFunction</TT> object that
 * implements the TOS method.
 *
 **************************************************************************/

    public void setConstructor(STosMethod method) {

	addMethod(STosClass.CONSTRUCTOR_NAME, method);
	_constructor = method;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public STosMethod getConstructor() {

      return _constructor;
   }





/**************************************************************************
 *
 * Associates a new TOS method with this TOS class. If a method with
 * the same name already existed then it is superceded by this new
 * method.
 *
 * @param methodName A symbol standing for the name of the method
 * being defined.
 *
 * @param method A reference to the <TT>SObjFunction</TT> object that
 * implements the TOS method.
 *
 **************************************************************************/

    public void addMethod(SObjSymbol methodName,
			  STosMethod method) {

	_methods.newVar(methodName, method);
    }





/**************************************************************************
 *
 * Retrieves the <code>STosMethod</code> corresponding to the
 * specified method for this TOS class. The method is looked for in
 * this TOS class and in its base classes.
 *
 * @return The <code>{@link STosMethod}</code> object that
 * implements the specified method.
 *
 * @exception com.pdmfc.tea.modules.tos.STosNoSuchMethodException
 * Thrown if the method had not been defined for this TOS class or in
 * any of the base classes.
 *
 **************************************************************************/

    public STosMethod getMethod(SObjSymbol methodName)
	throws SNoSuchMethodException {

	try {
	    return (STosMethod)_methods.getVar(methodName);
	} catch (SNoSuchVarException e) {
	    throw new SNoSuchMethodException(getName(), methodName);
	}
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Iterator getMethods() {

	throw new RuntimeException("Not yet implemented...");
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public STosObj newInstance(SContext context,
			       Object[] args)
	throws STeaException {

	STosObj obj = null;

	if ( _superClass == null ) {
	    obj = new STosTeaObj();
	} else {
	    obj = _superClass.newInstance(context, args);
	}
	obj.init(this, context, args);

	return obj;
    }





/**************************************************************************
 *
 * Associates a name with the class object. A name can be associated
 * to the class just for informational purposes. Currently this name
 * is only used in error messages generated by the TOS class
 * object. Note that there is no restriction on the contents of the
 * name. There may even be more than one class with the same name.
 *
 * @param name The name that will be associated with the class.
 *
 **************************************************************************/

    public void setName(String name) {

	// ****** ******
	// _name = name;
    }
    




/**************************************************************************
 *
 * Fetches the name associated with the class. This is the string
 * passed to last call to the <code>setName<(String)</code> method.
 *
 * @return
 *	The name associated with the class.
 *
 **************************************************************************/

    public String getName() {

	if ( _tosClassName == null ) {
	    _tosClassName = createName();
	}

	return _tosClassName;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static synchronized String createName() {

	return NAME_PREFIX + (++_counter);
    }

}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

class SMethodSet
    extends SContext {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SMethodSet(SMethodSet superClassMethods) {

	super(superClassMethods);
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

