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
# Makefile for a Java package. This file is always included from a
# makefile in the package source directory.
#
# This makefile expects the following variables to be set to something
# reasonable:
#
# IS_UNIX - 
#
# PACKAGE_DIR - Package directory relative to the source directory. I.e.
# the package to be compiled with "." replaced with "/".
#
# SOURCE_BASE_DIR - 
#
# CLASSES_BASE_DIR - 
#
# JAVAC - 
#
# CP - 
#
# MKDIR - 
#
#
# Revisions:
#
# 2005/02/15 Created. (jfn)
#
###########################################################################





#
#
#
packageSourceDir = $(SOURCE_BASE_DIR)/$(PACKAGE_DIR)
packageTargetDir = $(CLASSES_BASE_DIR)/$(PACKAGE_DIR)
javaSourceFiles  = $(filter %.java, $(SOURCES))
otherSourceFiles = $(filter-out $(javaSourceFiles), $(SOURCES))
javaClassFiles   = $(javaSourceFiles:%.java=$(packageTargetDir)/%.class)
otherTargetFiles = $(otherSourceFiles:%=$(packageTargetDir)/%)





#
# Implicit rule for compiling Java files.
#
$(packageTargetDir)/%.class : $(packageSourceDir)/%.java
	$(JAVAC) $<

#
# And for all other files that are not Java source files.
#
$(packageTargetDir)/% : $(packageSourceDir)/%
	$(CP) $< $@

#
# Compiles all the required Java source files and copies other files
# to the classes directory.
#
build : $(packageTargetDir) $(javaClassFiles) $(otherTargetFiles)

$(packageTargetDir) :
	$(MKDIR) $@





###########################################################################
#
# 
#
###########################################################################

