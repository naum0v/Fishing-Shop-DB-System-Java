chcp 65001 >nul

@echo off
echo ========================================
echo Рыболовный магазин — запуск приложения
echo ========================================
echo.

REM Проверка наличия Java
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo Java не найдена. Попытка установить через winget...
    winget install EclipseAdoptium.Temurin.17.JRE --accept-package-agreements --accept-source-agreements
    if %errorlevel% neq 0 (
        echo Не удалось установить Java автоматически.
        echo Установите Java 17 или выше вручную: https://adoptium.net/temurin/releases/
        pause
        exit /b 1
    )
    echo Java установлена. Перезапустите скрипт.
    pause
    exit /b 0
)

REM Проверка наличия JAR-файла
if not exist "FishingShopApp.jar" (
    echo [ОШИБКА] Файл FishingShopApp.jar не найден.
    pause
    exit /b 1
)

echo Запуск приложения...
java -jar FishingShopApp.jar

pause