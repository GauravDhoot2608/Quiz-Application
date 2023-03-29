package com.quizapp.services;

import com.quizapp.entities.User;

public interface UserService {

    User createUser(String email, String password);

    User getUser(String email, String password);
}
