package com.jb.couponsystem.exceptions;

/**
 * This exception is thrown when the entered password does not fit the user's password in the database.
 * 
 * @author Alexander Zablotsky
 *
 */
public class WrongPasswordException extends RuntimeException  {
	
	public WrongPasswordException (String message){
	super(message);
	}
}
