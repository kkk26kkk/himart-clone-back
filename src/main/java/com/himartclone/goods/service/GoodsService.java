package com.himartclone.goods.service;

import com.himartclone.goods.domain.GoodsBase;
import com.himartclone.goods.repository.GoodsQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoodsService {

    private final GoodsQueryRepository goodsQueryRepository;

    public GoodsBase findGoodsOne(String id) {

        GoodsBase goodsBase = goodsQueryRepository.findGoodsOne(id);

        if (goodsBase == null) {
            throw new RuntimeException("상품 정보가 존재하지 않습니다.");
        }

        return goodsBase;
    }
}
