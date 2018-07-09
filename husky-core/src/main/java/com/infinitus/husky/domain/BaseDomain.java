package com.infinitus.husky.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@MappedSuperclass
public class BaseDomain implements Serializable {

    @Setter
    @Getter
    protected int version;
    @Setter
    @Getter
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="CREATETIME",updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    protected Date createTime;
    @Setter
    @Getter
    @Column(name="UPDATETIME")
    @org.hibernate.annotations.UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    protected Date modifyTime;

}
