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
public class ItemExhibitionId implements Serializable {
    private Long itemId;
    private Long exhibitionId;
}
