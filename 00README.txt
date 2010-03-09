The Tea Scripting Language
==========================

Tea is a scripting language for the Java environment. Go to the Tea
home page (http://www.pdmfc.com/tea-site/info) for lots of aditional
information.

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
http://www.pdmfc.com/tea-site/info/download.html


2. Download and install any aditional Java packages you migh need.

If you intend to access relational databases from within Tea by using
the TDBC module you will need the apropriate JDBC drivers. Check with
your database vendor where you can get your JDBC driver. Tea has been
used in production environments with Postgresql (6.x and 7.0), MySql
(3.22), Oracle (7.3 and 8i), Sybase, Informix, SQL Server.


3. Unpack the Tea tar ball where you want the Tea package
installed. Do as follows:

cd wherever-you-want-to-install
gunzip -c tea-3.xx.yy.tar.gz | tar xvf -


4. Setup the installation by running the "setup" tool. The "setup"
tool can be found in the "bin" directory at the root of the
instalation tree. This is needed to configure the Java Runtime
Environment to use and the Java libraries needed by the Tea runtime
(e.g. JDBC drivers).

The "setup" tool accepts the following options:

--java-home=PATH

	Specifies the base dir of your Java instalation. The PATH is
	supposed to refer to a directory containing a "bin" directory
	with the a "java" program. If this option is not specified
	then its value is taken to be "/usr".


--jre=COMMAND

	Specifies the comand that will execute a Java program. It must
	recognize the "-D" and "-classpath" options. If this option is
	not specified then its value is taken to be "$PATH/bin/java"
	where PATH was specified through the "--java-home" option.

--jre-options==STRING

	Set of options passed as argument to the JRE. This option is
	not mandatory. It defaults to an empty string, that is, the JRE
	is invoked with no aditional options.

--classpath=PATH_LIST

	Specifies the Class Path needed to run the Tea interpeter. If
	your Java environment needs any aditional libraries you should
	specify them here (e.g.  Kaffe 1.0.x needs "Klasses.jar"). If
	you are going to use the TDBC module then you should also
	specify here the libraries for your JDBC drivers.


As an example, supposing you have a Kaffe instalation under "/usr" you
would do as follows:

cd wherever-you-want-to-install/tea-3.xx.yy
./bin/setup --java-home=/usr --classpath=/usr/share/kaffe/Klasses.jar


5. You will probably want to run the Tea shell ("bin/tsh") just by
writing "tsh" at the command prompt. To achieve that it will suffice
to add "wherever-you-installed-tea/tea-3.xx.yy/bin" to your PATH. You
can also put somewhere in your PATH a soft link pointing to
"wherever-you-installed-tea/tea-3.xx.yy/bin/tsh".





Installing and Configuring Tea on a Microsoft Windows Environment
=================================================================

To run the Tea interpreter under Windows you will need to have a Java
virtual machine installed on your system. You can download one for the
Windows environment (95, 98, 2000, NT4) from
"http://java.sun.com/j2se/1.3/download-windows.html". After installing
it (if you had not one already installed) follow the instructions
bellow in order to install the Tea runtime environment.


1. Download the latest Tea version from
http://www.pdmfc.com/tea-site/info/download.html


2. Download and install any aditional Java packages you migh need.

If you intend to access relational databases from within Tea by using
the TDBC module you will need the apropriate JDBC drivers. Check with
your database vendor where you can get you JDBC driver. Tea has been
used in production environment with Postgresql (6.x and 7.0), MySql
(3.22), Oracle (7.3, 8i), Sybase, Informix, SQL Server.


3. Unpack the Tea tar ball where you want the Tea package
installed. You will again need something like WinZip to unpack the
"tar.gz" file.


4. Make the necessary changes to the "%TEA_BASE_DIR%\bin\tsh.bat" file
to reflect your particular instalation, where "TEA_BASE_DIR" is the
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
the console. To test your instalation you could type the following at
the DOS prompt:

c:\> tsh
echo "Hello, world!"
^Z

And you should see the "Hello, world!" string being displayed on the
console. Here "^Z" means the "Control" key and the "Z" key pressed
simultaneously (it signals an end of file condition from the console).





Bug Reports
===========

Send your bug reports and requests for new features to
tea.team@pdmfc.com

