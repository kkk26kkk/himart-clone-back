package com.himartclone.category.repository;

import com.himartclone.category.domain.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CategoryRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @Transactional
    void 카테고리등록() throws Exception{

        Category category1 = new Category(
                "1106000000", "냉장고·주방가전", "1106000000", "", "", "");

        Category category2 = new Category(
                "1106120000", "전기포트", "1106000000", "1106120000", "", "");

        Category category3 = new Category(
                "1106120200", "전기주전자", "1106000000", "1106120000", "1106120200", "");

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);

        em.flush();

        Category findCategory1 = categoryRepository.findById(category1.getId()).get();
        Category findCategory2 = categoryRepository.findById(category2.getId()).get();
        Category findCategory3 = categoryRepository.findById(category3.getId()).get();

        assertThat(findCategory1.getDispNm()).isEqualTo("냉장고·주방가전");
        assertThat(findCategory2.getDispNm()).isEqualTo("전기포트");
        assertThat(findCategory3.getDispNm()).isEqualTo("전기주전자");
    }
}
