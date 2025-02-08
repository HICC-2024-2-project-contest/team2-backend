package com.hiccproject.moaram.entity.composite;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class ExhibitionScrapId implements Serializable {
    private Long userId;
    private Long exhibitionId;
}
