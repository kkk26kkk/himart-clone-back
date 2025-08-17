package com.himartclone.goods.service;

import com.himartclone.goods.domain.GoodsBase;
import com.himartclone.goods.repository.GoodsBoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class GoodsBoService {

    private final GoodsBoRepository repository;

    public List<GoodsBase> goodsList() {
        return repository.findAll();
    }

    public GoodsBase goodsDetail(String goodsNo) {
        return repository.findById(goodsNo)
                .orElseThrow(() -> new RuntimeException("상품 정보가 존재하지 않습니다."));
    }

    public void createGoods(GoodsBase goods) {
        repository.save(goods);
    }
}
