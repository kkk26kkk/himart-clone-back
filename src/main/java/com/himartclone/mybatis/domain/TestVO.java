package com.himartclone.mybatis.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MyBatis 테스트용 VO 클래스
 * Test_Tb 테이블과 매핑되는 도메인 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestVO {
    
    /**
     * 기본키 - 자동증가
     */
    private Long id;
    
    /**
     * 테스트 이름
     */
    private String testName;
    
    /**
     * 테스트 설명
     */
    private String testDescription;
    
    /**
     * 생성일시 (데이터베이스에서 자동 설정)
     */
    private java.time.LocalDateTime createdAt;
}
