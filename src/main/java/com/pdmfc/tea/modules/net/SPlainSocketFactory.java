/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 * 
 * $Id$
 *
 *
 * Revisions:
 *
 * 2002/10/19
 * Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.modules.net;

import java.io.IOException;
import java.net.Socket;

import com.pdmfc.tea.modules.net.SSocketFactory;





/***************************************************************************
 *
 * Creates sockets of the regular kind. The sockets created by this
 * factory are instances of the <code>java.net.Socket</code> class.
 *
 ***************************************************************************/

class SPlainSocketFactory
    extends Object
    implements SSocketFactory {





    /**
     *
     */
    public static final SSocketFactory SELF = new SPlainSocketFactory();





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    public SPlainSocketFactory() {
    }





/***************************************************************************
 *
 * Creates a new instance of <code>java.net.Socket</code>.
 *
 * @param host The name of the host which the socket will be connected
 * to.
 *
 * @param port The number of the port in the remote <code>host</code>
 * the created socket will connect to.
 *
 * @return The newly created socket.
 *
 * @exception java.io.IOException Thrown if the socket could not be
 * successfuly created.
 *
 ***************************************************************************/

    public Socket createSocket(String host,
                               int    port)
        throws IOException {

        Socket result = new Socket(host, port);

        return result;
    }
}





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

