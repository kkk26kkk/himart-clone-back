package com.himartclone.common;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class MessageSourceTest {

    @Autowired
    MessageSource ms;

    @Test
    void goodsSelect() {
        String message = ms.getMessage("goods.select", null, null);
        assertThat(message).isEqualTo("상품을 선택해주세요.");

        String messageEn = ms.getMessage("goods.select", null, Locale.ENGLISH);
        assertThat(messageEn).isEqualTo("Please select a product.");
    }
}
