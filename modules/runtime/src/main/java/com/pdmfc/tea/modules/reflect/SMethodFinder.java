/**************************************************************************
 *
 * Copyright (c) 2005-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import com.pdmfc.tea.runtime.SRuntimeException;





/***************************************************************************
 *
 * Provides utility method for retrieving methods from a class given
 * its name and possible signature.
 *
 ***************************************************************************/

final class SMethodFinder
    extends Object {





    private static Map<Class,Class<?>> _primitiveToClass =
        new HashMap<Class,Class<?>>();





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    static {
        _primitiveToClass.put(Boolean.TYPE,   Boolean.class);
        _primitiveToClass.put(Character.TYPE, Character.class);
        _primitiveToClass.put(Byte.TYPE,      Byte.class);
        _primitiveToClass.put(Short.TYPE,     Short.class);
        _primitiveToClass.put(Integer.TYPE,   Integer.class);
        _primitiveToClass.put(Long.TYPE,      Long.class);
        _primitiveToClass.put(Float.TYPE,     Float.class);
        _primitiveToClass.put(Double.TYPE,    Double.class);
        _primitiveToClass.put(Void.TYPE,      Void.class);
    }





/***************************************************************************
 *
 * No instances of this class are to be created.
 *
 ***************************************************************************/

    private SMethodFinder() {

        // Nothing to do.
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/
    
    public static Constructor<?> findConstructor(final Class<?>   klass,
                                                 final Class<?>[] paramTypes)
        throws SRuntimeException {

        Constructor<?> result = null;

        try {
            result = doFindConstructor(klass, paramTypes);
        } catch (NoSuchMethodException e) {
            String paramsTxt = buildTypesDescription(paramTypes);
            throw new SRuntimeException("could not find constructor for '"
                                        + klass.getName()
                                        + "(" + paramsTxt + ")'");
        }
        
        return result;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/
    
    public static String buildTypesDescription(final Class<?>[] paramTypes) {

        StringBuilder builder = new StringBuilder();
        boolean       isFirst = true;

        for ( Class<?> klass : paramTypes ) {
            String typeDescription = (klass==null) ? "null" : klass.getName();

            if ( isFirst ) {
                isFirst = false;
            } else {
                builder.append(",");
            }

            builder.append(typeDescription);
        }

        String result = builder.toString();

        return result;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/
    
    private static Constructor<?> doFindConstructor(final Class<?>   klass,
                                                    final Class<?>[] paramTypes)
        throws NoSuchMethodException {

        Constructor<?>[]          constructors         =
            klass.getConstructors();
        TreeSet<ConstructorScore> possibleConstructors = 
            new TreeSet<ConstructorScore>();
        int                       numParams            = paramTypes.length;

        for ( Constructor constructor : constructors ) {
            // Check if method has the same number of params.
            Class<?>[] constParamTypes = constructor.getParameterTypes();

            if  ( constParamTypes.length == numParams ) {
                if ( paramArrayMatches(paramTypes, constParamTypes) ) {
                    int              score            =
                        paramArrayDistance(paramTypes, constParamTypes);
                    ConstructorScore constructorScore =
                        new ConstructorScore(constructor, score);

                    possibleConstructors.add(constructorScore);
                }
            }
        }
        
        Constructor<?> result = null;

        if ( possibleConstructors.isEmpty() ) {
            result = klass.getConstructor(paramTypes);
        } else {
            result = possibleConstructors.first()._constructor;
        }
        
        return result;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    public static Method findMethod(final Class<?>   klass,
                                    final String     methodName,
                                    final Class<?>[] paramTypes,
                                    final boolean    useVariants)
        throws SRuntimeException {

        Method result = null;

        try {
            result = doFindMethod(klass, methodName, paramTypes, useVariants);
        } catch ( NoSuchMethodException e ) {
            throw new SRuntimeException("could not find method '"
                                        + methodName + "'");
        }

        return result;
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    private static Method doFindMethod(final Class<?>   klass,
                                       final String     methodName,
                                       final Class<?>[] paramTypes,
                                       final boolean    useVariants)
        throws NoSuchMethodException {

        Method method = null;

        if ( useVariants ) {
            method = findMethod(klass, methodName, paramTypes);
        } else {
            method = klass.getMethod(methodName, paramTypes);
        }

        return method;
    }
    




/***************************************************************************
 *
 * Populates a Set with valid methods and selects the one closer.
 *
 ***************************************************************************/

    private static Method findMethod(final Class   cl, 
                                     final String  methodName, 
                                     final Class[] paramTypes) 
        throws NoSuchMethodException {

        // TBD - Cache <cl, methodName, paramTypes> -> method ?

        TreeSet<MethodScore> possibleMethods = new TreeSet<MethodScore>();

        findMethod(cl, methodName, paramTypes, possibleMethods, 0);

        Method result = null;
        if ( possibleMethods.isEmpty() ) {
            throw new NoSuchMethodException("Can't find method "+methodName);
        } else {
            result = possibleMethods.first()._method;
        }
        
        return result;
    }





/***************************************************************************
 *
 * Populates the Set possibleMethods with valid methods of public classes
 * or interfaces.
 *
 ***************************************************************************/

    private static void findMethod(final Class<?>             cl,
                                   final String               methodName,
                                   final Class<?>[]           paramTypes,
                                   final TreeSet<MethodScore> possibleMethods,
                                   final int                  initialDistance)
        throws NoSuchMethodException {

        if ( Modifier.isPublic(cl.getModifiers()) ) {
            // class is public, so we'll use the class directly
            findMethodInClass(cl,
                              methodName,
                              paramTypes, 
                              possibleMethods,
                              initialDistance);
        } else {
            // class is not public, try the interfaces and then the super class
            for ( Class<?> anInterf : cl.getInterfaces() ) {
                // TBD - maybe the calculation of distance is not the best
                findMethod(anInterf,
                           methodName,
                           paramTypes,
                           possibleMethods,
                           1+initialDistance);
            }
            Class<?> aSuper = cl.getSuperclass();
            if ( null != aSuper ) {
                // TBD - maybe the calculation of distance is not the best
                findMethod(aSuper,
                           methodName,
                           paramTypes,
                           possibleMethods,
                           1+initialDistance);
            }
        }
    }





/***************************************************************************
 *
 * Populates a Set (possibleMethods) with all the valid methods with 
 * the given name and paramTypes.
 * This will only be executed in public classes (or interfaces).
 *
 ***************************************************************************/

   private static void
       findMethodInClass(final Class<?>             cl,
                         final String               methodName,
                         final Class<?>[]           paramTypes,
                         final TreeSet<MethodScore> possibleMethods,
                         final int                  initialDistance)
        throws NoSuchMethodException {

        Method[] meths     = cl.getMethods();
        int      numParams = paramTypes.length;

        for ( Method method : meths ) {

            // Check if method has the same name.
            if ( method.getName().equals(methodName) ) {

                // Check if method has the same number of params.
                Class<?>[] methParamTypes = method.getParameterTypes();
                if ( methParamTypes.length == numParams ) {
                    if (paramArrayMatches(paramTypes, methParamTypes)) {
                        // TBD - maybe the calculation of distance is
                        // not the best
                        int score =
                            initialDistance
                            +
                            paramArrayDistance(paramTypes, methParamTypes);

                        possibleMethods.add(new MethodScore(method,score));
                    }
                }
            }
        }
    }
    
    



/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    private static boolean paramArrayMatches(final Class<?>[] givenParamTypes,
                                             final Class<?>[] knownParamTypes) {

        boolean paramsMatch = true;
        int     numParams   = givenParamTypes.length;

        for ( int i=0; i<numParams && paramsMatch; i++ ) {
            Class<?> givenParamType = givenParamTypes[i];
            Class<?> knownParamType = knownParamTypes[i];

            if ( givenParamType == null ) {
                paramsMatch =  !(knownParamType.isPrimitive());
            } else {
                paramsMatch = knownParamType.isAssignableFrom(givenParamType);
            }

            // If the parameter is primitive, check with correct class.
            if ( !paramsMatch && knownParamType.isPrimitive()
                 && (givenParamType!=null) ) {
                Class<?> primitiveClass = _primitiveToClass.get(knownParamType);

                paramsMatch = 
                    primitiveClass.isAssignableFrom(givenParamType);
            }
        }
        
        return paramsMatch;
    }
    




/***************************************************************************
 *
 * Calculates the distance between two array of Classes, where the distance
 * is the sum of distances between each element.
 *
 ***************************************************************************/

    private static int paramArrayDistance(final Class[] givenParamTypes,
                                          final Class[] knownParamTypes) {

        int score    = 0;
        int distance = 0;

        for (int i=0; i<givenParamTypes.length; i++ ) {
            // TBD - Maybe the calculation of distance is not the
            // best...
            distance =
                paramClassDistance(givenParamTypes[i], knownParamTypes[i]);

            if ( distance >= 0) {
                score += distance;
            }
        }
        
        return score;
    }





/***************************************************************************
 *
 * Calculates the distance between Classes, which is the number of
 * classes/interfaces between them.
 *
 * Also keep a cache of the calculated distances.
 *
 * The calculation algorithm is defined as:
 *
 * - distance between two equal classes is zero.
 *
 *  - distance between a class and one given super class is 1 plus the
 *    distance of it's direct super class to the given super class.
 *
 *  - distance between a class and one give interface is lowest of 1
 *    plus the distance of each of its interfaces to the given
 *    interface.
 *
 * This method relies on the fact that it's always called after the
 * check knownParamType.isAssignableFrom(givenParamType) returns true.
 *
 ***************************************************************************/

    private static Map<ClassPair,Integer> _classDistances = 
        new HashMap<ClassPair,Integer>();
        
    private static int paramClassDistance(final Class<?> givenParamType,
                                          final Class<?> knownParamType) {
        
        ClassPair aPair    = new ClassPair(givenParamType,knownParamType);
        int       distance = 0;

        if ( _classDistances.containsKey(aPair) ) {
            distance=_classDistances.get(aPair).intValue();
        } else {
            if ( givenParamType == null ) {
                if (knownParamType.isPrimitive()) {
                    // null does not match any primitive type
                    distance = -1;
                } else {
                    // null matches any non-primitive argument with a
                    // zero distance
                    distance = 0;
                }
            } else if ( !givenParamType.equals(knownParamType) ) {
                // If classes are diferent, then calculate the
                // distance
                TreeSet<Integer> distanceSet = new TreeSet<Integer>();
                // check super class of givenParamType that is
                // assignable to knownParamType for the lowest
                // distance
                Class aSuper = givenParamType.getSuperclass();
                if ( (null!=aSuper)
                     && knownParamType.isAssignableFrom(aSuper) ) {
                    // TBD - maybe the calculation of distance is not the best
                    int aDist=paramClassDistance(aSuper, knownParamType);
                    if (aDist>=0) {
                        distanceSet.add(Integer.valueOf(1+aDist));
                    }
                }
                // Check every interfaces of givenParamType that are
                // assignable to knownParamType for the lowest
                // distance
                for ( Class anInterf : givenParamType.getInterfaces() ) {
                    if (knownParamType.isAssignableFrom(anInterf)) {
                        // TBD - maybe the calculation of distance is
                        // not the best
                        int aDist=paramClassDistance(anInterf,knownParamType);
                        if (aDist>=0) {
                            distanceSet.add(Integer.valueOf(1+aDist));
                        }
                    }
                }
                // there might not be any method!!!
                if ( distanceSet.isEmpty() ) {
                    distance=-1;
                } else {
                    distance = distanceSet.first().intValue();
                }
            }
            // store the distance if it exists
            if ( distance >= 0 ) {
                _classDistances.put(aPair, Integer.valueOf(distance));
            }
        }
        return distance;
    }





/***************************************************************************
 *
 * Each element will store a possible method with it's score in the TreeSet,
 * the order is normal, having the greatest score as the last object in 
 * the Set.
 *
 ***************************************************************************/

    private static final class MethodScore
        extends Object
        implements Comparable {

        public Method _method = null;
        public int    _score  = 0;

        public MethodScore(final Method aMethod,
                           final int aScore) {
            _method = aMethod;
            _score = aScore;
        }

        public int compareTo(final Object anObject) {
            MethodScore aMethodScore = (MethodScore) anObject;
            return _score - aMethodScore._score;
        }
    }





/***************************************************************************
 *
 * Each element will store a possible method with it's score in the TreeSet,
 * the order is normal, having the greatest score as the last object in 
 * the Set.
 *
 ***************************************************************************/

    private static final class ConstructorScore
        extends Object
        implements Comparable {

        public Constructor<?> _constructor = null;
        public int            _score       = 0;

        public ConstructorScore(final Constructor<?> aConstructor,
                                final int aScore) {
            _constructor = aConstructor;
            _score       = aScore;
        }

        public int compareTo(final Object anObject) {
            ConstructorScore aConstructorScore = (ConstructorScore) anObject;
            return _score - aConstructorScore._score;
        }
    }





/***************************************************************************
 *
 * Used to store the keys for the Class distance cache.
 *
 ***************************************************************************/

    private static final class ClassPair
        extends Object {

        private  Class<?> _given = null;
        private  Class<?> _known = null;
        private int      _hash  = 0;


        public ClassPair(final Class<?> aGiven,
                         final Class<?> aKnown) {
            _given = aGiven;
            _known = aKnown;
            String aGivenName = _given == null ? "null" : _given.getName();
            _hash = (aGivenName+_known.getName()).hashCode();
        }


        @Override
        public int hashCode() {
            return _hash;
        }

        
        @Override
        public boolean equals(final Object anObj) {

            if ( this.getClass() != anObj.getClass() ) {
                return false;
            }

            ClassPair aPair = (ClassPair)anObj;

            if ( _given == null ) {
                if ( aPair._given == null ) {
                    return _known.equals(aPair._known);
                } else {
                    return false;
                }
            } else {
                return
                    _given.equals(aPair._given)
                    && _known.equals(aPair._known);
            }
        }
    }


}





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

