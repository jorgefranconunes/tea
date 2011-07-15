###########################################################################
#
# Copyright (c) 2008-2011 PDM&FC, All Rights Reserved.
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




Makefile.conf :
	@echo "***"
	@echo "*** File \"Makefile.conf\" is missing."
	@echo "*** See 00README-developers.txt for aditional details."
	@echo "***"
	@exit 1


#
# Compiles all the code.
# 
all : Makefile.conf
	@$(ANT_TOOL) $@


#
#
#
test : Makefile.conf
	@$(ANT_TOOL) $@


#
# Builds the Tea and Java documentation
#
doc : Makefile.conf
	@$(ANT_TOOL) $@

teadoc : Makefile.conf
	@$(ANT_TOOL) $@

javadoc : Makefile.conf
	@$(ANT_TOOL) $@


#
# Removes all Java class files and JARs.
#
clean :
	@$(ANT_TOOL) $@

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
release : Makefile.conf
	@$(ANT_TOOL) $@





###########################################################################
#
# 
#
###########################################################################

