/**************************************************************************
 *
 * Copyright (c) 2007-2011 PDMFC, All Rights Reserved.
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
 * source.
 *
 * <p>It is the responsability of the caller to close the returned
 * <code>InputStream</code>.
 *
 * @return A newly open <code>InputStream</code> associated with this
 * input source.
 *
 * @exception IOException Thrown if there were problems opening the
 * input stream.
 *
 **************************************************************************/

    InputStream openStream()
        throws IOException;


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

