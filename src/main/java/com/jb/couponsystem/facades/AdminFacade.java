package com.jb.couponsystem.facades;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
import com.jb.couponsystem.exceptions.CouponNotFoundException;
import com.jb.couponsystem.exceptions.IllegalUpdateException;
import com.jb.couponsystem.exceptions.UserAlreadyExistsException;
import com.jb.couponsystem.exceptions.UserNotFoundException;
import com.jb.couponsystem.exceptions.WrongPasswordException;


/**
 * This class contains methods that allow the administrator of the Coupon System
 * to log in, to obtain data from and to make changes in the database
 * by calling instances of DBDAO classes.
 *  
 * @author Alexander Zablotsky
 *
 */
@Component
public class AdminFacade implements CouponClientFacade {

	//Attributes

	private ApplicationContext ctx;

	private CompanyDBDAO companyDBDAO;

	private CustomerDBDAO customerDBDAO;
	
	private CouponDBDAO couponDBDAO;

	/**
	 * CTOR
	 * 
	 * @param ctx
	 */
	public AdminFacade(ApplicationContext ctx) {
		super();
		this.ctx = ctx;
		this.companyDBDAO =  ctx.getBean(CompanyDBDAO.class);
		this.customerDBDAO = ctx.getBean(CustomerDBDAO.class);
		this.couponDBDAO = ctx.getBean(CouponDBDAO.class);
	}

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


	//Methods

	/*
	 *
	 * Login as admin
	 * 
	 * (non-Javadoc)
	 * @see com.jb.couponsystem.facades.CouponClientFacade#login(java.lang.String, java.lang.String)
	 */
	@Override
	public CouponClientFacade login(String name, String password) throws WrongPasswordException {

		if (!name.toLowerCase().equals("admin")) {
			throw new UserNotFoundException ("Login failed. User " 
					+ name + " not found.");
			
		}

		else if (!password.equals("1234")) {
			throw new WrongPasswordException("Login failed. Wrong password: "
					+ password);
		}
		else return this;
	}


	/**
	 * This method creates company object send as a parameter from the outside in the database.
	 * It calls the instance of companyDBDAO class to create company, while catching the exceptions
	 * existing in its method. UserAlreadyExistsException is re-thrown.
	 * 
	 * @param c company object
	 */

	public void createCompany(Company c) {

		// call company DBDAO to create company
		try {
			companyDBDAO.createCompany(c);
			System.out.println("Company "+ c.getCompanyName() +" was successfully created.");

		} 
		catch (UserAlreadyExistsException e) {
			System.err.println( e.getMessage());
			throw e;
		} 
		catch (InterruptedException e) {
			System.err.println( e.getMessage());
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}

	}
	

	/**
	 * This method removes company object send as a parameter from the outside from the database.
	 * First it calls the instance of couponDBDAO class to remove all company's coupons,
	 * then it calls the instance of companyDBDAO class to remove the company itself, while catching the exceptions
	 * existing in these methods. UserNotFoundException is re-thrown.
	 * 
	 * @param c company object
	 */
	public void removeCompany(Company c) {
        //first call coupon DBDAO to remove all comany's coupons if they exist.
		//then call company DBDAO to remove company...
		
		try {
		    couponDBDAO.removeCompanyCoupons(c.getId());
			companyDBDAO.removeCompany(c);
			System.out.println("Company "+ c.getCompanyName() +" was successfully removed.");
		} 

		catch (UserNotFoundException e) {
			System.err.println( e.getMessage());
			throw e;
		} 
		catch (InterruptedException e) {
			System.err.println( e.getMessage());
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		

	}			


	/**
	 * This method updates the company in the database replacing it with a company object send as a parameter from the outside.
	 * It calls the instance of companyDBDAO class to update the company, while catching the exceptions
	 * existing in its method. UserNotFoundException and IllegalUpdateException are re-thrown.
	 * 
	 * @param c company object
	 */
	public void updateCompany(Company c) {

		// call company DBDAO to update company...
		try {
			companyDBDAO.updateCompany(c);
			System.out.println("Company "+ c.getCompanyName() +" was successfully updated.\n"+
					"Company details: "+ c);

		} catch (UserNotFoundException e) {
			System.err.println( e.getMessage());
			throw e;
		}
		catch (IllegalUpdateException e) {
			System.err.println( e.getMessage());
			throw e;
		}
		catch (InterruptedException e) {
			System.err.println( e.getMessage());
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

	}



	/**
	 * This method displays the company object from the database if the company ID is the same
	 * as the ID send as a parameter from the outside.
	 * It calls the instance of companyDBDAO class to get the company, while catching the exceptions
	 * existing in its method. UserNotFoundException is re-thrown.
	 * 
	 * @param id company id
	 * @return company object
	 */
	public Company getCompany(long id) {

		// call company DBDAO to get company
		try {
			return companyDBDAO.getCompany(id);

		} catch (UserNotFoundException e) {
			System.err.println( e.getMessage());
			throw e;
		}
		catch (InterruptedException e) {
			System.err.println( e.getMessage());
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}



	/**
	 * This method displays all company objects from the database.
	 * It calls the instance of companyDBDAO class to get all the companies, while catching the exceptions
	 * existing in its method. UserNotFoundException is re-thrown.
	 * 
	 * @return collection of company objects
	 */
	public Collection <Company> getAllCompanies() {
		// call company DBDAO to get all companies.
		try {
			return companyDBDAO.getAllCompanies();

		} 
		catch (UserNotFoundException e) {
			System.err.println( e.getMessage());
			throw e;
		}
		catch (InterruptedException e) {
			System.err.println( e.getMessage());
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * This method creates customer object send as a parameter from the outside in the database.
	 * It calls the instance of customerDBDAO class to create customer, while catching the exceptions
	 * existing in its method. UserAlreadyExistsException is re-thrown.
	 * 
	 * @param c company object
	 */
	public void createCustomer(Customer c){
		// call customer DBDAO to create customer
		try {
			customerDBDAO.createCustomer(c);
			System.out.println("Customer "+ c.getCustomerName() +" was successfully created.");

		} 
		catch (UserAlreadyExistsException e) {
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
	 * This method removes customer object send as a parameter from the outside from the database.
	 * It calls the instance of customerDBDAO class to remove company, while catching the exceptions
	 * existing in its method. UserNotFoundException is re-thrown.
	 * 
	 * @param c customer object
	 */
	public void removeCustomer(Customer c){
		// call customer DBDAO to remove customer	
		try {
			//c.getCoupons()
			customerDBDAO.removeCustomer(c);
			System.out.println("Customer "+ c.getCustomerName() +" was successfully removed.");

		} 
		catch (UserNotFoundException e) {
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
	 * This method updates the customer in the database replacing it with a customer object send as a parameter from the outside.
	 * It calls the instance of customerDBDAO class to update the customer, while catching the exceptions
	 * existing in its method. UserNotFoundException and IllegalUpdateException are re-thrown.
	 * 
	 * @param c customer object
	 */
	public void updateCustomer(Customer c){
		// call customer DBDAO to update customer
		try {
			customerDBDAO.updateCustomer(c);
			System.out.println("Customer "+ c.getCustomerName() +" was successfully updated.\n"
					+ "Customer details: "+ c);
		} 
		catch (UserNotFoundException e) {
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
	 * This method displays the customer object from the database which has the ID send as a parameter from the outside.
	 * It calls the instance of customerDBDAO class to get the customer, while catching the exceptions
	 * existing in its method. UserNotFoundException is re-thrown.
	 * 
	 * @param id customer id
	 * @return customer object
	 */
	public Customer getCustomer(long id) {

		// call customer DBDAO to get a customer...
		try {
			return customerDBDAO.getCustomer(id);
		} 
		catch (UserNotFoundException e) {
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
	 * This method displays all customer objects from the database.
	 * It calls the instance of customerDBDAO class to get all the customers, while catching the exceptions
	 * existing in its method. UserNotFoundException is re-thrown.
	 * 
	 * @return collection of customer objects
	 */
	public Collection <Customer> getAllCustomers() {

		// call customer DBDAO to get all customers...
		try {
			return customerDBDAO.getAllCustomers();

		} 
		catch (UserNotFoundException e) {
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
