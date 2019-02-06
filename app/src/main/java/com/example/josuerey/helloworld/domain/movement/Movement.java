package com.example.josuerey.helloworld.domain.movement;

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
public final class Movement {
    private int id;
    private int assignment_id;
    private String street_from;
    private String street_to;
    private String street_from_direction;
    private String street_to_direction;
    private String street_from_code;
    private String street_to_code;
    private String movement_name;
    private int movement_code;
    private int enabled;
    private String created_at;
    private String updated_at;
}
