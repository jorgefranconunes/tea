/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SModuleRegexp.java,v 1.1 2002/08/02 17:47:24 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/08/02
 * Moved to the "com.pdmfc.tea.modules" package. (jfn)
 *
 * 2002/08/01
 * All functions of the "Regexp" module are now implemented in this
 * class. (jfn)
 *
 * 2002/02/22
 * Added the following functions for downwards compatibility with
 * Tea 1.x: "matches". (jfn)
 *
 * 2002/01/20
 * Calls to the "addJavaFunction()" method were replaced by inner
 * classes for performance. (jfn)
 *
 * 2002/01/10
 * This classe now derives from SModuleCore. (jfn)
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules;

import java.io.File;
import java.io.FilenameFilter;

import gnu.regexp.RE;
import gnu.regexp.REException;
import gnu.regexp.REMatch;
import gnu.regexp.REMatchEnumeration;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.modules.SModule;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjPair;
import com.pdmfc.tea.runtime.STeaRuntime;
import com.pdmfc.tea.runtime.SNumArgException;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;





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

public class SModuleRegexp
    extends SModule {





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public SModuleRegexp() {
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

   public void init(STeaRuntime context)
       throws STeaException {

       super.init(context);

	context.addFunction("regexp-pattern",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionPattern(func, context, args);
				    }
				});

	context.addFunction("glob",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionGlob(func, context, args);
				    }
				});

	context.addFunction("regsub",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionRegsub(func, context, args);
				    }
				});

	SObjFunction matches = new SObjFunction() {
		public Object exec(SObjFunction func,
				   SContext     context,
				   Object[]     args)
		    throws STeaException {
		    return functionMatches(func, context, args);
		}
	    };

	context.addFunction("matches?", matches);
	
	// For backwards compatibility with Tea 1.x.
	context.addFunction("matches", matches);

	context.addFunction("regexp",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionRegexp(func, context, args);
				    }
				});

	context.addFunction("str-split",
			    new SObjFunction() {
				    public Object exec(SObjFunction func,
						       SContext     context,
						       Object[]     args)
					throws STeaException {
					return functionSplit(func, context, args);
				    }
				});
   }





//* 
//* <TeaFunction name="regexp-pattern"
//* 		arguments="aString"
//*             module="tea.regexp">
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
 * 
 *
 **************************************************************************/

    private static Object functionPattern(SObjFunction func,
					  SContext     context,
					  Object[]     args)
	throws STeaException {

	if ( args.length != 2 ) {
	    throw new SNumArgException(args[0], "pattern-string");
	}

	return getPattern(args,1);
    }





//* 
//* <TeaFunction name="glob"
//* 		 arguments="dirName fileSpec [...]"
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
 * 
 *
 **************************************************************************/

    private Object functionGlob(SObjFunction func,
				SContext     context,
				Object[]     args)
	throws STeaException {

	int numArgs = args.length;

	if ( numArgs < 3 ) {
	    throw new SNumArgException(args[0], "dir-name regexp [regexp ...]");
	}

	final RE[] patterns = new RE[numArgs-2];
	File       aDir     = new File(STypes.getString(args, 1));

	for ( int i=numArgs; (i--)>2; ) {
	    patterns[i-2] = getPattern(args,i);
	}

	FilenameFilter filter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
		    for ( int i=patterns.length; (i--)>0; ) {
			if ( patterns[i].isMatch(name)  ) {
			    return true;
			}
		    }
		    return false;
		}
	    };
	String[] fileNames = aDir.list(filter);
	SObjPair head      = SObjPair.emptyList();

	if ( fileNames != null ) {
	    for ( int i=fileNames.length; (i--)>0; ) {
		head = new SObjPair(fileNames[i], head);
	    }
	}

	return head;
    }





//* 
//* <TeaFunction name="regsub"
//* 		arguments="aRegexp substitution input"
//*             module="tea.regexp">
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
 * 
 *
 **************************************************************************/

    private static Object functionRegsub(SObjFunction func,
					 SContext     context,
					 Object[]     args)
	throws STeaException {

	if ( args.length != 4 ) {
	    throw new SNumArgException(args[0], "regex substitution input");
	}

	RE      pattern  = getPattern(args,1);
	String  subst    = STypes.getString(args,2);
	String  input    = STypes.getString(args,3);
	String  result;
	
	// Perform the substitutions
	result = pattern.substituteAll(input, subst);

	return result;
    }





//* 
//* <TeaFunction name="matches?"
//* 		arguments="aRegexp aString"
//*             module="tea.regexp">
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
 * 
 *
 **************************************************************************/

    private static Object functionMatches(SObjFunction func,
					  SContext     context,
					  Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException(args[0], "regex input");
	}

	RE      pattern  = getPattern(args,1);
	String  input    = STypes.getString(args,2);

	return pattern.isMatch(input) ? Boolean.TRUE : Boolean.FALSE;
    }





//* 
//* <TeaFunction name="regexp"
//* 		arguments="aRegexp aString"
//*             module="tea.regexp">
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
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static Object functionRegexp(SObjFunction func,
					 SContext     context,
					 Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException(args[0], "regex string");
	}

	RE                 pattern  = getPattern(args,1);
	String             aString  = STypes.getString(args,2);
	REMatchEnumeration matchSet = pattern.getMatchEnumeration(aString);
	SObjPair           head     = null;
	SObjPair           tail     = null;

	while ( matchSet.hasMoreMatches() ) {
	    SObjPair node = new SObjPair(buildMatch(matchSet.nextMatch()),null);

	    if ( head == null ) {
		head = node;
	    } else {
		tail._cdr = node;
	    }
	    tail = node;
	}
	if ( tail != null ) {
	    tail._cdr = SObjPair.emptyList();
	} else {
	    head = SObjPair.emptyList();
	}

	return head;
    }





/**************************************************************************
 *
 * Builds a list with match information. First element is the matched
 * portion. Following elements are the portions mathcing the
 * parenthesized sets in the pattern.
 *
 * @param result
 *    A <TT>REMatch</TT> object representing a match.
 *
 * @return
 *    An <TT>SObjPair</TT> that is the first element in a list.
 *
 **************************************************************************/

   private static SObjPair buildMatch(REMatch result) {

      SObjPair head  = new SObjPair(result.toString(),null);
      SObjPair tail  = head;

      for ( int i=1; result.getSubStartIndex(i)>=0; i++ ) {
	 String   subExpr = result.toString(i);
	 SObjPair node    = new SObjPair(subExpr,null);

	 if ( head == null ) {
	    head = node;
	 } else {
	    tail._cdr = node;
	 }
	 tail = node;
      }
      tail._cdr = SObjPair.emptyList();

      return head;
   }





//* 
//* <TeaFunction name="str-split"
//* 		arguments="aString separator"
//*             module="tea.string">
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
//* </Returns>
//*
//* <Description>
//* </Description>
//* 
//* </TeaFunction>
//* 

/**************************************************************************
 *
 * This method is supposed to be called with <TT>args</TT> having at least
 * one element.
 *
 * @exception STeaException
 *   Thrown if there is not at least one argument for the command.
 *
 **************************************************************************/

    private static Object functionSplit(SObjFunction func,
					SContext     context,
					Object[]     args)
	throws STeaException {

	if ( args.length != 3 ) {
	    throw new SNumArgException(args[0], "string split-string");
	}

	String str = STypes.getString(args,1);

	if ( str.length() == 0 ) {
	    return SObjPair.emptyList();
	}

	RE                 pattern  = getPattern(args,2);
	int                index    = 0;
	REMatchEnumeration matchSet = pattern.getMatchEnumeration(str);
	SObjPair           head     = null;
	SObjPair           tail     = null;

	while ( matchSet.hasMoreMatches() ) {
	    REMatch  match = matchSet.nextMatch();
	    String   part  = str.substring(index, match.getStartIndex());
	    SObjPair node  = new SObjPair(part,null);
	    
	    if ( head == null ) {
		head = node;
	    } else {
		tail._cdr = node;
	    }
	    tail = node;
	    index = match.getEndIndex();
	}

	String   part  = str.substring(index);
	SObjPair node  = new SObjPair(part,null);

	if ( head == null ) {
	    head = node;
	} else {
	    tail._cdr = node;
	}
	tail = node;
	tail._cdr = SObjPair.emptyList();

	return head;
    }





/**************************************************************************
 *
 * Tries to convert argument <TT>index</TT> into a
 * <TT>gnu.regexp.RE</TT>. If that argument is neither a
 * <TT>String</TT> representing a valid regular expression nor a
 * <TT>gnu.regexp.RE</TT>, an exception is thrown.
 *
 * @param args Array of <TT>Object</TT>, supposed to be the arguments
 * received by a call to the command.
 *
 * @param index The index of the argument to convert.
 *
 * @exception com.pdmfc.tea.STeaException Thrown if argument
 * <TT>index</TT> could not be converted to a <TT>SObjPattern</TT>.
 *
 **************************************************************************/

    private static RE getPattern(Object[] args,
				 int      index)
	throws STeaException {

	Object theArg = args[index];

	if ( theArg instanceof RE ) {
	    return (RE)theArg;
	}

	if ( theArg instanceof String ) {
	    String strPattern = (String)theArg;
	    try {
		return new RE(strPattern);
	    } catch (REException e){
		throw new SRuntimeException(args[0],
					    "malformed pattern (" + 
					    e.getMessage() + ")");
	    }
	}

	throw new STypeException(args[0],
				 "argument " + index +
				 " should be a regular expression, not a " +
				 STypes.getTypeName(theArg));
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

