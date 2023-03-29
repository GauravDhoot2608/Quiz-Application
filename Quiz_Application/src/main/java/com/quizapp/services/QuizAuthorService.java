package com.quizapp.services;

import com.quizapp.entities.Quiz;
import com.quizapp.entities.QuizQuestion;
import com.quizapp.payloads.QuestionRequest;
import com.quizapp.payloads.QuizRequest;

import java.util.Map;

public interface QuizAuthorService {

    String createQuiz(String userId, QuizRequest quizRequest);

    String updateQuizTitle(String userId, String quizId, String title);

    void publishQuiz(String userId, String quizId);

    Map<String, Object> getAllQuizzesForAuthor(String userId, int page, int size);

    Quiz getQuizForAuthor(String userId, String quizId);

    Map<String, Object> getAllResponsesOnThisQuiz(String userId, String quizId, int page, int size);

    void deactivateQuiz(String userId, String quizId);

    QuizQuestion addQuestionForQuiz(String userId, String quizId, QuestionRequest questionRequest);

    QuizQuestion updateQuestionForQuiz(String userId, String quizId, QuizQuestion quizQuestionEntity);

    void removeQuestionFromQuizQuestion(String userId, String quizId, String questionId);
}
