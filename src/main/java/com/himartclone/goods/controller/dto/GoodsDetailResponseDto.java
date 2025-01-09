package com.himartclone.goods.controller.dto;

import com.himartclone.goods.domain.DisplayedGoods;
import com.himartclone.goods.domain.GoodsBase;
import com.himartclone.goods.domain.GoodsConts;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class GoodsDetailResponseDto {

    private String goodsNo;
    private String goodsNm;
    private String mdlNm;
    private String brndNm;
    private String saleStatCd;
    private String goodsTpCd;
    private String goodsCmpsCd;
    private String dlvTpCd;
    private String dlvMeanCd;
    private int salePrc;
    private String manual;
    private List<GoodsConts.ImageInfo> images;
    private String mdNotice;
    private String description;
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
