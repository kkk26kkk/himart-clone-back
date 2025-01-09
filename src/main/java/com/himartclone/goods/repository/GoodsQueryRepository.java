package com.himartclone.goods.repository;

import com.himartclone.goods.domain.GoodsBase;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class GoodsQueryRepository {

    private final EntityManager em;

    public GoodsBase findGoodsOne(String id) {
        LocalDateTime now = LocalDateTime.now();

        String query = "select gb " +
                        "from GoodsBase gb " +
                        "left join fetch gb.goodsPriceList prc "+
                        "where gb.id = :id " +
                        "and :now between prc.histStrtDtime and prc.histEndDtime";

        return em.createQuery(query, GoodsBase.class)
                .setParameter("id", id)
                .setParameter("now", now)
                .getSingleResult();
    }
}
