package com.himartclone.category.domain;

import com.himartclone.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "pr_disp_shop_base")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Category extends BaseTimeEntity {

    @Id
    @Column(name = "disp_no")
    private String id;
    private String dispNm;
    private String dispLrgNo;
    private String dispMidNo;
    private String dispSmlNo;
    private String dispThnNo;
}