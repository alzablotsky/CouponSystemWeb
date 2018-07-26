package com.jb.couponsystem.enums;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * This enum contains the types of the coupons which can be created in the Coupon System.
 * 
 * @author Alexander Zablotsky
 *
 */
@XmlRootElement
//@JsonFormat(shape = JsonFormat.Shape.OBJECT)

public enum CouponType implements Serializable {

	
	RESTAURANTS,
	ELECTRICITY,
	FOOD,
	HEALTH,
	SPORTS,
	CAMPING,
	TRAVELLING;
//	
//	private String value;
//	
//	
//
//	private CouponType(String value) {
//		this.value = value;
//	}
//
//	/**
//	 * @return the value
//	 */
//	public String getValue() {
//		return value;
//	}
//
//	/**
//	 * @param value the value to set
//	 */
//	public void setValue(String value) {
//		this.value = value;
//	}

	
	
	
	
	

}
