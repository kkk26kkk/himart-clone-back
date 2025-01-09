package com.himartclone.goods.controller;

import com.himartclone.goods.controller.dto.GoodsDetailResponseDto;
import com.himartclone.goods.domain.GoodsBase;
import com.himartclone.goods.service.GoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;

    @GetMapping("/goods/goodsDetail")
    public ResponseEntity<GoodsDetailResponseDto> findOneGoods(@RequestParam String goodsNo) {
        GoodsBase goodsBase = goodsService.findGoodsOne(goodsNo);
        GoodsDetailResponseDto dto = new GoodsDetailResponseDto(goodsBase);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
