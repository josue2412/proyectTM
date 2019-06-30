package com.example.josuerey.helloworld.domain.origindestiny;

import com.google.gson.annotations.SerializedName;

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
public class Question {
    @SerializedName("id")
    private int id;

    @SerializedName("questionary_id")
    private int questionaryId;

    @SerializedName("description")
    private String description;

    @SerializedName("name")
    private String name;

    @SerializedName("enabled")
    private int enabled;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("answers")
    private List<AnswerOption> answers;
}
