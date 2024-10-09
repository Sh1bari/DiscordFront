@echo off
REM Путь к папке с JAR файлом
set JAR_OUTPUT_PATH=D:\discord\deviceproductiondesktop\deviceProduction\out\artifacts\deviceProduction_jar

REM Путь к JAR файлу
set JAR_FILE=%JAR_OUTPUT_PATH%\deviceProduction.jar

REM Путь для создания EXE
set EXE_OUTPUT_PATH=D:\TestApp

REM Путь к кастомной JDK, созданной с помощью jlink
set CUSTOM_JDK_PATH=D:/custom-jdk

REM Проверка, что JAR файл существует
if exist %JAR_FILE% (
    echo JAR file found: %JAR_FILE%.
) else (
    echo JAR file not found. Exiting.
    exit /b 1
)

REM Команда для создания EXE файла с помощью jpackage
echo Creating EXE file...
call jpackage --input %JAR_OUTPUT_PATH% --name DeviceProduction --main-jar deviceProduction.jar --main-class com.example.JavaFxApplication ^
    --type exe --runtime-image %CUSTOM_JDK_PATH% --app-version 1.0 --win-dir-chooser --win-shortcut --win-menu --win-menu-group "DeviceProduction" ^
    --dest %EXE_OUTPUT_PATH%

REM Проверка, был ли создан EXE файл
if %errorlevel% neq 0 (
    echo Failed to create EXE file. Exiting.
    exit /b 1
)

echo EXE file created successfully at %EXE_OUTPUT_PATH%.
pause
