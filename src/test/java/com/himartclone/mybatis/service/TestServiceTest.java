package com.himartclone.mybatis.service;

import com.himartclone.mybatis.domain.TestVO;
import com.himartclone.mybatis.repository.TestRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * MyBatis 및 트랜잭션 테스트 클래스
 * @Transactional 어노테이션의 동작을 검증
 */
@Slf4j
@SpringBootTest
@Transactional
@Rollback(false)  // 테스트 종료 후 자동 롤백 비활성화
@ActiveProfiles({"local", "frontMobile"})
class TestServiceTest {
    
    @Autowired
    private TestService testService;

    @Test
    void transactionTest() {
        testService.testTransaction();
    }

    @Test
    void selfInvocationTest() {
        // Given - 테스트 전 데이터 개수 확인
        int beforeCount = testService.findAll().size();
        log.info("테스트 전 데이터 개수: {}", beforeCount);
        
        // When & Then - 예외 발생 확인
        assertThatThrownBy(() -> {
            testService.testSelfInvocation();
        }).isInstanceOf(RuntimeException.class)
          .hasMessageContaining("Self-invocation 테스트용 예외");
        
        // Then - 데이터 확인 (핵심!)
        int afterCount = testService.findAll().size();
        log.info("테스트 후 데이터 개수: {}", afterCount);
        
        if (afterCount > beforeCount) {
            log.error("🚨 문제 발생: selfInvocationInsert()가 커밋되었습니다!");
            log.error("원인: Auto-commit이 활성화되었거나 트랜잭션이 제대로 작동하지 않음");
        } else {
            log.info("✅ 정상: 전체 롤백되어 데이터 변화 없음");
        }
    }
    
    @Test
    @DisplayName("테스트 데이터 생성 테스트")
    void createTest() {
        // Given
        TestVO testVO = TestVO.builder()
                .testName("JUnit 테스트")
                .testDescription("JUnit으로 생성한 테스트 데이터")
                .build();
        
        // When
        TestVO savedTest = testService.createTest(testVO);
        
        // Then
        assertThat(savedTest.getId()).isNotNull();
        assertThat(savedTest.getTestName()).isEqualTo("JUnit 테스트");
        assertThat(savedTest.getTestDescription()).isEqualTo("JUnit으로 생성한 테스트 데이터");
        assertThat(savedTest.getCreatedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("테스트 데이터 수정 테스트")
    void updateTest() {
        // Given - 먼저 데이터 생성
        TestVO originalTest = TestVO.builder()
                .testName("원본 테스트")
                .testDescription("원본 설명")
                .build();
        TestVO savedTest = testService.createTest(originalTest);
        
        // When - 데이터 수정
        savedTest.setTestName("수정된 테스트");
        savedTest.setTestDescription("수정된 설명");
        TestVO updatedTest = testService.updateTest(savedTest);
        
        // Then
        assertThat(updatedTest.getTestName()).isEqualTo("수정된 테스트");
        assertThat(updatedTest.getTestDescription()).isEqualTo("수정된 설명");
        
        // 데이터베이스에서 다시 조회하여 확인
        Optional<TestVO> foundTest = testService.findById(savedTest.getId());
        assertThat(foundTest).isPresent();
        assertThat(foundTest.get().getTestName()).isEqualTo("수정된 테스트");
    }
    
    @Test
    @DisplayName("트랜잭션 성공 케이스 테스트")
    @Rollback(false) // 실제 커밋하여 결과 확인
    void transactionSuccessTest() {
        // Given
        TestVO test1 = TestVO.builder()
                .testName("트랜잭션 테스트1")
                .testDescription("첫 번째 트랜잭션 테스트")
                .build();
        
        TestVO test2 = TestVO.builder()
                .testName("트랜잭션 테스트2")
                .testDescription("두 번째 트랜잭션 테스트")
                .build();
        
        // When
        testService.createMultipleTestsSuccess(test1, test2);
        
        // Then - 두 데이터 모두 정상 생성되었는지 확인
        List<TestVO> allTests = testService.findAll();
        long count = allTests.stream()
                .filter(test -> test.getTestName().startsWith("트랜잭션 테스트"))
                .count();
        
        assertThat(count).isGreaterThanOrEqualTo(2);
    }
    
    @Test
    @DisplayName("트랜잭션 롤백 테스트")
    void transactionRollbackTest() {
        // Given
        TestVO test1 = TestVO.builder()
                .testName("롤백 테스트1")
                .testDescription("첫 번째 롤백 테스트")
                .build();
        
        TestVO test2 = TestVO.builder()
                .testName("롤백 테스트2")
                .testDescription("두 번째 롤백 테스트 - 실패 예정")
                .build();
        
        // When & Then - 예외 발생으로 인한 롤백 확인
        assertThatThrownBy(() -> 
            testService.createMultipleTestsWithRollback(test1, test2)
        ).isInstanceOf(RuntimeException.class);
        
        // 롤백으로 인해 첫 번째 데이터도 저장되지 않았는지 확인
        List<TestVO> rollbackTests = testService.findByTestName("롤백 테스트");
        assertThat(rollbackTests).isEmpty();
    }
    
    @Test
    @DisplayName("데이터 조회 테스트")
    void findTest() {
        // Given
        TestVO testVO = TestVO.builder()
                .testName("조회 테스트")
                .testDescription("조회용 테스트 데이터")
                .build();
        TestVO savedTest = testService.createTest(testVO);
        
        // When & Then - ID로 조회
        Optional<TestVO> foundById = testService.findById(savedTest.getId());
        assertThat(foundById).isPresent();
        assertThat(foundById.get().getTestName()).isEqualTo("조회 테스트");
        
        // When & Then - 이름으로 조회
        List<TestVO> foundByName = testService.findByTestName("조회");
        assertThat(foundByName).isNotEmpty();
        assertThat(foundByName.get(0).getTestName()).contains("조회");
        
        // When & Then - 전체 조회
        List<TestVO> allTests = testService.findAll();
        assertThat(allTests).isNotEmpty();
    }
    
    @Test
    @DisplayName("데이터 유효성 검증 테스트")
    void validateTest() {
        // Given - 잘못된 데이터
        TestVO invalidTest = TestVO.builder()
                .testName(null) // 필수 필드 누락
                .testDescription("유효성 검증 테스트")
                .build();
        
        // When & Then - 예외 발생 확인
        assertThatThrownBy(() -> 
            testService.createTest(invalidTest)
        ).isInstanceOf(IllegalArgumentException.class)
         .hasMessageContaining("테스트 이름은 필수입니다");
    }
    
    @Test
    @DisplayName("데이터 삭제 테스트")
    void deleteTest() {
        // Given
        TestVO testVO = TestVO.builder()
                .testName("삭제 테스트")
                .testDescription("삭제용 테스트 데이터")
                .build();
        TestVO savedTest = testService.createTest(testVO);
        
        // When
        testService.deleteById(savedTest.getId());
        
        // Then
        Optional<TestVO> deletedTest = testService.findById(savedTest.getId());
        assertThat(deletedTest).isEmpty();
    }
    
    @Test
    @DisplayName("testTransaction 메서드 실행 테스트 - 예외 발생 및 롤백 확인")
    @Rollback(false) // 테스트 결과를 DB에서 직접 확인하기 위해 롤백 비활성화
    void testTransactionMethod() {
        // Given - 테스트 전 데이터 개수 확인
        List<TestVO> beforeTests = testService.findAll();
        int beforeCount = beforeTests.size();
        log.info("테스트 시작 전 데이터 개수: {}", beforeCount);
        
        // When & Then - testTransaction 실행 시 예외 발생 확인
        assertThatThrownBy(() -> {
            testService.testTransaction();
        }).isInstanceOf(Exception.class); // MyBatisSystemException 또는 ReflectionException
        
        // Then - 예외 발생 후 데이터 개수 확인 (롤백되었는지 검증)
        List<TestVO> afterTests = testService.findAll();
        int afterCount = afterTests.size();
        log.info("예외 발생 후 데이터 개수: {}", afterCount);
        
        // 트랜잭션 롤백으로 인해 데이터 개수가 동일해야 함
        assertThat(afterCount).isEqualTo(beforeCount);
        log.info("트랜잭션 롤백 확인 완료 - 데이터 변화 없음");
    }
    
    @Test
    @DisplayName("Self-invocation 테스트 - 프록시 우회 확인")
    @Rollback(false)
    void testSelfInvocationMethod() {
        // Given
        List<TestVO> beforeTests = testService.findAll();
        int beforeCount = beforeTests.size();
        log.info("Self-invocation 테스트 시작 전 데이터 개수: {}", beforeCount);
        
        // When & Then
        assertThatThrownBy(() -> {
            testService.testSelfInvocation();
        }).isInstanceOf(RuntimeException.class)
          .hasMessageContaining("Self-invocation 테스트용 예외");
        
        // Then - 데이터 확인
        List<TestVO> afterTests = testService.findAll();
        int afterCount = afterTests.size();
        log.info("Self-invocation 테스트 후 데이터 개수: {}", afterCount);
        
        // Self-invocation이 제대로 작동했다면:
        // - private 메서드의 @Transactional이 무시되어 
        // - 모든 작업이 하나의 트랜잭션에서 실행되고
        // - 예외 발생으로 전체 롤백되어야 함
        if (afterCount == beforeCount) {
            log.info("✅ Self-invocation 확인: @Transactional이 무시되어 전체 롤백됨");
        } else {
            log.info("❌ 프록시가 작동: Insert는 별도 트랜잭션으로 커밋됨");
        }
    }
}
