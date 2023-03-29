package com.quizapp.services;

import com.quizapp.payloads.QuizAnswersRequest;
import com.quizapp.payloads.QuizAnswersResponse;

import java.util.Map;

public interface QuizParticipantService {

    QuizAnswersResponse participateInQuiz(String userId, String quizId, QuizAnswersRequest quizAnswersRequest);

    Map<String, Object> getAllQuizzesAttemptedByUser(String userId, int page, int size);
}
