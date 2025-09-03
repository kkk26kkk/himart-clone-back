package com.himartclone.mybatis.repository;

import com.himartclone.mybatis.domain.TestVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis 테스트용 Repository 인터페이스
 * Test_Tb 테이블에 대한 데이터 액세스 계층
 */
@Mapper
public interface TestRepository {
    
    /**
     * 새로운 테스트 데이터 삽입
     * @param testVO 삽입할 테스트 데이터
     * @return 삽입된 행 수
     */
    int insertTest(TestVO testVO);
    
    /**
     * 테스트 데이터 업데이트
     * @param testVO 업데이트할 테스트 데이터
     * @return 업데이트된 행 수
     */
    int updateTest(TestVO testVO);
    
    /**
     * ID로 테스트 데이터 조회
     * @param id 조회할 테스트 데이터의 ID
     * @return 조회된 테스트 데이터
     */
    Optional<TestVO> findById(@Param("id") Long id);
    
    /**
     * 모든 테스트 데이터 조회
     * @return 모든 테스트 데이터 리스트
     */
    List<TestVO> findAll();
    
    /**
     * 이름으로 테스트 데이터 조회
     * @param testName 조회할 테스트 이름
     * @return 해당 이름을 가진 테스트 데이터 리스트
     */
    List<TestVO> findByTestName(@Param("testName") String testName);
    
    /**
     * 테스트 데이터 삭제
     * @param id 삭제할 테스트 데이터의 ID
     * @return 삭제된 행 수
     */
    int deleteById(@Param("id") Long id);
}
