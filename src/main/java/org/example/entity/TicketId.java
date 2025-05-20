package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketId implements Serializable {
    private Long id;
    private Long voyageId;
    private String vesselId;
    private Long cabinId;

}