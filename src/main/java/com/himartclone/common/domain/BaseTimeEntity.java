package com.himartclone.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public abstract class BaseTimeEntity implements Persistable<String> {

    @CreatedDate
    @Column(name = "sys_reg_dtime", updatable = false)
    private LocalDateTime sysRegDtime;

    @LastModifiedDate
    @Column(name = "sys_mod_dtime")
    private LocalDateTime sysModDtime;

    @Override
    public boolean isNew() {
        return sysRegDtime == null;
    }
}
