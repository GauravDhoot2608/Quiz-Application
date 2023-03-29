package com.quizapp.payloads;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionAnswerResponse {

    private String questionId;
    private double score;
    private double scorePercentage;
    private List<String> optionsSelected;
}
