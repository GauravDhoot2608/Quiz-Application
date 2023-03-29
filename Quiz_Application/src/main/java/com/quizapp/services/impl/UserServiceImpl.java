package com.quizapp.services.impl;

import com.quizapp.entities.User;
import com.quizapp.exceptions.ResourceNotFoundException;
import com.quizapp.repositories.UserRepo;
import com.quizapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;
    @Override
    public User createUser(String email, String password) {
        User user = new User(email,password);
        User savedUser = this.userRepo.save(user);
        return savedUser;
    }

    @Override
    public User getUser(String email, String password) {
        User user = this.userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User Email" , "email" , email));
        return user;
    }
}
