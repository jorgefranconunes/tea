/**************************************************************************
 *
 * Copyright (c) 2005 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: ReflectTest.java,v 1.4 2007/03/06 19:39:35 pcorreia Exp $
 *
 *
 * Revisions:
 *
 * $Log: ReflectTest.java,v $
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

public class ReflectTest {

    public static String  _CTE_STRING  = "This is a String Constant";
    public static Float   _CTE_FLOAT   = new Float(3.14);
    public static Integer _CTE_INTEGER = new Integer(234);
    public static List    _CTE_LIST    = new ArrayList();
    public static Map     _CTE_MAP     = new HashMap();
    public String _aString = "This is a member string";

    static {
	List subList = new ArrayList();
	_CTE_LIST.add("Hello");
	_CTE_LIST.add(new Boolean(true));
	_CTE_LIST.add(subList);
	_CTE_LIST.add(new Integer(345));
	subList.add("This is a subList");
	Map subHash = new HashMap();
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

   public ReflectTest() {
       _b = 0;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public ReflectTest(int b) {
       _b = b;
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public ReflectTest(double b) {
       _b = (int)(b+0.5);
   }








/**************************************************************************
 *
 *
 *
 **************************************************************************/

   public ReflectTest(String s) {
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

   public int adder(int a, ReflectTest c) {

       return _b+c.adder(a);
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public ReflectTest getAdder(int a, ReflectTest c) {

       return new ReflectTest(_b+c.adder(a));
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

       Method[] mtds = ReflectTest.class.getDeclaredMethods();

       for(int i=0;i<mtds.length; i++) {
	   System.out.println(mtds[i].getName()+":\n  Params:");
	   Class[] params=mtds[i].getParameterTypes();
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

       ReflectTest t = new ReflectTest(null);
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




