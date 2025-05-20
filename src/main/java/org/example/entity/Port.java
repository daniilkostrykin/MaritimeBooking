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
@Table(name = "ports", schema = "maritime_booking")
public class Port {
    @Id
    @Column(name = "un_locode", length = 5)
    private String unLocode;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Column(name = "harbor_depth", nullable = false)
    private Double harborDepth;

    @OneToMany(mappedBy = "departurePort")
    private List<VoyageStage> departureStages;

    @OneToMany(mappedBy = "arrivalPort")
    private List<VoyageStage> arrivalStages;
}