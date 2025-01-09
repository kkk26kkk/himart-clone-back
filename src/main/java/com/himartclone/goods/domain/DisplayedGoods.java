package com.himartclone.goods.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pr_brch_disp_goods_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class DisplayedGoods extends GoodsBase {

    private String brchId;
    private String prdGrdCd;

    public void updateDisplayInfo(String goodsNm, String brchId, String prdGrdCd) {
        super.goodsNm = goodsNm;
        this.brchId = brchId;
        this.prdGrdCd = prdGrdCd;
    }

    public static DisplayedGoods fromGoodsBase(GoodsBase goodsBase, String brchId, String prdGrdCd) {
        DisplayedGoods displayedGoods = new DisplayedGoods();
        displayedGoods.id = goodsBase.getId();
        displayedGoods.goodsNm = goodsBase.getGoodsNm();
        displayedGoods.mdlNm = goodsBase.getMdlNm();
        displayedGoods.brndNm = goodsBase.getBrndNm();
        displayedGoods.saleStatCd = goodsBase.getSaleStatCd();
        displayedGoods.goodsTpCd = goodsBase.getGoodsTpCd();
        displayedGoods.goodsCmpsCd = goodsBase.getGoodsCmpsCd();
        displayedGoods.goodsStatSctCd = "03";
        displayedGoods.supCorpNo = goodsBase.getSupCorpNo();
        displayedGoods.dlvTpCd = goodsBase.getDlvTpCd();
        displayedGoods.dlvMeanCd = goodsBase.getDlvMeanCd();
        displayedGoods.goodsCategoryList = goodsBase.getGoodsCategoryList();
        displayedGoods.goodsPriceList = goodsBase.getGoodsPriceList();
        displayedGoods.goodsContsList = goodsBase.getGoodsContsList();
        displayedGoods.goodsDescList = goodsBase.getGoodsDescList();
        displayedGoods.brchId = brchId;
        displayedGoods.prdGrdCd = prdGrdCd;
        return displayedGoods;
    }
}
