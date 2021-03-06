/**************************************************************************
 *
 * Copyright (c) 2001-2012 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import java.io.InputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;





/**************************************************************************
 *
 * Provides configuration information used by some parts of the Tea
 * API implementation.
 *
 **************************************************************************/

public final class TeaConfig
    extends Object {





    private static final String RES_NAME = "TeaConfig.properties";

    private static Properties _props = new Properties();







/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    static {
        String      resourcePath = RES_NAME;
        InputStream input        =
            TeaConfig.class.getResourceAsStream(resourcePath);

        if ( input != null ) {
            try {
                _props.load(input);
            } catch ( IOException e ) {
                String msg =
                    MessageFormat.format("Failed to read resource \"{0}\"",
                                         resourcePath);
                throw new IllegalStateException(msg, e);
            } finally {
                try { input.close(); } catch ( IOException e ) {/* */}
            }
        } else {
            String msg = 
                MessageFormat.format("Missing resource \"{0}\"",
                                     resourcePath);
            throw new IllegalStateException(msg);
        }
    }





/**************************************************************************
 *
 * No instances of this class will ever be instantiated.
 *
 **************************************************************************/

    private TeaConfig() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * @param key Name of the configuration parameter to retrieve.
 *
 * @return The value of the given configuration parameter. Null if no
 * configuration paremeter with that name is known.
 *
 **************************************************************************/

    public static String get(final String key) {

        return _props.getProperty(key);
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

