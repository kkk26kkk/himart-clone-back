package com.himartclone.goods.domain;

import com.himartclone.category.domain.Category;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "pr_disp_goods_info")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class GoodsCategory {

    @EmbeddedId
    private GoodsCategoryKey goodsCategoryKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_no")
    @MapsId("goodsNo")
    private GoodsBase goods;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disp_no")
    @MapsId("dispNo")
    private Category category;

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Getter
    public static class GoodsCategoryKey implements Serializable {

        private String goodsNo;
        private String dispNo;
    }
}
