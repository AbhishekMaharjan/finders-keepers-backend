package com.FindersKeepers.backend.model.auth;


import com.FindersKeepers.backend.abstractclass.AbstractEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "authorities")
public class Authorities extends AbstractEntity<Integer> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authorities_SEQ_GEN")
    @SequenceGenerator(name = "authorities_SEQ_GEN", sequenceName = "authorities_seq", initialValue = 1, allocationSize = 1)
    @Basic(optional = false)
    private Integer id;

    @NotNull
    @Column(unique = true, length = 25, nullable = false)
    private String authority;

    @NotNull
    @Column(nullable = false)
    private Boolean status = true;

}