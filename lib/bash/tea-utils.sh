#!/bin/bash
###########################################################################
#
# Copyright (c) 2001-2012 PDM&FC, All Rights Reserved.
#
#
# Utility functions. This script is not meant to be executed by
# itself. Instead it is tipically sourced from within other scripts
# using the Bash "source" operator.
#
#########################################################################





###########################################################################
#
# Displays a message to stderr and exits the process.
#
# Arguments:
#
# 1. The message to be displayed.
#
###########################################################################

function teaError () {

    echo "***" $1 >&2
    exit 1
}





###########################################################################
#
# Determines the process current working directory.
#
###########################################################################

function teaCwd () {

    case "$OSTYPE" in
	cygwin )
	    cygpath --windows $(pwd) | sed 's/\\\/\\//g'
	    ;;
	* )
	    pwd
	    ;;
    esac
}





###########################################################################
#
# 
#
###########################################################################

function teaNormalizePath () {

    case "$OSTYPE" in
	cygwin )
	    cygpath --windows "$1" | sed 's/\\\/\\//g'
	    ;;
	* )
	    echo $1
    esac
}





###########################################################################
#
# Makes the core configuration parameters available.
#
# Arguments:
#
# 1. Tea installation base directory.
#
###########################################################################

function teaLoadCoreConfig () {

    local teaBaseDir=$1

    # The path of the core configuration parameters file.
    local configFile=${teaBaseDir}/conf/tea-core.conf

    # If it does not exist it means the installation tree is broken.
    # This is not supposed to happen.
    if [ -f ${configFile} ] ; then
	source ${configFile}
    else
	teaError "Tea core configuration file \"${configFile}\" not found."
    fi
}





###########################################################################
#
# Arguments:
#
# 1. The path for the template file.
#
###########################################################################

function teaParseTemplate () {

    tpt_tmpScript=/tmp/tea-parse-template.$$

    echo "cat <<EOF" > $tpt_tmpScript
    sed '/^%%/d' "$1" >> $tpt_tmpScript || exit 1
    echo "EOF" >> $tpt_tmpScript

    source $tpt_tmpScript

    rm -f $tpt_tmpScript
}





###########################################################################
#
# Echos the username of the user running the current process.
#
###########################################################################

function teaWhoAmI () {

    expr "$(id)" : "uid=[0-9]*(\([^)]*\).*"
}





###########################################################################
#
# Echos the hosname of the machine running the current process.
#
###########################################################################

function teaHostname () {

    uname -n
}





###########################################################################
#
# Echoes the system current time in a format apropriate for display.
#
###########################################################################

function teaDate () {

    date "+%Y/%m/%d %H:%M:%S"
}





###########################################################################
#
# Retrieves the operating system type.
#
###########################################################################

function teaOsType () {

    uname -s
}





###########################################################################
#
# Retrieves the path separator character. It depends on the platform.
#
###########################################################################

function teaPathSeparator () {

    case $(teaOsType) in
	*CYGWIN* )
	    echo ";"
	    return
	    ;;
	* )
	    echo ":"
	    return
    esac
}





###########################################################################
#
# 
#
###########################################################################

