/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
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
import com.pdmfc.tea.modules.tos.SNoSuchMethodException;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNoSuchVarException;
import com.pdmfc.tea.util.SList;





/**************************************************************************
 *
 * This class implements a TOS class.
 *
 **************************************************************************/

public class STosClass
    extends Object {





    // The base class of this one
    private STosClass _superClass;

    // List of member names (symbols)
    private SList _members;

    // The level down in the class hierarchy (0 => no base class)
    private int _level;

    // This context stores the methods of this TOS class.
    private SMethodSet _methods;

    // The constructor proc.
    private SObjFunction _constructor;

    // Name of the class. Just used for informational purposes.
    private String _name;

    // Counter of instances.
    private static int _counter = 0;

    private static final String NAME_PREFIX = "Class";

    // The constructor method name.
    public static final SObjSymbol _constructName =
        SObjSymbol.addSymbol("constructor");





/**************************************************************************
 *
 * Defines a new TOS class.
 *
 * @param superClass
 *    The base class of the class being defined. If it is null
 *    it means the class being defined has no base class.
 *
 * @param members
 *    Array with the member names. It must be a <TT>Vector</TT> of
 *    <TT>SObjSymbol</TT> objects.
 *
 **************************************************************************/

    public STosClass(STosClass superClass,
                     SList     members) {

        _name        = null;
        _superClass  = superClass;
        _members     = members;
        _constructor = null;
        _methods     = new SMethodSet((_superClass==null)
                                      ? null : _superClass._methods);
        _level   = (_superClass==null) ? 0 : (1+_superClass.level());
    }





/**************************************************************************
 *
 * Defines a new TOS class with no base class.
 *
 * @param members
 *    Array with the member names. It must be a <TT>Vector</TT> of
 *    <TT>SObjSymbol</TT> objects.
 *
 * @exception com.pdmfc.tea.modules.tos.STosNoSuchClassException
 *    Never thrown.
 *
 **************************************************************************/

   public STosClass(SList members) {

      this(null, members);
   }





/**************************************************************************
 *
 * Defines a new TOS class with no members.
 *
 * @param superClass
 *    The base class of the class being defined. If it is null
 *    it means the class being defined has no base class.
 *
 **************************************************************************/

   public STosClass(STosClass superClass) {

      this(superClass, new SList());
   }





/**************************************************************************
 *
 * Defines a new TOS class with no base class and with no members.
 *
 **************************************************************************/

   public STosClass() {

      this(null, new SList());
   }





/**************************************************************************
 *
 * Retrieves the TOS base class of this TOS class.
 *
 * @return
 *    An <TT>STosClass</TT> object representing the TOS base class of this
 *    TOS class. If it is <TT>null</TT> it means it has no base class.
 *
 **************************************************************************/

   public STosClass getSuperClass() {

      return _superClass;
   }





/**************************************************************************
 *
 * Retrieves the member names of this TOS class.
 *
 * @return
 *    An <TT>Enumeration</TT> that will iterate over all the member names.
 *    The <TT>Enumeration</TT> will always iterate over the members the
 *    same way.
 *
 **************************************************************************/

   public SList memberNames() {

      return _members;
   }





/**************************************************************************
 *
 * Retrieves the level of the TOS class definition down the hierarchy.
 * A value of zero means it is a TOS class with no base class.
 *
 * @return A non-negative integer.
 *
 **************************************************************************/

   public int level() {

      return _level;
   }





/**************************************************************************
 *
 * Creates a new instance of an object of this TOS class, but does not
 * initialize the object.
 *
 * @return A new <TT>STosObj</TT> object, representing a new instance
 * of this class. The object will have to be initialized.
 *
 * @exception STeaException Not throws by this method. Only declared
 * for derived classes that reimplement this method.
 *
 **************************************************************************/

   public STosObj newInstance()
         throws STeaException {

      return new STosObj(this);
   }





/**************************************************************************
 *
 * Creates a new instance of an object of this TOS class.
 *
 * @param args
 *    Array of argumens to be passed to the constructor. The first element
 *    in the array is not used. The second element should be a symbol
 *    with the class name of the object being instantiated.
 *
 * @return
 *    A new <TT>STosObj</TT> object, representing a new instance of this
 *    TOS class.
 *
 **************************************************************************/

   public STosObj newInstance(SContext context,
                              Object[] args)
         throws STeaException {

      STosObj obj = newInstance();

      obj.init(context, args);

      return obj;
   }





/**************************************************************************
 *
 * Associates a new TOS method with this TOS class. If a method with
 * the same name already existed then it is superceded by this new
 * method.
 *
 * @param methodName
 *    A symbol standing for the name of the method being defined.
 *
 * @param method
 *    A reference to the <TT>SObjFunction</TT> object that implements the
 *    TOS method.
 *
 **************************************************************************/

   public void addMethod(SObjSymbol   methodName,
                         SObjFunction method) {

      _methods.newVar(methodName, method);
   }





/**************************************************************************
 *
 * Associates a new TOS method with this TOS class.
 *
 * <P>The constructor for the object is a method with the same name as the
 * class.
 *
 * @param methodName
 *    A symbol standing for the name of the method being defined.
 *
 * @param method
 *    A reference to the <TT>SObjFunction</TT> object that implements the
 *    TOS method.
 *
 **************************************************************************/

   public void addMethod(String       methodName,
                         SObjFunction method) {

      addMethod(SObjSymbol.addSymbol(methodName), method);
   }





/**************************************************************************
 *
 * Associates a constructor with this TOS class.
 *
 * @param method A reference to the <code>{@link SObjFunction}</code>
 * object that implements the constructor.
 *
 **************************************************************************/

   public void addConstructor(SObjFunction method) {

      addMethod(_constructName, method);
      _constructor = method;
   }





/**************************************************************************
 *
 * Retrieves the <TT>SObjFunction</TT> object that implements the
 * method referenced by the symbol <TT>methodName</TT>. An exception
 * is thrown if that method has not been defined for this class.
 *
 * @return
 *    The <TT>SObjFunction</TT> object that implements the specified method.
 *
 * @exception com.pdmfc.tea.modules.tos.STosNoSuchMethodException
 *    Thrown if the method had not been defined for this class.
 *
 **************************************************************************/

   public SObjFunction getMethod(SObjSymbol methodName)
         throws SNoSuchMethodException {

      try {
         return (SObjFunction)_methods.getVar(methodName);
      } catch (SNoSuchVarException e) {
         throw new SNoSuchMethodException(methodName, _name);
      }
   }





/**************************************************************************
 *
 * Retrieves the <TT>STObjProc</TT> object that implements the constructor
 * for this TOS class.
 *
 * <P>The constructor is just a method with the same name as the TOS class.
 *
 * @return
 *    The <TT>STObjProc</TT> object that implements the TOS constructor.
 *    <TT>null</TT> if a constructor was not defined.
 *
 **************************************************************************/

   public SObjFunction getConstructor() {

      return _constructor;
   }





/**************************************************************************
 *
 * Associates a name with the class object. A name can be associated
 * to the class just for informational purposes. Currently this name
 * is only used in error messages generated by the TOS class
 * object. Note that there is no restriction on the contents of the
 * name. There may even be more than one class with the same name.
 *
 * @param name
 *        The name that will be associated with the class.
 *
 **************************************************************************/

    public void setName(String name) {

        _name = name;
    }





/**************************************************************************
 *
 * Fetches the name associated with the class. This is the string
 * passed to last call to the <code>setName<(String)</code> method.
 *
 * @return
 *        The name associated with the class.
 *
 **************************************************************************/

    public String getName() {

        if ( _name == null ) {
            _name = createName();
        }

        return _name;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static synchronized String createName() {

        return NAME_PREFIX + (_counter++);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private class SMethodSet
        extends SContext {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public SMethodSet(SMethodSet superClassMethods) {

            super(superClassMethods);
        }


    }

}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

