###########################################################################
#
# Copyright (c) 2005-2010 PDM&FC, All Rights Reserved.
#
###########################################################################

###########################################################################
#
# $Id$
#
# Makefile for the "com.pdmfc.tea" package.
#
# The following variables are expected to be defined:
#
# BUILD_BASE_DIR - The project root directory.
#
#
# Revisions:
#
# 2005/02/15 Created. (jfn)
#
###########################################################################





PACKAGE_DIR := com/pdmfc/tea

SOURCES := \
	SConfigInfo.java \
	STeaException.java \
	TeaConfig.properties


$(SOURCE_BASE_DIR)/$(PACKAGE_DIR)/TeaConfig.properties : $(BUILD_BASE_DIR)/config/tea-core.conf $(SOURCE_BASE_DIR)/$(PACKAGE_DIR)/TeaConfig.properties.template
	$(BUILD_BASE_DIR)/bin/process-template $(SOURCE_BASE_DIR)/com/pdmfc/tea/TeaConfig.properties.template > $@


include $(BUILD_BASE_DIR)/make/package.make





###########################################################################
#
# 
#
###########################################################################

