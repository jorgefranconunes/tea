/**************************************************************************
 *
 * Copyright (c) 2005-2010 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $HeadURL$
 * $Id$
 *
 *
 * Revisions:
 *
 * 2010-10-10 jpsl TSK-PDMFC-TEA-034 Renamed to
 * src/test/com/pdmfc/tea/modules/refelect/SModuleReflectTestTopClass.java
 * while rewriting SModuleReflect Tea tests as JUnit tests.
 *
 * Revision 1.4  2007/03/06 19:39:35  pcorreia
 * TSK-PDMFC-TEA-0038 - Added support for array returning methods.
 *
 * Revision 1.3  2006/01/28 16:37:07  pcorreia
 * Better wrapping/unwrapping of objects as parameters to methods
 *
 * Revision 1.2  2006/01/18 00:36:50  pcorreia
 * Support for setting member objects. Support to get/set members on instances.
 *
 * Revision 1.1  2005/12/05 20:37:53  pcorreia
 * Support for reflection over Java
 *
 *
 **************************************************************************/

package com.pdmfc.tea.modules.reflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




/**************************************************************************
 *
 * Test class for reflection.
 *
 **************************************************************************/

public class SModuleReflectTestTopClass {

    // SModuleReflectTest depends on the values and data structures in this class

    public static String              _CTE_STRING  = "This is a String Constant";
    public static Float               _CTE_FLOAT   = new Float(3.14);
    public static Integer             _CTE_INTEGER = new Integer(234);
    public static List<Object>        _CTE_LIST    = new ArrayList<Object>();
    public static Map<Object, Object> _CTE_MAP     = new HashMap<Object,Object>();
    public String _aString = "This is a member string";

    static {
	List<Object> subList = new ArrayList<Object>();
	_CTE_LIST.add("Hello");
	_CTE_LIST.add(new Boolean(true));
	_CTE_LIST.add(subList);
	_CTE_LIST.add(new Integer(345));
	subList.add("This is a subList");
	Map<Object,Object> subHash = new HashMap<Object,Object>();
	_CTE_MAP.put("A sub hash", subHash);
	_CTE_MAP.put("A list", _CTE_LIST);
	_CTE_MAP.put("A float", _CTE_FLOAT);
	subHash.put("A String", _CTE_STRING);
    }

    private int    _b = 0;
    private String _s = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SModuleReflectTestTopClass() {
       _b = 0;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SModuleReflectTestTopClass(int b) {
       _b = b;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SModuleReflectTestTopClass(double b) {
       _b = (int)(b+0.5);
   }








/**************************************************************************
 *
 *
 *
 **************************************************************************/

   public SModuleReflectTestTopClass(String s) {
       _s = s;
   }





/**************************************************************************
 *
 *
 *
 **************************************************************************/

   public String adder(String s) {

       if (_s != null) {
           return _s + "+" + s;
       } else {
           return _b + "+" + s;
       }
   }





/**************************************************************************
 *
 *
 *
 **************************************************************************/

   public String adder(String s2, String s3) {

       if (_s != null) {
           return _s + "+" + s2 + "+" + s3;
       } else {
           return _b + "+" + s2 + "+" + s3;
       }
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public int adder(int a) {

       return a+_b;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public int adder(int a, double b) {

       return a+_b+(int)b;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public int adder(int a, SModuleReflectTestTopClass c) {

       return _b+c.adder(a);
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SModuleReflectTestTopClass getAdder(int a, SModuleReflectTestTopClass c) {

       return new SModuleReflectTestTopClass(_b+c.adder(a));
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public static int adder(int a, int b) {

       return a+b;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public static int adder(Integer a, Integer b) {

       return a.intValue()+b.intValue()+1;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public static float adder(Float a, Float b) {

       return a.floatValue()+b.floatValue()+2;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public static long adder(long a, long b) {
       return a+b+3;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public static String concat(String a, String b) {

       return a+b;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public static String[] getArray(String a, String b) {

       String[] result = new String[2];

       result[0] = a;
       result[1] = b;

       return result;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public static int[] getArray(int a, int b) {

       int[] result = new int[2];

       result[0] = a;
       result[1] = b;

       return result;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public static void main(String[] args) {

       Method[] mtds = SModuleReflectTestTopClass.class.getDeclaredMethods();

       for(int i=0;i<mtds.length; i++) {
	   System.out.println(mtds[i].getName()+":\n  Params:");
	   Class<?>[] params=mtds[i].getParameterTypes();
	   for(int j=0;j<params.length; j++) {
	       System.out.println("    "+params[j].getName());
	   }
	   System.out.println("  Returns:\n    "+mtds[i].getReturnType().getName());
       }

 
       System.out.println("Calling getArray(String,null)");
       String[] a = getArray("s1",null);
       int i=0;
       for(Object o : a) {
           System.out.println("  ["+(i++)+"]="+o);
       }

       SModuleReflectTestTopClass t = new SModuleReflectTestTopClass(null);
       System.out.println("RefelectTest(null).adder(null,\"s2\")= "+t.adder(null,"s2"));
     
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

}




