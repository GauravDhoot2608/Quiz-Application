package com.quizapp.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionRequest {

    @NotBlank
    private String value;
    @NotNull
    private Boolean isCorrect;
}
