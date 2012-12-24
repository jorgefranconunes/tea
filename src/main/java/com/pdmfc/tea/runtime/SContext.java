/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SObjVar;
import com.pdmfc.tea.runtime.SNoSuchVarException;





/**************************************************************************
 *
 * Represents a context inside which variables are created and
 * commands executed. Contexts form an hierarchy, where child contexts
 * hide variables defined in parent contexts. A context with no parent
 * is a toplevel context.
 *
 **************************************************************************/

public class SContext
    extends Object {





    private SContext _parent = null;

    // The buckets in the hash table.
    private SVarSet[] _varTable = null;

    // The number of variables currently defined in this context.
    private int _varCount = 0;

    // The hash table threshold. When the number of entries (Tea
    // variables) in the hash table grows above this threshold then
    // the hash table is rehashed with a larger number of buckets.
    private int _hashThreshold = -1;

    // The load factor should always be positive.
    private static final float HASH_LOAD_FACTOR = 0.75f;

    private static final int INITIAL_CAPACITY = 21;





/**************************************************************************
 *
 * Initializes a top-level context.
 *
 **************************************************************************/

    public SContext() {

        _parent = null;
    }





/**************************************************************************
 *
 * Initializes a context descending from
 * <code>parent</code>. Variables defined inside this context hide
 * variables with the same name inside <code>context</code>. If
 * <code>parent</code> is <code>null</code> then this will be a
 * top-level context.
 *
 * @param parent The parent context of this one. It will be
 * <code>null</code> in the case of a top level context.
 *
 **************************************************************************/

    protected SContext(final SContext parent) {

        _parent = parent;
    }





/**************************************************************************
 *
 * Creates a new context having this context for parent.
 *
 * @return A newly created context.
 *
 **************************************************************************/

    public final SContext newChild() {

        SContext result = new SContext(this);

        return result;
    }
   




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public final SContext clone(final SContext parent) {

        SContext result = new SContext(parent);

        result._varTable      = _varTable;
        result._varCount      = _varCount;
        result._hashThreshold = _hashThreshold;

        return result;
    }





/**************************************************************************
 *
 * Retrives the parent context. In the case of a toplevel context the
 * parent context is not set.
 *
 * @return The parent context. It will be null in the case of a
 * toplevel context.
 *
 **************************************************************************/

    public final SContext getParent() {

        return _parent;
    }





/**************************************************************************
 *
 * Initializes the hashtable used to store the variables.
 *
 **************************************************************************/

    private void initHashtable() {

        _varTable      = new SVarSet[INITIAL_CAPACITY];
        _hashThreshold = (int)(INITIAL_CAPACITY * HASH_LOAD_FACTOR);
    }





/**************************************************************************
 *
 * Forgets all the variables.
 *
 **************************************************************************/

    protected final void clearAll() {

        _varTable      = null;
        _hashThreshold = 0;
    }





/**************************************************************************
 *
 * Checks if a given variable is defined in this context. The variable
 * is also searched in all parent contexts.
 *
 * @param name The symbol associated with the variable being checked
 * for existence.
 *
 * @return True if the variable is defined in this context or in some
 * of its parent contexts. False otherwise.
 *
 **************************************************************************/

    public final boolean isDefined(final SObjSymbol name) {

        if ( _varTable == null ) {
            if (_parent != null) {
                return _parent.isDefined(name);
            } else {
                return false;
            }
        }
      
        // Search for a variable with that name.
        SVarSet[] table = _varTable;
        int       hash  = name.hashCode();
        int       index = (hash & 0x7FFFFFFF) % table.length;
        
        for ( SVarSet entry=table[index]; entry!=null; entry=entry._next ) {
            if ( entry._symbolHash == hash ) {
                // We found it!
                return true;
            }
        }

        // There is no variable with that name in this context.
        // So search in the parent context.
        if ( _parent != null) {
            return _parent.isDefined(name);
        } else {
            return false;
        }
   }





/**************************************************************************
 *
 * Creates a new variable inside this context. The variable will be
 * associated with a symbol named <code>name</code> and it will have
 * has contents the object referenced by <code>value</code>.
 *
 * @param name The name of the symbol to be associated to the variable
 * being created.
 *
 * @param value Reference to the object to be stored inside the
 * variable.
 *
 **************************************************************************/

    public final SObjVar newVar(final String name,
                                final Object value) {

        return newVar(SObjSymbol.addSymbol(name), value);
    }





/**************************************************************************
 *
 * Creates a new variable inside this context. The variable will be
 * associated with symbol <code>name</code> and it will have has
 * contents the object referenced by <code>value</code>.
 *
 * @param name Symbol to be associated to the variable being created.
 *
 **************************************************************************/

    public final SObjVar newVar(final SObjSymbol name,
                                final Object     value) {

        if ( _varTable == null ) {
            initHashtable();
        }
      
        // First check if there is already a variable with that name.
        SVarSet[] table = _varTable;
        int       hash  = name.hashCode();
        int       index = (hash & 0x7FFFFFFF) % table.length;

        for ( SVarSet entry=table[index]; entry!=null; entry=entry._next ){
            if ( entry._symbolHash == hash ) {
                // There was already a variable with the same name:
                entry._value = value;
                return entry;
            }
        }

        // There is no variable with that name, so create the new entry:
        SVarSet entry = new SVarSet();

        entry._symbolHash = hash;
        entry._value      = value;
        entry._next       = table[index];
        table[index]      = entry;
        _varCount++;

        // Rehash the table if the threshold is exceeded.
        if ( _varCount > _hashThreshold ) {
            rehashHashtable();
        }

        return entry;
    }





/**************************************************************************
 *
 * Rehashes the contents of the table into a bigger one. This method is
 * invoked when the hashtable size exceeds its threshold.
 *
 **************************************************************************/

    private void rehashHashtable() {

        int     prevCapacity = _varTable.length;
        SVarSet prevTable[]  = _varTable;
        int     newCapacity  = prevCapacity * 2 + 1;
        SVarSet newTable[]   = new SVarSet[newCapacity];

        _hashThreshold = (int)(newCapacity * HASH_LOAD_FACTOR);
        _varTable      = newTable;

        for ( int i=prevCapacity; i-->0; ) {
            for ( SVarSet prev=prevTable[i]; prev!=null; ) {
                SVarSet entry = prev;
                int     index = (entry._symbolHash & 0x7FFFFFFF) % newCapacity;

                prev            = prev._next;
                entry._next     = newTable[index];
                newTable[index] = entry;
            }
        }
    }





/**************************************************************************
 *
 * Sets the contents of a variable previously created. After this
 * method returns variable <code>name</code> will have as contents a
 * reference to object <code>value</code>.
 *
 * @param name Symbol associated with the variable to be set.
 *
 * @param value Object to store in the variable.
 *
 * @exception com.pdmfc.tea.runtime.SNoSuchVarException Throw if no
 * variable </code>name</code> was previously created.
 *
 **************************************************************************/

    public final void setVar(final SObjSymbol name,
                             final Object     value) 
        throws SNoSuchVarException {

        if ( _varTable == null ) {
            if (_parent != null) {
                _parent.setVar(name, value);
                return;
            } else {
                throw new SNoSuchVarException(name);
            }
        }
      
        // Search for a variable with that name.
        SVarSet[] table = _varTable;
        int       hash  = name.hashCode();
        int       index = (hash & 0x7FFFFFFF) % table.length;
        
        for ( SVarSet entry=table[index]; entry!=null; entry=entry._next ) {
            if ( entry._symbolHash == hash ) {
                // We found it!
                entry._value = value;
                return;
            }
        }

        // There is no variable with that name in this context.
        // So search in the parent context.
        if ( _parent != null) {
            _parent.setVar(name, value);
        } else {
            throw new SNoSuchVarException(name);
        }
   }





/**************************************************************************
 *
 * Obtains the contents of the variable associated with symbol
 * <code>name</code>.  If such a variable does not exist in this
 * context then it is searched in its parent.
 *
 * @param name Symbol associated with the variable whose contents are
 * to be retrieved.
 *
 * @return The object in the variable associated with symbol
 * <code>name</code>.
 *
 * @exception SNoSuchVarException Thrown if the variable does not
 * exist neither in this context nor in any of the contexts up the
 * hierarchy.
 *
 **************************************************************************/

   public final Object getVar(final SObjSymbol name) 
         throws SNoSuchVarException {

        if ( _varTable == null ) {
            if ( _parent != null ) {
                return _parent.getVar(name);
            } else {
                throw new SNoSuchVarException(name);
            }
        }
      
        // Search for a variable with that name.
        SVarSet[] table = _varTable;
        int       hash  = name.hashCode();
        int       index = (hash & 0x7FFFFFFF) % table.length;
        
        for ( SVarSet entry=table[index]; entry!=null; entry=entry._next ) {
            if ( entry._symbolHash == hash ) {
                // We found it!
                return entry._value;
            }
        }

        // There is no variable with that name in this context.
        // So search in the parent context.
        if ( _parent != null ) {
            return _parent.getVar(name);
        } else {
            throw new SNoSuchVarException(name);
        }
   }





/**************************************************************************
 *
 * Obtains the contents of the variable associated with symbol
 * <code>name</code>.  If such a variable does not exist in this
 * context then it is searched in its parent.
 *
 * @param name Symbol associated with the variable whose contents are
 * to be retrieved.
 *
 * @return The object in the variable associated with symbol
 * <code>name</code>.
 *
 * @exception SNoSuchVarException Thrown if the variable does not
 * exist neither in this context nor in any of the contexts up the
 * hierarchy.
 *
 **************************************************************************/

    public final SObjVar getVarObject(final SObjSymbol name) 
        throws SNoSuchVarException {

        SObjVar result = getVarObjectIfPossible(name);

        if ( result == null ) {
            throw new SNoSuchVarException(name);
        }

        return result;
   }





/**************************************************************************
 *
 * Fetches the container of a variable. If such a variable does not
 * exist in this context then it is searched in its parent.
 *
 * @param name Symbol associated with the variable whose contents are
 * to be retrieved.
 *
 * @return The variable container associated with symbol
 * <code>name</code> or null if no Tea variable with that name is
 * defined.
 *
 **************************************************************************/

    public final SObjVar getVarObjectIfPossible(final SObjSymbol name)  {

        SObjVar result = null;

        if ( _varTable == null ) {
            if ( _parent != null ) {
                result = _parent.getVarObjectIfPossible(name);
            }
        } else {
            // Search for a variable with that name.
            SVarSet[] table = _varTable;
            int       hash  = name.hashCode();
            int       index = (hash & 0x7FFFFFFF) % table.length;
            
            for ( SVarSet entry=table[index]; entry!=null; entry=entry._next ){
                if ( entry._symbolHash == hash ) {
                    // We found it!
                    result = entry;
                    break;
                }
            }

            if ( result == null ) {
                // There is no variable with that name in this
                // context.  So search in the parent context.
                if ( _parent != null ) {
                    result = _parent.getVarObjectIfPossible(name);
                }
            }
        }

        return result;
   }





/**************************************************************************
 *
 * This is an entry in the hashtable collision list.
 *
 **************************************************************************/

    private static final class SVarSet
        extends Object
        implements SObjVar {

        // The hash value of the symbol object this variable is
        // associated with.
        private int     _symbolHash;

        // The contents of the variable.
        private Object  _value;
        
        // The next entry in this set.
        private SVarSet _next;




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public void set(final Object newValue) {

            _value = newValue;
        }




/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public Object get() {

            return _value;
        }


    }
    

}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

