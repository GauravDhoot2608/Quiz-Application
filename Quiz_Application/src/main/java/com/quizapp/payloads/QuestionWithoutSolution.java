package com.quizapp.payloads;

import com.quizapp.enums.QuestionType;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionWithoutSolution {

    private String questionId;
    private String questionText;
    private QuestionType questionType;
    private Map<String, String> optionsIdAndText;
}

