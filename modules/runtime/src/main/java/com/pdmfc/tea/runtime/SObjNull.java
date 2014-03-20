/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;





/**************************************************************************
 *
 * The class for Tea null object. There will be only a single instance
 * of this class.
 *
 **************************************************************************/

public final class SObjNull
    extends Object {





    /**
     * The Tea null object.
     */
    public static final Object NULL = new SObjNull();





/**************************************************************************
 *
 * The constructor is private to ensure that only one instance will ever
 * exist.
 *
 **************************************************************************/

    private SObjNull() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Override
    public String toString() {

        String result = "NULL";

        return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

