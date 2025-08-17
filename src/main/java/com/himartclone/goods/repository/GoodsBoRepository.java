package com.himartclone.goods.repository;

import com.himartclone.goods.domain.GoodsBase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsBoRepository extends JpaRepository<GoodsBase, String>  {
}
