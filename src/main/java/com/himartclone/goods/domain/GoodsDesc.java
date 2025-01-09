package com.himartclone.goods.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class GoodsDesc {

    private String descTpCd;
    private int sn;
    private String descCont;

    public boolean isDescription() {
        return "01".equals(descTpCd);
    }

    public boolean isMdNotice() {
        return "02".equals(descTpCd);
    }
}
