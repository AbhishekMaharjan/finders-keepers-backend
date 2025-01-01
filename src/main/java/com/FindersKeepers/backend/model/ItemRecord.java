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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item_record")
@SQLDelete(sql = "UPDATE item_record SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
public class ItemRecord extends AbstractEntity<Long> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_record_SEQ_GEN")
    @SequenceGenerator(name = "item_record_SEQ_GEN", sequenceName = "item_record_seq", initialValue = 1, allocationSize = 1)
    @Basic(optional = false)
    private Long id;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "status", length = 20)
    private String status;

    @JoinColumn(name = "users_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Users claimUser;

    @JoinColumn(name = "item_id", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.EAGER)
    private Item item;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id", referencedColumnName = "id")
    private File proofImage;

    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted = false;
}
