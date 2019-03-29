package com.example.josuerey.helloworld.domain.project;

import android.arch.persistence.room.ColumnInfo;

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
    @ColumnInfo(name = "idprojects")
    private int metaProject;
    private int customer_id;
    private String name;
    private String begin_at;
    private String end_at;
    private int enabled;
    private String created_at;
    private String updated_at;
}
