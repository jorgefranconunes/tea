/**************************************************************************
 *
 * Copyright (c) 2001-2010 PDM&FC, All Rights Reserved.
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
                throw new RuntimeException(e);
            }
        } else {
            String msg = "Missing resource \"" + RES_NAME + "\"";
            throw new RuntimeException(msg);
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

