package com.himartclone.goods.controller.dto;

import com.himartclone.goods.domain.DisplayedGoods;
import com.himartclone.goods.domain.GoodsBase;
import com.himartclone.goods.domain.GoodsConts;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "상품 마스터")
public class GoodsDetailResponseDto {

    @Schema(description = "상품번호")
    private String goodsNo;

    @Schema(description = "상품명")
    private String goodsNm;

    @Schema(description = "모델명")
    private String mdlNm;

    @Schema(description = "브랜드명")
    private String brndNm;

    @Schema(description = "판매상태")
    private String saleStatCd;

    @Schema(description = "상품유형", example = "일반상품")
    private String goodsTpCd;

    @Schema(description = "상품구성", example = "일반형")
    private String goodsCmpsCd;

    @Schema(description = "배송유형", example = "하이마트배송")
    private String dlvTpCd;

    @Schema(description = "배송수단", example = "직배")
    private String dlvMeanCd;

    @Schema(description = "판매가")
    private int salePrc;

    @Schema(description = "사용설명서")
    private String manual;

    @Schema(description = "상품이미지")
    private List<GoodsConts.ImageInfo> images;

    @Schema(description = "MD공지")
    private String mdNotice;

    @Schema(description = "상세설명")
    private String description;

    @Schema(description = "전시상품정보")
    private Map<String, Object> displayInfo = new HashMap<>();

    public GoodsDetailResponseDto(GoodsBase goodsBase) {
        this.goodsNo = goodsBase.getId();
        this.goodsNm = goodsBase.getGoodsNm();
        this.mdlNm = goodsBase.getMdlNm();
        this.brndNm = goodsBase.getBrndNm();
        this.saleStatCd = goodsBase.getSaleStatCd();
        this.goodsTpCd = goodsBase.getGoodsTpCd();
        this.goodsCmpsCd = goodsBase.getGoodsCmpsCd();
        this.dlvTpCd = goodsBase.getDlvTpCd();
        this.dlvMeanCd = goodsBase.getDlvMeanCd();
        this.salePrc = goodsBase.getSalePrc();
        this.manual = goodsBase.getManual();
        this.images = goodsBase.getImages();
        this.mdNotice = goodsBase.getMdNotice();
        this.description = goodsBase.getDescription();

        if (goodsBase instanceof DisplayedGoods displayedGoods) {
            displayInfo.put("brchId", displayedGoods.getBrchId());
            displayInfo.put("prdGrdCd", displayedGoods.getPrdGrdCd());
        }
    }
}
