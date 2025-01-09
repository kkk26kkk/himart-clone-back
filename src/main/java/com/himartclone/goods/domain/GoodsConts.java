package com.himartclone.goods.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class GoodsConts {

    private String contsTpCd;
    private int sn;
    private String filePath;
    private String fileName;

    public boolean isManual() {
        return contsTpCd.equals("01");
    }

    public boolean isImage() {
        return contsTpCd.equals("02");
    }

    public String getFileUrl() {
        return !StringUtils.hasText(filePath) || !StringUtils.hasText(fileName) ?
                "" : "https://static1.e-himart.co.kr" + filePath + fileName;
    }

    public ImageInfo toImageInfo() {
        return new ImageInfo(sn, getFileUrl());
    }

    @AllArgsConstructor
    @Getter
    public static class ImageInfo {

        private int sn;
        private String url;
    }
}
