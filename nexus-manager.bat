@echo off
echo ===============================================
echo    Nexus Repository Manager - 통합 관리도구
echo ===============================================

:: 관리자 권한 확인
>nul 2>&1 "%SYSTEMROOT%\system32\cacls.exe" "%SYSTEMROOT%\system32\config\system"
if '%errorlevel%' NEQ '0' (
    echo 이 스크립트는 관리자 권한이 필요합니다.
    echo 관리자 권한으로 다시 실행해주세요.
    pause
    exit /B 1
)

set NEXUS_HOME=C:\nexus
set NEXUS_DATA=C:\nexus-data
set NEXUS_VERSION=3.45.0-01

:MAIN_MENU
echo.
echo ===============================================
echo         Nexus Repository Manager
echo ===============================================
echo 1. Nexus 설치 (처음 사용 시)
echo 2. Nexus 시작/중지/상태 확인
echo 3. Repository 설정
echo 4. 연결 테스트
echo 5. 로그 확인
echo 6. 초기 비밀번호 확인
echo 7. 웹 브라우저에서 열기
echo 8. 문제 해결 진단
echo 9. 완전 제거
echo 0. 종료
echo ===============================================
set /p CHOICE="선택하세요 (0-9): "

if "%CHOICE%"=="1" goto INSTALL
if "%CHOICE%"=="2" goto MANAGE
if "%CHOICE%"=="3" goto SETUP_REPOS
if "%CHOICE%"=="4" goto TEST_CONNECTION
if "%CHOICE%"=="5" goto VIEW_LOGS
if "%CHOICE%"=="6" goto CHECK_PASSWORD
if "%CHOICE%"=="7" goto OPEN_BROWSER
if "%CHOICE%"=="8" goto DIAGNOSE
if "%CHOICE%"=="9" goto UNINSTALL
if "%CHOICE%"=="0" goto END

echo 잘못된 선택입니다.
goto MAIN_MENU

:INSTALL
echo.
echo ===============================================
echo         Nexus Repository 설치
echo ===============================================
echo.

echo [1/8] Java 설치 확인...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java가 설치되지 않았습니다.
    echo Java 17 이상을 설치해주세요: https://adoptium.net/
    pause
    goto MAIN_MENU
)

echo [2/8] 기존 설치 정리...
if exist "%NEXUS_HOME%" (
    rmdir /s /q "%NEXUS_HOME%" 2>nul
)
if exist "%NEXUS_DATA%" (
    rmdir /s /q "%NEXUS_DATA%" 2>nul
)

echo [3/8] 설치 디렉토리 생성...
mkdir "%NEXUS_HOME%" 2>nul
mkdir "%NEXUS_DATA%" 2>nul

echo [4/8] Nexus 다운로드... (약 250MB, 2-3분 소요)
powershell -Command "Invoke-WebRequest -Uri 'https://download.sonatype.com/nexus/3/nexus-%NEXUS_VERSION%-win64.zip' -OutFile '%NEXUS_HOME%\nexus.zip' -UseBasicParsing"

if not exist "%NEXUS_HOME%\nexus.zip" (
    echo ERROR: 다운로드에 실패했습니다. 인터넷 연결을 확인해주세요.
    pause
    goto MAIN_MENU
)

echo [5/8] 압축 해제...
powershell -Command "Expand-Archive -Path '%NEXUS_HOME%\nexus.zip' -DestinationPath '%NEXUS_HOME%\temp' -Force"

echo [6/8] 파일 이동...
xcopy "%NEXUS_HOME%\temp\nexus-%NEXUS_VERSION%\*" "%NEXUS_HOME%\" /E /Y /Q

echo [7/8] 정리...
rmdir /s /q "%NEXUS_HOME%\temp" 2>nul
del "%NEXUS_HOME%\nexus.zip" 2>nul

echo [8/8] 설치 확인...
if exist "%NEXUS_HOME%\bin\nexus.exe" (
    echo ✅ Nexus 설치 완료!
    echo.
    echo 다음 단계: 옵션 2에서 Nexus를 시작하세요.
) else (
    echo ❌ 설치 실패
)

pause
goto MAIN_MENU

:MANAGE
echo.
echo ===============================================
echo         Nexus 서비스 관리
echo ===============================================
echo.
echo 1. Nexus 시작 (백그라운드)
echo 2. Nexus 중지
echo 3. Nexus 상태 확인
echo 4. 메인 메뉴로 돌아가기
echo.
set /p SERVICE_CHOICE="선택하세요 (1-4): "

if "%SERVICE_CHOICE%"=="1" goto START_NEXUS
if "%SERVICE_CHOICE%"=="2" goto STOP_NEXUS
if "%SERVICE_CHOICE%"=="3" goto STATUS_NEXUS
if "%SERVICE_CHOICE%"=="4" goto MAIN_MENU

:START_NEXUS
echo.
echo Nexus 시작 중... (2-3분 소요)
start /B "Nexus" "%NEXUS_HOME%\bin\nexus.exe" /run
echo.
echo Nexus가 백그라운드에서 시작되었습니다.
echo 완전히 시작되려면 2-3분이 걸립니다.
echo.
echo 상태 확인: 옵션 3 선택
echo 웹 접속: http://localhost:8081
pause
goto MANAGE

:STOP_NEXUS
echo.
echo Nexus 중지 중...
taskkill /F /IM nexus.exe 2>nul
taskkill /F /IM java.exe /FI "WINDOWTITLE eq Nexus" 2>nul
echo Nexus가 중지되었습니다.
pause
goto MANAGE

:STATUS_NEXUS
echo.
echo ===============================================
echo         Nexus 상태 확인
echo ===============================================
echo.
echo [1] 프로세스 상태:
tasklist | findstr java.exe | findstr /v grep >nul
if %errorlevel% equ 0 (
    echo ✅ Java 프로세스 실행 중
) else (
    echo ❌ Java 프로세스 없음
)

echo.
echo [2] 포트 8081 상태:
netstat -an | findstr :8081 | findstr LISTENING >nul
if %errorlevel% equ 0 (
    echo ✅ 포트 8081 LISTENING
) else (
    echo ❌ 포트 8081 사용되지 않음
)

echo.
echo [3] 웹 서비스 테스트:
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081' -TimeoutSec 5 -UseBasicParsing; Write-Host '✅ 웹 서비스 정상: HTTP' $response.StatusCode } catch { Write-Host '❌ 웹 서비스 접근 불가' }" 2>nul

pause
goto MANAGE

:SETUP_REPOS
echo.
echo ===============================================
echo         Repository 설정
echo ===============================================
echo.
echo 이 기능은 Nexus가 시작되고 admin 로그인이 완료된 후에 사용하세요.
echo.
set /p ADMIN_PASSWORD="Nexus admin 비밀번호를 입력하세요: "

echo.
echo [1/4] 연결 테스트...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/service/rest/v1/status' -UseBasicParsing; Write-Host '✅ 연결 성공' } catch { Write-Host '❌ 연결 실패'; exit 1 }"
if %errorlevel% neq 0 (
    echo Nexus가 시작되지 않았거나 접근할 수 없습니다.
    pause
    goto MAIN_MENU
)

echo.
echo [2/4] Maven Releases Repository 생성...
powershell -Command "$cred = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes('admin:%ADMIN_PASSWORD%')); try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/service/rest/v1/repositories/maven/hosted' -Method POST -Headers @{'Authorization'='Basic '+$cred; 'Content-Type'='application/json'} -Body '{\"name\":\"maven-releases\",\"online\":true,\"storage\":{\"blobStoreName\":\"default\",\"strictContentTypeValidation\":true,\"writePolicy\":\"ALLOW_ONCE\"},\"maven\":{\"versionPolicy\":\"RELEASE\",\"layoutPolicy\":\"STRICT\"}}' -UseBasicParsing; Write-Host '✅ maven-releases 생성 성공' } catch { Write-Host '⚠️ maven-releases 이미 존재하거나 생성 실패' }"

echo.
echo [3/4] Maven Snapshots Repository 생성...
powershell -Command "$cred = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes('admin:%ADMIN_PASSWORD%')); try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/service/rest/v1/repositories/maven/hosted' -Method POST -Headers @{'Authorization'='Basic '+$cred; 'Content-Type'='application/json'} -Body '{\"name\":\"maven-snapshots\",\"online\":true,\"storage\":{\"blobStoreName\":\"default\",\"strictContentTypeValidation\":true,\"writePolicy\":\"ALLOW\"},\"maven\":{\"versionPolicy\":\"SNAPSHOT\",\"layoutPolicy\":\"STRICT\"}}' -UseBasicParsing; Write-Host '✅ maven-snapshots 생성 성공' } catch { Write-Host '⚠️ maven-snapshots 이미 존재하거나 생성 실패' }"

echo.
echo [4/4] Maven Proxy Repository 생성...
powershell -Command "$cred = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes('admin:%ADMIN_PASSWORD%')); try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/service/rest/v1/repositories/maven/proxy' -Method POST -Headers @{'Authorization'='Basic '+$cred; 'Content-Type'='application/json'} -Body '{\"name\":\"maven-proxy\",\"online\":true,\"storage\":{\"blobStoreName\":\"default\",\"strictContentTypeValidation\":true},\"proxy\":{\"remoteUrl\":\"https://repo1.maven.org/maven2/\",\"contentMaxAge\":1440,\"metadataMaxAge\":1440},\"negativeCache\":{\"enabled\":true,\"timeToLive\":1440},\"httpClient\":{\"blocked\":false,\"autoBlock\":true},\"maven\":{\"versionPolicy\":\"MIXED\",\"layoutPolicy\":\"STRICT\"}}' -UseBasicParsing; Write-Host '✅ maven-proxy 생성 성공' } catch { Write-Host '⚠️ maven-proxy 이미 존재하거나 생성 실패' }"

echo.
echo Repository 설정이 완료되었습니다.
echo 웹 UI에서 Browse 메뉴를 통해 확인할 수 있습니다.
pause
goto MAIN_MENU

:TEST_CONNECTION
echo.
echo ===============================================
echo         연결 테스트
echo ===============================================
echo.
echo [1/3] Nexus 웹 서비스 테스트...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081' -UseBasicParsing; Write-Host '✅ 웹 UI: HTTP' $response.StatusCode } catch { Write-Host '❌ 웹 UI 접근 실패' }"

echo.
echo [2/3] Nexus REST API 테스트...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/service/rest/v1/status' -UseBasicParsing; Write-Host '✅ REST API: HTTP' $response.StatusCode } catch { Write-Host '❌ REST API 접근 실패' }"

echo.
echo [3/3] Repository 접근 테스트...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/repository/maven-central/' -UseBasicParsing; Write-Host '✅ Repository 접근 가능' } catch { Write-Host '⚠️ Repository 접근 제한 (정상일 수 있음)' }"

pause
goto MAIN_MENU

:VIEW_LOGS
echo.
echo ===============================================
echo         로그 확인
echo ===============================================
echo.
if exist "%NEXUS_DATA%\log\nexus.log" (
    echo 최근 Nexus 로그 (마지막 20줄):
    echo ===============================================
    powershell -Command "Get-Content '%NEXUS_DATA%\log\nexus.log' -Tail 20"
) else (
    echo 로그 파일이 없습니다.
    echo Nexus가 아직 시작되지 않았거나 로그가 생성되지 않았습니다.
)
pause
goto MAIN_MENU

:CHECK_PASSWORD
echo.
echo ===============================================
echo         초기 비밀번호 확인
echo ===============================================
echo.
if exist "%NEXUS_DATA%\admin.password" (
    echo admin 계정의 초기 비밀번호:
    echo ===============================================
    type "%NEXUS_DATA%\admin.password"
    echo.
    echo ===============================================
    echo 이 비밀번호로 웹 UI에서 admin 계정에 로그인하세요.
    echo 첫 로그인 후 새 비밀번호를 설정해야 합니다.
) else (
    echo 초기 비밀번호 파일이 없습니다.
    echo - Nexus가 아직 완전히 시작되지 않았거나
    echo - 이미 초기 설정이 완료되었습니다.
)
pause
goto MAIN_MENU

:OPEN_BROWSER
echo.
echo 웹 브라우저에서 Nexus를 엽니다...
start http://localhost:8081
echo.
echo 브라우저에서 http://localhost:8081이 열렸습니다.
echo admin 계정으로 로그인하세요.
pause
goto MAIN_MENU

:DIAGNOSE
echo.
echo ===============================================
echo         문제 해결 진단
echo ===============================================
echo.
echo [1] 설치 상태 확인:
if exist "%NEXUS_HOME%\bin\nexus.exe" (
    echo ✅ Nexus 설치됨: %NEXUS_HOME%
) else (
    echo ❌ Nexus 설치되지 않음
)

echo.
echo [2] Java 확인:
java -version 2>nul && echo ✅ Java 설치됨 || echo ❌ Java 설치되지 않음

echo.
echo [3] 프로세스 확인:
tasklist | findstr java.exe >nul && echo ✅ Java 프로세스 실행 중 || echo ❌ Java 프로세스 없음

echo.
echo [4] 포트 확인:
netstat -an | findstr :8081 >nul && echo ✅ 포트 8081 사용 중 || echo ❌ 포트 8081 미사용

echo.
echo [5] 방화벽 확인:
echo 방화벽에서 포트 8081이 차단되었을 수 있습니다.

echo.
echo [6] 디스크 공간 확인:
for /f "tokens=3" %%a in ('dir "%NEXUS_HOME%" 2^>nul ^| find "bytes free"') do echo 여유 공간: %%a bytes

pause
goto MAIN_MENU

:UNINSTALL
echo.
echo ===============================================
echo         Nexus 완전 제거
echo ===============================================
echo.
echo 경고: 모든 Nexus 데이터가 삭제됩니다!
set /p CONFIRM="정말로 제거하시겠습니까? (y/n): "
if /i not "%CONFIRM%"=="y" goto MAIN_MENU

echo.
echo [1/4] Nexus 프로세스 중지...
taskkill /F /IM nexus.exe 2>nul
taskkill /F /IM java.exe /FI "WINDOWTITLE eq Nexus" 2>nul

echo [2/4] 설치 디렉토리 제거...
rmdir /s /q "%NEXUS_HOME%" 2>nul

echo [3/4] 데이터 디렉토리 제거...
rmdir /s /q "%NEXUS_DATA%" 2>nul

echo [4/4] 환경변수 정리...
reg delete "HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v NEXUS_HOME /f 2>nul
reg delete "HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v NEXUS_DATA /f 2>nul

echo.
echo Nexus가 완전히 제거되었습니다.
pause
goto MAIN_MENU

:END
echo.
echo Nexus Manager를 종료합니다.
pause
exit /b 0

