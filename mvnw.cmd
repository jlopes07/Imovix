@echo off
setlocal
if exist "C:\Program Files\Microsoft\jdk-25.0.4.101-hotspot\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Microsoft\jdk-25.0.4.101-hotspot"
    set "PATH=C:\Program Files\Microsoft\jdk-25.0.4.101-hotspot\bin;%PATH%"
)
cd /d "%~dp0backend"
call "mvnw.cmd" %*
endlocal
