@echo off
setlocal

set EXAMPLES_DIR=%~dp0..
set EXAMPLE=%1
set EXAMPLE_PATH_BEGIN=com\tangosol\examples

echo EXAMPLES: %EXAMPLES_DIR%

if "%EXAMPLE%"=="" (
    echo usage: run example
    echo where \"example\" is a directory under com.tangosol.examples
    echo.
    echo example: run contacts
    echo.
    echo current directories:
    dir /b src\%EXAMPLE_PATH_BEGIN%
    goto exit
   )

rem remove any double quotes in the environment variables

for /f "tokens=* delims=" %%J in (%COHERENCE_HOME%) do ( set NEW_COHERENCE_HOME=%%J)
rem if there were no double quotes, the new environment variable will not be set
if "%NEW_COHERENCE_HOME%"=="" (
    set NEW_COHERENCE_HOME=%COHERENCE_HOME%
   )

for /f "tokens=* delims=" %%J in (%JAVA_HOME%) do ( set NEW_JAVA_HOME=%%J)
rem if there were no double quotes, the new environment variable will not be set
if "%NEW_JAVA_HOME%"=="" (
    set NEW_JAVA_HOME=%JAVA_HOME%
   )

if "%NEW_COHERENCE_HOME%"=="" (
    echo COHERENCE_HOME is not set!
    echo set COHERENCE_HOME to a Coherence 3.4.2 or later installation.
    goto exit
  )

if "%NEW_JAVA_HOME%"=="" (
    echo JAVA_HOME is not set!
    echo set JAVA_HOME to a JRE/JDK 1.4.2 or greater installation.
    goto exit
  )

set CONFIG=%EXAMPLES_DIR%\resource\config

set COH_OPTS=%COH_OPTS% -cp %CONFIG%;"%NEW_COHERENCE_HOME%"\lib\coherence.jar;%EXAMPLES_DIR%\classes
set COH_OPTS=%COH_OPTS% -Dtangosol.coherence.cacheconfig=%CONFIG%\examples-cache-config.xml

echo redirecting Coherence logging to %EXAMPLE%.log...

"%NEW_JAVA_HOME%"\bin\java %COH_OPTS% -Xms256m -Xmx256m ^
     -Dtangosol.coherence.distributed.localstorage=false ^
     -Dtangosol.coherence.log=%EXAMPLE%.log ^
     com.tangosol.examples.%EXAMPLE%.Driver contacts %EXAMPLES_DIR%\..\resource\contacts.csv %2 %3 %4 %5 %6 %7

:exit
