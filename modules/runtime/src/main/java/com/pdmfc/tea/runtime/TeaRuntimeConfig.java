/**************************************************************************
 *
 * Copyright (c) 2012-2014 PDMFC, All Rights Reserved.
 *
 **************************************************************************/

package com.pdmfc.tea.runtime;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;





/**************************************************************************
 *
 * Set of configuration parameters required for initializing a
 * <code>STeaRuntime</code>.
 *
 **************************************************************************/

public final class TeaRuntimeConfig
    extends Object {





    private String       _argv0              = null;
    private String[]     _argv               = null;
    private Charset      _sourceCharset      = null;
    private List<String> _importLocationList = null;





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    private TeaRuntimeConfig(final Builder builder) {

        _argv0         = builder._argv0;
        _argv          = builder._argv;
        _sourceCharset = builder._sourceCharset;

        List<String> importlocationListCopy =
            new ArrayList<String>(builder._importLocationList);

        _importLocationList =
            Collections.unmodifiableList(importlocationListCopy);
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public String getArgv0() {

        return _argv0;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public String[] getArgv() {

        return _argv;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public Charset getSourceCharset() {

        return _sourceCharset;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public List<String> getImportLocationList() {

        return _importLocationList;
    }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

    public static final class Builder
        extends Object {





        private String       _argv0              = null;
        private String[]     _argv               = {};
        private Charset      _sourceCharset      = null;
        private List<String> _importLocationList = new ArrayList<String>();





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        private Builder() {

            // Nothing to do.
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public Builder setArgv0(final String argv0) {

            _argv0 = argv0;

            return this;
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public Builder setArgv(final String[] argv) {

            _argv = Arrays.copyOf(argv, argv.length);

            return this;
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public Builder setSourceCharset(final Charset sourceCharset) {

            _sourceCharset = sourceCharset;

            return this;
        }





/**************************************************************************
 *
 * Specifies the list of locations for importing source files from
 * within the Tea script. The locations can be either file system path
 * names or URLs.
 *
 * @param importLocationList List of strings representing file system
 * path names or URLs.
 *
 * @return This builder object.
 *
 **************************************************************************/

        public Builder
            setImportLocationList(final List<String> importLocationList) {

            if ( importLocationList != null ) {
                _importLocationList = new ArrayList<String>(importLocationList);
            } else {
                _importLocationList.clear();
            }

            return this;
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public TeaRuntimeConfig build() {

            TeaRuntimeConfig result = new TeaRuntimeConfig(this);

            return result;
        }





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

        public static Builder start() {

            Builder result = new Builder();

            return result;
        }


    }


}





/**************************************************************************
 *
 * 
 *
 **************************************************************************/

