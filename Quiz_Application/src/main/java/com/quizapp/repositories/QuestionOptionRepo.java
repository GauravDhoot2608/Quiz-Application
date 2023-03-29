package com.quizapp.repositories;

import com.quizapp.entities.QuestionOption;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionOptionRepo extends MongoRepository<QuestionOption , String> {

}
