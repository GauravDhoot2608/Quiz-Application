package com.quizapp.controllers;

import com.quizapp.services.GeneralQuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/{userId}/quiz")
public class GeneralQuizController {

    @Autowired
    private GeneralQuizService generalQuizService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPublishedQuizzesForUser(@PathVariable("userId") String userId,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "3") int size) {
        return new ResponseEntity<>(generalQuizService.getAllPublishedQuizzes(userId, page, size), HttpStatus.OK);
    }
}
