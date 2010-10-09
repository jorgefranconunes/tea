/**************************************************************************
 *
 * Copyright (c) 2007-2010 PDM&FC, All Rights Reserved.
 *
 **************************************************************************/

/**************************************************************************
 *
 * $Id$
 *
 *
 * Revisions:
 *
 * 2007/06/24 Created. (jfn)
 *
 **************************************************************************/

package com.pdmfc.tea.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.net.URL;

import com.pdmfc.tea.util.SInputSource;





/**************************************************************************
 *
 * A factory for input sources. Supported input sources are files,
 * URLs supported by the JRE and URLs with the "resource" scheme.
 *
 **************************************************************************/

public class SInputSourceFactory
    extends Object {




    // URL protocols we handle in our own way.
    private static final String PROTOCOL_RESOURCE = "resource";





/**************************************************************************
 *
 * Retrieves the input stream associated with this input resource. The
 * input stream is opened th first time this method is called.
 *
 * @param source Either a file system path name or a URL.
 *
 * @exception IOException Thrown if there were problems opening the
 * input stream.
 *
 **************************************************************************/

    public static SInputSource createInputSource(String source)
        throws IOException {

        SInputSource result  = null;
        String      protocol = getProtocol(source);

        if ( protocol == null ) {
            result = new SFileInputSource(source);
        } else if ( protocol.equals(PROTOCOL_RESOURCE) ) {
            result = new SResourceInputSource(source);
        } else {
            result = new SUrlInputSource(source);
        }

        return result;
    }





/**************************************************************************
 *
 * Retrieves the input stream associated with the given input resource.
 *
 * @param sourceBase The base file system path or URL.
 *
 * @param source A path relative to <code>sourceBase</code>.
 *
 * @exception IOException Thrown if there were problems opening the
 * input stream.
 *
 **************************************************************************/

    public static SInputSource createInputSource(String sourceBase,
                                                 String source)
        throws IOException {

        SInputSource result  = null;
        String      protocol = getProtocol(sourceBase);

        if ( protocol == null ) {
            result = new SFileInputSource(sourceBase, source);
        } else if ( protocol.equals(PROTOCOL_RESOURCE) ) {
            result = new SResourceInputSource(sourceBase, source);
        } else {
            result = new SUrlInputSource(sourceBase, source);
        }

        return result;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static String getProtocol(String source) {

        int    colonIndex = source.indexOf(':');
        String protocol   =
            (colonIndex>1) ? source.substring(0, colonIndex) : null;
        
        // We also handle the case of Windows path names starting with
        // a drive letter. In that case "source" is not considered a
        // URL because the "protocol" would be only a characeter wide.

        return protocol;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static Reader openReader(String  source,
                                    Charset charset)
        throws IOException {

        if ( charset == null ) {
            charset = Charset.defaultCharset();
        }

        SInputSource inputSource = createInputSource(source);
        InputStream  input       = inputSource.openStream();
        Reader       reader      = new InputStreamReader(input, charset);
        Reader       bufReader   = new BufferedReader(reader);

        return bufReader;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static Reader openReader(String  sourceBase,
                                    String  source,
                                    Charset charset)
        throws IOException {

        if ( charset == null ) {
            charset = Charset.defaultCharset();
        }

        SInputSource inputSource = createInputSource(sourceBase, source);
        InputStream  input       = inputSource.openStream();
        Reader       reader      = new InputStreamReader(input, charset);
        Reader       bufReader   = new BufferedReader(reader);

        return bufReader;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static final class SFileInputSource
        extends Object
        implements SInputSource {





        private File _path = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public SFileInputSource(String path) {

            _path = new File(path);
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public SFileInputSource(String baseDir,
                                String path) {

            _path = new File(baseDir, path);
        }





/**************************************************************************
 *
 * @throws IOException When the given path could not be opened for
 * reading.
 *
 **************************************************************************/

        public InputStream openStream()
            throws IOException {

            InputStream result = new FileInputStream(_path);

            return result;
        }


    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static final class SUrlInputSource
        extends Object
        implements SInputSource {





        private URL _url = null;





/**************************************************************************
 *
 * @throws IOException When the given string does not represent a
 * valid URL or if the URL protocol is not supported.
 *
 **************************************************************************/

        public SUrlInputSource(String url)
            throws IOException {

            _url = new URL(url);
        }





/**************************************************************************
 *
 * @throws IOException When the given string does not represent a
 * valid URL or if the URL protocol is not supported.
 *
 **************************************************************************/

        public SUrlInputSource(String baseUrl,
                               String relativeUrl)
            throws IOException {

            String url = 
                baseUrl.endsWith("/") ? 
                (baseUrl + relativeUrl) :
                (baseUrl + "/" + relativeUrl);

            _url = new URL(url);
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public InputStream openStream()
            throws IOException {

            InputStream result = _url.openStream();

            return result;
        }

    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private static class SResourceInputSource
        extends Object
        implements SInputSource {





        private String _resource = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public SResourceInputSource(String resourceUri) {

            int colonIndex = resourceUri.indexOf(":");

            _resource = 
                (colonIndex<0) ?
                resourceUri :
                resourceUri.substring(1+colonIndex);
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public SResourceInputSource(String baseDirUri,
                                    String resource) {

            int    colonIndex = baseDirUri.indexOf(":");
            String baseDir    = 
                (colonIndex<0) ?
                baseDirUri :
                baseDirUri.substring(1+colonIndex);

            _resource = baseDir + "/" + resource;
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public InputStream openStream()
            throws IOException {

            InputStream result = 
                this.getClass().getResourceAsStream(_resource);

            if ( result == null ) {
                String msg = "No such resource - " + _resource;
                throw new IOException(msg);
            }

            return result;
        }

    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

