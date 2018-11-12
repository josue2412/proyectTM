package com.example.josuerey.helloworld.network;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssignmentResponse {
    private int id;
    private int capturist_id;
    private int project_id;
    private String begin_at;
    private int duration_in_hours;
    private String street_from;
    private String street_to;
    private String street_from_direction;
    private String street_to_direction;
    private String street_from_code;
    private String street_to_code;
    private String movement;
    private int movement_code;
    private int enabled;
    private String created_at;
    private String updated_at;
    private Capturist capturist;
    private Project project;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static final class Capturist {
        private int id;
        private String name;
        private String telephone_number;
        private String birthdate;
        private int enabled;
        private String created_at;
        private String updated_at;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static final class Project {
        private int id;
        private int customer_id;
        private String name;
        private String begin_at;
        private String end_at;
        private int enabled;
        private String created_at;
        private String updated_at;
    }
}