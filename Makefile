###########################################################################
#
# Copyright (c) 2008 PDM&FC, All Rights Reserved.
#
###########################################################################

###########################################################################
#
# $Id$
#
#
# The main Makefile for the Tea project.
#
#
# Revisions:
#
# 2008/08/28 Created. (jfn)
#
###########################################################################





include Makefile.conf
include make/Makefile





Makefile.conf : build.conf config/tea-core.conf
	./bin/build-configure

build.conf :
	@echo "***"
	@echo "***File \"build.conf\" is missing."
	@echo "*** See 00README-developers.txt for aditional details."
	@echo "***"
	@exit 1





###########################################################################
#
# 
#
###########################################################################

