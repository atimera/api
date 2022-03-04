package com.atimera.api.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Category implements Serializable {

    @Id
    private Long id;
    private String name;

}
