package com.jb.couponsystem.exceptions;

/**
 * This exception is thrown when the coupon has expired.
 * 
 * @author Alexander Zablotsky
 *
 */
public class CouponExpiredException extends RuntimeException {
	
	public CouponExpiredException (String message){
		super(message);
	}

}
