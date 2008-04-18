###########################################################################
#
# Copyright (c) 2005-2008 PDM&FC, All Rights Reserved.
#
###########################################################################

###########################################################################
#
# $Id$
#
#
# Makefile for the "com.pdmfc.tea" package.
#
# The following variables are expected to be defined:
#
# BASE_DIR - The project root directory.
#
#
# Revisions:
#
# 2005/02/15 Created. (jfn)
#
###########################################################################





SOURCES= \
	SConfigInfo.java \
	STeaException.java \
	TeaConfig.properties


TeaConfig.properties : TeaConfig.properties.template
	$(BASE_DIR)/bin/process-template $< > $@


include $(BASE_DIR)/make/package.make





###########################################################################
#
# 
#
###########################################################################

