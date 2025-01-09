package com.himartclone.goods.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class GoodsPrice {

    private LocalDateTime histStrtDtime;
    private LocalDateTime histEndDtime;
    private int salePrc;

    public boolean isCurrentPrice() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(histStrtDtime) && now.isBefore(histEndDtime);
    }
}
