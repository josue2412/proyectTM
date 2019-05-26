package com.example.josuerey.helloworld.domain.capturist;

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
public final class Capturist {
    private int id;
    private int employer_id;
    private String name;
    private String telephone_number;
    private String birthday;
    private int enabled;
    private String created_at;
    private String updated_at;
}
