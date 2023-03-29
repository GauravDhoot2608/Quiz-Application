package com.quizapp.payloads;

import com.quizapp.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {

    @NotNull
    private QuestionType type;
    @NotBlank
    private String text;

    @Valid
    @NotNull
    @NotEmpty
    private List<QuestionOptionRequest> options;
}

