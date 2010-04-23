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
# Defines Makefile targets for building the final jar.
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
# Configurations for creating the jar containing the full Tea
# package.
#
###########################################################################

JAR_CLASSES_DIR := $(BUILD_BASE_DIR)/target/classes
JAR_MANIFEST    := $(BUILD_BASE_DIR)/config/JarManifest
JAR_DIR         := $(BUILD_BASE_DIR)/lib/jars
JAR_NAME        := tea-$(BUILD_VERSION).jar
JAR_TARGET      := $(JAR_DIR)/$(JAR_NAME)
JAR_FILES       := -C $(JAR_CLASSES_DIR) .
JAR_JAR         := $(BUILD_JAR)





###########################################################################
#
# And finally the rules themselves.
#
###########################################################################

.PHONY : jar-clean


# Builds the jar with the whole Tea package.
.jar-all : $(JAR_TARGET)
	touch .jar-all

.jar-all-once : .java-all-once $(JAR_TARGET)
	touch .jar-all
	touch .jar-all-once

$(JAR_TARGET) : .java-all .tea-all $(JAR_MANIFEST) $(JAR_DIR)
	$(JAR_JAR) cmf $(JAR_MANIFEST) $(JAR_TARGET) $(JAR_FILES)

$(JAR_DIR) :
	mkdir -p $(JAR_DIR)


# Removes all Java class files and JARs.
jar-clean :
	rm -rf .jar-all .jar-all-once
	rm -rf $(JAR_DIR)/tea-*.jar

