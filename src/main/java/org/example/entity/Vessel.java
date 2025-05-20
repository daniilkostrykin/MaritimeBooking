package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vessels", schema = "maritime_booking")
public class Vessel {
    @Id
    @Column(name = "imo", length = 10)
    private String imo;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "owner", nullable = false, length = 100)
    private String owner;

    @Column(name = "year_built", nullable = false)
    private Integer yearBuilt;

    @Column(name = "registration_country", nullable = false, length = 50)
    private String registrationCountry;

    @OneToMany(mappedBy = "vessel")
    private List<Voyage> voyages;

    @OneToMany(mappedBy = "vessel")
    private List<Cabin> cabins;

}