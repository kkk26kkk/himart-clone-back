package com.himartclone;

import com.himartclone.category.domain.Category;
import com.himartclone.goods.domain.*;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InitData {

    private final InitService initService;

    public InitData(InitService initService) {
        this.initService = initService;
    }

    @PostConstruct
    void postConstruct() {
        initService.dbInit();
    }

    @Component
    @Transactional
    public static class InitService {

        @PersistenceContext
        private EntityManager em;

        public void dbInit() {

            Category categoryA1 = new Category(
                    "1106000000", "냉장고·주방가전", "1106000000", "", "", "");

            Category categoryA2 = new Category(
                    "1106120000", "전기포트", "1106000000", "1106120000", "", "");

            Category categoryA3 = new Category(
                    "1106120200", "전기주전자", "1106000000", "1106120000", "1106120200", "");

            Category categoryB1 = new Category(
                    "1113000000", "청소기·생활가전", "1113000000", "", "", "");

            Category categoryB2 = new Category(
                    "1113100000", "청소기", "1113000000", "1113100000", "", "");

            Category categoryB3 = new Category(
                    "111300200", "스틱·핸디청소기", "1113000000", "1113100000", "111300200", "");

            em.persist(categoryA1);
            em.persist(categoryA2);
            em.persist(categoryA3);
            em.persist(categoryB1);
            em.persist(categoryB2);
            em.persist(categoryB3);

            // 일반상품
            GoodsBase goodsBase = GoodsBase.builder()
                    .id("0000001745")
                    .goodsNm("미니 전기주전자 BI-81252A [0.8L/1850W]")
                    .mdlNm("BI-81252A")
                    .brndNm("테팔")
                    .saleStatCd("01")
                    .goodsTpCd("01")
                    .goodsCmpsCd("01")
                    .goodsStatSctCd("01")
                    .supCorpNo("10046490")
                    .dlvTpCd("01")
                    .dlvMeanCd("01")
                    .goodsPriceList(List.of(
                            new GoodsPrice(
                                    LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                                    LocalDateTime.of(2024, 12, 31, 23, 59, 59),
                                    10000
                            ),
                            new GoodsPrice(
                                    LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                                    LocalDateTime.of(9999, 12, 31, 23, 59, 59),
                                    15000
                            )))
                    .goodsContsList(List.of(
                            new GoodsConts(
                                    "02",
                                    1,
                                    "/contents/goods/00/00/00/17/45",
                                    "/0000001745__BI-81252A__M_640_640.jpg"
                            )))
                    .goodsDescList(List.of(
                            new GoodsDesc("01", 1, "1"),
                            new GoodsDesc("01", 2, "2"),
                            new GoodsDesc("02", 1, "MD공지")))
                    .build();

            em.persist(goodsBase);

            GoodsCategory goodsCategory =
                    GoodsCategory.builder()
                            .goodsCategoryKey(new GoodsCategory.GoodsCategoryKey(goodsBase.getId(), categoryA3.getId()))
                            .goods(goodsBase)
                            .category(categoryA3)
                            .build();

            em.persist(goodsCategory);

            // 전시상품
            GoodsBase goodsBase2 = GoodsBase.builder()
                    .id("0022103485")
                    .goodsNm("LG A9/200W/물걸레/침구")
                    .mdlNm("AT9170IA.BKOR1")
                    .brndNm("쿠쿠전자")
                    .saleStatCd("01")
                    .goodsTpCd("01")
                    .goodsCmpsCd("01")
                    .goodsStatSctCd("01")
                    .supCorpNo("00000001")
                    .dlvTpCd("01")
                    .dlvMeanCd("02")
                    .goodsPriceList(List.of(
                            new GoodsPrice(
                                    LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                                    LocalDateTime.of(2024, 12, 31, 23, 59, 59),
                                    639000
                            ),
                            new GoodsPrice(
                                    LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                                    LocalDateTime.of(9999, 12, 31, 23, 59, 59),
                                    599000
                            )))
                    .goodsContsList(List.of(
                            new GoodsConts(
                                    "01",
                                    1,
                                    "/contents/goods/00/22/10/34/85/attatch/0/",
                                    "AT9170IA.BKOR1.zip"
                            ),
                            new GoodsConts(
                                    "02",
                                    1,
                                    "/contents/goods/00/22/10/34/85",
                                    "/0022103485__JAT9170IA.BKOR1__M_640_640.jpg"
                            )))
                    .goodsDescList(List.of(
                            new GoodsDesc("01", 1, "상세설명")
                    ))
                    .build();

            GoodsCategory goodsCategory2 =
                    GoodsCategory.builder()
                            .goodsCategoryKey(new GoodsCategory.GoodsCategoryKey(goodsBase2.getId(), categoryB3.getId()))
                            .goods(goodsBase2)
                            .category(categoryB3)
                            .build();

            em.persist(goodsBase2);
            em.persist(goodsCategory2);

            GoodsBase goodsBaseForDisp = GoodsBase.builder()
                    .id("0028266334")
                    .goodsNm(String.format("[%s / %s] %s %s", "상급", "효자점", goodsBase2.getGoodsNm(), "(J)" + goodsBase2.getMdlNm()))
                    .mdlNm("(J)" + goodsBase2.getMdlNm())
                    .brndNm(goodsBase2.getBrndNm())
                    .saleStatCd("01")
                    .goodsTpCd("01")
                    .goodsCmpsCd("01")
                    .goodsStatSctCd("03")
                    .supCorpNo(goodsBase2.getSupCorpNo())
                    .dlvTpCd(goodsBase2.getDlvTpCd())
                    .dlvMeanCd(goodsBase2.getDlvMeanCd())
                    .goodsPriceList(List.of(
                            new GoodsPrice(
                                    LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                                    LocalDateTime.of(9999, 12, 31, 23, 59, 59),
                                    599000
                            )))
                    .goodsContsList(List.of(
                            new GoodsConts(
                                    "01",
                                    1,
                                    "/contents/goods/00/28/26/63/34/attatch/0/",
                                    "AT9170IA.BKOR1.zip"
                            ),
                            new GoodsConts(
                                    "02",
                                    1,
                                    "/contents/goods/00/28/26/63/34",
                                    "/0028266334__JAT9170IA.BKOR1__M_640_640.jpg"
                            )))
                    .goodsDescList(goodsBase2.getGoodsDescList()
                            .stream()
                            .map(desc -> new GoodsDesc(desc.getDescTpCd(), desc.getSn(), desc.getDescCont()))
                            .collect(Collectors.toList()))
                    .build();

            DisplayedGoods displayedGoods = DisplayedGoods.fromGoodsBase(goodsBaseForDisp, "A6031E", "B");

            em.persist(displayedGoods);

            em.flush();
            em.clear();
        }
    }
}
