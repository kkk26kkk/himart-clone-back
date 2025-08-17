package com.himartclone.goods.controller.dto;

import com.himartclone.goods.domain.DisplayedGoods;
import com.himartclone.goods.domain.GoodsBase;
import com.himartclone.goods.domain.GoodsConts;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class GoodsBoDetailDto {

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

    private GoodsBoDetailDto() {}

    public static GoodsBoDetailDto goodsBaseToDto(GoodsBase goodsBase) {
        GoodsBoDetailDto dto = new GoodsBoDetailDto();
        dto.goodsNo = goodsBase.getId();
        dto.goodsNm = goodsBase.getGoodsNm();
        dto.mdlNm = goodsBase.getMdlNm();
        dto.brndNm = goodsBase.getBrndNm();
        dto.saleStatCd = goodsBase.getSaleStatCd();
        dto.goodsTpCd = goodsBase.getGoodsTpCd();
        dto.goodsCmpsCd = goodsBase.getGoodsCmpsCd();
        dto.dlvTpCd = goodsBase.getDlvTpCd();
        dto.dlvMeanCd = goodsBase.getDlvMeanCd();
        dto.salePrc = goodsBase.getSalePrc();
        dto.manual = goodsBase.getManual();
        dto.images = goodsBase.getImages();
        dto.mdNotice = goodsBase.getMdNotice();
        dto.description = goodsBase.getDescription();

        return dto;
    }
}
