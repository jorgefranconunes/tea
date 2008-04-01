/**************************************************************************
 *
 * Copyright (c) 2002 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: STosObj.java,v 1.2 2002/09/17 16:35:28 jfn Exp $
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
import com.pdmfc.tea.tos.STosClass;





/**************************************************************************
 *
 * Represents an instance of a TOS class.
 *
 **************************************************************************/

public interface STosObj
    extends SObjFunction {





/**************************************************************************
 *
 * Initializes the object internal state.
 *
 * @param myClass The TOS class the TOS object belong to.
 *
 * @param context The context where the TOS object is being
 * instantiated.
 *
 * @param constructorArgs The arguments to pass the constructor of
 * this TOS object.
 *
 **************************************************************************/

    public void init(STosClass myClass,
		     SContext  context,
		     Object[]  constructorArgs)
	throws STeaException;





/**************************************************************************
 *
 * Fetches the TOS class this TOS object is an instance of. This
 * method is supposed to be invoked only after a call to <code>{@link
 * #init(STosClass,SContext,Object[]) init(...)}</code>.
 *
 * @return The <code>{@link STosClass}</code> this TOS object is
 * associated with.
 *
 **************************************************************************/

    public STosClass getTosClass();





/**************************************************************************
 *
 * Fetches the members for this TOS object corresponding to a given
 * TOS class in the TOS object class hierarchy. The returned
 * <code>{@link SContext}</code> has no parent.
 *
 * @param level Identifies the TOS class in the TOS object class
 * hierarchy of this TOS object. A value of zero means the root class
 * in the hierarchy.
 *
 * @return The set of members (names and values) for this object
 * related to a specific TOS class.
 *
 **************************************************************************/

    public SContext getMembers(int level);


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

