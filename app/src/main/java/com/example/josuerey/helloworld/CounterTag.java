package com.example.josuerey.helloworld;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CounterTag {

    private int movementId;
    private String vehicleType;

    @Override
    public String toString() {
        return String.format("%s-%d", this.vehicleType, this.movementId);
    }
}
