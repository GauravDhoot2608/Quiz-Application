package com.quizapp.services.impl;

import com.quizapp.entities.QuestionOption;
import com.quizapp.entities.Quiz;
import com.quizapp.entities.QuizQuestion;
import com.quizapp.entities.User;
import com.quizapp.exceptions.ResourceNotFoundException;
import com.quizapp.payloads.PublishedQuiz;
import com.quizapp.payloads.QuestionWithoutSolution;
import com.quizapp.repositories.QuizRepo;
import com.quizapp.repositories.UserRepo;
import com.quizapp.services.GeneralQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeneralQuizServiceImpl implements GeneralQuizService {
    @Autowired
    private QuizRepo quizRepository;
    @Autowired
    private UserRepo userRepository;

    @Override
    public Map<String, Object> getAllPublishedQuizzes(String userId, int page, int size) {

        User userEntity = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user","user_id", userId));
        Pageable paging = PageRequest.of(page, size);
        Page<Quiz> quizEntities = quizRepository.findAllByPublishedTrueAndActiveTrueAndAuthorNot(userEntity, paging);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("currentPage", quizEntities.getNumber());
        responseMap.put("totalItems", quizEntities.getTotalElements());
        responseMap.put("totalPages", quizEntities.getTotalPages());
        List<PublishedQuiz> publishedQuizList = new ArrayList<>();
        for (Quiz quizEntity : quizEntities.getContent()) {
            List<QuestionWithoutSolution> questionWithoutSolutionList = new ArrayList<>();
            for (QuizQuestion quizQuestionEntity : quizEntity.getQuizQuestions()) {
                Map<String, String> optionsMap =
                        quizQuestionEntity.getQuestionOptionEntities().stream().collect(
                                Collectors.toMap(QuestionOption:: getId, QuestionOption ::getValue));
                questionWithoutSolutionList.add(QuestionWithoutSolution.builder().questionId(quizQuestionEntity.getId())
                        .questionText(
                                quizQuestionEntity.getQuestionText())
                        .questionType(
                                quizQuestionEntity.getQuestionType())
                        .optionsIdAndText(optionsMap).build());
            }
            publishedQuizList.add(PublishedQuiz.builder().title(quizEntity.getTitle()).id(quizEntity.getId())
                    .questions(questionWithoutSolutionList).build());
        }
        responseMap.put("quizzes", publishedQuizList);
        return responseMap;
    }
}
