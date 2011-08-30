/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDM&FC, All Rights Reserved.
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

public final class SConfigInfo
    extends Object {





    private static final String RES_NAME = "TeaConfig.properties";

    private static Properties _props = new Properties();







/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    static {
        InputStream in = SConfigInfo.class.getResourceAsStream(RES_NAME);

        if ( in != null ) {
            try {
                _props.load(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try { in.close(); } catch (IOException e) {/**/}
            }
        } else {
            String msg = "Missing resource \"" + RES_NAME + "\"";
            throw new IllegalStateException(msg);
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
 * @param key Name of the configuration parameter to retrieve.
 *
 * @return The value of the given configuration parameter. Null if no
 * configuration paremeter with that name is known.
 *
 **************************************************************************/

    public static String getProperty(final String key) {

        return _props.getProperty(key);
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

