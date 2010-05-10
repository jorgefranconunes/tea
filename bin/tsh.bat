@echo off

REM #######################################################################
REM #
REM # Copyright (c) 2001, 2002 PDM&FC, All Rights Reserved.
REM #
REM #######################################################################

REM
REM Utility script to run a Tea interpreter.
REM
REM You will need to configure the environment variables described
REM bellow to reflect your instalation. In tipical instalation
REM you will only to change the TEA_BASE_DIR environment variable
REM to match your instalation options.
REM


REM
REM TEA_BASE_DIR is the directory where the Tea instalation resides.
REM

SET TEA_BASE_DIR=c:\tea-3.2.5


REM
REM TEA_CLASSPATH is the list of the Java libraries needed by
REM the Tea runtime. It must at least contain the "gnu.regexp" library
REM and the "lib\classes" directory of the Tea instalation.
REM

SET TEA_CLASSPATH=%TEA_BASE_DIR%\lib\tea-3.2.5.jar


REM
REM TEA_LIBRARY is the list of directories with Tea libraries. These
REM are the directories searched by the Tea "import" function when
REM reading a file. It must at least contain the "lib/tea" directory
REM contained in the Tea runtime JAR file.
REM

SET TEA_LIBRARY=jar:file:%TEA_BASE_DIR%/lib/tea-3.2.5.jar!/lib/tea
SET TEA_LIBRARY=%TEA_LIBRARY%;.


REM
REM The DOS command used to invoke the Java virtual machine.
REM

SET TEA_JRE=java


REM
REM No more configurations bellow this point.
REM

%TEA_JRE% -classpath %TEA_CLASSPATH% -DTEA_LIBRARY=%TEA_LIBRARY% com.pdmfc.tea.apps.STeaShell %1 %2 %3 %4 %5 %6 %7 %8 %9

