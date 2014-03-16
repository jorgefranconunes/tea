/**************************************************************************
 *
 * Copyright (c) 2010 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.junit.Test;
import static org.junit.Assert.*;

import com.pdmfc.tea.compiler.STeaParserUtils;





/**************************************************************************
 *
 * Tests for the Tea parser engine.
 *
 **************************************************************************/

public class TeaParserTest
    extends Object {





    private static final String RES_STRING_LITERAL_TESTS =
        "StringLiteralTests.txt";





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void parseStringLiteral()
        throws UnsupportedEncodingException,
               IOException {

        InputStream       input =
            TeaParserTest.class.getResourceAsStream(RES_STRING_LITERAL_TESTS);
        InputStreamReader inputReader =
            new InputStreamReader(input, "UTF-8");
        BufferedReader    reader =
            new BufferedReader(inputReader);

        try {
            processTestCases(reader);
        } finally {
            try { reader.close(); } catch (IOException e) {}
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void processTestCases(BufferedReader reader)
        throws IOException {

        MyTestData testData = null;

        while ( (testData=fetchTestData(reader)) != null ) {
            String stringLiteral = testData.getStringLiteral();
            String stringValue   = testData.getStringValue();
            String actualString  =
                STeaParserUtils.parseStringLiteral(stringLiteral);

            assertEquals(stringValue, actualString);
        }
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private MyTestData fetchTestData(BufferedReader reader)
        throws IOException {

        MyTestData result        = null;
        String     stringLiteral = fetchLine(reader);
        String     stringValue   = fetchLine(reader);

        if ( (stringLiteral!=null) && (stringValue!=null) ) {
            result = new MyTestData(stringLiteral, stringValue);
        }

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private String fetchLine(BufferedReader reader)
        throws IOException {

        boolean lineWasRead = false;
        String  line        = null;

        while ( !lineWasRead ) {
            line = reader.readLine();

            if ( line == null ) {
                // End-of-file reached.
                lineWasRead = true;
                line        = null;
            } else {
                lineWasRead =
                    ( line.length() > 0 ) &&
                    ( line.charAt(0) != '#' );
            }
        }

        return line;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static class MyTestData
        extends Object {





        private String _stringLiteral = null;
        private String _stringValue   = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public MyTestData(String stringLiteral,
                          String stringValue) {

            _stringLiteral = stringLiteral;
            _stringValue   = stringValue;
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public String getStringLiteral() { return _stringLiteral; }
        public String getStringValue() { return _stringValue; }


    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

