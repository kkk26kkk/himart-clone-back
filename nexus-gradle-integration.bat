@echo off
echo ===============================================
echo    Nexus-Gradle 연동 및 테스트 도구
echo ===============================================

:MAIN_MENU
echo.
echo ===============================================
echo         Nexus-Gradle 연동 관리
echo ===============================================
echo 1. 연결 테스트 (testNexusConnection)
echo 2. 프로젝트 빌드 테스트 (Nexus 통한 종속성 다운로드)
echo 3. 프로젝트 배포 테스트 (publish to Nexus)
echo 4. 종속성 확인 (checkDependencies)
echo 5. Gradle 캐시 정리 (clearNexusCache)
echo 6. 전체 연동 테스트 (종합 테스트)
echo 7. Gradle 설정 확인
echo 8. Repository 접근 로그 확인
echo 9. 성능 벤치마크
echo 0. 종료
echo ===============================================
set /p CHOICE="선택하세요 (0-9): "

if "%CHOICE%"=="1" goto TEST_CONNECTION
if "%CHOICE%"=="2" goto BUILD_TEST
if "%CHOICE%"=="3" goto PUBLISH_TEST
if "%CHOICE%"=="4" goto CHECK_DEPS
if "%CHOICE%"=="5" goto CLEAR_CACHE
if "%CHOICE%"=="6" goto FULL_TEST
if "%CHOICE%"=="7" goto CHECK_CONFIG
if "%CHOICE%"=="8" goto ACCESS_LOGS
if "%CHOICE%"=="9" goto BENCHMARK
if "%CHOICE%"=="0" goto END

echo 잘못된 선택입니다.
goto MAIN_MENU

:TEST_CONNECTION
echo.
echo ===============================================
echo         Nexus Repository 연결 테스트
echo ===============================================
echo.
call gradlew.bat testNexusConnection
pause
goto MAIN_MENU

:BUILD_TEST
echo.
echo ===============================================
echo         빌드 테스트 (Nexus 통한 종속성 다운로드)
echo ===============================================
echo.
echo [1/3] 프로젝트 정리...
call gradlew.bat clean

echo.
echo [2/3] 컴파일 (종속성 다운로드)...
call gradlew.bat compileJava

echo.
echo [3/3] 전체 빌드...
set /p FULL_BUILD="전체 빌드를 진행하시겠습니까? (y/n): "
if /i "%FULL_BUILD%"=="y" (
    call gradlew.bat build
)

pause
goto MAIN_MENU

:PUBLISH_TEST
echo.
echo ===============================================
echo         Nexus 배포 테스트
echo ===============================================
echo.
echo 현재 프로젝트를 Nexus Repository에 배포합니다.
echo gradle.properties의 nexusUsername과 nexusPassword가 올바른지 확인하세요.
echo.
set /p CONFIRM="배포를 진행하시겠습니까? (y/n): "
if /i not "%CONFIRM%"=="y" goto MAIN_MENU

echo.
echo 배포 중...
call gradlew.bat publish

if %errorlevel% equ 0 (
    echo.
    echo ✅ 배포 성공!
    echo Nexus 웹 UI에서 Browse > maven-snapshots 또는 maven-releases를 확인하세요.
) else (
    echo.
    echo ❌ 배포 실패
    echo gradle.properties의 인증 정보를 확인하세요.
)

pause
goto MAIN_MENU

:CHECK_DEPS
echo.
echo ===============================================
echo         종속성 확인
echo ===============================================
echo.
call gradlew.bat checkDependencies
pause
goto MAIN_MENU

:CLEAR_CACHE
echo.
echo ===============================================
echo         Gradle 캐시 정리
echo ===============================================
echo.
echo [1/2] Nexus 캐시 정리...
call gradlew.bat clearNexusCache

echo.
echo [2/2] Gradle 데몬 재시작...
call gradlew.bat --stop
echo Gradle 캐시가 정리되었습니다.

pause
goto MAIN_MENU

:FULL_TEST
echo.
echo ===============================================
echo         전체 연동 테스트
echo ===============================================
echo.
echo 종합적인 Nexus-Gradle 연동 테스트를 진행합니다.
echo 예상 소요 시간: 3-5분
echo.
set /p CONFIRM="전체 테스트를 진행하시겠습니까? (y/n): "
if /i not "%CONFIRM%"=="y" goto MAIN_MENU

echo.
echo [1/7] Nexus 서버 상태 확인...
powershell -Command "try { $response = Invoke-WebRequest -Uri 'http://localhost:8081/service/rest/v1/status' -UseBasicParsing; Write-Host '✅ Nexus 서버 정상: HTTP' $response.StatusCode } catch { Write-Host '❌ Nexus 서버 접근 불가'; exit 1 }"
if %errorlevel% neq 0 (
    echo 테스트를 중단합니다. Nexus 서버를 먼저 시작하세요.
    pause
    goto MAIN_MENU
)

echo.
echo [2/7] Repository 연결 테스트...
call gradlew.bat testNexusConnection

echo.
echo [3/7] 캐시 정리...
call gradlew.bat clean

echo.
echo [4/7] 종속성 다운로드 테스트...
call gradlew.bat compileJava

echo.
echo [5/7] 종속성 확인...
call gradlew.bat checkDependencies

echo.
echo [6/7] 빌드 테스트...
call gradlew.bat build --quiet

echo.
echo [7/7] 배포 테스트...
set /p DEPLOY_TEST="배포 테스트도 진행하시겠습니까? (y/n): "
if /i "%DEPLOY_TEST%"=="y" (
    call gradlew.bat publish
)

echo.
echo ===============================================
echo         전체 테스트 완료!
echo ===============================================
echo 모든 연동 테스트가 완료되었습니다.
echo Nexus 웹 UI에서 Browse 메뉴를 통해 다운로드된 라이브러리를 확인하세요.

pause
goto MAIN_MENU

:CHECK_CONFIG
echo.
echo ===============================================
echo         Gradle 설정 확인
echo ===============================================
echo.
echo [1] gradle.properties 내용:
echo ===============================================
if exist gradle.properties (
    type gradle.properties
) else (
    echo gradle.properties 파일이 없습니다.
)

echo.
echo ===============================================
echo [2] 현재 Repository 설정:
echo ===============================================
call gradlew.bat -q dependencies --configuration compileClasspath | findstr "repository\|nexus\|maven" | head -10

echo.
echo [3] Gradle 버전:
echo ===============================================
call gradlew.bat --version | head -5

pause
goto MAIN_MENU

:ACCESS_LOGS
echo.
echo ===============================================
echo         Repository 접근 로그 확인
echo ===============================================
echo.
echo [1] 최근 Gradle 빌드 로그에서 Repository 접근 기록:
echo ===============================================
if exist .gradle\daemon (
    for /f %%f in ('dir /b /od .gradle\daemon\*\daemon-*.out.log 2^>nul') do (
        echo 최근 데몬 로그에서 Repository 접근:
        findstr /i "repository\|nexus\|downloaded\|maven" ".gradle\daemon\%%f" | tail -10 2>nul
    )
) else (
    echo Gradle 데몬 로그를 찾을 수 없습니다.
)

echo.
echo [2] 마지막 빌드에서 다운로드된 라이브러리:
echo ===============================================
call gradlew.bat checkDependencies | findstr "Downloaded:" | tail -10

pause
goto MAIN_MENU

:BENCHMARK
echo.
echo ===============================================
echo         성능 벤치마크
echo ===============================================
echo.
echo Nexus 사용 vs Maven Central 직접 접근 성능 비교를 진행합니다.
set /p CONFIRM="벤치마크를 진행하시겠습니까? (y/n): "
if /i not "%CONFIRM%"=="y" goto MAIN_MENU

echo.
echo [1/4] 캐시 정리...
call gradlew.bat clean
call gradlew.bat --stop

echo.
echo [2/4] Nexus 경유 빌드 시간 측정...
set START_TIME=%time%
call gradlew.bat compileJava --quiet
set END_TIME=%time%
echo Nexus 빌드 완료 시간: %START_TIME% ~ %END_TIME%

echo.
echo [3/4] 캐시 재정리...
call gradlew.bat clean

echo.
echo [4/4] 재빌드 시간 측정 (캐시 효과 확인)...
set START_TIME2=%time%
call gradlew.bat compileJava --quiet
set END_TIME2=%time%
echo 캐시된 빌드 완료 시간: %START_TIME2% ~ %END_TIME2%

echo.
echo ===============================================
echo         벤치마크 결과
echo ===============================================
echo 첫 빌드: %START_TIME% ~ %END_TIME%
echo 재빌드: %START_TIME2% ~ %END_TIME2%
echo.
echo 재빌드 시간이 현저히 빨라졌다면 Nexus 캐싱이 정상 작동하고 있습니다.

pause
goto MAIN_MENU

:END
echo.
echo Nexus-Gradle 연동 도구를 종료합니다.
pause
exit /b 0

