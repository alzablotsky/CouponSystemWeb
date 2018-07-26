package com.jb.couponsystem.dbdao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.jb.couponsystem.connection.ConnectionPool;
import com.jb.couponsystem.connection.DbConnection;
import com.jb.couponsystem.dao.CustomerDAO;
import com.jb.couponsystem.entities.Company;
import com.jb.couponsystem.entities.Coupon;
import com.jb.couponsystem.entities.Customer;
import com.jb.couponsystem.exceptions.CouponAlreadyExistsException;
import com.jb.couponsystem.exceptions.CouponAlreadyPurchasedException;
import com.jb.couponsystem.exceptions.CouponExpiredException;
import com.jb.couponsystem.exceptions.CouponNotFoundException;
import com.jb.couponsystem.exceptions.CouponOutOfStockException;
import com.jb.couponsystem.exceptions.IllegalUpdateException;
import com.jb.couponsystem.exceptions.UserAlreadyExistsException;
import com.jb.couponsystem.exceptions.UserNotFoundException;
import com.jb.couponsystem.exceptions.WrongPasswordException;
import com.jb.couponsystem.repo.CustomerRepo;

/**
 * This class contains methods that call CustomerRepo interface in order
 * to perform operations on customer objects in the database.
  * Since the number of connections to the system is limited, each method begins with sending  
 * a request for connection from the pool of connections.
 * Also, at the end of each method the connection is returned back to the connection pool.
 *   
 * @author Alexander Zablotsky
 *
 */
@Component
public class CustomerDBDAO implements CustomerDAO {

	//Attributes
	@Autowired
	CustomerRepo customerRepo;

	private Customer loginCustomer;

	//Getters and setters - for login customer

	/**
	 * 
	 * @return the loginCustomer
	 */
	public Customer getLoginCustomer() {
		return loginCustomer;
	}


	/**
	 * 
	 * @param loginCustomer the loginCustomer to set
	 */
	public void setLoginCustomer(Customer loginCustomer) {
		this.loginCustomer = loginCustomer;
	}

//Methods
	
	/*
	 * 
	 * Create customer
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CustomerDAO#createCustomer(com.jb.couponsystem.entities.Customer)
	 */
	@Override
	public void createCustomer(Customer c) throws UserAlreadyExistsException, InterruptedException, SQLException {
		
		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();
		
		//If a customer with this ID already exists - return connection and throw exception
		if (customerRepo.existsById(c.getId())) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new UserAlreadyExistsException ("Cannot create new customer. Customer id=" + c.getId() + " already exists.");
		}

		//If a customer with this name already exists - return connection and throw exception
		if (customerRepo.existsByCustomerName(c.getCustomerName())){
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new UserAlreadyExistsException ("Cannot create new customer. Customer name " + c.getCustomerName() + " already exists.");
		}
		//Otherwise - create a customer and return connection
		else {
			customerRepo.save(c);
			ConnectionPool.getInstance().returnConnection(dbConnection);
			}
	}


	/*
	 * 
	 * Remove customer
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CustomerDAO#removeCustomer(com.jb.couponsystem.entities.Customer)
	 */
	@Override
	public void removeCustomer(Customer c) throws UserNotFoundException, InterruptedException, SQLException {

		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();
		

		//If a customer with this ID does not exist - return connection and throw exception
		if  (!customerRepo.existsById(c.getId())) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new UserNotFoundException ("Cannot remove customer. Customer " + c 
					+ " does not exist.");
		}
		//Otherwise - remove customer and return connection
		else {
			customerRepo.removeCustomerById(c.getId());
			ConnectionPool.getInstance().returnConnection(dbConnection);
		}

	}

	/*
	 * 
	 * Update customer
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CustomerDAO#updateCustomer(com.jb.couponsystem.entities.Customer)
	 */
	@Override
	public void updateCustomer(Customer c) throws UserNotFoundException, IllegalUpdateException, InterruptedException, SQLException {

		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();
				
		//Compare to the customer in DB
		Customer customerInDb = customerRepo.findCustomerById(c.getId());

		//If a customer with this ID does not exist in the DB - return connection and throw exception 
		if  (customerInDb==null) { 
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new UserNotFoundException ("Cannot update customer. Customer " + c 
					+ " does not exist.");
		}

		//If a customer name was changed - return connection and throw exception		
		else if (!customerInDb.getCustomerName().equals(c.getCustomerName())) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new IllegalUpdateException ("Cannot update customer "
					+ customerInDb.getCustomerName()
					+ ". Customer name cannot be changed."); 
		}

		//Otherwise - update customer and return connection
		else {
			customerRepo.save(c);
			ConnectionPool.getInstance().returnConnection(dbConnection);
		}
	}


	/*
	 * Get customer by id
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CustomerDAO#getCustomer(long)
	 */
	@Cacheable(value = "customer", key = "#id")
	@Override
	public Customer getCustomer(long id) throws UserNotFoundException, InterruptedException, SQLException {

		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();
				
		//If the customer with this ID does not exist - return connection and throw exception
		if  (!customerRepo.existsById(id))  {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new UserNotFoundException ("Cannot display customer details. "
					+ "Customer id=" + id + " does not exist.");
		}
		//Otherwise - return connection and return customer
		else {
			Customer c= customerRepo.findCustomerById(id);
			ConnectionPool.getInstance().returnConnection(dbConnection);			
			return c;
		}
	}

	/*
	 * Get all customers
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CustomerDAO#getAllCustomers()
	 */
	@Override
	public Collection<Customer> getAllCustomers() throws UserNotFoundException, InterruptedException, SQLException {
		
		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();
		
		Collection<Customer> cust = (Collection<Customer>) customerRepo.findAll();

		//If no customers exist - return connection and throw exception
		if (cust.isEmpty())  {
			ConnectionPool.getInstance().returnConnection(dbConnection);	
			throw new UserNotFoundException ("No customers were found.");
		}
		//Otherwise - return connection and return all the customers 
		else {
			ConnectionPool.getInstance().returnConnection(dbConnection);	
			return cust;
		}
	}


	/*
	 * 
	 * Get all customer's coupons
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CustomerDAO#getCoupons()
	 */
	@Override
	public Collection<Coupon> getCoupons() throws CouponNotFoundException, InterruptedException, SQLException {
		
		//Ask for connection from the connection pool
		Connection dbConnection = ConnectionPool.getInstance().getConnection();
				
		Collection<Coupon> coupons = this.loginCustomer.getCoupons();

		//If the customer has no coupons - return connection and throw exception
		if (coupons.isEmpty()) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new CouponNotFoundException ("Customer "+ this.loginCustomer.getCustomerName()
			+ " does not have any coupons.");
		}

		//Otherwise - return connection and return customer's coupons
		else {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			return coupons;
		}
	}


	/*
	 * 
	 * Login as customer
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.dao.CustomerDAO#login(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean login(String customerName, String password) 
			throws UserNotFoundException, WrongPasswordException, InterruptedException, SQLException {

		//Ask for connection from the connection pool
				Connection dbConnection = ConnectionPool.getInstance().getConnection();
				
		//If a customer with this name does not exist - return connection and throw exception
		if (!customerRepo.existsByCustomerName(customerName)) {
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new UserNotFoundException ("Login failed. Customer name " 
					+ customerName + " does not exist.");
		}

		//If the password does not fit the name	- return connection and throw exception
		else if (customerRepo.findByNameAndPwd(customerName, password)==null){	
			ConnectionPool.getInstance().returnConnection(dbConnection);
			throw new WrongPasswordException("Login failed. Wrong name + password: "
					+ customerName + ", " + password);
		}

		//Otherwise -  set the logged in customer, return connection and return true
		else {
			this.setLoginCustomer(customerRepo.findByNameAndPwd(customerName, password));
			ConnectionPool.getInstance().returnConnection(dbConnection);
			return true;
		}

	}



}
