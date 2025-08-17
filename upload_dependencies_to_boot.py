#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Nexus Boot Repository 의존성 업로드 스크립트

deps.txt 파일의 의존성 트리를 분석하여 
모든 라이브러리를 Nexus Boot Repository에 업로드합니다.
"""

import os
import re
import requests
import subprocess
import shutil
from pathlib import Path
from typing import List, Tuple, Set
import json

# Nexus 설정
NEXUS_URL = "http://localhost:8081"
NEXUS_USERNAME = "admin"
NEXUS_PASSWORD = "admin123"
BOOT_REPO = "boot"

# Gradle 캐시 디렉토리
GRADLE_CACHE = Path.home() / ".gradle" / "caches" / "modules-2" / "files-2.1"

class DependencyUploader:
    def __init__(self):
        self.uploaded_artifacts = set()
        self.failed_uploads = []
        
    def parse_deps_file(self, deps_file: str = "deps.txt") -> Set[Tuple[str, str, str]]:
        """deps.txt 파일에서 의존성 정보를 추출합니다."""
        dependencies = set()
        excluded_boms = set()
        
        if not os.path.exists(deps_file):
            print(f"❌ {deps_file} 파일을 찾을 수 없습니다.")
            return dependencies
            
        with open(deps_file, 'r', encoding='utf-8') as f:
            content = f.read()
            
        # 의존성 패턴 매칭: group:artifact:version
        pattern = r'([a-zA-Z0-9\-_.]+):([a-zA-Z0-9\-_.]+):([a-zA-Z0-9\-_.]+)'
        matches = re.findall(pattern, content)
        
        for match in matches:
            group_id, artifact_id, version = match
            # 버전에서 " -> " 이후 부분 제거 (conflict resolution)
            if " -> " in version:
                version = version.split(" -> ")[1]
                
            # BOM(Bill of Materials) 타입 의존성 제외
            if self.is_bom_dependency(group_id, artifact_id):
                excluded_boms.add((group_id, artifact_id, version))
                continue
                
            dependencies.add((group_id, artifact_id, version))
            
        print(f"✓ {len(dependencies)}개의 고유한 의존성을 발견했습니다.")
        if excluded_boms:
            print(f"ℹ️  {len(excluded_boms)}개의 BOM 의존성을 제외했습니다:")
            for group_id, artifact_id, version in sorted(excluded_boms):
                print(f"   • {group_id}:{artifact_id}:{version}")
        return dependencies
    
    def is_bom_dependency(self, group_id: str, artifact_id: str) -> bool:
        """BOM(Bill of Materials) 타입 의존성인지 확인합니다."""
        # BOM 패턴 확인
        bom_patterns = [
            '-bom',
            'bom-',
            '-bill-of-materials'
        ]
        
        # artifact_id에 BOM 패턴이 포함되어 있는지 확인
        artifact_lower = artifact_id.lower()
        for pattern in bom_patterns:
            if pattern in artifact_lower:
                return True
                
        # 알려진 BOM 의존성 목록
        known_boms = {
            ('com.fasterxml.jackson', 'jackson-bom'),
            ('org.junit', 'junit-bom'),
            ('org.springframework', 'spring-framework-bom'),
            ('org.springframework.boot', 'spring-boot-dependencies'),
            ('org.springframework.cloud', 'spring-cloud-dependencies'),
            ('io.netty', 'netty-bom'),
            ('com.azure', 'azure-sdk-bom'),
            ('software.amazon.awssdk', 'bom'),
        }
        
        return (group_id, artifact_id) in known_boms
    
    def find_jar_file(self, group_id: str, artifact_id: str, version: str) -> Path:
        """Gradle 캐시에서 JAR 파일을 찾습니다."""
        cache_path = GRADLE_CACHE / group_id / artifact_id / version
        
        if not cache_path.exists():
            return None
            
        # JAR 파일 찾기
        for jar_file in cache_path.rglob("*.jar"):
            # pom.jar나 sources.jar 제외
            if not any(x in jar_file.name for x in ['-sources.jar', '-javadoc.jar', '.pom']):
                return jar_file
                
        return None
    
    def download_missing_dependency(self, group_id: str, artifact_id: str, version: str) -> Path:
        """Maven Central에서 의존성을 다운로드합니다."""
        temp_dir = Path("temp-dependencies")
        temp_dir.mkdir(exist_ok=True)
        
        jar_filename = f"{artifact_id}-{version}.jar"
        local_jar_path = temp_dir / jar_filename
        
        if local_jar_path.exists():
            return local_jar_path
            
        # Maven Central URL
        maven_url = f"https://repo1.maven.org/maven2/{group_id.replace('.', '/')}/{artifact_id}/{version}/{jar_filename}"
        
        try:
            print(f"   📥 Maven Central에서 다운로드: {jar_filename}")
            response = requests.get(maven_url, stream=True)
            response.raise_for_status()
            
            with open(local_jar_path, 'wb') as f:
                shutil.copyfileobj(response.raw, f)
                
            return local_jar_path
            
        except requests.RequestException as e:
            print(f"   ❌ 다운로드 실패: {e}")
            return None
    
    def upload_to_nexus(self, jar_path: Path, group_id: str, artifact_id: str, version: str) -> bool:
        """Nexus Repository에 JAR 파일을 업로드합니다."""
        if not jar_path.exists():
            return False
            
        # Nexus Component Upload API 사용 (hosted repository용)
        upload_url = f"{NEXUS_URL}/service/rest/v1/components"
        
        try:
            # multipart/form-data로 업로드
            files = {
                'maven2.asset1': ('file', open(jar_path, 'rb'), 'application/java-archive'),
                'maven2.asset1.extension': (None, 'jar'),
                'maven2.groupId': (None, group_id),
                'maven2.artifactId': (None, artifact_id),
                'maven2.version': (None, version)
            }
            
            params = {
                'repository': BOOT_REPO
            }
            
            response = requests.post(
                upload_url,
                auth=(NEXUS_USERNAME, NEXUS_PASSWORD),
                files=files,
                params=params
            )
            
            # 파일 객체 닫기
            files['maven2.asset1'][1].close()
                
            if response.status_code in [200, 201, 204]:
                print(f"   ✅ 업로드 성공: {group_id}:{artifact_id}:{version}")
                return True
            else:
                print(f"   ❌ 업로드 실패 (HTTP {response.status_code}): {response.text[:200]}...")
                return False
                
        except requests.RequestException as e:
            print(f"   ❌ 업로드 실패: {e}")
            return False
        except Exception as e:
            print(f"   ❌ 파일 처리 오류: {e}")
            return False
    
    def test_nexus_connection(self) -> bool:
        """Nexus Boot Repository 연결을 테스트합니다."""
        test_url = f"{NEXUS_URL}/repository/{BOOT_REPO}/"
        
        try:
            response = requests.get(test_url, auth=(NEXUS_USERNAME, NEXUS_PASSWORD))
            if response.status_code == 200:
                print("✅ Nexus Boot Repository 연결 성공")
                return True
            else:
                print(f"❌ Nexus 연결 실패: HTTP {response.status_code}")
                return False
        except requests.RequestException as e:
            print(f"❌ Nexus 연결 실패: {e}")
            return False
    
    def process_dependency(self, group_id: str, artifact_id: str, version: str) -> bool:
        """개별 의존성을 처리합니다."""
        artifact_key = f"{group_id}:{artifact_id}:{version}"
        
        if artifact_key in self.uploaded_artifacts:
            return True
            
        print(f"\n📦 처리중: {artifact_key}")
        
        # 1. Gradle 캐시에서 JAR 파일 찾기
        jar_path = self.find_jar_file(group_id, artifact_id, version)
        
        # 2. 캐시에 없으면 Maven Central에서 다운로드
        if not jar_path:
            print(f"   ℹ️  Gradle 캐시에서 찾을 수 없음, 다운로드 시도...")
            jar_path = self.download_missing_dependency(group_id, artifact_id, version)
            
        if not jar_path:
            print(f"   ⚠️  JAR 파일을 찾을 수 없음: {artifact_key}")
            self.failed_uploads.append(artifact_key)
            return False
            
        # 3. Nexus에 업로드
        success = self.upload_to_nexus(jar_path, group_id, artifact_id, version)
        
        if success:
            self.uploaded_artifacts.add(artifact_key)
            
        return success
    
    def run_gradle_dependencies(self):
        """Gradle dependencies 명령을 실행하여 캐시를 채웁니다."""
        print("🔄 Gradle dependencies 실행 중...")
        try:
            result = subprocess.run(
                ["./gradlew.bat" if os.name == 'nt' else "./gradlew", "dependencies"],
                capture_output=True,
                text=True,
                cwd="."
            )
            print("✅ Gradle dependencies 완료")
        except Exception as e:
            print(f"⚠️  Gradle dependencies 실행 중 오류: {e}")
    
    def run_upload(self):
        """전체 업로드 프로세스를 실행합니다."""
        print("=" * 60)
        print("   Nexus Boot Repository 의존성 업로드")
        print("=" * 60)
        
        # 1. Nexus 연결 테스트
        if not self.test_nexus_connection():
            return False
            
        # 2. Gradle dependencies 실행
        self.run_gradle_dependencies()
        
        # 3. deps.txt 파일 분석
        dependencies = self.parse_deps_file()
        
        if not dependencies:
            print("❌ 업로드할 의존성이 없습니다.")
            return False
            
        # 4. 각 의존성 업로드
        total = len(dependencies)
        success_count = 0
        
        print(f"\n🚀 {total}개 의존성 업로드 시작...\n")
        
        for i, (group_id, artifact_id, version) in enumerate(sorted(dependencies), 1):
            print(f"[{i}/{total}]", end=" ")
            if self.process_dependency(group_id, artifact_id, version):
                success_count += 1
                
        # 5. 결과 요약
        print("\n" + "=" * 60)
        print("   업로드 완료 요약")
        print("=" * 60)
        print(f"✅ 성공: {success_count}/{total}")
        print(f"❌ 실패: {len(self.failed_uploads)}")
        
        if self.failed_uploads:
            print("\n실패한 의존성:")
            for failed in self.failed_uploads:
                print(f"  • {failed}")
                
        print(f"\n🌐 Boot Repository 확인: {NEXUS_URL}/#browse/browse:{BOOT_REPO}")
        
        return success_count > 0

def main():
    uploader = DependencyUploader()
    success = uploader.run_upload()
    
    if success:
        print("\n✅ 의존성 업로드가 완료되었습니다!")
    else:
        print("\n❌ 의존성 업로드에 실패했습니다.")
    
    input("\n엔터 키를 누르면 종료됩니다...")

if __name__ == "__main__":
    main()
