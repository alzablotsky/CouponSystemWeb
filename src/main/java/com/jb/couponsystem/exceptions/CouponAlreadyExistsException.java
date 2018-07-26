package com.jb.couponsystem.exceptions;

/**
 * This exception is thrown when the coupon already exists in the database.
 * 
 * @author Alexander Zablotsky
 *
 */

public class CouponAlreadyExistsException extends Exception {

	public CouponAlreadyExistsException (String message){
		super(message);
	}
}
