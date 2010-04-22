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
# Configuration for producing Javadoc documentation from the Java
# source files.
#
###########################################################################

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

JAVADOC_SOURCE_DIR  = $(BUILD_BASE_DIR)/src
JAVADOC_DIR         = $(BUILD_BASE_DIR)/doc/javadoc
JAVADOC_WINDOWTITLE = "Tea $(BUILD_VERSION) Java Runtime API"
JAVADOC_DOCTITLE    = $(JAVADOC_WINDOWTITLE)
JAVADOC_HEADER      = "<b>Tea Java Runtime API</b><br><b>Version $(BUILD_VERSION)</b>"
JAVADOC_BOTTOM      = "<font size=\"-1\">&copy; 2010 <a href=\"http://www.pdmfc.com\" target=\"_top\">PDM&amp;FC</a>, All Rights Reserved.</font>"
JAVADOC             = $(BUILD_JAVADOC)







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
# And finally the rules themselves.
#
###########################################################################

.PHONY : doc-all doc-teadoc doc-javadoc doc-clean


# Builds the Tea documentation
doc-all : doc-javadoc doc-teadoc


# Builds Javadoc documentation.
doc-javadoc : 
	mkdir -p $(JAVADOC_DIR)
	$(JAVADOC) \
		-quiet \
		-d $(JAVADOC_DIR) \
		-sourcepath $(JAVADOC_SOURCE_DIR) \
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
	@echo Done with javadoc.


# Builds the Tea documentation extracted from the source files.
doc-teadoc : .jar-all
	mkdir -p $(TEADOC_DIR)
	if [ ! -f ./config/tea-install.conf ] ; then ./bin/setup --java-home=$(BUILD_JAVA_HOME) ; fi
	$(TEADOC) \
		--header=$(TEADOC_HEADER) \
		--footer=$(TEADOC_FOOTER) \
		--bottom=$(TEADOC_BOTTOM) \
		--doc-title=$(TEADOC_DOC_TITLE) \
		--window-title=$(TEADOC_WIN_TITLE) \
		--path-list=$(TEADOC_DIRLIST) \
		--output-dir=$(TEADOC_DIR)
	cp $(BUILD_BASE_DIR)/doc/images/LogoSmallPdmfc.png $(TEADOC_DIR)
	@echo Done with TeaDoc.


# Removes all Java class files and JARs.
doc-clean :
	rm -rf $(TEADOC_DIR)
	rm -rf $(JAVADOC_DIR)

