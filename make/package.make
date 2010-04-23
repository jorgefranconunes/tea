###########################################################################
#
# Copyright (c) 2005-2010 PDM&FC, All Rights Reserved.
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
# SOURCES - List of source files used to build the targets. These can
# be java source files (files with a ".java" extension) or any other
# type of files. Java source files will be compiled and the
# corresponding class files will be put in the CLASSES_BASE_DIR
# directory. All other files will be copied to the corresponding
# location in the CLASSES_BASE_DIR directory.
#
# PACKAGE_DIR - Package directory relative to the source directory. I.e.
# the package to be compiled with "." replaced with "/".
#
# SOURCE_BASE_DIR - 
#
# CLASSES_BASE_DIR - 
#
# JAVA_JAVAC - 
#
###########################################################################





#
#
#
packageSourceDir := $(SOURCE_DIR)/$(PACKAGE_DIR)
packageTargetDir := $(TARGET_DIR)/$(PACKAGE_DIR)
javaSourceFiles  := $(filter %.java, $(SOURCES))
otherSourceFiles := $(filter-out $(javaSourceFiles), $(SOURCES))
javaClassFiles   := $(javaSourceFiles:%.java=$(packageTargetDir)/%.class)
otherTargetFiles := $(otherSourceFiles:%=$(packageTargetDir)/%)

ALL_SOURCES       := $(ALL_SOURCES) $(SOURCES:%=$(packageSourceDir)/%)
ALL_CLASS_FILES   := $(ALL_CLASS_FILES) $(javaClassFiles)
ALL_OTHER_TARGETS := $(ALL_OTHER_TARGETS) $(otherTargetFiles)





#
# Implicit rule for compiling Java source files in a particular package.
#
$(packageTargetDir)/%.class : $(packageSourceDir)/%.java
	$(JAVA_JAVAC) $<

#
# And for all other files that are not Java source files.
#
$(packageTargetDir)/% : $(packageSourceDir)/%
	mkdir -p $(dir $@)
	cp $< $@




###########################################################################
#
# 
#
###########################################################################

