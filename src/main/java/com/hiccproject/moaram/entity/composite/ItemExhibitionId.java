package com.hiccproject.moaram.entity.composite;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class ItemExhibitionId implements Serializable {
    private Long itemId;
    private Long exhibitionId;
}
