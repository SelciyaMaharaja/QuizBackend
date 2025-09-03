package com.example.quizonline.service;

import com.example.quizonline.model.User;

import java.util.List;

public interface IUserService {

    User registerUser(User user);
    List<User> getUsers();
    void deleteUser(String email);
    User getUser(String email);

}