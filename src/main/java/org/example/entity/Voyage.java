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
@Table(name = "voyages", schema = "maritime_booking")
@IdClass(VoyageId.class)
public class Voyage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Id
    @Column(name = "vessel_id", length = 10)
    private String vesselId;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private VoyageStatus status;

    @ManyToOne
    @JoinColumn(name = "vessel_id", insertable = false, updatable = false)
    private Vessel vessel;

    @OneToMany(mappedBy = "voyage")
    private List<VoyageStage> stages;

    @OneToMany(mappedBy = "voyage")
    private List<Ticket> tickets;

    public enum VoyageStatus {
        active, delayed, completed, cancelled, postponed, in_progress
    }
}