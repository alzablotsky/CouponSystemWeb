package com.jb.couponsystem.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import com.jb.couponsystem.entities.Coupon;
import com.jb.couponsystem.enums.CouponType;
import com.jb.couponsystem.exceptions.CouponAlreadyExistsException;
import com.jb.couponsystem.exceptions.CouponAlreadyPurchasedException;
import com.jb.couponsystem.exceptions.CouponExpiredException;
import com.jb.couponsystem.exceptions.CouponNotFoundException;
import com.jb.couponsystem.exceptions.CouponOutOfStockException;
import com.jb.couponsystem.exceptions.IllegalUpdateException;
import com.jb.couponsystem.exceptions.UserAlreadyExistsException;
import com.jb.couponsystem.exceptions.UserNotFoundException;

/**
 * 
 * This interface includes all methods implemented by 
 * CouponDBDAO class. 
 *  
 * @author Alexander Zablotsky
 *
 */
public interface CouponDAO {
	
	
	/**
	 * This method creates coupon, received as an object from the outside,
	 * as a parameter in the database.
	 * First it checks if a coupon with the same ID or with the same name
	 * already exists in the database, in which case the coupon cannot be created,
	 * and CouponAlreadyExistsException is thrown.
	 * Otherwise - it calls CouponRepo class to save the coupon in the database. 
	 * 
	 * @param c coupon object 
	 * @throws CouponAlreadyExistsException if a coupon with the same ID 
	 * or with the same name already exists in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	void createCoupon(Coupon c) throws CouponAlreadyExistsException, InterruptedException, SQLException;

	/**
	 * This method removes coupon of the logged in company, received as an object from the outside,
	 * from the database.
	 * First it checks if the coupon with the received ID of the logged in company exists in the database.
	 * If it does not exist -  CouponNotFoundException is thrown.
	 * Otherwise - it calls CouponRepo class to delete the coupon from the database. 
	 *  
	 * @param c coupon object
	 * @throws CouponNotFoundException if the coupon with the given ID and with the logged in company ID
	 * does not exist in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */	
	void removeCoupon(Coupon c) throws CouponNotFoundException, InterruptedException, SQLException;
	
	
	/**
	 * This method updates coupon of the logged in company in the database, replacing it with the coupon
	 * received as an object from the outside.
	 * First it checks by ID and company name if the received coupon exists in the database, 
	 * if not - CouponNotFoundException is thrown.
	 * Then it checks if there is a difference between any attribute, excluding PRICE and END DATE, 
	 * of the received coupon and the coupon in the database. If there is a difference - IllegalUpdateException is thrown
	 * since only PRICE and END DATE of the coupon may be changed.
	 * Otherwise - it calls CouponRepo class to save the received coupon in the database. 
	 *  
	 * @param c coupon object
	 * @throws CouponNotFoundException if the coupon with the given title and logged in company ID does not exist in the database
	 * @throws IllegalUpdateException if one of the attributes, excluding PRICE and END DATE, of the received coupon is different
	 * from the corresponding attribute of the coupon in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */	
	void updateCoupon(Coupon c) throws  CouponNotFoundException, IllegalUpdateException, InterruptedException, SQLException;

	
	/**
	 * This method returns coupon from the database belonging to the logged in company . The coupon is found by the logged in company ID
	 * and coupon ID which are received from the outside.
	 * First it checks if the coupon with the received ID and the logged in company ID exists in the database, 
	 * if not - CouponNotFoundException is thrown. Otherwise - it calls CouponRepo class to find the coupon with the received ID
	 * and the logged in company ID in the database and returns this coupon as an object.
	 *  
	 * @param id coupon ID
	 * @return coupon object
	 * @throws CouponNotFoundException if the coupon with the received ID and the logged in company ID does not exist in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	Coupon getCompanyCoupon(long id) throws CouponNotFoundException, InterruptedException, SQLException;

	/**
	 * This method returns all the coupons from the database belonging to the logged in company .
	 * It calls CouponRepo class to find all coupons of the logged in company by its ID and saves them as a collection of objects.
	 * Then it checks if this collection is empty, if yes - CouponNotFoundException is thrown.
	 * Otherwise - the collection is returned.
	 *   
	 * @return logged in company's coupons as a collection of objects
	 * @throws CouponNotFoundException if there is no coupons belonging to the logged in company in the database 
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	Collection <Coupon> getAllCompanyCoupons() throws CouponNotFoundException, InterruptedException, SQLException;

	/**
	 * This method returns all the coupons from the database belonging to the logged in company of the given coupon type. 
	 * The coupon type is received as a parameter from the outside. 
	 * The method calls CouponRepo class to find all coupons by the given type and the logged in company ID and saves them as a collection of objects.
	 * Then it checks if this collection is empty, if yes - CouponNotFoundException is thrown.
	 * Otherwise - the collection is returned.
	 *   
	 * @param type coupon type
	 * @return logged in company's coupons as a collection of objects
	 * @throws CouponNotFoundException if there is no coupons belonging to the logged in company of the given type in the database 
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	Collection <Coupon> getCompanyCouponsByType(CouponType type)throws CouponNotFoundException, InterruptedException, SQLException;
	
	/**
	 * This method returns all the coupons from the database belonging to the logged in company the price of which is lower than the given price. 
	 * The price is received as a parameter from the outside. 
	 * The method calls CouponRepo class to find all coupons by the given price and the logged in company ID and saves them as a collection of objects.
	 * Then it checks if this collection is empty, if yes - CouponNotFoundException is thrown.
	 * Otherwise - the collection is returned.
	 *   
	 * @param price coupon's price
	 * @return logged in company's coupons under the given price as a collection of objects
	 * @throws CouponNotFoundException if there is no coupons belonging to the logged in company the price of which is lower than the given price
	 * in the database 
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */	
	Collection<Coupon> getCompanyCouponsByPrice(double price) throws CouponNotFoundException, InterruptedException, SQLException;

	/**
	 * This method returns all the coupons from the database belonging to the logged in company the end date of which is earlier than the given end date. 
	 * The end date is received as a parameter from the outside. 
	 * The method calls CouponRepo class to find all coupons by the given end date and the logged in company ID and saves them as a collection of objects.
	 * Then it checks if this collection is empty, if yes - CouponNotFoundException is thrown.
	 * Otherwise - the collection is returned.
	 *   
	 * @param endDate coupon's end date
	 * @return logged in company's coupons the end date of which is later than the given end date as a collection of objects
	 * @throws CouponNotFoundException if there is no coupons belonging to the logged in company with end date earlier than the given end date in the database 
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */	
	Collection<Coupon> getCompanyCouponsByEndDate(Date endDate) throws CouponNotFoundException, InterruptedException, SQLException;

	/**
	 * This method returns coupon from the database by coupon ID received from the outside.
	 * First it checks if the coupon with the received ID exists in the database, 
	 * if not - CouponNotFoundException is thrown. Otherwise - it calls CouponRepo class to find
	 *  the coupon with the received ID in the database and returns this coupon as an object.
	 *  
	 * @param id coupon ID
	 * @return coupon object
	 * @throws CouponNotFoundException if the coupon with the received ID does not exist in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	Coupon getCoupon(long id) throws CouponNotFoundException, InterruptedException, SQLException;
	
	
	/**
	 * This method returns all the coupons from the database.
	 * It calls CouponRepo class to find all coupons and saves them as a collection of objects.
	 * Then it checks if this collection is empty, if yes - CouponNotFoundException is thrown.
	 * Otherwise - the collection is returned.
	 *   
	 * @return coupons as a collection of objects
	 * @throws CouponNotFoundException if there is no coupons in the database 
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	Collection<Coupon> getAllCoupons() throws CouponNotFoundException, InterruptedException, SQLException;
	
	
	/**
	 * This method allows the logged in customer to purchase coupon given as an object from the outside.
	 * First it checks if the given coupon exists in the database, if not - CouponNotFoundException is thrown. 
	 * Then it checks whether the given coupon was already purchased by the logged in customer,
	 * in which case CouponAlreadyPurchasedException is thrown.
	 * Then it checks whether the amount of the coupon is zero, in which case CouponOutOfStockException is thrown.
	 * Then it checks whether the end date of the coupon is before the present date, in which case CouponExpiredException is thrown.
	 * Otherwise, it adds the logged in customer to the list of customers  that purchased the coupon,
	 * Then it updated the amount of coupon in the stock by decreasing it by one,
	 * Finally it saves the coupon details in the database.	 
	 * 
	 * @param c coupon object
	 * @throws CouponNotFoundException if the coupon does not exist in the database
	 * @throws CouponAlreadyPurchasedException if the coupon was already purchased by the logged in customer
	 * @throws CouponOutOfStockException if the coupon amount is zero
	 * @throws CouponExpiredException if the coupon end date is before the present date
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	void purchaseCoupon(Coupon c) throws CouponNotFoundException, CouponAlreadyPurchasedException,
			CouponOutOfStockException, CouponExpiredException, InterruptedException, SQLException;
	
	
	/**
	 * This method returns from the database all the coupons of the given type purchased by the logged in customer. 
	 * The coupon type is received as a parameter from the outside. 
	 * The method calls CouponRepo class to find all coupons of the given type and
	 * customer ID and saves them as a collection of objects.
	 * Then it checks if this collection is empty, if yes - CouponNotFoundException is thrown.
	 * Otherwise - the collection is returned.
	 * 
	 * @param type coupon type
	 * @return collection of coupon objects
	 * @throws CouponNotFoundException if the customer's coupons of the given type do not exist in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	Collection<Coupon> getAllPurchasedCouponsByType(CouponType type) throws CouponNotFoundException, InterruptedException, SQLException;

	
	/**
	 * This method returns from the database all the coupons purchased by the logged in customer
	 * the price of which is lower than the given price. This price is received as a parameter from the outside. 
	 * The method calls CouponRepo class to find all coupons under the given price with the given customer ID
	 * and saves them as a collection of objects.
	 * Then it checks if this collection is empty, if yes - CouponNotFoundException is thrown.
	 * Otherwise - the collection is returned.
	 * 
	 * @param price coupon price
	 * @return collection of coupon objects
	 * @throws CouponNotFoundException if the customer's coupons under the given price do not exist in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	Collection<Coupon> getAllPurchasedCouponsByPrice(double price) throws CouponNotFoundException, InterruptedException, SQLException;

	/**
	 * This method returns from the database all the coupons purchased by the logged in customer
	 * The method calls CouponRepo class to find all coupons with the given customer ID
	 * and saves them as a collection of objects.
	 * Then it checks if this collection is empty, if yes - CouponNotFoundException is thrown.
	 * Otherwise - the collection is returned.
	 * 
	 * @return collection of coupon objects
	 * @throws CouponNotFoundException if the customer's coupons do not exist in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	Collection<Coupon> getAllPurchasedCoupons() throws CouponNotFoundException, InterruptedException, SQLException;

	/**
	 * This method returns from the database all the coupons that were not purchased by the logged in customer
	 * The method calls CouponRepo class to find all coupons which do not have the given customer ID
	 * and saves them as a collection of objects.
	 * Then it checks if this collection is empty, if yes - CouponNotFoundException is thrown.
	 * Otherwise - the collection is returned.
	 * 
	 * @return collection of coupon objects
	 * @throws CouponNotFoundException if all coupons that exist in the database were purchased by the customer
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	Collection<Coupon> getAllNonPurchasedCoupons() throws CouponNotFoundException, InterruptedException, SQLException;

	/**
	 * This method removes from the database all coupons of the company, by company ID received as a parameter from the outside,
	 * First it checks if any coupons of the company exist in the database.
	 * If no coupons exist -  CouponNotFoundException is thrown.
	 * Otherwise - it calls CouponRepo class to delete the coupons from the database. 
	 *  
	 * @param id company ID
	 * @throws CouponNotFoundException if no coupons of the given company exist in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */	
	void removeCompanyCoupons(long id) throws InterruptedException, SQLException;

	
	

}
