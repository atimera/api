package com.atimera.api.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserInformations {
    @Transient
    private static final String SEQUENCE_NAME = "user_informations_seq";

    @SequenceGenerator(
            name = SEQUENCE_NAME,
            sequenceName = SEQUENCE_NAME,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = SEQUENCE_NAME
    )
    @Id
    private Long idUserInformations;
    private String address;
    private String city;
    private String phoneNumber;

//    @OneToOne( cascade = CascadeType.ALL )
//    @JoinColumn( name = "idUser", nullable = false )
//    private UserPrincipal user;
}
