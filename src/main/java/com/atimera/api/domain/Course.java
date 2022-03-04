package com.atimera.api.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Course implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date heureDebut;
    private Date heureFin;
    private Integer gain;
    private Integer compteurKm;
    private Boolean pleinEssence;
    private Double montantEssence;
    private String note;

}
