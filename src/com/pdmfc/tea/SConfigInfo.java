/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SConfigInfo.java,v 1.6 2007/06/04 09:39:16 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;





/**************************************************************************
 *
 * Provides configuration information used by some parts of the Tea
 * API implementation.
 *
 **************************************************************************/

public class SConfigInfo
    extends Object {





    private static final String RES_NAME = "TeaConfig.properties";

    private static Properties _props = new Properties();

    static {
	InputStream in = SConfigInfo.class.getResourceAsStream(RES_NAME);

	if ( in != null ) {
	    try {
		_props.load(in);
	    } catch (IOException e) {
		// Too bad we just don't care...
	    }
	}
    }





/**************************************************************************
 *
 * No instances of this class will ever be instantiated.
 *
 **************************************************************************/

    private SConfigInfo() {
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static String getProperty(String key) {

	return _props.getProperty(key);
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

