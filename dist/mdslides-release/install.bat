@echo off
REM MDSlides Installation Script (Windows)
REM This script installs MDSlides to %APPDATA%\mdslides and adds it to PATH

setlocal enabledelayedexpansion

REM Get the directory where this script is located
set SCRIPT_DIR=%~dp0
set INSTALL_DIR=%APPDATA%\mdslides
set BIN_DIR=%INSTALL_DIR%\bin

echo ================================================================
echo   MDSlides Installation
echo ================================================================

REM Check Java is available
echo Checking Java installation...
where java >nul 2>nul
if errorlevel 1 (
  echo X Java not found. Please install Java 21+ and try again.
  echo   Download from: https://jdk.java.net/21/
  echo   Or use: choco install java17
  pause
  exit /b 1
)

for /f "tokens=*" %%i in ('java -version 2^>^&1 ^| findstr /R "version"') do (
  echo + Found: %%i
)

REM Create install directory structure
echo.
echo Installing to %INSTALL_DIR%...
if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"
if not exist "%INSTALL_DIR%\templates" mkdir "%INSTALL_DIR%\templates"
if not exist "%INSTALL_DIR%\examples" mkdir "%INSTALL_DIR%\examples"
if not exist "%INSTALL_DIR%\config" mkdir "%INSTALL_DIR%\config"

REM Copy JAR
if exist "%SCRIPT_DIR%mdslides.jar" (
  copy "%SCRIPT_DIR%mdslides.jar" "%BIN_DIR%\" >nul
  echo + Copied mdslides.jar
) else (
  echo X mdslides.jar not found in %SCRIPT_DIR%
  pause
  exit /b 1
)

REM Copy wrapper script
if exist "%SCRIPT_DIR%mdslides.bat" (
  copy "%SCRIPT_DIR%mdslides.bat" "%BIN_DIR%\" >nul
  echo + Copied mdslides.bat wrapper
) else (
  echo X mdslides.bat wrapper not found in %SCRIPT_DIR%
  pause
  exit /b 1
)

REM Copy templates
if exist "%SCRIPT_DIR%templates" (
  xcopy "%SCRIPT_DIR%templates" "%INSTALL_DIR%\templates" /E /I /Y >nul 2>&1
  echo + Copied templates
) else (
  echo - templates directory not found
)

REM Copy examples
if exist "%SCRIPT_DIR%examples" (
  xcopy "%SCRIPT_DIR%examples" "%INSTALL_DIR%\examples" /E /I /Y >nul 2>&1
  echo + Copied examples
) else (
  echo - examples directory not found
)

REM Create default config if not exists
if not exist "%INSTALL_DIR%\config\mdslides.conf" (
  (
    echo # MDSlides Configuration
    echo # This is the global (system-level) configuration
    echo # Project config and CLI flags override these settings
    echo.
    echo # Default theme: light, dark, or path to custom theme
    echo theme=light
    echo.
    echo # Copy images to output directory
    echo copy-images=true
    echo.
    echo # Skip accessibility validation
    echo skip-accessibility=false
    echo.
    echo # Default output directory (relative to deck file^)
    echo output-dir=./
    echo.
    echo # Template search path (colon-separated on Unix, semicolon on Windows^)
    echo # Paths are relative to %%APPDATA%%\mdslides
    echo template-path=.\templates\light;.\templates\dark
  ) > "%INSTALL_DIR%\config\mdslides.conf"
  echo + Created default config
) else (
  echo + Config already exists
)

REM Add to PATH
echo.
echo Configuring PATH...

REM Check if bin directory is already in PATH
echo %PATH% | find /I "%BIN_DIR%" >nul
if not errorlevel 1 (
  echo + PATH already configured
) else (
  setx PATH "%PATH%;%BIN_DIR%"
  echo + Added %BIN_DIR% to PATH
  echo   Note: You may need to restart your terminal for changes to take effect
)

echo.
echo ================================================================
echo   Installation Complete!
echo ================================================================
echo.
echo Next steps:
echo   1. Restart your terminal/command prompt
echo   2. Test installation: mdslides --version
echo   3. View tutorial: %INSTALL_DIR%\examples\tutorial.md
echo      (Rendered examples in tutorial-light\ and tutorial-dark\^)
echo.
echo Configuration:
echo   Global config: %INSTALL_DIR%\config\mdslides.conf
echo   Templates:     %INSTALL_DIR%\templates\
echo   Examples:      %INSTALL_DIR%\examples\
echo.
echo Quick start:
echo   mdslides my-deck.md
echo.
pause
