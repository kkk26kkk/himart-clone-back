package com.himartclone.goods.repository;

import com.himartclone.goods.domain.GoodsCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsCategoryRepository extends JpaRepository<GoodsCategory, GoodsCategory.GoodsCategoryKey> {
}
