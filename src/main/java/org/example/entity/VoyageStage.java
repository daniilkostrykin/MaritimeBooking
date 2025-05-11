package org.example.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voyage_stages", schema = "maritime_booking")
@IdClass(VoyageStageId.class)
public class VoyageStage {
    @Id
    @Column(name = "stop_number", nullable = false)
    private Integer stopNumber;

    @Id
    @Column(name = "voyage_id")
    private Long voyageId;

    @Id
    @Column(name = "vessel_id")
    private String vesselId;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "voyage_id", referencedColumnName = "id", insertable = false, updatable = false),
            @JoinColumn(name = "vessel_id", referencedColumnName = "vessel_id", insertable = false, updatable = false)
    })
    private Voyage voyage;

    @ManyToOne
    @JoinColumn(name = "departure_port_id", nullable = false)
    private Port departurePort;

    @ManyToOne
    @JoinColumn(name = "arrival_port_id", nullable = false)
    private Port arrivalPort;

    @Column(name = "departure_datetime", nullable = false)
    private LocalDateTime departureDateTime;

    @Column(name = "arrival_datetime", nullable = false)
    private LocalDateTime arrivalDateTime;

    // Геттеры и сеттеры
    public Integer getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(Integer stopNumber) {
        this.stopNumber = stopNumber;
    }

    public Long getVoyageId() {
        return voyageId;
    }

    public void setVoyageId(Long voyageId) {
        this.voyageId = voyageId;
    }

    public String getVesselId() {
        return vesselId;
    }

    public void setVesselId(String vesselId) {
        this.vesselId = vesselId;
    }

    public Voyage getVoyage() {
        return voyage;
    }

    public void setVoyage(Voyage voyage) {
        this.voyage = voyage;
    }

    public Port getDeparturePort() {
        return departurePort;
    }

    public void setDeparturePort(Port departurePort) {
        this.departurePort = departurePort;
    }

    public Port getArrivalPort() {
        return arrivalPort;
    }

    public void setArrivalPort(Port arrivalPort) {
        this.arrivalPort = arrivalPort;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(LocalDateTime departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    public LocalDateTime getArrivalDateTime() {
        return arrivalDateTime;
    }

    public void setArrivalDateTime(LocalDateTime arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }
}