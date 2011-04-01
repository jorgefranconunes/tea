The Tea Scripting Language
==========================



Preparing the Development Environment under Eclipse Java IDE
============================================================

These instructions apply to building the lib/jars/tea-4.Y.Z.jar
from an Eclipse Java Development IDE, regardless of the underlying
operating system.

THESE INSTRUCTIONS ARE NOT OFICIALLY SUPPORTED AS PART OF THE Tea
RELEASE, and may disappear in future releases, although PDM provides
them in order to ease the setup of Tea development for those who
prefer this popular IDE environment.

If you whish participate in making an official Tea release archive,
(after contacting the release team), please read the instructions
in the 00README-developers.txt file.

PDM does not support the maintenance of the Eclipse project settings
folder .project (and as such, it is not part of the release), so please
check with your project manager (or PDM services) which are the recommended
project settings (coding conventions, etc...).

Tea uses an Ant 1.7 compatible build file, so, the following
instructions are based on importing existing sources, and using
an ant build script under Eclipse.


1. Fetch the source tree from the Subversion repository, or
   from the tarball.
   (It is out of the scope of this 00README to help you with
   setting up a subversion client, or handling .tar.gz archives.)

1.1 If using the SVN Repository browser perspective on URL
    svn+ssh://ssh.pdmfc.com/opt/svnroot/tea, checkout the trunk
    (it should automatically choose project name "tea").

1.2 If using the tarball, extract it to your disk. The project
    root will be a folder called tea-4.0.0 (the version number will change).
    For the rest of these instructions, please rename the folder
    name "tea-4.0.0" to "tea" to avoid any misunderstandings.
    (You can keep the folder name with the version number if you whish.
    This is a mere suggestion for 1st timers witout SVN ssh access).

   You should be left with a directory tree, such as:
   
   tea
      00README.txt
      00README-developers.txt
      00README-developers-eclipse.txt # this file
      build.xml
      Makefile.conf.example     # You can ignore this under eclipse
      bin                       # shell script executable files
      apps                      # libraries used by some executables
      config                    # Tea release information
      devtools                  # scripts to help build Tea from the
                                #   command line. You don't need them
                                #   to build Tea under Eclipse.
      lib                       # jars used by the application
      src                       # tea and java source files, and resources
      target                    # output directory - created on 1st build
      third-party               # 3rd party tools and libraries, needed
                                #   for building Tea. This folder
                                #   contains the Ant and JUnit tools,
                                #   so that you may build Tea from the
                                #   command-line regardless of your IDE's
                                #   version of Ant and JUnit tools.
                                #   You will not be using this folder
                                #   for building Tea under Eclipse. 
      tests                     # old test scripts
      
2. (If you have extracted tea using the SVN Repository browser,) On the
    workspace, Deleted the generic "tea" project without deleting the project
    contents on disk.

3. In the Eclipse IDE, use menu File, New, Java Project, Project name: tea
   Check "Create new project in workspace".
   Press "Next". It should open the "Java Settings" dialog.

3.1. In the "Java Settings" dialog (which is poped-up), in the Source
     tab:
     
3.1.1. Delete the tea/src folder and add two folders
         tea/src/main/java
         tea/src/test/java
       as the Java source folders.
       
3.1.2. Change the "Default output folder:" from tea/bin to
     tea/target/classes. (Do not remove the tea/bin folder).

3.2. In the "Java Settings" dialog (which is poped-up), in the Libraries
     tab, click "Add Library" and add JUnit 4 libraries to the project.
     (Is this is your first attempt to setup Tea, you may skip this step
     and come back here later, after reading step 4 and dealing with
     errors regarding missing JUnit classes).

3.3. Click finish. (If eclipse asks you to remove the tea/bin folder and
     its contents, don't do it - say No.).

4. If you whish to use the JUnit libraries provided with the source release
   or have already setup a build.conf file with the JUnit libraries,
   please skip to step 5. Otherwise please read these 4.* steps on how to
   configure JUnit libraries suitable for your IDE environment.
  
4.1 Selecting the tea project, menu File, New , File, create a file
    named "build.conf" in the root of the tea project (having tea as the
    parent folder).

4.2. Edit (and save the file) such as it contains a line defining a
     property BUILD_JUNIT_JAR with the list of jars (separated by ;)
     needed by the JUnit framework you are using.
Example:
----- CUT HERE ------
BUILD_JUNIT_JAR=D:/eclipseSr1/eclipse/plugins/org.junit_4.8.1.v4_8_1_=v20100427-1100/junit.jar;D:/eclipseSr1/eclipse/plugins/org.hamcrest.core_1.=1.0.v20090501071000.jar
----- CUT HERE ------
     If you attempted to use your IDE JUnit 4 framework, the jars
     here should be the same.
     Using libraries older that 4.8 might not allow the tests to
     compile and run.
     Also, if you use the IDE to generate code templates, there
     is a risk that some code details are incompatible with the
     JUnit version supplied with the official release source tree. 

4.3. You may wish to change the JUnit libraries setup in step 3.2.

5. Open the build.xml file, by double-clicking on it under the expanded
   tea project, in the package explorer.

5.1. On the Outline window, the build.xml ant targets are show. Run the target
     of your choice by right clicking, and select "Run As, Ant Build".
     Please read the next section for an explanation of the targets available
     and their purpose.
     Observe the console with the log of the project build.




Building
========

The Ant build.xml file supports the following targets:

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
condition.

release - Creates a release package.









Bug reports:

Send your bug reports and requests for new features to
tea.dev@pdmfc.com

