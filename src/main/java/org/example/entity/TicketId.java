package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketId implements Serializable {
    private Long id;
    private Long voyageId;
    private String vesselId;
    private Long cabinId;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TicketId ticketId = (TicketId) o;
        return Objects.equals(id, ticketId.id) &&
                Objects.equals(voyageId, ticketId.voyageId) &&
                Objects.equals(vesselId, ticketId.vesselId) &&
                Objects.equals(cabinId, ticketId.cabinId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, voyageId, vesselId, cabinId);
    }
}