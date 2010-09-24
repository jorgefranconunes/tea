/**************************************************************************
 *
 * Copyright (c) 2007 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $HeadURL$
 * $Id$
 *
 * Revisions:
 *
 * 2006/12/24 Created. (jpsl)
 *
 **************************************************************************/

package com.pdmfc.tea.engine;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.script.*;

import com.pdmfc.tea.SConfigInfo;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjVar;
import com.pdmfc.tea.runtime.STeaRuntime;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SNoSuchVarException;

/**
 * The javax.script.Bindings interface allows Java code to access
 * (get, set, remove, list, etc...) variable bindings for Tea code.
 * In Tea, this is implemented in the base class
 * com.pdmfc.tea.runtime.SContext, com.pdmfc.tea.runtime.STeaRuntime.
 * <p>Up to Tea 3.2.1, SContext is optimized for speed of execution, and does
 * not support unsetting (undefining) variables or listing 
 * variables. As such, methods related to these features throw the
 * java.lang.UnsupportedOperationException.<p>
 */
public class TeaBindings implements Bindings {
    /**
     * The top level runtime SContext. (Please see Tea javadoc
     * for actual details.)
     */
    protected STeaRuntime _runtime;

    /**
     * List of directories (or resource URLs) to be used as import
     * directories. It will be initialized by default from the Tea
     * core configuration, but it can be manipulated using the
     * getImportDirList() method.
     */
    protected List _importDirList = new ArrayList();

    /**
     * _runtime will be initialized on 1st execution attempt, as we
     * are not allowed to throw a ScriptException here.
     * _importDirList is initialized from Tea core configuration.
     * @see #getImportDirList()
     */
    public TeaBindings() {
        _runtime = null;

        // import dirs
        String importDirs = SConfigInfo.getProperty("com.pdmfc.tea.coreImportDir");
        //URL importDirsUrl = STeaLauncher.class.getResource(importDirs);
        _importDirList.add(importDirs);
    }

    /**
     * Unsupported.
     * @throws java.lang.UnsupportedOperationException
     */
    public void clear() {
        throw new UnsupportedOperationException("TeaBindings.clear() unsupported. Tea does not allow to undefine variables!");
    }

    public boolean containsKey(Object key) {
        try {
            String sKey = (String)key;
            SContext context = getMyRuntime().getToplevelContext();
            SObjSymbol nameSymbol = SObjSymbol.addSymbol(sKey);
            return context.isDefined(nameSymbol);
        } catch (ScriptException e) {
            throw new NullPointerException(e.getMessage());
        }
    }

    /**
     * Unsupported.
     * @throws java.lang.UnsupportedOperationException
     */
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("TeaBindings.containsValue() unsupported. Tea does not allow to undefine variables!");
    }

    /**
     * Unsupported.
     * @throws java.lang.UnsupportedOperationException
     */
    public Set<Map.Entry<String,Object>> entrySet() {
        throw new UnsupportedOperationException("TeaBinding.entrySet(). Tea contexts do not support reflection!");
    }

    /**
     * Unsupported.
     * @throws java.lang.UnsupportedOperationException
     */
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("TeaBindings.equals() unsupported. Tea contexts do not support reflection!");
    }

    /**
     * Unsupported.
     * @throws java.lang.UnsupportedOperationException
     */
    public Object get(Object key) {
        try {
            String sKey = (String)key;
            SContext context = getMyRuntime().getToplevelContext();
            SObjSymbol nameSymbol = SObjSymbol.addSymbol(sKey);
            return context.getVar(nameSymbol);
        } catch (SNoSuchVarException e) {
            return null;
        } catch (ScriptException e) {
            throw new NullPointerException(e.getMessage());
        }
    }

    /**
     * Unsupported.
     * @throws java.lang.UnsupportedOperationException
     */
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("TeaBinding.entrySet() unsupported. Tea contexts do not support reflection!");
    }

    /**
     * Unsupported.
     * @throws java.lang.UnsupportedOperationException
     */
    public boolean isEmpty() {
        throw new UnsupportedOperationException("TeaBindings.isEmpty() unsupported. Tea does not allow to undefine variables!");
    }

    /**
     * Unsupported.
     * @throws java.lang.UnsupportedOperationException
     */
    public Set<String> keySet() {
        throw new UnsupportedOperationException("TeaBinding.keySet() unsupported. Tea contexts do not support reflection!");
    }

    public Object put(String key, Object value) {
        SObjSymbol keySym;
        try {
            String sKey = key;
            SContext context = getMyRuntime().getToplevelContext();
            keySym = SObjSymbol.addSymbol(sKey);
            try {
                SObjVar v = context.getVarObject(keySym);
                Object previous = v.get();
                v.set(value);
                return previous;
            } catch (SNoSuchVarException e) {
                context.newVar(keySym, value);
                return null;
            }
        } catch (ScriptException e) {
            throw new NullPointerException(e.getMessage());
        }
    }

    public void putAll(Map<? extends String,? extends Object> toMerge) {
        for(Iterator i=toMerge.keySet().iterator(); i.hasNext(); ) {
            String key = (String)i.next();
            put(key, toMerge.get(key));
        }
    }

    /**
     * Unsupported.
     * @throws java.lang.UnsupportedOperationException
     */
    public Object remove(Object key) {
        throw new UnsupportedOperationException("TeaBindings.remove(Object key) unsupported. Tea does not allow to undefine variables!");
        //try {
        //    String sKey = (String)key;
        //    STeaRuntime context = getMyRuntime();
        //    SObjSymbol nameSymbol = SObjSymbol.addSymbol(sKey);
        //    return context.getVar(nameSymbol);
        //} catch (SNoSuchVarException e) {
        //    return null;
        //} catch (ScriptException e) {
        //    throw new NullPointerException(e.getMessage());
        //}
    }

    /**
     * Unsupported.
     * @throws java.lang.UnsupportedOperationException
     */
    public int size() {
        throw new UnsupportedOperationException("TeaBindings.size() unsupported. Tea contexts do not support reflection!");
    }

    /**
     * Unsupported.
     * @throws java.lang.UnsupportedOperationException
     */
    public Collection<Object> values() {
        throw new UnsupportedOperationException("TeaBindings.values() unsupported. Tea contexts do not support reflection!");
    }

    /**
     * Gets the STeaRuntime context. Initializes it for the 1st time,
     * if needed.
     */
    public synchronized STeaRuntime getMyRuntime() throws ScriptException {
        // on the 1st tine instantiate an STeaRuntime
        if (_runtime == null) {
            //try {
                _runtime = new STeaRuntime();
                _runtime.setImportLocations(_importDirList);
                // TODO: setup import paths and readers/writers from other
                // properties ?
                // context.start() ? No ? In TeaCompiledScript.eval ?
                // context.stop(); - put where ?
                // context.end(); - put where ?
            //} catch (STeaException e) {
            //    throw new ScriptException(e);
            //}
        }
        return _runtime;    
    }

    /**
     * @return _importDirList.
     * This is the string list of directories/resource URLs 
     * that Tea will search to import a file.
     * At the present, _importDirList is initialized in the
     * constructor with the Tea core dir. When the 1st script is run,
     * the Tea interpreter will search for an init.tea file in every
     * directory, and execute it.
     */
    public List getImportDirList() {
        return _importDirList;
    }
}
