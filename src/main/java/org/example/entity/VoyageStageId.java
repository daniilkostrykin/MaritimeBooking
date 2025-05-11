package org.example.entity;

import java.io.Serializable;
import java.util.Objects;

public class VoyageStageId implements Serializable {
    private Integer stopNumber;
    private Long voyageId;
    private String vesselId;

    public VoyageStageId() {
    }

    public VoyageStageId(Integer stopNumber, Long voyageId, String vesselId) {
        this.stopNumber = stopNumber;
        this.voyageId = voyageId;
        this.vesselId = vesselId;
    }

    public Integer getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(Integer stopNumber) {
        this.stopNumber = stopNumber;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        VoyageStageId that = (VoyageStageId) o;
        return Objects.equals(stopNumber, that.stopNumber) &&
                Objects.equals(voyageId, that.voyageId) &&
                Objects.equals(vesselId, that.vesselId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stopNumber, voyageId, vesselId);
    }
}