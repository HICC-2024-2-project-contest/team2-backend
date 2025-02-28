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
public class ExhibitionScrapId implements Serializable {
    private Long userId;
    private Long exhibitionId;
}
