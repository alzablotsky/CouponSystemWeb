package com.jb.couponsystem.facades;

import java.sql.SQLException;

import com.jb.couponsystem.exceptions.UserNotFoundException;
import com.jb.couponsystem.exceptions.WrongPasswordException;

/**
 * This interface includes the login method implemented by
 * AdminFacade, CompanyFacade and CustomerFacade classes. 
 *  
 * @author Alexander Zablotsky
 *
 */
 
public interface CouponClientFacade {
	/**
	 * This method executes the login in the coupon system for the user's facade class,
	 * with the username and password sent as parameters from the outside.
	 * In case of AdminFacade it checks whether the username and password are correct,
	 * If they are correct, the facade object is return, otherwise null is returned.
	 * In case of CompanyFacade and CustomerFacade, it calls the user's DBDAO to execute its login method.
	 * If the DBDAO returns true, the facade object is return, otherwise null is returned.
	 * All the exceptions thrown by the DBDAO login method are thrown by this method. 
	 * 
	 * @param name username
	 * @param password user's password
	 * @return the user's facade object if the login succeeded, null otherwise.
	 * @throws WrongPasswordException if the password is incorrect for the given username
	 * @throws UserNotFoundException if the username is incorrect
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	CouponClientFacade login(String name, String password) throws WrongPasswordException, UserNotFoundException, InterruptedException, SQLException;

}
