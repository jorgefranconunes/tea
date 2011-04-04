The Tea Scripting Language
==========================



Preparing the Development Environment under Netbeans Java IDE
=============================================================

These instructions apply to building the lib/jars/tea-4.Y.Z.jar
from an Netbeans Java Development IDE, regardless of the underlying
operating system.

THESE INSTRUCTIONS ARE NOT OFICIALLY SUPPORTED AS PART OF THE Tea
RELEASE, and may disappear in future releases, although PDM provides
them in order to ease the setup of Tea development for those who
prefer this popular IDE environment.

If you wish participate in making an official Tea release archive,
(after contacting the release team), please read the instructions
in the 00README-developers.txt file.

PDM does not support the maintenance of the Netbeans project settings
folder .nbproject (and as such, it is not part of the release), so please
check with your project manager (or PDM services) which are the recommended
project settings (coding conventions, etc...).

Tea uses an Ant 1.7 compatible build file, so, the following instructions
are based on importing existing sources as a Java free-form project.


1. Fetch the source tree from the Subversion repository, or
   from the tarball.
   (It is out of the scope of this 00README to help you with
   setting up a subversion client, or handling .tar.gz archives.)

1.1 If using the tarball, extract it to your disk. The project
    root will be a folder called tea-4.0.0 (the version number will change).
    For the rest of these instructions, please rename the folder
    name "tea-4.0.0" to "tea" to avoid any misunderstandings.
    (You can keep the folder name with the version number if you wish.
    This is a mere suggestion for 1st timers without SVN ssh access).

   You should be left with a directory tree, as described in the file
   00README-developer.txt, section "Guide to the Source Tree".

3. In the Netbeans IDE, use menu File, New Project, Categories=Java,
   Projects=Java Free-Form Project, and press "Next".

3.1. In the "Name and Location" step, set the
       Location=... path to the root folder that you have just extracted
                    in step 1.1

     Netbeans should automatically fill out the other fields with
       Build script   =... Location path/build.xml
       Project Name   = Tea
       Project Folder =... Location path
 
     Press "Next".

3.2. In the "Build and Run Actions" set, check that Netbeans has already
     filled for you the fields:

       Build Project    = all
       Clean Project    = clean
       Generate Javadoc = javadoc
       Run Project      = <blank ... you can fill it out later>
       Test Project     = test

     Press "Next".

3.3. In the "Source Package Folders" step, 

     In the "Source Package Folders", add the ...src/main/java folder.

     The "Test Package Folders" should automatically contain
     src/test/java.

     Set the "Source Level" to JDK 1.5 or higher, and the encoding to
     ISO-8859-1 (although the encoding is irrelevant, as the Tea source
     should not use any non-ASCII characters).

     Press "Next".

3.4. In the "Java Sources Classpath" step, select the 
        Source Package Folder = src/test/java 
     and then "Add JAR/Folder" ...target/classes (even if this
     folder does not yet exist).

     JUnit library jars (included in the distribution) are not yet setup,
     so you will add them in step 3.7.

     Press "Next".

3.5. In the "Project Output" step, select
        Source Package Folder = src/main/java 
     and then "Add JAR/Folder" ...target/classes (even if this
     folder does not yet exist).
     Select the 
        Source Package Folder = src/test/java 
     and then "Add JAR/Folder" ...target/test-classes (even if this
     folder does not yet exist).

     Set 
       Javadoc Output = doc/javadoc

     Press "Finish".

     The src/test/java classes are showing a compilation error mark
     (as the target folder was not yet created and the JUnit libraries
     are not yet setup). Please ignore this for now.

3.6. In menu "Run", execute the "Test Project (Tea)" option.
     The project should compile and all the tests should pass 100% ok.

3.7. In the File menu, select the "Project Properties (Tea)".
     Select the 
        Source Package Folder = src/test/java 
     and then "Add JAR/Folder" ...devtools/lib/jars/junit-4.8.2.jar
     or similar (minor/micro version may vary) which should now exist.

     After pressing Ok, compilation error marks should disappear.

4. Open the build.xml file, by double-clicking on it under the expanded
   tea project, in the package explorer.

4.1. On the Outline window, the build.xml ant targets are show. Run the target
     of your choice by right clicking, and select "Run Target, ...".
     Please read the 00README-developers.txt "Building" section for an
     explanation of the targets available and their purpose.
     Observe the console with the log of target execution.



