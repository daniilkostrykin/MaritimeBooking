package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "voyage_stages", schema = "maritime_booking")
@IdClass(VoyageStageId.class)
public class VoyageStage {
    @Id
    @Column(name = "stop_number")
    private Integer stopNumber;

    @Id
    @Column(name = "voyage_id")
    private Long voyageId;

    @Id
    @Column(name = "vessel_id", length = 10)
    private String vesselId;

    @Column(name = "departure_port_id", nullable = false, length = 5)
    private String departurePortId;

    @Column(name = "arrival_port_id", nullable = false, length = 5)
    private String arrivalPortId;

    @Column(name = "departure_datetime", nullable = false)
    private LocalDateTime departureDateTime;

    @Column(name = "arrival_datetime", nullable = false)
    private LocalDateTime arrivalDateTime;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "voyage_id", referencedColumnName = "id", insertable = false, updatable = false),
            @JoinColumn(name = "vessel_id", referencedColumnName = "vessel_id", insertable = false, updatable = false)
    })
    private Voyage voyage;

    @ManyToOne
    @JoinColumn(name = "departure_port_id", referencedColumnName = "un_locode", insertable = false, updatable = false)
    private Port departurePort;

    @ManyToOne
    @JoinColumn(name = "arrival_port_id", referencedColumnName = "un_locode", insertable = false, updatable = false)
    private Port arrivalPort;
}