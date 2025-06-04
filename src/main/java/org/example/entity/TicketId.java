package org.example.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.io.Serializable;
import jakarta.persistence.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TicketId implements Serializable {
    @Column(name = "id")
    private Long id;

    @Column(name = "voyage_id")
    private Long voyageId;

    @Column(name = "vessel_id", length = 10)
    private String vesselId;

    @Column(name = "cabin_id")
    private Long cabinId;
}