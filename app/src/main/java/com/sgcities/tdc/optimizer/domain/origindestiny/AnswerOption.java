package com.sgcities.tdc.optimizer.domain.origindestiny;

import com.google.gson.annotations.SerializedName;

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
public class AnswerOption {
    @SerializedName("id")
    private int id;

    @SerializedName("question_id")
    private int questionId;

    @SerializedName("answer")
    private String answer;

    @SerializedName("enabled")
    private int enabled;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @Override
    public String toString() {
        return this.answer;
    }
}
