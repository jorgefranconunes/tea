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
#
# Revisions:
#
# 2010/04/22 Created. (jfn) (#175)
#
###########################################################################





###########################################################################
#
# Configurations for creating the jar containing the full Tea
# package.
#
###########################################################################

JAR_CLASSES_DIR = $(BUILD_BASE_DIR)/lib/classes
JAR_MANIFEST    = $(BUILD_BASE_DIR)/config/JarManifest
JAR_NAME        = tea-$(BUILD_VERSION).jar
JAR_PATH        = $(BUILD_BASE_DIR)/lib/$(JAR_NAME)
JAR_FILES       = -C $(JAR_CLASSES_DIR) com -C $(JAR_CLASSES_DIR) lib/tea-$(BUILD_VERSION)
JAR_JAR         = $(BUILD_JAR)





###########################################################################
#
# And finally the rules themselves.
#
###########################################################################

.PHONY : jar-clean


# Builds the jar with the whole Tea package.
.jar-all : $(JAR_PATH)
	touch .jar-all

.jar-all-once : .java-all-once $(JAR_PATH)
	touch .jar-all
	touch .jar-all-once

$(JAR_PATH) : .java-all .tea-all $(JAR_MANIFEST)
	mkdir -p $(JAR_CLASSES_DIR)/lib/tea-$(BUILD_VERSION)
	(cd $(JAR_CLASSES_DIR)/lib/tea; tar cf - .) | (cd $(JAR_CLASSES_DIR)/lib/tea-$(BUILD_VERSION); tar xf -)
	cd $(JAR_CLASSES_DIR) ; \
	$(JAR_JAR) cmf $(JAR_MANIFEST) $(JAR_PATH) ./com ./lib/tea-$(BUILD_VERSION)


# Removes all Java class files and JARs.
jar-clean :
	rm -rf .jar-all .jar-all-once
	rm -rf $(BUILD_BASE_DIR)/lib/tea-*.jar

