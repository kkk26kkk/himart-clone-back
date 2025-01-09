package com.himartclone.goods.domain;

import com.himartclone.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "pr_goods_base")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class GoodsBase extends BaseTimeEntity {

    @Id
    @Column(name = "goods_no")
    protected String id;

    protected String goodsNm;

    protected String mdlNm;

    protected String brndNm;

    protected String saleStatCd;

    protected String goodsTpCd;

    protected String goodsCmpsCd;

    protected String goodsStatSctCd;

    protected String supCorpNo;

    protected String dlvTpCd;

    protected String dlvMeanCd;

    @OneToMany(mappedBy = "goods")
    protected List<GoodsCategory> goodsCategoryList;

    @ElementCollection
    @CollectionTable(name = "pr_goods_prc_hist", joinColumns = @JoinColumn(name = "goods_no"))
    protected List<GoodsPrice> goodsPriceList;

//    protected GoodsStock stock;

//    protected List<GoodsItem> goodsItemList;

    @ElementCollection
    @CollectionTable(name = "pr_goods_conts_info", joinColumns = @JoinColumn(name = "goods_no"))
    @OrderBy("sn")
    protected List<GoodsConts> goodsContsList;

    @ElementCollection
    @CollectionTable(name = "pr_goods_desc_info", joinColumns = @JoinColumn(name = "goods_no"))
    @OrderBy("sn")
    protected List<GoodsDesc> goodsDescList;

    public boolean isDisplayed() {
        return "03".equals(goodsStatSctCd);
    }

    public boolean isClearance() {
        return "06".equals(goodsStatSctCd);
    }

    public int getSalePrc() {
        return goodsPriceList.stream()
                .filter(GoodsPrice::isCurrentPrice)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("가격 정보가 존재하지 않습니다."))
                .getSalePrc();
    }

    public String getManual() {
        return goodsContsList.stream()
                .filter(GoodsConts::isManual)
                .findFirst()
                .orElseGet(GoodsConts::new)
                .getFileUrl();
    }

    public List<GoodsConts.ImageInfo> getImages() {
        return goodsContsList.stream()
                .filter(GoodsConts::isImage)
                .map(GoodsConts::toImageInfo)
                .collect(Collectors.toList());
    }

    public String getMdNotice() {
        Optional<GoodsDesc> mdNotice = goodsDescList.stream()
                .filter(GoodsDesc::isMdNotice)
                .findFirst();
        return mdNotice.isPresent() ? mdNotice.get().getDescCont() : "";
    }

    public String getDescription() {
        return goodsDescList.stream()
                .filter(GoodsDesc::isDescription)
                .map(GoodsDesc::getDescCont)
                .collect(Collectors.joining());
    }
}
