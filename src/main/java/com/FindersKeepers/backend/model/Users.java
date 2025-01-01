package com.FindersKeepers.backend.model;


import com.FindersKeepers.backend.abstractclass.AbstractEntity;
import com.FindersKeepers.backend.model.auth.Authorities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
public class Users extends AbstractEntity<Long> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_SEQ_GEN")
    @SequenceGenerator(name = "user_SEQ_GEN", sequenceName = "user_seq", initialValue = 1, allocationSize = 1)
    @Basic(optional = false)
    private Long id;

    @Singular
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "users_authorities", joinColumns = {
            @JoinColumn(name = "users_id", referencedColumnName = "id")}, inverseJoinColumns = {
            @JoinColumn(name = "authorities_id", referencedColumnName = "id")})
    @JsonIgnore
    private Set<Authorities> authorities;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @Column(length = 90)
    @JsonIgnore
    private String password;

    @Column(name = "is_enabled")
    @JsonIgnore
    private Boolean isEnabled = true;

    @Column(name = "last_password_change_date")
    @JsonIgnore
    private Date lastPasswordChangeDate = new Date();

    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "is_deleted")
    @JsonIgnore
    private Boolean isDeleted = false;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;
}