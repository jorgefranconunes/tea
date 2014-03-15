/**************************************************************************
 *
 * Copyright (c) 2010-2012 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.compiler;





/**************************************************************************
 *
 * Provides utility functions that encapsulate some parsing abilities
 * of the Tea language.
 *
 **************************************************************************/

public final class STeaParserUtils
    extends Object {





/**************************************************************************
 *
 * No instances of this class are to be created.
 *
 **************************************************************************/

    private STeaParserUtils() {

        // Nothing to do.
    }





/**************************************************************************
 *
 * Takes a Tea string literal and builds the actual string represented
 * by that literal.
 *
 * @param s The Tea string literal to unescape.
 *
 * @return The unescaped string.
 *
 **************************************************************************/

    public static String parseStringLiteral(final String s) {

        StringBuilder result    = new StringBuilder();
        int           size      = s.length();
        int           lastIndex = size - 1;

        for ( int i=0; i<size; i++ ) {
            char c = s.charAt(i);

            if ( c != '\\' ) {
                result.append(c);
            } else {
                if ( i == lastIndex ) {
                    result.append(c);
                    break;
                }
                char d3, d2, d1, d0;
                int  d3i, d2i, d1i, d0i;
                switch ( c=s.charAt(++i) ) {
                case 'b' :
                    result.append('\b'); break;
                case 'f' :
                    result.append('\f'); break;
                case 'n'  :
                    result.append('\n'); break;
                case 'r'  :
                    result.append('\r'); break;
                case 't'  :
                    result.append('\t'); break;
                case 'u' :
                    if ( i == lastIndex ) {
                        result.append("\\u");
                        break;
                    }
                    d3 = s.charAt(++i);
                    if ( i == lastIndex ) {
                        result.append("\\u");
                        result.append(d3);
                        break;
                    }
                    d2 = s.charAt(++i);
                    if ( i == lastIndex ) {
                        result.append("\\u");
                        result.append(d3);
                        result.append(d2);
                        break;
                    }
                    d1 = s.charAt(++i);
                    if ( i == lastIndex ) {
                        result.append("\\u");
                        result.append(d3);
                        result.append(d2);
                        result.append(d1);
                        break;
                    }
                    d0 = s.charAt(++i);
                    d3i = Character.digit(d3, 16);
                    d2i = Character.digit(d2, 16);
                    d1i = Character.digit(d1, 16);
                    d0i = Character.digit(d0, 16);
                    if ( (d0i==-1) || (d1i==-1) || (d2i==-1) || (d3i==-1) ) {
                        result.append("\\u");
                        result.append(d3);
                        result.append(d2);
                        result.append(d1);
                        result.append(d0);
                        break;
                    }
                    result.append((char)((d3i<<12) | (d2i<<8) | (d1i<<4)|d0i));
                    break;
                case '"' :
                    result.append('"'); break;
                case '\'' :
                    result.append('\''); break;
                case '\\' :
                    result.append('\\');break;
                case '0' :
                case '1' :
                case '2' :
                case '3' :
                case '4' :
                case '5' :
                case '6' :
                case '7' :
                    d2 = c;
                    if ( i == lastIndex ) {
                        result.append('\\');
                        result.append(d2);
                        break;
                    }
                    d1 = s.charAt(++i);
                    if ( i == lastIndex ) {
                        result.append('\\');
                        result.append(d2);
                        result.append(d1);
                        break;
                    }
                    d0 = s.charAt(++i);
                    d2i = Character.digit(d2, 8);
                    d1i = Character.digit(d1, 8);
                    d0i = Character.digit(d0, 8);
                    if ( (d0i==-1) || (d1i==-1) || (d2i==-1) ) {
                        result.append('\\');
                        result.append(d2);
                        result.append(d1);
                        result.append(d0);
                        break;
                    }
                    result.append((char)((d2i<<6) | (d1i<<3) | d0i));
                    break;
                default   :
                    result.append('\\');
                    result.append(c);
                }
            }
        }

        return result.toString();
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

