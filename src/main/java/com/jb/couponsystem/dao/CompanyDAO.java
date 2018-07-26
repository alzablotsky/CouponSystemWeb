package com.jb.couponsystem.dao;

import java.sql.SQLException;
import java.util.Collection;


import com.jb.couponsystem.entities.Company;
import com.jb.couponsystem.exceptions.IllegalUpdateException;
import com.jb.couponsystem.exceptions.UserAlreadyExistsException;
import com.jb.couponsystem.exceptions.UserNotFoundException;
import com.jb.couponsystem.exceptions.WrongPasswordException;

/**
 * 
 * This interface includes all methods implemented by 
 * CompanyDBDAO class. 
 *  
 * @author Alexander Zablotsky
 *
 */
 
public interface CompanyDAO {

	/**
	 * This method creates company, received as an object from the outside,
	 * as a parameter in the database.
	 * First it checks if a company with the same ID or with the same name
	 * already exists in the database, in which case the company cannot be created,
	 * and UserAlreadyExistsException is thrown.
	 * Otherwise - it calls CompanyRepo class to save the company in the database. 
	 * 
	 * @param c company object 
	 * @throws UserAlreadyExistsException if a company with the same ID 
	 * or with the same name already exists in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 *    
	 */
	void createCompany(Company c) throws UserAlreadyExistsException, InterruptedException, SQLException;

	/**
	 * This method removes company, received as an object from the outside,
	 * from the database.
	 * First it checks if the company with the received ID exists in the database.
	 * If it does not exist -  UserNotFoundException is thrown.
	 * Otherwise - it calls CompanyRepo class to delete the company from the database. 
	 *  
	 * @param c company object
	 * @throws UserNotFoundException if the company with the given ID
	 * does not exist in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	void removeCompany(Company c) throws UserNotFoundException, InterruptedException, SQLException;

	/**
	 * This method updates company in the database, replacing it with the company
	 * received as an object from the outside.
	 * First it checks by ID if the received company exists in the database, 
	 * if not - UserNotFoundException is thrown.
	 * Then it checks if the the received company has the same name as
	 * the company in the database, if not - IllegalUpdateException is thrown
	 * since the company name cannot be changed.
	 * Otherwise - it calls CompanyRepo class to  save the received
	 * company in the database. 
	 *  
	 * @param c company object
	 * @throws UserNotFoundException if the company with the given ID does not exist in the database
	 * @throws IllegalUpdateException if the name of the received company is different from the name of the company in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	void updateCompany(Company c) throws UserNotFoundException, IllegalUpdateException, InterruptedException, SQLException;

	/**
	 * This method returns company from the database. The company is found by the ID received from the outside.
	 * First it checks if the company with the received ID exists in the database, if not - UserNotFoundException is thrown.
	 * Otherwise - it calls CompanyRepo class to find the company with the received ID in the database and returns this company as an object.
	 * company in the database.
	 *  
	 * @param id company ID
	 * @return company object
	 * @throws UserNotFoundException if the company with the received ID does not exist in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	Company getCompany(long id) throws UserNotFoundException, InterruptedException, SQLException;

	/**
	 * This method returns all companies from the database.
	 * First it checks that there are any companies in the database. If not - UserNotFoundException is thrown.
	 * Otherwise - it calls CompanyRepo class to find all the companies in the database and returns them as a collection of objects.
	 *   
	 * @return companies as a collection of objects
	 * @throws UserNotFoundException if there is no companies in the database 
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	Collection<Company> getAllCompanies() throws UserNotFoundException, InterruptedException, SQLException;
	
	/**
	 * This method executes login for a company using name and password received from the outside.
	 * It returns boolean value which is true for successful login and false otherwise.
	 * First it checks if the company with the received name exists in the database, if not - UserNotFoundException is thrown.
	 * Then it checks if the received password is the password of the company in the database, if not - WrongPasswordExceptionis thrown.
	 * Otherwise - the method calls CompanyRepo class to find the company with the received name and sets it as the logged in company.
	 * Then it returns boolean value true.
	 * 
	 * @param companyName name of the company
	 * @param password password of the company
	 * @return boolean value true if the company's name and password are correct, false otherwise
	 * @throws UserNotFoundException if the company with the received name does not exist in the database
	 * @throws WrongPasswordException if the received password is different from the company's password in the database
	 * @throws InterruptedException if the thread was interrupted while waiting for connection
	 * @throws SQLException 
	 */
	boolean login (String companyName, String password) 
			throws UserNotFoundException, WrongPasswordException, InterruptedException, SQLException;



}

