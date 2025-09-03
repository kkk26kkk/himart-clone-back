package com.himartclone.mybatis.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * MyBatis 트랜잭션 통합 구조 설명
 * 
 * 트랜잭션 관리는 다음과 같은 계층 구조로 이루어집니다:
 * 
 * 1. @Transactional (Spring AOP)
 *    ↓
 * 2. DataSourceTransactionManager (실제 트랜잭션 관리)
 *    ↓
 * 3. SqlSessionTemplate (MyBatis-Spring 통합)
 *    ↓
 * 4. SqlSessionFactory (MyBatis 핵심)
 *    ↓
 * 5. DataSource (실제 DB 연결)
 */
@Configuration
@EnableTransactionManagement
public class MyBatisTransactionExplain {
    
    /**
     * 1단계: 실제 트랜잭션 관리자
     * - 트랜잭션 시작/커밋/롤백을 담당
     * - Connection을 ThreadLocal에 바인딩
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
    
    /**
     * 2단계: MyBatis 핵심 팩토리
     * - SqlSession 생성을 담당
     * - 하지만 트랜잭션 관리는 하지 않음!
     * - 단순히 MyBatis 설정과 매퍼 정보를 제공
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        // SqlSessionFactory는 다음 역할만 수행:
        // 1. MyBatis 설정 정보 보관
        // 2. Mapper XML 파일 위치 정보
        // 3. Type Alias 설정
        // 4. DataSource 참조 (트랜잭션 통합을 위해)
        
        // ❌ 트랜잭션 관리는 하지 않음!
        // ✅ 트랜잭션 통합을 위한 인프라만 제공
        return null; // 실제 구현은 MyBatisConfig 참조
    }
    
    /**
     * 3단계: MyBatis-Spring 통합 핵심
     * - 실제 트랜잭션 통합이 일어나는 곳
     * - Spring의 트랜잭션에 참여하는 SqlSession 생성
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
        
        // SqlSessionTemplate의 핵심 기능:
        // 1. 현재 Spring 트랜잭션이 있으면 해당 트랜잭션에 참여
        // 2. 트랜잭션이 없으면 auto-commit 모드로 실행
        // 3. 트랜잭션 종료 시 SqlSession 자동 정리
    }
}

/**
 * 실제 동작 예시:
 * 
 * @Transactional
 * public void someMethod() {
 *     // 1. DataSourceTransactionManager가 트랜잭션 시작
 *     // 2. Connection을 ThreadLocal에 저장
 *     
 *     testRepository.insertTest(testVO);
 *     // 3. SqlSessionTemplate이 ThreadLocal에서 Connection 조회
 *     // 4. 동일한 Connection으로 SQL 실행 (트랜잭션 참여!)
 *     
 *     testRepository.updateTest(testVO);  
 *     // 5. 같은 Connection 재사용 (같은 트랜잭션)
 *     
 *     // 6. 메서드 종료 시 DataSourceTransactionManager가 커밋
 * }
 * 
 * 핵심: SqlSessionFactory는 설정 정보만 제공하고,
 *      실제 트랜잭션 통합은 SqlSessionTemplate이 담당!
 */
