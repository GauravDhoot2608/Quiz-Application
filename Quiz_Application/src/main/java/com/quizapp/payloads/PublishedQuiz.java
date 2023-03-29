package com.quizapp.payloads;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublishedQuiz {

    private String id;
    private String title;
    private List<QuestionWithoutSolution> questions;
}
