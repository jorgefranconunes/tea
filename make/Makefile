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
# 2008/08/29 Slight refactoring to use new parameter names. (jfn)
#
# 2005/02/15 Refactored in order not to need the "jdeps" tool. (jfn)
#
# 2004/12/15 Added "doc/release-notes.txt" to the list of files to be
# included in a release. (jfn)
#
# 2003/04/16 Added a "doc" target. (jfn)
#
# 2001/05/07 Created. (jfn)
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

#
# The list of packages to build and put into the JAR.
#
TEA_PACKAGES = \
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
	com/pdmfc/tea/tos	\
	com/pdmfc/tea/util	\
	lib/tea	\
	lib/tea/deprecated	\
	lib/tea/html	\
	lib/tea/io	\
	lib/tea/net	\
	lib/tea/tdbc	\
	lib/tea/util	\
	lib/tea/xml	\

#
# The list of packages for which javadoc documentation will be generated.
#
JAVADOC_PACKAGES = \
	com.pdmfc.ep	\
	com.pdmfc.tea\
	com.pdmfc.tea.apps	\
	com.pdmfc.tea.compiler	\
	com.pdmfc.tea.modules	\
	com.pdmfc.tea.modules.io	\
	com.pdmfc.tea.modules.lang	\
	com.pdmfc.tea.modules.tdbc	\
	com.pdmfc.tea.modules.tos	\
	com.pdmfc.tea.modules.util	\
	com.pdmfc.tea.modules.xml	\
	com.pdmfc.tea.modules.net	\
	com.pdmfc.tea.modules.reflect	\
	com.pdmfc.tea.runtime	\
	com.pdmfc.tea.util	\
	com.pdmfc.tea.tos	\





###########################################################################
#
# 
#
###########################################################################

SEP = $(BUILD_PATH_SEPARATOR)
EMPTY =
SPACE = $(EMPTY) $(EMPTY)

SOURCE_BASE_DIR   = $(BUILD_BASE_DIR)/src
CLASSES_BASE_DIR  = $(BUILD_BASE_DIR)/lib/classes





###########################################################################
#
# Configurations for creating the jar containing the full Tea
# package.
#
###########################################################################

JAR_MANIFEST = $(BUILD_BASE_DIR)/config/JarManifest
JAR_NAME     = tea-$(BUILD_VERSION).jar
JAR_PATH     = $(BUILD_BASE_DIR)/lib/$(JAR_NAME)
JAR_FILES    = -C $(CLASSES_BASE_DIR) com -C $(CLASSES_BASE_DIR) lib/tea-$(BUILD_VERSION)
JAR          = $(BUILD_JAR)







###########################################################################
#
# Configurations for producing Tea documentation from the Java
# and Tea source files.
#
###########################################################################

TEADOC           = $(BUILD_BASE_DIR)/bin/teadoc
TEADOC_DIR       = $(BUILD_BASE_DIR)/doc/teadoc
TEADOC_DIR_LIST  = src
TEADOC_DOC_TITLE = "Tea $(BUILD_VERSION) Reference Documentation"
TEADOC_WIN_TITLE = $(TEADOC_DOC_TITLE)
TEADOC_HEADER    = "Tea $(BUILD_VERSION) Reference Documentation<br>&copy 2010 <a href=\"http://www.pdmfc.com\">PDM&amp;FC</a>"
TEADOC_FOOTER    = $(TEADOC_HEADER)
TEADOC_BOTTOM    = "<font size=\"-1\"><a href=\"mailto:info@pdmfc.com\">Report a bug or request new features</a></font><br><font size=\"-1\">&copy; 2010</font> <a href=\"http://www.pdmfc.com\"><img align=\"absmiddle\" border=\"0\" src=\"LogoSmallPdmfc.png\"></a>"
TEADOC_TMP       = $(patsubst %,$(BUILD_BASE_DIR)/%,$(TEADOC_DIR_LIST))
TEADOC_DIRLIST   = $(subst $(SPACE),$(SEP),$(TEADOC_TMP))





###########################################################################
#
# Configuration for producing Javadoc documentation from the Java
# source files.
#
###########################################################################

JAVADOC_DIR         = $(BUILD_BASE_DIR)/doc/javadoc
JAVADOC_WINDOWTITLE = "Tea $(BUILD_VERSION) Java Runtime API"
JAVADOC_DOCTITLE    = $(JAVADOC_WINDOWTITLE)
JAVADOC_HEADER      = "<b>Tea Java Runtime API</b><br><b>Version $(BUILD_VERSION)</b>"
JAVADOC_BOTTOM      = "<font size=\"-1\">&copy; 2010 <a href=\"http://www.pdmfc.com\" target=\"_top\">PDM&amp;FC</a>, All Rights Reserved.</font>"
JAVADOC             = $(BUILD_JAVADOC)





###########################################################################
#
# Some tools used inside the rules.
#
###########################################################################

JAVAC  = \
	$(BUILD_JAVAC) \
	-classpath "$(BUILD_MAKEFILE_CLASSPATH)$(SEP)$(SOURCE_BASE_DIR)" \
	-d "$(CLASSES_BASE_DIR)"
CP     = cp
MKDIR  = mkdir -p
DELETE = rm -rf
ECHO   = @echo
TOUCH  = touch





###########################################################################
#
# Variables required by sub-makefiles.
#
###########################################################################

BASE_DIR = $(BUILD_BASE_DIR)

export BASE_DIR
export IS_UNIX
export SOURCE_BASE_DIR
export CLASSES_BASE_DIR
export JAVAC
export CP
export MKDIR





###########################################################################
#
# And finally the rules themselves.
#
###########################################################################

.PHONY : default all jar clean doc teadoc javadoc release all-once

# Default target.
default : all


allMakefiles := $(TEA_PACKAGES:%=$(SOURCE_BASE_DIR)/%/Makefile)

ifdef BUILD_BASE_DIR
include $(allMakefiles)
endif

allTargets := $(ALL_CLASS_FILES) $(ALL_OTHER_TARGETS)
allSources := $(ALL_SOURCES)
allJavaFiles := $(filter %.java, $(allSources))


#
# And now the rules where the real action takes place.
#

#
# Compiles all the code.
# 
all : jar


# Builds the jar with the whole Tea package.
jar : $(JAR_PATH)

$(JAR_PATH) : $(CLASSES_BASE_DIR) $(allTargets) $(JAR_MANIFEST)
	$(MKDIR) $(CLASSES_BASE_DIR)/lib/tea-$(BUILD_VERSION)
	(cd $(CLASSES_BASE_DIR)/lib/tea; tar cf - .) | (cd $(CLASSES_BASE_DIR)/lib/tea-$(BUILD_VERSION); tar xf -)
	cd $(CLASSES_BASE_DIR) ; \
	$(JAR) cmf $(JAR_MANIFEST) $(JAR_PATH) ./com ./lib/tea-$(BUILD_VERSION)

$(CLASSES_BASE_DIR) :
	$(MKDIR) $@


#
# Compiles all the Java code with a single invocation of the compiler.
#
all-once : $(CLASSES_BASE_DIR) $(ALL_OTHER_TARGETS)
	$(JAVAC) $(allJavaFiles)
	$(MKDIR) $(CLASSES_BASE_DIR)/lib/tea-$(BUILD_VERSION)
	(cd $(CLASSES_BASE_DIR)/lib/tea; tar cf - .) | (cd $(CLASSES_BASE_DIR)/lib/tea-$(BUILD_VERSION); tar xf -)
	cd $(CLASSES_BASE_DIR) ; \
	$(JAR) cmf $(JAR_MANIFEST) $(JAR_PATH) ./com ./lib/tea-$(BUILD_VERSION)


# Removes all Java class files and JARs.
clean :
	$(DELETE) $(CLASSES_BASE_DIR)
	$(DELETE) $(BUILD_BASE_DIR)/lib/tea-*.jar
	$(DELETE) $(TEADOC_DIR)
	$(DELETE) $(JAVADOC_DIR)
	$(DELETE) src/com/pdmfc/tea/TeaConfig.properties
	$(DELETE) Makefile.conf


# Builds the Tea documentation
doc : teadoc javadoc


# Builds Javadoc documentation.
javadoc : 
	$(MKDIR) $(JAVADOC_DIR)
	$(JAVADOC) \
		-quiet \
		-d $(JAVADOC_DIR) \
		-sourcepath $(SOURCE_BASE_DIR) \
		-classpath "$(BUILD_MAKEFILE_CLASSPATH)" \
		-nodeprecatedlist	\
		-author \
		-splitIndex \
		-version \
		-windowtitle $(JAVADOC_WINDOWTITLE) \
		-doctitle $(JAVADOC_DOCTITLE) \
		-header $(JAVADOC_HEADER) \
		-bottom $(JAVADOC_BOTTOM) \
		$(JAVADOC_PACKAGES)
	$(ECHO) Done with javadoc.


# Builds the Tea documentation extracted from the source files.
teadoc : jar
	$(MKDIR) $(TEADOC_DIR)
	if [ ! -f ./config/tea-install.conf ] ; then ./bin/setup --java-home=$(BUILD_JAVA_HOME) ; fi
	$(TEADOC) \
		--header=$(TEADOC_HEADER) \
		--footer=$(TEADOC_FOOTER) \
		--bottom=$(TEADOC_BOTTOM) \
		--doc-title=$(TEADOC_DOC_TITLE) \
		--window-title=$(TEADOC_WIN_TITLE) \
		--path-list=$(TEADOC_DIRLIST) \
		--output-dir=$(TEADOC_DIR)
	$(CP) $(BUILD_BASE_DIR)/doc/images/LogoSmallPdmfc.png $(TEADOC_DIR)
	$(ECHO) Done with TeaDoc.

#
#
#
release : all doc
	$(BUILD_BASE_DIR)/bin/make-release $(TEA_RELEASE_FILES)
