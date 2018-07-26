package com.jb.couponsystem.facades;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.jb.couponsystem.dbdao.CompanyDBDAO;
import com.jb.couponsystem.dbdao.CouponDBDAO;
import com.jb.couponsystem.dbdao.CustomerDBDAO;
import com.jb.couponsystem.entities.Company;
import com.jb.couponsystem.entities.Coupon;
import com.jb.couponsystem.enums.CouponType;
import com.jb.couponsystem.exceptions.CouponAlreadyExistsException;
import com.jb.couponsystem.exceptions.CouponNotFoundException;
import com.jb.couponsystem.exceptions.IllegalUpdateException;
import com.jb.couponsystem.exceptions.UserAlreadyExistsException;
import com.jb.couponsystem.exceptions.UserNotFoundException;
import com.jb.couponsystem.exceptions.WrongPasswordException;


/**
 * This class contains methods that allow the company which is the client of the Coupon System
 * to log in, to obtain data from and to make changes in the database
 * by calling instances of DBDAO classes.
 *  
 * @author Alexander Zablotsky
 *
 */

@Component
public class CompanyFacade implements CouponClientFacade {

	//Attributes
	private ApplicationContext ctx;

	private CompanyDBDAO companyDBDAO;

	private CouponDBDAO couponDBDAO;

	private Company loginCompany;

	/**
	 * CTOR
	 * 
	 * @param ctx
	 */
	public CompanyFacade(ApplicationContext ctx) {
		this.ctx = ctx;
		this.companyDBDAO = ctx.getBean(CompanyDBDAO.class);
		this.couponDBDAO = ctx.getBean(CouponDBDAO.class);
	}

	//Getters and setters

	/**
	 * @return the companyDBDAO
	 */
	public CompanyDBDAO getCompanyDBDAO() {
		return companyDBDAO;
	}


	/**
	 * @param companyDBDAO the companyDBDAO to set
	 */
	public void setCompanyDBDAO(CompanyDBDAO companyDBDAO) {
		this.companyDBDAO = companyDBDAO;
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
	 * @return the loginCompany
	 */
	public Company getLoginCompany() {
		return loginCompany;
	}

	/**
	 * @param loginCompany the loginCompany to set
	 */
	public void setLoginCompany(Company loginCompany) {
		this.loginCompany = loginCompany;
	}
	
	/**
	 * This method returns the name of the logged in company.
	 * 
	 * @return name of the logged in company
	 */
	public String getLoginCompanyName ()
	{
		return this.loginCompany.getCompanyName();
	}
	
	//Methods
	/*
	 * Login as company
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.facades.CouponClientFacade#login(java.lang.String, java.lang.String)
	 */
	@Override
	public CouponClientFacade login(String companyName, String password) 
			throws WrongPasswordException, UserNotFoundException, InterruptedException, SQLException {

		// check if company DBDAO can login...
		if(companyDBDAO.login(companyName, password)){

			//Set company in this class	
			this.loginCompany = companyDBDAO.getLoginCompany();

			//Set company in coupon DBDAO
			couponDBDAO.setLoginCompany(this.loginCompany);

			return this;
		}

		else return null;
	}


	/**
	 * This method creates coupon object send as a parameter from the outside in the database.
	 * The coupon is created by the company which is logged in the coupon system.
	 * It calls the instance of couponDBDAO class to create coupon, while catching the exceptions
	 * existing in its method. CouponAlreadyExistsException is re-thrown.
	 * 
	 * @param c coupon object
	 * @throws CouponAlreadyExistsException if the coupon already exists in the database
	 */
	public void createCoupon(Coupon c) throws CouponAlreadyExistsException {
		try {
			// define the coupon's company and call coupon DBDAO to create the coupon
			c.setCompany(this.loginCompany);
			couponDBDAO.createCoupon(c);
			System.out.println("Coupon "+ c.getTitle() +
					" was successfully created by " + this.loginCompany.getCompanyName());
		} 
		catch (CouponAlreadyExistsException e) {
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
	 * This method removes coupon object of the logged in company send as a parameter from the outside from the database.
	 * It calls the instance of couponDBDAO class to remove coupon, while catching the exceptions
	 * existing in its method. CouponNotFoundException is re-thrown.
	 * 
	 * @param c coupon object
	 */
	public void removeCoupon(Coupon c) 	{
		// call coupon DBDAO to remove coupon
		try {
			couponDBDAO.removeCoupon(c);
			System.out.println("Coupon "+ c.getTitle() +" was successfully removed.");

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

	}

	/**
	 * This method updates the coupon object of the logged in company in the database,
	 * replacing it with a coupon object send as a parameter from the outside.
	 * It calls the instance of couponDBDAO class to update the coupon, while catching the exceptions
	 * existing in its method. CouponNotFoundException and IllegalUpdateException are re-thrown.
	 * 
	 * @param c company object
	 */
	public void updateCoupon(Coupon c) 	{

		try {
			couponDBDAO.updateCoupon(c);
			System.out.println("Coupon "+ c.getTitle() +" was successfully updated.");
		} 
		catch (CouponNotFoundException e) {
			System.err.println( e.getMessage());
			throw e;
		} 
		catch (IllegalUpdateException e) {
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
	 * This method displays the coupon object of the logged in company from the database,
	 * if the coupon ID is the same as the ID send as a parameter from the outside.
	 * It calls the instance of couponDBDAO class to get the coupon, while catching the exceptions
	 * existing in its method. CouponNotFoundException is re-thrown.
	 * 
	 * @param id coupon id
	 * @return coupon object
	 */
	public Coupon getCoupon(long id) 	{
		// call coupon DBDAO to get coupon
		try {
			return couponDBDAO.getCompanyCoupon(id);

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
	 * This method displays all coupon objects of the logged in company existing in the database,
	 * It calls the instance of couponDBDAO class to get all coupons, while catching the exceptions
	 * existing in its method. CouponNotFoundException is re-thrown.
	 * 
	 * @return collection of coupon objects
	 */
	public Collection <Coupon> getAllCoupons() {
		// call coupon DBDAO to get all coupons
		try {
			return  couponDBDAO.getAllCompanyCoupons();
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
	 * This method displays all coupon objects of the logged in company existing in the database,
	 * if the coupons are of the same type as the type send as a parameter from the outside.
	 * It calls the instance of couponDBDAO class to get all company's coupons by type, while catching the exceptions
	 * existing in its method. CouponNotFoundException is re-thrown.
	 * 
	 * @param type coupon type
	 * @return collection of coupon objects
	 */
	public Collection <Coupon> getCouponsByType(CouponType type) {
		// call coupon DBDAO to get coupons by type
		try {
			return  couponDBDAO.getCompanyCouponsByType(type);
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
	 * This method displays all coupon objects of the logged in company existing in the database,
	 * if the price of the coupons is lower than the price send as a parameter from the outside.
	 * It calls the instance of couponDBDAO class to get all company's coupons by type, while catching the exceptions
	 * existing in its method. CouponNotFoundException is re-thrown.
	 * 
	 * @param price coupon price
	 * @return collection of coupon objects
	 */
	public Collection <Coupon> getCouponsByPrice(double price) {
		// call coupon DBDAO to get coupons under this price
		try {
			return  couponDBDAO.getCompanyCouponsByPrice(price);
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
	 * This method displays all coupon objects of the logged in company existing in the database,
	 * if the end date of the coupons is before the end date send as a parameter from the outside.
	 * It calls the instance of couponDBDAO class to get all company's coupons by type, while catching the exceptions
	 * existing in its method. CouponNotFoundException is re-thrown.
	 * 
	 * @param endDate coupon end date
	 * @return collection of coupon objects
	 */
	public Collection <Coupon> getCouponsByEndDate(Date endDate) {
		// call coupon DBDAO to get coupons before this end date
		try {
			return  couponDBDAO.getCompanyCouponsByEndDate(endDate);
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









