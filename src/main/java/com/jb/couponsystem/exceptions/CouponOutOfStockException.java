package com.jb.couponsystem.exceptions;

/**
 * This exception is thrown when the coupon's amount in the database is zero.
 * 
 * @author Alexander Zablotsky
 *
 */
public class CouponOutOfStockException extends RuntimeException {
	
	public CouponOutOfStockException (String message) {
		super(message);
	}

}
