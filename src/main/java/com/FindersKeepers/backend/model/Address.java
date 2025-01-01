package com.FindersKeepers.backend.model;


import com.FindersKeepers.backend.abstractclass.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "address")
@SQLDelete(sql = "UPDATE address SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
public class Address extends AbstractEntity<Integer> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_SEQ_GEN")
    @SequenceGenerator(name = "address_SEQ_GEN", sequenceName = "address_seq", initialValue = 1, allocationSize = 1)
    @Basic(optional = false)
    private Integer id;

    @Column(name = "street", length = 150, nullable = false)
    private String street;

    @Column(name = "zip_code", nullable = false, length = 20)
    private String zipCode;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "province", length = 50)
    private String province;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id", nullable = false, referencedColumnName = "id")
    private Country country;

    @Column(name = "is_deleted")
    @JsonIgnore
    private boolean isDeleted = false;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

}
