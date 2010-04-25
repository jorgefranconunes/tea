###########################################################################
#
# Copyright (c) 2008-2010 PDM&FC, All Rights Reserved.
#
###########################################################################

###########################################################################
#
# $Id$
#
#
# The main Makefile for the Tea project.
#
###########################################################################





#
# Include the configuration parameters. It will be automatically created
# if it does not yet exist.
#
include Makefile.conf

ANT_TOOL = $(BUILD_BASE_DIR)/bin/build-ant





###########################################################################
#
# 
#
###########################################################################

.PHONY : default all doc clean release

#
# Default target.
#
default : all





#
# Required by this makefile and by the build.xml for the Ant tool.
#
Makefile.conf : build.conf config/tea-core.conf config/Makefile.conf.template
	./bin/build-configure

build.conf :
	@echo "***"
	@echo "***File \"build.conf\" is missing."
	@echo "*** See 00README-developers.txt for aditional details."
	@echo "***"
	@exit 1


#
#Compiles all the code.
# 
all :
	@$(ANT_TOOL) $@


#
#
#
test :
	@$(ANT_TOOL) $@


#
# Builds the Tea and Java documentation
#
doc :
	@$(ANT_TOOL) $@

teadoc :
	@$(ANT_TOOL) $@

javadoc :
	@$(ANT_TOOL) $@


#
# Removes all Java class files and JARs.
#
clean :
	@$(ANT_TOOL) $@
	rm -rf Makefile.conf

#
#
#
release : all doc
	$(BUILD_BASE_DIR)/bin/make-release $(TEA_RELEASE_FILES)





###########################################################################
#
# 
#
###########################################################################

