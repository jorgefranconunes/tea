/**************************************************************************
 *
 * Copyright (c) 2002 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SWidget.java,v 1.2 2002/10/28 16:20:58 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/06/14
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tango;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.tos.STosClass;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypes;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public abstract class SWidget
    extends STosObj {





    private Component _component;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    protected SWidget(STosClass myClass)
	 throws STeaException {

	super(myClass);

	_component = null;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    protected void setComponent(Component component) {

	_component = component;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Component getComponent() {

	return _component;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object set(SObjFunction obj,
		      SContext     context,
		      Object[]     args)
	throws STeaException {

	if ( args.length != 4 ) {
	    throw new SNumArgException("attrib value");
	}

	String   attrName   = STypes.getSymbol(args, 2).getName();
	Object   attrValue  = args[3];
	Method   setter     = getSetterMethod(attrName);
	Object[] setterArgs = new Object[] {context, attrName, attrValue};
	Object   result     = null;

	try {
	    setter.invoke(this, setterArgs);
	} catch (IllegalAccessException e) {
	    throw new SRuntimeException("Method {0}.{1} is not accessible",
					new Object[] {getClass().getName(),
						      setter.getName()});
	} catch (InvocationTargetException e) {
	    Throwable e2 =  e.getTargetException();
	    if ( e2 instanceof STeaException ) {
		throw (STeaException)e2;
	    }
	    throw new SRuntimeException("Internal error - {0} - {1}",
					new Object[] {e2.getClass().getName(),
						      e2.getMessage()});
	}

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object get(SObjFunction obj,
		      SContext     context,
		      Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException("attrib");
	}

	String   attrName   = STypes.getSymbol(args, 2).getName();
	Method   getter     = getGetterMethod(attrName);
	Object[] getterArgs = new Object[] {context, attrName};
	Object   result     = null;

	try {
	    getter.invoke(this, getterArgs);
	} catch (IllegalAccessException e) {
	    throw new SRuntimeException("Method {0}.{1} is not accessible",
					new Object[] {getClass().getName(),
						      getter.getName()});
	} catch (InvocationTargetException e) {
	    Throwable e2 =  e.getTargetException();
	    if ( e2 instanceof STeaException ) {
		throw (STeaException)e2;
	    }
	    throw new SRuntimeException("Internal error - {0} - {1}",
					new Object[] {e2.getClass().getName(),
						      e2.getMessage()});
	}

	return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public abstract Method getSetterMethod(String attr);





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public abstract Method getGetterMethod(String attr);


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

