@echo off

set JAVA_EXE=%JAVA_HOME%\bin\java.exe
IF EXIST "%JAVA_EXE%" goto start
set JAVA_EXE=java.exe

:start
set CP=.;lib\*
set JAVA_OPTS=""
"%JAVA_EXE%" %JAVA_OPTS% -classpath %CP% org.prismus.scrambler.log.Log4jLogstash %*
