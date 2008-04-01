/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id: SObjByteArray.java,v 1.1 2001/07/11 13:58:28 jfn Exp $
 *
 *
 * Revisions:
 *
 * 2001/05/12
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;





/**************************************************************************
 *
 * Instances of this class represent byte arrays in the Tea program.
 *
 **************************************************************************/

public class SObjByteArray
    extends Object {





    private byte[] _bytes;
    private int    _offset;
    private int    _count;
    private String _string;






/**************************************************************************
 *
 * Initializes an empty byte array.
 *
 **************************************************************************/

   public SObjByteArray() {

       setContents(new byte[0], 0, 0);
   }




/**************************************************************************
 *
 * Initializes the byte array.
 *
 * @param bytes
 *
 **************************************************************************/

   public SObjByteArray(byte[] bytes) {

       setContents(bytes, 0, bytes.length);
   }





/**************************************************************************
 *
 * Initializes the byte array.
 *
 **************************************************************************/

   public SObjByteArray(byte[] bytes,
			int    offset,
			int    count) {

       setContents(bytes, offset, count);
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void setContents(byte[] bytes,
			    int    offset,
			    int    count) {

       _bytes  = bytes;
       _offset = offset;
       _count  = count;
       _string = null;
    }





/**************************************************************************
 *
 * Fetches the array of bytes stored in this object.
 *
 * @return
 *    A byte array.
 *
 **************************************************************************/

    public byte[] getBytes() {

	return _bytes;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public int getOffset() {

	return _offset;
    }





/**************************************************************************
 *
 * Fetches the number of bytes stored.
 *
 * @return
 *    A byte array.
 *
 **************************************************************************/

    public int getCount() {

	return _count;
    }





/**************************************************************************
 *
 * Fetches the conversion to a string of this byte array.
 *
 * @return
 *    The string resulting from the byte array conversion.
 *
 **************************************************************************/

    public String getStringValue() {

	if ( _string == null ) {
	    _string = new String(_bytes, _offset, _count);
	}
	return _string;
    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

