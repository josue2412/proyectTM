package com.sgcities.tdc.optimizer.application.vehicularcap;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CounterViewTag {
    private int movementId;
    private String vehicleType;

    @Override
    public String toString() {
        return String.format("%s-%d", this.vehicleType, this.movementId);
    }
}
