/**************************************************************************
 *
 * Copyright (c) 2001-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea;





/**************************************************************************
 *
 * Instances of this class represent byte arrays in the Tea program.
 *
 **************************************************************************/

public final class TeaByteArray
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

   public TeaByteArray() {

       setContents(new byte[0], 0, 0);
   }




/**************************************************************************
 *
 * Initializes the byte array.
 *
 * @param bytes
 *
 **************************************************************************/

   public TeaByteArray(final byte[] bytes) {

       setContents(bytes, 0, bytes.length);
   }





/**************************************************************************
 *
 * Initializes the byte array.
 *
 **************************************************************************/

   public TeaByteArray(final byte[] bytes,
                        final int    offset,
                        final int    count) {

       setContents(bytes, offset, count);
   }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public void setContents(final byte[] bytes,
                            final int    offset,
                            final int    count) {

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

