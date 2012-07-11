/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SArgs;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * This class implements a TOS method that when invoked for the first
 * time loads and creates an instance of another class. That instance
 * will then replace the TOS method that had just been invoked. The
 * loaded class must be derived from <code>SObjFunction</code> and it
 * must represent a TOS method for the same TOS class the
 * <code>SMethodLoader</code> had been a method of.
 *
 **************************************************************************/

public final class SMethodLoader
    extends Object
    implements SObjFunction {





   private String       _className;
   private SObjFunction _method;





/**************************************************************************
 *
 * @param className The name of the class to load the first time the
 *    <code>exec()</code> method is invoked.
 *
 **************************************************************************/

   public SMethodLoader(final String className) {

      _className = className;
      _method    = null;
   }





/**************************************************************************
 *
 * This method is supposed to be called with <code>args</code> having
 * at least one element.
 *
 * <P>Argument <code>obj</code> is supposed to be an
 * <code>STosObj</code>. Argument <code>args[1]</code> is supposed to
 * be a symbol naming the called method. The method named after
 * <code>args[1]</code> of the class of the <code>obj</code> TOS
 * object is replaced by an instance of the classe whose name was
 * passed to the constructor.
 *
 * @param obj The <code>SObjProc</code> that led to execute this
 * method.
 *
 * @param context The intepreter context where the Tea function was
 * called.
 *
 * @param args The arguments the function was called with.
 *
 * @exception STeaException Thrown if there were problems
 * instantianting the new <code>SObjProc</code> or while invoking the
 * <code>exec()</code> method for that object.
 *
 **************************************************************************/

   public Object exec(final SObjFunction obj,
                      final SContext     context,
                      final Object[]     args)
      throws STeaException {

      if ( _method != null ) {
         return _method.exec(obj, context, args);
      }

      STosObj    object     = (STosObj)obj;
      STosClass  objClass   = object.getTosClass();
      SObjSymbol methodName = SArgs.getSymbol(args,1);

      _method = instantiateProc(_className);
      objClass.addMethod(methodName, _method);

      return _method.exec(obj, context, args);
   }





/**************************************************************************
 *
 * Instantiates an object of the class whose name is given as
 * argument. That class must be derived from the
 * <code>SObjFunction</code> class. The constructor with no arguments
 * is invoked.
 *
 * @param className The name of the class of the object to be
 * instantiated.
 *
 * @return A newly created object of the class named
 * <code>className</code>.
 *
 **************************************************************************/

   private static SObjFunction instantiateProc(final String className)
      throws SRuntimeException {

      SObjFunction proc = null;
      String       msg  = null;

      try {
         proc = (SObjFunction)Class.forName(className).newInstance();
      } catch (ClassNotFoundException e1) {
         msg = "could not find class '" + className + "'";
      } catch (InstantiationException e2) {
         msg = "failed instantiation for object of class '" + className + "'";
      } catch (IllegalAccessException e3) {
         msg = "class '" + className
             + "' or its initializer are not accessible";
      } catch (ClassCastException e4) {
         msg = "class '" + className + "' does not implement SObjFunction";
      } catch (NoSuchMethodError e5) {
         msg = "class '" + className + "' does not have a default constructor";
      }

      if ( msg != null ) {
         throw new SRuntimeException(msg);
      }

      return proc;
   }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

