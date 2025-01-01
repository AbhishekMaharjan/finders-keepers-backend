package com.FindersKeepers.backend.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "country")
public class Country implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "country_SEQ_GEN")
    @SequenceGenerator(name = "country_SEQ_GEN", sequenceName = "country_seq", initialValue = 1, allocationSize = 1)
    @Basic(optional = false)
    private Integer id;

    @NotNull
    @Column(length = 50, nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(length = 10, nullable = false)
    private String code;

}
