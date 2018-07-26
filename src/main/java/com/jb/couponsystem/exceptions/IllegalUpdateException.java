package com.jb.couponsystem.exceptions;

/**
 * This exception is thrown when there is an attempt to change the object's attribute which is not allowed to be changed.
 * 
 * @author Alexander Zablotsky
 *
 */
public class IllegalUpdateException extends RuntimeException {
	public IllegalUpdateException (String message){
		super(message);
	}

}
