/**************************************************************************
 *
 * Copyright (c) 2001-2011 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;

import com.pdmfc.tea.STeaException;
import com.pdmfc.tea.compiler.SWord;
import com.pdmfc.tea.runtime.SContext;
import com.pdmfc.tea.runtime.SObjFunction;
import com.pdmfc.tea.runtime.SRuntimeException;





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
    private int _lineNumber;





/**************************************************************************
 *
 * Builds an empty argument list (null command).
 *
 * @param line The source line number where this statement occurs.
 *
 **************************************************************************/

    public SStatement(final int line) {

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





/**************************************************************************
 *
 * Creates <code>SStatement</code> instances.
 *
 **************************************************************************/

    public static final class Factory
        extends Object {





        private SStatement.Node _wordsHead = null;
        private SStatement.Node _wordsTail = null;
        private int             _wordCount = 0;

        // The line number this statement belongs to.
        private int    _lineNumber;





/**************************************************************************
 *
 * Builds an empty argument list (null command).
 *
 * @param line The source line number where this statement occurs.
 *
 **************************************************************************/

        public Factory(final int line) {

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

        public void addWord(final SWord aWord) {

            SStatement.Node newWord = new SStatement.Node(aWord);

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
                throw new IllegalStateException("Empty Tea statement!");
            case 1 :
                result = new SStatement.Order1(_lineNumber, _wordsHead);
                break;
            case 2 :
                result = new SStatement.Order2(_lineNumber, _wordsHead);
                break;
            case 3 :
                result = new SStatement.Order3(_lineNumber, _wordsHead);
                break;
            case 4 :
                result = new SStatement.Order4(_lineNumber, _wordsHead);
                break;
            case 5 :
                result = new SStatement.Order5(_lineNumber, _wordsHead);
                break;
            default :
                result = new SStatement.Generic(_lineNumber, _wordsHead,_wordCount);
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

    private static class Generic
        extends SStatement {





        private SStatement.Node _wordsHead = null;
        private int            _wordCount = 0;
        private SWord          _firstWord = null;

        private static final String ERR_WORD =
            "        while evaluating argument {0} on line {1}";





/**************************************************************************
 *
 * Builds an empty argument list (null command).
 *
 * @param line The source line number where this statement occurs.
 *
 **************************************************************************/

        public Generic(final int             lineNumber,
                       final SStatement.Node head,
                       final int             wordCount) {

            super(lineNumber);

            _wordsHead = head;
            _wordCount = wordCount;
            _firstWord = head._element;
        }





/**************************************************************************
 *
 * {@inheritDoc}
 *
 **************************************************************************/

        public final Object exec(final SContext context)
            throws STeaException {

            int            numArgs  = _wordCount;
            SStatement.Node node     = _wordsHead._next;
            Object[]       args     = new Object[numArgs];
            SObjFunction   function = _firstWord.toFunction(context);

            args[0] = function;

            // First we evaluate the arguments:
            for ( int i=1; i<numArgs; i++ ) {
                try {
                    args[i] = node._element.get(context);
                    node = node._next;
                } catch (SRuntimeException e) {
                    Object[] fmtArgs =
                        { String.valueOf(i), String.valueOf(getLineNumber())};
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

    private static final class Node
        extends Object {





        public SWord _element = null;
        public Node  _next    = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public Node(final SWord word) {

            _element = word;
        }

    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static final class Order1
        extends SStatement {





        private SWord _word0 = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public Order1(final int             lineNumber,
                      final SStatement.Node head) {

            super(lineNumber);

            SStatement.Node node = head;

            _word0 = node._element;
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public Object exec(final SContext context)
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

    private static final class Order2
        extends SStatement {





        private SWord _word0 = null;
        private SWord _word1 = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public Order2(final int            lineNumber,
                      final SStatement.Node head) {

            super(lineNumber);

            SStatement.Node node = head;

            _word0 = node._element;
            node   = node._next;
            _word1 = node._element;
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public Object exec(final SContext context)
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

    private static final class Order3
        extends SStatement {





        private SWord _word0 = null;
        private SWord _word1 = null;
        private SWord _word2 = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Order3(final int             lineNumber,
                  final SStatement.Node head) {

        super(lineNumber);

        SStatement.Node node = head;

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

        public Object exec(final SContext context)
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

    private static final class Order4
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

        public Order4(final int            lineNumber,
                      final SStatement.Node head) {

            super(lineNumber);

            SStatement.Node node = head;

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

        public Object exec(final SContext context)
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

    private static final class Order5
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

        public Order5(final int            lineNumber,
                      final SStatement.Node head) {

            super(lineNumber);

            SStatement.Node node = head;

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

        public Object exec(final SContext context)
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


}






/**************************************************************************
 *
 * 
 *
 **************************************************************************/

