package com.jb.couponsystem.facades;


import java.sql.SQLException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.jb.couponsystem.*;
import com.jb.couponsystem.dbdao.CompanyDBDAO;
import com.jb.couponsystem.dbdao.CouponDBDAO;
import com.jb.couponsystem.dbdao.CustomerDBDAO;
import com.jb.couponsystem.entities.Company;
import com.jb.couponsystem.entities.Coupon;
import com.jb.couponsystem.entities.Customer;
import com.jb.couponsystem.enums.CouponType;
import com.jb.couponsystem.exceptions.CouponAlreadyExistsException;
import com.jb.couponsystem.exceptions.CouponAlreadyPurchasedException;
import com.jb.couponsystem.exceptions.CouponExpiredException;
import com.jb.couponsystem.exceptions.CouponNotFoundException;
import com.jb.couponsystem.exceptions.CouponOutOfStockException;
import com.jb.couponsystem.exceptions.IllegalUpdateException;
import com.jb.couponsystem.exceptions.UserNotFoundException;
import com.jb.couponsystem.exceptions.WrongPasswordException;

/**
 * This class contains methods that allow the customer who is the client of the Coupon System
 * to log in, to obtain data from and to make changes in the database
 * by calling instances of DBDAO classes.
 *  
 * @author Alexander Zablotsky
 *
 */
@Component
public class CustomerFacade implements CouponClientFacade {

	//Attributes
	private ApplicationContext ctx;

	private CustomerDBDAO customerDBDAO;

	private CouponDBDAO couponDBDAO;

	private Customer loginCustomer;

	/**
	 * CTOR
	 * 
	 * @param ctx
	 */
	public CustomerFacade(ApplicationContext ctx) {
		this.ctx = ctx;
		this.customerDBDAO = ctx.getBean(CustomerDBDAO.class);
		this.couponDBDAO = ctx.getBean(CouponDBDAO.class);
	}

	//Getters and setters 
	/**
	 * @return the customerDBDAO
	 */
	public CustomerDBDAO getCustomerDBDAO() {
		return customerDBDAO;
	}

	/**
	 * @param customerDBDAO the customerDBDAO to set
	 */
	public void setCustomerDBDAO(CustomerDBDAO customerDBDAO) {
		this.customerDBDAO = customerDBDAO;
	}

	/**
	 * @return the couponDBDAO
	 */
	public CouponDBDAO getCouponDBDAO() {
		return couponDBDAO;
	}

	/**
	 * @param couponDBDAO the couponDBDAO to set
	 */
	public void setCouponDBDAO(CouponDBDAO couponDBDAO) {
		this.couponDBDAO = couponDBDAO;
	}


	/**
	 * @return the loginCustomer
	 */
	public Customer getLoginCustomer() {
		return loginCustomer;
	}

	/**
	 * @param loginCustomer the loginCustomer to set
	 */
	public void setLoginCustomer(Customer loginCustomer) {
		this.loginCustomer = loginCustomer;
	}

	
	/**
	 * This method returns the name of the logged in customer.
	 * 
	 * @return name of the logged in company
	 */
	public String getLoginCustomerName ()
	{
		return this.loginCustomer.getCustomerName();
	}
	

	//Methods
	/*
	 * 
	 * Login as customer
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.facades.CouponClientFacade#login(java.lang.String, java.lang.String)
	 */
	@Override
	public CouponClientFacade login(String name, String password) 
			throws WrongPasswordException, UserNotFoundException, InterruptedException, SQLException {

		//If customer DBDAO returns true - set the customer and return this class
		if (customerDBDAO.login(name, password)) {

			//Set login customer here and in coupon DBDAO
			setLoginCustomer(customerDBDAO.getLoginCustomer());
			couponDBDAO.setLoginCustomer(this.loginCustomer);

			return this;
		}

		//Otherwise - return null
		else return null;
	}
	
	

	/**
	 * This method displays the coupon object from the database if the coupon ID 
	 * is the same as the ID send as a parameter from the outside.
	 * It calls the instance of couponDBDAO class to get the coupon, while catching the exceptions
	 * existing in its method. CouponNotFoundException is re-thrown.
	 * 
	 * @param id coupon id
	 * @return coupon object
	 */
	public Coupon getCoupon(long id) 	{
		// call coupon DBDAO to get coupon
		try {
			return couponDBDAO.getCoupon(id);

		} 
		catch (CouponNotFoundException e) {
			System.err.println( e.getMessage());
			throw e;
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method displays all coupon objects existing in the database,
	 * It calls the instance of couponDBDAO class to get all coupons, while catching the exceptions
	 * existing in its method. CouponNotFoundException is re-thrown.
	 * 
	 * @return collection of coupon objects
	 */
	public Collection <Coupon> getAllCoupons() {
		// call coupon DBDAO to get all coupons
		try {
			return  couponDBDAO.getAllCoupons();
		} 
		catch (CouponNotFoundException e) {
			System.err.println( e.getMessage());
			throw e;
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method allows the logged in customer to purchase coupon object send as a parameter from the outside.
	 * The coupon is added to the logged in customer's coupons in the database.
	 * It calls the instance of couponDBDAO class to execute the purchase of the coupon, while catching the exceptions
	 * existing in its method. CouponNotFoundException, CouponAlreadyPurchasedException,
	 * CouponOutOfStockException and CouponExpiredException are re-thrown.
	 * 
	 * @param c coupon object
	 */
	public void purchaseCoupon (Coupon c) {

		//Call coupon DBDAO  to purchase coupon
		try {
			couponDBDAO.purchaseCoupon(c);

			System.out.println("Coupon "+c.getTitle() +" was successfully purchased by customer " + loginCustomer.getCustomerName());

		} 
		catch (CouponNotFoundException e) {
			System.err.println( e.getMessage());
			throw e;
		} 
		catch (CouponAlreadyPurchasedException e) {
			System.err.println( e.getMessage());
			throw e;
		} 
		catch (CouponOutOfStockException e) {
			System.err.println( e.getMessage());
			throw e;
		} 
		catch (CouponExpiredException e) {
			System.err.println( e.getMessage());
			throw e;
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method displays all coupon objects purchased by the logged in customer existing in the database,
	 * It calls the instance of couponDBDAO class to get all customer's coupons, while catching the exceptions
	 * existing in its method. CouponNotFoundException is re-thrown.
	 * 
	 * @return collection of coupon objects
	 */
	public Collection<Coupon> getAllPurchasedCoupons() {
		//Call customer DBDAO to get all purchased coupons
		try {
			//return customerDBDAO.getCoupons();
			return couponDBDAO.getAllPurchasedCoupons();
			} 
		catch (CouponNotFoundException e) {
			System.err.println( e.getMessage());
			throw e;
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method displays all coupon objects of the logged in customer existing in the database, 
	 * the type of which is the same as the type sent as a parameter from the outside.
	 * It calls the instance of customerDBDAO class to get all customer's coupons by type, while catching the exceptions
	 * existing in its method. CouponNotFoundException is re-thrown.
	 * 
	 * @param type coupon type
	 * @return collection of coupon objects
	 */
	public Collection<Coupon> getAllPurchasedCouponsByType(CouponType type) {
		//Call coupon DBDAO to get all purchased coupons by type	
		try {
			return couponDBDAO.getAllPurchasedCouponsByType(type);
		} 
		catch (CouponNotFoundException e) {
			System.err.println( e.getMessage());
			throw e;
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method displays all coupon objects of the logged in customer existing in the database, the price of which is 
	 * lower than the price sent as a parameter from the outside.
	 * It calls the instance of customerDBDAO class to get all customer's coupons by price, while catching the exceptions
	 * existing in its method. CouponNotFoundException is re-thrown.
	 * 
	 * @param price coupon price
	 * @return collection of coupon objects
	 */
	public Collection<Coupon> getAllPurchasedCouponsByPrice(double price) {
		//Call coupon DBDAO to get all purchased coupons by price	
		try {
			return couponDBDAO.getAllPurchasedCouponsByPrice(price);
		} 
		catch (CouponNotFoundException e) {
			System.err.println( e.getMessage());
			throw e;
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method displays all coupon objects that were not purchased by the logged in customer existing in the database,
	 * It calls the instance of couponDBDAO class to get all customer's coupons, while catching the exceptions
	 * existing in its method. CouponNotFoundException is re-thrown.
	 * 
	 * @return collection of coupon objects
	 */
	public Collection<Coupon> getAllNonPurchasedCoupons() {
		//Call customer DBDAO to get all non-purchased coupons
		try {
			return couponDBDAO.getAllNonPurchasedCoupons();
			} 
		
		catch (CouponNotFoundException e) {
			System.err.println( e.getMessage());
			throw e;
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
