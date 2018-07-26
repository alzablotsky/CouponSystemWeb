package com.jb.couponsystem.exceptions;

/**
 * This exception is thrown when the user does not exist in the database.
 * 
 * @author Alexander Zablotsky
 *
 */
public class UserNotFoundException extends RuntimeException {
	
	public UserNotFoundException(String message){
		super(message);
	}

}
