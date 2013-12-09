@echo off
setlocal

set EXAMPLES_DIR=%~dp0..

set EXAMPLE=%1
set EXAMPLE_PATH_BEGIN=com\tangosol\examples

if "%EXAMPLE%"=="" (
    echo usage: build example 
    echo where \"example\" is a directory under com.tangosol.examples
    echo. 
    echo example: build contacts
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
    echo set JAVA_HOME to a JDK 1.4.2 or greater installation.
    goto exit
  )


set CLASSES_DIR=%EXAMPLES_DIR%\classes
set CP="%NEW_COHERENCE_HOME%"\lib\coherence.jar;%CLASSES_DIR%
set EXAMPLE_PATH=%EXAMPLE_PATH_BEGIN%\%EXAMPLE%
set MODEL_PATH=%EXAMPLE_PATH_BEGIN%\model

@rem Perform build
echo building %EXAMPLE% from %EXAMPLE_PATH%

if NOT EXIST %CLASSES_DIR% mkdir %CLASSES_DIR%

dir %EXAMPLES_DIR%\src\%MODEL_PATH%\*.java /s /b > model-list.txt
"%NEW_JAVA_HOME%"\bin\javac -d %CLASSES_DIR% -source 1.4 -cp %CP% @model-list.txt

del model-list.txt

dir %EXAMPLES_DIR%\src\%EXAMPLE_PATH%\*.java /s /b > example-list.txt
"%NEW_JAVA_HOME%"\bin\javac -d %CLASSES_DIR% -source 1.4 -cp %CP% @example-list.txt
del example-list.txt

echo To run this example execute 'bin\run %EXAMPLE%'

:exit
