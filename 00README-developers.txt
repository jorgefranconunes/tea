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

1. JDK 1.2

2. jdeps 1.0.1 or better. This tool is used to generate the apropriate
   Makefile dependencies.

3. The gnu.regexp 1.0.8 library or better. Both 1.0.8 and 1.1.4
   versions are know to work. You can download it from
   "http://www.cacas.org/~wes/java/".

4. Library with an implementation of the SAX API. The Xerces-1.4.4
   library is known to work. You can download it from
   "http://xml.apache.org/xerces-j/".





Preparing the Development Environment
=====================================

These instructions apply to unix environments. If you really want to
develop under windows you should have CygWin
(http://sources.redhat.com/cygwin/) installed on your system.


1. Unpack the source tree.


2. Run the "configure" script found on the "bin" directory at the root
   of the distribution tree. You will need to pass it the apropriate
   options. See bellow for details. Running "configure" will create
   a"Makefile" at the root, needed for compiling the sources, and a
   "config/tsh.config" needed to run the "tsh" utility.

The "configure" tool accepts the following options:

--java-home=PATH

    Specifies the base dir of your Java instalation. The PATH is
    supposed to refer to a directory containing a "bin" directory with
    the following programs: java, javac, jar, javadoc. If this option
    is not specified then it will default to "/usr".

--jre=COMMAND
	
    Specifies the comand that will execute a Java program. It must
    recognize the "-D" and "-classpath" options. If this option is not
    specified then its value is taken to be "$PATH/bin/java" where
    PATH was specified through the "--java-home" option.

--javac=COMMAND

    Specifies the command that invokes the Java compiler. It must
    recognize the "-d" and "-classpath" options. If this option is not
    specified then its value is taken to be "$PATH/bin/javac" where
    PATH was specified through the "--java-home" option.

--jar=COMMAND

    Specifies the comand that invokes the "jar" utility. If this
    option is not specified then its value is taken to be
    "$PATH/bin/jar" where PATH was specified through the "--java-home"
    option.

--javadoc=COMMAND

    Specifies the command that invokes the "javadoc" utility. If this
    option is not specified then its value is taken to be
    "$PATH/bin/javadoc" where PATH was specified through the
    "--java-home" option.

--classpath=PATH_LIST

    Specifies the ClassPath needed to compile the Tea runtime sources
    and run the Tea interpeter. To compile you will need that
    PATH_LIST references a library containing the "org.xml.sax"
    package. If your Java environment needs any aditional libraries
    you should specify them here.

--include-jars=JARLIST

    Colon separated list of pathnames refering to JAR files. The
    contents of these JAR files will be included in the final Tea
    JAR. At least the JAR for the gnu.regexp library must be
    referenced.

You can run the "configure" multiple times. After the first time, if
you do not specify one of the options it will use the value specified
the previous time it was run. This is useful when you just want to
change compiler options or just add another component to the
classpath.




Building
========

To compile just run "make" at the distribution root. This will use the
"Makefile" created in the previous step. This "Makefile" recognizes
the following targets:

all - This is the default target. It will create the Tea interpreter.

javadoc - Builds the javadoc documentation for the runtime
internals. The HTML files will be created under the "doc/javadoc"
directory.

teadoc - Builds the Tea documentation from the source code. The HTML
files will be created under the "doc/teadoc" directory.

jar - Builds the target class files and creates the single JAR needed
to run a Tea interpreter.

depend - Figures out the Makefile dependencyes and produces the
Makefile rules used to compile the Java sources. Do a "make depend"
whenever a source file is added, renamed or removed from the source
tree.

clean - Removes all files created as the result of a build. The
configuration information is not affected.

After a successfull compilation you are ready to run the "tsh"
utility. It is located under the "bin" directory. You can add this
directory to your PATH environment variable.





Bug reports:

Send your bug reports and requests for new features to
tea.dev@pdmfc.com

