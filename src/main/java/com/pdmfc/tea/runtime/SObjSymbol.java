/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.util.HashMap;
import java.util.Map;





/**************************************************************************
 *
 * Instances of this class represent a symbol in a Tea program. This
 * class is also manages all the existing symbols in a Tea program.
 *
 **************************************************************************/

public final class SObjSymbol
    extends Object {





    // The name of this symbol
    private String _name;

    // The hashcode of this symbol. It was calculated from its name.
    private int    _hashCode;

    // The set of all existing symbols.
    private static Map<String,SObjSymbol> _nameSet =
        new HashMap<String,SObjSymbol>();

    // The number of declared symbols. Used to generate hash codes. */
    private static int _symbolCount = 0;





/**************************************************************************
 *
 * @param name The name of the symbol. It is supposed to be unique.
 *
 * @param hash The hash code of this object. It is supposed to be
 * unique.
 *
 **************************************************************************/

    private SObjSymbol(final String name,
                       final int    hash) {

        _name     = name;
        _hashCode = hash;
    }





/**************************************************************************
 *
 * The hash code of this object. It is set when the object is
 * initialized.  There is no nee to reimplement the
 * <code>equals()</code> method because there are no to
 * <code>SObjSymbol</code> instances with the same name, so comparison
 * for equality can be made on an object basis.
 *
 * @return The hash code of this object.
 *
 **************************************************************************/

    public int hashCode() {

        return _hashCode;
    }





/**************************************************************************
 *
 * Fetches the name of this symbol.
 *
 * @return A <code>String</code> with the name of this symbol.
 *
 **************************************************************************/

    public String getName() {

        return _name;
    }






/**************************************************************************
 *
 * Retrieves a string representation for this object. This will be the
 * name for this symbol.
 *
 * @return A string representing the name of this symbol.
 *
 **************************************************************************/

    public String toString() {

        return getName();
    }





/**************************************************************************
 *
 * Finds the object associated with the symbol named
 * <code>name</code>. If there is no object with that name in the
 * symbol table, a <code>null</code> object is returned.
 *
 * @param name The name of the symbol to search for.
 *
 * @return Reference to the object representing the symbol, or
 * <code>null</code> if the symbol was not on the symbol table.
 *
 **************************************************************************/

    public static synchronized SObjSymbol getSymbol(final String name) {

        return _nameSet.get(name);
    }





/**************************************************************************
 *
 * Adds a new symbol to the symbol table and returns the associated
 * object.  There is only one object for each symbol.
 *
 * @param name String with the name of the symbol to be added to the
 * symbol table.
 *
 * @return Reference to the object representing the symbol.
 *
 **************************************************************************/

    public static synchronized SObjSymbol addSymbol(final String name) {

        SObjSymbol symbol = _nameSet.get(name);

        if ( symbol == null ) {
            symbol = new SObjSymbol(name, _symbolCount++);
            _nameSet.put(name, symbol);
        }

        return symbol;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

