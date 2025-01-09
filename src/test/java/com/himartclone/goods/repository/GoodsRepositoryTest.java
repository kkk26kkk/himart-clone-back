package com.himartclone.goods.repository;

import com.himartclone.goods.domain.GoodsBase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

@ComponentScan
@SpringBootTest
public class GoodsRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsQueryRepository goodsQueryRepository;

    @Autowired
    private GoodsCategoryRepository goodsCategoryRepository;

    @Test
    @Transactional
    @Rollback(false)
    void 상품조회() throws Exception {

        String goodsNo = "0000001745";

        GoodsBase findGoodsBase1 = em.find(GoodsBase.class, goodsNo);

        assertThat(findGoodsBase1.getSalePrc()).isEqualTo(15000);
        assertThat(findGoodsBase1.getImages().get(1))
                .isEqualTo("https://static1.e-himart.co.kr/contents/goods/00/00/00/17/45/0000001745__BI-81252A__M_640_640.jpg");
        assertThat(findGoodsBase1.getDescription()).isEqualTo("12");
        assertThat(findGoodsBase1.getMdNotice()).isEqualTo("MD공지");
        assertThat(findGoodsBase1.getGoodsCategoryList()).hasSize(1);

        em.clear();
        // em.clear() 하지 않으면 아래에서 desc_info가 없다고 판단하여 변경 발생.
        // delete from pr_goods_desc_info 실행

        GoodsBase findGoodsBase2 = goodsRepository.findById(goodsNo)
                .orElseThrow(() -> new RuntimeException("상품 정보가 존재하지 않습니다."));
        assertThat(findGoodsBase2.getSalePrc()).isEqualTo(15000);

        em.clear();

        GoodsBase findGoodsBase3 = goodsQueryRepository.findGoodsOne(goodsNo);
        assertThat(findGoodsBase3.getGoodsCategoryList()).hasSize(1);
        assertThat(findGoodsBase3.getGoodsPriceList()).hasSize(1);
        assertThat(findGoodsBase3.getSalePrc()).isEqualTo(15000);
    }
}
