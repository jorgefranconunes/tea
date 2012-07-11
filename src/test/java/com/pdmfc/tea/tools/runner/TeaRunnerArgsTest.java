/**************************************************************************
 *
 * Copyright (c) 2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.tools.runner;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.tools.runner.STeaRunnerArgs;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

public final class TeaRunnerArgsTest
    extends Object {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void noArgs()
        throws STeaException {

        String[]       noArgs = {};
        STeaRunnerArgs args   = STeaRunnerArgs.parse(noArgs);

        assertEmptyArgs(args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void emptyArgs()
        throws STeaException {

        String[]       emptyArgs = { "--" };
        STeaRunnerArgs args      = STeaRunnerArgs.parse(emptyArgs);

        assertEmptyArgs(args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void assertEmptyArgs(final STeaRunnerArgs args)
        throws STeaException {

        assertEquals(0, args.getLibraryList().size());
        assertNull(args.getScriptPath());
        assertNull(args.getEncoding());
        assertEquals(0, args.getScriptCliArgs().length);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test(expected=STeaException.class)
    public void unknownOption()
        throws STeaException {

        String[]       emptyArgs = { "--whatever=stuff" };
        STeaRunnerArgs args      = STeaRunnerArgs.parse(emptyArgs);

        fail("An exception should have been thrown by now...");
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void scriptOption()
        throws STeaException {

        String[]       cliArgs = { "--script=AA" };
        STeaRunnerArgs args      = STeaRunnerArgs.parse(cliArgs);

        assertEquals("AA", args.getScriptPath());
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void scriptOptionOverride()
        throws STeaException {

        String[]       cliArgs = { "--script=AA", "--script=BB" };
        STeaRunnerArgs args      = STeaRunnerArgs.parse(cliArgs);

        assertEquals("BB", args.getScriptPath());
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void encodingOption()
        throws STeaException {

        String[]       cliArgs = { "--encoding=AA" };
        STeaRunnerArgs args      = STeaRunnerArgs.parse(cliArgs);

        assertEquals("AA", args.getEncoding());
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void encodingOptionOverride()
        throws STeaException {

        String[]       cliArgs = { "--encoding=AA", "--encoding=BB" };
        STeaRunnerArgs args      = STeaRunnerArgs.parse(cliArgs);

        assertEquals("BB", args.getEncoding());
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void libraryOption1()
        throws STeaException {

        String[]       cliArgs = { "--library=AA" };
        STeaRunnerArgs args      = STeaRunnerArgs.parse(cliArgs);

        String[] expected = { "AA" };

        assertLibraryListEquals(expected, args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void libraryOption2()
        throws STeaException {

        String[]       cliArgs = { ("--library=AA"+File.pathSeparator+"BB") };
        STeaRunnerArgs args      = STeaRunnerArgs.parse(cliArgs);

        String[] expected = { "AA", "BB" };

        assertLibraryListEquals(expected, args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void libraryOption3()
        throws STeaException {

        String[]       cliArgs = { ("--library=A|A"+File.pathSeparator+"BB") };
        STeaRunnerArgs args      = STeaRunnerArgs.parse(cliArgs);

        String[] expected = { "A:A", "BB" };

        assertLibraryListEquals(expected, args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void libraryOptionAppend1()
        throws STeaException {

        String[]       cliArgs = {
            "--library=AA",
            "--library=BB"
        };
        STeaRunnerArgs args    = STeaRunnerArgs.parse(cliArgs);

        String[] expected = { "AA", "BB" };

        assertLibraryListEquals(expected, args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void libraryOptionAppend2()
        throws STeaException {

        String[]       cliArgs = {
            ("--library=AA"+File.pathSeparator+"BB"),
            "--library=CC"
        };
        STeaRunnerArgs args    = STeaRunnerArgs.parse(cliArgs);

        String[] expected = { "AA", "BB", "CC" };

        assertLibraryListEquals(expected, args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void libraryItemOption1()
        throws STeaException {

        String[]       cliArgs = { "--library-item=AA" };
        STeaRunnerArgs args      = STeaRunnerArgs.parse(cliArgs);

        String[] expected = { "AA" };

        assertLibraryListEquals(expected, args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void libraryItemOption2()
        throws STeaException {

        String[]       cliArgs = {
            ("--library-item=AA"+File.pathSeparator+"BB")
        };
        STeaRunnerArgs args      = STeaRunnerArgs.parse(cliArgs);

        String[] expected = { ("AA"+File.pathSeparator+"BB") };

        assertLibraryListEquals(expected, args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void assertLibraryListEquals(final String[]       expected,
                                         final STeaRunnerArgs args) {

        String[] actual = args.getLibraryList().toArray(new String[0]);

        assertArrayEquals(expected, actual);
    }


}