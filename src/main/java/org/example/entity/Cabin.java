package org.example.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "cabins", schema = "maritime_booking")
public class Cabin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vessel_id", nullable = false)
    private Vessel vessel;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CabinCategory category;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "window_view", nullable = false)
    private Boolean windowView;

    @OneToMany(mappedBy = "cabin")
    private List<Ticket> tickets;

    public enum CabinCategory {
        standard, deluxe, suite
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vessel getVessel() {
        return vessel;
    }

    public void setVessel(Vessel vessel) {
        this.vessel = vessel;
    }

    public CabinCategory getCategory() {
        return category;
    }

    public void setCategory(CabinCategory category) {
        this.category = category;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getWindowView() {
        return windowView;
    }

    public void setWindowView(Boolean windowView) {
        this.windowView = windowView;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public String getVesselId() {
        return this.vessel != null ? this.vessel.getImo() : null;
    }
}