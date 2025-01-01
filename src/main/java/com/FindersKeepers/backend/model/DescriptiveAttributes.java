package com.FindersKeepers.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "descriptive_attributes")
@SQLDelete(sql = "UPDATE descriptive_attributes SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted = false")
public class DescriptiveAttributes {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "descriptive_attributes_SEQ_GEN")
    @SequenceGenerator(name = "descriptive_attributes_SEQ_GEN", sequenceName = "descriptive_attributes_seq", initialValue = 1, allocationSize = 1)
    @Basic(optional = false)
    private Long id;

    @Column(name = "brand", length = 50)
    private String brand;

    @Column(name = "color", length = 20)
    private String color;

    @Column(name = "unique_identifier", length = 50)
    private String uniqueIdentifier;

    @Column(name = "item_condition")
    private String itemCondition;

    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted = false;

}
