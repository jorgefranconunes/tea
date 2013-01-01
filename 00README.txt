The Tea Scripting Language
==========================

Tea is a scripting language for the Java environment. Go to the Tea
home page (http://www.pdmfc.com/tea) for lots of additional information.

This document contains instructions on how to install and setup Tea on
your system.





Installing and Configuring Tea on a Unix Environment
====================================================

The following instructions tell you how to install the Tea runtime
environment. They assume a unix like environment with some GNU tools
available (just "gzip" is enough). If you are working under Windows
see the following section (Installing and Configuring Tea on a
Microsoft Windows Environment). These instructions have been tested
under Linux and Solaris. They should even work for Windows if you have
something like CygWin installed (http://sources.redhat.com/cygwin/).


1. Download the latest Tea version from
http://www.pdmfc.com/tea


2. Download and install any additional Java packages you migh need.

If you intend to access relational databases from within Tea by using
the TDBC module you will need the appropriate JDBC drivers. Check with
your database vendor where you can get your JDBC driver. Tea has been
used in production environments with Postgresql, MySql, Oracle, Sybase,
Informix, SQL Server.


3. Unpack the Tea tar ball where you want the Tea package
installed. Do as follows:

cd wherever-you-want-to-install
gunzip -c tea-4.xx.yy.tar.gz | tar xvf -


4. Ensure the JAVA_HOME environment variable is set. It should point
to a Java 6 or later JRE. Aletrnatively you can run the tools under
the "bin" folder passing them a --jre=PATH option pointing to the JRE
binary.


5. To embed Tea into your java applications, you can use the JSR-223
API (please read the examples in doc/engineProgGuide.html) or read the
Tea doc/javadoc API.




Installing and Configuring Tea on a Microsoft Windows Environment
=================================================================

To run the Tea interpreter under Windows you will need to have a Java
virtual machine installed on your system. You can download one for the
Windows environment (XP, Vista, 7, etc) from "http://java.com".
After installing it (if you had not one already installed) follow the
instructions bellow in order to install the Tea runtime environment.


1. Download the latest Tea version from
http://www.pdmfc.com/tea


2. Download and install any additional Java packages you migh need.

If you intend to access relational databases from within Tea by using
the TDBC module you will need the appropriate JDBC drivers. Check with
your database vendor where you can get you JDBC driver. Tea has been
used in production environment with Postgresql, MySql, Oracle, Sybase,
Informix, SQL Server.


3. Unpack the Tea tar ball where you want the Tea package
installed. You will again need something like WinZip to unpack the
"tar.gz" file.


4. Make the necessary changes to the "%TEA_BASE_DIR%\bin\tsh.bat" file
to reflect your particular installation, where "TEA_BASE_DIR" is the
directory where you installed the Tea 3 package. Just edit it with
Notepad or your text editor of choice. This "tsh.bat" is the DOS batch
file used to launch a Tea interpreter from within the DOS
prompt. Inside you will find detailed instructions explaining what
needs to be configured.


5. Add the "%TEA_BASE_DIR%\bin" directory to your path so you can just
type "tsh" at the DOS prompt to launch the Tea interpreter. The "tsh"
command takes as first argument the name of the file containing the
code to execute. The remaining arguments are passed as command line
arguments to the Tea program.

If you run "tsh" with no arguments then it will read Tea commands from
the console. To test your installation you could type the following at
the DOS prompt:

c:\> tsh
echo "Hello, world!"
^Z

And you should see the "Hello, world!" string being displayed on the
console. Here "^Z" means the "Control" key and the "Z" key pressed
simultaneously (it signals an end of file condition from the console).


6. To embed Tea into your java applications, you can use the JSR-223
API (please read the examples in doc/engineProgGuide.html) or read the
Tea doc/javadoc API.




Bug Reports
===========

Send your bug reports and requests for new features to
tea.dev@pdmfc.com

