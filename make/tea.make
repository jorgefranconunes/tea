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
# Defines targets for creating the dir tree containing the Tea code
# to be included in the final jar.
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
# The most commonly changed configuration parameters.
#
###########################################################################

#
# The list of packages to build and put into the JAR.
#
TEA_SOURCE_DIR_LIST = \
	lib/tea	\
	lib/tea/deprecated	\
	lib/tea/html	\
	lib/tea/io	\
	lib/tea/net	\
	lib/tea/tdbc	\
	lib/tea/tea/util	\
	lib/tea/util	\
	lib/tea/xml	\





###########################################################################
#
# 
#
###########################################################################

SEP = $(BUILD_PATH_SEPARATOR)
EMPTY =
SPACE = $(EMPTY) $(EMPTY)

TEA_SOURCE_DIR    = $(BUILD_BASE_DIR)/src
TEA_TARGET_DIR    = $(BUILD_BASE_DIR)/lib/classes
TEA_ALL_MAKEFILES = $(TEA_SOURCE_DIR_LIST:%=$(TEA_SOURCE_DIR)/%/Makefile)

ifdef BUILD_BASE_DIR
include $(TEA_ALL_MAKEFILES)
endif

TEA_TARGETS = $(filter %.tea, $(ALL_OTHER_TARGETS))





###########################################################################
#
# And finally the rules themselves.
#
###########################################################################

.PHONY : tea-clean

#
#
#
.tea-all : $(TEA_TARGETS)
	touch .tea-all


# Removes all Java class files and JARs.
tea-clean :
	rm -rf .tea-all
	rm -rf $(TEA_TARGET_DIR)

