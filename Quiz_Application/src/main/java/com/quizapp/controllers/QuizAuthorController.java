package com.quizapp.controllers;

import com.quizapp.entities.Quiz;
import com.quizapp.entities.QuizQuestion;
import com.quizapp.payloads.QuestionRequest;
import com.quizapp.payloads.QuizRequest;
import com.quizapp.services.QuizAuthorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/author/{userId}/quiz")
public class QuizAuthorController {

    @Autowired
    private QuizAuthorService quizAuthorService;

    @PostMapping
    public ResponseEntity<String> createQuiz(@PathVariable("userId") String userId,
                                             @Valid @RequestBody QuizRequest quizRequest) {
        String id = quizAuthorService.createQuiz(userId, quizRequest);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllQuizzes(@PathVariable("userId") String userId,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "3") int size) {
        return new ResponseEntity<>(quizAuthorService.getAllQuizzesForAuthor(userId, page, size), HttpStatus.OK);
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable("userId") String userId,
                                            @PathVariable("quizId") String quizId) {
        return new ResponseEntity<>(quizAuthorService.getQuizForAuthor(userId, quizId), OK);
    }

    @PatchMapping("/{quizId}/publish")
    public ResponseEntity<Void> publishQuiz(@PathVariable("userId") String userId,
                                            @PathVariable("quizId") String quizId) {
        quizAuthorService.publishQuiz(userId, quizId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{quizId}")
    public ResponseEntity<String> updateQuiz(@PathVariable("userId") String userId,
                                             @PathVariable("quizId") String quizId,
                                             @RequestParam("title") String title) {
        return new ResponseEntity<>(quizAuthorService.updateQuizTitle(userId, quizId, title), OK);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable("userId") String userId,
                                           @PathVariable("quizId") String quizId) {
        quizAuthorService.deactivateQuiz(userId, quizId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{quizId}/responses")
    public ResponseEntity<Map<String, Object>> getResponsesOnQuiz(@PathVariable("userId") String userId,
                                                                  @PathVariable("quizId") String quizId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "3") int size) {
        return new ResponseEntity<>(quizAuthorService.getAllResponsesOnThisQuiz(userId, quizId, page, size), OK);
    }

    @PostMapping("/{quizId}/question")
    public ResponseEntity<QuizQuestion> addQuizQuestion(@PathVariable("userId") String userId,
                                                        @PathVariable("quizId") String quizId,
                                                        @RequestBody @Valid QuestionRequest questionRequest) {
        return new ResponseEntity<>(quizAuthorService.addQuestionForQuiz(userId, quizId, questionRequest), OK);
    }

    @PatchMapping("/{quizId}/question")
    public ResponseEntity<QuizQuestion> updateQuestion(@PathVariable("userId") String userId,
                                                             @PathVariable("quizId") String quizId,
                                                             @RequestBody QuizQuestion quizQuestionEntity) {
        return new ResponseEntity<>(quizAuthorService.updateQuestionForQuiz(userId, quizId, quizQuestionEntity), OK);
    }

    @DeleteMapping("/{quizId}/question/{questionId}")
    public ResponseEntity<String> deleteQuestion(@PathVariable("userId") String userId,
                                                 @PathVariable("quizId") String quizId,
                                                 @PathVariable("questionId") String questionId) {
        quizAuthorService.removeQuestionFromQuizQuestion(userId, quizId, questionId);
        return ResponseEntity.ok().build();
    }


}

