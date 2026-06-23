package com.revature.controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;

import com.revature.DAOs.UserDAO;
import com.revature.models.User;

import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;

public class AuthController {

    UserDAO userDAO = new UserDAO();

    public Handler loginHandler = (ctx) -> {

        // Read the incoming JSON body and convert it into a User object
        User loginRequest = ctx.bodyAsClass(User.class);

        // Use the DAO to find the actual user record in the database
        User foundUser = userDAO.getUserByUsername(loginRequest.getUsername());

        // Check if user exists, password matches, AND role is manager
        BCrypt.Result result = null;
        if (foundUser != null) {
            result = BCrypt.verifyer().verify(
                    loginRequest.getPassword().toCharArray(),
                    foundUser.getPassword()
            );
        }

        if (foundUser != null
                && result.verified
                && foundUser.getRole().equals("manager")) {

            // Strip the password before sending the response back
            foundUser.setPassword(null);

            ctx.json(foundUser);
            ctx.status(HttpStatus.OK);

        } else {
            ctx.status(HttpStatus.UNAUTHORIZED);
        }
    };
}