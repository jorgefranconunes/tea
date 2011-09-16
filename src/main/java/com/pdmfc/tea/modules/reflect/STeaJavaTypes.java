/**************************************************************************
 *
 * Copyright (c) 2005-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.reflect;

import java.lang.reflect.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.reflect.JavaWrapperObject;
import com.pdmfc.tea.modules.tdbc.SCallableStatement;
import com.pdmfc.tea.modules.tdbc.SConnection;
import com.pdmfc.tea.modules.tdbc.SPreparedStatement;
import com.pdmfc.tea.modules.tdbc.SResultSet;
import com.pdmfc.tea.modules.tdbc.SStatement;
import com.pdmfc.tea.modules.tos.STosObj;
import com.pdmfc.tea.modules.util.SDate;
import com.pdmfc.tea.modules.util.SHashtable;
import com.pdmfc.tea.modules.util.SVector;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SLambdaFunction;
import com.pdmfc.tea.runtime.SObjBlock;
import com.pdmfc.tea.runtime.SObjByteArray;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjNull;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;





/**************************************************************************
 *
 * Utility methods to convert between Tea and Java types.
 *
 **************************************************************************/

public final class STeaJavaTypes
    extends Object {





/**************************************************************************
 *
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private STeaJavaTypes() {

        // Nothing to do.
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    public static Object java2Tea(final Object   obj,
                                  final SContext context)
        throws STeaException {
        
        if ( null == obj ) {
            return SObjNull.NULL;
        }
        if ( obj instanceof Date ) {
            try {
                SDate teaObj = SDate.newInstance(context);
                teaObj.initFromDate((Date)obj);
                return teaObj;
            } catch (STeaException e) {
                throw new STeaException(e);
            }
        }
        if ( obj instanceof Float ) {
            return new Double(((Float)obj).doubleValue());
        }
        if ( obj instanceof Map ) {
            return javaMap2Tea((Map<Object,Object>)obj, context);
        }
        if ( obj instanceof List ) {
            return javaList2Tea((List<Object>)obj, context);
        }
        if ( obj.getClass().isArray() ) {
            return javaArray2Tea(obj, context);
        }
        if ( obj instanceof Connection ) {
            return SConnection.newInstance(context, (Connection)obj);
        }
        if ( obj instanceof CallableStatement ) {
            SCallableStatement teaObj =
                (SCallableStatement)SCallableStatement.newInstance(context);
            teaObj.setCallableStatement((CallableStatement) obj);
            return teaObj;
        }
        if ( obj instanceof PreparedStatement ) {
            SPreparedStatement teaObj =
                (SPreparedStatement)SPreparedStatement.newInstance(context);
            teaObj.setPreparedStatement((PreparedStatement) obj);
            return teaObj;
        }
        if ( obj instanceof Statement ) {
            SStatement teaObj = SStatement.newInstance(context);
            teaObj.setStatement((Statement) obj);
            return teaObj;
        }
        if ( obj instanceof ResultSet ) {
            SResultSet teaObj = SResultSet.newInstance(context);
            try {
                teaObj.setResultSet((ResultSet) obj);
            } catch (SQLException e) {
                throw new SRuntimeException(e);
            }
            return teaObj;
        }
        if ( obj instanceof Double ) {
            return obj;
        }
        if ( obj instanceof Integer ) {
            return obj;
        }
        if ( obj instanceof Long ) {
            return obj;
        }
        if ( obj instanceof String ) {
            return obj;
        }
        if ( obj instanceof Boolean ) {
            return obj;
        }
        // TSK-PDMFC-TEA-0034 Tea engine - do not re-wrap a Tea runtime object
        // It can happen, if by intermixed java/Tea programming, a Tea value
        // object is inadvertly converted twice by java2Tea.
        if (obj instanceof STosObj
                || obj instanceof SLambdaFunction
                || obj instanceof SObjBlock
                || obj instanceof SObjByteArray
                || obj instanceof SObjFunction
                || obj instanceof SObjNull
                || obj instanceof SObjPair
                || obj instanceof SObjSymbol
                || obj instanceof JavaWrapperObject) {
            return obj;
        }

        // Wrap the object to a tea wrapped obj
        return new JavaWrapperObject(obj);
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    private static SHashtable javaMap2Tea(final Map<Object,Object> map,
                                          final SContext           context)
        throws STeaException {

        SHashtable         teaObj = null;
        Map<Object,Object> teaMap = null;

        try {
            teaObj = SHashtable.newInstance(context);
            teaMap = teaObj.getInternalMap();
        } catch (STeaException e) {
            throw new STeaException(e);
        }
        
        for ( Map.Entry<Object,Object> entry : map.entrySet() ) {
            Object key      = entry.getKey();
            Object value    = entry.getValue();
            Object teaValue = java2Tea(value, context);
            
            teaMap.put(key, teaValue);
        }
        

        return teaObj;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/
    
    private static SObjPair javaList2Tea(final List<Object> list,
                                         final SContext     context)
        throws STeaException {

        int      size = list.size();
        SObjPair head = SObjPair.emptyList();

        for ( int i=size; (i--)>0; ) {
            Object value    = list.get(i);
            Object teaValue = java2Tea(value, context);
            
            head = new SObjPair(teaValue, head);
        }

        return head;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/
    
    private static SObjPair javaArray2Tea(final Object   anArrayObj,
                                          final SContext context)
        throws STeaException {

        int      size = Array.getLength(anArrayObj);
        SObjPair head = SObjPair.emptyList();

        for ( int i=size; (i--)>0; ) {
            Object value    = Array.get(anArrayObj, i);
            Object teaValue = java2Tea(value, context);
            
            head = new SObjPair(teaValue, head);
        }

        return head;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    public static Object tea2Java(final Object obj)
        throws STeaException {

        if ( obj == SObjNull.NULL ) {
            return null;
        }

        if ( obj instanceof String ) {
            return obj;
        }
        if ( obj instanceof Integer ) {
            return obj;
        }
        if ( obj instanceof Long ) {
            return obj;
        }
        if ( obj instanceof Double ) {
            return obj;
        }
        if  ( obj instanceof Boolean ) {
            return obj;
        }
        if  ( obj instanceof SObjSymbol ) {
            return ((SObjSymbol)obj).getName();
        }
        if ( obj instanceof SObjPair ) {
            return teaList2List((SObjPair)obj);
        }
        if ( obj instanceof JavaWrapperObject ) {
            return ((JavaWrapperObject)obj).getWrappedObject();
        }
        if ( (obj instanceof STosObj)
             && (((STosObj)obj).part(0) instanceof SHashtable) ) {
            return teaMap2Map((SHashtable)((STosObj)obj).part(0));
        }
        if ( (obj instanceof STosObj)
             && (((STosObj)obj).part(0) instanceof SDate) ) {
            return ((SDate)((STosObj)obj).part(0)).getDate();
        }
        if ( (obj instanceof STosObj)
             && (((STosObj)obj).part(0) instanceof SConnection) ) {
            return ((SConnection)((STosObj)obj)).getInternalConnection();
        }
        if ( (obj instanceof STosObj)
             && (((STosObj)obj).part(0) instanceof SStatement) ) {
            return ((SStatement)((STosObj)obj)).getInternalStatement();
        }
        if ( (obj instanceof STosObj)
             && (((STosObj)obj).part(0) instanceof SResultSet) ) {
            return ((SResultSet)((STosObj)obj)).getInternalResultSet();
        }
        if ( (obj instanceof STosObj)
             && (((STosObj)obj).part(0) instanceof SVector) ) {
            return ((SVector)((STosObj)obj)).getInternalList();
        }

        return obj;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/
    
    private static List teaList2List(final SObjPair head)
        throws STeaException {
        
        List<Object> list = new ArrayList<Object>();
        
        for ( Object teaValue : head ) {
            Object iteaValue = tea2Java(teaValue);
            
            list.add(iteaValue);
        }
        
        return list;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    private static Map teaMap2Map(final SHashtable hashtable)
        throws STeaException {
        
        Map<Object,Object>  teaMap = hashtable.getInternalMap();
        Map<Object, Object> map    = new HashMap<Object, Object>();
        
        for ( Map.Entry<Object,Object> entry : teaMap.entrySet() ) {
            Object    key     = tea2Java(entry.getKey());
            Object    wsValue = tea2Java(entry.getValue());
            
            map.put(key, wsValue);
        }
        
        return map;
    }

    


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

