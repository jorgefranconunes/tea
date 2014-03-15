/**************************************************************************
 *
 * Copyright (c) 2001-2012 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tdbc;





/**************************************************************************
 *
 * Interface that should be implemented by classes interested in
 * other objects that get closed.
 *
 **************************************************************************/

public interface SClosedEventListener {





/**************************************************************************
 *
 * Signals that an object has been closed.
 *
 * @param closedObject The object that was closed.
 *
 **************************************************************************/

    void closedEvent(Object closedObject);


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

