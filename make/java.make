###########################################################################
#
# Copyright (c) 2010 PDM&FC, All Rights Reserved.
#
###########################################################################

###########################################################################
#
# $Id$
#
#
# Defines targets for compiling Java code.
#
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
###########################################################################





###########################################################################
#
# Some tools used inside the rules.
#
###########################################################################

#
# The list of packages to build and put into the JAR.
#
JAVA_SOURCE_DIR_LIST = \
	com/pdmfc/ep	\
	com/pdmfc/tea	\
	com/pdmfc/tea/apps	\
	com/pdmfc/tea/compiler	\
	com/pdmfc/tea/modules	\
	com/pdmfc/tea/modules/io	\
	com/pdmfc/tea/modules/lang	\
	com/pdmfc/tea/modules/tdbc	\
	com/pdmfc/tea/modules/tos	\
	com/pdmfc/tea/modules/util	\
	com/pdmfc/tea/modules/xml	\
	com/pdmfc/tea/modules/net	\
	com/pdmfc/tea/modules/reflect	\
	com/pdmfc/tea/runtime	\
	com/pdmfc/tea/util	\

JAVA_SOURCE_DIR = $(BUILD_BASE_DIR)/src
JAVA_TARGET_DIR = $(BUILD_BASE_DIR)/lib/classes

JAVA_JAVAC  = \
	$(BUILD_JAVAC) \
	-classpath "$(BUILD_MAKEFILE_CLASSPATH)$(SEP)$(JAVA_SOURCE_DIR)" \
	-d "$(JAVA_TARGET_DIR)"





###########################################################################
#
# And finally the rules themselves.
#
###########################################################################

.PHONY : java-clean


JAVA_ALL_MAKEFILES := $(JAVA_SOURCE_DIR_LIST:%=$(JAVA_SOURCE_DIR)/%/Makefile)

ifdef BUILD_BASE_DIR
include $(JAVA_ALL_MAKEFILES)
endif

JAVA_ALL_TARGETS := $(ALL_CLASS_FILES) $(ALL_OTHER_TARGETS)
JAVA_ALL_SOURCES := $(ALL_SOURCES)
JAVA_ALL_JAVA_SOURCES := $(filter %.java, $(JAVA_ALL_SOURCES))

#
# Compiles all the code.
# 
.java-all : $(JAVA_TARGET_DIR) $(JAVA_ALL_TARGETS)
	touch .java-all

$(JAVA_TARGET_DIR) :
	mkdir -p $@


#
# Compiles all the Java code with a single invocation of the compiler.
#
.java-all-once : $(JAVA_TARGET_DIR) $(JAVA_ALL_JAVA_SOURCES)
	$(JAVA_JAVAC) $(JAVA_ALL_JAVA_SOURCES)
	touch .java-all
	touch .java-all-once


# Removes all Java class files.
java-clean :
	rm -rf .java-all .java-all-once
	rm -rf $(JAVA_TARGET_DIR)
	rm -rf $(TEADOC_DIR)
	rm -rf $(JAVADOC_DIR)

