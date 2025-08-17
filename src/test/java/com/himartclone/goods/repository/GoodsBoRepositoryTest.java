package com.himartclone.goods.repository;

import com.himartclone.goods.controller.dto.GoodsBoDetailDto;
import com.himartclone.goods.domain.GoodsBase;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class GoodsBoRepositoryTest {

    @Autowired
    GoodsBoRepository repository;

    @Test
    @Transactional
    void 상품조회() {
        GoodsBase goodsBase = repository.findById("0000001745")
                .orElseThrow(() -> new RuntimeException("상품 정보가 존재하지 않습니다."));

        GoodsBoDetailDto dto = GoodsBoDetailDto.goodsBaseToDto(goodsBase);

        System.out.println(dto);
    }
}
