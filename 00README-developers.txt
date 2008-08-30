The Tea Scripting Language
==========================

Tea is a scripting language for the Java environment. Go to the Tea
home page (http://www.pdmfc.com/tea-site/info) for lots of aditional
information.

This document contains instructions on how to setup the development
environment for working on Tea itself. It is intended for the Tea
development team.





Pre-Requisites
==============

1. Compiler for Java 1.5 or later.





Preparing the Development Environment
=====================================

These instructions apply to unix environments. If you really want to
develop under windows you should have CygWin
(http://www.cygwin.com/) installed on your system.


1. Fetch the source tree.


2. Create the "build.conf" file.

The "build.conf" file must be manually created in the top of the
development tree. It will contain information on the Java compiler to
be used during the build. It is a file with Bourne shell syntax and it
must define the variables described bellow. There is a
"build.conf.example" file with example values for these variables.

BUILD_JAVAC - The command that invokes the Java compiler and is used
to compile Java source files during build. It must accept the "-d" and
"-classpath" options just like Sun's JDK javac compiler.

BUILD_JAR - The command to use for creating JAR files. It is expected
to accept the same command line options as Sun's JDK jar utility.

BUILD_JAVADOC - The command to use for creating javadoc documentation
from Java source files. It is expected to accept the same command line
options as Sun's JDK javadoc utility.

This file can be changed at any time. The build procedure will always
use the values currently in this config file.


3. Prepare the development tree to behave like an installation tree.

Run the "bin/setup" utility in order to prepare the development tree
to behave like an installation tree. The only required command line
option is "--java-home". Run it with "--help" for more details.

Note that only after a successfull build will the development tree
actually behave like an install tree.





Building
========

To compile just run "make" at the distribution root. The "Makefile"
recognizes the following targets:

all - This is the default target. It will create the Tea
interpreter. After a successfull build the development tree also
behaves like an installation tree.

javadoc - Builds the javadoc documentation for the runtime
internals. The HTML files will be created under the "doc/javadoc"
directory.

teadoc - Builds the Tea documentation from the source code. The HTML
files will be created under the "doc/teadoc" directory.

jar - Builds the target class files and creates the single JAR needed
to run a Tea interpreter.

clean - Removes all files created as the result of a build. The
configuration information is not affected.

release - Creates a release package.





Bug reports:

Send your bug reports and requests for new features to
tea.dev@pdmfc.com

