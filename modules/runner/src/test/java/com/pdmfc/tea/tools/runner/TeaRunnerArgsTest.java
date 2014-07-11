/**************************************************************************
 *
 * Copyright (c) 2011-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.tools.runner;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.tools.runner.TeaRunnerArgs;





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
        throws TeaException {

        String[]      noArgs = {};
        TeaRunnerArgs args   = TeaRunnerArgs.parse(noArgs);

        assertEmptyArgs(args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void emptyArgs()
        throws TeaException {

        String[]      emptyArgs = { "--" };
        TeaRunnerArgs args      = TeaRunnerArgs.parse(emptyArgs);

        assertEmptyArgs(args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void assertEmptyArgs(final TeaRunnerArgs args)
        throws TeaException {

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

    @Test(expected=TeaException.class)
    public void unknownOption()
        throws TeaException {

        String[]      emptyArgs = { "--whatever=stuff" };
        TeaRunnerArgs args      = TeaRunnerArgs.parse(emptyArgs);

        fail("An exception should have been thrown by now...");
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void scriptOption()
        throws TeaException {

        String[]      cliArgs = { "--script=AA" };
        TeaRunnerArgs args    = TeaRunnerArgs.parse(cliArgs);

        assertEquals("AA", args.getScriptPath());
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void scriptOptionOverride()
        throws TeaException {

        String[]      cliArgs = { "--script=AA", "--script=BB" };
        TeaRunnerArgs args    = TeaRunnerArgs.parse(cliArgs);

        assertEquals("BB", args.getScriptPath());
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void encodingOption()
        throws TeaException {

        String[]      cliArgs = { "--encoding=AA" };
        TeaRunnerArgs args    = TeaRunnerArgs.parse(cliArgs);

        assertEquals("AA", args.getEncoding());
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void encodingOptionOverride()
        throws TeaException {

        String[]      cliArgs = { "--encoding=AA", "--encoding=BB" };
        TeaRunnerArgs args    = TeaRunnerArgs.parse(cliArgs);

        assertEquals("BB", args.getEncoding());
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    @Test
    public void libraryOption1()
        throws TeaException {

        String[]      cliArgs = { "--library=AA" };
        TeaRunnerArgs args    = TeaRunnerArgs.parse(cliArgs);

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
        throws TeaException {

        String[]      cliArgs = { ("--library=AA"+File.pathSeparator+"BB") };
        TeaRunnerArgs args    = TeaRunnerArgs.parse(cliArgs);

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
        throws TeaException {

        String[]       cliArgs = { ("--library=A|A"+File.pathSeparator+"BB") };
        TeaRunnerArgs args     = TeaRunnerArgs.parse(cliArgs);

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
        throws TeaException {

        String[] cliArgs = {
            "--library=AA",
            "--library=BB"
        };
        TeaRunnerArgs args = TeaRunnerArgs.parse(cliArgs);

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
        throws TeaException {

        String[] cliArgs = {
            ("--library=AA"+File.pathSeparator+"BB"),
            "--library=CC"
        };
        TeaRunnerArgs args = TeaRunnerArgs.parse(cliArgs);

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
        throws TeaException {

        String[]      cliArgs = { "--library-item=AA" };
        TeaRunnerArgs args    = TeaRunnerArgs.parse(cliArgs);

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
        throws TeaException {

        String[] cliArgs = {
            ("--library-item=AA"+File.pathSeparator+"BB")
        };
        TeaRunnerArgs args = TeaRunnerArgs.parse(cliArgs);

        String[] expected = { ("AA"+File.pathSeparator+"BB") };

        assertLibraryListEquals(expected, args);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private void assertLibraryListEquals(final String[]      expected,
                                         final TeaRunnerArgs args) {

        String[] actual = args.getLibraryList().toArray(new String[0]);

        assertArrayEquals(expected, actual);
    }


}
