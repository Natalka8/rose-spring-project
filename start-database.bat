@echo off
chcp 65001 >nul
echo ========================================
echo    Rose Quest Database Starter
echo ========================================
echo.

:: Проверка установлен ли Docker
echo [1/5] Проверка установки Docker...
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ ОШИБКА: Docker не установлен!
    echo.
    echo Установите Docker Desktop с официального сайта:
    echo https://www.docker.com/products/docker-desktop/
    echo.
    pause
    exit /b 1
)
echo ✅ Docker установлен

:: Проверка установлен ли Docker Compose
echo [2/5] Проверка установки Docker Compose...
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo ❌ ОШИБКА: Docker Compose не установлен!
    echo.
    echo Установите Docker Desktop - он включает Docker Compose
    echo.
    pause
    exit /b 1
)
echo ✅ Docker Compose установлен

:: Проверка запущен ли Docker
echo [3/5] Проверка состояния Docker...
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ ОШИБКА: Docker не запущен!
    echo.
    echo Запустите Docker Desktop и дождитесь его полной загрузки
    echo.
    pause
    exit /b 1
)
echo ✅ Docker запущен

:: Запуск контейнеров
echo [4/5] Запуск контейнеров MySQL и phpMyAdmin...
docker-compose down >nul 2>&1
docker-compose up -d

if errorlevel 1 (
    echo ❌ ОШИБКА: Не удалось запустить контейнеры!
    echo.
    echo Возможные причины:
    echo - Порт 3306 или 8081 уже занят
    echo - Проблемы с сетью Docker
    echo - Недостаточно памяти
    echo.
    pause
    exit /b 1
)

:: Ожидание запуска MySQL
echo [5/5] Ожидание запуска MySQL...
timeout /t 15 /nobreak >nul

:: Проверка статуса контейнеров
echo.
echo ========================================
echo          СТАТУС КОНТЕЙНЕРОВ
echo ========================================
docker-compose ps

:: Финальная информация
echo.
echo ========================================
echo        БАЗА ДАННЫХ ЗАПУЩЕНА! ✅
echo ========================================
echo.
echo 📊 MySQL Database:
echo    Хост: localhost:3306
echo    База данных: rose_quest
echo    Пользователь: root
echo    Пароль: rootpassword
echo.
echo 🌐 phpMyAdmin (веб-интерфейс):
echo    URL: http://localhost:8081
echo    Сервер: mysql
echo    Пользователь: root
echo    Пароль: rootpassword
echo.
echo 🔧 Альтернативный пользователь:
echo    Пользователь: rose_user
echo    Пароль: rose_password
echo.
echo ⚡ Для остановки базы данных выполните:
echo    stop-database.bat
echo    или
echo    docker-compose down
echo.
echo Нажмите любую клавишу для выхода...
pause >nul