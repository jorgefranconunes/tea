/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.modules.regexp;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.pdmfc.tea.TeaException;
import com.pdmfc.tea.TeaModule;
import com.pdmfc.tea.Args;
import com.pdmfc.tea.TeaContext;
import com.pdmfc.tea.TeaNumArgException;
import com.pdmfc.tea.TeaFunction;
import com.pdmfc.tea.TeaPair;
import com.pdmfc.tea.TeaRunException;
import com.pdmfc.tea.TeaTypeException;
import com.pdmfc.tea.TeaFunctionImplementor;
import com.pdmfc.tea.TeaEnvironment;





//*
//* <TeaModule name="tea.regexp">
//* 
//* <Overview>
//* Support for regular expressions.
//* </Overview>
//*
//* <Description>
//* Functions envolving regular expressions.
//* </Description>
//*
//* </TeaModule>
//*





/**************************************************************************
 *
 * Regular expressions related package.
 *
 **************************************************************************/

public final class ModuleRegexp
    extends Object
    implements TeaModule {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public ModuleRegexp() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void init(final TeaEnvironment environment)
        throws TeaException {

        // Nothing to do. The functions provided by this module are
        // all implemented as methods of this with class with the
        // TeaFunction annotation.
   }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void end() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void start() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

    @Override
    public void stop() {

        // Nothing to do.
    }





//* 
//* <TeaFunction name="regexp-pattern"
//*              arguments="aString"
//*              module="tea.regexp">
//*
//* <Overview>
//* Creates a regular expression object.
//* </Overview>
//*
//* <Parameter name="aString">
//* A string object representing a regular expression.
//* </Parameter>
//*
//* <Returns>
//* A regular expression object containing the compiled version
//* of <Arg name="aString"/>.
//* </Returns>
//*
//* <Description>
//* Creates an object containing a compiled representation of a regular
//* expression. This regular expression object can be used wherever a
//* regular expression is expected. There is advantage in using a regular
//* expression object when it is reused multiple times, for example in a
//* loop.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>regexp-pattern</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("regexp-pattern")
    public static Object functionPattern(final TeaFunction func,
                                         final TeaContext  context,
                                         final Object[]    args)
        throws TeaException {

        if ( args.length != 2 ) {
            throw new TeaNumArgException(args, "pattern-string");
        }

        return getPattern(args,1);
    }





//* 
//* <TeaFunction name="glob"
//*              arguments="dirName fileSpec [...]"
//*              module="tea.io">
//*
//* <Overview>
//* Retrieves a list of files contained in a directory and whose names
//* match a regular expression.
//* </Overview>
//*
//* <Parameter name="dirName">
//* String representing the name of the directory where the files will
//* be searched for.
//* </Parameter>
//* 
//* <Parameter name="fileSpec">
//* A regular expression
//* </Parameter>
//*
//* <Returns>
//* A list of strings representing the names of files contained in the
//* <Arg name="dirName"/> and whose name matches one of the regular
//* expressions.
//* </Returns>
//*
//* <Description>
//* Note that there is no guaranty on the order of the names in the
//* list returned by the function.
//* <P>
//* As an example, the following function echos to the standard output
//* the names of the files in the current directory.
//* </P>
//* <Code>
//* define list-current-dir () {
//*     foreach fileName [glob "." ".*"] {
//*         $stdout writeln $fileName
//*     }
//* }
//* </Code>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>glob</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("glob")
    public Object functionGlob(final TeaFunction func,
                               final TeaContext  context,
                               final Object[]    args)
        throws TeaException {

        int numArgs = args.length;

        if ( numArgs < 3 ) {
            throw new TeaNumArgException(args,"dir-name regexp [regexp ...]");
        }

        String          dirName   = Args.getString(args, 1);
        File            directory = new File(dirName);
        final Pattern[] patterns  = new Pattern[numArgs-2];

        for ( int i=numArgs; (i--)>2; ) {
            patterns[i-2] = getPattern(args,i);
        }

        FilenameFilter filter = new FilenameFilter() {
                public boolean accept(final File dir, final String name) {
                    for ( int i=patterns.length; (i--)>0; ) {
                        Matcher matcher = patterns[i].matcher(name);
                        if ( matcher.matches()  ) {
                            return true;
                        }
                    }
                    return false;
                }
            };
        String[] fileNames = directory.list(filter);
        TeaPair head      = TeaPair.emptyList();

        if ( fileNames != null ) {
            for ( int i=fileNames.length; (i--)>0; ) {
                head = new TeaPair(fileNames[i], head);
            }
        }

        return head;
    }





//* 
//* <TeaFunction name="regsub"
//*              arguments="aRegexp substitution input"
//*              module="tea.regexp">
//*
//* <Overview>
//* Replaces substrings matching a regular expression.
//* </Overview>
//*
//* <Parameter name="aRegexp">
//* A regular expression object or a string representing a regular
//* expression.
//* </Parameter>
//*
//* <Parameter name="substitution">
//* The string that will be substituted into <Arg name="input"/> for each
//* portion matching <Arg name="aRegexp"/>.
//* </Parameter>
//*
//* <Parameter name="input">
//* The string object where the substituions will be performed.
//* </Parameter>
//*
//* <Returns>
//* A new string object obtained from <Arg name="input"/> by making
//* the substitutions.
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>regsub</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("regsub")
    public static Object functionRegsub(final TeaFunction func,
                                        final TeaContext  context,
                                        final Object[]    args)
        throws TeaException {

        if ( args.length != 4 ) {
            throw new TeaNumArgException(args, "regex substitution input");
        }

        Pattern pattern  = getPattern(args,1);
        String  subst    = Args.getString(args,2);
        String  input    = Args.getString(args,3);
        Matcher matcher  = pattern.matcher(input);
        String  result   = matcher.replaceAll(subst);

        return result;
    }





//* 
//* <TeaFunction name="matches?"
//*              arguments="aRegexp aString"
//*              module="tea.regexp">
//*
//* <Overview>
//* Checks if a regular expression is an exact match for a string.
//* </Overview>
//*
//* <Parameter name="aRegexp">
//* A regular expression object or a string representing a regular
//* expression.
//* </Parameter>
//*
//* <Parameter name="aString">
//* The string object against which the regular expression will be matched.
//* </Parameter>
//*
//* <Returns>
//* True if <Arg name="aRegexp"/> matches <Arg name="aString"/>.
//* False otherwise.
//* </Returns>
//*
//* <Description>
//* The <Arg name="aRegexp"/> can be either a string, whose contents will
//* be taken as a regular expression, or a regular expression object
//* returned by a call to the <FuncRef name="regexp-pattern"/> function.
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>matches?</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("matches?")
    public static Object functionMatches(final TeaFunction func,
                                         final TeaContext  context,
                                         final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "regex input");
        }

        Pattern pattern  = getPattern(args,1);
        String  input    = Args.getString(args,2);
        Matcher matcher  = pattern.matcher(input);
        Boolean result   = matcher.matches() ? Boolean.TRUE : Boolean.FALSE;

        return result;
    }





//* 
//* <TeaFunction name="regexp"
//*              arguments="aRegexp aString"
//*              module="tea.regexp">
//*
//* <Overview>
//* Finds all matches of a regular expression in a string.
//* </Overview>
//*
//* <Parameter name="aRegexp">
//* A regular expression object or a string representing a regular
//* expression.
//* </Parameter>
//*
//* <Parameter name="aString">
//* The string object against which the regular expression will be matched.
//* </Parameter>
//*
//* <Returns>
//* A list where each element represents a match. 
//* </Returns>
//*
//* <Description>
//* Each match in the returned list is represented as a list of strings.
//* The first element is the matched portion. The following elements are
//* the portions matching the parenthesized sets in the regular expression
//* pattern.
//* <ul>
//* <li><b>Example (positive match)</b><br/>
//* <pre>regexp "label([0-9]): (.*)" "label5: test message"</pre>
//* evaluates to a list within one list that can be expressed (in Tea) as:
//* <pre>( ( "label5: test message" "5" "test message" ) )</pre>
//* </li>
//* <li><b>Example (negative match)</b><br/>
//* <pre>regexp "label([0-9]): (.*)" "label: test message"</pre>
//* evaluates to an empty list.
//* </li>
//* </ul>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>regexp</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("regexp")
    public static Object functionRegexp(final TeaFunction func,
                                        final TeaContext  context,
                                        final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "regex string");
        }

        Pattern  pattern  = getPattern(args,1);
        String   aString  = Args.getString(args,2);
        Matcher  matcher  = pattern.matcher(aString);
        TeaPair head     = null;
        TeaPair tail     = null;

        while ( matcher.find() ) {
            MatchResult match = matcher.toMatchResult();
            TeaPair    elem  = buildMatch(match);
            TeaPair    node  = new TeaPair(elem,null);

            if ( head == null ) {
                head = node;
            } else {
                tail.setCdr(node);
            }
            tail = node;
        }
        if ( tail != null ) {
            tail.setCdr(TeaPair.emptyList());
        } else {
            head = TeaPair.emptyList();
        }

        return head;
    }





/**************************************************************************
 *
 * Builds a list with match information. First element is the matched
 * portion. Following elements are the portions matching the
 * parenthesized sets in the pattern.
 *
 * @param result
 *    A <TT>REMatch</TT> object representing a match.
 *
 * @return
 *    An <TT>TeaPair</TT> that is the first element in a list.
 *
 **************************************************************************/

   private static TeaPair buildMatch(final MatchResult result) {

      TeaPair head  = new TeaPair(result.group(),null);
      TeaPair tail  = head;

      for ( int i=1, count=result.groupCount(); i<=count; ++i ) {
         String   subExpr = result.group(i);
         TeaPair node    = new TeaPair(subExpr,null);

         if ( head == null ) {
            head = node;
         } else {
             tail.setCdr(node);
         }
         tail = node;
      }
      tail.setCdr(TeaPair.emptyList());

      return head;
   }





//* 
//* <TeaFunction name="str-split"
//*              arguments="aString separator"
//*              module="tea.string">
//*
//* <Overview>
//* Splits a string into a list of strings.
//* </Overview>
//*
//* <Parameter name="aString">
//* The string to be splited.
//* </Parameter>
//* 
//* <Parameter name="separator">
//* A string object representing a regular expression or a regular
//* expression object.
//* </Parameter>
//*
//* <Returns>
//* A list of string objects obtained from <Arg name="aString"/>
//* by splitting it using the <Arg name="separator"/> as boundary
//* sequence.
//* <P>If <Arg name="aString"/> is an empty string,
//* an empty list is returned.</P>
//* <P>If <Arg name="aString"/> does not contain the <Arg name="separator"/>,
//* a list with a single element <Arg name="aString"/> is returned.</P>
//* <P>Every two consecutive ocurrences of <Arg name="separator"/> found within
//* <Arg name="aString"/> result in an empty string element.</P>
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * Implements the Tea <code>str-split</code> function.
 *
 * @param func The Tea function object for which this function is
 * being called.
 *
 * @param context The Tea context where the function is being invoked.
 *
 * @param args The arguments the function is being invoked with.
 *
 * @exception TeaException Thrown if the function did not complete
 * successfully.
 *
 * @return The value returned by the Tea function.
 *
 **************************************************************************/

    @TeaFunctionImplementor("str-split")
    public static Object functionSplit(final TeaFunction func,
                                       final TeaContext  context,
                                       final Object[]    args)
        throws TeaException {

        if ( args.length != 3 ) {
            throw new TeaNumArgException(args, "string split-string");
        }

        String str = Args.getString(args,1);

        if ( str.length() == 0 ) {
            return TeaPair.emptyList();
        }

        Pattern            pattern = getPattern(args,2);
        int                index   = 0;
        Matcher            matcher = pattern.matcher(str);
        TeaPair           head    = null;
        TeaPair           tail    = null;

        while ( matcher.find() ) {
            MatchResult  match = matcher.toMatchResult();
            String       part  = str.substring(index, match.start());
            TeaPair     node  = new TeaPair(part,null);
            
            if ( head == null ) {
                head = node;
            } else {
                tail.setCdr(node);
            }
            tail = node;
            index = match.end();
        }

        String   part  = str.substring(index);
        TeaPair node  = new TeaPair(part,null);

        if ( head == null ) {
            head = node;
        } else {
            tail.setCdr(node);
        }
        tail = node;
        tail.setCdr(TeaPair.emptyList());

        return head;
    }





/**************************************************************************
 *
 * Tries to convert argument <TT>index</TT> into a
 * <TT>java.util.regex.Pattern</TT>. If that argument is neither a
 * <TT>String</TT> representing a valid regular expression nor a
 * <TT>java.util.regex.Patter</TT>, an exception is thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 * received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception com.pdmfc.tea.TeaException Thrown if argument
 * <TT>index</TT> could not be converted to a
 * <TT>java.util.regex.Pattern</TT>.
 *
 **************************************************************************/

    private static Pattern getPattern(final Object[] args,
                                      final int      index)
        throws TeaException {

        Object theArg = args[index];

        if ( theArg instanceof Pattern ) {
            return (Pattern)theArg;
        }

        if ( theArg instanceof String ) {
            String patternStr = (String)theArg;
            try {
                return Pattern.compile(patternStr);
            } catch ( PatternSyntaxException e ){
                String   msg     = "malformed pattern ({0})";
                Object[] fmtArgs = { e.getMessage() };
                throw new TeaRunException(args, msg, fmtArgs);
            }
        }

        throw new TeaTypeException(args, index, "regex");
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

