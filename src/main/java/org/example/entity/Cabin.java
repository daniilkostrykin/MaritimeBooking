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
@Table(name = "cabins", schema = "maritime_booking")
@IdClass(CabinId.class)
public class Cabin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Id
    @Column(name = "vessel_id", length = 10)
    private String vesselId;

    @Column(name = "category", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CabinCategory category;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "window_view", nullable = false)
    private Boolean windowView;

    @ManyToOne
    @JoinColumn(name = "vessel_id", insertable = false, updatable = false)
    private Vessel vessel;

    @OneToMany(mappedBy = "cabin")
    private List<Ticket> tickets;

    public enum CabinCategory {
        standard, deluxe, suite
    }
}