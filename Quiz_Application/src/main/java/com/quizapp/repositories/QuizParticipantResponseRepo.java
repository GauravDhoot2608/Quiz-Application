package com.quizapp.repositories;

import com.quizapp.entities.QuizParticipantResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizParticipantResponseRepo extends MongoRepository<QuizParticipantResponse , String> {
}
