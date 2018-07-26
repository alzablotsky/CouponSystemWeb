package com.jb.couponsystem.exceptions;

/**
 * This exception is thrown when the coupon does not exist in the database.
 * 
 * @author Alexander Zablotsky
 *
 */
public class CouponNotFoundException extends RuntimeException {
	
	public CouponNotFoundException(String message){
		super(message);
	}


}
