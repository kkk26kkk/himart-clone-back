package com.himartclone.mybatis.service;

import com.himartclone.mybatis.domain.TestVO;
import com.himartclone.mybatis.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * MyBatis 테스트용 Service 클래스
 * 트랜잭션 관리와 비즈니스 로직 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestService {
    
    private final TestRepository testRepository;
    private final DataSource dataSource;

    @Transactional(noRollbackFor = {org.mybatis.spring.MyBatisSystemException.class, 
                                     org.apache.ibatis.reflection.ReflectionException.class})
    public void testTransaction() {
        log.info("트랜잭션 테스트 시작");
        log.info("[1] isActive={}, transactionName={}",
                TransactionSynchronizationManager.isActualTransactionActive(),
                TransactionSynchronizationManager.getCurrentTransactionName());
        
        this.innerInsert();    // 1단계: INSERT 실행 (새로운 트랜잭션)
        
        try {
            this.innerUpdate();    // 2단계: UPDATE 실행 - 예외 발생 예상
        } catch (Exception e) {
            log.error("innerUpdate 실행 중 예외 발생: {}", e.getMessage());
            // 예외를 잡아서 부모 트랜잭션이 rollback-only로 마킹되는 것을 방지
            // 하지만 이미 innerInsert는 별도 트랜잭션으로 커밋됨
        }
        
        log.info("트랜잭션 테스트 완료");
    }
    
    /**
     * 진짜 Self-invocation 테스트 (프록시 우회)
     * ApplicationContextAware를 사용하지 않고 this.method() 호출
     */
    @Transactional
    public void testSelfInvocation() {
        log.info("Self-invocation 테스트 시작");
        
        // 트랜잭션 및 커넥션 상태 확인
        checkTransactionStatus("testSelfInvocation 시작");
        
        // 직접 this 호출 - 프록시 우회 가능성 테스트
        selfInvocationInsert();
        selfInvocationUpdate();
        
        log.info("Self-invocation 테스트 완료");
    }

    private void selfInvocationInsert() {
        log.info("Self-invocation Insert - 새로운 트랜잭션이어야 함");
        checkTransactionStatus("selfInvocationInsert");
        
        testRepository.insertTest(TestVO.builder()
            .testName("Self-invocation 테스트")
            .testDescription("Self-invocation Insert 테스트")
            .build());
    }

    private void selfInvocationUpdate() {
        log.info("Self-invocation Update - 새로운 트랜잭션이어야 함");
        checkTransactionStatus("selfInvocationUpdate");
        
        // 의도적으로 예외 발생
        throw new RuntimeException("Self-invocation 테스트용 예외");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void innerInsert() {
        log.info("[2] isActive={}, transactionName={}",
                TransactionSynchronizationManager.isActualTransactionActive(),
                TransactionSynchronizationManager.getCurrentTransactionName());
        testRepository.insertTest(TestVO.builder().testName("트랜잭션 테스트").testDescription("트랜잭션 테스트 설명").build());
    }

    private void innerUpdate() {
        log.info("[3] isActive={}, transactionName={}",
                TransactionSynchronizationManager.isActualTransactionActive(),
                TransactionSynchronizationManager.getCurrentTransactionName());
        testRepository.updateTest(TestVO.builder().id(1L).testName("트랜잭션 테스트").testDescription("트랜잭션 테스트 설명").build());
    }
    
    /**
     * 트랜잭션 상태 및 커넥션 상태 확인 메서드
     */
    private void checkTransactionStatus(String methodName) {
        boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        String transactionName = TransactionSynchronizationManager.getCurrentTransactionName();
        
        log.info("=== {} 트랜잭션 상태 ===", methodName);
        log.info("트랜잭션 활성화: {}", isTransactionActive);
        log.info("트랜잭션 이름: {}", transactionName);
        
        try (Connection connection = dataSource.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            log.info("Connection AutoCommit: {}", autoCommit);
            log.info("Connection 클래스: {}", connection.getClass().getSimpleName());
        } catch (SQLException e) {
            log.error("Connection 정보 조회 실패", e);
        }
        
        log.info("================================");
    }
    
    /**
     * 새로운 테스트 데이터 생성
     * @param testVO 생성할 테스트 데이터
     * @return 생성된 테스트 데이터
     */
    @Transactional
    public TestVO createTest(TestVO testVO) {
        log.info("테스트 데이터 생성 시작: {}", testVO.getTestName());
        
        // 데이터 유효성 검증
        validateTestData(testVO);
        
        int result = testRepository.insertTest(testVO);
        if (result <= 0) {
            throw new RuntimeException("테스트 데이터 삽입에 실패했습니다.");
        }
        
        log.info("테스트 데이터 생성 완료: ID={}", testVO.getId());
        return testVO;
    }
    
    /**
     * 테스트 데이터 수정
     * @param testVO 수정할 테스트 데이터
     * @return 수정된 테스트 데이터
     */
    @Transactional
    public TestVO updateTest(TestVO testVO) {
        log.info("테스트 데이터 수정 시작: ID={}", testVO.getId());
        
        // 기존 데이터 존재 여부 확인
        Optional<TestVO> existingTest = testRepository.findById(testVO.getId());
        if (existingTest.isEmpty()) {
            throw new RuntimeException("수정할 테스트 데이터를 찾을 수 없습니다. ID: " + testVO.getId());
        }
        
        // 데이터 유효성 검증
        validateTestData(testVO);
        
        int result = testRepository.updateTest(testVO);
        if (result <= 0) {
            throw new RuntimeException("테스트 데이터 수정에 실패했습니다.");
        }
        
        log.info("테스트 데이터 수정 완료: ID={}", testVO.getId());
        return testVO;
    }
    
    /**
     * 트랜잭션 테스트용 메서드 - 성공 케이스
     * 두 개의 테스트 데이터를 연속으로 생성
     */
    @Transactional
    public void createMultipleTestsSuccess(TestVO test1, TestVO test2) {
        log.info("트랜잭션 테스트 시작 - 성공 케이스");
        
        createTest(test1);
        createTest(test2);
        
        log.info("트랜잭션 테스트 완료 - 성공 케이스");
    }
    
    /**
     * 트랜잭션 테스트용 메서드 - 실패 케이스 (롤백 테스트)
     * 첫 번째 데이터는 성공, 두 번째에서 예외 발생하여 롤백
     */
    @Transactional
    public void createMultipleTestsWithRollback(TestVO test1, TestVO test2) {
        log.info("트랜잭션 테스트 시작 - 롤백 케이스");
        
        // 첫 번째 테스트 데이터 생성 (성공)
        createTest(test1);
        
        // 두 번째 테스트 데이터에 null 이름을 설정하여 예외 발생
        test2.setTestName(null);
        createTest(test2); // 이 부분에서 예외 발생 -> 전체 롤백
    }
    
    /**
     * ID로 테스트 데이터 조회
     * @param id 조회할 ID
     * @return 조회된 테스트 데이터
     */
    public Optional<TestVO> findById(Long id) {
        log.debug("테스트 데이터 조회: ID={}", id);
        return testRepository.findById(id);
    }
    
    /**
     * 모든 테스트 데이터 조회
     * @return 모든 테스트 데이터 리스트
     */
    public List<TestVO> findAll() {
        log.debug("모든 테스트 데이터 조회");
        return testRepository.findAll();
    }
    
    /**
     * 이름으로 테스트 데이터 조회
     * @param testName 조회할 이름
     * @return 해당 이름의 테스트 데이터 리스트
     */
    public List<TestVO> findByTestName(String testName) {
        log.debug("이름으로 테스트 데이터 조회: {}", testName);
        return testRepository.findByTestName(testName);
    }
    
    /**
     * 테스트 데이터 삭제
     * @param id 삭제할 ID
     */
    @Transactional
    public void deleteById(Long id) {
        log.info("테스트 데이터 삭제: ID={}", id);
        
        int result = testRepository.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("삭제할 테스트 데이터를 찾을 수 없습니다. ID: " + id);
        }
        
        log.info("테스트 데이터 삭제 완료: ID={}", id);
    }
    
    /**
     * 테스트 데이터 유효성 검증
     * @param testVO 검증할 테스트 데이터
     */
    private void validateTestData(TestVO testVO) {
        if (testVO.getTestName() == null || testVO.getTestName().trim().isEmpty()) {
            throw new IllegalArgumentException("테스트 이름은 필수입니다.");
        }
        
        if (testVO.getTestName().length() > 100) {
            throw new IllegalArgumentException("테스트 이름은 100자를 초과할 수 없습니다.");
        }
        
        if (testVO.getTestDescription() != null && testVO.getTestDescription().length() > 500) {
            throw new IllegalArgumentException("테스트 설명은 500자를 초과할 수 없습니다.");
        }
    }
}