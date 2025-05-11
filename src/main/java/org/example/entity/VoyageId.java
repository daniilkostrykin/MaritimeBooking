package org.example.entity;

import java.io.Serializable;
import java.util.Objects;

public class VoyageId implements Serializable {
    private Long id;
    private String vesselId;

    public VoyageId() {
    }

    public VoyageId(Long id, String vesselId) {
        this.id = id;
        this.vesselId = vesselId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        VoyageId voyageId = (VoyageId) o;
        return Objects.equals(id, voyageId.id) &&
                Objects.equals(vesselId, voyageId.vesselId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vesselId);
    }
}