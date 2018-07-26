package com.jb.couponsystem.dao;

import java.sql.SQLException;
import java.util.Collection;

import com.jb.couponsystem.entities.Coupon;
import com.jb.couponsystem.entities.Customer;
import com.jb.couponsystem.exceptions.CouponNotFoundException;
import com.jb.couponsystem.exceptions.IllegalUpdateException;
import com.jb.couponsystem.exceptions.UserAlreadyExistsException;
import com.jb.couponsystem.exceptions.UserNotFoundException;
import com.jb.couponsystem.exceptions.WrongPasswordException;

/**
 * 
 * This interface includes all methods implemented by CustomerDBDAO class. 
 *  
 * @author Alexander Zablotsky
 *
 */
public interface CustomerDAO {
	
	/**
	 * This method creates customer, received as an object from the outside,
	 * as a parameter in the database.
	 * First it checks if a customer with the same ID or with the same name
	 * already exists in the database, in which case the customer cannot be created,
	 * and UserAlreadyExistsException is thrown.
	 * Otherwise - it calls CustomerRepo class to save the customer in the database. 
	 * 
	 * @param c customer object 
	 * @throws UserAlreadyExistsException if a customer with the same ID 
	 * or with the same name already exists in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	void createCustomer(Customer c) throws UserAlreadyExistsException, InterruptedException, SQLException;
    
	
	/**
	 * This method removes customer, received as an object from the outside,
	 * from the database.
	 * First it checks if the customer with the received ID exists in the database.
	 * If it does not exist -  UserNotFoundException is thrown.
	 * Otherwise - it calls CustomerRepo class to delete the customer from the database. 
	 *  
	 * @param c customer object
	 * @throws UserNotFoundException if the customer with the given ID
	 * does not exist in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	void removeCustomer(Customer c) throws UserNotFoundException, InterruptedException, SQLException; 
	
	/**
	 * This method updates customer in the database, replacing it with the customer
	 * received as an object from the outside.
	 * First it checks by ID if the received customer exists in the database, 
	 * if not - UserNotFoundException is thrown.
	 * Then it checks if the the received customer has the same name as
	 * the customer in the database, if not - IllegalUpdateException is thrown
	 * since the customer name cannot be changed.
	 * Otherwise - it calls CustomerRepo class to save the received
	 * customer in the database. 
	 *  
	 * @param c customer object
	 * @throws UserNotFoundException if the customer with the given ID does not exist in the database
	 * @throws IllegalUpdateException if the name of the received customer is different from the name of the customer in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	void updateCustomer(Customer c) throws UserNotFoundException, IllegalUpdateException, InterruptedException, SQLException;
	
	/**
	 * This method returns customer from the database. The customer is found by the ID received from the outside.
	 * First it checks if the customer with the received ID exists in the database, if not - UserNotFoundException is thrown.
	 * Otherwise - it calls CustomerRepo class to find the customer with the received ID in the database and returns this customer as an object.
	 *  
	 * @param id customer ID
	 * @return customer object
	 * @throws UserNotFoundException if the customer with the received ID does not exist in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	Customer getCustomer(long id) throws UserNotFoundException, InterruptedException, SQLException;

	/**
	 * This method returns all customers from the database.
	 * First it checks that there are any customers in the database. If not - UserNotFoundException is thrown.
	 * Otherwise - it calls CustomerRepo class to find all the customers in the database and returns them as a collection of objects.
	 *   
	 * @return customers as a collection of objects
	 * @throws UserNotFoundException if there is no customers in the database 
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	Collection <Customer> getAllCustomers() throws UserNotFoundException, InterruptedException, SQLException;
	
	
	/**
	 * 
	 * This method returns all coupons from the database purchased by the customer logged in the coupon system.
	 * It defines all coupons of the logged in customer as a collection of objects.
	 * First it checks if the customer has any coupons. If not - UserNotFoundException is thrown.
	 * Otherwise - it returns the collection of customer's coupons.
	 *  
	 * @return logged in customer's coupons as a collection of objects
	 * @throws CouponNotFoundException if the customer has no coupons
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	Collection <Coupon> getCoupons() throws CouponNotFoundException, InterruptedException, SQLException;


	/**
	 * This method executes login for a customer using name and password received from the outside.
	 * It returns boolean value which is true for successful login and false otherwise.
	 * First it checks if the customer with the received name exists in the database, if not - UserNotFoundException is thrown.
	 * Then it checks if the received password is the password of the customer in the database, if not - WrongPasswordExceptionis thrown.
	 * Otherwise - the method calls CustomerRepo class to find the customer with the received name and sets him as the logged in customer.
	 * Then it returns boolean value true.
	 * 
	 * @param customerName name of the customer
	 * @param password password of the customer
	 * @return boolean value true if the customer's name and password are correct, false otherwise
	 * @throws UserNotFoundException if the customer with the received name does not exist in the database
	 * @throws WrongPasswordException if the received password is different from the customer's password in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */	

	boolean login (String customerName, String password) 
			throws UserNotFoundException, WrongPasswordException, InterruptedException, SQLException;
	
	
	
	


}
