/**************************************************************************
 *
 * Copyright (c) 2001 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 * 
 * $Id: SSslSocketFactory.java,v 1.1 2002/10/20 14:46:04 jfn Exp $
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
import javax.net.ssl.SSLSocketFactory;

import com.pdmfc.tea.modules.net.SSocketFactory;





/***************************************************************************
 *
 * Creates sockets for SSL connections.The implementation is
 * based on Sun's JSSE 1.0.2 API. The default <code>SSLSocketFactory</code>
 * is used. Sockets created through this API can be configured
 * (e.g. which certificate to use) through system and security properties.
 * See the JSSE API Users's Guide document for full details.
 *
 * The client part of SSL connection must recognize the final certificate
 * in the SSL server certificate chain as a trusted certificate. In order
 * for this to happen the client SSL API must have access to a keystore
 * with the trusted certificates. By default the files
 * <code>${JAVA_HOME}/lib/security/jssecacerts</code> and
 * <code>${JAVA_HOME}/lib/security/cacerts</code> are used as repository
 * of trusted certificates. It is possible to use other keystore of
 * trusted certificates by specifying its path name as the value of
 * the system property <code>javax.net.ssl.trustStore</code>.
 *
 ***************************************************************************/

class SSslSocketFactory
    extends Object
    implements SSocketFactory {





    public static final SSocketFactory SELF = new SSslSocketFactory();





    static {
        java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
    }





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

    public SSslSocketFactory() {
    }





/***************************************************************************
 *
 * Creates a new socket that will be used for SSL connections.  The
 * actual socket instance is obtained from the default
 * <code>javax.net.ssl.SSLSocketFactory</code>.
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

        Socket result = SSLSocketFactory.getDefault().createSocket(host, port);

        return result;
    }


}





/***************************************************************************
 *
 *
 *
 ***************************************************************************/

