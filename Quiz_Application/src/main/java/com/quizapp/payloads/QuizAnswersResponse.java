package com.quizapp.payloads;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAnswersResponse {

    private String quizId;
    private double totalScore;
    private double totalScorePercentage;
    private List<QuestionAnswerResponse> questionAnswers;
}
