package com.FindersKeepers.backend.model;

import com.FindersKeepers.backend.abstractclass.AbstractEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "item")
@SQLDelete(sql = "UPDATE item SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted = false")
public class Item extends AbstractEntity<Long> implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_SEQ_GEN")
    @SequenceGenerator(name = "item_SEQ_GEN", sequenceName = "item_seq", initialValue = 1, allocationSize = 1)
    @Basic(optional = false)
    private Long id;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "nearest_landmark", length = 500)
    private String nearestLandmark;

    @Column(name = "date_reported")
    private LocalDateTime dateReported;

    @Column(name = "status")
    private String itemStatus;

    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted = false;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", nullable = false, referencedColumnName = "id")
    private Address location;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "descriptive_id", nullable = false, referencedColumnName = "id")
    private DescriptiveAttributes descriptiveAttributes;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name = "item_categories_join", joinColumns = {
            @JoinColumn(name = "item_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "item_category_id", referencedColumnName = "id")})
    private List<ItemCategory> itemCategoryList;

    @JoinColumn(name = "users_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Users users;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id", nullable = false, referencedColumnName = "id")
    private File itemPhoto;
}
