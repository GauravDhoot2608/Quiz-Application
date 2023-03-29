package com.quizapp.repositories;

import com.quizapp.entities.Quiz;
import com.quizapp.entities.QuizParticipant;
import com.quizapp.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizParticipantRepo extends MongoRepository<QuizParticipant , String> {

    Optional<QuizParticipant> findByQuizEntityAndUserEntity(Quiz quiz, User user);

    Page<QuizParticipant> findAllByUserEntityAndQuizEntityNotIn(User user , List<Quiz> quizzes, Pageable pageable);

    Page<QuizParticipant> findAllByQuizEntity(Quiz quiz, Pageable pageable);
}
