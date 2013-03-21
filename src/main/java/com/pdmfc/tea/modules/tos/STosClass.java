/**************************************************************************
 *
 * Copyright (c) 2001-2013 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.tos;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.tos.SList;
import com.pdmfc.tea.modules.tos.SNoSuchMethodException;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNoSuchVarException;





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
    private SList<SObjSymbol> _members;

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

    /**
     * The constructor method name.
     */
    public static final SObjSymbol CONSTRUCTOR_NAME =
        SObjSymbol.addSymbol("constructor");





/**************************************************************************
 *
 * Defines a new TOS class.
 *
 * @param superClass The base class of the class being defined. If it
 * is null it means the class being defined has no base class.
 *
 * @param members Array with the member names.
 *
 **************************************************************************/

    public STosClass(final STosClass         superClass,
                     final SList<SObjSymbol> members) {

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
 * @param members Array with the member names. It must be a
 * <code>Vector</code> of <code>SObjSymbol</code> objects.
 *
 **************************************************************************/

    public STosClass(final SList<SObjSymbol> members) {

        this(null, members);
    }





/**************************************************************************
 *
 * Defines a new TOS class with no members.
 *
 * @param superClass The base class of the class being defined. If it
 * is null it means the class being defined has no base class.
 *
 **************************************************************************/

    public STosClass(final STosClass superClass) {

        this(superClass, new SList<SObjSymbol>());
    }





/**************************************************************************
 *
 * Defines a new TOS class with no base class and with no members.
 *
 **************************************************************************/

    public STosClass() {

        this(null, new SList<SObjSymbol>());
    }





/**************************************************************************
 *
 * Retrieves the TOS base class of this TOS class.
 *
 * @return An <code>STosClass</code> object representing the TOS base
 * class of this TOS class. If it is <code>null</code> it means it has
 * no base class.
 *
 **************************************************************************/

    public final STosClass getSuperClass() {

        return _superClass;
    }





/**************************************************************************
 *
 * Retrieves the member names of this TOS class.
 *
 * @return An <code>Enumeration</code> that will iterate over all the
 * member names.  The <code>Enumeration</code> will always iterate
 * over the members the same way.
 *
 **************************************************************************/

    public final SList<SObjSymbol> memberNames() {

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

    public final int level() {

        return _level;
    }





/**************************************************************************
 *
 * Creates a new instance of an object of this TOS class, but does not
 * initialize the object.
 *
 * @return A new <code>STosObj</code> object, representing a new instance
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
 * @param args Array of argumens to be passed to the constructor. The
 * first element in the array is not used. The second element should
 * be a symbol with the class name of the object being instantiated.
 *
 * @return A new <code>STosObj</code> object, representing a new
 * instance of this TOS class.
 *
 **************************************************************************/

    public final STosObj newInstance(final SContext context,
                                     final Object[] args)
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
 * @param methodName A symbol standing for the name of the method
 * being defined.
 *
 * @param method A reference to the <code>SObjFunction</code> object
 * that implements the TOS method.
 *
 **************************************************************************/

    public final void addMethod(final SObjSymbol   methodName,
                                final SObjFunction method) {

        _methods.newVar(methodName, method);
    }





/**************************************************************************
 *
 * Associates a new TOS method with this TOS class.
 *
 * <p>The constructor for the object is a method with the same name as
 * the class.</p>
 *
 * @param methodName A symbol standing for the name of the method
 * being defined.
 *
 * @param method A reference to the <code>SObjFunction</code> object
 * that implements the TOS method.
 *
 **************************************************************************/

    public final void addMethod(final String       methodName,
                                final SObjFunction method) {

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

    public final void addConstructor(final SObjFunction method) {

        addMethod(CONSTRUCTOR_NAME, method);
        _constructor = method;
    }





/**************************************************************************
 *
 * Retrieves the <code>SObjFunction</code> object that implements the
 * method referenced by the symbol <code>methodName</code>. An
 * exception is thrown if that method has not been defined for this
 * class.
 *
 * @return The <code>SObjFunction</code> object that implements the
 * specified method.
 *
 * @exception com.pdmfc.tea.modules.tos.SNoSuchMethodException Thrown
 * if the method had not been defined for this class.
 *
 **************************************************************************/

    public final SObjFunction getMethod(final SObjSymbol methodName)
        throws SNoSuchMethodException {

        try {
            return (SObjFunction)_methods.getVar(methodName);
        } catch (SNoSuchVarException e) {
            throw new SNoSuchMethodException(methodName, _name);
        }
    }





/**************************************************************************
 *
 * Retrieves the <code>STObjProc</code> object that implements the
 * constructor for this TOS class.
 *
 * <p>The constructor is just a method with the same name as the TOS
 * class.</p>
 *
 * @return The <code>STObjProc</code> object that implements the TOS
 * constructor.  <code>null</code> if a constructor was not defined.
 *
 **************************************************************************/

    public final SObjFunction getConstructor() {

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
 * @param name The name that will be associated with the class.
 *
 **************************************************************************/

    public final void setName(final String name) {

        _name = name;
    }





/**************************************************************************
 *
 * Fetches the name associated with the class. This is the string
 * passed to last call to the <code>setName(String)</code> method.
 *
 * @return The name associated with the class.
 *
 **************************************************************************/

    public final String getName() {

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

    private static final class SMethodSet
        extends SContext {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public SMethodSet(final SMethodSet superClassMethods) {

            super(superClassMethods);
        }


    }

}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

