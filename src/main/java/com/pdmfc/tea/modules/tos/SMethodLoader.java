/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SMethodLoader.java,v 1.1 2001/07/11 13:58:27 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypes;





/**************************************************************************
 *
 * This class implements a TOS method that when invoked for the first
 * time loads and creates an instance of another class. That instance
 * will then replace the TOS method that had just been invoked. The
 * loaded class must be derived from <TT>SObjFunction</TT> and it must
 * represent a TOS method for the same TOS class the
 * <TT>SMethodLoader</TT> had been a method of.
 *
 **************************************************************************/

public class SMethodLoader
    extends Object
    implements SObjFunction {





   private String       _className;
   private SObjFunction _method;





/**************************************************************************
 *
 * @param className
 *    The name of the class to load the first time the <TT>exec()</TT>
 *    method is invoked.
 *
 **************************************************************************/

   public SMethodLoader(String className) {

      _className = className;
      _method    = null;
   }





/**************************************************************************
 *
 * This method is supposed to be called with <TT>args</TT> having at
 * least one element.
 *
 * <P>Argument <TT>obj</TT> is supposed to be an
 * <TT>STosObj</TT>. Argument <TT>args[1]</TT> is supposed to be a
 * symbol naming the called method. The method named after
 * <TT>args[1]</TT> of the class of the <TT>obj</TT> TOS object is
 * replaced by an instance of the classe whose name was passed to the
 * constructor.
 *
 * @param obj
 *    The <TT>SObjProc</TT> that led to execute this method.
 *
 * @param context
 *    The intepreter context where the Tea function was called.
 *
 * @param args
 *    The arguments the function was called with.
 *
 * @exception STeaException
 *   Thrown if there were problems instantianting the new
 *   <TT>SObjProc</TT> or while invoking the <TT>exec()</TT> method
 *   for that object.
 *
 **************************************************************************/

   public Object exec(SObjFunction obj,
		      SContext     context,
		      Object[]     args)
      throws STeaException {

      if ( _method != null ) {
	 return _method.exec(obj, context, args);
      }

      STosObj    object     = (STosObj)obj;
      STosClass  objClass   = object.getTosClass();
      SObjSymbol methodName = STypes.getSymbol(args,1);

      _method = instantiateProc(_className);
      objClass.addMethod(methodName, _method);

      return _method.exec(obj, context, args);
   }





/**************************************************************************
 *
 * Instantiates an object of the class whose name is given as
 * argument. That class must be derived from the <TT>SObjFunction</TT>
 * class. The constructor with no arguments is invoked.
 *
 * @param className
 *    The name of the class of the object to be instantiated.
 *
 * @return
 *    A newly created object of the class named <TT>className</TT>.
 *
 **************************************************************************/

   private static SObjFunction instantiateProc(String className)
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
         msg = "class '" + className + "' or its initializer are not accessible";
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

