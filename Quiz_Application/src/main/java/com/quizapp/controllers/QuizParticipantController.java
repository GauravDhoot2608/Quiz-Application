package com.quizapp.controllers;

import com.quizapp.payloads.QuizAnswersRequest;
import com.quizapp.payloads.QuizAnswersResponse;
import com.quizapp.services.QuizParticipantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/participant/{userId}/quiz")
public class QuizParticipantController {

    @Autowired
    private QuizParticipantService quizParticipantService;

    @PostMapping("/{quizId}/attempt")
    public ResponseEntity<QuizAnswersResponse> attemptQuiz(@PathVariable("userId") String userId,
                                                           @PathVariable("quizId") String quizId,
                                                           @RequestBody @Valid QuizAnswersRequest quizAnswersRequest) {
        return new ResponseEntity<>(quizParticipantService.participateInQuiz(userId, quizId, quizAnswersRequest), OK);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAttemptedQuizzes(@PathVariable("userId") String userId,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "3") int size) {
        return new ResponseEntity<>(quizParticipantService.getAllQuizzesAttemptedByUser(userId, page, size), OK);
    }
}
