/**************************************************************************
 *
 * Copyright (c) 2002 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: STosClass.java,v 1.2 2002/09/17 16:35:27 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/06/24
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.tos;

import java.util.Iterator;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.tos.SNoSuchMethodException;
import com.pdmfc.tea.tos.STosMethod;
import com.pdmfc.tea.tos.STosObj;





/**************************************************************************
 *
 * Instances of this interface represent TOS classes.
 *
 **************************************************************************/

public interface STosClass {





    public static final SObjSymbol CONSTRUCTOR_NAME =
	SObjSymbol.addSymbol("constructor");





/**************************************************************************
 *
 * Retrieves the TOS base class of this TOS class.
 *
 * @return An <code>STosClass</code> object representing the TOS base
 * class of this TOS class. If it is <code>null</code> it means it has
 * no base class.
 *
 **************************************************************************/

    public STosClass getSuperClass();





/**************************************************************************
 *
 * Fetches the member names for this class. These names do not include
 * the member names of the TOS base class.
 *
 * @return An array with the member names for this class.
 *
 **************************************************************************/

    public SObjSymbol[] getMemberNames();





/**************************************************************************
 *
 * Specifies the constructor for this TOS class.
 *
 * @param method A reference to the <code{@link STosMethod} object
 * that implements this TOS class constructor.
 *
 **************************************************************************/

    public void setConstructor(STosMethod method);





/**************************************************************************
 *
 * Fetches the constructor method for this TOS class.
 *
 * @return The <code>{@link STosMethod}</code> object corresponding to
 * the constructor method for this TOS class. The null object is
 * returned if this TOS class has no constructor.
 *
 **************************************************************************/

    public STosMethod getConstructor();





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void addMethod(SObjSymbol methodName,
			  STosMethod method);





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
	throws SNoSuchMethodException;





/**************************************************************************
 *
 * Fetches all the methods this TOS class implements. These methods
 * will include all the methods of the base classes.
 *
 * @return A <code>java.util.Iterator</code> containing the all the
 * <code>STosMethod</code>s associated with this TOS class.
 *
 **************************************************************************/

    public Iterator getMethods();





/**************************************************************************
 *
 * Creates a new instance of an object of this TOS class.
 *
 * @return A new <code>STosObj</code> object, representing a new
 * instance of this TOS class.
 *
 * @exception com.pdmfc.tea.STeaException Throw if there were any
 * problems instantiating the new object.
 *
 **************************************************************************/

    public STosObj newInstance(SContext context,
			       Object[] args)
	throws STeaException;





/**************************************************************************
 *
 * Fetches the name associated with this TOS class.
 *
 * @return The name associated with the class.
 *
 **************************************************************************/

    public String getName();


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

