package com.revature.exceptions;

/*
 * ResourceNotFoundException
 *
 * Thrown when a requested resource (user, expense, approval)
 * cannot be found in the database.
 *
 * RuntimeException is unchecked so you don't need to say "throws" every
 * time you use it
 *
 * Used by: DAOs when a query returns no results
 * Caught by: Controllers, which translate it into an HTTP 404
 */

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}