/**************************************************************************
 *
 * Copyright (c) 2012-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;





/**************************************************************************
 *
 * Indicates that the annotated method implements a Tea function.
 *
 * <p>This annotaion is intended to be used only on methods with the
 * following signature:</p>
 *
 * <code>Object aMethod(TeaFunction, TeaContext, Object[])</code>
 *
 * <p>The annotated methods can be either class methods or instance
 * methods.</p>
 *
 **************************************************************************/

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TeaFunctionImplementor {





/**************************************************************************
 *
 * The name of the Tea function.
 *
 **************************************************************************/

    String value();

}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

