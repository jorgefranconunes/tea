###########################################################################
#
# Copyright (c) 2008-2010 PDM&FC, All Rights Reserved.
#
#
# The main Makefile for the Tea development tree.
#
###########################################################################





#
# The script used to launch the Ant tool.
#
ANT_TOOL = ./devtools/bin/build-ant





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
# Required by the build.xml for the Ant tool.
#
build.conf : Makefile.conf config/tea-core.conf devtools/conf/build.conf.template
	./devtools/bin/build-configure

Makefile.conf :
	@echo "***"
	@echo "*** File \"Makefile.conf\" is missing."
	@echo "*** See 00README-developers.txt for aditional details."
	@echo "***"
	@exit 1


#
# Compiles all the code.
# 
all : build.conf
	@$(ANT_TOOL) $@


#
#
#
test : build.conf
	@$(ANT_TOOL) $@


#
# Builds the Tea and Java documentation
#
doc : build.conf
	@$(ANT_TOOL) $@

teadoc : build.conf
	@$(ANT_TOOL) $@

javadoc : build.conf
	@$(ANT_TOOL) $@


#
# Removes all Java class files and JARs.
#
clean :
	@$(ANT_TOOL) $@
	rm -rf build.conf

#
# Resets the working area to its initial condition. 
#
distclean :
	rm -rf build.conf
	rm -rf target
	rm -rf devtools/apache-ant-*
	rm -rf devtools/lib
	rm -rf lib/jars

#
#
#
release : all doc
	./devtools/bin/build-release





###########################################################################
#
# 
#
###########################################################################

