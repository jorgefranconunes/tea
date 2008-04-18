/**************************************************************************
 *
 * Copyright (c) 2007 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SInputSource.java,v 1.1 2007/06/24 21:52:01 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2007/06/24 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.util;

import java.io.InputStream;
import java.io.IOException;





/**************************************************************************
 *
 * A source of an input stream.
 *
 **************************************************************************/

public interface SInputSource {





/**************************************************************************
 *
 * Opens and retrieves the input stream associated with this input
 * resource.
 *
 * @exception IOException Thrown if there were problems opening the
 * input stream.
 *
 **************************************************************************/

    public InputStream openStream()
        throws IOException;


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

