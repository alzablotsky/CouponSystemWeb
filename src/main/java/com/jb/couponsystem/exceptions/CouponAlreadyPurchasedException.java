package com.jb.couponsystem.exceptions;

/**
 * This exception is thrown when the coupon has been already purchased by the customer.
 * 
 * @author Alexander Zablotsky
 *
 */
public class CouponAlreadyPurchasedException extends RuntimeException {
	
	public CouponAlreadyPurchasedException (String message){
		super(message);
	}

}
