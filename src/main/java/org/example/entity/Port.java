package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.math.BigDecimal;

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

    // Геттеры и сеттеры
    public String getUnLocode() {
        return unLocode;
    }

    public void setUnLocode(String unLocode) {
        this.unLocode = unLocode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public BigDecimal getHarborDepth() {
        return BigDecimal.valueOf(harborDepth);
    }

    public void setHarborDepth(BigDecimal harborDepth) {
        this.harborDepth = harborDepth.doubleValue();
    }

    public List<VoyageStage> getDepartureStages() {
        return departureStages;
    }

    public void setDepartureStages(List<VoyageStage> departureStages) {
        this.departureStages = departureStages;
    }

    public List<VoyageStage> getArrivalStages() {
        return arrivalStages;
    }

    public void setArrivalStages(List<VoyageStage> arrivalStages) {
        this.arrivalStages = arrivalStages;
    }
}