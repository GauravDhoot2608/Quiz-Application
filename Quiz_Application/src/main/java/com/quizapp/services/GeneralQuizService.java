package com.quizapp.services;

import java.util.Map;

public interface GeneralQuizService {

    Map<String, Object> getAllPublishedQuizzes(String userId, int page, int size);
}

