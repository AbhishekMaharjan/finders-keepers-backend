package com.FindersKeepers.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "file")
public class File implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_SEQ_GEN")
    @SequenceGenerator(name = "file_SEQ_GEN", sequenceName = "file_seq", initialValue = 1, allocationSize = 1)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "path", length = 100)
    private String path;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "file_type", length = 50)
    private String fileType;
}
