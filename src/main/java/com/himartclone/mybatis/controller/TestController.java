package com.himartclone.mybatis.controller;

import com.himartclone.mybatis.domain.TestVO;
import com.himartclone.mybatis.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis 테스트용 REST API 컨트롤러
 * 웹에서 MyBatis 기능을 테스트할 수 있는 엔드포인트 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/mybatis/test")
@RequiredArgsConstructor
@Tag(name = "MyBatis Test API", description = "MyBatis 테스트용 API")
public class TestController {
    
    private final TestService testService;
    
    @PostMapping
    @Operation(summary = "테스트 데이터 생성", description = "새로운 테스트 데이터를 생성합니다.")
    public ResponseEntity<TestVO> createTest(@RequestBody TestVO testVO) {
        log.info("테스트 데이터 생성 요청: {}", testVO.getTestName());
        TestVO createdTest = testService.createTest(testVO);
        return ResponseEntity.ok(createdTest);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "테스트 데이터 수정", description = "기존 테스트 데이터를 수정합니다.")
    public ResponseEntity<TestVO> updateTest(
            @Parameter(description = "수정할 테스트 데이터 ID") @PathVariable Long id,
            @RequestBody TestVO testVO) {
        log.info("테스트 데이터 수정 요청: ID={}", id);
        testVO.setId(id);
        TestVO updatedTest = testService.updateTest(testVO);
        return ResponseEntity.ok(updatedTest);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "테스트 데이터 조회", description = "ID로 특정 테스트 데이터를 조회합니다.")
    public ResponseEntity<TestVO> getTest(
            @Parameter(description = "조회할 테스트 데이터 ID") @PathVariable Long id) {
        log.info("테스트 데이터 조회 요청: ID={}", id);
        Optional<TestVO> test = testService.findById(id);
        return test.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "모든 테스트 데이터 조회", description = "모든 테스트 데이터를 조회합니다.")
    public ResponseEntity<List<TestVO>> getAllTests() {
        log.info("모든 테스트 데이터 조회 요청");
        List<TestVO> tests = testService.findAll();
        return ResponseEntity.ok(tests);
    }
    
    @GetMapping("/search")
    @Operation(summary = "이름으로 테스트 데이터 검색", description = "테스트 이름으로 데이터를 검색합니다.")
    public ResponseEntity<List<TestVO>> searchTestsByName(
            @Parameter(description = "검색할 테스트 이름") @RequestParam String testName) {
        log.info("테스트 데이터 검색 요청: {}", testName);
        List<TestVO> tests = testService.findByTestName(testName);
        return ResponseEntity.ok(tests);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "테스트 데이터 삭제", description = "특정 테스트 데이터를 삭제합니다.")
    public ResponseEntity<Void> deleteTest(
            @Parameter(description = "삭제할 테스트 데이터 ID") @PathVariable Long id) {
        log.info("테스트 데이터 삭제 요청: ID={}", id);
        testService.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/transaction/success")
    @Operation(summary = "트랜잭션 성공 테스트", description = "두 개의 데이터를 성공적으로 생성하는 트랜잭션을 테스트합니다.")
    public ResponseEntity<String> testTransactionSuccess() {
        log.info("트랜잭션 성공 테스트 요청");
        
        TestVO test1 = TestVO.builder()
                .testName("트랜잭션 성공 테스트 1")
                .testDescription("첫 번째 성공 테스트")
                .build();
        
        TestVO test2 = TestVO.builder()
                .testName("트랜잭션 성공 테스트 2")
                .testDescription("두 번째 성공 테스트")
                .build();
        
        testService.createMultipleTestsSuccess(test1, test2);
        return ResponseEntity.ok("트랜잭션 성공 테스트 완료");
    }
    
    @PostMapping("/transaction/rollback")
    @Operation(summary = "트랜잭션 롤백 테스트", description = "예외 발생으로 인한 트랜잭션 롤백을 테스트합니다.")
    public ResponseEntity<String> testTransactionRollback() {
        log.info("트랜잭션 롤백 테스트 요청");
        
        TestVO test1 = TestVO.builder()
                .testName("롤백 테스트 1")
                .testDescription("첫 번째 롤백 테스트")
                .build();
        
        TestVO test2 = TestVO.builder()
                .testName("롤백 테스트 2")
                .testDescription("두 번째 롤백 테스트 - 실패 예정")
                .build();
        
        try {
            testService.createMultipleTestsWithRollback(test1, test2);
            return ResponseEntity.ok("롤백 테스트 - 예상과 다르게 성공함");
        } catch (Exception e) {
            return ResponseEntity.ok("트랜잭션 롤백 테스트 완료 - 예외 발생: " + e.getMessage());
        }
    }
}
