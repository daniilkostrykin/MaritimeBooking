package org.example.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "voyages", schema = "maritime_booking")
@IdClass(VoyageId.class)
public class Voyage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Id
    @Column(name = "vessel_id")
    private String vesselId;

    @ManyToOne
    @JoinColumn(name = "vessel_id", insertable = false, updatable = false)
    private Vessel vessel;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private VoyageStatus status;

    @OneToMany(mappedBy = "voyage", cascade = CascadeType.ALL)
    private List<VoyageStage> stages;

    @OneToMany(mappedBy = "voyage")
    private List<Ticket> tickets;

    public enum VoyageStatus {
        active, delayed, completed, cancelled, postponed, in_progress
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVesselId() {
        return vesselId;
    }

    public void setVesselId(String vesselId) {
        this.vesselId = vesselId;
    }

    public Vessel getVessel() {
        return vessel;
    }

    public void setVessel(Vessel vessel) {
        this.vessel = vessel;
    }

    public VoyageStatus getStatus() {
        return status;
    }

    public void setStatus(VoyageStatus status) {
        this.status = status;
    }

    public List<VoyageStage> getStages() {
        return stages;
    }

    public void setStages(List<VoyageStage> stages) {
        this.stages = stages;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}