package com.quizapp.repositories;

import com.quizapp.entities.QuizQuestion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface QuizQuestionRepo extends MongoRepository<QuizQuestion , String> {


}
