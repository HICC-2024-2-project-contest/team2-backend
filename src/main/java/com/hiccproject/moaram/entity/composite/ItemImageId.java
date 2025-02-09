package com.hiccproject.moaram.entity.composite;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class ItemImageId implements Serializable {
    private Long item_id;  // item_id로 변경
    private Integer idx;
}

