@echo off
REM MDSlides Wrapper Script (Windows)
REM Executes the MDSlides JAR with proper classpath and config directory references

setlocal enabledelayedexpansion

REM Determine the directory where this script is located
set SCRIPT_DIR=%~dp0

REM Set up paths
if not defined MDSLIDES_HOME (
  set MDSLIDES_HOME=%APPDATA%\mdslides
)
set MDSLIDES_JAR=%SCRIPT_DIR%mdslides.jar
set MDSLIDES_CONFIG=%MDSLIDES_HOME%\config
set MDSLIDES_TEMPLATES=%MDSLIDES_HOME%\templates

REM Check if JAR exists
if not exist "%MDSLIDES_JAR%" (
  echo Error: mdslides.jar not found at %MDSLIDES_JAR%
  echo Please reinstall MDSlides
  exit /b 1
)

REM Execute the JAR, passing all arguments through
java -jar "%MDSLIDES_JAR%" %*
exit /b %errorlevel%
