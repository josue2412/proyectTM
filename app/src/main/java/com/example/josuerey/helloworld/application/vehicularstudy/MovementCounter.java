package com.example.josuerey.helloworld.application.vehicularstudy;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovementCounter {

    @Builder.Default
    private final Map<String, CounterStats> counterStatusPerVehicle = new HashMap<>();

    /**
     * Calculates the total count of this movement, it includes all the available vehicles.
     * @return
     */
    public int getTotal() {
        int total = 0;
        for (String vehicle : counterStatusPerVehicle.keySet()) {
            total = total + counterStatusPerVehicle.get(vehicle).getTotalCount().get();
        }
        return total;
    }
}
