package com.revature.controllers;
import at.favre.lib.crypto.bcrypt.BCrypt;
import com.revature.DAOs.UserDAO;
import com.revature.models.User;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.revature.exceptions.ResourceNotFoundException;

public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    UserDAO userDAO = new UserDAO();

    public Handler loginHandler = (ctx) -> {
        try {
            // Read the incoming JSON body and convert it into a User object
            User loginRequest = ctx.bodyAsClass(User.class);
            logger.info("Login attempt for username: {}", loginRequest.getUsername());

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
                logger.info("Successful login for user: {}", loginRequest.getUsername());
                ctx.json(foundUser);
                ctx.status(HttpStatus.OK);

            } else {
                ctx.status(HttpStatus.UNAUTHORIZED);
            }

        } catch (ResourceNotFoundException e) {
            logger.warn("User not found during login: {}", e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND);
            ctx.result(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during login: {}", e.getMessage());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ctx.result("An unexpected error occurred."); // for post
        }
    };
}