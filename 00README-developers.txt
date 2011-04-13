The Tea Scripting Language
==========================

Tea is a scripting language for the Java environment. Go to the Tea
home page (http://www.pdmfc.com/tea) for lots of additional
information.

This document contains instructions on how to setup the development
environment for working on Tea itself. It is intended for the Tea
development team.

This development tree is for the 4.y.z main development trunk.





Pre-Requisites
==============

1. Compiler for Java 1.6 or later.





Preparing the Development Environment
=====================================

These instructions apply to unix-like environments. If you really want to
develop under windows you should have Cygwin (http://www.cygwin.com/)
installed on your system, or use a Java IDE.

If you want to use the Eclipse Java IDE, please read the file named
00README-developer-eclipse.txt, and stop reading this file here.

If you want to use the Netbeans Java IDE, please read the file named
00README-developer-netbeans.txt, and stop reading this file here.



1. Fetch the source tree from the Subversion repository.

For more insight on the file layout, read the section
"Guide to the Source Tree" below.


2. Create the "Makefile.conf" file.

The "Makefile.conf" file must be manually created at the top of the
development tree. It will contain information on the Java compiler to
be used during the build. It is a file with Bourne shell syntax and it
must define the variables described bellow. There is a
"Makefile.conf.example" file with example values for these variables.

BUILD_JAVA_HOME - The java home of the JDK to be used for compiling
and for seting up the development environment as a Tea installation.

This file can be changed at any time. The build procedure will always
use the values currently in this config file.

If using Cygwin, please supply the Cygwin path to the JDK directory.
Example:
BUILD_JAVA_HOME="/cygdrive/c/Program Files/Java/jdk1.6.0_17"




Building
========

To compile just run "make" at the working copy root. The "Makefile" is a
wrapper to calling Ant targets defined in the build.xml file.
Both make's "Makefile" and Ant's "build.xml" recognize the following targets:

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

test - Runs all unit tests.

clean - Removes all files created as the result of a build. The
configuration information is not affected.

distclean - Removes all files that may have been created during the
build procedure, returning the development tree to its pristine
condition. This target only exists in the Makefile, and not in the
Ant's build.xml file (because running this target removes the Ant 1.8
distribution setup from under devtools).

release - Creates a release package.





Making a New Release (for admins)
====================

1. Check the release version number.

Ensure that the version number is coherent in all files
("config/tea-core.conf" and "bin/tsh.bat").


2. Build the release bundle.

Execute the Makefile target to build the release archive:

    make release

The release bundle is named "tea-4.Y.Z.tar.gz" and should be available
at the working copy root.


3. Perform testing on the release.

Test the release, by trying out the installation of the archive
according to the Tea site's instructions.


4. Update the release on the Subversion repository

    svn cp -m 'Tea 4.Y.Z release.' \
         -r NNN \
        svn+ssh://ssh.pdmfc.com/opt/svnroot/tea/trunk \
        svn+ssh://ssh.pdmfc.com/opt/svnroot/tea/releases/4.Y.Z


5. Update the Tea site.

If it is a public release, update the Tea site.




Guide to the Source Tree
========================

This section is intended to give a hint on the purpose of the layout
of the files and folders in the Tea source distribution.

   tea
      00README.txt              # This is the main README file in the binary
                                #   distribution.
      00README-developers.txt   # This file. (Not included in the binary
                                #   distribution.)
      00README-developers-netbeans.txt # Instructions for using this
                                #   specific IDE
      00README-developers-eclipse.txt # Instructions for using this
                                #   specific IDE
      build.xml                 # Ant build file.
      Makefile.conf.example     # Template for creating the Makefile.conf file.
                                #   You can ignore this file when working
                                #   with an Ant based IDE (such as Netbeans
                                #   or Eclipse).
      bin                       # shell script executable files
      apps                      # libraries used by some executables
      config                    # Tea release information
      devtools                  # software tools and scripts to help build Tea
                                #   from the command line. You don't need them
                                #   to build Tea under an IDE.
      lib                       # Jars used by the application, and that will
                                #   be included in the binary distribution.
      src                       # tea and java source files, and resources
      target                    # output directory - created on 1st build
                                #   The distribution archives created by the
                                #   "release" target also appear here.
      third-party               # 3rd party tools and libraries, needed
                                #   for building Tea. This folder
                                #   contains the Ant and JUnit tools,
                                #   so that you may build Tea from the
                                #   command-line regardless of your IDE's
                                #   version of Ant and JUnit tools.
                                #   You will not be using this folder
                                #   for building Tea under Eclipse or Netbeans.
      tests                     # old test scripts




Bug reports:

Send your bug reports and requests for new features to
tea.dev@pdmfc.com

