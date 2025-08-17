package com.himartclone.goods.controller.dto;

import com.himartclone.common.file.UploadFile;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GoodsBoCreateDto {

    @NotBlank
    private String goodsNo;

    @NotBlank
    private String goodsNm;

    @NotBlank
    private String mdlNm;

    @NotBlank
    private String brndNm;
//    private String saleStatCd;
//    private String goodsTpCd;
//    private String goodsCmpsCd;
    private String dlvTpCd;
//    private String dlvMeanCd;

    @NotNull
    @Min(1000)
    private Integer salePrc;

    private String description;

    private MultipartFile imageFile;
}
