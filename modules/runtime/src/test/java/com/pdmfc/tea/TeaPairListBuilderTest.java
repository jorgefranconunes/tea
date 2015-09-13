/**************************************************************************
 *
 * Copyright (c) 2015 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.pdmfc.tea.TeaPair;
import com.pdmfc.tea.TeaPairListBuilder;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class TeaPairListBuilderTest
    extends Object {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void buildEmpty() {

        TeaPair teaList = new TeaPairListBuilder().build();

        assertTeaListEquals(teaList);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void append() {

        testAppend("Hello");
        testAppend("Hello", "World");
        testAppend("This", "is", "a", "long", "list");
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void testAppend(final Object... array) {

        TeaPairListBuilder builder = new TeaPairListBuilder();

        for ( Object item : array ) {
            builder.append(item);
        }

        TeaPair teaList = builder.build();

        assertTeaListEquals(teaList, array);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void prepend() {

        testPrepend("Hello");
        testPrepend("Hello", "World");
        testPrepend("This", "is", "a", "long", "list");
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void testPrepend(final Object... array) {

        TeaPairListBuilder builder = new TeaPairListBuilder();
        Object[] reverseArray = new Object[array.length];

        int index = reverseArray.length-1;
        for ( Object item : array ) {
            builder.prepend(item);
            reverseArray[index] = item;
            --index;
        }

        TeaPair teaList = builder.build();

        assertTeaListEquals(teaList, reverseArray);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void assertTeaListEquals(final TeaPair teaList,
                                     final Object... expectedArray) {

        List<Object> actualList = new ArrayList<>();

        for ( Object item : teaList ) {
            actualList.add(item);
        }

        Object[] actualArray = actualList.toArray();

        assertArrayEquals(expectedArray, actualArray);
    }


}
