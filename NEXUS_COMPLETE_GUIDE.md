# Nexus Repository OSS 완전 가이드

## 🎯 개요
Docker 없이 Windows에 Nexus Repository OSS를 설치하고 Spring Boot Gradle 프로젝트와 연동하는 완전 가이드입니다.

## 🚀 빠른 시작

### 1단계: Nexus 설치 및 시작
```bash
# 관리자 권한 CMD에서 실행
nexus-manager.bat

# 메뉴에서 순서대로:
# 1 → Nexus 설치
# 2 → Nexus 시작
# 7 → 웹 브라우저에서 열기
```

### 2단계: 웹 UI 초기 설정
1. `http://localhost:8081` 접속
2. "Sign in" 클릭
3. Username: `admin`
4. 초기 비밀번호: `nexus-manager.bat`에서 옵션 6으로 확인
5. Setup Wizard 완료 (새 비밀번호 설정)

### 3단계: Repository 설정
```bash
# nexus-manager.bat에서
# 3 → Repository 설정 (admin 비밀번호 입력)
```

### 4단계: Gradle 연동 테스트
```bash
# Gradle 연동 테스트
nexus-gradle-integration.bat

# 6 → 전체 연동 테스트
```

## 📋 주요 도구 및 기능

### 🔧 nexus-manager.bat
**Nexus Repository의 모든 관리 기능을 통합**

#### 주요 기능:
- **설치**: 자동 다운로드, 압축 해제, 설정
- **서비스 관리**: 시작/중지/상태 확인
- **Repository 설정**: Maven repositories 자동 생성
- **연결 테스트**: 웹 UI, REST API, Repository 접근 테스트
- **로그 확인**: 실시간 로그 모니터링
- **문제 해결**: 자동 진단 및 해결 방안 제시
- **완전 제거**: 깨끗한 언인스톨

#### 사용 예시:
```bash
# 관리자 권한으로 실행
nexus-manager.bat

# 주요 메뉴:
# 1. 첫 설치 시
# 2. 일상적인 시작/중지
# 3. Repository 생성
# 8. 문제 발생 시 진단
```

### 🔗 nexus-gradle-integration.bat
**Gradle과 Nexus 연동의 모든 테스트 및 관리**

#### 주요 기능:
- **연결 테스트**: Repository 접근 가능성 확인
- **빌드 테스트**: Nexus 통한 종속성 다운로드 테스트
- **배포 테스트**: 프로젝트를 Nexus에 배포
- **캐시 관리**: Gradle 캐시 정리 및 최적화
- **성능 벤치마크**: 캐싱 효과 측정
- **종합 테스트**: 전체 연동 상태 점검

#### 사용 예시:
```bash
nexus-gradle-integration.bat

# 주요 워크플로우:
# 1 → 연결 테스트 (연동 확인)
# 2 → 빌드 테스트 (실제 사용)
# 6 → 전체 테스트 (종합 점검)
# 9 → 성능 벤치마크 (최적화 확인)
```

## 📊 시스템 아키텍처

```
Spring Boot 프로젝트
        ↓
gradle.properties (nexus 설정)
        ↓
Nexus Repository (localhost:8081)
        ↓ (proxy)
Maven Central + 기타 저장소
```

### Repository 구조:
- **maven-releases**: 릴리스 버전 라이브러리
- **maven-snapshots**: 스냅샷 버전 라이브러리
- **maven-proxy**: 외부 저장소 프록시 (Maven Central 등)

## ⚙️ 설정 파일 설명

### gradle.properties
```properties
# Nexus Repository 설정
nexusUrl=http://localhost:8081/repository
nexusUsername=admin
nexusPassword=your-password

# Repository 이름들
nexusReleaseRepo=maven-releases
nexusSnapshotRepo=maven-snapshots
nexusProxyRepo=maven-proxy
```

### build.gradle (핵심 설정)
```gradle
repositories {
    maven {
        name = "nexus-releases"
        url = "${nexusUrl}/${nexusReleaseRepo}"
        credentials {
            username = project.findProperty('nexusUsername')
            password = project.findProperty('nexusPassword')
        }
    }
    // ... 다른 nexus repositories
    mavenCentral() // 백업용
}

publishing {
    repositories {
        maven {
            name = "nexus"
            url = version.endsWith('SNAPSHOT') ? 
                "${nexusUrl}/${nexusSnapshotRepo}" : 
                "${nexusUrl}/${nexusReleaseRepo}"
            credentials {
                username = project.findProperty('nexusUsername')
                password = project.findProperty('nexusPassword')
            }
        }
    }
}
```

## 🔧 일상적인 사용법

### 개발 워크플로우
```bash
# 1. Nexus 시작 (하루 1회)
nexus-manager.bat → 2 → 1

# 2. 프로젝트 빌드 (Nexus 통해 종속성 다운로드)
gradlew build

# 3. 프로젝트 실행
gradlew bootRun

# 4. 내부 라이브러리 배포 (필요시)
gradlew publish
```

### 주요 Gradle 명령어
```bash
# Nexus 연결 테스트
gradlew testNexusConnection

# 종속성 확인
gradlew checkDependencies

# 캐시 정리
gradlew clearNexusCache

# 빌드 (Nexus 통한 종속성 다운로드)
gradlew build

# Nexus에 배포
gradlew publish
```

## 🚨 문제 해결

### 1. 일반적인 문제들

#### Nexus가 시작되지 않는 경우
```bash
# 진단 실행
nexus-manager.bat → 8

# 확인 사항:
# - Java 17+ 설치 여부
# - 포트 8081 충돌 여부
# - 디스크 공간 충분 여부
# - 방화벽 설정
```

#### Gradle 연동 실패
```bash
# 연결 테스트
nexus-gradle-integration.bat → 1

# 확인 사항:
# - gradle.properties의 nexusPassword 정확성
# - Nexus 서버 실행 상태
# - Repository 존재 여부
```

#### 성능 이슈
```bash
# 캐시 정리
nexus-gradle-integration.bat → 5

# 성능 벤치마크
nexus-gradle-integration.bat → 9
```

### 2. 자주 발생하는 오류

#### HTTP 401 Unauthorized
- **원인**: 잘못된 인증 정보
- **해결**: `gradle.properties`의 nexusPassword 확인

#### HTTP 404 Not Found
- **원인**: Repository가 존재하지 않음
- **해결**: `nexus-manager.bat → 3`으로 Repository 생성

#### Connection Refused
- **원인**: Nexus 서버가 시작되지 않음
- **해결**: `nexus-manager.bat → 2 → 1`으로 Nexus 시작

## 📈 성능 최적화

### 1. JVM 튜닝
`C:\nexus\bin\nexus.vmoptions` 파일 수정:
```
-Xms2g
-Xmx4g
-XX:MaxDirectMemorySize=4g
-XX:+UseG1GC
```

### 2. 네트워크 최적화
- 기가비트 네트워크 사용
- Nexus를 SSD에 설치
- 정기적인 blob store 압축

### 3. 캐시 전략
```bash
# 주기적 캐시 정리 (주 1회)
nexus-gradle-integration.bat → 5

# 성능 모니터링
nexus-gradle-integration.bat → 9
```

## 🔐 보안 강화

### 1. 인증 및 권한
- 기본 admin 비밀번호 즉시 변경
- 개별 사용자 계정 생성
- Role 기반 권한 관리

### 2. 네트워크 보안
```bash
# Windows 방화벽 설정
netsh advfirewall firewall add rule name="Nexus" dir=in action=allow protocol=TCP localport=8081
```

### 3. 데이터 보안
```bash
# 정기 백업 (nexus-manager.bat 사용 시 자동)
C:\nexus-data\ → 백업 저장소
```

## 📊 모니터링

### 1. 웹 UI 모니터링
- **URL**: `http://localhost:8081`
- **Browse**: 저장된 라이브러리 확인
- **Administration → System → Nodes**: 시스템 상태
- **Administration → Support → Metrics**: 성능 메트릭

### 2. 로그 모니터링
```bash
# 실시간 로그 확인
nexus-manager.bat → 5

# Gradle 연동 로그
nexus-gradle-integration.bat → 8
```

### 3. 성능 메트릭
- CPU 사용률: 일반적으로 10-30%
- 메모리 사용률: 2-4GB
- 디스크 I/O: 빌드 시 증가
- 네트워크: 다운로드 시 급증

## 🔄 백업 및 복구

### 자동 백업 (권장)
```bash
# nexus-manager.bat에 내장된 백업 기능 사용
# 데이터 위치: C:\nexus-data\
```

### 수동 백업
```bash
# 1. Nexus 중지
nexus-manager.bat → 2 → 2

# 2. 데이터 복사
xcopy C:\nexus-data C:\backup\nexus-data\ /E /I

# 3. Nexus 재시작
nexus-manager.bat → 2 → 1
```

## 🎯 고급 활용

### 1. 팀 협업
- 중앙 Nexus 서버 구축
- 공통 gradle.properties 배포
- CI/CD 파이프라인 연동

### 2. 멀티 프로젝트 관리
- Group Repository 활용
- 프로젝트별 Repository 분리
- 버전 관리 정책 수립

### 3. 기업 환경 배포
- HTTPS 설정
- LDAP 연동
- 외부 blob store 사용

## 📞 지원 및 문의

### 명령어 요약
```bash
# Nexus 관리
nexus-manager.bat

# Gradle 연동
nexus-gradle-integration.bat

# 웹 UI 접속
http://localhost:8081
```

### 트러블슈팅 순서
1. `nexus-manager.bat → 8` (진단)
2. `nexus-gradle-integration.bat → 1` (연결 테스트)
3. 로그 확인 및 해결 방안 적용
4. 전체 재설치 (최후 수단)

이 가이드를 통해 Nexus Repository OSS와 Spring Boot Gradle 프로젝트의 완전한 연동 환경을 구축하고 유지관리할 수 있습니다.

