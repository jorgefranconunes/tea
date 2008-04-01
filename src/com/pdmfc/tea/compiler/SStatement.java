/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SStatement.java,v 1.7 2002/09/17 16:35:27 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2002/03/28
 * No longer stores the file name from where the SStatement was
 * compiled. That is now stored by the SCode holding this
 * SStatement. (jfn)
 *
 * 2001/06/14
 * No longer uses an SObjArrayFactory. (jfn)
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import java.io.PrintStream;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SObjSymbol;
import com.pdmfc.tea.runtime.SRuntimeException;
import com.pdmfc.tea.runtime.STypeException;
import com.pdmfc.tea.runtime.STypes;





/**************************************************************************
 *
 * Represents the compiled form of a single Tea statement. The
 * statement is stored as a list of words. These words are its
 * arguments, with the first argument representing the function.
 *
 **************************************************************************/

abstract class SStatement
    extends Object {





    // The line number this statement belongs to.
    protected int    _lineNumber;





/**************************************************************************
 *
 * Builds an empty argument list (null command).
 *
 * @param line The source line number where this statement occurs.
 *
 **************************************************************************/

    public SStatement(int line) {

	_lineNumber = line;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public int getLineNumber() {

	return _lineNumber;
    }





/**************************************************************************
 *
 * Executes this statement. The first word must be either a symbol
 * refering to a variable containing an <code>{@link
 * SObjFunction}</code>, or an <code>{@link SObjFunction}</code>
 * object.
 *
 * <p>The following steps are followed, in this order:</p>
 *
 * <ul>
 *
 * <li>The arguments are evaluated, left to right.</li>
 *
 * <li>The command to execute is fecthed from the first argument.</li>
 *
 * <li>The command is asked to executed with the given arguments.</li>
 *
 * </ul>
 *
 * @param context The context where the command will be executed.
 *
 * @return The object returned by the execution of the command.
 *
 * @exception STeaException An exception can be thrown in four cases:
 *
 * <ul>
 *
 * <li>While evaluating one of the arguments.</li>
 *
 * <li>The first word was a symbol but either there was no variable
 * with that name or it did not contain an <TT>SObjFunction</TT>.</li>
 *
 * <li>The first word was neither an <TT>SObjSymbol</TT> nor a
 * <TT>SObjFunction</TT>.</li>
 *
 * <li>an exception was raised during the execution of the command.</li>
 *
 * </ul>
 *
 **************************************************************************/

    public abstract Object exec(SContext context)
	throws STeaException;


}





/**************************************************************************
 *
 * Creates <code>SStatement</code> instances.
 *
 **************************************************************************/

class SStatementFactory
    extends Object {





    private SStatementNode _wordsHead = null;
    private SStatementNode _wordsTail = null;
    private int            _wordCount = 0;

    // The line number this statement belongs to.
    private int    _lineNumber;





/**************************************************************************
 *
 * Builds an empty argument list (null command).
 *
 * @param line The source line number where this statement occurs.
 *
 **************************************************************************/

    public SStatementFactory(int line) {

	_lineNumber = line;
    }





/**************************************************************************
 *
 * Adds a new statement word to the end of the command line.
 *
 * @param aWord The statement word to be added at the end of the
 * command line.
 *
 **************************************************************************/

    public void addWord(SWord aWord) {

	SStatementNode newWord = new SStatementNode(aWord);

	if ( _wordsHead == null ) {
	    _wordsHead = newWord;
	} else {
	    _wordsTail._next = newWord;
	}
	_wordsTail = newWord;
	_wordCount++;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SStatement createStatement() {

	SStatement result = null;

	switch ( _wordCount ) {
	case 0 :
	    throw new RuntimeException("Empty Tea statement!");
	case 1 :
	    result = new SStatement1(_lineNumber, _wordsHead);
	    break;
	case 2 :
	    result = new SStatement2(_lineNumber, _wordsHead);
	    break;
	case 3 :
	    result = new SStatement3(_lineNumber, _wordsHead);
	    break;
	case 4 :
	    result = new SStatement4(_lineNumber, _wordsHead);
	    break;
	case 5 :
	    result = new SStatement5(_lineNumber, _wordsHead);
	    break;
	default :
	    result = new SGenericStatement(_lineNumber, _wordsHead,_wordCount);
	    break;
	}

	return result;
    }


}





/**************************************************************************
 *
 * Stores the compiled form of a single Tea statement. The command is
 * stored as a list of words. This words are its arguments, with the
 * first argument representing the command.
 *
 **************************************************************************/

class SGenericStatement
    extends SStatement {





    private SStatementNode _wordsHead = null;
    private int            _wordCount = 0;
    private SWord          _firstWord = null;

    private static final String ERR_WORD =
	"	while evaluating argument {0} on line {1}";





/**************************************************************************
 *
 * Builds an empty argument list (null command).
 *
 * @param line The source line number where this statement occurs.
 *
 **************************************************************************/

    public SGenericStatement(int            lineNumber,
			     SStatementNode head,
			     int            wordCount) {

	super(lineNumber);

	_wordsHead = head;
	_wordCount = wordCount;
	_firstWord = head._element;
    }





/**************************************************************************
 *
 * Executes this statement. The first word must be either a symbol
 * refering to a variable containing an <code>{@link
 * SObjFunction}</code>, or an <code>{@link SObjFunction}</code>
 * object.
 *
 * <p>The following steps are followed, in this order:</p>
 *
 * <ul>
 *
 * <li>The arguments are evaluated, left to right.</li>
 *
 * <li>The command to execute is fecthed from the first argument.</li>
 *
 * <li>The command is asked to executed with the given arguments.</li>
 *
 * </ul>
 *
 * @param context The context where the command will be executed.
 *
 * @return The object returned by the execution of the command.
 *
 * @exception STeaException An exception can be thrown in four cases:
 *
 * <ul>
 *
 * <li>While evaluating one of the arguments.</li>
 *
 * <li>The first word was a symbol but either there was no variable
 * with that name or it did not contain an <TT>SObjFunction</TT>.</li>
 *
 * <li>The first word was neither an <TT>SObjSymbol</TT> nor a
 * <TT>SObjFunction</TT>.</li>
 *
 * <li>an exception was raised during the execution of the command.</li>
 *
 * </ul>
 *
 **************************************************************************/

    public final Object exec(SContext context)
	throws STeaException {

	int            numArgs  = _wordCount;
	SStatementNode node     = _wordsHead._next;
	Object[]       args     = new Object[numArgs];
	SObjFunction   function = _firstWord.toFunction(context);

	args[0] = function;

	// First we evaluate the arguments:
	for ( int i=1; i<numArgs; i++ ) {
	    try {
		args[i] = node._element.get(context);
		node = node._next;
	    } catch (SRuntimeException e) {
		Object[] fmtArgs = { new Integer(i), new Integer(_lineNumber)};
		e.addMessage(ERR_WORD, fmtArgs);
		throw e;
	    }
	}

	// And finally we execute the Tea function, passing it the
	// arguments:
	Object result = function.exec(function, context, args);

	return result;
    }


}





/**************************************************************************
 *
 * Nodes of a linked list.
 *
 **************************************************************************/

class SStatementNode
    extends Object {





    public SWord          _element = null;
    public SStatementNode _next    = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SStatementNode(SWord word) {

	_element = word;
    }

}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

class SStatement1
    extends SStatement {





    private SWord _word0 = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SStatement1(int            lineNumber,
		       SStatementNode head) {

	super(lineNumber);

	SStatementNode node = head;

	_word0 = node._element;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object exec(SContext context)
	throws STeaException {

	SObjFunction function = _word0.toFunction(context);
	Object[]     args     = {
	    function,
	};
	Object       result   = function.exec(function, context, args);

	return result;
    }
}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

class SStatement2
    extends SStatement {





    private SWord _word0 = null;
    private SWord _word1 = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SStatement2(int            lineNumber,
		       SStatementNode head) {

	super(lineNumber);

	SStatementNode node = head;

	_word0 = node._element;
	node   = node._next;
	_word1 = node._element;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object exec(SContext context)
	throws STeaException {

	SObjFunction function = _word0.toFunction(context);
	Object[]     args     = {
	    function,
	    _word1.get(context)
	};
	Object       result   = function.exec(function, context, args);

	return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

class SStatement3
    extends SStatement {





    private SWord _word0 = null;
    private SWord _word1 = null;
    private SWord _word2 = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SStatement3(int            lineNumber,
		       SStatementNode head) {

	super(lineNumber);

	SStatementNode node = head;

	_word0 = node._element;
	node   = node._next;
	_word1 = node._element;
	node   = node._next;
	_word2 = node._element;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object exec(SContext context)
	throws STeaException {

	SObjFunction function = _word0.toFunction(context);
	Object[]     args     = {
	    function,
	    _word1.get(context),
	    _word2.get(context)
	};
	Object       result   = function.exec(function, context, args);

	return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

class SStatement4
    extends SStatement {





    private SWord _word0 = null;
    private SWord _word1 = null;
    private SWord _word2 = null;
    private SWord _word3 = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SStatement4(int            lineNumber,
		       SStatementNode head) {

	super(lineNumber);

	SStatementNode node = head;

	_word0 = node._element;
	node   = node._next;
	_word1 = node._element;
	node   = node._next;
	_word2 = node._element;
	node   = node._next;
	_word3 = node._element;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object exec(SContext context)
	throws STeaException {

	SObjFunction function = _word0.toFunction(context);
	Object[]     args     = {
	    function,
	    _word1.get(context),
	    _word2.get(context),
	    _word3.get(context)
	};
	Object       result   = function.exec(function, context, args);

	return result;
    }


}



/**************************************************************************
 *
 * 
 *
 **************************************************************************/

class SStatement5
    extends SStatement {





    private SWord _word0 = null;
    private SWord _word1 = null;
    private SWord _word2 = null;
    private SWord _word3 = null;
    private SWord _word4 = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public SStatement5(int            lineNumber,
		       SStatementNode head) {

	super(lineNumber);

	SStatementNode node = head;

	_word0 = node._element;
	node   = node._next;
	_word1 = node._element;
	node   = node._next;
	_word2 = node._element;
	node   = node._next;
	_word3 = node._element;
	node   = node._next;
	_word4 = node._element;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Object exec(SContext context)
	throws STeaException {

	SObjFunction function = _word0.toFunction(context);
	Object[]     args     = {
	    function,
	    _word1.get(context),
	    _word2.get(context),
	    _word3.get(context),
	    _word4.get(context)
	};
	Object       result   = function.exec(function, context, args);

	return result;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

