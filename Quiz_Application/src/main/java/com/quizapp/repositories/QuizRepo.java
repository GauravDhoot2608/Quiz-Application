package com.quizapp.repositories;

import com.quizapp.entities.Quiz;
import com.quizapp.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepo extends MongoRepository<Quiz , String> {

    Page<Quiz> findAllByAuthorAndActiveTrue(User author, Pageable pageable);

    Page<Quiz> findAllByPublishedTrueAndActiveTrueAndAuthorNot(User user, Pageable pageable);

    List<Quiz> findAllByActiveFalse();

}
