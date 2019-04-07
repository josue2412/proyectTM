package com.example.josuerey.helloworld.infrastructure.network;

import com.example.josuerey.helloworld.domain.capturist.Capturist;
import com.example.josuerey.helloworld.domain.movement.Movement;
import com.example.josuerey.helloworld.domain.project.Project;

import java.util.List;

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
    private int enabled;
    private String created_at;
    private String updated_at;
    private Capturist capturist;
    private Project project;
    private List<Movement> movements;

}