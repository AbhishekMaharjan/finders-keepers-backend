package com.FindersKeepers.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item_category")
@SQLDelete(sql = "UPDATE item SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted = false")
public class ItemCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_category_SEQ_GEN")
    @SequenceGenerator(name = "item_category_SEQ_GEN", sequenceName = "item_category_seq", initialValue = 1, allocationSize = 1)
    @Basic(optional = false)
    private Long id;

    @Column(name = "level")
    private int level;

    @Column(name = "name", length = 50)
    private String name;

    @JoinColumn(name = "parent_item_category_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private ItemCategory parentCategory;

    @Column(name = "is_deleted", length = 500)
    @JsonIgnore
    private Boolean isDeleted = false;
}
