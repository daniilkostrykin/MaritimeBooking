package org.example.entity;

import java.io.Serializable;
import java.util.Objects;

public class TicketId implements Serializable {
    private Long id;
    private Long voyageId;
    private String vesselId;
    private Long cabinId;

    public TicketId() {
    }

    public TicketId(Long id, Long voyageId, String vesselId, Long cabinId) {
        this.id = id;
        this.voyageId = voyageId;
        this.vesselId = vesselId;
        this.cabinId = cabinId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCabinId() {
        return cabinId;
    }

    public void setCabinId(Long cabinId) {
        this.cabinId = cabinId;
    }

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