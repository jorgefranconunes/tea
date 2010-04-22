###########################################################################
#
# Copyright (c) 2001-2010 PDM&FC, All Rights Reserved.
#
###########################################################################

###########################################################################
#
# $Id$
#
#
# The main Makefile for the Tea project. This makefile is actually
# included by another, smaller, makefile in the development root directory
# that defines apropriate configuration parameters (variables).
#
# The following variables are expected to be set with something
# reasonable:
#
# BUILD_BASE_DIR - 
#
# BUILD_VERSION - 
#
# BUILD_JAVA_HOME - 
#
# BUILD_JAVAC - 
#
# BUILD_JAR - 
#
# BUILD_JAVADOC - 
#
# BUILD_MAKEFILE_CLASSPATH -
#
#
# Revisions:
#
# 2010/04/22 Created. (jfn) (#175)
#
###########################################################################





###########################################################################
#
# The most commonly changed configuration parameters.
#
###########################################################################

#
# The files and directories that make up a Tea release. 
#
TEA_RELEASE_FILES =	\
	00README.txt	\
	apps	\
	config/tea-core.conf	\
	config/tea-install.conf.template	\
	bin/setup	\
	bin/teadoc	\
	bin/tea-utils	\
	bin/tsh	\
	doc/javadoc	\
	doc/release-notes.txt	\
	doc/teadoc	\
	doc/tea-mode.el	\
	doc/Memos/Faq.txt	\
	lib/*.jar	\





###########################################################################
#
# Variables required by included sub-makefiles.
#
###########################################################################

BASE_DIR = $(BUILD_BASE_DIR)

SEP   = $(BUILD_PATH_SEPARATOR)
EMPTY =
SPACE = $(EMPTY) $(EMPTY)




###########################################################################
#
# And finally the rules themselves.
#
###########################################################################

.PHONY : default all all-once doc clean release

# Default target.
default : all

ifdef BUILD_BASE_DIR
include $(BUILD_BASE_DIR)/make/java.make
include $(BUILD_BASE_DIR)/make/tea.make
include $(BUILD_BASE_DIR)/make/jar.make
include $(BUILD_BASE_DIR)/make/doc.make
endif

#
# Compiles all the code.
# 
all : .jar-all


#
# Compiles all the Java code with a single invocation of the compiler.
#
all-once : .jar-all-once


# Builds the Tea and Java documentation
doc : doc-all

teadoc : doc-teadoc

javadoc : doc-javadoc


# Removes all Java class files and JARs.
clean : java-clean tea-clean jar-clean doc-clean
	rm -rf src/com/pdmfc/tea/TeaConfig.properties
	rm -rf Makefile.conf

#
#
#
release : all doc
	$(BUILD_BASE_DIR)/bin/make-release $(TEA_RELEASE_FILES)

