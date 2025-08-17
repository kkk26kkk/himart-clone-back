package com.himartclone.member.domain;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Member {

    private Long id;

    @NotEmpty
    private String memberId;

    @NotEmpty
    private String name;

    @NotEmpty
    private String password;
}
