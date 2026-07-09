package com.revature.DAOs;

import com.revature.models.User;


//here we will lay out functionalities that UserDAO will implement
public interface UserDAOInterface {

    /*
    As a manager, I want to log in securely so that I can access
    and manage employee expense reports.
     */
    User getUserByUsername(String username);

    // to verify a user exists by their id
    User getUserById(int userId);
}