/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 * 
 * $Id: SSocketFactory.java,v 1.1 2002/10/20 14:46:04 jfn Exp $
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





/***************************************************************************
 *
 * Classes implementing this interface are used to create
 * sockets. Different implementations may create different types of
 * sockets. For instance, a particular implementation may create plain
 * <code>java.net.Socket</code> while one other may create
 * <code>javax.net.ssl.SSLSocket</code> instances.
 *
 ***************************************************************************/

interface SSocketFactory {





/***************************************************************************
 *
 * Creates a new TCP/IP socket.
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
        throws IOException;
}





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

