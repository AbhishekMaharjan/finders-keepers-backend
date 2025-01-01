package com.FindersKeepers.backend.abstractclass;


import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
public class AbstractPojo implements Serializable {

    private Long id;

    private String createdDate;

    private String modifiedDate;

    private Long createdBy;

    private Long modifiedBy;
}
