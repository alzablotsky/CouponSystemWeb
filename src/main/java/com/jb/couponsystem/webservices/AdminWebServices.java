package com.jb.couponsystem.webservices;

import java.util.Collection;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jb.couponsystem.entities.Company;
import com.jb.couponsystem.entities.Coupon;
import com.jb.couponsystem.entities.Customer;
import com.jb.couponsystem.entry.CouponSystem;
import com.jb.couponsystem.enums.ClientType;
import com.jb.couponsystem.exceptions.CouponAlreadyExistsException;
import com.jb.couponsystem.exceptions.UserAlreadyExistsException;
import com.jb.couponsystem.facades.AdminFacade;
import com.jb.couponsystem.facades.CompanyFacade;

import ch.qos.logback.core.net.SyslogOutputStream;

/**
 * This class operates as a web service. It contains methods that allow
 * the administrator logged in the Coupon System
 * to obtain data from and to make changes in the database. 
 * Its methods create the instance of AdminFacade class for the logged in administrator 
 * and call it to execute its methods.
 *  
 * @author Alexander Zablotsky
 *
 */
@CrossOrigin("*")
@RestController
@RequestMapping("admin")
public class AdminWebServices {
	

	@Autowired
	ApplicationContext ctx;

	/**
	 * This method creates the instance of adminFacade class for the logged in administrator according to the received request.
	 * If the administrator did not pass login procedure it returns null.
	 * 
	 * @param request
	 * @return instance of adminFacade class for the logged in administrator
	 */ 
	
	private AdminFacade getFacade(HttpServletRequest request)
	{
				if (request.getSession().getAttribute("af") == null)
				{
					System.err.println("Facade was null"); 
					return null;
				}
				
				else {
				AdminFacade adminFacade = (AdminFacade)request.getSession().getAttribute("af");
				
				return adminFacade;
				}

		//"Fake login"
//		CouponSystem couponSystem = new CouponSystem(ctx);
//		AdminFacade result = (AdminFacade) couponSystem.login("admin", "1234", ClientType.ADMIN);
//		return result;	
	}
	
	

	/**
	 * This method executes the request of the logged in administrator to display all companies that exist in the database.
	 * It creates the instance of adminFacade class for the logged in administrator using getFacade method of the present class.
	 * Then it calls getAllCompanies method of the created facade.
	 * 
	 * @param request of the logged in administrator
	 * @return list of all companies
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcompany" ,
	method = RequestMethod.GET)
	public Collection<Company> doGetAllCompanies(HttpServletRequest request)
	{

		AdminFacade adminFacade = this.getFacade(request);
		return adminFacade.getAllCompanies();
	}

	
	
	/**
	 * This method executes the request of the logged in administrator to display company with certain ID from the database.
	 * The ID is received as a variable with the request and is mapped in method's URL.
	 * The method creates the instance of adminFacade class for the logged in administrator using getFacade method of the present class.
	 * Then it calls getCompany method of the created facade.
	 * 
	 * @param request of the logged in administrator
	 * @param id of the requested company 
	 * @return the requested company
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcompany/{id}", 
	method = RequestMethod.GET)
	public Company doGetCompany(HttpServletRequest request,
			@PathVariable("id") int id)
	{
		AdminFacade adminFacade = this.getFacade(request);

		return adminFacade.getCompany(id);
	
	}
	

	
	/**
	 * This method executes the request of the logged in administrator
	 * to create company object sent in the body of the request in the database.
	 * It creates the instance of adminFacade class for the logged in administrator
	 * using getFacade method of the present class.
	 * Then it calls createCompany method of the created facade.
	 * 
	 * @param request of the logged in administrator
	 * @param c company object sent in the body of the request
	 * @throws UserAlreadyExistsException thrown by the method of the facade
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcompany",
	method = RequestMethod.POST)
	public void doPostCompany(HttpServletRequest request,
			@RequestBody Company c) 
					throws UserAlreadyExistsException
	{
		AdminFacade adminFacade = this.getFacade(request);

		adminFacade.createCompany(c);	
	}
	
	
	/**
	 * This method executes the request of the logged in administrator to update a company with certain ID in the database.
	 * The ID is received as a variable with the request and is mapped in method's URL.
	 * It creates the instance of adminFacade class for the logged in administrator using getFacade method of the present class.
	 * Then it creates the company instance using the received ID.
	 * Then it sets the company PASSWORD and EMAIL attributes to be the same as of the company sent in the body of the request. 
	 * Finally it calls updateCompany method of the created facade which receives the created company instance as its signature.
	 * 
	 * @param request of the logged in administrator
	 * @param sent updated company sent in the body of the request
	 * @param id of the requested company
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcompany/{id}",
	method = RequestMethod.PUT)
	public void doPutCompany(HttpServletRequest request,
			@RequestBody Company sent, @PathVariable("id") int id)

	{
		AdminFacade adminFacade = this.getFacade(request);

		Company c = adminFacade.getCompany(id);
		
		c.setPassword(sent.getPassword());
		
		c.setEmail(sent.getEmail());

		adminFacade.updateCompany(c);	
	}
	

	

	
	/**
	 * This method executes the request of the logged in administrator to remove a company with certain ID from the database.
	 * The ID is received as a variable with the request and is mapped in method's URL. 
	 * The method creates instance of adminFacade class for the logged in administrator using getFacade method of the present class.
	 * Then it creates the company instance using the received ID.
	 * Finally it calls removeCompany method of the created facade which receives the created company instance as its signature.
	 * 
	 * @param request of the logged in administrator
	 * @param id of the requested company
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcompany/{id}",
	method = RequestMethod.DELETE)
	public void doDeleteCompany(HttpServletRequest request,
			@PathVariable("id") int id) 

	{
		AdminFacade adminFacade = this.getFacade(request);

		Company c = adminFacade.getCompany(id);

		adminFacade.removeCompany(c);		
	}

	
	
	/**
	 * This method executes the request of the logged in administrator to display all customers that exist in the database.
	 * It creates the instance of adminFacade class for the logged in administrator using getFacade method of the present class.
	 * Then it calls getAllCustomers method of the created facade.
	 * 
	 * @param request of the logged in administrator
	 * @return list of all customers
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcustomer" ,
	method = RequestMethod.GET)
	public Collection<Customer> doGetAllCustomers(HttpServletRequest request)
	{

		AdminFacade adminFacade = this.getFacade(request);
		return adminFacade.getAllCustomers();
	}

	
	
	/**
	 * This method executes the request of the logged in administrator to display customer with certain ID from the database.
	 * The ID is received as a variable with the request and is mapped in method's URL.
	 * The method creates the instance of adminFacade class for the logged in administrator using getFacade method of the present class.
	 * Then it calls getCustomer method of the created facade.
	 * 
	 * @param request of the logged in administrator
	 * @param id of the requested customer 
	 * @return the requested customer
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcustomer/{id}", 
	method = RequestMethod.GET)
	public Customer doGetCustomer(HttpServletRequest request,
			@PathVariable("id") int id)
	{
		AdminFacade adminFacade = this.getFacade(request);

		return adminFacade.getCustomer(id);
	}
	
	/**
	 * This method executes the request of the logged in administrator
	 * to create customer object sent in the body of the request in the database.
	 * It creates the instance of adminFacade class for the logged in administrator
	 * using getFacade method of the present class.
	 * Then it calls createCustomer method of the created facade.
	 * 
	 * @param request of the logged in administrator
	 * @param c customer object sent in the body of the request
	 * @throws UserAlreadyExistsException thrown by the method of the facade
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcustomer",
	method = RequestMethod.POST)
	public void doPostCustomer(HttpServletRequest request,
			@RequestBody Customer c) 
					throws UserAlreadyExistsException
	{
		AdminFacade adminFacade = this.getFacade(request);

		adminFacade.createCustomer(c);	
	}
	
	
	/**
	 * This method executes the request of the logged in administrator to update a customer with certain ID in the database.
	 * The ID is received as a variable with the request and is mapped in method's URL.
	 * It creates the instance of adminFacade class for the logged in administrator using getFacade method of the present class.
	 * Then it creates the customer instance using the received ID.
	 * Then it sets the customer PASSWORD attribute to be the same as of the customer sent in the body of the request. 
	 * Finally it calls updateCustomer method of the created facade which receives the created customer instance as its signature.
	 * 
	 * @param request of the logged in administrator
	 * @param sent updated customer sent in the body of the request
	 * @param id of the requested customer
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcustomer/{id}",
	method = RequestMethod.PUT)
	public void doPutCustomer(HttpServletRequest request,
			@RequestBody Customer sent, @PathVariable("id") int id)

	{
		AdminFacade adminFacade = this.getFacade(request);

		Customer c = adminFacade.getCustomer(id);
		
		c.setPassword(sent.getPassword());
		
		adminFacade.updateCustomer(c);	
	}
	
	
	
	/**
	 * This method executes the request of the logged in administrator to remove a customer with certain ID from the database.
	 * The ID is received as a variable with the request and is mapped in method's URL. 
	 * The method creates instance of adminFacade class for the logged in administrator using getFacade method of the present class.
	 * Then it creates the customer instance using the received ID.
	 * Finally it calls removeCustomer method of the created facade which receives the created customer instance as its signature.
	 * 
	 * @param request of the logged in administrator
	 * @param id of the requested customer
	 */
	@CrossOrigin(origins = "*")
	@RequestMapping( value = "/getcustomer/{id}",
	method = RequestMethod.DELETE)
	public void doDeleteCustomer(HttpServletRequest request,
			@PathVariable("id") int id) 

	{
		AdminFacade adminFacade = this.getFacade(request);

		Customer c = adminFacade.getCustomer(id);

		adminFacade.removeCustomer(c);		
	}
	

}
