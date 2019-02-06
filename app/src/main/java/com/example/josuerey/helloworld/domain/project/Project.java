package com.example.josuerey.helloworld.domain.project;

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
public final class Project {
    private int id;
    private int customer_id;
    private String name;
    private String begin_at;
    private String end_at;
    private int enabled;
    private String created_at;
    private String updated_at;
}
