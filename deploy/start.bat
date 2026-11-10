chcp 65001 >nul

@echo off
echo ========================================
echo Рыболовный магазин — установка БД
echo ========================================
echo.

REM Проверка наличия Docker
where docker >nul 2>nul
if %errorlevel% neq 0 (
    echo Docker не найден. Попытка установить через winget...
    winget install Docker.DockerDesktop --accept-package-agreements --accept-source-agreements
    if %errorlevel% neq 0 (
        echo Не удалось установить Docker автоматически.
        echo Установите Docker Desktop вручную: https://www.docker.com/products/docker-desktop/
        pause
        exit /b 1
    )
    echo Docker установлен. Перезапустите скрипт после перезагрузки.
    pause
    exit /b 0
)

REM Проверка, запущен ли Docker
docker info >nul 2>nul
if %errorlevel% neq 0 (
    echo [ОШИБКА] Docker не запущен. Запустите Docker Desktop и повторите попытку.
    pause
    exit /b 1
)

echo [1/2] Запуск контейнера PostgreSQL...
docker-compose up -d

if %errorlevel% neq 0 (
    echo [ОШИБКА] Не удалось запустить контейнер.
    pause
    exit /b 1
)

echo [2/2] Ожидание готовности базы данных...
timeout /t 5 /nobreak >nul

echo.
echo ========================================
echo БАЗА ДАННЫХ УСПЕШНО РАЗВЁРНУТА!
echo ========================================
echo.
echo Подключение:
echo   Хост: localhost
echo   Порт: 5432
echo   БД:   fishing_shop
echo   Логин: admin
echo   Пароль: secretpassword
echo.
echo pgAdmin: http://localhost:8080
echo   Логин: admin@fishing.com
echo   Пароль: adminpassword
echo.
pause