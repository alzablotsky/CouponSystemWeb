package com.jb.couponsystem.exceptions;

/**
 * This exception is thrown when the user already exists in the database.
 * 
 * @author Alexander Zablotsky
 *
 */
public class UserAlreadyExistsException extends RuntimeException {

	public UserAlreadyExistsException (String message){
		super(message);
	}
}
