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
	JAVA_HOME=$(BUILD_JAVA_HOME) $(BUILD_ANT) $@


#
#
#
test :
	JAVA_HOME=$(BUILD_JAVA_HOME) $(BUILD_ANT) $@


#
# Builds the Tea and Java documentation
#
doc :
	JAVA_HOME=$(BUILD_JAVA_HOME) $(BUILD_ANT) $@

teadoc :

javadoc :


#
# Removes all Java class files and JARs.
#
clean :
	JAVA_HOME=$(BUILD_JAVA_HOME) $(BUILD_ANT) $@
	rm -rf src/com/pdmfc/tea/TeaConfig.properties
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

