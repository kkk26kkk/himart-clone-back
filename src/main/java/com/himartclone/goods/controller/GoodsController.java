package com.himartclone.goods.controller;

import com.himartclone.goods.controller.dto.GoodsDetailResponseDto;
import com.himartclone.goods.domain.GoodsBase;
import com.himartclone.goods.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Goods API", description = "상품 API")
public class GoodsController {

    private final GoodsService goodsService;

    @GetMapping("/goods/goodsDetail")
    @Operation(summary = "상품 상세 조회", description = "goodsNo로 상품 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "상품 정보를 찾을 수 없음")
    })
    public ResponseEntity<GoodsDetailResponseDto> findOneGoods(@RequestParam String goodsNo) {
        GoodsBase goodsBase = goodsService.findGoodsOne(goodsNo);
        GoodsDetailResponseDto dto = new GoodsDetailResponseDto(goodsBase);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
